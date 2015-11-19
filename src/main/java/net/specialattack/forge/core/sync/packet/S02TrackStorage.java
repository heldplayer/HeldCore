package net.specialattack.forge.core.sync.packet;

import io.netty.buffer.ByteBuf;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.specialattack.forge.core.sync.SyncRole;

public class S02TrackStorage extends SyncPacket {

    public UUID storageUUID;
    public SyncRole role;
    public boolean startTracking;

    public S02TrackStorage() {
    }

    public S02TrackStorage(UUID storageUUID, SyncRole role, boolean startTracking) {
        this.storageUUID = storageUUID;
        this.role = role;
        this.startTracking = startTracking;
    }

    @Override
    public Side getSendingSide() {
        return Side.SERVER;
    }

    @Override
    public Side getReceivingSide() {
        return Side.CLIENT;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.storageUUID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
        this.role = SyncRole.getRole(ByteBufUtils.readUTF8String(buf));
        this.startTracking = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, this.storageUUID.toString());
        ByteBufUtils.writeUTF8String(buf, this.role.name);
        buf.writeBoolean(this.startTracking);
    }

    @Override
    public void handle(MessageContext ctx, EntityPlayer player) {
        if (this.role != null && this.role.handler != null) {
            this.role.handler.handle(this.storageUUID, this.startTracking);
        }
    }
}
