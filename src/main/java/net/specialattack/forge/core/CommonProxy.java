package net.specialattack.forge.core;

import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.specialattack.forge.core.sync.SyncHandler;

public class CommonProxy extends SpACoreProxy {

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

    protected void initializeSyncHandler() {
        SyncHandler.initialize();
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
    }

    @Override
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        this.initializeSyncHandler();
    }

    public Side getSide() {
        return Side.SERVER;
    }

}
