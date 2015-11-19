package net.specialattack.forge.core.sync;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.util.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraftforge.fml.relauncher.Side;
import net.specialattack.forge.core.SpACore;
import net.specialattack.forge.core.sync.packet.S03StartSyncing;
import net.specialattack.forge.core.sync.packet.S04UpdateSyncable;
import net.specialattack.util.PlayerUtils;

public class ConnectionInfo implements Comparable<ConnectionInfo> {

    private UUID uuid;
    private Set<String> providers;

    public Set<PlayerTracker> trackers = new HashSet<PlayerTracker>();
    protected int refreshRate, ticks;
    protected List<NBTTagCompound> trackingUpdates = new ArrayList<NBTTagCompound>();

    public ConnectionInfo(NetHandlerPlayServer handler) {
        this(handler.playerEntity.getUniqueID(), Side.SERVER);
    }

    public ConnectionInfo(UUID uuid, Side side) {
        this.uuid = uuid;
        if (side == Side.CLIENT) {
            SyncHandlerClient.debug("Created server tracker %s", this.uuid);
        } else {
            SyncHandler.debug("Created player tracker %s", this.uuid);
        }
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public void setProviders(Collection<String> providers) {
        this.providers = Collections.unmodifiableSet(new TreeSet<String>(providers));
    }

    public Set<String> getProviders() {
        return this.providers;
    }

    public void setRefreshRate(int refreshRate) {
        this.refreshRate = refreshRate;
    }

    public int getRefreshRate() {
        return this.refreshRate;
    }

    @Override
    public int compareTo(ConnectionInfo o) {
        return this.uuid.compareTo(o.uuid);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ConnectionInfo that = (ConnectionInfo) o;
        return getUuid().equals(that.getUuid());
    }

    @Override
    public int hashCode() {
        return getUuid().hashCode();
    }

    public void tick() {
        int refreshRate = this.refreshRate;
        if (refreshRate <= 0) {
            return;
        }
        if (--this.ticks <= 0) {
            this.ticks = this.refreshRate;

            EntityPlayerMP player = null;

            // Send tracking start and stops
            if (this.trackingUpdates.size() > 0) {
                SyncHandler.debug("Sending updates to %s", this.getUuid());
                player = PlayerUtils.getServerPlayer(this.getUuid());
                int i = 0;
                NBTTagList list = new NBTTagList();
                for (NBTTagCompound tag : this.trackingUpdates) {
                    list.appendTag(tag);
                    i++;
                    if (i >= SyncHandler.MAX_REQUESTS_PER_PACKET) {
                        ConnectionInfo.sendTrackRequest(list, player);
                        list = new NBTTagList();
                        i = 0;
                    }
                }
                this.trackingUpdates.clear();
                if (i > 0) {
                    ConnectionInfo.sendTrackRequest(list, player);
                }
            }

            // Send updates
            int i = 0;
            NBTTagList list = new NBTTagList();
            for (PlayerTracker tracker : this.trackers) {
                if (tracker.changedSyncables.isEmpty()) {
                    continue;
                }
                if (player == null) {
                    player = PlayerUtils.getServerPlayer(this.getUuid());
                }
                Multimap<ISyncableOwner, ISyncable> changed = HashMultimap.create();
                for (ISyncable syncable : tracker.changedSyncables) {
                    changed.put(syncable.getOwner(), syncable);
                }
                tracker.changedSyncables.clear();
                for (ISyncableOwner owner : changed.keySet()) {
                    Map<String, ISyncable> syncables = new HashMap<String, ISyncable>();
                    Collection<ISyncable> col = changed.get(owner);
                    for (Map.Entry<String, ISyncable> entry : owner.getSyncables().entrySet()) {
                        if (col.contains(entry.getValue())) {
                            syncables.put(entry.getKey(), entry.getValue());
                        }
                    }
                    list.appendTag(S04UpdateSyncable.writeCompound(tracker.storage, owner, syncables));
                    i++;
                    if (i >= SyncHandler.MAX_REQUESTS_PER_PACKET) {
                        ConnectionInfo.sendSyncUpdate(list, player);
                        list = new NBTTagList();
                        i = 0;
                    }
                }
            }
            if (i > 0) {
                ConnectionInfo.sendSyncUpdate(list, player);
            }
        }
    }

    private static void sendTrackRequest(NBTTagList list, EntityPlayerMP player) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setTag("data", list);
        SyncHandler.debug("Sending tracking request for %d objects to %s", list.tagCount(), player.getUniqueID());
        SpACore.syncPacketHandler.sendTo(new S03StartSyncing(tag), player);
    }

    private static void sendSyncUpdate(NBTTagList list, EntityPlayerMP player) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setTag("data", list);
        SyncHandler.debug("Sending sync updates for %d objects to %s", list.tagCount(), player.getUniqueID());
        SpACore.syncPacketHandler.sendTo(new S04UpdateSyncable(tag), player);
    }

}
