
package me.heldplayer.util.HeldCore.sync;

import java.util.LinkedList;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.MemoryConnection;
import net.minecraft.network.TcpConnection;

public class PlayerTracker {

    public LinkedList<ISyncable> syncables = new LinkedList<ISyncable>();
    public final INetworkManager manager;

    public PlayerTracker(INetworkManager manager) {
        this.manager = manager;
    }

    public EntityPlayerMP getPlayer() {
        if (manager instanceof TcpConnection) {
            TcpConnection tcpConnection = (TcpConnection) manager;
        }
        else if (manager instanceof MemoryConnection) {
            MemoryConnection memoryConnection = (MemoryConnection) manager;
        }

        return null;
    }

}
