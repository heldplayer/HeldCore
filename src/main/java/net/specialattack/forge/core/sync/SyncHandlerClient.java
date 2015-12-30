package net.specialattack.forge.core.sync;

import com.google.common.collect.Lists;
import java.util.*;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.specialattack.forge.core.Objects;
import net.specialattack.forge.core.SpACore;
import net.specialattack.forge.core.event.SyncEvent;
import net.specialattack.forge.core.sync.packet.C01Connection;
import net.specialattack.forge.core.sync.packet.C02RequestSync;
import net.specialattack.util.CollectionUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class SyncHandlerClient {

    private static boolean initialized = false;

    public static final Logger log = LogManager.getLogger("SpACore:SyncC");

    public static ConnectionInfo server;
    public static SyncTrackingStorage globalStorage;
    public static SyncWorldTrackingStorage worldStorage;
    public static Map<UUID, SyncTrackingStorage> storages = Collections.synchronizedMap(new TreeMap<UUID, SyncTrackingStorage>());

    private static final List<SyncHandlerClient.TrackingRequests> trackingRequests = Collections.synchronizedList(new ArrayList<SyncHandlerClient.TrackingRequests>());

    /**
     * Initializes the SyncHandlerClient.
     * <br><br>
     * <b>Called during FMLPostInitializationEvent, do not call yourself</b>
     */
    public static SyncHandlerClient initialize() {
        if (!SyncHandlerClient.initialized) {
            SyncHandlerClient.initialized = true;
            return new SyncHandlerClient();
        }
        return null;
    }

    /**
     * Sends a debug message to the client SyncHandler logger.
     *
     * @param message
     *         A format string
     * @param args
     *         Arguments for the format string
     *
     * @see String#format(String, Object...)
     */
    public static void debug(String message, Object... args) {
        if (SyncHandler.debug) {
            SyncHandlerClient.log.log(Level.INFO, "[Sync/Client] " + String.format(message, args));
        }
    }

    private SyncHandlerClient() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    /**
     * Releases all data that has been stored by the client for syncing.
     */
    public static void releaseServerData() {
        SyncHandlerClient.debug("Releasing server specific syncing data");
        if (SyncHandlerClient.globalStorage != null) {
            SyncHandlerClient.globalStorage.releaseData();
            SyncHandlerClient.globalStorage = null;
        }
        SyncHandlerClient.trackingRequests.clear();
        SyncHandlerClient.storages.clear();
        SyncHandlerClient.server = null;
    }

    public static void releaseWorldData() {
        SyncHandlerClient.debug("Releasing world specific syncing data");
        if (SyncHandlerClient.worldStorage != null) {
            SyncHandlerClient.worldStorage.releaseData();
            SyncHandlerClient.worldStorage = null;
        }
    }

    @SubscribeEvent // FML event
    public void onClientConnectedToServer(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        if (event.handler instanceof NetHandlerPlayClient) {
            SyncHandlerClient.debug("Connected to a server");

            Objects.SYNC_EVENT_BUS.post(new SyncEvent.ClientConnected(event.handler));
        }
    }

    @SubscribeEvent // FML event
    public void onClientDisconnectionFromServer(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        if (event.handler instanceof NetHandlerPlayClient) {
            SyncHandlerClient.debug("Disconnected from server");

            SyncHandlerClient.releaseServerData();
            SyncHandlerClient.releaseWorldData();
            Objects.SYNC_EVENT_BUS.post(new SyncEvent.ClientDisconnected(event.handler));
        }
    }

    public void worldChanged(WorldClient world) {
        SyncHandlerClient.releaseWorldData();
        //if (world != null) {
        //SyncHandlerClient.worldStorage = new SyncWorldTrackingStorage(world.provider.dimensionId, world, Side.CLIENT);
        //}
    }

    @SubscribeEvent // FML event
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (SyncHandlerClient.server == null) {
            return;
        }
        sync:
        synchronized (SyncHandlerClient.trackingRequests) {
            if (SyncHandlerClient.trackingRequests.isEmpty()) {
                break sync;
            }
            int i = 0;
            NBTTagList list = new NBTTagList();
            List<SyncHandlerClient.TrackingRequests> delayedList = null;
            for (SyncHandlerClient.TrackingRequests request : SyncHandlerClient.trackingRequests) {
                if (!request.owner.canStartTracking()) {
                    if (delayedList == null) {
                        delayedList = new ArrayList<SyncHandlerClient.TrackingRequests>();
                    }
                    delayedList.add(request);
                    continue;
                }
                list.appendTag(C02RequestSync.writeCompound(request.storageId, request.owner, request.track));
                i++;
                if (i >= SyncHandler.MAX_REQUESTS_PER_PACKET) {
                    this.sendTrackRequest(list);
                    list = new NBTTagList();
                    i = 0;
                }
            }
            if (i > 0) {
                this.sendTrackRequest(list);
            }
            SyncHandlerClient.trackingRequests.clear();
            if (delayedList != null) {
                SyncHandlerClient.trackingRequests.addAll(delayedList);
            }
        }
    }

    private void sendTrackRequest(NBTTagList list) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setTag("data", list);
        SyncHandlerClient.debug("Sending tracking request for %d objects", list.tagCount());
        SpACore.syncPacketHandler.sendToServer(new C02RequestSync(tag));
    }

    public static void serverUUIDReceived(UUID uuid, Set<String> availableProviders) {
        boolean newConnection;
        if ((newConnection = SyncHandlerClient.server == null) || !SyncHandlerClient.server.getUuid().equals(uuid)) {
            SyncHandlerClient.debug("The current server got changed!" + (newConnection ? "" : " (Bungeecord?)"));
            SyncHandlerClient.releaseServerData();
            Set<String> availableProviderNames = SyncHandler.getAvailableProviderNames();

            SyncHandlerClient.server = new ConnectionInfo(uuid, Side.CLIENT);
            SyncHandlerClient.server.setProviders(Lists.newArrayList(CollectionUtils.intersection(availableProviders, availableProviderNames)));
            // Tell the server how many ticks should be between every update of the tracked data, and which providers the client has
            SpACore.syncPacketHandler.sendToServer(new C01Connection(SpACore.config.refreshRate, availableProviderNames));

            Objects.SYNC_EVENT_BUS.post(new SyncEvent.ClientServerInfoReceived(uuid, Collections.unmodifiableSet(availableProviders)));
        }
    }

    public static void requestStartTracking(ISyncableOwner owner, UUID storage) {
        SyncHandlerClient.trackingRequests.add(new SyncHandlerClient.TrackingRequests(owner, storage, true));
    }

    public static void requestStopTracking(ISyncableOwner owner, UUID storage) {
        SyncHandlerClient.trackingRequests.add(new SyncHandlerClient.TrackingRequests(owner, storage, false));
    }

    public static SyncTrackingStorage getStorage(UUID uuid) {
        return SyncHandlerClient.storages.get(uuid);
    }

    private static class TrackingRequests {

        public ISyncableOwner owner;
        public UUID storageId;
        public boolean track;

        public TrackingRequests(ISyncableOwner owner, UUID storageId, boolean track) {
            this.owner = owner;
            this.storageId = storageId;
            this.track = track;
        }
    }
}
