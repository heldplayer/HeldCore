package net.specialattack.forge.core.sync;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.specialattack.forge.core.CommonProxy;
import org.apache.logging.log4j.Level;

public class PlayerTracker {

    public Set<ISyncable> syncables;
    public Set<ISyncableObjectOwner> syncableOwners;
    protected Set<ISyncable> updatedSyncables;
    public final UUID uuid;
    public int ticks;
    public int interval;

    public PlayerTracker(INetHandlerPlayServer handler, int interval) {
        this.syncables = new HashSet<ISyncable>();
        this.syncableOwners = new HashSet<ISyncableObjectOwner>();
        this.updatedSyncables = new HashSet<ISyncable>();
        this.interval = interval;
        if (handler instanceof NetHandlerPlayServer) {
            NetHandlerPlayServer netHandlerPlayServer = (NetHandlerPlayServer) handler;
            this.uuid = netHandlerPlayServer.playerEntity.getUniqueID();
            if (SyncHandler.debug) {
                SyncHandler.Server.log.log(Level.INFO, String.format("Created tracker %s", this.uuid));
            }
        } else {
            throw new IllegalArgumentException("handler must be of type NetHandlerPlayServer");
        }
    }

    @Override
    public String toString() {
        return String.format("[Player Tracker %s]", this.uuid);
    }

    public EntityPlayerMP getPlayer() {
        return CommonProxy.getPlayerFromUUID(this.uuid);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PlayerTracker tracker = (PlayerTracker) o;

        return uuid.equals(tracker.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

}
