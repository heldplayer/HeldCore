package net.specialattack.forge.core.packet;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetHandlerPlayServer;
import net.specialattack.forge.core.SpACore;
import org.apache.logging.log4j.Level;

public abstract class SpAPacket<P extends SpAPacket<P>> implements IMessage, IMessageHandler<P, IMessage> {

    public String getDebugInfo() {
        return this.toString();
    }

    @Override
    public final IMessage onMessage(P message, MessageContext context) {
        if (SpAPacketHandler.debug) {
            SpAPacketHandler.log.log(Level.INFO, String.format("[SpA/Packet] Received packet on side %s of type %s, %s", context.side, message.getClass().getName(), message.getDebugInfo()));
        }
        EntityPlayer player;
        if (context.side == Side.CLIENT) {
            player = SpACore.proxy.getClientPlayer();
        } else {
            player = ((NetHandlerPlayServer) context.netHandler).playerEntity;
        }
        message.handle(context, player);
        return null;
    }

    public abstract Side getSendingSide();

    public abstract Side getReceivingSide();

    public abstract void handle(MessageContext context, EntityPlayer player);

}
