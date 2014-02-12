
package net.specialattack.forge.core.event;

import net.specialattack.forge.core.sync.ISyncableObjectOwner;
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
