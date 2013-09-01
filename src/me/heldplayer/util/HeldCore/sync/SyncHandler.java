
package me.heldplayer.util.HeldCore.sync;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

import me.heldplayer.util.HeldCore.HeldCore;
import net.minecraft.network.INetworkManager;
import net.minecraft.world.World;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.relauncher.Side;

public class SyncHandler implements ITickHandler {

    private static LinkedList<ISyncable> syncablesServer = new LinkedList<ISyncable>();
    private static LinkedList<ISyncable> syncablesClient = new LinkedList<ISyncable>();

    private static LinkedList<PlayerTracker> players = new LinkedList<PlayerTracker>();

    public static void registerSyncable(ISyncableObjectOwner object, Side side) {
        if (side == Side.CLIENT) {
            syncablesClient.addAll(object.getSyncables());
        }
        else {
            List<ISyncable> syncables = object.getSyncables();
            syncablesServer.addAll(syncables);
            // TODO: Send start tracking packet
        }
    }

    public static void reset(Side side) {
        HeldCore.log.log(Level.FINER, "Clearing syncables list of " + side);
        if (side == Side.CLIENT) {
            syncablesClient.clear();
        }
        else {
            syncablesServer.clear();
            players.clear();
        }
    }

    public static void startTracking(INetworkManager manager) {
        players.add(new PlayerTracker(manager));
    }

    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData) {}

    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData) {
        // New tracking system:

        // Client loads Tile Entity
        // Client requests Tile to be tracked
        // Server starts tracking Tile for client

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

            ArrayList<ISyncableObjectOwner> toDrop = null;

            Iterator<ISyncable> i = SyncHandler.syncablesServer.iterator();

            while (i.hasNext()) {
                ISyncable syncable = i.next();

                if (syncable.getOwner() == null) {
                    i.remove();
                    continue;
                }
                if (syncable.getOwner().isInvalid()) {
                    if (toDrop == null) {
                        toDrop = new ArrayList<ISyncableObjectOwner>();
                    }

                    if (!toDrop.contains(syncable.getOwner())) {
                        toDrop.add(syncable.getOwner());
                    }

                    i.remove();
                    continue;
                }

                if (syncable.hasChanged()) {
                    // TODO: Send update tracked value packet
                    syncable.setChanged(true);
                }
            }

            if (toDrop != null) {
                for (ISyncableObjectOwner object : toDrop) {
                    HeldCore.log.log(Level.FINE, "Untracking ");
                    // TODO: Send stop tracking packet
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
