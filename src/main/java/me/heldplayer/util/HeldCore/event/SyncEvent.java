
package me.heldplayer.util.HeldCore.event;

import me.heldplayer.util.HeldCore.sync.ISyncableObjectOwner;
import cpw.mods.fml.common.eventhandler.Event;

public class SyncEvent extends Event {

    public static class RequestObject extends SyncEvent {

        public final String identifier;
        public ISyncableObjectOwner result;

        public RequestObject(String identifier) {
            this.identifier = identifier;
        }

    }

}
