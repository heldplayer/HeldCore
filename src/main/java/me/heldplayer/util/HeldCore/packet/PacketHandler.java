
package me.heldplayer.util.HeldCore.packet;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.List;
import java.util.TreeMap;
import java.util.logging.Level;

import me.heldplayer.util.HeldCore.Objects;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerInstance;
import net.minecraft.server.management.PlayerManager;
import net.minecraft.world.WorldServer;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;

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
            Objects.log.log(Level.WARNING, "[Networking] Failed instantiating packet", e);
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
                Objects.log.log(Level.WARNING, "[Networking] Failed reading packet", e);
            }

            packet.onData(manager, (EntityPlayer) player);
        }
    }

    public Packet250CustomPayload createPacket(HeldCorePacket packet) {
        if (packet == null) {
            return null;
        }
        if (packet.world != null && packet.getSendingSide() == Side.CLIENT && !packet.world.isRemote) {
            return null;
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream(32767);
        DataOutputStream dos = new DataOutputStream(bos);

        try {
            dos.writeByte(packet.packetId);

            packet.write(dos);
        }
        catch (Exception e) {
            Objects.log.log(Level.WARNING, "[Networking] Failed writing packet", e);
        }

        Packet250CustomPayload payload = new Packet250CustomPayload();

        payload.channel = this.channel;
        payload.data = bos.toByteArray();
        payload.length = payload.data.length;
        payload.isChunkDataPacket = packet.isMapPacket();

        return payload;
    }

    public static void sendPacketToPlayersWatching(Packet packet, int dimensionId, int chunkX, int chunkZ) {
        if (packet == null) {
            return;
        }
        MinecraftServer server = MinecraftServer.getServer();

        if (server != null) {
            for (WorldServer world : server.worldServers) {
                if (world.provider.dimensionId == dimensionId) {
                    PlayerManager manager = world.getPlayerManager();
                    PlayerInstance instance = manager.getOrCreateChunkWatcher(chunkX, chunkZ, false);

                    if (instance != null) {
                        instance.sendToAllPlayersWatchingChunk(packet);
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static void sendPacketToPlayersInDim(Packet packet, int dimensionId) {
        if (packet == null) {
            return;
        }
        MinecraftServer server = MinecraftServer.getServer();

        if (server != null) {
            List<EntityPlayerMP> players = server.getConfigurationManager().playerEntityList;

            for (EntityPlayerMP player : players) {
                if (player.dimension == dimensionId) {
                    player.playerNetServerHandler.sendPacketToPlayer(packet);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static void sendPacketToAllPlayers(Packet packet) {
        if (packet == null) {
            return;
        }
        MinecraftServer server = MinecraftServer.getServer();

        if (server != null) {
            List<EntityPlayerMP> players = server.getConfigurationManager().playerEntityList;

            for (EntityPlayerMP player : players) {
                player.playerNetServerHandler.sendPacketToPlayer(packet);
            }
        }
    }

}
