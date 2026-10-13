package dev.buildcraftstudio.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.RenderHandEvent;

public final class ClientEvents {
    @SubscribeEvent public void onKeys(InputEvent.KeyInputEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player != null && mc.currentScreen == null && ClientProxy.OPEN.isPressed()) {
            StudioSession.start(mc);
        }
    }
    @SubscribeEvent public void tick(TickEvent.ClientTickEvent event) {
        Minecraft mc=Minecraft.getMinecraft();
        if (event.phase == TickEvent.Phase.START) StudioSession.beforeClientTick(mc);
        else if (event.phase == TickEvent.Phase.END) StudioSession.tick(mc);
    }
    @SubscribeEvent public void hideDetachedCameraHand(RenderHandEvent event) {
        if (StudioSession.get()!=null) event.setCanceled(true);
    }
    @SubscribeEvent public void render(RenderWorldLastEvent event) {
        StudioRenderer.render(Minecraft.getMinecraft(),event.getPartialTicks());
    }
}
