
package me.heldplayer.util.HeldCore.sync.packet;

import java.io.DataOutputStream;
import java.io.IOException;

import me.heldplayer.util.HeldCore.event.SyncEvent;
import me.heldplayer.util.HeldCore.packet.HeldCorePacket;
import me.heldplayer.util.HeldCore.sync.ISyncableObjectOwner;
import me.heldplayer.util.HeldCore.sync.SyncHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetworkManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.relauncher.Side;

public class Packet1TrackingStatus extends HeldCorePacket {

    public boolean isWordly;
    public String identifier;
    public int posX;
    public int posY;
    public int posZ;
    public boolean track;

    public Packet1TrackingStatus(int packetId) {
        super(packetId, null);
    }

    public Packet1TrackingStatus(ISyncableObjectOwner object, boolean track) {
        super(1, null);

        this.track = track;
        if (object.isWorldBound()) {
            this.isWordly = true;

            this.posX = object.getPosX();
            this.posY = object.getPosY();
            this.posZ = object.getPosZ();
        }
        else {
            this.isWordly = false;

            this.identifier = object.getIdentifier();
        }
    }

    @Override
    public Side getSendingSide() {
        return Side.CLIENT;
    }

    @Override
    public void read(ByteArrayDataInput in) throws IOException {
        this.isWordly = in.readBoolean();

        if (this.isWordly) {
            this.posX = in.readInt();
            this.posY = in.readInt();
            this.posZ = in.readInt();
        }
        else {
            byte[] data = new byte[in.readInt()];
            in.readFully(data);
            this.identifier = new String(data);
        }

        this.track = in.readBoolean();
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeBoolean(this.isWordly);

        if (this.isWordly) {
            out.writeInt(this.posX);
            out.writeInt(this.posY);
            out.writeInt(this.posZ);
        }
        else {
            byte[] data = this.identifier.getBytes();
            out.writeInt(data.length);
            out.write(data);
        }

        out.writeBoolean(this.track);
    }

    @Override
    public void onData(INetworkManager manager, EntityPlayer player) {
        if (!(player instanceof EntityPlayerMP)) {
            return;
        }

        if (this.isWordly) {
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
        else {
            SyncEvent.RequestObject event = new SyncEvent.RequestObject(this.identifier);
            MinecraftForge.EVENT_BUS.post(event);

            if (event.result != null) {
                if (this.track) {
                    SyncHandler.startTracking(event.result, (EntityPlayerMP) player);
                }
                else {
                    SyncHandler.stopTracking(event.result, (EntityPlayerMP) player);
                }
            }
        }
    }

}
