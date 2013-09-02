
package me.heldplayer.util.HeldCore.sync.packet;

public class PacketHandler extends me.heldplayer.util.HeldCore.packet.PacketHandler {

    public static PacketHandler instance;

    public PacketHandler() {
        super("HeldCore");
        this.registerPacket(1, Packet1TrackingStatus.class);
        this.registerPacket(2, Packet2TrackingBegin.class);
        this.registerPacket(3, Packet3TrackingUpdate.class);
        this.registerPacket(4, Packet4InitiateClientTracking.class);
        instance = this;
    }

}
