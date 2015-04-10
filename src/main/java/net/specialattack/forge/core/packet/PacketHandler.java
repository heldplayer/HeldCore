package net.specialattack.forge.core.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import java.util.EnumMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.FMLEmbeddedChannel;
import net.minecraftforge.fml.common.network.FMLIndexedMessageToMessageCodec;
import net.minecraftforge.fml.common.network.FMLOutboundHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.specialattack.forge.core.SpACore;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// Thanks AtomicStryker for your example classes!
// https://code.google.com/p/atomicstrykers-minecraft-mods/source/browse/Minions/src/main/java/atomicstryker/minions/common/network/NetworkHelper.java
// https://code.google.com/p/atomicstrykers-minecraft-mods/source/browse/Minions/src/main/java/atomicstryker/minions/common/network/PacketDispatcher.java
public class PacketHandler<P extends SpACorePacket> {

    private final FMLEmbeddedChannel clientOutboundChannel;
    private final FMLEmbeddedChannel serverOutboundChannel;
    private final String channelName;

    public static final boolean debug = Boolean.parseBoolean(System.getProperty("spacore.packet.debug", "false"));
    public static final Logger log = LogManager.getLogger("SpACore:Pckt");

    public PacketHandler(String channelName, Class<? extends P>... handledPacketClasses) {
        EnumMap<Side, FMLEmbeddedChannel> channelPair = NetworkRegistry.INSTANCE.newChannel(channelName, new ChannelHandler(handledPacketClasses), new MessageHandler());
        this.clientOutboundChannel = channelPair.get(Side.CLIENT);
        this.serverOutboundChannel = channelPair.get(Side.SERVER);
        this.channelName = channelName;
    }

    private <T> void setAttr(Side side, AttributeKey<T> attribute, T value) {
        if (side == Side.CLIENT) {
            this.clientOutboundChannel.attr(attribute).set(value);
        } else if (side == Side.SERVER) {
            this.serverOutboundChannel.attr(attribute).set(value);
        } else {
            throw new IllegalStateException("Setting attribute on unknwon side!");
        }
    }

    private void write(Side side, P packet) {
        if (side == Side.CLIENT) {
            Verification.verifyPacket(this.clientOutboundChannel, packet, side);
            this.clientOutboundChannel.writeOutbound(packet);
        } else if (side == Side.SERVER) {
            Verification.verifyPacket(this.serverOutboundChannel, packet, side);
            this.serverOutboundChannel.writeOutbound(packet);
        } else {
            throw new IllegalStateException("Writing from unknown side!");
        }
    }

    public void sendPacketToServer(P packet) {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            setAttr(Side.CLIENT, FMLOutboundHandler.FML_MESSAGETARGET, FMLOutboundHandler.OutboundTarget.TOSERVER);
            packet.attr(Attributes.SENDING_PLAYER).set(SpACore.proxy.getClientPlayer());
            write(Side.CLIENT, packet);
        } else {
            throw new IllegalStateException("Calling client only code on the server side!");
        }
    }

    public void sendPacketToPlayer(P packet, EntityPlayer player) {
        if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
            setAttr(Side.SERVER, FMLOutboundHandler.FML_MESSAGETARGET, FMLOutboundHandler.OutboundTarget.PLAYER);
            setAttr(Side.SERVER, FMLOutboundHandler.FML_MESSAGETARGETARGS, player);
            packet.attr(Attributes.TARGET_PLAYER).set(player.getUniqueID());
            write(Side.SERVER, packet);
        } else {
            throw new IllegalStateException("Calling server only code on the client side!");
        }
    }

    public void sendPacketToAllPlayers(P packet) {
        if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
            setAttr(Side.SERVER, FMLOutboundHandler.FML_MESSAGETARGET, FMLOutboundHandler.OutboundTarget.ALL);
            write(Side.SERVER, packet);
        } else {
            throw new IllegalStateException("Calling server only code on the client side!");
        }
    }

    public void sendPacketToAllAroundPoint(P packet, NetworkRegistry.TargetPoint tp) {
        if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
            setAttr(Side.SERVER, FMLOutboundHandler.FML_MESSAGETARGET, FMLOutboundHandler.OutboundTarget.ALLAROUNDPOINT);
            setAttr(Side.SERVER, FMLOutboundHandler.FML_MESSAGETARGETARGS, tp);
            write(Side.SERVER, packet);
        } else {
            throw new IllegalStateException("Calling server only code on the client side!");
        }
    }

    public void sendPacketToAllInDimension(P packet, int dimension) {
        if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
            setAttr(Side.SERVER, FMLOutboundHandler.FML_MESSAGETARGET, FMLOutboundHandler.OutboundTarget.DIMENSION);
            setAttr(Side.SERVER, FMLOutboundHandler.FML_MESSAGETARGETARGS, dimension);
            write(Side.SERVER, packet);
        } else {
            throw new IllegalStateException("Calling server only code on the client side!");
        }
    }

    public <T> void prepareServerOutbound(AttributeKey<T> attributeKey, T arg) {
        if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
            setAttr(Side.SERVER, attributeKey, arg);
        } else {
            throw new IllegalStateException("Calling server only code on the client side!");
        }
    }

    public <T> void prepareClientOutbound(AttributeKey<T> attributeKey, T arg) {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            setAttr(Side.CLIENT, attributeKey, arg);
        } else {
            throw new IllegalStateException("Calling client only code on the server side!");
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
            try {
                if (debug) {
                    if (packet.getSendingSide() == Side.SERVER && packet.attr(Attributes.TARGET_PLAYER).get() != null) {
                        log.log(Level.INFO, String.format("%s %s send > %s @ %s", packet.getSendingSide(), channelName, packet.getDebugInfo(), packet.attr(Attributes.TARGET_PLAYER).get()));
                    } else {
                        log.log(Level.INFO, String.format("%s %s send > %s", packet.getSendingSide(), channelName, packet.getDebugInfo()));
                    }
                }
                // Writing the attributes
                Attributes.writeAttributes(packet, out);
                // Writing the packet
                packet.write(context, out);
            } catch (Exception e) {
                throw new IllegalStateException("Failed writing packet", e);
            }
        }

        @Override
        public void decodeInto(ChannelHandlerContext context, ByteBuf in, P packet) {
            try {
                // Reading the attributes
                Attributes.readAttributes(packet, in);
                // Reading the packet
                packet.read(context, in);
                if (debug) {
                    log.log(Level.INFO, String.format("%s %s recv < %s", packet.getSendingSide() == Side.CLIENT ? Side.SERVER : Side.CLIENT, channelName, packet.getDebugInfo()));
                }
            } catch (Exception e) {
                throw new IllegalStateException("Failed reading packet", e);
            }
        }
    }

    @Sharable
    private class MessageHandler extends SimpleChannelInboundHandler<P> {

        @Override
        protected void channelRead0(ChannelHandlerContext context, P packet) {
            try {
                packet.onData(context);
            } catch (Exception e) {
                log.warn("Failed handling packet", e);
            }
        }

    }

}
