
package me.heldplayer.util.HeldCore.packet;

import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.world.World;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.relauncher.Side;

public abstract class HeldCorePacket {

    public final int packetId;
    public final World world;

    public HeldCorePacket(int packetId, World world) {
        this.packetId = packetId;
        this.world = world;
    }

    public boolean isMapPacket() {
        return false;
    }

    public abstract Side getSendingSide();

    public abstract void read(ByteArrayDataInput in) throws IOException;

    public abstract void write(DataOutputStream out) throws IOException;

    public abstract void onData(INetworkManager manager, EntityPlayer player);

}
