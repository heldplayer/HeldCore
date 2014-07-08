package net.specialattack.forge.core.event;

import cpw.mods.fml.common.eventhandler.Event;
import net.specialattack.forge.core.sync.ISyncableObjectOwner;
import net.specialattack.forge.core.sync.PlayerTracker;
import net.specialattack.forge.core.sync.SyncHandler;

public abstract class SyncEvent extends Event {

    /**
     * Called when the synchronization code tries to find an
     * {@link ISyncableObjectOwner} that isn't bound to a world, based on its
     * identifier.
     */
    public static class RequestObject extends SyncEvent {

        public final String identifier;
        public ISyncableObjectOwner result;

        public RequestObject(String identifier) {
            this.identifier = identifier;
        }

    }

    private abstract static class Tracking extends SyncEvent {

        public final PlayerTracker tracker;

        public Tracking(PlayerTracker tracker) {
            this.tracker = tracker;
        }

    }

    /**
     * Called when the {@link SyncHandler} starts tracking a player.
     * Called on the server.
     */
    public static class StartTracking extends Tracking {

        public StartTracking(PlayerTracker tracker) {
            super(tracker);
        }

    }

    /**
     * Called when the {@link SyncHandler} stops tracking a player.
     * Called on the server.
     */
    public static class StopTracking extends Tracking {

        public StopTracking(PlayerTracker tracker) {
            super(tracker);
        }

    }

    /**
     * Called when the client is ready to start sending packets of custom
     * channels.
     * Called on the client.
     */
    public static class ClientStartSyncing extends SyncEvent {

    }

}
