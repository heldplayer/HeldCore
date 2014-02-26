
package net.specialattack.forge.core.sync;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetHandler;
import net.minecraft.world.World;
import net.specialattack.forge.core.Objects;
import net.specialattack.forge.core.SpACore;
import net.specialattack.forge.core.sync.packet.Packet2TrackingBegin;
import net.specialattack.forge.core.sync.packet.Packet3TrackingUpdate;
import net.specialattack.forge.core.sync.packet.Packet5TrackingEnd;
import net.specialattack.forge.core.sync.packet.Packet6SetInterval;

import org.apache.logging.log4j.Level;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;

public class SyncHandler {

    public static LinkedList<PlayerTracker> players = new LinkedList<PlayerTracker>();
    public static LinkedList<ISyncableObjectOwner> globalObjects = new LinkedList<ISyncableObjectOwner>();
    public static int lastSyncId = 0;
    public static LinkedList<ISyncable> clientSyncables = new LinkedList<ISyncable>();

    public static boolean debug = true;

    public static void reset() {
        if (SyncHandler.players.isEmpty()) {
            return;
        }

        Iterator<PlayerTracker> i = SyncHandler.players.iterator();

        while (i.hasNext()) {
            PlayerTracker tracker = i.next();
            tracker.syncables.clear();
            tracker.syncableOwners.clear();
            tracker.syncables = null;
            tracker.syncableOwners = null;
            tracker.handler = null;
        }

        SyncHandler.players.clear();
        SyncHandler.globalObjects.clear();

        SyncHandler.lastSyncId = 0;
    }

    public static void startTracking(INetHandler manager) {
        PlayerTracker tracker = new PlayerTracker(manager, SpACore.refreshRate.getValue());
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

        Iterator<PlayerTracker> i = SyncHandler.players.iterator();

        while (i.hasNext()) {
            PlayerTracker tracker = i.next();
            if (tracker.getPlayer() == player) {
                if (debug)
                    Objects.log.log(Level.INFO, "Starting to track " + object.toString());
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

        Iterator<PlayerTracker> i = SyncHandler.players.iterator();

        while (i.hasNext()) {
            PlayerTracker tracker = i.next();
            if (tracker.getPlayer() == player) {
                List<ISyncable> syncables = object.getSyncables();
                for (ISyncable syncable : syncables) {
                    SpACore.packetHandler.sendPacketToPlayer(new Packet5TrackingEnd(syncable), tracker.getPlayer());

                    if (tracker.syncables.remove(syncable)) {
                        if (debug)
                            Objects.log.log(Level.INFO, "Untracked " + syncable.toString() + " by request");
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

        Iterator<PlayerTracker> i = SyncHandler.players.iterator();

        while (i.hasNext()) {
            PlayerTracker tracker = i.next();

            if (tracker.syncableOwners.isEmpty()) {
                continue;
            }

            if (tracker.syncableOwners.contains(object)) {
                if (debug)
                    Objects.log.log(Level.INFO, "Dynamically tracking " + syncable.toString());
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
            Iterator<PlayerTracker> i = SyncHandler.players.iterator();

            while (i.hasNext()) {
                PlayerTracker tracker = i.next();

                tracker.syncables.remove(syncable);
                SpACore.packetHandler.sendPacketToPlayer(new Packet5TrackingEnd(syncable), tracker.getPlayer());
                if (debug)
                    Objects.log.log(Level.INFO, "Dynamically untracked " + syncable.toString());
            }
        }
    }

    public static void startTracking(ISyncableObjectOwner object) {
        SyncHandler.globalObjects.add(object);

        if (SyncHandler.players.isEmpty()) {
            return;
        }

        Iterator<PlayerTracker> i = SyncHandler.players.iterator();

        if (debug)
            Objects.log.log(Level.INFO, "Starting to track " + object.toString() + " for everybody");
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

        if (debug)
            Objects.log.log(Level.INFO, "Untracking " + object.toString() + " for everybody");

        while (i.hasNext()) {
            PlayerTracker tracker = i.next();

            for (ISyncable syncable : syncables) {
                SpACore.packetHandler.sendPacketToPlayer(new Packet5TrackingEnd(syncable), tracker.getPlayer());

                tracker.syncables.remove(syncable);
            }
            tracker.syncableOwners.remove(object);
        }
    }

    public static int initializationCounter = 0;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (initializationCounter > 0) {
            initializationCounter--;

            if (initializationCounter == 0) {
                SpACore.packetHandler.sendPacketToServer(new Packet6SetInterval(Integer.valueOf(SpACore.refreshRate.getValue())));
            }
        }
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase == Phase.START) {
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
                            if (debug)
                                Objects.log.log(Level.INFO, "Untracked " + syncable.toString());
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
