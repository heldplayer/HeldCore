package net.specialattack.forge.core.sync.packet;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.specialattack.forge.core.Objects;
import net.specialattack.forge.core.event.SyncEvent;
import net.specialattack.forge.core.sync.ISyncableObjectOwner;
import net.specialattack.forge.core.sync.SyncHandler;
import org.apache.logging.log4j.Level;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet2TrackingBegin extends SyncPacket {

    public boolean isWordly;
    public String identifier;
    public int posX;
    public int posY;
    public int posZ;
    public byte[] data;

    public Packet2TrackingBegin() {
        super(null);
    }

    public Packet2TrackingBegin(ISyncableObjectOwner object) {
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

        ByteArrayOutputStream bos = new ByteArrayOutputStream(32640);
        DataOutputStream dos = new DataOutputStream(bos);

        try {
            object.writeSetup(dos);
        } catch (IOException e) {
            Objects.log.log(Level.WARN, "Failed synchronizing object", e);
        }

        this.data = bos.toByteArray();
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

        this.data = new byte[in.readInt()];
        in.readBytes(this.data);
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

        out.writeInt(this.data.length);
        out.writeBytes(this.data);
    }

    @Override
    public void onData(ChannelHandlerContext context, EntityPlayer player) {
        if (this.isWordly) {
            TileEntity tile = player.worldObj.getTileEntity(this.posX, this.posY, this.posZ);
            if (tile != null) {
                if (tile instanceof ISyncableObjectOwner) {
                    ISyncableObjectOwner object = (ISyncableObjectOwner) tile;
                    try {
                        ByteArrayDataInput dat = ByteStreams.newDataInput(this.data);

                        object.readSetup(dat);

                        SyncHandler.clientSyncables.addAll(object.getSyncables());
                    } catch (IOException e) {
                        Objects.log.log(Level.WARN, "Failed synchronizing object", e);
                    }
                }
            }
        } else {
            SyncEvent.RequestObject event = new SyncEvent.RequestObject(this.identifier);
            MinecraftForge.EVENT_BUS.post(event);

            if (event.result != null) {
                ISyncableObjectOwner object = event.result;

                try {
                    ByteArrayDataInput dat = ByteStreams.newDataInput(this.data);

                    object.readSetup(dat);

                    SyncHandler.clientSyncables.addAll(object.getSyncables());
                } catch (IOException e) {
                    Objects.log.log(Level.WARN, "Failed synchronizing object", e);
                }
            }
        }
    }

}
