
package me.heldplayer.util.HeldCore.config;

import java.io.File;
import java.util.ArrayList;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;

import net.minecraftforge.common.Configuration;

/**
 * Class used for configurations
 * 
 * @author heldplayer
 * 
 */
public class Config {

    protected ArrayList<ConfigValue<?>> keys;
    protected Configuration config;

    /**
     * Creates a new Config instance from given file
     * 
     * @param file
     *        The configuration file, usually from
     *        {@link FMLPreInitializationEvent#getSuggestedConfigurationFile()}
     */
    public Config(File file) {
        keys = new ArrayList<ConfigValue<?>>();

        this.config = new Configuration(file);
    }

    /**
     * Adds a new config value to the configuration. Must be called before
     * {@link #load()}
     * 
     * @param key
     *        The key to add
     */
    public void addConfigKey(ConfigValue<?> key) {
        keys.add(key);
        key.config = this;
    }

    /**
     * Loads the configuration
     */
    public void load() {
        for (ConfigValue<?> key : keys) {
            key.load();
        }
    }

    /**
     * Saves the configuration, does not take in account if the entries have
     * changed or not
     */
    public void save() {
        this.config.save();
    }

    /**
     * Saves the configuration if any key has been changed
     */
    public void saveOnChange() {
        for (ConfigValue<?> key : keys) {
            if (key.isChanged()) {
                this.config.save();

                break;
            }
        }
    }

}
