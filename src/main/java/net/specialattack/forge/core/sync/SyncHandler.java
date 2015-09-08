package net.specialattack.forge.core.sync;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.relauncher.Side;
import java.util.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.specialattack.forge.core.CommonProxy;
import net.specialattack.forge.core.Objects;
import net.specialattack.forge.core.SpACore;
import net.specialattack.forge.core.event.SyncEvent;
import net.specialattack.forge.core.sync.packet.S01Connection;
import net.specialattack.forge.core.world.storage.SpACoreWorldData;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The server side handler for Syncing, also contains some data about the instance that can be used on the client
 */
public final class SyncHandler {

    public static final int MAX_REQUESTS_PER_PACKET = 128;

    private static boolean initialized = false;
    private static HashMap<String, SyncObjectProvider> providers = new HashMap<String, SyncObjectProvider>();

    public static final boolean debug = Boolean.parseBoolean(System.getProperty("spacore.sync.debug", "false"));
    public static final Logger log = LogManager.getLogger("SpACore:SyncS");

    public static UUID serverUUID;
    public static Map<UUID, ConnectionInfo> players = new TreeMap<UUID, ConnectionInfo>();
    public static Map<UUID, SyncTrackingStorage> syncStorages = Collections.synchronizedMap(new TreeMap<UUID, SyncTrackingStorage>());
    public static SyncTrackingStorage globalStorage;

    public static void initialize() {
        if (!SyncHandler.initialized) {
            new SyncHandler();
            SyncHandler.initialized = true;
        }
    }

    public static void debug(String message, Object... args) {
        if (SyncHandler.debug) {
            SyncHandler.log.log(Level.INFO, "[Sync/Server] " + String.format(message, args));
        }
    }

    protected static void registerProvider(SyncObjectProvider provider) {
        if (SyncHandler.initialized) {
            throw new IllegalStateException("Can't register new providers after init!");
        }
        SyncHandler.providers.put(provider.id, provider);
    }

    @SuppressWarnings("unchecked")
    protected static <T extends ISyncableOwner> SyncObjectProvider<T> getProvider(String name) {
        return (SyncObjectProvider<T>) SyncHandler.providers.get(name);
    }

    protected static Set<String> getAvailableProviderNames() {
        return SyncHandler.providers.keySet();
    }

    protected static ConnectionInfo getConnectionInfo(UUID uuid, boolean create) {
        ConnectionInfo result = SyncHandler.players.get(uuid);
        if (result == null && create) {
            result = new ConnectionInfo(uuid, Side.SERVER);
            SyncHandler.players.put(uuid, result);
            Objects.SYNC_EVENT_BUS.post(new SyncEvent.ServerConnectionSetup(result));
        }
        return result;
    }

    private SyncHandler() {
        FMLCommonHandler.instance().bus().register(this);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public static void onServerAboutToStart(FMLServerAboutToStartEvent event) {
        SyncHandler.serverUUID = UUID.randomUUID();
        SyncHandler.debug("Server starting, new UUID for the server is %s", SyncHandler.serverUUID);
        if (!SyncHandler.players.isEmpty() || !SyncHandler.syncStorages.isEmpty() || SyncHandler.globalStorage != null) {
            SyncHandler.log.warn("Server Sync Handler state is dirty! Cleaning up for server start.");
            SyncHandler.cleanUp();
        }
        SyncHandler.globalStorage = new SyncTrackingStorage("Global Tracker", Side.SERVER, SyncHandler.serverUUID);
        SyncHandler.globalStorage.setRole(SyncRole.ROLE_GLOBAL);
        SyncHandler.syncStorages.put(SyncHandler.globalStorage.uuid, SyncHandler.globalStorage);

        Objects.SYNC_EVENT_BUS.post(new SyncEvent.ServerStarted(SyncHandler.globalStorage));
    }

    public static void onServerStopped(FMLServerStoppedEvent event) {
        SyncHandler.debug("Server stopped, unsetting UUID and releasing objects");
        SyncHandler.serverUUID = null;
        Objects.SYNC_EVENT_BUS.post(new SyncEvent.ServerStopped(SyncHandler.globalStorage));
        SyncHandler.cleanUp();
    }

    private static void cleanUp() {
        if (!SyncHandler.players.isEmpty()) {
            SyncHandler.debug("Cleaned up %d player trackers", SyncHandler.players.size());
            SyncHandler.players.clear();
        }
        if (!SyncHandler.syncStorages.isEmpty()) {
            for (SyncTrackingStorage tracker : SyncHandler.syncStorages.values()) {
                tracker.releaseData();
            }
            SyncHandler.debug("Cleaned up %d world storages", SyncHandler.syncStorages.size());
            SyncHandler.syncStorages.clear();
        }
        SyncHandler.globalStorage = null;
    }

    public static void serverTick() {
        for (SyncTrackingStorage storage : SyncHandler.syncStorages.values()) {
            storage.tick();
        }
        for (ConnectionInfo connection : SyncHandler.players.values()) {
            connection.tick();
        }
    }

    @SubscribeEvent // FML Event
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        final EntityPlayerMP player = (EntityPlayerMP) event.player;

        // Send a packet to the client to say "hey, you're connected to a new server"
        Objects.SYNC_EVENT_BUS.post(new SyncEvent.ServerClientConnected(player));
        CommonProxy.serverScheduler.scheduleTask(new Runnable() {
            @Override
            public void run() {
                SyncHandler.debug("Sending connection data to player %s (%s)", player.getCommandSenderName(), player.getUniqueID());
                SpACore.syncPacketHandler.sendTo(new S01Connection(SyncHandler.serverUUID, SyncHandler.getAvailableProviderNames()), player);
            }
        }, 1L);
    }

    @SubscribeEvent // FML Event
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        EntityPlayerMP player = (EntityPlayerMP) event.player;

        UUID uuid = player.getUniqueID();
        ConnectionInfo connection = SyncHandler.getConnectionInfo(uuid, false);
        for (PlayerTracker tracker : new ArrayList<PlayerTracker>(connection.trackers)) {
            tracker.storage.stopTrackingPlayer(uuid);
        }
        SyncHandler.players.remove(uuid);

        Objects.SYNC_EVENT_BUS.post(new SyncEvent.ServerClientDisconnected(player, connection));
    }

    @SubscribeEvent // FML Event
    public void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        SyncHandler.log.warn(String.format("Player %s went from %d to %d", event.player.getUniqueID(), event.fromDim, event.toDim));
    }

    @SubscribeEvent // Forge Event
    public void onWorldLoad(WorldEvent.Load event) {
        World world = event.world;
        if (!world.isRemote && world.provider != null) {
            SyncHandler.debug("A world got loaded, creating a tracker for the world.");

            UUID syncUUID = SpACoreWorldData.getData(world).getSyncUUID();
            int dimId = world.provider.dimensionId;

            if (!SyncHandler.syncStorages.containsKey(syncUUID)) {
                SyncWorldTrackingStorage storage = new SyncWorldTrackingStorage(dimId, world, Side.SERVER, syncUUID);

                Objects.SYNC_EVENT_BUS.post(new SyncEvent.ServerWorldTracked(storage));

                SyncHandler.syncStorages.put(syncUUID, storage);
                SyncHandler.debug("Created tracker for dimension %d", dimId);
            } else {
                SyncHandler.debug("We already know about dimension %d!", dimId);
            }
        }
    }

    @SubscribeEvent // Forge Event
    public void onWorldUnload(WorldEvent.Unload event) {
        World world = event.world;
        if (!world.isRemote && world.provider != null) {
            SyncHandler.debug("A world got unloaded, releasing tracker.");

            UUID syncUUID = SpACoreWorldData.getData(world).getSyncUUID();
            int dimId = world.provider.dimensionId;

            if (SyncHandler.syncStorages.containsKey(syncUUID)) {
                SyncTrackingStorage storage = SyncHandler.syncStorages.remove(syncUUID);

                Objects.SYNC_EVENT_BUS.post(new SyncEvent.ServerWorldUntracked((SyncWorldTrackingStorage) storage));

                storage.releaseData();
                SyncHandler.debug("Removed tracker for dimension %d", dimId);
            } else {
                SyncHandler.debug("We don't know about dimension %d!", dimId);
            }
        }
    }

}
