
package me.heldplayer.util.HeldCore.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;

public abstract class HeldCorePacket {

    public final World world;
    // Can somebody PLEASE give me a way to get these without having to include them every time?
    public String senderName;
    public Side senderSide;

    public HeldCorePacket(World world) {
        this.world = world;
    }

    public boolean isMapPacket() {
        return false;
    }

    public abstract Side getSendingSide();

    public abstract void read(ChannelHandlerContext context, ByteBuf in) throws IOException;

    public abstract void write(ChannelHandlerContext context, ByteBuf out) throws IOException;

    public abstract void onData(ChannelHandlerContext context, EntityPlayer player);

}
