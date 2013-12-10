
package me.heldplayer.util.HeldCore.sync.packet;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Iterator;

import me.heldplayer.util.HeldCore.packet.HeldCorePacket;
import me.heldplayer.util.HeldCore.sync.PlayerTracker;
import me.heldplayer.util.HeldCore.sync.SyncHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.relauncher.Side;

public class Packet6SetInterval extends HeldCorePacket {

    public int interval;

    public Packet6SetInterval(int packetId) {
        super(packetId, null);
    }

    public Packet6SetInterval(Integer interval) {
        super(6, null);

        this.interval = interval.intValue();
    }

    @Override
    public Side getSendingSide() {
        return Side.CLIENT;
    }

    @Override
    public void read(ByteArrayDataInput in) throws IOException {
        this.interval = in.readInt();
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeInt(this.interval);
    }

    @Override
    public void onData(INetworkManager manager, EntityPlayer player) {
        Iterator<PlayerTracker> i = SyncHandler.players.iterator();

        while (i.hasNext()) {
            PlayerTracker tracker = i.next();

            if (tracker.manager == manager) {
                tracker.interval = this.interval > tracker.interval ? this.interval : tracker.interval;
            }
        }
    }
}
