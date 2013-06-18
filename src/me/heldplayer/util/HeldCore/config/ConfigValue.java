
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
        if (this.side != FMLCommonHandler.instance().getSide() && this.side != null) {
            return;
        }

        if (this.mode == 0) {
            this.value = this.config.config.get(this.category, this.name, ((IConfigurable) this.deff).serialize(), this.comment);
        }
        if (this.mode == 1) {
            this.value = this.config.config.get(this.category, this.name, (Boolean) this.deff, this.comment);
        }
        if (this.mode == 2) {
            this.value = this.config.config.get(this.category, this.name, (Double) this.deff, this.comment);
        }
        if (this.mode == 3) {
            if (this.category.equalsIgnoreCase(Configuration.CATEGORY_BLOCK)) {
                this.value = this.config.config.getBlock(this.category, this.name, (Integer) this.deff, this.comment);
            }
            else if (this.category.equalsIgnoreCase(Configuration.CATEGORY_ITEM)) {
                this.value = this.config.config.getItem(this.category, this.name, (Integer) this.deff, this.comment);
            }
            else {
                this.value = this.config.config.get(this.category, this.name, (Integer) this.deff, this.comment);
            }
        }
        if (this.mode == 4) {
            this.value = this.config.config.get(this.category, this.name, (String) this.deff, this.comment);
        }
    }

    /**
     * Returns the value this config entry is set to
     * 
     * @return The set value
     */
    @SuppressWarnings("unchecked")
    public T getValue() {
        if (this.side != FMLCommonHandler.instance().getSide() && this.side != null) {
            return null;
        }

        if (this.value == null) {
            return this.deff;
        }

        if (this.mode == 0) {
            return (T) ((IConfigurable) this.deff).load(this.value.getString());
        }
        if (this.mode == 1) {
            return (T) Boolean.valueOf(this.value.getBoolean((Boolean) this.deff));
        }
        if (this.mode == 2) {
            return (T) Double.valueOf(this.value.getDouble((Double) this.deff));
        }
        if (this.mode == 3) {
            return (T) Integer.valueOf(this.value.getInt((Integer) this.deff));
        }
        if (this.mode == 4) {
            return (T) this.value.getString();
        }

        return this.deff;
    }

    public boolean isChanged() {
        if (this.side != FMLCommonHandler.instance().getSide() && this.side != null) {
            return false;
        }

        return this.value != null ? this.value.hasChanged() : false;
    }

}
