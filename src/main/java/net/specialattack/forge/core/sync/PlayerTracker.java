package net.specialattack.forge.core.sync;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;

import java.util.LinkedList;

public class PlayerTracker {

    public LinkedList<ISyncable> syncables;
    public LinkedList<ISyncableObjectOwner> syncableOwners;
    public INetHandler handler;
    public int ticks;
    public int interval;

    public PlayerTracker(INetHandler handler, int interval) {
        this.handler = handler;
        this.syncables = new LinkedList<ISyncable>();
        this.syncableOwners = new LinkedList<ISyncableObjectOwner>();
        this.interval = interval;
    }

    @Override
    public String toString() {
        EntityPlayerMP player = this.getPlayer();
        return "Player Tracker " + (player != null ? player.getCommandSenderName() : null);
    }

    public EntityPlayerMP getPlayer() {
        if (this.handler instanceof NetHandlerPlayServer) {
            NetHandlerPlayServer netHandlerPlayServer = (NetHandlerPlayServer) this.handler;
            return netHandlerPlayServer.playerEntity;
        }
        return null;
    }

}
