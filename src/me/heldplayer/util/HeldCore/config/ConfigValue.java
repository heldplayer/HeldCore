
package me.heldplayer.util.HeldCore.config;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

/**
 * Class used for storing a configuration entry
 * 
 * @author heldplayer
 * 
 * @param <T>
 *        The type of value that will be stored in this config
 */
public class ConfigValue<T> {

    protected String category;
    protected String name;
    protected T deff;
    protected String comment;
    protected Property value;
    protected Config config;
    protected Side side;

    private int mode;

    public ConfigValue(String name, String category, Side side, T deff, String comment) {
        this.name = name;
        this.category = category;
        this.side = side;
        this.deff = deff;
        this.comment = comment;

        this.mode = -1;

        if (deff instanceof IConfigurable) {
            this.mode = 0;
        }
        if (deff.getClass() == Boolean.class) {
            this.mode = 1;
        }
        if (deff.getClass() == Double.class) {
            this.mode = 2;
        }
        if (deff.getClass() == Integer.class) {
            this.mode = 3;
        }
        if (deff.getClass() == String.class) {
            this.mode = 4;
        }

        if (this.mode == -1) {
            throw new IncompatibleClassChangeError();
        }
    }

    public void load() {
        if (side != FMLCommonHandler.instance().getSide() && side != null) {
            return;
        }

        if (mode == 0) {
            value = config.config.get(category, name, ((IConfigurable) deff).serialize(), comment);
        }
        if (mode == 1) {
            value = config.config.get(category, name, (Boolean) deff, comment);
        }
        if (mode == 2) {
            value = config.config.get(category, name, (Double) deff, comment);
        }
        if (mode == 3) {
            if (this.category.equalsIgnoreCase(Configuration.CATEGORY_BLOCK)) {
                value = config.config.getBlock(category, name, (Integer) deff, comment);
            }
            else if (this.category.equalsIgnoreCase(Configuration.CATEGORY_ITEM)) {
                value = config.config.getItem(category, name, (Integer) deff, comment);
            }
            else {
                value = config.config.get(category, name, (Integer) deff, comment);
            }
        }
        if (mode == 4) {
            value = config.config.get(category, name, (String) deff, comment);
        }
    }

    /**
     * Returns the value this config entry is set to
     * 
     * @return The set value
     */
    @SuppressWarnings("unchecked")
    public T getValue() {
        if (side != FMLCommonHandler.instance().getSide() && side != null) {
            return null;
        }

        if (this.value == null) {
            return deff;
        }

        if (mode == 0) {
            return (T) ((IConfigurable) this.deff).load(this.value.getString());
        }
        if (mode == 1) {
            return (T) Boolean.valueOf(this.value.getBoolean((Boolean) deff));
        }
        if (mode == 2) {
            return (T) Double.valueOf(this.value.getDouble((Double) deff));
        }
        if (mode == 3) {
            return (T) Integer.valueOf(this.value.getInt((Integer) deff));
        }
        if (mode == 4) {
            return (T) this.value.getString();
        }

        return deff;
    }

    public boolean isChanged() {
        if (side != FMLCommonHandler.instance().getSide() && side != null) {
            return false;
        }

        return this.value != null ? this.value.hasChanged() : false;
    }

}
