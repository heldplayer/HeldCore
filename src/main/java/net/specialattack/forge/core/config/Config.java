
package net.specialattack.forge.core.config;

import java.io.File;
import java.util.List;

import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.client.config.IConfigElement;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

/**
 * Class used for configurations
 * 
 * @author heldplayer
 * 
 */
public class Config<T> extends ConfigCategory<T> {

    protected Configuration config;

    /**
     * Creates a new Config instance from given file
     * 
     * @param file
     *        The configuration file, usually from
     *        {@link FMLPreInitializationEvent#getSuggestedConfigurationFile()}
     */
    public Config(File file) {
        super("", "root", "");

        this.config = new Configuration(file);
        super.config = this;
    }

    /**
     * Loads the configuration
     */
    @Override
    public void load() {
        for (ConfigCategory<?> category : this.children) {
            category.load();
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
        for (ConfigCategory<?> category : this.children) {
            if (category.isChanged()) {
                this.config.save();

                break;
            }
        }
    }

    public Configuration getConfig() {
        return config;
    }

    @Override
    public void addValue(ConfigValue<?> value) {}

    @SuppressWarnings("rawtypes")
    public List<IConfigElement> getConfigElements() {
        return this.getChildElements();
    }

}
