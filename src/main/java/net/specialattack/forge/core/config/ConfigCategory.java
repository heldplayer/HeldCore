
package net.specialattack.forge.core.config;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.client.config.ConfigGuiType;
import cpw.mods.fml.client.config.GuiConfigEntries.IConfigEntry;
import cpw.mods.fml.client.config.GuiEditArrayEntries.IArrayEntry;
import cpw.mods.fml.client.config.IConfigElement;

public class ConfigCategory<T> implements IConfigElement<T> {

    protected String name;
    protected String displayName;
    protected Config<?> config;
    protected ConfigCategory<?> parent;
    protected ArrayList<ConfigValue<?>> keys;
    protected ArrayList<ConfigCategory<?>> children;
    protected String comment;
    protected boolean requiresWorldRestart;
    protected boolean requiresMcRestart;
    protected boolean showInGui = true;

    public ConfigCategory(String name, String displayName, String comment) {
        this.name = name;
        this.displayName = displayName;
        this.comment = comment;
        this.keys = new ArrayList<ConfigValue<?>>();
        this.children = new ArrayList<ConfigCategory<?>>();
    }

    public void addValue(ConfigValue<?> value) {
        this.keys.add(value);
        value.category = this;
    }

    public void addCategory(ConfigCategory<?> category) {
        this.children.add(category);
        category.parent = this;
        category.config = this.config;
    }

    public void load() {
        for (ConfigValue<?> key : this.keys) {
            key.load();
        }

        for (ConfigCategory<?> category : this.children) {
            category.load();
        }
    }

    public boolean isChanged() {
        for (ConfigValue<?> key : this.keys) {
            if (key.isChanged()) {
                return true;
            }
        }

        for (ConfigCategory<?> category : this.children) {
            if (category.isChanged()) {
                return true;
            }
        }

        return false;
    }

    public void setRequiresMcRestart(boolean requiresMcRestart) {
        this.requiresMcRestart = requiresMcRestart;
    }

    public void setRequiresWorldRestart(boolean requiresWorldRestart) {
        this.requiresWorldRestart = requiresWorldRestart;
    }

    public void setShowInGui(boolean showInGui) {
        this.showInGui = showInGui;
    }

    @Override
    public boolean isProperty() {
        return false;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Class<? extends IConfigEntry> getConfigEntryClass() {
        return null;
    }

    @Override
    public Class<? extends IArrayEntry> getArrayEntryClass() {
        return null;
    }

    @Override
    public String getName() {
        return this.displayName;
    }

    @Override
    public String getQualifiedName() {
        if (this.parent == this.config || this.parent == null) {
            return this.name;
        }
        return parent.getQualifiedName() + Configuration.CATEGORY_SPLITTER + name;
    }

    @Override
    public String getLanguageKey() {
        return this.getQualifiedName();
    }

    @Override
    public String getComment() {
        return this.comment;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List<IConfigElement> getChildElements() {
        ArrayList<IConfigElement> result = new ArrayList<IConfigElement>();
        result.addAll(this.children);
        result.addAll(this.keys);
        return result;
    }

    @Override
    public ConfigGuiType getType() {
        return ConfigGuiType.CONFIG_CATEGORY;
    }

    @Override
    public boolean isList() {
        return false;
    }

    @Override
    public boolean isListLengthFixed() {
        return false;
    }

    @Override
    public int getMaxListLength() {
        return -1;
    }

    @Override
    public boolean isDefault() {
        return true;
    }

    @Override
    public Object getDefault() {
        return null;
    }

    @Override
    public Object[] getDefaults() {
        return null;
    }

    @Override
    public void setToDefault() {}

    @Override
    public boolean requiresWorldRestart() {
        return this.requiresWorldRestart;
    }

    @Override
    public boolean showInGui() {
        return this.showInGui;
    }

    @Override
    public boolean requiresMcRestart() {
        return this.requiresMcRestart;
    }

    @Override
    public Object get() {
        return null;
    }

    @Override
    public Object[] getList() {
        return null;
    }

    @Override
    public void set(T value) {}

    @Override
    public void set(T[] aVal) {}

    @Override
    public String[] getValidValues() {
        return null;
    }

    @Override
    public T getMinValue() {
        return null;
    }

    @Override
    public T getMaxValue() {
        return null;
    }

    @Override
    public Pattern getValidationPattern() {
        return null;
    }

}
