
package me.heldplayer.util.HeldCore.event;

import cpw.mods.fml.common.eventhandler.Event;
import me.heldplayer.util.HeldCore.sync.ISyncableObjectOwner;

public class SyncEvent extends Event {

    public static class RequestObject extends SyncEvent {

        public final String identifier;
        public ISyncableObjectOwner result;

        public RequestObject(String identifier) {
            this.identifier = identifier;
        }

    }

}
