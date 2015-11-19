package net.specialattack.util;

import java.util.UUID;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;

public final class PlayerUtils {

    private PlayerUtils() {
    }

    public static EntityPlayerMP getServerPlayer(UUID uuid) {
        MinecraftServer server = MinecraftServer.getServer();
        if (server != null) {
            ServerConfigurationManager configManager = server.getConfigurationManager();
            if (configManager != null && configManager.playerEntityList != null) {
                for (Object o : configManager.playerEntityList) {
                    if (o instanceof EntityPlayerMP) {
                        EntityPlayerMP player = (EntityPlayerMP) o;
                        if (player.getUniqueID().equals(uuid)) {
                            return player;
                        }
                    }
                }
            }
        }
        return null;
    }

}
