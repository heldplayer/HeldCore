
package me.heldplayer.util.HeldCore.packet;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.TreeMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public abstract class PacketHandler implements IPacketHandler {

    public final String channel;
    public final TreeMap<Integer, Class<? extends HeldCorePacket>> packetTypes;

    public PacketHandler(String channel) {
        this.channel = channel;
        this.packetTypes = new TreeMap<Integer, Class<? extends HeldCorePacket>>();
    }

    public void registerPacket(int packetId, Class<? extends HeldCorePacket> clazz) {
        if (this.packetTypes.containsKey(packetId)) {
            throw new RuntimeException("Can't register a packet with this packet ID as it already exists");
        }
        this.packetTypes.put(packetId, clazz);
    }

    public HeldCorePacket instantiatePacket(int packetId) {
        if (!this.packetTypes.containsKey(packetId)) {
            throw new RuntimeException("Unknown packet ID");
        }
        Class<? extends HeldCorePacket> clazz = this.packetTypes.get(packetId);
        try {
            HeldCorePacket packet = clazz.getConstructor(int.class).newInstance(packetId);
            return packet;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void onPacketData(INetworkManager manager, Packet250CustomPayload payload, Player player) {
        ByteArrayDataInput dat = ByteStreams.newDataInput(payload.data);

        int type = dat.readUnsignedByte();

        HeldCorePacket packet = this.instantiatePacket(type);

        if (packet != null) {
            try {
                packet.read(dat);
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            packet.onData(manager, (EntityPlayer) player);
        }
    }

    public Packet250CustomPayload createPacket(HeldCorePacket packet) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(32767);
        DataOutputStream dos = new DataOutputStream(bos);

        try {
            dos.writeByte(packet.packetId);

            packet.write(dos);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        Packet250CustomPayload payload = new Packet250CustomPayload();

        payload.channel = this.channel;
        payload.data = bos.toByteArray();
        payload.length = payload.data.length;
        payload.isChunkDataPacket = packet.isMapPacket();

        return payload;
    }

}
