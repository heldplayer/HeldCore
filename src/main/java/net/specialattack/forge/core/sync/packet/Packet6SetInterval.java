package net.specialattack.forge.core.sync.packet;

import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import java.io.IOException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.specialattack.forge.core.SpACore;
import net.specialattack.forge.core.packet.Attributes;
import net.specialattack.forge.core.sync.PlayerTracker;
import net.specialattack.forge.core.sync.SyncHandler;

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
    public String getDebugInfo() {
        return String.format("PacketSetInterval[Interval: %s]", this.interval);
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
    public void onData(ChannelHandlerContext context) {
        this.requireAttribute(Attributes.SENDING_PLAYER);

        EntityPlayer player = this.attr(Attributes.SENDING_PLAYER).get();

        PlayerTracker tracker = SyncHandler.Server.getTracker(player);
        if (tracker != null) {
            tracker.interval = Math.max(this.interval, SpACore.refreshRate.getValue());
        } else {
            SyncHandler.Server.startTracking((EntityPlayerMP) player);
            tracker = SyncHandler.Server.getTracker(player);
            if (tracker != null) {
                tracker.interval = Math.max(this.interval, SpACore.refreshRate.getValue());
            } else {
                throw new IllegalStateException("Failed starting to track player!");
            }
        }
    }

}
