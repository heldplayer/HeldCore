
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

public class PacketHandler {

    private final FMLEmbeddedChannel clientOutboundChannel;
    private final FMLEmbeddedChannel serverOutboundChannel;

    public PacketHandler(String channelName, Class<? extends HeldCorePacket>... handledPacketClasses) {
        EnumMap<Side, FMLEmbeddedChannel> channelPair = NetworkRegistry.INSTANCE.newChannel(channelName, new ChannelHandler(handledPacketClasses));
        clientOutboundChannel = channelPair.get(Side.CLIENT);
        serverOutboundChannel = channelPair.get(Side.SERVER);
    }

    public void sendPacketToServer(HeldCorePacket packet) {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            clientOutboundChannel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.TOSERVER);
            clientOutboundChannel.writeOutbound(packet);
        }
    }

    public void sendPacketToPlayer(HeldCorePacket packet, EntityPlayer player) {
        if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
            serverOutboundChannel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.PLAYER);
            serverOutboundChannel.attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player);
            serverOutboundChannel.writeOutbound(packet);
        }
    }

    public void sendPacketToAllPlayers(HeldCorePacket packet) {
        if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
            serverOutboundChannel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALL);
            serverOutboundChannel.writeOutbound(packet);
        }
    }

    public void sendPacketToAllAroundPoint(HeldCorePacket packet, TargetPoint tp) {
        if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
            serverOutboundChannel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALLAROUNDPOINT);
            serverOutboundChannel.attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(tp);
            serverOutboundChannel.writeOutbound(packet);
        }
    }

    public void sendPacketToAllInDimension(HeldCorePacket packet, int dimension) {
        if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
            serverOutboundChannel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.DIMENSION);
            serverOutboundChannel.attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(dimension);
            serverOutboundChannel.writeOutbound(packet);
        }
    }

    private class ChannelHandler extends FMLIndexedMessageToMessageCodec<HeldCorePacket> {

        public ChannelHandler(Class<? extends HeldCorePacket>... handledPacketClasses) {
            for (int i = 0; i < handledPacketClasses.length; i++) {
                addDiscriminator(i, handledPacketClasses[i]);
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
