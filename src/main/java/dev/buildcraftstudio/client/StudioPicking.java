package dev.buildcraftstudio.client;

import dev.buildcraftstudio.core.BuildDocument;
import dev.buildcraftstudio.core.EditorMath;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Map;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

/** Picks from the actual world projection, captured during RenderWorldLastEvent. */
public final class StudioPicking {
    public final BlockPos local;
    public final BlockPos hitExisting;
    public final boolean valid;
    public final String reason;
    private static final FloatBuffer MODEL=BufferUtils.createFloatBuffer(16);
    private static final FloatBuffer PROJECTION=BufferUtils.createFloatBuffer(16);
    private static final IntBuffer VIEWPORT=BufferUtils.createIntBuffer(16);
    private static boolean captured;
    private static StudioSession capturedSession;

    private StudioPicking(BlockPos local,BlockPos existing,boolean valid,String reason) {
        this.local=local;this.hitExisting=existing;this.valid=valid;this.reason=reason;
    }

    public static void capture(StudioSession session) {
        // RenderWorldLastEvent happens while the 3D matrices are active, before GuiScreen's 2D projection.
        MODEL.clear(); PROJECTION.clear(); VIEWPORT.clear();
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX,MODEL);
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX,PROJECTION);
        GL11.glGetInteger(GL11.GL_VIEWPORT,VIEWPORT);
        captured=VIEWPORT.get(2)>0 && VIEWPORT.get(3)>0;
        capturedSession=session;
    }
    public static void reset() {captured=false;capturedSession=null;}

    private static Vec3d ray(StudioSession s,Minecraft mc,int mx,int my,int width,int height) {
        if(captured && capturedSession==s) {
            FloatBuffer near=BufferUtils.createFloatBuffer(3);
            FloatBuffer far=BufferUtils.createFloatBuffer(3);
            float winX=VIEWPORT.get(0)+(float)((mx+.5)*VIEWPORT.get(2)/(double)width);
            float winY=VIEWPORT.get(1)+(float)((height-my-.5)*VIEWPORT.get(3)/(double)height);
            // Subtracting two unprojected positions cancels any camera translation in MODEL.
            // Only direction is used; the ray starts at the actual camera's world coordinates.
            if(GLU.gluUnProject(winX,winY,0.0f,MODEL,PROJECTION,VIEWPORT,near)
                    && GLU.gluUnProject(winX,winY,1.0f,MODEL,PROJECTION,VIEWPORT,far)) {
                Vec3d delta=new Vec3d(far.get(0)-near.get(0),far.get(1)-near.get(1),far.get(2)-near.get(2));
                if(delta.lengthSquared()>1.0e-10) return delta.normalize();
            }
        }
        // First-frame fallback: the next 3D frame supplies an exact projection.
        double nx=(2.0*(mx+.5)/width)-1.0, ny=1.0-(2.0*(my+.5)/height);
        double[] v=EditorMath.screenRay(s.yaw,s.pitch,nx,ny,(double)mc.displayWidth/mc.displayHeight,mc.gameSettings.fovSetting);
        return new Vec3d(v[0],v[1],v[2]);
    }

    public static StudioPicking at(Minecraft mc,StudioSession s,int mx,int my,int width,int height) {
        if(width<=0 || height<=0 || mc.world==null) return new StudioPicking(null,null,false,"Sem área de renderização");
        Vec3d start=new Vec3d(s.camera.posX,s.camera.posY,s.camera.posZ);
        Vec3d dir=ray(s,mc,mx,my,width,height);
        // Clip the ray before the first unavailable chunk. rayTraceBlocks must not
        // access unloaded terrain and accidentally load/generate it during editing.
        double maxRay=128;
        for(int step=1;step<=128;step++) {
            if(!mc.world.isBlockLoaded(new BlockPos(start.add(dir.scale(step))))) {
                maxRay=Math.max(0,step-1.0); break;
            }
        }
        if(maxRay<0.5) return new StudioPicking(null,null,false,"Terreno não carregado");
        Vec3d end=start.add(dir.scale(maxRay));
        double closest=Double.POSITIVE_INFINITY;
        BlockPos result=null,existing=null;

        // Candidate 1: the closest visible virtual face.
        for(Map.Entry<BlockPos,IBlockState> entry:s.document.blocks().entrySet()) {
            BlockPos worldPos=s.document.origin().add(entry.getKey());
            RayTraceResult hit=new AxisAlignedBB(worldPos).calculateIntercept(start,end);
            if(hit==null || hit.hitVec==null || hit.sideHit==null) continue;
            double distance=start.squareDistanceTo(hit.hitVec);
            if(distance<closest) {
                closest=distance;
                result=worldPos.offset(hit.sideHit);
                existing=entry.getKey();
            }
        }

        // Candidate 2: real terrain. Do not place behind visible ground/walls.
        RayTraceResult terrain=mc.world.rayTraceBlocks(start,end,false,true,false);
        if(terrain!=null && terrain.typeOfHit==RayTraceResult.Type.BLOCK && terrain.hitVec!=null && terrain.sideHit!=null) {
            double distance=start.squareDistanceTo(terrain.hitVec);
            if(distance<closest-1.0e-5) {
                closest=distance;
                result=terrain.getBlockPos().offset(terrain.sideHit);
                existing=null;
            }
        }

        // Candidate 3: active build plane. A raised plane overrides more distant terrain.
        double planeY=s.document.origin().getY()+s.planeOffset;
        double t=EditorMath.planeDistance(start.y,dir.y,planeY);
        if(!Double.isNaN(t) && t<=maxRay && t*t<closest-1.0e-5) {
            Vec3d hit=start.add(dir.scale(t));
            result=new BlockPos(Math.floor(hit.x),Math.floor(planeY),Math.floor(hit.z));
            existing=null;
        }
        if(result==null) return new StudioPicking(null,null,false,"Posicione o cursor sobre terreno, peças ou plano");
        BlockPos local=result.subtract(s.document.origin());
        if(!BuildDocument.validLocal(local))return new StudioPicking(local,existing,false,"Fora da área virtual 64³");
        if(result.getY()<1 || result.getY()>=mc.world.getActualHeight())return new StudioPicking(local,existing,false,"Altura inválida");
        if(!mc.world.isBlockLoaded(result))return new StudioPicking(local,existing,false,"Chunk não carregado");
        if(mc.player.getDistanceSq(result.getX()+.5,result.getY()+.5,result.getZ()+.5)>64*64)return new StudioPicking(local,existing,false,"Além do alcance Criativo (64)");
        if(s.document.blocks().containsKey(local))return new StudioPicking(local,existing,false,"Posição virtual já ocupada");
        return new StudioPicking(local,existing,true,"Pronto para colocar");
    }
}
