package net.specialattack.forge.core.sync.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import java.io.IOException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.specialattack.forge.core.event.SyncEvent;
import net.specialattack.forge.core.packet.Attributes;
import net.specialattack.forge.core.sync.ISyncableObjectOwner;
import net.specialattack.forge.core.sync.PlayerTracker;
import net.specialattack.forge.core.sync.SyncHandler;

public class Packet1TrackingStatus extends SyncPacket {

    public boolean isWordly;
    public String identifier;
    public int posX;
    public int posY;
    public int posZ;
    public boolean track;

    public Packet1TrackingStatus() {
        super(null);
    }

    public Packet1TrackingStatus(ISyncableObjectOwner object, boolean track) {
        super(object != null && object.isWorldBound() ? object.getWorld() : null);
        if (object == null) {
            throw new IllegalArgumentException("object");
        }

        this.track = track;
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
        return String.format("PacketTrackingStatus[isWorldly: %s, %s]", this.isWordly, this.isWordly ? String.format("x: %s, y: %s, z: %s", this.posX, this.posY, this.posZ) : String.format("Identifier: %s", this.identifier));
    }

    @Override
    public Side getSendingSide() {
        return Side.CLIENT;
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

        this.track = in.readBoolean();
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

        out.writeBoolean(this.track);
    }

    @Override
    public void onData(ChannelHandlerContext context) {
        this.requireAttribute(Attributes.SENDING_PLAYER);

        EntityPlayer player = this.attr(Attributes.SENDING_PLAYER).get();

        PlayerTracker tracker = SyncHandler.Server.getTracker(player);
        if (tracker == null) {
            SyncHandler.Server.startTracking((EntityPlayerMP) player);
        }

        if (this.isWordly) {
            if (player.worldObj != null) {
                TileEntity tile = player.worldObj.getTileEntity(new BlockPos(this.posX, this.posY, this.posZ));
                if (tile != null) {
                    if (tile instanceof ISyncableObjectOwner) {
                        if (this.track) {
                            SyncHandler.Server.startTracking((ISyncableObjectOwner) tile, (EntityPlayerMP) player);
                        } else {
                            SyncHandler.Server.stopTracking((ISyncableObjectOwner) tile, (EntityPlayerMP) player);
                        }
                    }
                }
            }
        } else {
            SyncEvent.RequestObject event = new SyncEvent.RequestObject(this.identifier);
            MinecraftForge.EVENT_BUS.post(event);

            if (event.result != null) {
                if (this.track) {
                    SyncHandler.Server.startTracking(event.result, (EntityPlayerMP) player);
                } else {
                    SyncHandler.Server.stopTracking(event.result, (EntityPlayerMP) player);
                }
            }
        }
    }

}
