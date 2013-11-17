
package me.heldplayer.util.HeldCore.sync.packet;

import java.io.DataOutputStream;
import java.io.IOException;

import me.heldplayer.util.HeldCore.packet.HeldCorePacket;
import me.heldplayer.util.HeldCore.sync.ISyncableObjectOwner;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.tileentity.TileEntity;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.relauncher.Side;

public class Packet4InitiateClientTracking extends HeldCorePacket {

    public int posX;
    public int posY;
    public int posZ;

    public Packet4InitiateClientTracking(int packetId) {
        super(packetId, null);
    }

    public Packet4InitiateClientTracking(int posX, int posY, int posZ) {
        super(4, null);

        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
    }

    @Override
    public Side getSendingSide() {
        return Side.SERVER;
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
        TileEntity tile = player.worldObj.getBlockTileEntity(this.posX, this.posY, this.posZ);
        if (tile != null) {
            if (tile instanceof ISyncableObjectOwner) {
                manager.addToSendQueue(PacketHandler.instance.createPacket(new Packet1TrackingStatus(this.posX, this.posY, this.posZ, true)));
            }
        }
    }

}
