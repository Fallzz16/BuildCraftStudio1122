package dev.buildcraftstudio.client;

import java.util.Map;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL11;

/** Cached vanilla block models + a lightweight, terrain-projected editing grid. */
public final class StudioRenderer {
    private StudioRenderer() { }
    private static StudioSession cachedSession;
    private static long cachedRevision=-1;
    private static BufferBuilder.State cachedModel;
    private static boolean hasModel;
    private static final int GRID_RADIUS=6;
    private static final int GRID_WIDTH=GRID_RADIUS*2+1;
    private static final int[] gridHeight=new int[GRID_WIDTH*GRID_WIDTH];
    private static int gridX=Integer.MIN_VALUE,gridZ=Integer.MIN_VALUE,gridDimension=Integer.MIN_VALUE;
    private static net.minecraft.world.World cachedWorld;

    public static void reset() {
        cachedSession=null;cachedRevision=-1;cachedModel=null;hasModel=false;
        cachedWorld=null;gridX=Integer.MIN_VALUE;gridZ=Integer.MIN_VALUE;
    }

    public static void render(Minecraft mc,float partialTicks) {
        StudioSession s=StudioSession.get();
        if(s==null || mc.world==null || !(mc.currentScreen instanceof StudioGui))return;
        StudioPicking.capture(s);
        StudioGui gui=(StudioGui)mc.currentScreen;
        // Reuse the picking result from the UI; never issue two full terrain raycasts/frame.
        StudioPicking picked=gui.getHover();
        GlStateManager.pushMatrix();
        GlStateManager.translate(-s.camera.posX,-s.camera.posY,-s.camera.posZ);
        GlStateManager.disableLighting();
        GlStateManager.enableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA,GL11.GL_ONE,GL11.GL_ZERO);
        Tessellator tess=Tessellator.getInstance();
        BufferBuilder buffer=tess.getBuffer();
        mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        // Block models are expensive to bake. Render once for each document revision,
        // then replay already-built vertices while orbiting or moving the camera.
        if(s!=cachedSession || cachedRevision!=s.document.revision()) {
            cachedModel=null;hasModel=false;
            cachedSession=s;cachedRevision=s.document.revision();
            if(!s.document.blocks().isEmpty()) {
                buffer.begin(GL11.GL_QUADS,DefaultVertexFormats.BLOCK);
                for(Map.Entry<BlockPos,IBlockState> entry:s.document.blocks().entrySet()) {
                    BlockPos pos=s.document.origin().add(entry.getKey());
                    try {mc.getBlockRendererDispatcher().renderBlock(entry.getValue(),pos,mc.world,buffer);}
                    catch(RuntimeException ex) {
                        // The editor accepts only a small allowlist of blocks for now;
                        // a bad model must not corrupt the entire preview.
                        org.apache.logging.log4j.LogManager.getLogger("BuildCraftStudio").warn("Preview model at {} could not render",pos,ex);
                    }
                }
                hasModel=buffer.getVertexCount()>0;
                if(hasModel)cachedModel=buffer.getVertexState();
                tess.draw();
            }
        } else if(hasModel && cachedModel!=null) {
            mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            buffer.begin(GL11.GL_QUADS,DefaultVertexFormats.BLOCK);
            buffer.setVertexState(cachedModel);
            tess.draw();
        }
        mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        // The live placement preview now uses the selected block's actual model,
        // instead of drawing the same cyan cube for every material.
        if(picked!=null && picked.local!=null && picked.valid && gui.currentBlock()!=null) {
            BlockPos pos=s.document.origin().add(picked.local);
            GlStateManager.pushMatrix();
            GlStateManager.translate(pos.getX(),pos.getY(),pos.getZ());
            GlStateManager.color(1f,1f,1f,0.62f);
            mc.getBlockRendererDispatcher().renderBlockBrightness(gui.currentBlock(),1.0f);
            GlStateManager.color(1f,1f,1f,1f);
            GlStateManager.popMatrix();
        }

        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        GlStateManager.disableCull();
        buffer.begin(GL11.GL_LINES,DefaultVertexFormats.POSITION_COLOR);
        if(picked!=null && picked.local!=null) {
            BlockPos p=s.document.origin().add(picked.local);
            boolean valid=picked.valid && gui.currentBlock()!=null;
            lines(buffer,p.getX(),p.getY(),p.getZ(),valid?.24f:1f,valid?1f:.25f,valid?.38f:.25f,1f);
        }
        if(gui.getSelected()!=null && s.document.blocks().containsKey(gui.getSelected())) {
            BlockPos p=s.document.origin().add(gui.getSelected());
            lines(buffer,p.getX(),p.getY(),p.getZ(),1f,.93f,.19f,1f);
        }
        if(s.showGrid)drawTerrainGrid(mc,s,buffer);
        tess.draw();
        GlStateManager.enableCull();
        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.color(1f,1f,1f,1f);
        GlStateManager.popMatrix();
    }
    private static void refreshGrid(Minecraft mc,int x,int z) {
        if(mc.world==cachedWorld && x==gridX && z==gridZ && gridDimension==mc.player.dimension)return;
        cachedWorld=mc.world;gridX=x;gridZ=z;gridDimension=mc.player.dimension;
        for(int ix=0;ix<GRID_WIDTH;ix++)for(int iz=0;iz<GRID_WIDTH;iz++) {
            BlockPos sample=new BlockPos(x+ix-GRID_RADIUS,0,z+iz-GRID_RADIUS);
            int index=ix*GRID_WIDTH+iz;
            gridHeight[index]=mc.world.isBlockLoaded(sample)?mc.world.getHeight(sample).getY():-1;
        }
    }
    /** Top surface is sampled only after moving to a different grid cell, never per frame. */
    private static void drawTerrainGrid(Minecraft mc,StudioSession s,BufferBuilder buffer) {
        int cx=(int)Math.floor(s.focus.x),cz=(int)Math.floor(s.focus.z);
        refreshGrid(mc,cx,cz);
        for(int ix=0;ix<GRID_WIDTH;ix++) for(int iz=0;iz<GRID_WIDTH;iz++) {
            int height=gridHeight[ix*GRID_WIDTH+iz];
            if(height<1 || height>=mc.world.getActualHeight())continue;
            int x=cx+ix-GRID_RADIUS,z=cz+iz-GRID_RADIUS;
            double y=height+.018;
            float alpha=.36f;
            segment(buffer,x,y,z,x+1,y,z,alpha);
            segment(buffer,x,y,z,x,y,z+1,alpha);
        }
    }
    private static void segment(BufferBuilder b,double x1,double y1,double z1,double x2,double y2,double z2,float alpha) {
        b.pos(x1,y1,z1).color(1f,1f,1f,alpha).endVertex();
        b.pos(x2,y2,z2).color(1f,1f,1f,alpha).endVertex();
    }
    private static void lines(BufferBuilder b,double x,double y,double z,float r,float g,float bl,float a) {
        for(int v=0;v<8;v++)for(int axis=0;axis<3;axis++)if((v&(1<<axis))==0) {
            int next=v|(1<<axis);
            b.pos(x+(v&1),y+((v>>1)&1),z+((v>>2)&1)).color(r,g,bl,a).endVertex();
            b.pos(x+(next&1),y+((next>>1)&1),z+((next>>2)&1)).color(r,g,bl,a).endVertex();
        }
    }
}
