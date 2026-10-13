package dev.buildcraftstudio.client;

import dev.buildcraftstudio.BuildCraftStudio;
import dev.buildcraftstudio.core.BuildDocument;
import dev.buildcraftstudio.core.BuildPalette;
import dev.buildcraftstudio.core.EditorMath;
import dev.buildcraftstudio.net.PlacePacket;
import java.io.IOException;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

/** Compact construction interface using the real player hotbar and Creative inventory. */
public final class StudioGui extends GuiScreen {
    private final StudioSession s;
    private int lastMouseX=-1,lastMouseY=-1;
    private boolean confirming;
    private int revision,confirmedRevision=-1;
    private int mouseXNow,mouseYNow;
    private int rightStartX,rightStartY;
    private boolean rightDrag;
    private StudioPicking hover;
    private BlockPos selected;
    public StudioGui(StudioSession s) { this.s=s; }
    public BlockPos getSelected() {return selected;}
    public int getHoverX() {return mouseXNow;}
    public int getHoverY() {return mouseYNow;}
    public IBlockState currentBlock() {
        ItemStack stack=mc.player==null?ItemStack.EMPTY:mc.player.inventory.getCurrentItem();
        if(stack.isEmpty()) return null;
        Item item=stack.getItem();
        if(!(item instanceof ItemBlock)) return null;
        Block block=((ItemBlock)item).getBlock();
        if(!BuildPalette.isSafe(block)) return null;
        try {return block.getStateFromMeta(((ItemBlock)item).getMetadata(stack.getMetadata()));}
        catch(RuntimeException ex) {return null;}
    }
    @Override public boolean doesGuiPauseGame() {return false;}
    private int barLeft() {return width/2-99;}
    private int barTop() {return height-27;}
    @Override public void drawScreen(int mx,int my,float partial) {
        mouseXNow=mx;mouseYNow=my;
        hover=StudioPicking.at(mc,s,mx,my,width,height);
        drawRect(4,4,191,22,0xA5101C23);
        fontRenderer.drawStringWithShadow("BUILDCRAFT | M1.2 | " +(s.walkMode?"STEVE":"CAMERA"),9,9,0xF4F6F8);
        drawRect(width-91,4,width-5,22,confirming?0xD0A04D16:0xCC287359);
        drawCenteredString(fontRenderer,confirming?"CONFIRMAR":"FINALIZAR",width-48,9,0xFFFFFF);
        drawRect(width-149,4,width-95,22,0xCC283845);
        drawCenteredString(fontRenderer,"ITENS E",width-122,9,0xF3F7FF);

        // True 9-slot player hotbar, without the oversized labels of v0.1.0.
        int x0=barLeft(),y0=barTop();
        drawRect(x0-3,y0-3,x0+201,y0+22,0xB314232D);
        for(int i=0;i<9;i++) {
            int x=x0+i*22;
            drawRect(x,y0,x+20,y0+20,i==mc.player.inventory.currentItem?0xD06A956C:0xAC34414A);
            ItemStack item=mc.player.inventory.getStackInSlot(i);
            if(!item.isEmpty()) {
                mc.getRenderItem().renderItemAndEffectIntoGUI(item,x+2,y0+2);
                mc.getRenderItem().renderItemOverlayIntoGUI(fontRenderer,item,x+2,y0+2,null);
            }
        }
        String reason=hover==null?"":hover.reason;
        String status=s.notice+(reason.isEmpty()?"":" | "+reason);
        String trimmed=fontRenderer.trimStringToWidth(status,width-20);
        drawRect(4,26,Math.min(width-4,12+fontRenderer.getStringWidth(trimmed)),40,0xB0182731);
        fontRenderer.drawStringWithShadow(trimmed,8,29,0xEDF1F3);
        String tips="WASD: mover "+(s.walkMode?"Steve":"camera")+" | Tab: alternar | RMB arrastar: orbitar | RMB clique: apagar | E: itens | G: grade | B: sair";
        String fitted=fontRenderer.trimStringToWidth(tips,width-16);
        fontRenderer.drawStringWithShadow(fitted,8,height-42,0xE7EDEF);
        if(hover!=null && hover.local!=null) {
            BlockPos p=s.document.origin().add(hover.local);
            String pos=p.getX()+", "+p.getY()+", "+p.getZ()+"  |  "+s.document.blocks().size()+"/512";
            drawRect(4,height-65,12+fontRenderer.getStringWidth(pos),height-52,0xA8182731);
            fontRenderer.drawStringWithShadow(pos,8,height-63,0xE5EBED);
        }
        if(currentBlock()==null) {
            String hint="Selecione um bloco compatível na hotbar. E abre todos os itens Criativos.";
            String compact=fontRenderer.trimStringToWidth(hint,width-20);
            drawRect(4,44,12+fontRenderer.getStringWidth(compact),57,0xAC623F2F);
            fontRenderer.drawStringWithShadow(compact,8,47,0xFFE2A9);
        }
    }
    private void openCreative() {
        if(!mc.player.capabilities.isCreativeMode) {
            s.notice="Inventário Criativo disponível apenas no Criativo"; return;
        }
        s.inventoryOpen=true;
        mc.displayGuiScreen(new GuiContainerCreative(mc.player));
    }
    @Override protected void mouseClicked(int mx,int my,int button) throws IOException {
        lastMouseX=mx;lastMouseY=my;
        if(button==1) {rightStartX=mx;rightStartY=my;rightDrag=false;return;}
        if(button!=0)return;
        if(my>=4 && my<23 && mx>=width-91) {finish();return;}
        if(my>=4 && my<23 && mx>=width-149 && mx<width-94) {openCreative();return;}
        if(my>=barTop()-3 && my<=barTop()+22 && mx>=barLeft() && mx<barLeft()+198) {
            mc.player.inventory.currentItem=Math.min(8,(mx-barLeft())/22);
            confirming=false;
            return;
        }
        if(my<60 || my>height-47)return;
        StudioPicking hit=StudioPicking.at(mc,s,mx,my,width,height);
        if(GuiScreen.isShiftKeyDown()) {
            BlockPos virtual=StudioPicking.pickVirtual(mc,s,mx,my,width,height);
            if(virtual!=null && s.document.blocks().containsKey(virtual)) {
                selected=virtual;s.notice="Bloco selecionado: Delete para remover";
            } else s.notice="Nenhum bloco virtual sob o cursor";
        } else {
            if(!hit.valid) {s.notice=hit.reason;return;}
            IBlockState state=currentBlock();
            if(state==null) {s.notice="Escolha um bloco suportado na hotbar (E abre Criativo)";return;}
            if(s.document.edit(hit.local,state)) {s.notice="Bloco posicionado";changed();}
            else s.notice="Posição inválida ou limite de peças atingido";
        }
    }
    private void changed() {revision++;confirming=false;}
    @Override protected void mouseClickMove(int mx,int my,int button,long elapsed) {
        if(lastMouseX<0) {lastMouseX=mx;lastMouseY=my;return;}
        int dx=mx-lastMouseX,dy=my-lastMouseY;lastMouseX=mx;lastMouseY=my;
        if(button==1) {
            if(Math.abs(mx-rightStartX)>3 || Math.abs(my-rightStartY)>3)rightDrag=true;
            if(!rightDrag)return;
            double beforeYaw=s.yaw,beforePitch=s.pitch;
            s.yaw=(s.yaw+dx*.42)%360;
            s.pitch=EditorMath.clamp(s.pitch+dy*.33,-85,85);
            if(!s.updateCamera(mc)) {s.yaw=beforeYaw;s.pitch=beforePitch;s.notice="Limite de chunks carregados";}
        } else if(button==2) {
            double scale=s.distance*.0025;
            double a=Math.toRadians(s.yaw),p=Math.toRadians(s.pitch);
            Vec3d right=new Vec3d(Math.cos(a),0,Math.sin(a));
            Vec3d up=new Vec3d(-Math.sin(a)*Math.sin(p),Math.cos(p),Math.cos(a)*Math.sin(p));
            s.changeFocus(mc,s.focus.subtract(right.scale(dx*scale)).add(up.scale(dy*scale)));
        }
    }
    @Override protected void mouseReleased(int mx,int my,int button) {
        if(button==1 && !rightDrag && my>=60 && my<=height-47) {
            BlockPos virtual=StudioPicking.pickVirtual(mc,s,mx,my,width,height);
            if(virtual!=null && s.document.edit(virtual,null)) {
                selected=null;s.notice="Bloco removido";changed();
            } else s.notice="Não há bloco virtual sob o cursor";
        }
        lastMouseX=-1;lastMouseY=-1;
    }
    @Override public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int wheel=Mouse.getEventDWheel();
        if(wheel!=0) {
            if(GuiScreen.isShiftKeyDown()) s.planeOffset=Math.max(-32,Math.min(31,s.planeOffset+Integer.signum(wheel)));
            else {
                double old=s.distance;s.distance=EditorMath.clamp(s.distance*(wheel>0?.88:1.13),3.5,36);
                if(!s.updateCamera(mc)) {s.distance=old;s.notice="Limite de chunks carregados";}
            }
        }
    }
    @Override protected void keyTyped(char ch,int key) throws IOException {
        if(key==Keyboard.KEY_ESCAPE || key==Keyboard.KEY_B) {mc.displayGuiScreen(null);return;}
        if(key==Keyboard.KEY_E) {openCreative();return;}
        if(key==Keyboard.KEY_TAB) {s.walkMode=!s.walkMode;s.notice=s.walkMode?"WASD move Steve, menu continua aberto":"WASD move a câmera livremente";s.releaseMovementKeys(mc);return;}
        if(key==Keyboard.KEY_G) {s.showGrid=!s.showGrid;s.notice=s.showGrid?"Grade visível":"Grade oculta";return;}
        if(key==Keyboard.KEY_PRIOR) s.planeOffset=Math.min(31,s.planeOffset+1);
        else if(key==Keyboard.KEY_NEXT) s.planeOffset=Math.max(-32,s.planeOffset-1);
        else if(GuiScreen.isCtrlKeyDown() && key==Keyboard.KEY_Z) {if(s.document.undo()){s.notice="Desfeito";changed();}}
        else if(GuiScreen.isCtrlKeyDown() && key==Keyboard.KEY_Y) {if(s.document.redo()){s.notice="Refeito";changed();}}
        else if(key==Keyboard.KEY_DELETE && selected!=null) {if(s.document.edit(selected,null)){s.notice="Excluído";changed();}selected=null;}
        else if(key>=Keyboard.KEY_1 && key<=Keyboard.KEY_9) mc.player.inventory.currentItem=Math.min(8,key-Keyboard.KEY_1);
        else if(key==Keyboard.KEY_I) {s.yaw=45;s.pitch=35;s.updateCamera(mc);}
        else if(key==Keyboard.KEY_T) {s.pitch=85;s.updateCamera(mc);}
        else if(key==Keyboard.KEY_O) {s.yaw=0;s.pitch=0;s.updateCamera(mc);}
        else if(key==Keyboard.KEY_P) {s.yaw=90;s.pitch=0;s.updateCamera(mc);}
        else if(key==Keyboard.KEY_S && GuiScreen.isCtrlKeyDown()) {
            s.notice=StudioStorage.save(mc,s.document)?"Projeto salvo": "Falha ao salvar: "+StudioStorage.lastError();
        }
    }
    private void finish() {
        if(!mc.player.capabilities.isCreativeMode) {s.notice="M1: finalizar somente no Criativo";return;}
        if(s.document.blocks().isEmpty()) {s.notice="Projeto vazio";return;}
        if(s.document.blocks().size()>128) {s.notice="M1: limite de 128 blocos por finalização";return;}
        int occupied=0;
        for(BlockPos local:s.document.blocks().keySet()) {
            BlockPos pos=s.document.origin().add(local);
            if(!mc.world.isBlockLoaded(pos)) {s.notice="Chunk não carregado: bloqueado";return;}
            if(!mc.world.isAirBlock(pos)) occupied++;
        }
        if(!confirming || confirmedRevision!=revision) {
            confirming=true;confirmedRevision=revision;
            s.notice="Clique CONFIRMAR: "+occupied+" blocos reais serão substituídos";
            return;
        }
        PlacePacket packet=new PlacePacket(mc.player.dimension);
        for(Map.Entry<BlockPos,IBlockState> e:s.document.blocks().entrySet()) {
            BlockPos p=s.document.origin().add(e.getKey());
            Block b=e.getValue().getBlock();
            packet.add(p.getX(),p.getY(),p.getZ(),Block.getIdFromBlock(b),b.getMetaFromState(e.getValue()));
        }
        if(!StudioStorage.save(mc,s.document)){s.notice="§cFalha ao salvar: "+StudioStorage.lastError();return;}
        BuildCraftStudio.NETWORK.sendToServer(packet);
        confirming=false;s.notice="Pedido enviado; confira o resultado no chat";
    }
    @Override public void onGuiClosed() {if(!s.inventoryOpen)StudioSession.stop(mc);}
}
