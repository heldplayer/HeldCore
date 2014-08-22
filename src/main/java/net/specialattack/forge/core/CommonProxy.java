package net.specialattack.forge.core;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraftforge.common.MinecraftForge;
import net.specialattack.forge.core.sync.SyncHandler;

public class CommonProxy extends SpACoreProxy {

    public boolean allowSnooping() {
        return !SpACore.optOut.getValue();
    }

    public EntityPlayer getClientPlayer() {
        return null;
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
    }

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

    // FIXME: Restart tracking on world change
    @SubscribeEvent
    public void onServerConnectionFromClient(FMLNetworkEvent.ServerConnectionFromClientEvent event) {
        NetHandlerPlayServer handler = (NetHandlerPlayServer) event.handler;
        if (handler.playerEntity.playerNetServerHandler == null) {
            handler.playerEntity.playerNetServerHandler = handler;
            SyncHandler.startTracking(handler);
            handler.playerEntity.playerNetServerHandler = null;
        } else {
            SyncHandler.startTracking(handler);
        }
    }

    @SubscribeEvent
    public void onServerDisconnectionFromClient(FMLNetworkEvent.ServerDisconnectionFromClientEvent event) {
        SyncHandler.stopTracking(event.handler);
    }

}
