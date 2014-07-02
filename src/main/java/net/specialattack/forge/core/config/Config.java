
package net.specialattack.forge.core.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.client.config.IConfigElement;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

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
        this.keys = new ArrayList<ConfigValue<?>>();

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
        this.keys.add(key);
        key.config = this;
    }

    /**
     * Loads the configuration
     */
    public void load() {
        for (ConfigValue<?> key : this.keys) {
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
        for (ConfigValue<?> key : this.keys) {
            if (key.isChanged()) {
                this.config.save();

                break;
            }
        }
    }

    public Configuration getConfig() {
        return config;
    }

    // TODO: Build the config elements properly
    @SuppressWarnings("rawtypes")
    public List<IConfigElement> getConfigElements() {
        return Arrays.asList((IConfigElement) new ConfigElement(config.getCategory(Configuration.CATEGORY_GENERAL)));
    }

}
