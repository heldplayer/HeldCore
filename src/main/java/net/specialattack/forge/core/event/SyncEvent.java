package net.specialattack.forge.core.event;

import java.util.Set;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.specialattack.forge.core.sync.ConnectionInfo;
import net.specialattack.forge.core.sync.SyncTrackingStorage;
import net.specialattack.forge.core.sync.SyncWorldTrackingStorage;

public abstract class SyncEvent extends Event {

    /**
     * Fired at the server when a client has connected.
     */
    public static class ServerClientConnected extends SyncEvent {

        public final EntityPlayer player;

        public ServerClientConnected(EntityPlayer player) {
            this.player = player;
        }
    }

    /**
     * Fired at the server when a client has disconnected.
     */
    public static class ServerClientDisconnected extends SyncEvent {

        public final EntityPlayer player;
        public final ConnectionInfo connection;

        public ServerClientDisconnected(EntityPlayer player, ConnectionInfo connection) {
            this.player = player;
            this.connection = connection;
        }
    }

    /**
     * Fired at the server when a client has disconnected.
     */
    public static class ServerConnectionSetup extends SyncEvent {

        public final ConnectionInfo connection;

        public ServerConnectionSetup(ConnectionInfo connection) {
            this.connection = connection;
        }
    }

    /**
     * Fired at the client when it connects to a server.
     * Sync data and possibilities have not been negotiated yet when this is fired.
     */
    public static class ClientConnected extends SyncEvent {

        public final INetHandlerPlayClient handler;

        public ClientConnected(INetHandlerPlayClient handler) {
            this.handler = handler;
        }
    }

    /**
     * Fired at the client when it is disconnected from a server.
     */
    public static class ClientDisconnected extends SyncEvent {

        public final INetHandlerPlayClient handler;

        public ClientDisconnected(INetHandlerPlayClient handler) {
            this.handler = handler;
        }
    }

    /**
     * Fired when the client receives server information.
     */
    public static class ClientServerInfoReceived extends SyncEvent {

        public final UUID serverUUID;
        public final Set<String> availableProviders;

        public ClientServerInfoReceived(UUID serverUUID, Set<String> availableProviders) {
            this.serverUUID = serverUUID;
            this.availableProviders = availableProviders;
        }
    }

    public static class ServerStarted extends SyncEvent {

        public final SyncTrackingStorage globalStorage;

        public ServerStarted(SyncTrackingStorage globalStorage) {
            this.globalStorage = globalStorage;
        }
    }

    public static class ServerStopped extends SyncEvent {

        public final SyncTrackingStorage globalStorage;

        public ServerStopped(SyncTrackingStorage globalStorage) {
            this.globalStorage = globalStorage;
        }
    }

    public static class ClientWorldTracked extends SyncEvent {

        public final SyncWorldTrackingStorage storage;

        public ClientWorldTracked(SyncWorldTrackingStorage storage) {
            this.storage = storage;
        }
    }

    public static class ClientWorldUntracked extends SyncEvent {

        public final SyncWorldTrackingStorage storage;

        public ClientWorldUntracked(SyncWorldTrackingStorage storage) {
            this.storage = storage;
        }
    }

    public static class ServerWorldTracked extends SyncEvent {

        public final SyncWorldTrackingStorage storage;

        public ServerWorldTracked(SyncWorldTrackingStorage storage) {
            this.storage = storage;
        }
    }

    public static class ServerWorldUntracked extends SyncEvent {

        public final SyncWorldTrackingStorage storage;

        public ServerWorldUntracked(SyncWorldTrackingStorage storage) {
            this.storage = storage;
        }
    }
}
