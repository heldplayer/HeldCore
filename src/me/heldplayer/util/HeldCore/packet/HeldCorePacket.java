
package me.heldplayer.util.HeldCore.packet;

import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;

import com.google.common.io.ByteArrayDataInput;

public abstract class HeldCorePacket {

    public final int packetId;

    public HeldCorePacket(int packetId) {
        this.packetId = packetId;
    }

    public boolean isMapPacket() {
        return false;
    }

    public abstract void read(ByteArrayDataInput in) throws IOException;

    public abstract void write(DataOutputStream out) throws IOException;

    public abstract void onData(INetworkManager manager, EntityPlayer player);

}
