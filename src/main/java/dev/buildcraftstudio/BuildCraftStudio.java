package dev.buildcraftstudio;

import dev.buildcraftstudio.net.PlacePacket;
import dev.buildcraftstudio.server.PlacementManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = BuildCraftStudio.MODID, name = "BuildCraft Studio", version = "0.1.2", acceptedMinecraftVersions = "[1.12.2]")
public final class BuildCraftStudio {
    public static final String MODID = "buildcraftstudio";
    public static final SimpleNetworkWrapper NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);

    @SidedProxy(clientSide = "dev.buildcraftstudio.client.ClientProxy", serverSide = "dev.buildcraftstudio.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        NETWORK.registerMessage(PlacePacket.Handler.class, PlacePacket.class, 0, Side.SERVER);
        net.minecraftforge.fml.common.FMLCommonHandler.instance().bus().register(PlacementManager.INSTANCE);
        proxy.preInit();
    }
}
