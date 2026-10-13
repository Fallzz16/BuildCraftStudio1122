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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

/** Non-pausing mouse-controlled building canvas. No inventory or game clicks leak through. */
public final class StudioGui extends GuiScreen {
    private final StudioSession s;
    private int lastMouseX=-1, lastMouseY=-1;
    private boolean confirming;
    private int draftRevision;
    private int confirmedRevision=-1;
    private int mouseXNow,mouseYNow;
    private StudioPicking hover;
    private BlockPos selected;
    public StudioGui(StudioSession s) { this.s=s; }
    public BlockPos getSelected() { return selected; }
    public int getHoverX() { return mouseXNow; }
    public int getHoverY() { return mouseYNow; }
    @Override public boolean doesGuiPauseGame() { return false; }
    @Override public void drawScreen(int mx,int my,float ticks) {
        mouseXNow=mx;mouseYNow=my;
        hover=StudioPicking.at(mc,s,mx,my,width,height);
        drawRect(4,4,width-4,30,0xB6132029);
        fontRenderer.drawStringWithShadow("BUILDCRAFT STUDIO  |  M1",12,12,0xEDF5F7);
        drawRect(width-100,6,width-8,27,confirming?0xD0A04D16:0xCC386D52);
        drawCenteredString(fontRenderer,confirming?"CONFIRMAR":"FINALIZAR",width-54,13,0xFFFFFF);
        drawRect(4,height-51,width-4,height-4,0xB6132029);
        int shown=Math.min(BuildPalette.SAFE.size(),9);
        int center=width/2-(shown*37)/2;
        for(int i=0;i<shown;i++) {
            int x=center+i*37;
            drawRect(x,height-46,x+33,height-12,i==s.slot?0xE03A8B65:0xCC344351);
            String name=BuildPalette.SAFE.get(i).getLocalizedName();
            drawCenteredString(fontRenderer,""+(i+1),x+16,height-44,0xFFE9D7);
            String abbrev=name.length()>4?name.substring(0,4):name;
            drawCenteredString(fontRenderer,abbrev,x+16,height-29,0xFFFFFF);
        }
        String status=s.notice;
        if (hover!=null) status+=" | "+hover.reason;
        drawRect(4,33,Math.min(width-4,9+fontRenderer.getStringWidth(status)),46,0xAA132029);
        fontRenderer.drawStringWithShadow(status,7,36,0xE8EDF0);
        String tips="RMB arrastar: orbitar  |  MMB: pan  |  scroll: zoom  |  Shift+scroll: plano  |  PgUp/PgDn: altura  |  Ctrl+Z/Y: histórico  |  B/Esc: sair";
        fontRenderer.drawStringWithShadow(tips,10,height-61,0xD2DDDF);
        fontRenderer.drawStringWithShadow("Blocos: "+s.document.blocks().size()+"/512 | Altura: "+s.planeOffset,10,height-76,0xE0E8EF);
    }
    @Override protected void mouseClicked(int mx,int my,int button) throws IOException {
        lastMouseX=mx;lastMouseY=my;
        if(button!=0)return;
        if(my<31 && mx>width-105) { finish(); return; }
        int shown=Math.min(BuildPalette.SAFE.size(),9);
        int center=width/2-(shown*37)/2;
        if(my>=height-48 && my<=height-10 && mx>=center && mx<center+shown*37) {
            s.slot=(mx-center)/37; confirming=false;return;
        }
        if(my<78 || my>height-78) return;
        if(GuiScreen.isShiftKeyDown()) {
            StudioPicking pick=StudioPicking.at(mc,s,mx,my,width,height);
            if(pick.hitExisting!=null && s.document.blocks().containsKey(pick.hitExisting)) {
                selected=pick.hitExisting;
                s.notice="Bloco selecionado. Pressione Delete para remover.";
            } else s.notice="Nenhum bloco virtual nessa posição";
        } else {
            StudioPicking pick=StudioPicking.at(mc,s,mx,my,width,height);
            if(!pick.valid) { s.notice=pick.reason; return; }
            IBlockState state=BuildPalette.SAFE.get(s.slot).getDefaultState();
            if(s.document.edit(pick.local,state)) {s.notice="Bloco adicionado ao projeto virtual"; changed();}
            else s.notice="Limite do projeto atingido ou posição inválida";
        }
    }
    private void changed() { draftRevision++; confirming=false; }
    @Override protected void mouseClickMove(int mx,int my,int button,long elapsed) {
        if(lastMouseX<0) { lastMouseX=mx;lastMouseY=my;return; }
        int dx=mx-lastMouseX,dy=my-lastMouseY;
        lastMouseX=mx;lastMouseY=my;
        if(button==1) {
            double oldYaw=s.yaw,oldPitch=s.pitch;
            s.yaw=(s.yaw+dx*0.42)%360;
            s.pitch=EditorMath.clamp(s.pitch+dy*.33,-85,85);
            if(!s.updateCamera(mc)) {s.yaw=oldYaw;s.pitch=oldPitch;s.notice="Limite de chunks carregados";}
        } else if(button==2) {
            double scale=s.distance*.0025;
            double a=Math.toRadians(s.yaw),p=Math.toRadians(s.pitch);
            Vec3d right=new Vec3d(Math.cos(a),0,Math.sin(a));
            Vec3d up=new Vec3d(-Math.sin(a)*Math.sin(p),Math.cos(p),Math.cos(a)*Math.sin(p));
            s.changeFocus(mc,s.focus.subtract(right.scale(dx*scale)).add(up.scale(dy*scale)));
        }
    }
    @Override protected void mouseReleased(int mx,int my,int button) {
        lastMouseX=-1;lastMouseY=-1;
    }
    @Override public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int wheel=Mouse.getEventDWheel();
        if(wheel!=0) {
            if(GuiScreen.isShiftKeyDown()) s.planeOffset=Math.max(-32,Math.min(31,s.planeOffset+Integer.signum(wheel)));
            else {
                double old=s.distance;
                s.distance=EditorMath.clamp(s.distance*(wheel>0?.88:1.13),3.5,36);
                if(!s.updateCamera(mc)) {s.distance=old;s.notice="Limite de chunks carregados";}
            }
        }
    }
    @Override protected void keyTyped(char ch,int key) throws IOException {
        if(key==Keyboard.KEY_ESCAPE || key==Keyboard.KEY_B) { mc.displayGuiScreen(null);return; }
        if(key==Keyboard.KEY_PRIOR) s.planeOffset=Math.min(31,s.planeOffset+1);
        else if(key==Keyboard.KEY_NEXT) s.planeOffset=Math.max(-32,s.planeOffset-1);
        else if(GuiScreen.isCtrlKeyDown() && key==Keyboard.KEY_Z) { if(s.document.undo()) {s.notice="Desfeito";changed();} }
        else if(GuiScreen.isCtrlKeyDown() && key==Keyboard.KEY_Y) { if(s.document.redo()) {s.notice="Refeito";changed();} }
        else if(key==Keyboard.KEY_DELETE && selected!=null) {
            if(s.document.edit(selected,null)) {s.notice="Seleção removida";changed();}
            selected=null;
        }
        else if(key>=Keyboard.KEY_1 && key<=Keyboard.KEY_9) s.slot=Math.min(BuildPalette.SAFE.size()-1,key-Keyboard.KEY_1);
        else if(key==Keyboard.KEY_I) { s.yaw=45;s.pitch=35;s.updateCamera(mc); }
        else if(key==Keyboard.KEY_T) {s.pitch=85;s.updateCamera(mc);}
        else if(key==Keyboard.KEY_O) {s.yaw=0;s.pitch=0;s.updateCamera(mc);}
        else if(key==Keyboard.KEY_P) {s.yaw=90;s.pitch=0;s.updateCamera(mc);}
        else if(key==Keyboard.KEY_S && GuiScreen.isCtrlKeyDown()) {
            s.notice=StudioStorage.save(mc,s.document)?"Projeto salvo":"Falha ao salvar projeto";
        }
    }
    private void finish() {
        if(!mc.player.capabilities.isCreativeMode) {s.notice="M1: finalização somente no Criativo";return;}
        if(s.document.blocks().isEmpty()) {s.notice="Projeto vazio";return;}
        if(s.document.blocks().size()>128) {s.notice="M1: limite de 128 blocos por finalização";return;}
        int occupied=0;
        for(BlockPos local:s.document.blocks().keySet()) {
            BlockPos world=s.document.origin().add(local);
            if(!mc.world.isBlockLoaded(world)) {s.notice="Chunk indisponível: finalização bloqueada";return;}
            if(!mc.world.isAirBlock(world))occupied++;
        }
        if(!confirming || confirmedRevision!=draftRevision) {
            confirming=true;confirmedRevision=draftRevision;
            s.notice="Confirme FINALIZAR novamente: "+occupied+" posições já contêm blocos reais!";
            return;
        }
        PlacePacket packet=new PlacePacket(mc.player.dimension);
        for(Map.Entry<BlockPos,IBlockState> e:s.document.blocks().entrySet()) {
            BlockPos world=s.document.origin().add(e.getKey());
            Block b=e.getValue().getBlock();
            packet.add(world.getX(),world.getY(),world.getZ(),Block.getIdFromBlock(b),b.getMetaFromState(e.getValue()));
        }
        if(!StudioStorage.save(mc,s.document)) {s.notice="§cSalvar falhou: operação cancelada";return;}
        BuildCraftStudio.NETWORK.sendToServer(packet);
        confirming=false;
        s.notice="Pedido enviado; verifique o resultado no chat. Projeto mantido como backup.";
    }
    @Override public void onGuiClosed() { StudioSession.stop(mc); }
}
