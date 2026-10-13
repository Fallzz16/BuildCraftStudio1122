package dev.buildcraftstudio.client;

import dev.buildcraftstudio.core.BuildDocument;
import dev.buildcraftstudio.core.BuildPalette;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

/** Project data only; does not store unsafe TileEntity NBT or modify the world. */
public final class StudioStorage {
    private StudioStorage() { }
    private static volatile String failure="Nenhuma falha registrada";
    public static String lastError() {return failure;}
    private static boolean fail(String stage,File location,Exception exception) {
        failure=stage+": "+exception.getClass().getSimpleName() +
                (exception.getMessage()==null?"": " ("+exception.getMessage()+")");
        org.apache.logging.log4j.LogManager.getLogger("BuildCraftStudio").error(
                "Erro ao salvar BuildCraft Studio em {}",location.getAbsolutePath(),exception);
        return false;
    }
    private static File file(Minecraft mc) {
        String world = mc.getIntegratedServer() == null ? "not_singleplayer" : mc.getIntegratedServer().getFolderName();
        world = world.replaceAll("[^a-zA-Z0-9_.-]", "_");
        return new File(new File(net.minecraftforge.fml.common.Loader.instance().getConfigDir().getParentFile(),"buildcraftstudio/projects"), world + "_dim" + mc.player.dimension + ".dat");
    }
    public static BuildDocument load(Minecraft mc, BlockPos fallback) {
        File main = file(mc);
        NBTTagCompound tag = read(main);
        File backup = new File(main.getPath()+".bak");
        if (tag == null) tag = read(backup);
        if (tag == null) return (main.exists() || backup.exists()) ? null : new BuildDocument(fallback);
        if (tag.getInteger("schema") != 1) return null;
        BlockPos origin = new BlockPos(tag.getInteger("ox"),tag.getInteger("oy"),tag.getInteger("oz"));
        BuildDocument project = new BuildDocument(origin);
        NBTTagList entries = tag.getTagList("blocks",10);
        if (entries.tagCount()>BuildDocument.MAX_PREVIEW_BLOCKS) return null;
        for (int i=0;i<entries.tagCount();i++) {
            NBTTagCompound b = entries.getCompoundTagAt(i);
            try {
                Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(b.getString("id")));
                if (block == null || !BuildPalette.isSafe(block)) return null;
                IBlockState state = block.getStateFromMeta(b.getInteger("meta"));
                BlockPos local = new BlockPos(b.getInteger("x"),b.getInteger("y"),b.getInteger("z"));
                if (b.getInteger("meta")<0 || b.getInteger("meta")>15 || !BuildDocument.validLocal(local) || project.blocks().containsKey(local)) return null;
                project.loadBlock(local,state);
            } catch (RuntimeException ex) { return null; }
        }
        project.saved();
        return project;
    }
    private static NBTTagCompound read(File file) {
        if (!file.isFile() || file.length() > 4_000_000L) return null;
        try (FileInputStream in = new FileInputStream(file)) { return CompressedStreamTools.readCompressed(in); }
        catch (Exception ex) { return null; }
    }
    public static boolean save(Minecraft mc, BuildDocument doc) {
        if (mc.getIntegratedServer() == null) return false;
        File main = file(mc), tmp = new File(main.getPath()+".tmp"), backup=new File(main.getPath()+".bak");
        if (!main.getParentFile().isDirectory() && !main.getParentFile().mkdirs())
            return fail("Criar pasta",main,new IOException("Sem permissão em "+main.getParentFile()));
        NBTTagCompound data = new NBTTagCompound();
        data.setInteger("schema",1);
        data.setInteger("ox",doc.origin().getX());
        data.setInteger("oy",doc.origin().getY());
        data.setInteger("oz",doc.origin().getZ());
        NBTTagList list=new NBTTagList();
        for (java.util.Map.Entry<BlockPos,IBlockState> e:doc.blocks().entrySet()) {
            NBTTagCompound part=new NBTTagCompound();
            Block block=e.getValue().getBlock();
            part.setString("id",block.getRegistryName().toString());
            part.setInteger("meta",block.getMetaFromState(e.getValue()));
            part.setInteger("x",e.getKey().getX());
            part.setInteger("y",e.getKey().getY());
            part.setInteger("z",e.getKey().getZ());
            list.appendTag(part);
        }
        data.setTag("blocks",list);
        try {
            try(FileOutputStream out = new FileOutputStream(tmp)) {
                CompressedStreamTools.writeCompressed(data,out);
                out.getFD().sync();
            }
            if(main.exists() && read(main)!=null) Files.copy(main.toPath(),backup.toPath(),StandardCopyOption.REPLACE_EXISTING);
            try { Files.move(tmp.toPath(),main.toPath(),StandardCopyOption.ATOMIC_MOVE,StandardCopyOption.REPLACE_EXISTING); }
            catch(AtomicMoveNotSupportedException ex) { Files.move(tmp.toPath(),main.toPath(),StandardCopyOption.REPLACE_EXISTING); }
            doc.saved();
            failure="Nenhuma falha registrada";
            return true;
        } catch(Exception ex) { return fail("Escrever arquivo",main,ex); }
    }
}
