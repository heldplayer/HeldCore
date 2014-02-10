
package me.heldplayer.util.HeldCore.sync.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

import me.heldplayer.util.HeldCore.HeldCore;
import me.heldplayer.util.HeldCore.event.SyncEvent;
import me.heldplayer.util.HeldCore.packet.HeldCorePacket;
import me.heldplayer.util.HeldCore.sync.ISyncableObjectOwner;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.relauncher.Side;

public class Packet4InitiateClientTracking extends HeldCorePacket {

    public boolean isWordly;
    public String identifier;
    public int posX;
    public int posY;
    public int posZ;

    public Packet4InitiateClientTracking() {
        super(null);
    }

    public Packet4InitiateClientTracking(ISyncableObjectOwner object) {
        super(object != null ? object.getWorld() : null);

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
        return Side.SERVER;
    }

    @Override
    public void read(ChannelHandlerContext context, ByteBuf in) throws IOException {
        this.isWordly = in.readBoolean();

        if (this.isWordly) {
            this.posX = in.readInt();
            this.posY = in.readInt();
            this.posZ = in.readInt();
        }
        else {
            byte[] data = new byte[in.readInt()];
            in.readBytes(data);
            this.identifier = new String(data);
        }
    }

    @Override
    public void write(ChannelHandlerContext context, ByteBuf out) throws IOException {
        out.writeBoolean(this.isWordly);

        if (this.isWordly) {
            out.writeInt(this.posX);
            out.writeInt(this.posY);
            out.writeInt(this.posZ);
        }
        else {
            byte[] data = this.identifier.getBytes();
            out.writeInt(data.length);
            out.writeBytes(data);
        }
    }

    @Override
    public void onData(ChannelHandlerContext context, EntityPlayer player) {
        if (this.isWordly) {
            TileEntity tile = player.worldObj.getTileEntity(this.posX, this.posY, this.posZ);
            if (tile != null) {
                if (tile instanceof ISyncableObjectOwner) {
                    HeldCore.packetHandler.sendPacketToServer(new Packet1TrackingStatus((ISyncableObjectOwner) tile, true));
                }
            }
        }
        else {
            SyncEvent.RequestObject event = new SyncEvent.RequestObject(this.identifier);
            MinecraftForge.EVENT_BUS.post(event);

            if (event.result != null) {
                HeldCore.packetHandler.sendPacketToServer(new Packet1TrackingStatus(event.result, true));
            }
        }
    }

}
