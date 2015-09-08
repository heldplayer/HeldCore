package net.specialattack.forge.core.config;

/**
 * Interface for configration entries that are not standard
 *
 * @author heldplayer
 */
public interface IConfigurable {

    /**
     * Returns the config value as a String to be written
     *
     * @return The config value, as a String
     */
    String serialize();

    /**
     * Loads the config value from a serialized String and returns a new
     * instance
     *
     * @param serialized
     *         The saved String
     *
     * @return A new instance of the config value
     */
    IConfigurable load(String serialized);

    String[] getValidValues();

}
