
package me.heldplayer.util.HeldCore.sync;

import java.util.LinkedList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.MemoryConnection;
import net.minecraft.network.TcpConnection;

public class PlayerTracker {

    public LinkedList<ISyncable> syncables;
    public LinkedList<ISyncableObjectOwner> syncableOwners;
    public INetworkManager manager;
    public int ticks;
    public int interval;

    public PlayerTracker(INetworkManager manager, int interval) {
        this.manager = manager;
        this.syncables = new LinkedList<ISyncable>();
        this.syncableOwners = new LinkedList<ISyncableObjectOwner>();
        this.interval = interval;
    }

    public EntityPlayerMP getPlayer() {
        if (this.manager instanceof TcpConnection) {
            TcpConnection tcpConnection = (TcpConnection) this.manager;
            EntityPlayer player = tcpConnection.theNetHandler.getPlayer();

            if (player instanceof EntityPlayerMP) {
                return (EntityPlayerMP) player;
            }
        }
        else if (this.manager instanceof MemoryConnection) {
            MemoryConnection memoryConnection = (MemoryConnection) this.manager;
            EntityPlayer player = memoryConnection.myNetHandler.getPlayer();

            if (player instanceof EntityPlayerMP) {
                return (EntityPlayerMP) player;
            }
        }

        return null;
    }

}
