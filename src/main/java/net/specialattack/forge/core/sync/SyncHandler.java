package net.specialattack.forge.core.sync;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import java.util.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetHandler;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.specialattack.forge.core.Objects;
import net.specialattack.forge.core.SpACore;
import net.specialattack.forge.core.event.SyncEvent;
import net.specialattack.forge.core.sync.packet.Packet2TrackingBegin;
import net.specialattack.forge.core.sync.packet.Packet3TrackingUpdate;
import net.specialattack.forge.core.sync.packet.Packet5TrackingEnd;
import net.specialattack.forge.core.sync.packet.Packet6SetInterval;
import org.apache.logging.log4j.Level;

public class SyncHandler {

    public static LinkedList<PlayerTracker> players = new LinkedList<PlayerTracker>();
    public static LinkedList<ISyncableObjectOwner> globalObjects = new LinkedList<ISyncableObjectOwner>();
    public static int lastSyncId = 0;
    public static LinkedList<ISyncable> clientSyncables = new LinkedList<ISyncable>();

    public static boolean debug = true;
    public static int initializationCounter = 0;

    public static void reset() {
        SyncHandler.globalObjects.clear();
        SyncHandler.lastSyncId = 0;

        if (SyncHandler.debug) {
            Objects.log.log(Level.DEBUG, "Removed all server syncables");
        }

        if (SyncHandler.players.isEmpty()) {
            return;
        }

        for (PlayerTracker tracker : SyncHandler.players) {
            if (SyncHandler.debug) {
                Objects.log.log(Level.DEBUG, "Removing " + tracker.toString());
            }
            tracker.syncables.clear();
            tracker.syncableOwners.clear();
            tracker.syncables = null;
            tracker.syncableOwners = null;
            tracker.handler = null;
        }

        SyncHandler.players.clear();
    }

    public static void startTracking(INetHandler manager) {
        PlayerTracker tracker = new PlayerTracker(manager, SpACore.refreshRate.getValue());

        SyncEvent.StartTracking event = new SyncEvent.StartTracking(tracker);
        MinecraftForge.EVENT_BUS.post(event);

        SyncHandler.players.add(tracker);
        tracker.syncableOwners.addAll(SyncHandler.globalObjects);
        for (ISyncableObjectOwner object : SyncHandler.globalObjects) {
            tracker.syncables.addAll(object.getSyncables());
            SpACore.packetHandler.sendPacketToPlayer(new Packet2TrackingBegin(object), tracker.getPlayer());
        }
    }

    public static void stopTracking(INetHandler manager) {
        if (SyncHandler.players.isEmpty()) {
            return;
        }

        Iterator<PlayerTracker> i = SyncHandler.players.iterator();

        while (i.hasNext()) {
            PlayerTracker tracker = i.next();
            if (tracker.handler == manager) {
                SyncEvent.StopTracking event = new SyncEvent.StopTracking(tracker);
                MinecraftForge.EVENT_BUS.post(event);

                tracker.syncables.clear();
                tracker.syncableOwners.clear();
                tracker.syncables = null;
                tracker.syncableOwners = null;
                tracker.handler = null;
                i.remove();
            }
        }
    }

    public static void startTracking(ISyncableObjectOwner object, EntityPlayerMP player) {
        if (SyncHandler.players.isEmpty()) {
            return;
        }

        for (PlayerTracker tracker : SyncHandler.players) {
            if (tracker.getPlayer() == player) {
                if (SyncHandler.debug) {
                    Objects.log.log(Level.DEBUG, "Starting to track " + object.toString());
                }
                tracker.syncables.addAll(object.getSyncables());
                tracker.syncableOwners.add(object);
                SpACore.packetHandler.sendPacketToPlayer(new Packet2TrackingBegin(object), tracker.getPlayer());
            }
        }
    }

    public static void stopTracking(ISyncableObjectOwner object, EntityPlayerMP player) {
        if (SyncHandler.players.isEmpty()) {
            return;
        }

        for (PlayerTracker tracker : SyncHandler.players) {
            if (tracker.getPlayer() == player) {
                List<ISyncable> syncables = object.getSyncables();
                for (ISyncable syncable : syncables) {
                    SpACore.packetHandler.sendPacketToPlayer(new Packet5TrackingEnd(syncable), tracker.getPlayer());

                    if (tracker.syncables.remove(syncable)) {
                        if (SyncHandler.debug) {
                            Objects.log.log(Level.DEBUG, "Untracked " + syncable.toString() + " by request");
                        }
                    }
                }
                tracker.syncableOwners.remove(object);
            }
        }
    }

    public static void startTracking(ISyncableObjectOwner object, ISyncable syncable) {
        if (SyncHandler.players.isEmpty()) {
            return;
        }

        for (PlayerTracker tracker : SyncHandler.players) {
            if (tracker.syncableOwners.isEmpty()) {
                continue;
            }

            if (tracker.syncableOwners.contains(object)) {
                if (SyncHandler.debug) {
                    Objects.log.log(Level.DEBUG, "Dynamically tracking " + syncable.toString());
                }
                tracker.syncables.add(syncable);
            }
        }
    }

    public static void stopTracking(ISyncableObjectOwner object, ISyncable syncable) {
        if (SyncHandler.players.isEmpty()) {
            return;
        }

        List<ISyncable> syncables = object.getSyncables();
        if (syncables.contains(syncable)) {
            for (PlayerTracker tracker : SyncHandler.players) {
                tracker.syncables.remove(syncable);
                SpACore.packetHandler.sendPacketToPlayer(new Packet5TrackingEnd(syncable), tracker.getPlayer());
                if (SyncHandler.debug) {
                    Objects.log.log(Level.DEBUG, "Dynamically untracked " + syncable.toString());
                }
            }
        }
    }

    public static void startTracking(ISyncableObjectOwner object) {
        SyncHandler.globalObjects.add(object);

        if (SyncHandler.players.isEmpty()) {
            return;
        }

        Iterator<PlayerTracker> i = SyncHandler.players.iterator();

        if (SyncHandler.debug) {
            Objects.log.log(Level.DEBUG, "Starting to track " + object.toString() + " for everybody");
        }
        while (i.hasNext()) {
            PlayerTracker tracker = i.next();
            tracker.syncables.addAll(object.getSyncables());
            tracker.syncableOwners.add(object);
            SpACore.packetHandler.sendPacketToPlayer(new Packet2TrackingBegin(object), tracker.getPlayer());
        }
    }

    public static void stopTracking(ISyncableObjectOwner object) {
        SyncHandler.globalObjects.remove(object);

        if (SyncHandler.players.isEmpty()) {
            return;
        }

        Iterator<PlayerTracker> i = SyncHandler.players.iterator();

        List<ISyncable> syncables = object.getSyncables();

        if (SyncHandler.debug) {
            Objects.log.log(Level.DEBUG, "Untracking " + object.toString() + " for everybody");
        }

        while (i.hasNext()) {
            PlayerTracker tracker = i.next();

            for (ISyncable syncable : syncables) {
                SpACore.packetHandler.sendPacketToPlayer(new Packet5TrackingEnd(syncable), tracker.getPlayer());

                tracker.syncables.remove(syncable);
            }
            tracker.syncableOwners.remove(object);
        }
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        if (event.world.isRemote) {
            SyncHandler.initializationCounter = 0;
            SyncHandler.clientSyncables.clear();

            if (SyncHandler.debug) {
                Objects.log.log(Level.DEBUG, "Removed all client syncables");
            }
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (SyncHandler.initializationCounter > 0) {
            SyncHandler.initializationCounter--;

            if (SyncHandler.initializationCounter == 0) {
                SyncEvent.ClientStartSyncing clientEvent = new SyncEvent.ClientStartSyncing();
                MinecraftForge.EVENT_BUS.post(clientEvent);

                SpACore.packetHandler.sendPacketToServer(new Packet6SetInterval(SpACore.refreshRate.getValue()));
            }
        }
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase == Phase.END) {
            if (event.side.isServer()) {
                World world = event.world;

                if (world.isRemote) {
                    return;
                }

                if (SyncHandler.players.isEmpty()) {
                    return;
                }

                Iterator<PlayerTracker> i = SyncHandler.players.iterator();

                HashSet<ISyncable> allChanged = null;

                while (i.hasNext()) {
                    PlayerTracker player = i.next();

                    player.ticks++;
                    if (player.ticks <= player.interval) {
                        return;
                    }
                    player.ticks = 0;

                    if (player.syncables.isEmpty()) {
                        continue;
                    }

                    ArrayList<ISyncable> changedList = null;

                    Iterator<ISyncable> i2 = player.syncables.iterator();

                    while (i2.hasNext()) {
                        ISyncable syncable = i2.next();

                        if (syncable.getOwner().isNotValid()) {
                            SpACore.packetHandler.sendPacketToPlayer(new Packet5TrackingEnd(syncable), player.getPlayer());
                            i2.remove();
                            if (SyncHandler.debug) {
                                Objects.log.log(Level.DEBUG, "Untracked " + syncable.toString());
                            }
                            continue;
                        }

                        if (syncable.hasChanged()) {
                            if (changedList == null) {
                                changedList = new ArrayList<ISyncable>();
                                allChanged = new HashSet<ISyncable>();
                            }

                            changedList.add(syncable);
                            allChanged.add(syncable);
                        }
                    }

                    if (changedList != null) {
                        ISyncable[] syncables = changedList.toArray(new ISyncable[changedList.size()]);

                        SpACore.packetHandler.sendPacketToPlayer(new Packet3TrackingUpdate(syncables), player.getPlayer());
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
