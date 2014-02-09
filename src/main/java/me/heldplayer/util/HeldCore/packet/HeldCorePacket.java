
package me.heldplayer.util.HeldCore.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;

public abstract class HeldCorePacket {

    public final World world;

    public HeldCorePacket(World world) {
        this.world = world;
    }

    public boolean isMapPacket() {
        return false;
    }

    public abstract Side getSendingSide();

    public abstract void read(ChannelHandlerContext context, ByteBuf in) throws IOException;

    public abstract void write(ChannelHandlerContext context, ByteBuf out) throws IOException;

    // FIXME? public abstract void onData(INetworkManager manager, EntityPlayer player);

}
