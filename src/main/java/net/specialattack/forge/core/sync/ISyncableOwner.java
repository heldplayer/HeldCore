package net.specialattack.forge.core.sync;

import java.util.Map;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayerMP;

public interface ISyncableOwner {

    /**
     * Gets a map containing all the syncables for this object,
     * with the key being the name of te syncable in this object.
     *
     * @return A name->syncable map of all the syncables for this object.
     */
    Map<String, ISyncable> getSyncables();

    /**
     * Called on the server when this syncable gets registered.
     * Can be used to notify the server of additional syncable objects being added.
     *
     * @param tracker
     *         The storage this syncable was added to, can be world specific or global.
     *         If null means that the storage is being cleaned up.
     */
    void register(SyncTrackingStorage tracker);

    /**
     * Checks to see if a player has permissions to.
     *
     * @param player
     *         The player to check permissions on.
     *
     * @return True of the player has permissions to track this object, false otherwise.
     */
    boolean canPlayerTrack(EntityPlayerMP player);

    /**
     * Used to get the provider of this syncable.
     *
     * @return The provider of this syncable, must not be null.
     */
    SyncObjectProvider getProvider();

    /**
     * Used to get the UUID of the syncable.
     *
     * The UUID should be generated on the server, the client UUID is set by the system using {@link ISyncableOwner#setSyncUUID(UUID) setSyncUUID}
     *
     * The UUID does not have to be persistent between sessions, but it must be persistent during a session.
     *
     * @return The UUID on the server, can be null on the client
     */
    UUID getSyncUUID();

    /**
     * Used by the syncing system to set the UUID on the client
     *
     * @param uuid
     *         The UUID to set
     */
    void setSyncUUID(UUID uuid);

    /**
     * Used to see if a syncable owner can start tracking on the client, if it can't then the request to start tracking gets delayed.
     *
     * @return True if the syncable owner can start tracking on the client, false otherwise
     */
    boolean canStartTracking();

    /**
     * Used for debugging purposes.
     *
     * @return A string representing this Syncable Owner
     */
    String getDebugDisplay();

    /**
     * Used to notify an owner on the client that one of its syncables has changed values.
     * This method can be called in rapid succession if multiple syncables change at the same time.
     *
     * @param syncable
     *         The syncable that has been changed
     */
    void syncableChanged(ISyncable syncable);

}
