package net.specialattack.forge.core.sync.packet;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.specialattack.forge.core.sync.SyncHandlerClient;

public class S01Connection extends SyncPacket {

    public UUID serverUUID;
    public Set<String> availableProviders;

    public S01Connection() {
    }

    public S01Connection(UUID serverUUID, Set<String> availableProviders) {
        this.serverUUID = serverUUID;
        this.availableProviders = availableProviders;
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
        this.serverUUID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
        int count = buf.readInt();
        this.availableProviders = new HashSet<String>(count);
        for (int i = 0; i < count; i++) {
            this.availableProviders.add(ByteBufUtils.readUTF8String(buf));
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, this.serverUUID.toString());
        buf.writeInt(this.availableProviders.size());
        for (String availableProvider : this.availableProviders) {
            ByteBufUtils.writeUTF8String(buf, availableProvider);
        }
    }

    @Override
    public void handle(MessageContext ctx, EntityPlayer player) {
        SyncHandlerClient.serverUUIDReceived(this.serverUUID, this.availableProviders);
    }
}
