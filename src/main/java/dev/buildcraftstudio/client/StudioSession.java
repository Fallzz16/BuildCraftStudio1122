package dev.buildcraftstudio.client;

import dev.buildcraftstudio.core.BuildDocument;
import dev.buildcraftstudio.core.EditorMath;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

/** Manages render-view restoration and singleplayer project life cycle. */
public final class StudioSession {
    private static StudioSession active;
    public final BuildDocument document;
    public final StudioCameraEntity camera;
    public final Entity oldCamera;
    private final int oldThirdPerson;
    private final int startDimension;
    private float health;
    private int autosaveTicks;
    public double yaw=45, pitch=35, distance=10;
    public Vec3d focus;
    public int planeOffset=0;
    public int slot=0;
    public boolean showGrid=true;
    public boolean inventoryOpen=false;
    public String notice="Clique para colocar • Direito: orbitar • Meio: deslocar";

    private StudioSession(Minecraft mc, BuildDocument document) {
        this.document = document;
        this.oldCamera = mc.getRenderViewEntity();
        this.oldThirdPerson = mc.gameSettings.thirdPersonView;
        this.startDimension=mc.player.dimension;
        this.health=mc.player.getHealth();
        this.camera = new StudioCameraEntity(mc.world);
        BlockPos o=document.origin();
        this.focus=new Vec3d(o.getX()+0.5,o.getY()+0.5,o.getZ()+0.5);
        mc.gameSettings.thirdPersonView=0;
        while (!updateCamera(mc) && distance > 3.6) distance -= 0.5;
        if (!updateCamera(mc)) {
            mc.gameSettings.thirdPersonView=oldThirdPerson;
            throw new IllegalStateException("Área da câmera não carregada");
        }
        mc.setRenderViewEntity(camera);
    }
    public static StudioSession get() { return active; }
    public static void start(Minecraft mc) {
        if (active!=null || mc.player==null || mc.world==null || mc.getIntegratedServer()==null) {
            if (mc.player!=null && mc.getIntegratedServer()==null) mc.player.sendMessage(new net.minecraft.util.text.TextComponentString("§eBuildCraft M1 funciona apenas em mundos individuais."));
            return;
        }
        RayTraceResult hit=mc.objectMouseOver;
        BlockPos at = hit!=null && hit.typeOfHit==RayTraceResult.Type.BLOCK
            ? hit.getBlockPos().offset(hit.sideHit) : new BlockPos(mc.player.getPositionEyes(1).add(mc.player.getLookVec().scale(5)));
        BuildDocument loaded=StudioStorage.load(mc,at);
        if(loaded==null) {
            mc.player.sendMessage(new net.minecraft.util.text.TextComponentString("§cBuildCraft: projeto incompatível ou corrompido. Nada será sobrescrito; verifique o backup."));
            return;
        }
        try { active=new StudioSession(mc,loaded); }
        catch(IllegalStateException ex) {
            mc.player.sendMessage(new net.minecraft.util.text.TextComponentString("§eBuildCraft: câmera fora dos chunks carregados."));
            active=null;return;
        }
        mc.displayGuiScreen(new StudioGui(active));
    }
    public static void tick(Minecraft mc) {
        if(active==null) return;
        StudioSession s=active;
        if(mc.player==null || mc.world==null || mc.player.dimension != s.startDimension || mc.player.isDead) {
            stop(mc); return;
        }
        if(s.inventoryOpen && mc.currentScreen==null) {
            s.inventoryOpen=false;
            mc.displayGuiScreen(new StudioGui(s));
            return;
        }
        if(s.inventoryOpen && mc.currentScreen instanceof net.minecraft.client.gui.inventory.GuiContainerCreative) {
            s.updateCamera(mc);
        } else if(!(mc.currentScreen instanceof StudioGui)) {
            stop(mc);return;
        }
        if(mc.player.getHealth() < s.health) {
            s.notice="Dano recebido: salvando projeto e saindo";
            stop(mc);
            mc.displayGuiScreen(null);
            return;
        }
        s.health = mc.player.getHealth();
        s.updateCamera(mc);
        if(++s.autosaveTicks>=200 && s.document.isDirty()) {
            if(!StudioStorage.save(mc,s.document)) s.notice="§cAutosave falhou: "+StudioStorage.lastError();
            s.autosaveTicks=0;
        }
    }
    public boolean updateCamera(Minecraft mc) {
        double[] f=EditorMath.forward(yaw,pitch);
        Vec3d pos=focus.subtract(f[0]*distance,f[1]*distance,f[2]*distance);
        if (mc.world==null || !mc.world.isBlockLoaded(new BlockPos(pos))) return false;
        camera.setPosition(pos.x,pos.y,pos.z);
        camera.prevPosX=camera.posX; camera.prevPosY=camera.posY;camera.prevPosZ=camera.posZ;
        camera.lastTickPosX=camera.posX; camera.lastTickPosY=camera.posY;camera.lastTickPosZ=camera.posZ;
        camera.rotationYaw=(float)yaw; camera.prevRotationYaw=(float)yaw;
        camera.rotationPitch=(float)pitch;camera.prevRotationPitch=(float)pitch;
        return true;
    }
    public boolean changeFocus(Minecraft mc, Vec3d candidate) {
        if (mc.world==null || mc.player==null) return false;
        if(mc.player.getDistanceSq(candidate.x,candidate.y,candidate.z) > 48*48 || !mc.world.isBlockLoaded(new BlockPos(candidate))) return false;
        Vec3d before=focus;focus=candidate;
        if(!updateCamera(mc)) { focus=before;return false; }
        return true;
    }
    public static void stop(Minecraft mc) {
        StudioSession s=active;
        if(s==null)return;
        if(mc.player!=null && mc.getIntegratedServer()!=null && !StudioStorage.save(mc,s.document)) {
            mc.player.sendMessage(new net.minecraft.util.text.TextComponentString("§cBuildCraft: falha ao salvar! Verifique espaço/permissões na pasta do jogo."));
        }
        s.inventoryOpen=false;
        StudioPicking.reset();
        if(mc.getRenderViewEntity()==s.camera) mc.setRenderViewEntity(mc.player!=null?mc.player:s.oldCamera);
        mc.gameSettings.thirdPersonView=s.oldThirdPerson;
        active=null;
    }
}
