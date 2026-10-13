package dev.buildcraftstudio.client;

import dev.buildcraftstudio.CommonProxy;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.lwjgl.input.Keyboard;

public final class ClientProxy extends CommonProxy {
    public static final KeyBinding OPEN = new KeyBinding("key.buildcraftstudio.open",Keyboard.KEY_B,"key.categories.buildcraftstudio");
    @Override public void preInit() {
        ClientRegistry.registerKeyBinding(OPEN);
        ClientEvents events = new ClientEvents();
        MinecraftForge.EVENT_BUS.register(events);
        FMLCommonHandler.instance().bus().register(events);
    }
}
