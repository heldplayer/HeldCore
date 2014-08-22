package net.specialattack.forge.core.sync.packet;

import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.specialattack.forge.core.sync.PlayerTracker;
import net.specialattack.forge.core.sync.SyncHandler;

import java.io.IOException;

public class Packet6SetInterval extends SyncPacket {

    public int interval;

    public Packet6SetInterval() {
        super(null);
    }

    public Packet6SetInterval(Integer interval) {
        super(null);

        this.interval = interval;
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
        for (PlayerTracker tracker : SyncHandler.players) {
            EntityPlayerMP playerMP = tracker.getPlayer();

            if (playerMP == player) {
                tracker.interval = this.interval > tracker.interval ? this.interval : tracker.interval;
            }
        }
    }

}
