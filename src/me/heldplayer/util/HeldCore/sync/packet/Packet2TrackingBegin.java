
package me.heldplayer.util.HeldCore.sync.packet;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.logging.Level;

import me.heldplayer.util.HeldCore.Objects;
import me.heldplayer.util.HeldCore.packet.HeldCorePacket;
import me.heldplayer.util.HeldCore.sync.ISyncableObjectOwner;
import me.heldplayer.util.HeldCore.sync.SyncHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.tileentity.TileEntity;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import cpw.mods.fml.relauncher.Side;

public class Packet2TrackingBegin extends HeldCorePacket {

    public int posX;
    public int posY;
    public int posZ;
    public ISyncableObjectOwner object;
    public byte[] data;

    public Packet2TrackingBegin(int packetId) {
        super(packetId, null);
    }

    public Packet2TrackingBegin(int posX, int posY, int posZ, ISyncableObjectOwner object) {
        super(2, null);

        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.object = object;

        if (this.object != null) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream(32640);
            DataOutputStream dos = new DataOutputStream(bos);

            try {
                this.object.writeSetup(dos);
            }
            catch (IOException e) {
                Objects.log.log(Level.WARNING, "Failed synchronizing object", e);
            }

            this.data = bos.toByteArray();
        }
        else {
            this.data = new byte[0];
        }
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

        this.data = new byte[in.readInt()];
        in.readFully(this.data);
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeInt(this.posX);
        out.writeInt(this.posY);
        out.writeInt(this.posZ);

        out.writeInt(this.data.length);
        out.write(this.data);
    }

    @Override
    public void onData(INetworkManager manager, EntityPlayer player) {
        TileEntity tile = player.worldObj.getBlockTileEntity(this.posX, this.posY, this.posZ);
        if (tile != null) {
            if (tile instanceof ISyncableObjectOwner) {
                this.object = (ISyncableObjectOwner) tile;
                try {
                    ByteArrayDataInput dat = ByteStreams.newDataInput(this.data);

                    this.object.readSetup(dat);

                    SyncHandler.clientSyncables.addAll(this.object.getSyncables());
                }
                catch (IOException e) {
                    Objects.log.log(Level.WARNING, "Failed synchronizing object", e);
                }
            }
        }
    }

}
