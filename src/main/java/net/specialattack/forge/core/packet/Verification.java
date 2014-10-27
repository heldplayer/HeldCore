package net.specialattack.forge.core.packet;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import io.netty.util.AttributeMap;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class Verification {

    static final Set<PacketVerifier> packetVerifiers = new HashSet<PacketVerifier>();

    static {
        addVerifier(new PacketVerifier() {
            @Override
            void verifyPacket(AttributeMap context, SpACorePacket packet, Side side) {
                // Make sure we are sending the sendingSide data
                Side sendingSide = context.attr(NetworkRegistry.CHANNEL_SOURCE).get();
                if (sendingSide == null || sendingSide != side) {
                    throw new IllegalStateException(String.format("Tried sending packet from %s side, but the attributes specify it to be on the %s side", side, packet.getSendingSide()));
                }
            }
        });
        addVerifier(new PacketVerifier() {
            @Override
            void verifyPacket(AttributeMap context, SpACorePacket packet, Side side) {
                // Make sure the packet being sent is being sent from the right side
                Side sendingSide = context.attr(NetworkRegistry.CHANNEL_SOURCE).get();
                if (sendingSide != null) {
                    if (sendingSide != packet.getSendingSide()) {
                        throw new IllegalStateException(String.format("Tried sending packet from %s side, but packet should only be sent from %s side", sendingSide, packet.getSendingSide()));
                    }
                }
            }
        });
        addVerifier(new PacketVerifier() {
            @Override
            void verifyPacket(AttributeMap context, SpACorePacket packet, Side side) {
                // Make sure that when sending from the client side, we are sending the player as well, but not when we are on the server side
                EntityPlayer sendingPlayer = packet.attr(Attributes.SENDING_PLAYER).get();
                if (side == Side.CLIENT) {
                    if (sendingPlayer == null) {
                        throw new IllegalStateException(String.format("Tried sending packet from %s side, but no player info is being sent", side));
                    }
                    if (sendingPlayer instanceof EntityPlayerMP) {
                        throw new IllegalStateException(String.format("Tried sending a multiplayer player on %s side", side));
                    }
                } else if (sendingPlayer != null) {
                    throw new IllegalStateException(String.format("Adding a sending player is not allowed on the %s side", side));
                }
            }
        });
    }

    public static void addVerifier(PacketVerifier verifier) {
        packetVerifiers.add(verifier);
    }

    public static void verifyPacket(AttributeMap context, SpACorePacket packet, Side side) {
        for (PacketVerifier verifier : packetVerifiers) {
            verifier.verifyPacket(context, packet, side);
        }
    }

    public static abstract class PacketVerifier {

        abstract void verifyPacket(AttributeMap context, SpACorePacket packet, Side side);

    }

}
