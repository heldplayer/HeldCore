
package me.heldplayer.util.HeldCore;

public class PacketHandler extends me.heldplayer.util.HeldCore.packet.PacketHandler {

    public static PacketHandler instance;

    public PacketHandler() {
        super("HeldCore");
        this.registerPacket(1, Packet1RequestTracking.class);
        instance = this;
    }

}
