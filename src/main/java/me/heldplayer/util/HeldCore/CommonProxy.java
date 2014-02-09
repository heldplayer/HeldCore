
package me.heldplayer.util.HeldCore;

import me.heldplayer.util.HeldCore.sync.SyncHandler;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.relauncher.Side;

public class CommonProxy extends HeldCoreProxy {

    public void preInit(FMLPreInitializationEvent event) {}

    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);

        FMLCommonHandler.instance().bus().register(this);
    }

    public void postInit(FMLPostInitializationEvent event) {
        FMLCommonHandler.instance().bus().register(new SyncHandler());
    }

    public Side getSide() {
        return Side.SERVER;
    }

    @SubscribeEvent
    public void onServerConnectionFromClient(FMLNetworkEvent.ServerConnectionFromClientEvent event) {
        SyncHandler.startTracking(event.handler);
    }

    @SubscribeEvent
    public void onServerDisconnectionFromClient(FMLNetworkEvent.ServerDisconnectionFromClientEvent event) {
        SyncHandler.stopTracking(event.handler);
    }

}
