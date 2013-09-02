
package me.heldplayer.util.HeldCore.sync;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

import me.heldplayer.util.HeldCore.HeldCore;
import me.heldplayer.util.HeldCore.sync.packet.Packet2TrackingBegin;
import me.heldplayer.util.HeldCore.sync.packet.Packet3TrackingUpdate;
import me.heldplayer.util.HeldCore.sync.packet.PacketHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetworkManager;
import net.minecraft.world.World;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class SyncHandler implements ITickHandler {

    private static LinkedList<PlayerTracker> players = new LinkedList<PlayerTracker>();
    private static int lastSyncId = 0;
    public static LinkedList<ISyncable> clientSyncables = new LinkedList<ISyncable>();

    public static void reset() {
        if (players.isEmpty()) {
            return;
        }

        Iterator<PlayerTracker> i = players.iterator();

        while (i.hasNext()) {
            PlayerTracker tracker = i.next();
            tracker.syncables.clear();
            tracker.syncables = null;
            tracker.manager = null;
        }

        players.clear();

        lastSyncId = 0;
    }

    public static void startTracking(INetworkManager manager) {
        players.add(new PlayerTracker(manager));
    }

    public static void stopTracking(INetworkManager manager) {
        if (players.isEmpty()) {
            return;
        }

        Iterator<PlayerTracker> i = players.iterator();

        while (i.hasNext()) {
            PlayerTracker tracker = i.next();
            if (tracker.manager == manager) {
                i.remove();
                tracker.syncables.clear();
                tracker.syncables = null;
                tracker.manager = null;
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
                HeldCore.log.log(Level.INFO, "Starting to track " + object.toString());
                tracker.syncables.addAll(object.getSyncables());
                tracker.manager.addToSendQueue(PacketHandler.instance.createPacket(new Packet2TrackingBegin(object.getPosX(), object.getPosY(), object.getPosZ(), object)));
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
                    if (tracker.syncables.remove(syncable)) {
                        HeldCore.log.log(Level.INFO, "Untracked " + syncable.toString() + " by request");
                    }
                }
            }
        }
    }

    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData) {}

    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData) {
        // New tracking system:

        // Client loads Tile Entity
        // Client requests Tile to be tracked
        // Server starts tracking Tile for client

        // Server checks for updates
        // Tile has updates
        // Server sends updates to all tracking players

        // Client unloads Tile Entity
        // Client requests to stop tracking Tile
        // Server stops sending updates
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

                if (player.syncables.isEmpty()) {
                    continue;
                }

                ArrayList<ISyncable> changedList = null;

                Iterator<ISyncable> i2 = player.syncables.iterator();

                while (i2.hasNext()) {
                    ISyncable syncable = i2.next();

                    if (syncable.getOwner().isInvalid()) {
                        i2.remove();
                        HeldCore.log.log(Level.INFO, "Untracked " + syncable.toString());
                        continue;
                    }

                    if (syncable.getId() == -1) {
                        syncable.setId(lastSyncId++);
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
