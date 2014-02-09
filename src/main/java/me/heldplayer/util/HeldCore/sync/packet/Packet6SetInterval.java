
package me.heldplayer.util.HeldCore.sync.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

import me.heldplayer.util.HeldCore.packet.HeldCorePacket;
import cpw.mods.fml.relauncher.Side;

public class Packet6SetInterval extends HeldCorePacket {

    public int interval;

    public Packet6SetInterval() {
        super(null);
    }

    public Packet6SetInterval(Integer interval) {
        super(null);

        this.interval = interval.intValue();
    }

    @Override
    public Side getSendingSide() {
        return Side.CLIENT;
    }

    @Override
    public void read(ChannelHandlerContext context, ByteBuf in) throws IOException {
        this.interval = in.readInt();
    }

    @Override
    public void write(ChannelHandlerContext context, ByteBuf out) throws IOException {
        out.writeInt(this.interval);
    }

    // FIXME
    //    @Override
    //    public void onData(INetworkManager manager, EntityPlayer player) {
    //        Iterator<PlayerTracker> i = SyncHandler.players.iterator();
    //
    //        while (i.hasNext()) {
    //            PlayerTracker tracker = i.next();
    //
    //            if (tracker.manager == manager) {
    //                tracker.interval = this.interval > tracker.interval ? this.interval : tracker.interval;
    //            }
    //        }
    //    }

}
