
package me.heldplayer.util.HeldCore;

import java.io.DataOutputStream;
import java.io.IOException;

import me.heldplayer.util.HeldCore.packet.HeldCorePacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.relauncher.Side;

public class Packet1RequestTracking extends HeldCorePacket {

    public int posX;
    public int posY;
    public int posZ;

    public Packet1RequestTracking(int packetId) {
        super(packetId, null);
    }

    public Packet1RequestTracking(int posX, int posY, int posZ) {
        super(1, null);

        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
    }

    @Override
    public Side getSendingSide() {
        return Side.CLIENT;
    }

    @Override
    public void read(ByteArrayDataInput in) throws IOException {
        this.posX = in.readInt();
        this.posY = in.readInt();
        this.posZ = in.readInt();
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeInt(this.posX);
        out.writeInt(this.posY);
        out.writeInt(this.posZ);
    }

    @Override
    public void onData(INetworkManager manager, EntityPlayer player) {

    }

}
