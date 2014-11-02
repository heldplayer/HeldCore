package net.specialattack.forge.core.sync;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.*;
import java.util.concurrent.Callable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.specialattack.forge.core.SpACore;
import net.specialattack.forge.core.client.MC;
import net.specialattack.forge.core.event.SyncEvent;
import net.specialattack.forge.core.sync.packet.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class SyncHandler {

    public static final boolean debug = Boolean.parseBoolean(System.getProperty("spacore.sync.debug", "false"));
    private static boolean initialized = false;

    public static void initialize() {
        if (!SyncHandler.initialized) {
            SyncHandler.initialized = true;
            new SyncHandler.Server();
            Thread playersOnlineChecker = new Thread(new PlayersOnlineChecker(), "Players Online Checker Thread");
            playersOnlineChecker.setDaemon(true);
            playersOnlineChecker.start();
        }
    }

    @SideOnly(Side.CLIENT)
    public static void initializeClient() {
        if (!SyncHandler.initialized) {
            SyncHandler.initialize();
            new SyncHandler.Client();
        }
    }

    @SideOnly(Side.CLIENT)
    public static final class Client {

        private static final Set<ISyncable> syncables = new HashSet<ISyncable>();
        private static int initializationCounter = 0;

        public static final Logger log = LogManager.getLogger("SpACore:SyncC");

        public static void reset() {
            synchronized (syncables) {
                if (SyncHandler.debug) {
                    SyncHandler.Client.log.log(Level.INFO, "SyncHandler.Client reset");
                }

                SyncHandler.Client.initializationCounter = 20;
                SyncHandler.Client.syncables.clear();
            }
        }

        public static void sendUpdateInterval() {
            if (SyncHandler.Client.initializationCounter == 0) {
                if (SyncHandler.debug) {
                    SyncHandler.Client.log.log(Level.INFO, "Sending update interval configuration");
                }

                SpACore.syncPacketHandler.sendPacketToServer(new Packet6SetInterval(SpACore.refreshRate.getValue()));
            }
        }

        public static void startTracking(Collection<ISyncable> syncables) {
            synchronized (SyncHandler.Client.syncables) {
                if (SyncHandler.debug) {
                    for (ISyncable syncable : syncables) {
                        SyncHandler.Client.log.log(Level.INFO, String.format("Starting tracking %s", syncable));
                        SyncHandler.Client.syncables.add(syncable);
                    }
                } else {
                    SyncHandler.Client.syncables.addAll(syncables);
                }
            }
        }

        public static ISyncable getSyncable(int id) {
            synchronized (SyncHandler.Client.syncables) {
                for (ISyncable syncable : SyncHandler.Client.syncables) {
                    if (syncable.getId() == id) {
                        return syncable;
                    }
                }
                return null;
            }
        }

        public static void removeSyncable(int id) {
            synchronized (SyncHandler.Client.syncables) {
                Iterator<ISyncable> i = SyncHandler.Client.syncables.iterator();
                while (i.hasNext()) {
                    ISyncable syncable = i.next();
                    if (syncable.getId() == id) {
                        SyncHandler.Client.log.log(Level.INFO, String.format("Untracking %s", syncable));
                        i.remove();
                    }
                }
            }
        }

        private Client() {
            FMLCommonHandler.instance().bus().register(this);
            MinecraftForge.EVENT_BUS.register(this);
        }

        @SubscribeEvent
        public void onWorldLoad(WorldEvent.Load event) {
            if (event.world.isRemote) {
                if (SyncHandler.debug) {
                    SyncHandler.Client.log.log(Level.INFO, "Reinitializing SyncHandler because of world change");
                }

                SyncHandler.Client.reset();
            }
        }

        @SubscribeEvent
        public void onWorldUnload(WorldEvent.Unload event) {
            if (event.world.isRemote) {
                if (SyncHandler.debug) {
                    SyncHandler.Client.log.log(Level.INFO, "Resetting SyncHandler because of world unload");
                }

                SyncHandler.Client.reset();
            }
        }

        @SubscribeEvent
        public void onClientTick(TickEvent.ClientTickEvent event) {
            if (SyncHandler.Client.initializationCounter > 0 && MC.getWorld() != null) {
                SyncHandler.Client.initializationCounter--;

                if (SyncHandler.Client.initializationCounter == 0) {
                    if (SyncHandler.debug) {
                        SyncHandler.Client.log.log(Level.INFO, "Done waiting for initiallization");
                    }

                    SpACore.syncPacketHandler.sendPacketToServer(new Packet6SetInterval(SpACore.refreshRate.getValue()));

                    SyncEvent.ClientStartSyncing clientEvent = new SyncEvent.ClientStartSyncing();
                    MinecraftForge.EVENT_BUS.post(clientEvent);
                }
            }
        }

        @SubscribeEvent
        public void onClientConnectedToServer(FMLNetworkEvent.ClientConnectedToServerEvent event) {
            if (SyncHandler.debug) {
                SyncHandler.Client.log.log(Level.INFO, "Connected to server!");
            }
            SyncHandler.Client.reset();
        }

        @SubscribeEvent
        public void onClientDisconnectionFromServer(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
            if (SyncHandler.debug) {
                SyncHandler.Client.log.log(Level.INFO, "Disconnected from server!");
            }
            SyncHandler.Client.reset();
        }

    }

    public static final class Server {

        private static final Set<PlayerTracker> players = new HashSet<PlayerTracker>();
        public static final Set<PlayerTracker> playerSet = Collections.unmodifiableSet(players);
        private static final Set<ISyncableObjectOwner> globalObjects = new HashSet<ISyncableObjectOwner>();
        private static final Queue<Callable<Void>> delayedTasks = new LinkedList<Callable<Void>>();
        private static int lastSyncId = 0;
        private static boolean terminated = false;

        public static final Logger log = LogManager.getLogger("SpACore:SyncS");

        public static void reset() {
            synchronized (SyncHandler.Server.globalObjects) {
                SyncHandler.Server.globalObjects.clear();
                SyncHandler.Server.lastSyncId = 0;
            }

            if (SyncHandler.debug) {
                SyncHandler.Server.log.log(Level.INFO, "Removed all server syncables");
            }

            synchronized (players) {
                if (SyncHandler.Server.players.isEmpty()) {
                    return;
                }

                for (PlayerTracker tracker : SyncHandler.Server.players) {
                    if (SyncHandler.debug) {
                        SyncHandler.Server.log.log(Level.INFO, String.format("Removing %s", tracker));
                    }
                    tracker.syncables.clear();
                    tracker.syncableOwners.clear();
                    tracker.syncables = null;
                    tracker.syncableOwners = null;
                }

                SyncHandler.Server.players.clear();
            }
        }

        public static int getNextFreeId() {
            return lastSyncId++;
        }

        public static void addDelayedTask(Callable<Void> task) {
            if (terminated) {
                return;
            }
            synchronized (SyncHandler.Server.delayedTasks) {
                SyncHandler.Server.delayedTasks.add(task);
            }
        }

        public static void terminateSynchronization() {
            if (SyncHandler.debug) {
                SyncHandler.Server.log.log(Level.INFO, "Synchronization has been terminated!");
            }
            terminated = true;
            reset();
        }

        public static void startTracking(INetHandlerPlayServer manager) {
            if (terminated) {
                return;
            }
            PlayerTracker tracker = new PlayerTracker(manager, SpACore.refreshRate.getValue());

            SyncEvent.StartTracking event = new SyncEvent.StartTracking(tracker);
            MinecraftForge.EVENT_BUS.post(event);

            synchronized (SyncHandler.Server.players) {
                SyncHandler.Server.players.add(tracker);
            }
            synchronized (SyncHandler.Server.globalObjects) {
                tracker.syncableOwners.addAll(SyncHandler.Server.globalObjects);
                for (ISyncableObjectOwner object : SyncHandler.Server.globalObjects) {
                    if (SyncHandler.debug) {
                        SyncHandler.Server.log.log(Level.INFO, String.format("Starting to track global %s", object));
                    }
                    tracker.syncables.addAll(object.getSyncables());
                    SpACore.syncPacketHandler.sendPacketToPlayer(new Packet2TrackingBegin(object), tracker.getPlayer());
                }
            }
        }

        public static void startTracking(EntityPlayerMP player) {
            if (terminated) {
                return;
            }
            if (player.playerNetServerHandler == null) {
                if (SyncHandler.debug) {
                    SyncHandler.Server.log.log(Level.INFO, String.format("Null playerNetServerHandler for %s", player));
                }
                return;
            }
            SyncHandler.Server.startTracking(player.playerNetServerHandler);
        }

        public static void stopTracking(EntityPlayerMP player) {
            if (terminated) {
                return;
            }
            if (player != null) {
                SyncHandler.Server.stopTracking(player.getUniqueID());
            }
        }

        public static void stopTracking(UUID uuid) {
            if (terminated) {
                return;
            }
            synchronized (SyncHandler.Server.players) {
                if (SyncHandler.Server.players.isEmpty()) {
                    if (SyncHandler.debug) {
                        SyncHandler.Server.log.log(Level.INFO, "No players?!");
                    }
                    return;
                }

                Iterator<PlayerTracker> i = SyncHandler.Server.players.iterator();

                while (i.hasNext()) {
                    PlayerTracker tracker = i.next();
                    if (tracker.uuid.equals(uuid)) {
                        SyncEvent.StopTracking event = new SyncEvent.StopTracking(tracker);
                        MinecraftForge.EVENT_BUS.post(event);

                        tracker.syncables.clear();
                        tracker.syncableOwners.clear();
                        tracker.syncables = null;
                        tracker.syncableOwners = null;
                        i.remove();
                    }
                }
            }
        }

        public static void startTracking(ISyncableObjectOwner object, EntityPlayerMP player) {
            if (terminated) {
                return;
            }
            synchronized (SyncHandler.Server.players) {
                if (SyncHandler.Server.players.isEmpty()) {
                    if (SyncHandler.debug) {
                        SyncHandler.Server.log.log(Level.INFO, "No players?!");
                    }
                    return;
                }

                for (PlayerTracker tracker : SyncHandler.Server.players) {
                    if (tracker.getPlayer() == player) {
                        if (SyncHandler.debug) {
                            SyncHandler.Server.log.log(Level.INFO, String.format("Starting to track %s", object));
                        }
                        tracker.syncables.addAll(object.getSyncables());
                        tracker.syncableOwners.add(object);
                        SpACore.syncPacketHandler.sendPacketToPlayer(new Packet2TrackingBegin(object), tracker.getPlayer());
                    }
                }
            }
        }

        public static void stopTracking(ISyncableObjectOwner object, EntityPlayerMP player) {
            if (terminated) {
                return;
            }
            synchronized (SyncHandler.Server.players) {
                if (SyncHandler.Server.players.isEmpty()) {
                    if (SyncHandler.debug) {
                        SyncHandler.Server.log.log(Level.INFO, "No players?!");
                    }
                    return;
                }

                for (PlayerTracker tracker : SyncHandler.Server.players) {
                    if (tracker.getPlayer() == player) {
                        List<ISyncable> syncables = object.getSyncables();
                        for (ISyncable syncable : syncables) {
                            SpACore.syncPacketHandler.sendPacketToPlayer(new Packet5TrackingEnd(syncable), tracker.getPlayer());

                            if (tracker.syncables.remove(syncable)) {
                                if (SyncHandler.debug) {
                                    SyncHandler.Server.log.log(Level.INFO, String.format("Untracked %s by request", syncable));
                                }
                            }
                        }
                        SpACore.syncPacketHandler.sendPacketToPlayer(new Packet7TrackingStop(object), tracker.getPlayer());
                        tracker.syncableOwners.remove(object);
                    }
                }
            }
        }

        public static void startTracking(ISyncableObjectOwner object, ISyncable syncable) {
            if (terminated) {
                return;
            }
            synchronized (SyncHandler.Server.players) {
                if (SyncHandler.Server.players.isEmpty()) {
                    return;
                }

                for (PlayerTracker tracker : SyncHandler.Server.players) {
                    if (tracker.syncableOwners.isEmpty()) {
                        continue;
                    }

                    if (tracker.syncableOwners.contains(object)) {
                        if (SyncHandler.debug) {
                            SyncHandler.Server.log.log(Level.INFO, String.format("Dynamically tracking %s", syncable));
                        }
                        tracker.syncables.add(syncable);
                    }
                }
            }
        }

        public static void stopTracking(ISyncableObjectOwner object, ISyncable syncable) {
            if (terminated) {
                return;
            }
            synchronized (SyncHandler.Server.players) {
                if (SyncHandler.Server.players.isEmpty()) {
                    return;
                }

                List<ISyncable> syncables = object.getSyncables();
                if (syncables.contains(syncable)) {
                    for (PlayerTracker tracker : SyncHandler.Server.players) {
                        tracker.syncables.remove(syncable);
                        SpACore.syncPacketHandler.sendPacketToPlayer(new Packet5TrackingEnd(syncable), tracker.getPlayer());
                        if (SyncHandler.debug) {
                            SyncHandler.Server.log.log(Level.INFO, String.format("Dynamically untracked %s", syncable));
                        }
                    }
                }
            }
        }

        public static void startTracking(ISyncableObjectOwner object) {
            if (terminated) {
                return;
            }
            synchronized (globalObjects) {
                if (globalObjects.contains(object)) {
                    if (SyncHandler.debug) {
                        SyncHandler.Server.log.log(Level.INFO, String.format("Trying to track %s globally but already tracking it!", object));
                    }
                    return;
                }
                globalObjects.add(object);
            }

            synchronized (SyncHandler.Server.players) {
                if (SyncHandler.Server.players.isEmpty()) {
                    return;
                }

                Iterator<PlayerTracker> i = SyncHandler.Server.players.iterator();

                if (SyncHandler.debug) {
                    SyncHandler.Server.log.log(Level.INFO, String.format("Starting to track %s for everybody", object));
                }
                while (i.hasNext()) {
                    PlayerTracker tracker = i.next();
                    tracker.syncables.addAll(object.getSyncables());
                    tracker.syncableOwners.add(object);
                    SpACore.syncPacketHandler.sendPacketToPlayer(new Packet2TrackingBegin(object), tracker.getPlayer());
                }
            }
        }

        public static void stopTracking(ISyncableObjectOwner object) {
            if (terminated) {
                return;
            }
            synchronized (globalObjects) {
                globalObjects.remove(object);
            }

            synchronized (SyncHandler.Server.players) {
                if (SyncHandler.Server.players.isEmpty()) {
                    return;
                }

                Iterator<PlayerTracker> i = SyncHandler.Server.players.iterator();

                List<ISyncable> syncables = object.getSyncables();

                if (SyncHandler.debug) {
                    SyncHandler.Server.log.log(Level.INFO, String.format("Untracking %s for everybody", object));
                }

                while (i.hasNext()) {
                    PlayerTracker tracker = i.next();

                    for (ISyncable syncable : syncables) {
                        SpACore.syncPacketHandler.sendPacketToPlayer(new Packet5TrackingEnd(syncable), tracker.getPlayer());

                        tracker.syncables.remove(syncable);
                    }

                    SpACore.syncPacketHandler.sendPacketToPlayer(new Packet7TrackingStop(object), tracker.getPlayer());

                    tracker.syncableOwners.remove(object);
                }

            }
        }

        public static PlayerTracker getTracker(EntityPlayer player) {
            synchronized (SyncHandler.Server.players) {
                for (PlayerTracker tracker : SyncHandler.Server.players) {
                    if (tracker.uuid.equals(player.getUniqueID())) {
                        return tracker;
                    }
                }
            }
            return null;
        }

        private Server() {
            FMLCommonHandler.instance().bus().register(this);
        }

        @SubscribeEvent
        public void onServerTick(TickEvent.ServerTickEvent event) {
            if (terminated) {
                return;
            }
            if (event.phase == Phase.END) {
                if (event.side.isServer()) {
                    HashSet<ISyncable> allChanged = null;

                    synchronized (SyncHandler.Server.delayedTasks) {
                        for (Callable<Void> task : SyncHandler.Server.delayedTasks) {
                            try {
                                task.call();
                            } catch (Exception e) {
                            }
                        }
                        SyncHandler.Server.delayedTasks.clear();
                    }

                    synchronized (SyncHandler.Server.globalObjects) {
                        HashSet<ISyncableObjectOwner> invalidOwners = null;

                        for (ISyncableObjectOwner owner : SyncHandler.Server.globalObjects) {
                            if (owner.isNotValid()) {
                                if (invalidOwners == null) {
                                    invalidOwners = new HashSet<ISyncableObjectOwner>();
                                }

                                invalidOwners.add(owner);
                            }
                        }

                        if (invalidOwners != null) {
                            for (ISyncableObjectOwner owner : invalidOwners) {
                                Server.stopTracking(owner);
                            }
                        }
                    }

                    synchronized (SyncHandler.Server.players) {
                        if (SyncHandler.Server.players.isEmpty()) {
                            return;
                        }

                        for (PlayerTracker player : SyncHandler.Server.players) {
                            player.ticks++;
                            boolean updating = player.ticks > player.interval;

                            if (player.syncables.isEmpty()) {
                                continue;
                            }

                            for (ISyncable syncable : player.syncables) {
                                if (syncable.hasChanged()) {
                                    if (allChanged == null) {
                                        allChanged = new HashSet<ISyncable>();
                                    }
                                    player.updatedSyncables.add(syncable);

                                    allChanged.add(syncable);
                                }
                            }

                            if (updating && !player.updatedSyncables.isEmpty()) {
                                ISyncable[] syncables = player.updatedSyncables.toArray(new ISyncable[player.updatedSyncables.size()]);

                                SpACore.syncPacketHandler.sendPacketToPlayer(new Packet3TrackingUpdate(syncables), player.getPlayer());

                                player.updatedSyncables.clear();
                                player.ticks = 0;
                            }

                            Iterator<ISyncableObjectOwner> i2 = player.syncableOwners.iterator();

                            while (i2.hasNext()) {
                                ISyncableObjectOwner owner = i2.next();

                                if (owner.isNotValid()) {
                                    if (SyncHandler.debug) {
                                        SyncHandler.Server.log.log(Level.INFO, String.format("Untracking owner %s for %s", owner, player.uuid));
                                    }
                                    SpACore.syncPacketHandler.sendPacketToPlayer(new Packet7TrackingStop(owner), player.getPlayer());

                                    for (ISyncable syncable : owner.getSyncables()) {
                                        if (SyncHandler.debug) {
                                            SyncHandler.Server.log.log(Level.INFO, String.format("Untracking child %s", syncable));
                                        }
                                        SpACore.syncPacketHandler.sendPacketToPlayer(new Packet5TrackingEnd(syncable), player.getPlayer());
                                    }
                                    i2.remove();
                                }
                            }
                        }

                        if (allChanged != null) {
                            for (ISyncable syncable : allChanged) {
                                syncable.setChanged(false);
                            }
                        }
                    }
                }
            }
        }

        @SubscribeEvent
        public void onServerDisconnectionFromClient(FMLNetworkEvent.ServerDisconnectionFromClientEvent event) {
            if (terminated) {
                return;
            }
            SyncHandler.Server.stopTracking(((NetHandlerPlayServer) event.handler).playerEntity);
        }

    }

    private SyncHandler() {
    }

}
