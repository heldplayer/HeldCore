
package me.heldplayer.util.HeldCore.sync.packet;

import java.io.DataOutputStream;
import java.io.IOException;

import me.heldplayer.util.HeldCore.packet.HeldCorePacket;
import me.heldplayer.util.HeldCore.sync.ISyncableObjectOwner;
import me.heldplayer.util.HeldCore.sync.SyncHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetworkManager;
import net.minecraft.tileentity.TileEntity;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.relauncher.Side;

public class Packet1TrackingStatus extends HeldCorePacket {

    public int posX;
    public int posY;
    public int posZ;
    public boolean track;

    public Packet1TrackingStatus(int packetId) {
        super(packetId, null);
    }

    public Packet1TrackingStatus(int posX, int posY, int posZ, boolean track) {
        super(1, null);

        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.track = track;
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
        this.track = in.readBoolean();
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeInt(this.posX);
        out.writeInt(this.posY);
        out.writeInt(this.posZ);
        out.writeBoolean(this.track);
    }

    @Override
    public void onData(INetworkManager manager, EntityPlayer player) {
        if (!(player instanceof EntityPlayerMP)) {
            return;
        }

        TileEntity tile = player.worldObj.getBlockTileEntity(this.posX, this.posY, this.posZ);
        if (tile != null) {
            if (tile instanceof ISyncableObjectOwner) {
                if (this.track) {
                    SyncHandler.startTracking((ISyncableObjectOwner) tile, (EntityPlayerMP) player);
                }
                else {
                    SyncHandler.stopTracking((ISyncableObjectOwner) tile, (EntityPlayerMP) player);
                }
            }
        }
    }

}
