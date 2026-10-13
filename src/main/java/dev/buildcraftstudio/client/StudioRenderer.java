package dev.buildcraftstudio.client;

import dev.buildcraftstudio.core.BuildDocument;
import java.util.Map;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL11;

/** Section-independent M1 preview: lightweight translucent cubes, no world modifications. */
public final class StudioRenderer {
    private StudioRenderer() { }
    public static void render(Minecraft mc,float partialTicks) {
        StudioSession s=StudioSession.get();
        if(s==null || mc.world==null || !(mc.currentScreen instanceof StudioGui))return;
        StudioPicking.capture(s);
        GlStateManager.pushMatrix();
        GlStateManager.translate(-s.camera.posX,-s.camera.posY,-s.camera.posZ);
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA,GL11.GL_ONE,GL11.GL_ZERO);
        GlStateManager.depthMask(false);
        GlStateManager.disableCull();
        Tessellator tess=Tessellator.getInstance();
        BufferBuilder buf=tess.getBuffer();
        buf.begin(GL11.GL_QUADS,DefaultVertexFormats.POSITION_COLOR);
        for(Map.Entry<BlockPos,IBlockState> e:s.document.blocks().entrySet()) {
            BlockPos p=s.document.origin().add(e.getKey());
            cube(buf,p.getX(),p.getY(),p.getZ(),0.30f,0.76f,0.95f,0.24f);
        }
        StudioGui gui=(StudioGui)mc.currentScreen;
        StudioPicking picked=StudioPicking.at(mc,s,gui.getHoverX(),gui.getHoverY(),gui.width,gui.height);
        if(picked.local!=null && picked.valid && gui.currentBlock()!=null) {
            BlockPos p=s.document.origin().add(picked.local);
            cube(buf,p.getX(),p.getY(),p.getZ(),0.25f,1.0f,0.4f,0.28f);
        }
        tess.draw();
        buf.begin(GL11.GL_LINES,DefaultVertexFormats.POSITION_COLOR);
        for(Map.Entry<BlockPos,IBlockState> e:s.document.blocks().entrySet()) {
            BlockPos p=s.document.origin().add(e.getKey());
            lines(buf,p.getX(),p.getY(),p.getZ(),0.48f,0.89f,1.0f,0.75f);
        }
        if(picked.local!=null) {
            BlockPos p=s.document.origin().add(picked.local);
            if(picked.valid && gui.currentBlock()!=null) lines(buf,p.getX(),p.getY(),p.getZ(),0.32f,1f,0.56f,1f);
            else lines(buf,p.getX(),p.getY(),p.getZ(),1f,.2f,.2f,1f);
        }
        if(gui.getSelected()!=null && s.document.blocks().containsKey(gui.getSelected())) {
            BlockPos p=s.document.origin().add(gui.getSelected());
            lines(buf,p.getX(),p.getY(),p.getZ(),1f,.95f,.25f,1f);
        }
        if(s.showGrid) drawTerrainGrid(mc,s,buf);
        tess.draw();
        GlStateManager.enableCull();
        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }
    /** White lines projected over the loaded terrain's exposed top surface only. */
    private static void drawTerrainGrid(Minecraft mc,StudioSession s,BufferBuilder buffer) {
        int centerX=(int)Math.floor(s.focus.x),centerZ=(int)Math.floor(s.focus.z);
        final int radius=11;
        for(int x=centerX-radius;x<=centerX+radius;x++) {
            for(int z=centerZ-radius;z<=centerZ+radius;z++) {
                BlockPos sample=new BlockPos(x,0,z);
                if(!mc.world.isBlockLoaded(sample))continue;
                double top=mc.world.getHeight(sample).getY()+0.015;
                // Never draw behind a solid wall or far into the empty sky.
                if(top<1 || top>=mc.world.getActualHeight())continue;
                double fade=1.0-Math.max(Math.abs(x-centerX),Math.abs(z-centerZ))/(double)(radius+1);
                float alpha=(float)(0.17+0.28*fade);
                gridSegment(buffer,x,top,z,x+1,top,z,alpha);
                gridSegment(buffer,x,top,z,x,top,z+1,alpha);
                gridSegment(buffer,x+1,top,z,x+1,top,z+1,alpha);
                gridSegment(buffer,x,top,z+1,x+1,top,z+1,alpha);
            }
        }
    }
    private static void gridSegment(BufferBuilder b,double x1,double y1,double z1,
                                    double x2,double y2,double z2,float alpha) {
        b.pos(x1,y1,z1).color(1f,1f,1f,alpha).endVertex();
        b.pos(x2,y2,z2).color(1f,1f,1f,alpha).endVertex();
    }
    private static void cube(BufferBuilder b,double x,double y,double z,float r,float g,float bl,float a) {
        quad(b,x,y,z,x+1,y,z,x+1,y+1,z,x,y+1,z,r,g,bl,a);
        quad(b,x,y,z+1,x,y+1,z+1,x+1,y+1,z+1,x+1,y,z+1,r,g,bl,a);
        quad(b,x,y,z,x,y,z+1,x+1,y,z+1,x+1,y,z,r,g,bl,a);
        quad(b,x,y+1,z,x+1,y+1,z,x+1,y+1,z+1,x,y+1,z+1,r,g,bl,a);
        quad(b,x,y,z,x,y+1,z,x,y+1,z+1,x,y,z+1,r,g,bl,a);
        quad(b,x+1,y,z,x+1,y,z+1,x+1,y+1,z+1,x+1,y+1,z,r,g,bl,a);
    }
    private static void quad(BufferBuilder b,double x1,double y1,double z1,double x2,double y2,double z2,double x3,double y3,double z3,double x4,double y4,double z4,float r,float g,float bl,float a) {
        b.pos(x1,y1,z1).color(r,g,bl,a).endVertex();b.pos(x2,y2,z2).color(r,g,bl,a).endVertex();
        b.pos(x3,y3,z3).color(r,g,bl,a).endVertex();b.pos(x4,y4,z4).color(r,g,bl,a).endVertex();
    }
    private static void lines(BufferBuilder b,double x,double y,double z,float r,float g,float bl,float a) {
        for(int v=0;v<8;v++)for(int axis=0;axis<3;axis++)if((v&(1<<axis))==0){
            int next=v|(1<<axis);
            b.pos(x+(v&1),y+((v>>1)&1),z+((v>>2)&1)).color(r,g,bl,a).endVertex();
            b.pos(x+(next&1),y+((next>>1)&1),z+((next>>2)&1)).color(r,g,bl,a).endVertex();
        }
    }
}
