package net.specialattack.forge.core.sync;

import cpw.mods.fml.relauncher.Side;
import java.util.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.specialattack.forge.core.SpACore;
import net.specialattack.forge.core.sync.packet.S02TrackStorage;
import net.specialattack.util.PlayerUtils;

public class SyncTrackingStorage {

    /**
     * Contains all players that are tracking this storage
     */
    public Set<PlayerTracker> playerTrackers;

    public Set<ISyncableOwner> globalSyncableOwners = new HashSet<ISyncableOwner>();
    public Set<ISyncable> globalSyncables = new HashSet<ISyncable>();

    public Set<ISyncableOwner> trackingSyncableOwners = new HashSet<ISyncableOwner>();
    public Set<ISyncable> trackingSyncables = new HashSet<ISyncable>();

    public String name;
    private SyncRole role;
    public final Side side;
    public final UUID uuid;

    public SyncTrackingStorage(String name, Side side) {
        this(name, side, UUID.randomUUID());
    }

    public SyncTrackingStorage(String name, Side side, UUID uuid) {
        this.name = name;
        this.uuid = uuid;
        this.side = side;
        if (side == Side.SERVER) {
            this.playerTrackers = Collections.synchronizedSet(new HashSet<PlayerTracker>());
        }
    }

    public void setRole(SyncRole role) {
        this.role = role;
    }

    protected void debug(String message, Object... args) {
        if (this.side == Side.CLIENT) {
            SyncHandlerClient.debug(message, args);
        } else {
            SyncHandler.debug(message, args);
        }
    }

    public void releaseData() {
        if (this.playerTrackers != null) {
            for (PlayerTracker tracker : this.playerTrackers) {
                tracker.releaseData();
            }
            this.debug("[%s] Cleaned up %d player trackers for tracker", this.name, this.playerTrackers.size());
            this.playerTrackers.clear();
            this.playerTrackers = null;
        }
        for (ISyncableOwner owner : this.trackingSyncableOwners) {
            owner.register(null);
        }
        for (ISyncableOwner owner : this.globalSyncableOwners) {
            owner.register(null);
        }
        this.trackingSyncables.clear();
        this.trackingSyncables = null;
        this.trackingSyncableOwners.clear();
        this.trackingSyncableOwners = null;
        this.globalSyncables.clear();
        this.globalSyncables = null;
        this.globalSyncableOwners.clear();
        this.globalSyncableOwners = null;
    }

    public void tick() {
        for (ISyncable syncable : this.trackingSyncables) {
            if (syncable.changed()) {

                for (PlayerTracker tracker : this.playerTrackers) {
                    if (tracker.syncables.contains(syncable)) {
                        tracker.changedSyncables.add(syncable);
                    }
                }
            }
        }
    }

    public boolean canPlayerTrack(ConnectionInfo player) {
        return true;
    }

    public boolean startTrackingPlayer(ConnectionInfo connection) {
        if (this.side == Side.CLIENT) {
            throw new IllegalStateException("Server side code only!");
        }
        if (this.role == null) {
            throw new IllegalStateException("Cannot start tracking a storage that has no role assigned to it");
        }
        if (!this.canPlayerTrack(connection)) {
            this.debug("[%s] Player %d is not allowed to track tracker", this.name, connection.getUuid());
            return false; // Not allowed to track
        }
        for (PlayerTracker tracker : this.playerTrackers) {
            if (connection.getUuid().equals(tracker.getPlayerInfo().getUuid())) {
                this.debug("[%s] Already tracking player %s", this.name, connection.getUuid());
                return false;
            }
        }
        this.debug("[%s] Created tracker for player %s", this.name, connection.getUuid());
        PlayerTracker tracker = new PlayerTracker(connection, this);
        this.playerTrackers.add(tracker);
        EntityPlayerMP player = PlayerUtils.getServerPlayer(connection.getUuid());
        if (player != null) {
            SpACore.syncPacketHandler.sendTo(new S02TrackStorage(this.uuid, this.role, true), player);
        }
        for (ISyncableOwner owner : this.globalSyncableOwners) {
            if (tracker.attemptTrack(owner)) {
                if (!this.trackingSyncableOwners.contains(owner)) { // Contains check so that we don't try to add every syncable again
                    this.trackingSyncableOwners.add(owner);
                    for (ISyncable syncable : owner.getSyncables().values()) {
                        if (syncable != null) {
                            this.trackingSyncables.add(syncable);
                        }
                    }
                }
            }
        }
        return true;
    }

    public PlayerTracker getPlayerTracker(UUID uuid) {
        if (this.side == Side.CLIENT) {
            throw new IllegalStateException("Server side code only!");
        }
        for (PlayerTracker tracker : this.playerTrackers) {
            if (uuid.equals(tracker.getPlayerInfo().getUuid())) {
                return tracker;
            }
        }
        return null;
    }

    public void stopTrackingPlayer(UUID uuid) {
        if (this.side == Side.CLIENT) {
            throw new IllegalStateException("Server side code only!");
        }
        Iterator<PlayerTracker> i = this.playerTrackers.iterator();
        while (i.hasNext()) {
            PlayerTracker tracker = i.next();
            if (uuid.equals(tracker.getPlayerInfo().getUuid())) {
                this.debug("[%s] Removed tracker for player %s", this.name, uuid);
                EntityPlayerMP player = PlayerUtils.getServerPlayer(uuid);
                if (player != null) {
                    // We only need to send this packet, as all the contents of the storage on the client side will get nuked
                    SpACore.syncPacketHandler.sendTo(new S02TrackStorage(this.uuid, this.role, false), player);
                }
                // Now we look at all the syncables this player was tracking on this storage and see if any other player is tracking them
                // If they're being tracked by other players we keep them on our local watch list
                search:
                for (ISyncableOwner owner : tracker.syncableOwners) {
                    for (PlayerTracker other : this.playerTrackers) {
                        if (other.syncableOwners.contains(owner)) {
                            continue search;
                        }
                    }
                    // We didn't find the owner
                    this.trackingSyncableOwners.remove(owner);
                    this.trackingSyncables.removeAll(owner.getSyncables().values());
                }
                tracker.releaseData();
                i.remove();
            }
        }
    }

    /**
     * Registers a global syncable owner for this tracker, a global syncable is sent
     * to every client that has access to the tracking storage.
     *
     * @param owner
     *         The syncable owner to register.
     */
    public void registerSyncableOwner(ISyncableOwner owner) {
        if (owner != null) {
            if (owner.getSyncUUID() == null) {
                owner.setSyncUUID(UUID.randomUUID());
            }
            this.debug("[%s] Adding owner %s", this.name, owner.getDebugDisplay());
            this.globalSyncableOwners.add(owner);
            for (ISyncable syncable : owner.getSyncables().values()) {
                if (syncable != null) {
                    this.debug("[%s] Adding syncable %s", this.name, syncable.getDebugDisplay());
                    this.globalSyncables.add(syncable);
                } else {
                    this.debug("[%s] Skipped null syncable", this.name);
                }
            }
            owner.register(this);
            boolean tracked = false;
            for (PlayerTracker tracker : this.playerTrackers) {
                if (tracker.attemptTrack(owner)) {
                    tracked = true;
                }
            }
            if (tracked) {
                this.trackingSyncableOwners.add(owner);
                for (ISyncable syncable : owner.getSyncables().values()) {
                    if (syncable != null) {
                        this.trackingSyncables.add(syncable);
                    }
                }
            }
        }
    }

    /**
     * Unregisters a syncable owner, removes it from the watch list for every player as well as removes it from the storage-global list
     *
     * @param owner
     *         The owner to stop tracking
     */
    public void unregisterSyncableOwner(ISyncableOwner owner) {
        if (owner != null) {
            this.debug("[%s] Removing owner %s", this.name, owner.getDebugDisplay());
            if (this.trackingSyncableOwners.contains(owner)) { // Oh boy, it's being tracked, great now I have to inform EVERYBODY now
                for (PlayerTracker tracker : this.playerTrackers) {
                    if (tracker.syncableOwners.contains(owner)) {
                        this.debug("[%s] Untracking for %s", this.uuid, tracker.getPlayerInfo().getUuid());
                        tracker.untrack(owner);
                    }
                }

                this.trackingSyncableOwners.remove(owner);
                this.trackingSyncables.removeAll(owner.getSyncables().values());
            }
            this.globalSyncableOwners.remove(owner);
            this.globalSyncables.removeAll(owner.getSyncables().values());
        }
    }

    /**
     * Adds a syncable if its owner is also present.
     *
     * @param syncable
     *         The syncable to register
     */
    @Deprecated
    private void addSyncable(ISyncable syncable) {
        if (syncable != null) {
            ISyncableOwner owner = syncable.getOwner();
            if (owner != null) {
                if (this.globalSyncableOwners.contains(owner)) {
                    this.debug("[%s] Adding syncable %s from %s", this.name, syncable.getDebugDisplay(), owner.getDebugDisplay());
                    this.globalSyncables.add(syncable);
                    if (this.trackingSyncableOwners.contains(owner)) {
                        this.trackingSyncables.add(syncable); // Only add to the tracking list if we know the owner is being tracked
                    } else {
                        return;
                    }
                }

                for (PlayerTracker tracker : this.playerTrackers) {
                    if (tracker.syncableOwners.contains(owner)) {
                        tracker.syncables.add(syncable);
                        // Globals are also added to player trackers, so
                        // TODO: send tracking
                    }
                }
            }
        }
    }

    /**
     * Removes a syncable if its owner is also present.
     *
     * @param syncable
     *         The syncable to remove
     */
    @Deprecated
    private void removeSyncable(ISyncable syncable) {
        // TODO: implement
    }

    public ISyncableOwner getTrackedOwner(UUID uuid) {
        if (this.side == Side.SERVER) {
            throw new IllegalStateException("Client side code only!");
        }
        for (ISyncableOwner owner : this.globalSyncableOwners) {
            if (owner.getSyncUUID().equals(uuid)) {
                return owner;
            }
        }
        return null;
    }
}
