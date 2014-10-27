package net.specialattack.forge.core.sync.packet;

import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import java.io.IOException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.specialattack.forge.core.SpACore;
import net.specialattack.forge.core.event.SyncEvent;
import net.specialattack.forge.core.sync.ISyncableObjectOwner;
import net.specialattack.forge.core.sync.SyncHandler;
import org.apache.logging.log4j.Level;

public class Packet7TrackingStop extends SyncPacket {

    public boolean isWordly;
    public String identifier;
    public int posX;
    public int posY;
    public int posZ;

    public Packet7TrackingStop() {
        super(null);
    }

    public Packet7TrackingStop(ISyncableObjectOwner object) {
        super(object != null ? object.getWorld() : null);

        if (object == null) {
            throw new NullPointerException("Object mustn't be null");
        }

        if (object.isWorldBound()) {
            this.isWordly = true;

            this.posX = object.getPosX();
            this.posY = object.getPosY();
            this.posZ = object.getPosZ();
        } else {
            this.isWordly = false;

            this.identifier = object.getIdentifier();
        }
    }

    @Override
    public String getDebugInfo() {
        return String.format("PacketTrackingStop[isWorldly: %s, %s]", this.isWordly, this.isWordly ? String.format("x: %s, y: %s, z: %s", this.posX, this.posY, this.posZ) : String.format("Identifier: %s", this.identifier));
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
        } else {
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
        } else {
            byte[] data = this.identifier.getBytes();
            out.writeInt(data.length);
            out.writeBytes(data);
        }
    }

    @Override
    public void onData(ChannelHandlerContext context) {
        EntityPlayer player = SpACore.proxy.getClientPlayer();

        if (this.isWordly) {
            if (player.worldObj != null) {
                TileEntity tile = player.worldObj.getTileEntity(this.posX, this.posY, this.posZ);
                if (tile != null) {
                    if (tile instanceof ISyncableObjectOwner) {
                        ISyncableObjectOwner object = (ISyncableObjectOwner) tile;

                        SyncHandler.Client.log.log(Level.INFO, String.format("Marking %s as not valid", object));
                        object.setNotValid();
                    }
                }
            }
        } else {
            SyncEvent.RequestObject event = new SyncEvent.RequestObject(this.identifier);
            MinecraftForge.EVENT_BUS.post(event);

            if (event.result != null) {
                ISyncableObjectOwner object = event.result;

                SyncHandler.Client.log.log(Level.INFO, String.format("Marking %s as not valid", object));
                object.setNotValid();
            }
        }
    }

}
