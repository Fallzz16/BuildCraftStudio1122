package dev.buildcraftstudio.server;

import dev.buildcraftstudio.core.BuildPalette;
import dev.buildcraftstudio.net.PlacePacket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/** Singleplayer v0.1 placement: main-thread, creative-only, bounded and fail-closed. */
public final class PlacementManager {
    public static final PlacementManager INSTANCE = new PlacementManager();
    public static final int MAX_PLACEMENT = 128;
    private static final int BATCH_SIZE = 16;
    private final Map<UUID, Job> jobs = new HashMap<>();
    private PlacementManager() { }

    public void request(EntityPlayerMP player, PlacePacket msg) {
        if (player == null || !player.getServer().isSinglePlayer() || !player.capabilities.isCreativeMode || msg.dimension != player.dimension) {
            reply(player,"§cBuildCraft M1: finalização disponível apenas no Criativo."); return;
        }
        if (jobs.containsKey(player.getUniqueID())) { reply(player,"§eJá existe uma aplicação em andamento."); return; }
        if (msg.entries.isEmpty() || msg.entries.size() > MAX_PLACEMENT) {
            reply(player,"§cLimite inicial: 128 blocos por finalização."); return;
        }
        WorldServer world = player.getServerWorld();
        Set<BlockPos> unique = new HashSet<>();
        List<Change> changes = new ArrayList<>();
        for (PlacePacket.Entry e : msg.entries) {
            BlockPos pos = new BlockPos(e.x,e.y,e.z);
            if (!unique.add(pos) || e.y < 1 || e.y >= world.getActualHeight() || !world.isBlockLoaded(pos)) {
                reply(player,"§cPosição repetida, fora do mundo ou chunk indisponível."); return;
            }
            double dist = player.getDistanceSq(e.x+0.5,e.y+0.5,e.z+0.5);
            if (dist > 64.0 * 64.0 || !world.isBlockModifiable(player,pos)) {
                reply(player,"§cÁrea fora do alcance ou sem permissão."); return;
            }
            Block block = Block.getBlockById(e.blockId);
            if (block == null || !BuildPalette.isSafe(block)) {
                reply(player,"§cBloco não liberado para finalização na M1."); return;
            }
            IBlockState state;
            try { state = block.getStateFromMeta(e.meta); }
            catch (RuntimeException ex) { reply(player,"§cEstado de bloco inválido."); return; }
            if (state.getBlock() != block || e.meta < 0 || e.meta > 15) {
                reply(player,"§cMetadados inválidos."); return;
            }
            if (player.getEntityBoundingBox().intersects(new AxisAlignedBB(pos))) {
                reply(player,"§cA construção atingiria o jogador. Mova o personagem e tente novamente."); return;
            }
            IBlockState previous = world.getBlockState(pos);
            if (world.getTileEntity(pos) != null || previous.getBlock() == Blocks.BEDROCK) {
                reply(player,"§cExiste um bloco protegido ou com inventário no destino."); return;
            }
            changes.add(new Change(pos,previous,state));
        }
        jobs.put(player.getUniqueID(),new Job(world,player,changes));
        reply(player,"§aBuildCraft: plano validado. Aplicação iniciada (16 blocos/tick).");
    }

    @SubscribeEvent public void onServerTick(TickEvent.ServerTickEvent e) {
        if (e.phase != TickEvent.Phase.END || jobs.isEmpty()) return;
        for (UUID id : new ArrayList<>(jobs.keySet())) {
            Job job = jobs.get(id);
            if (job == null) continue;
            try {
                if (!job.tick()) jobs.remove(id);
            } catch (RuntimeException ex) {
                jobs.remove(id);
                try { job.rollback(); } catch(RuntimeException ignored) { /* best effort only */ }
                reply(job.player,"§cBuildCraft: falha inesperada na aplicação. Confira o terreno e o log.");
                java.util.logging.Logger.getLogger("BuildCraftStudio").log(java.util.logging.Level.SEVERE,"Creative placement failed",ex);
            }
        }
    }

    private static void reply(EntityPlayerMP player,String message) {
        if (player != null) player.sendMessage(new TextComponentString(message));
    }

    private static final class Change {
        final BlockPos pos;
        final IBlockState before, after;
        Change(BlockPos pos,IBlockState before,IBlockState after) {
            this.pos=pos; this.before=before; this.after=after;
        }
    }
    private static final class Job {
        final WorldServer world;
        final EntityPlayerMP player;
        final List<Change> changes;
        int progress;
        Job(WorldServer world, EntityPlayerMP player, List<Change> changes) {
            this.world=world; this.player=player; this.changes=changes;
        }
        boolean tick() {
            if (player.isDead || player.world != world || world.getPlayerEntityByUUID(player.getUniqueID()) != player || !player.capabilities.isCreativeMode) {
                rollback(); reply(player,"§cAplicação cancelada; tentativa de restauração realizada."); return false;
            }
            for (int n=0;n<BATCH_SIZE && progress<changes.size();n++) {
                Change c=changes.get(progress);
                if (!world.isBlockLoaded(c.pos) || !world.getBlockState(c.pos).equals(c.before)
                        || !world.isBlockModifiable(player,c.pos) || world.getTileEntity(c.pos)!=null) {
                    rollback(); reply(player,"§cO mundo mudou durante a aplicação. Restauração tentada."); return false;
                }
                if (!world.setBlockState(c.pos,c.after,3) || !world.getBlockState(c.pos).equals(c.after)) {
                    rollback(); reply(player,"§cFalha ao aplicar bloco. Restauração tentada."); return false;
                }
                progress++;
            }
            if (progress == changes.size()) {
                reply(player,"§aBuildCraft: "+progress+" blocos aplicados. (M1: sem undo de mundo)");
                return false;
            }
            return true;
        }
        /** Best-effort only; no durable journal in M1. Do not claim ACID rollback. */
        void rollback() {
            for (int i=progress-1;i>=0;i--) {
                Change c=changes.get(i);
                if (world.isBlockLoaded(c.pos) && world.getBlockState(c.pos).equals(c.after)) {
                    world.setBlockState(c.pos,c.before,3);
                }
            }
        }
    }
}
