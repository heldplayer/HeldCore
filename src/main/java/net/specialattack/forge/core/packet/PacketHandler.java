package net.specialattack.forge.core.packet;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.FMLIndexedMessageToMessageCodec;
import cpw.mods.fml.common.network.FMLOutboundHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.util.EnumMap;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.specialattack.forge.core.Objects;
import net.specialattack.forge.core.SpACore;

// Thanks AtomicStryker for your example classes!
// https://code.google.com/p/atomicstrykers-minecraft-mods/source/browse/Minions/src/main/java/atomicstryker/minions/common/network/NetworkHelper.java
// https://code.google.com/p/atomicstrykers-minecraft-mods/source/browse/Minions/src/main/java/atomicstryker/minions/common/network/PacketDispatcher.java
public class PacketHandler<P extends SpACorePacket> {

    private final FMLEmbeddedChannel clientOutboundChannel;
    private final FMLEmbeddedChannel serverOutboundChannel;

    public PacketHandler(String channelName, Class<? extends P>... handledPacketClasses) {
        EnumMap<Side, FMLEmbeddedChannel> channelPair = NetworkRegistry.INSTANCE.newChannel(channelName, new ChannelHandler(handledPacketClasses), new ClickMessageHandler());
        this.clientOutboundChannel = channelPair.get(Side.CLIENT);
        this.serverOutboundChannel = channelPair.get(Side.SERVER);
    }

    public void sendPacketToServer(P packet) {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            this.clientOutboundChannel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.TOSERVER);
            EntityPlayer player = SpACore.proxy.getClientPlayer();
            if (player != null) {
                packet.sender = player.getUniqueID();
            }
            packet.senderSide = Side.CLIENT;
            this.clientOutboundChannel.writeOutbound(packet);
        }
    }

    public void sendPacketToPlayer(P packet, EntityPlayer player) {
        if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
            this.serverOutboundChannel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.PLAYER);
            this.serverOutboundChannel.attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player);
            packet.senderSide = Side.SERVER;
            this.serverOutboundChannel.writeOutbound(packet);
        }
    }

    public void sendPacketToAllPlayers(P packet) {
        if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
            this.serverOutboundChannel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALL);
            packet.senderSide = Side.SERVER;
            this.serverOutboundChannel.writeOutbound(packet);
        }
    }

    public void sendPacketToAllAroundPoint(P packet, NetworkRegistry.TargetPoint tp) {
        if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
            this.serverOutboundChannel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALLAROUNDPOINT);
            this.serverOutboundChannel.attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(tp);
            packet.senderSide = Side.SERVER;
            this.serverOutboundChannel.writeOutbound(packet);
        }
    }

    public void sendPacketToAllInDimension(P packet, int dimension) {
        if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
            this.serverOutboundChannel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.DIMENSION);
            this.serverOutboundChannel.attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(dimension);
            packet.senderSide = Side.SERVER;
            this.serverOutboundChannel.writeOutbound(packet);
        }
    }

    private class ChannelHandler extends FMLIndexedMessageToMessageCodec<P> {

        public ChannelHandler(Class<? extends P>... handledPacketClasses) {
            for (int i = 0; i < handledPacketClasses.length; i++) {
                this.addDiscriminator(i, handledPacketClasses[i]);
            }
        }

        @Override
        public void encodeInto(ChannelHandlerContext context, P packet, ByteBuf out) throws Exception {
            if (packet.sender != null) {
                byte[] bytes = packet.sender.toString().getBytes();
                out.writeInt(bytes.length);
                out.writeBytes(bytes);
            } else {
                out.writeInt(0);
            }
            if (packet.senderSide != null) {
                out.writeInt(packet.senderSide.ordinal());
            } else {
                Side side = packet.getSendingSide();
                if (side != null) {
                    out.writeInt(side.ordinal());
                } else {
                    out.writeInt(Side.SERVER.ordinal());
                }
            }
            packet.write(context, out);
        }

        @Override
        public void decodeInto(ChannelHandlerContext context, ByteBuf in, P packet) {
            try {
                byte[] bytes = new byte[in.readInt()];
                in.readBytes(bytes);
                String player = new String(bytes);
                Side side = Side.values()[in.readInt()];
                try {
                    packet.sender = UUID.fromString(player);
                } catch (IllegalArgumentException e) {
                }
                packet.senderSide = side;

                try {
                    packet.read(context, in);
                } catch (Exception e) {
                    throw new RuntimeException("Failed reading packet", e);
                }
            } catch (Throwable e) {
                Objects.log.warn("Failed reading packet", e);
            }
        }

    }

    @Sharable
    private class ClickMessageHandler extends SimpleChannelInboundHandler<P> {

        @Override
        protected void channelRead0(ChannelHandlerContext context, P packet) throws Exception {
            try {
                EntityPlayer player = null;
                if (packet.senderSide.isServer()) {
                    player = SpACore.proxy.getClientPlayer();
                } else if (packet.senderSide.isClient()) {
                    if (packet.sender != null) {
                        for (Object obj : MinecraftServer.getServer().getConfigurationManager().playerEntityList) {
                            if (obj instanceof EntityPlayer) {
                                if (((EntityPlayerMP) obj).getUniqueID().equals(packet.sender)) {
                                    player = (EntityPlayer) obj;
                                }
                            }
                        }
                    }
                }

                packet.onData(context, player);
            } catch (Throwable e) {
                Objects.log.warn("Failed handling packet", e);
            }
        }

    }

}
