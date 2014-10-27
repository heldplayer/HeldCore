package net.specialattack.forge.core.sync.packet;

import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import java.io.IOException;
import net.specialattack.forge.core.sync.ISyncable;
import net.specialattack.forge.core.sync.SyncHandler;

public class Packet5TrackingEnd extends SyncPacket {

    public int id;

    public Packet5TrackingEnd() {
        super(null);
    }

    public Packet5TrackingEnd(ISyncable syncable) {
        super(null);

        this.id = syncable.getId();
    }

    @Override
    public String getDebugInfo() {
        return String.format("PacketTrackingEnd[Id: %s]", this.id);
    }

    @Override
    public Side getSendingSide() {
        return Side.SERVER;
    }

    @Override
    public void read(ChannelHandlerContext context, ByteBuf in) throws IOException {
        this.id = in.readInt();
    }

    @Override
    public void write(ChannelHandlerContext context, ByteBuf out) throws IOException {
        out.writeInt(this.id);
    }

    @Override
    public void onData(ChannelHandlerContext context) {
        SyncHandler.Client.removeSyncable(id);
    }

}
