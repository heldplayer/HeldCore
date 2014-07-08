package net.specialattack.forge.core.packet;

import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import java.io.IOException;
import java.util.UUID;

public abstract class SpACorePacket {

    public final World world;
    // Can somebody PLEASE give me a way to get these without having to include them every time?
    public UUID sender;
    public Side senderSide;

    public SpACorePacket(World world) {
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
