
package me.heldplayer.util.HeldCore.sync;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

import me.heldplayer.util.HeldCore.HeldCore;
import me.heldplayer.util.HeldCore.Objects;
import me.heldplayer.util.HeldCore.sync.packet.Packet2TrackingBegin;
import me.heldplayer.util.HeldCore.sync.packet.Packet3TrackingUpdate;
import me.heldplayer.util.HeldCore.sync.packet.Packet5TrackingEnd;
import me.heldplayer.util.HeldCore.sync.packet.PacketHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetworkManager;
import net.minecraft.world.World;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class SyncHandler implements ITickHandler {

    public static LinkedList<PlayerTracker> players = new LinkedList<PlayerTracker>();
    public static LinkedList<ISyncableObjectOwner> globalObjects = new LinkedList<ISyncableObjectOwner>();
    public static int lastSyncId = 0;
    public static LinkedList<ISyncable> clientSyncables = new LinkedList<ISyncable>();

    public static void reset() {
        if (players.isEmpty()) {
            return;
        }

        Iterator<PlayerTracker> i = players.iterator();

        while (i.hasNext()) {
            PlayerTracker tracker = i.next();
            tracker.syncables.clear();
            tracker.syncableOwners.clear();
            tracker.syncables = null;
            tracker.syncableOwners = null;
            tracker.manager = null;
        }

        players.clear();
        globalObjects.clear();

        lastSyncId = 0;
    }

    public static void startTracking(INetworkManager manager) {
        PlayerTracker tracker = new PlayerTracker(manager, HeldCore.refreshRate.getValue());
        players.add(tracker);
        tracker.syncableOwners.addAll(globalObjects);
        for (ISyncableObjectOwner object : globalObjects) {
            tracker.syncables.addAll(object.getSyncables());
            tracker.manager.addToSendQueue(PacketHandler.instance.createPacket(new Packet2TrackingBegin(object)));
        }
    }

    public static void stopTracking(INetworkManager manager) {
        if (players.isEmpty()) {
            return;
        }

        Iterator<PlayerTracker> i = players.iterator();

        while (i.hasNext()) {
            PlayerTracker tracker = i.next();
            if (tracker.manager == manager) {
                tracker.syncables.clear();
                tracker.syncableOwners.clear();
                tracker.syncables = null;
                tracker.syncableOwners = null;
                tracker.manager = null;
                i.remove();
            }
        }
    }

    public static void startTracking(ISyncableObjectOwner object, EntityPlayerMP player) {
        if (players.isEmpty()) {
            return;
        }

        Iterator<PlayerTracker> i = players.iterator();

        while (i.hasNext()) {
            PlayerTracker tracker = i.next();
            if (tracker.getPlayer() == player) {
                Objects.log.log(Level.FINE, "Starting to track " + object.toString());
                tracker.syncables.addAll(object.getSyncables());
                tracker.syncableOwners.add(object);
                tracker.manager.addToSendQueue(PacketHandler.instance.createPacket(new Packet2TrackingBegin(object)));
            }
        }
    }

    public static void stopTracking(ISyncableObjectOwner object, EntityPlayerMP player) {
        if (players.isEmpty()) {
            return;
        }

        Iterator<PlayerTracker> i = players.iterator();

        while (i.hasNext()) {
            PlayerTracker tracker = i.next();
            if (tracker.getPlayer() == player) {
                List<ISyncable> syncables = object.getSyncables();
                for (ISyncable syncable : syncables) {
                    tracker.manager.addToSendQueue(PacketHandler.instance.createPacket(new Packet5TrackingEnd(syncable)));

                    if (tracker.syncables.remove(syncable)) {
                        Objects.log.log(Level.FINE, "Untracked " + syncable.toString() + " by request");
                    }
                }
                tracker.syncableOwners.remove(object);
            }
        }
    }

    public static void startTracking(ISyncableObjectOwner object, ISyncable syncable) {
        if (players.isEmpty()) {
            return;
        }

        Iterator<PlayerTracker> i = players.iterator();

        while (i.hasNext()) {
            PlayerTracker tracker = i.next();

            if (tracker.syncableOwners.isEmpty()) {
                continue;
            }

            if (tracker.syncableOwners.contains(object)) {
                Objects.log.log(Level.FINE, "Dynamically tracking " + syncable.toString());
                tracker.syncables.add(syncable);
            }
        }
    }

    public static void stopTracking(ISyncableObjectOwner object, ISyncable syncable) {
        if (players.isEmpty()) {
            return;
        }

        List<ISyncable> syncables = object.getSyncables();
        if (syncables.contains(syncable)) {
            Iterator<PlayerTracker> i = players.iterator();

            while (i.hasNext()) {
                PlayerTracker tracker = i.next();

                tracker.syncables.remove(syncable);
                tracker.manager.addToSendQueue(PacketHandler.instance.createPacket(new Packet5TrackingEnd(syncable)));
                Objects.log.log(Level.FINE, "Dynamically untracked " + syncable.toString());
            }
        }
    }

    public static void startTracking(ISyncableObjectOwner object) {
        globalObjects.add(object);

        if (players.isEmpty()) {
            return;
        }

        Iterator<PlayerTracker> i = players.iterator();

        Objects.log.log(Level.FINE, "Starting to track " + object.toString() + " for everybody");
        while (i.hasNext()) {
            PlayerTracker tracker = i.next();
            tracker.syncables.addAll(object.getSyncables());
            tracker.syncableOwners.add(object);
            tracker.manager.addToSendQueue(PacketHandler.instance.createPacket(new Packet2TrackingBegin(object)));
        }
    }

    public static void stopTracking(ISyncableObjectOwner object) {
        globalObjects.remove(object);

        if (players.isEmpty()) {
            return;
        }

        Iterator<PlayerTracker> i = players.iterator();

        List<ISyncable> syncables = object.getSyncables();

        Objects.log.log(Level.FINE, "Untracking " + object.toString() + " for everybody");

        while (i.hasNext()) {
            PlayerTracker tracker = i.next();

            for (ISyncable syncable : syncables) {
                tracker.manager.addToSendQueue(PacketHandler.instance.createPacket(new Packet5TrackingEnd(syncable)));

                tracker.syncables.remove(syncable);
            }
            tracker.syncableOwners.remove(object);
        }
    }

    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData) {}

    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData) {
        if (type.equals(EnumSet.of(TickType.WORLD))) {
            World world = (World) tickData[0];

            if (world.isRemote) {
                return;
            }

            if (players.isEmpty()) {
                return;
            }

            Iterator<PlayerTracker> i = players.iterator();

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
                        player.manager.addToSendQueue(PacketHandler.instance.createPacket(new Packet5TrackingEnd(syncable)));
                        i2.remove();
                        Objects.log.log(Level.FINE, "Untracked " + syncable.toString());
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

                    player.manager.addToSendQueue(PacketHandler.instance.createPacket(new Packet3TrackingUpdate(syncables)));
                }
            }

            if (allChanged != null) {
                for (ISyncable syncable : allChanged) {
                    syncable.setChanged(false);
                }
            }
        }
    }

    @Override
    public EnumSet<TickType> ticks() {
        return EnumSet.of(TickType.WORLD);
    }

    @Override
    public String getLabel() {
        return "Objects Syncing";
    }

}
