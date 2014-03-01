
package net.specialattack.forge.core;

import net.minecraftforge.common.MinecraftForge;
import net.specialattack.forge.core.sync.SyncHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.relauncher.Side;

public class CommonProxy extends SpACoreProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {}

    @Override
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);

        FMLCommonHandler.instance().bus().register(this);
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        SyncHandler syncHandler = new SyncHandler();
        FMLCommonHandler.instance().bus().register(syncHandler);
        MinecraftForge.EVENT_BUS.register(syncHandler);
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
