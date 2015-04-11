package net.specialattack.forge.core.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.ConfigGuiType;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.GuiEditArrayEntries;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.relauncher.Side;

public class ConfigCategory<T> implements IConfigElement {

    protected String name;
    protected String unlocalizedName;
    protected Config<?> config;
    protected ConfigCategory<?> parent;
    protected ArrayList<ConfigValue<?>> keys;
    protected ArrayList<ConfigCategory<?>> children;
    protected Side side;
    protected boolean requiresWorldRestart;
    protected boolean requiresMcRestart;
    protected boolean showInGui = true;

    public ConfigCategory(String name, String unlocalizedName, Side side) {
        this.name = name;
        this.unlocalizedName = unlocalizedName;
        this.side = side;
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

    public void sort() {
        Collections.sort(this.children, new Comparator<ConfigCategory<?>>() {
            @Override
            public int compare(ConfigCategory<?> o1, ConfigCategory<?> o2) {
                return o1.getLanguageKey().compareTo(o2.getLanguageKey());
            }
        });
        for (ConfigCategory<?> category : this.children) {
            category.sort();
        }
        Collections.sort(this.keys, new Comparator<ConfigValue<?>>() {
            @Override
            public int compare(ConfigValue<?> o1, ConfigValue<?> o2) {
                return o1.getLanguageKey().compareTo(o2.getLanguageKey());
            }
        });
    }

    @Override
    public boolean isProperty() {
        return false;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Class<? extends GuiConfigEntries.IConfigEntry> getConfigEntryClass() {
        return null;
    }

    @Override
    public Class<? extends GuiEditArrayEntries.IArrayEntry> getArrayEntryClass() {
        return null;
    }

    @Override
    public String getName() {
        return this.name;
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
        return this.unlocalizedName;
    }

    @Override
    public String getComment() {
        return I18n.format(this.unlocalizedName + ".comment");
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List<IConfigElement> getChildElements() {
        ArrayList<IConfigElement> result = new ArrayList<IConfigElement>();

        for (ConfigCategory<?> category : this.children) {
            if (category.side == Side.SERVER) {
                continue;
            }
            result.add(category);
        }

        for (ConfigValue<?> key : this.keys) {
            if (key.side == Side.SERVER) {
                continue;
            }
            result.add(key);
        }

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
    public void setToDefault() {
    }

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
    public void set(Object value) {
    }

    @Override
    public void set(Object[] aVal) {
    }

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
