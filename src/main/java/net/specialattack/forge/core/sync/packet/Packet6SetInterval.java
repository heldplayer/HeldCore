package net.specialattack.forge.core.sync.packet;

import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.specialattack.forge.core.packet.SpACorePacket;
import net.specialattack.forge.core.sync.PlayerTracker;
import net.specialattack.forge.core.sync.SyncHandler;

import java.io.IOException;
import java.util.Iterator;

public class Packet6SetInterval extends SpACorePacket {

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

    @Override
    public void onData(ChannelHandlerContext context, EntityPlayer player) {
        Iterator<PlayerTracker> i = SyncHandler.players.iterator();

        while (i.hasNext()) {
            PlayerTracker tracker = i.next();

            EntityPlayerMP playerMP = tracker.getPlayer();

            if (playerMP == player) {
                tracker.interval = this.interval > tracker.interval ? this.interval : tracker.interval;
            }
        }
    }

}
