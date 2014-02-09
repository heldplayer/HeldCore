
package me.heldplayer.util.HeldCore.sync.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

import me.heldplayer.util.HeldCore.packet.HeldCorePacket;
import me.heldplayer.util.HeldCore.sync.ISyncable;
import cpw.mods.fml.relauncher.Side;

public class Packet5TrackingEnd extends HeldCorePacket {

    public int id;

    public Packet5TrackingEnd() {
        super(null);
    }

    public Packet5TrackingEnd(ISyncable syncable) {
        super(null);

        this.id = syncable.getId();
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

    // FIXME
    //    @Override
    //    public void onData(INetworkManager manager, EntityPlayer player) {
    //        Iterator<ISyncable> i = SyncHandler.clientSyncables.iterator();
    //
    //        while (i.hasNext()) {
    //            ISyncable syncable = i.next();
    //            if (syncable.getId() == this.id) {
    //                i.remove();
    //            }
    //        }
    //    }

}
