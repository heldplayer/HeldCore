package net.specialattack.forge.core.sync;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import net.specialattack.forge.core.sync.packet.S03StartSyncing;
import net.specialattack.util.PlayerUtils;

public class PlayerTracker {

    private ConnectionInfo player;
    protected SyncTrackingStorage storage;

    public Set<ISyncableOwner> syncableOwners = new HashSet<ISyncableOwner>();
    public Set<ISyncable> syncables = new HashSet<ISyncable>();
    public Set<ISyncable> changedSyncables = new HashSet<ISyncable>();

    public PlayerTracker(ConnectionInfo player, SyncTrackingStorage storage) {
        this.player = player;
        this.storage = storage;
        player.trackers.add(this);
    }

    public void releaseData() {
        this.player.trackers.remove(this);
        this.syncables.clear();
        this.syncables = null;
        this.syncableOwners.clear();
        this.syncableOwners = null;
        this.changedSyncables.clear();
        this.changedSyncables = null;
        this.player = null;
        this.storage = null;
    }

    public ConnectionInfo getPlayerInfo() {
        return this.player;
    }

    public boolean attemptTrack(ISyncableOwner owner) {
        if (!owner.canPlayerTrack(PlayerUtils.getServerPlayer(this.player.getUuid()))) {
            return false; // The client isn't allowed to know about this
        }
        if (!this.player.getProviders().contains(owner.getProvider().id)) {
            return false; // The client doesn't know how to handle this
        }

        if (owner.getSyncUUID() == null) {
            owner.setSyncUUID(UUID.randomUUID());
        }

        SyncHandler.debug("Starting to track owner %s (%s) for %s", owner.getDebugDisplay(), owner.getSyncUUID(), this.player.getUuid());
        SyncHandler.debug("Owner syncables: ", this.syncables.toString());

        this.player.trackingUpdates.add(S03StartSyncing.writeCompound(this.storage, owner, true));
        this.syncableOwners.add(owner);
        this.syncables.addAll(owner.getSyncables().values());
        this.storage.trackingSyncableOwners.add(owner);
        this.storage.trackingSyncables.addAll(owner.getSyncables().values());
        return true;
    }

    public void untrack(ISyncableOwner owner) {
        this.player.trackingUpdates.add(S03StartSyncing.writeCompound(this.storage, owner, false));

        UUID uuid = owner.getSyncUUID();

        Iterator<ISyncableOwner> it = this.syncableOwners.iterator();
        while (it.hasNext()) {
            ISyncableOwner obj = it.next();
            if (obj.getSyncUUID().equals(uuid)) {
                it.remove();
                SyncHandler.debug("Untracked owner %s (%s) for %s", obj.getDebugDisplay(), uuid, this.player.getUuid());
            }
        }

        Iterator<ISyncable> it2 = this.syncables.iterator();
        while (it2.hasNext()) {
            ISyncable syncable = it2.next();
            if (syncable.getOwner().getSyncUUID().equals(uuid)) {
                it2.remove();
                SyncHandler.debug("Untracked syncable %s (owned by %s) for %s", syncable.getDebugDisplay(), uuid, this.player.getUuid());
            }
        }

        this.storage.checkStillTracked(owner);
    }

    public void untrack(UUID uuid) {
        ISyncableOwner last = null;
        Iterator<ISyncableOwner> it = this.syncableOwners.iterator();
        while (it.hasNext()) {
            ISyncableOwner obj = it.next();
            if (obj.getSyncUUID().equals(uuid)) {
                last = obj;
                it.remove();
                SyncHandler.debug("Untracked owner %s (%s) for %s", obj.getDebugDisplay(), uuid, this.player.getUuid());
            }
        }

        Iterator<ISyncable> it2 = this.syncables.iterator();
        while (it2.hasNext()) {
            ISyncable syncable = it2.next();
            if (syncable.getOwner().getSyncUUID().equals(uuid)) {
                it2.remove();
                SyncHandler.debug("Untracked syncable %s (owned by %s) for %s", syncable.getDebugDisplay(), uuid, this.player.getUuid());
            }
        }

        if (last != null) {
            this.player.trackingUpdates.add(S03StartSyncing.writeCompound(this.storage, last, false));
            this.storage.checkStillTracked(last);
        }
    }
}
