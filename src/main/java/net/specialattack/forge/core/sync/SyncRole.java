package net.specialattack.forge.core.sync;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import net.minecraft.client.multiplayer.WorldClient;
import net.specialattack.forge.core.client.MC;

public class SyncRole {

    private static Map<String, SyncRole> roles = new TreeMap<String, SyncRole>();

    public static final SyncRole ROLE_GLOBAL = new SyncRole("SpACore:Global", new RoleHandler() {
        @Override
        @SideOnly(Side.CLIENT)
        public SyncTrackingStorage handle(UUID uuid, boolean startTracking) {
            if (SyncHandlerClient.globalStorage != null) {
                SyncHandlerClient.storages.remove(SyncHandlerClient.globalStorage.uuid);
                SyncHandlerClient.globalStorage.releaseData();
                SyncHandlerClient.globalStorage = null;
            }
            if (startTracking) {
                SyncHandlerClient.globalStorage = new SyncTrackingStorage("Global Tracker", Side.CLIENT, uuid);
                SyncHandlerClient.storages.put(uuid, SyncHandlerClient.globalStorage);
                return SyncHandlerClient.globalStorage;
            }
            return null;
        }
    });
    public static final SyncRole ROLE_WORLD = new SyncRole("SpACore:World", new RoleHandler() {
        @Override
        @SideOnly(Side.CLIENT)
        public SyncTrackingStorage handle(UUID uuid, boolean startTracking) {
            if (SyncHandlerClient.worldStorage != null) {
                SyncHandlerClient.storages.remove(SyncHandlerClient.worldStorage.uuid);
                SyncHandlerClient.worldStorage.releaseData();
                SyncHandlerClient.worldStorage = null;
            }
            if (startTracking) {
                WorldClient world = MC.getWorld();
                SyncHandlerClient.worldStorage = new SyncWorldTrackingStorage(world.provider.dimensionId, world, Side.CLIENT, uuid);
                SyncHandlerClient.storages.put(uuid, SyncHandlerClient.worldStorage);
                return SyncHandlerClient.worldStorage;
            }
            return null;
        }
    });
    public static final SyncRole ROLE_DUMMY = new SyncRole("SpACore:Dummy", null);

    public final String name;
    public final RoleHandler handler;

    public SyncRole(String name, RoleHandler handler) {
        if (SyncRole.roles.containsKey(name)) {
            throw new IllegalArgumentException("Duplicate role " + name + " already exists!");
        }
        this.name = name;
        this.handler = handler;
        SyncRole.roles.put(name, this);
    }

    public static SyncRole getRole(String name) {
        return SyncRole.roles.get(name);
    }

    public interface RoleHandler {

        @SideOnly(Side.CLIENT)
        SyncTrackingStorage handle(UUID uuid, boolean startTracking);
    }
}
