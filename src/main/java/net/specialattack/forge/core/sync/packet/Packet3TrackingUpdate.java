
package net.specialattack.forge.core.sync.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.specialattack.forge.core.Objects;
import net.specialattack.forge.core.packet.SpACorePacket;
import net.specialattack.forge.core.sync.ISyncable;
import net.specialattack.forge.core.sync.SyncHandler;

import org.apache.logging.log4j.Level;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import cpw.mods.fml.relauncher.Side;

public class Packet3TrackingUpdate extends SpACorePacket {

    public ISyncable[] syncables;
    public byte[] data;

    public Packet3TrackingUpdate() {
        super(null);
    }

    public Packet3TrackingUpdate(ISyncable[] syncables) {
        super(null);

        this.syncables = syncables;

        if (this.syncables != null) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream(32640);
            DataOutputStream dos = new DataOutputStream(bos);

            try {
                dos.writeInt(this.syncables.length);
                for (int i = 0; i < this.syncables.length; i++) {
                    dos.writeInt(this.syncables[i].getId());
                    this.syncables[i].write(dos);
                }
            }
            catch (IOException e) {
                Objects.log.log(Level.WARN, "Failed synchronizing object", e);
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
    public void read(ChannelHandlerContext context, ByteBuf in) throws IOException {
        this.data = new byte[in.readInt()];
        in.readBytes(this.data);
    }

    @Override
    public void write(ChannelHandlerContext context, ByteBuf out) throws IOException {
        out.writeInt(this.data.length);
        out.writeBytes(this.data);
    }

    @Override
    public void onData(ChannelHandlerContext context, EntityPlayer player) {
        ByteArrayDataInput dat = ByteStreams.newDataInput(this.data);

        try {
            this.syncables = new ISyncable[dat.readInt()];
            for (int i = 0; i < this.syncables.length; i++) {
                int id = dat.readInt();
                for (ISyncable syncable : SyncHandler.clientSyncables) {
                    if (syncable.getId() == id) {
                        this.syncables[i] = syncable;
                        syncable.read(dat);
                        syncable.getOwner().onDataChanged(syncable);
                    }
                }
            }
        }
        catch (IOException e) {
            Objects.log.log(Level.WARN, "Failed synchronizing object", e);
        }
    }

}
