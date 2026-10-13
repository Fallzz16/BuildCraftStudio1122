package dev.buildcraftstudio.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;

public final class ClientEvents {
    @SubscribeEvent public void onKeys(InputEvent.KeyInputEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player != null && mc.currentScreen == null && ClientProxy.OPEN.isPressed()) {
            StudioSession.start(mc);
        }
    }
    @SubscribeEvent public void tick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        StudioSession.tick(Minecraft.getMinecraft());
    }
    @SubscribeEvent public void render(RenderWorldLastEvent event) {
        StudioRenderer.render(Minecraft.getMinecraft(),event.getPartialTicks());
    }
}
