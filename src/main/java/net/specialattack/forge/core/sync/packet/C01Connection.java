package net.specialattack.forge.core.sync.packet;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.entity.player.EntityPlayer;
import net.specialattack.forge.core.sync.*;
import net.specialattack.util.CollectionUtils;

public class C01Connection extends SyncPacket {

    public int limit;
    public Set<String> availableProviders;

    public C01Connection() {
    }

    public C01Connection(int limit, Set<String> availableProviders) {
        this.limit = limit;
        this.availableProviders = availableProviders;
    }

    @Override
    public Side getSendingSide() {
        return Side.CLIENT;
    }

    @Override
    public Side getReceivingSide() {
        return Side.SERVER;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.limit = buf.readInt();
        int count = buf.readInt();
        this.availableProviders = new HashSet<String>(count);
        for (int i = 0; i < count; i++) {
            this.availableProviders.add(ByteBufUtils.readUTF8String(buf));
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.limit);
        buf.writeInt(this.availableProviders.size());
        for (String availableProvider : this.availableProviders) {
            ByteBufUtils.writeUTF8String(buf, availableProvider);
        }
    }

    @Override
    public void handle(MessageContext ctx, EntityPlayer player) {
        ConnectionInfo connection = SyncServerAPI.getConnectionInfo(player.getUniqueID(), true);
        connection.setRefreshRate(this.limit);
        connection.setProviders(CollectionUtils.intersection(this.availableProviders, SyncServerAPI.getAvailableProviderNames()));
        SyncHandler.globalStorage.startTrackingPlayer(connection);
        for (SyncTrackingStorage storage : SyncHandler.syncStorages.values()) {
            if (storage instanceof SyncWorldTrackingStorage && ((SyncWorldTrackingStorage) storage).dimId == player.worldObj.provider.dimensionId) {
                storage.startTrackingPlayer(connection);
            }
        }
    }
}
