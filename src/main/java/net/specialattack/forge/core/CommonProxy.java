package net.specialattack.forge.core;

import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.specialattack.forge.core.client.texture.IconHolder;
import net.specialattack.forge.core.config.ConfigManager;
import net.specialattack.forge.core.sync.SyncHandler;
import net.specialattack.forge.core.sync.SyncServerAPI;
import net.specialattack.forge.core.sync.TileEntitySyncObjectProvider;
import net.specialattack.util.Consumer;
import net.specialattack.util.Scheduler;

public class CommonProxy extends SpACoreProxy {

    public static TileEntitySyncObjectProvider tileEntityProvider = new TileEntitySyncObjectProvider();
    public static Scheduler serverScheduler;

    public EntityPlayer getClientPlayer() {
        throw new IllegalStateException("This code is client-side only!");
    }

    public static EntityPlayerMP getPlayerFromUUID(UUID uuid) {
        for (Object obj : MinecraftServer.getServer().getConfigurationManager().playerEntityList) {
            if (obj instanceof EntityPlayerMP) {
                if (((EntityPlayerMP) obj).getUniqueID().equals(uuid)) {
                    return (EntityPlayerMP) obj;
                }
            }
        }
        return null;
    }

    @Override
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        SyncServerAPI.registerProvider(CommonProxy.tileEntityProvider);
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        SyncHandler.initialize();
    }

    @SubscribeEvent
    public void onTick(TickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (event.type == TickEvent.Type.SERVER) {
                SyncHandler.serverTick();
            }
            Scheduler.tick(event.type);
        }
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        for (ConfigManager manager : ConfigManager.configs.values()) {
            if (manager.modId.equals(event.modID)) {
                manager.configuration.save();
                manager.reload();
            }
        }
    }

    public void registerIconHolder(IconHolder holder) {
    }

    public void registerIconProvider(Consumer provider) {
    }


    public Side getSide() {
        return Side.SERVER;
    }
}
