package net.specialattack.forge.core.sync;

import java.util.Set;
import java.util.UUID;

public final class SyncServerAPI {

    private SyncServerAPI() {
    }

    /**
     * Registers a provider to be used by the Sync system
     *
     * @param provider
     *         The provider to register
     *
     * @throws IllegalStateException
     *         If called after FMLInitializationEvent has been raised.
     */
    public static void registerProvider(SyncObjectProvider provider) {
        SyncHandler.registerProvider(provider);
    }

    /**
     * Returns a set of all available provider names of the Sync handler
     *
     * @return A Set containing all available provider names
     */
    public static Set<String> getAvailableProviderNames() {
        return SyncHandler.getAvailableProviderNames();
    }

    /**
     * Gets a provider based on the name
     *
     * @param name
     *         The name of the provider
     * @param <T>
     *         The type of the syncable owner the provider gives
     *
     * @return The provider of syncable owners, or null if it does not exist
     */
    public static <T extends ISyncableOwner> SyncObjectProvider<T> getProvider(String name) {
        return SyncHandler.getProvider(name);
    }

    /**
     * Returns the current connection info for a player, or creates a new one of none exist and create is set to true.
     *
     * @param uuid
     *         The UUID of the player
     * @param create
     *         Set to true if you want a new connection to be created if it doesn't exist.
     *         False if you don't want that behaviour.
     *
     * @return The connection info of a player, or null if none exists and create is false
     */
    public static ConnectionInfo getConnectionInfo(UUID uuid, boolean create) {
        return SyncHandler.getConnectionInfo(uuid, create);
    }

}
