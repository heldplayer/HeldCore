
package me.heldplayer.util.HeldCore.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.EnumMap;

import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.FMLIndexedMessageToMessageCodec;
import cpw.mods.fml.common.network.FMLOutboundHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.relauncher.Side;

// Thanks AtomicStryker for your example classes!
// https://code.google.com/p/atomicstrykers-minecraft-mods/source/browse/Minions/src/main/java/atomicstryker/minions/common/network/NetworkHelper.java
// https://code.google.com/p/atomicstrykers-minecraft-mods/source/browse/Minions/src/main/java/atomicstryker/minions/common/network/PacketDispatcher.java
public class PacketHandler {

    private final FMLEmbeddedChannel clientOutboundChannel;
    private final FMLEmbeddedChannel serverOutboundChannel;

    public PacketHandler(String channelName, Class<? extends HeldCorePacket>... handledPacketClasses) {
        EnumMap<Side, FMLEmbeddedChannel> channelPair = NetworkRegistry.INSTANCE.newChannel(channelName, new ChannelHandler(handledPacketClasses));
        this.clientOutboundChannel = channelPair.get(Side.CLIENT);
        this.serverOutboundChannel = channelPair.get(Side.SERVER);
    }

    public void sendPacketToServer(HeldCorePacket packet) {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            this.clientOutboundChannel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.TOSERVER);
            this.clientOutboundChannel.writeOutbound(packet);
        }
    }

    public void sendPacketToPlayer(HeldCorePacket packet, EntityPlayer player) {
        if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
            this.serverOutboundChannel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.PLAYER);
            this.serverOutboundChannel.attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player);
            this.serverOutboundChannel.writeOutbound(packet);
        }
    }

    public void sendPacketToAllPlayers(HeldCorePacket packet) {
        if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
            this.serverOutboundChannel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALL);
            this.serverOutboundChannel.writeOutbound(packet);
        }
    }

    public void sendPacketToAllAroundPoint(HeldCorePacket packet, TargetPoint tp) {
        if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
            this.serverOutboundChannel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALLAROUNDPOINT);
            this.serverOutboundChannel.attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(tp);
            this.serverOutboundChannel.writeOutbound(packet);
        }
    }

    public void sendPacketToAllInDimension(HeldCorePacket packet, int dimension) {
        if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
            this.serverOutboundChannel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.DIMENSION);
            this.serverOutboundChannel.attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(dimension);
            this.serverOutboundChannel.writeOutbound(packet);
        }
    }

    private class ChannelHandler extends FMLIndexedMessageToMessageCodec<HeldCorePacket> {

        public ChannelHandler(Class<? extends HeldCorePacket>... handledPacketClasses) {
            for (int i = 0; i < handledPacketClasses.length; i++) {
                this.addDiscriminator(i, handledPacketClasses[i]);
            }
        }

        @Override
        public void encodeInto(ChannelHandlerContext context, HeldCorePacket packet, ByteBuf out) throws Exception {
            packet.write(context, out);
        }

        @Override
        public void decodeInto(ChannelHandlerContext context, ByteBuf in, HeldCorePacket packet) {
            try {
                packet.read(context, in);
            }
            catch (Exception e) {
                throw new RuntimeException("Failed reading packet", e);
            }
        }

    }

}
