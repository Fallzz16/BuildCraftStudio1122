package dev.buildcraftstudio.client;

import dev.buildcraftstudio.core.BuildDocument;
import dev.buildcraftstudio.core.EditorMath;
import java.util.Map;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

/** Independent screen-space ray: default plane fallback; face-snap for virtual cubes. */
public final class StudioPicking {
    public final BlockPos local;
    public final BlockPos hitExisting;
    public final boolean valid;
    public final String reason;
    private StudioPicking(BlockPos local, BlockPos hitExisting, boolean valid, String reason) {
        this.local=local;this.hitExisting=hitExisting;this.valid=valid;this.reason=reason;
    }
    public static StudioPicking at(Minecraft mc,StudioSession s,int mx,int my,int width,int height) {
        if(width<=0 || height<=0) return new StudioPicking(null,null,false,"Sem área de renderização");
        double nx=(2.0*mx/width)-1,ny=1-(2.0*my/height);
        double aspect=(double)mc.displayWidth/mc.displayHeight;
        double[] v=EditorMath.screenRay(s.yaw,s.pitch,nx,ny,aspect,mc.gameSettings.fovSetting);
        Vec3d start=new Vec3d(s.camera.posX,s.camera.posY,s.camera.posZ);
        Vec3d dir=new Vec3d(v[0],v[1],v[2]);
        Vec3d end=start.add(dir.scale(128));
        double nearest=Double.MAX_VALUE;
        BlockPos snapped=null,hitExisting=null;
        for(Map.Entry<BlockPos,IBlockState> entry:s.document.blocks().entrySet()) {
            BlockPos wp=s.document.origin().add(entry.getKey());
            RayTraceResult hit=new AxisAlignedBB(wp).calculateIntercept(start,end);
            if(hit==null || hit.hitVec==null || hit.sideHit==null)continue;
            double dist=start.squareDistanceTo(hit.hitVec);
            if(dist<nearest) { nearest=dist;snapped=wp.offset(hit.sideHit);hitExisting=entry.getKey(); }
        }
        BlockPos target;
        if(snapped!=null) target=snapped;
        else {
            double planeY=s.document.origin().getY()+s.planeOffset;
            double length=EditorMath.planeDistance(start.y,dir.y,planeY);
            if(Double.isNaN(length)) return new StudioPicking(null,null,false,"Aponte para o plano ou um bloco virtual");
            Vec3d hit=start.add(dir.scale(length));
            target=new BlockPos(Math.floor(hit.x),Math.floor(planeY),Math.floor(hit.z));
        }
        BlockPos rel=target.subtract(s.document.origin());
        if(!BuildDocument.validLocal(rel))return new StudioPicking(rel,hitExisting,false,"Fora da área virtual 64³");
        if(target.getY()<1 || target.getY()>=mc.world.getActualHeight())return new StudioPicking(rel,hitExisting,false,"Altura inválida");
        if(!mc.world.isBlockLoaded(target))return new StudioPicking(rel,hitExisting,false,"Chunk ainda não carregado");
        if(mc.player.getDistanceSq(target.getX()+.5,target.getY()+.5,target.getZ()+.5)>64*64)return new StudioPicking(rel,hitExisting,false,"Além do alcance Criativo (64)");
        if(s.document.blocks().containsKey(rel))return new StudioPicking(rel,hitExisting,false,"Posição já ocupada");
        return new StudioPicking(rel,hitExisting,true,"Pronto para colocar");
    }
}
