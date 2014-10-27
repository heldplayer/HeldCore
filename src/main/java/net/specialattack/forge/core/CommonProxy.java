package net.specialattack.forge.core;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.specialattack.forge.core.sync.SyncHandler;

public class CommonProxy extends SpACoreProxy {

    public boolean allowSnooping() {
        return !SpACore.optOut.getValue();
    }

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
