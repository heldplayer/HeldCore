
package me.heldplayer.util.HeldCore.sync.packet;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Iterator;

import me.heldplayer.util.HeldCore.packet.HeldCorePacket;
import me.heldplayer.util.HeldCore.sync.ISyncable;
import me.heldplayer.util.HeldCore.sync.SyncHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.relauncher.Side;

public class Packet5TrackingEnd extends HeldCorePacket {

    public int id;

    public Packet5TrackingEnd(int packetId) {
        super(packetId, null);
    }

    public Packet5TrackingEnd(ISyncable syncable) {
        super(5, null);

        this.id = syncable.getId();
    }

    @Override
    public Side getSendingSide() {
        return Side.SERVER;
    }

    @Override
    public void read(ByteArrayDataInput in) throws IOException {
        this.id = in.readInt();
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeInt(this.id);
    }

    @Override
    public void onData(INetworkManager manager, EntityPlayer player) {
        Iterator<ISyncable> i = SyncHandler.clientSyncables.iterator();

        while (i.hasNext()) {
            ISyncable syncable = i.next();
            if (syncable.getId() == this.id) {
                i.remove();
            }
        }
    }
}
