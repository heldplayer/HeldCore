
package me.heldplayer.util.HeldCore.sync.packet;

public class PacketHandler extends me.heldplayer.util.HeldCore.packet.PacketHandler {

    public static PacketHandler instance;

    public PacketHandler() {
        super("HeldCore");
        this.registerPacket(1, Packet1TrackingStatus.class);
        instance = this;
    }

}
