package net.specialattack.forge.core.sync.packet;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.specialattack.forge.core.Objects;
import net.specialattack.forge.core.sync.ISyncable;
import net.specialattack.forge.core.sync.SyncHandler;
import org.apache.logging.log4j.Level;

public class Packet3TrackingUpdate extends SyncPacket {

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
                for (ISyncable syncable : this.syncables) {
                    dos.writeInt(syncable.getId());
                    syncable.write(dos);
                }
            } catch (IOException e) {
                Objects.log.log(Level.WARN, "Failed synchronizing object", e);
            }

            this.data = bos.toByteArray();
        } else {
            this.data = new byte[0];
        }
    }

    @Override
    public String getDebugInfo() {
        return String.format("PacketTrackingUpdate[Syncables: length=%s, data: %s bytes]", this.syncables == null ? "null" : this.syncables.length, this.data == null ? "null" : this.data.length);
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
    public void onData(ChannelHandlerContext context) {
        ByteArrayDataInput dat = ByteStreams.newDataInput(this.data);

        try {
            this.syncables = new ISyncable[dat.readInt()];
            for (int i = 0; i < this.syncables.length; i++) {
                int id = dat.readInt();
                ISyncable syncable = SyncHandler.Client.getSyncable(id);
                if (syncable != null) {
                    this.syncables[i] = syncable;
                    syncable.read(dat);
                    syncable.getOwner().onDataChanged(syncable);
                }
            }
        } catch (IOException e) {
            SyncHandler.Client.log.log(Level.WARN, "Failed synchronizing object", e);
        }
    }

}
