package net.specialattack.forge.core.config;

import java.util.List;
import java.util.regex.Pattern;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.config.ConfigGuiType;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.GuiEditArrayEntries;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Class used for storing a configuration entry
 *
 * @param <T>
 *         The type of value that will be stored in this config
 *
 * @author heldplayer
 */
@SuppressWarnings("ConstantConditions")
public class ConfigValue<T> implements IConfigElement {

    protected ConfigCategory<?> category;
    protected String name;
    protected String unlocalizedName;
    protected T deff;
    protected Property value;
    protected Side side;
    protected boolean requiresWorldRestart;
    protected boolean requiresMcRestart;
    protected boolean showInGui = true;

    private int mode;

    public ConfigValue(String name, String unlocalizedName, Side side, T deff) {
        this.name = name;
        this.unlocalizedName = unlocalizedName;
        this.side = side;
        this.deff = deff;

        this.mode = -1;

        if (deff == null) {
            throw new IllegalArgumentException("Default cannot be null");
        }

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
        if (deff instanceof IConfigurable[]) {
            this.mode = 5;
        }
        if (deff instanceof boolean[]) {
            this.mode = 6;
        }
        if (deff instanceof double[]) {
            this.mode = 7;
        }
        if (deff instanceof int[]) {
            this.mode = 8;
        }
        if (deff instanceof String[]) {
            this.mode = 9;
        }

        if (this.mode == -1) {
            throw new IllegalStateException("Configuration type must be valid");
        }
    }

    public void load() {
        if (this.side != FMLCommonHandler.instance().getSide() && this.side != null) {
            return;
        }

        if (this.mode == 0) {
            this.value = this.category.config.config.get(this.category.name, this.name, ((IConfigurable) this.deff).serialize(), this.getComment());
        }
        if (this.mode == 1) {
            this.value = this.category.config.config.get(this.category.name, this.name, (Boolean) this.deff, this.getComment());
        }
        if (this.mode == 2) {
            this.value = this.category.config.config.get(this.category.name, this.name, (Double) this.deff, this.getComment());
        }
        if (this.mode == 3) {
            this.value = this.category.config.config.get(this.category.name, this.name, (Integer) this.deff, this.getComment());
        }
        if (this.mode == 4) {
            this.value = this.category.config.config.get(this.category.name, this.name, (String) this.deff, this.getComment());
        }
        if (this.mode == 5) {
            String[] values = new String[((IConfigurable[]) this.deff).length];
            for (int i = 0; i < values.length; i++) {
                values[i] = ((IConfigurable[]) this.deff)[i].serialize();
            }
            this.value = this.category.config.config.get(this.category.name, this.name, values, this.getComment());
        }
        if (this.mode == 6) {
            this.value = this.category.config.config.get(this.category.name, this.name, (boolean[]) this.deff, this.getComment());
        }
        if (this.mode == 7) {
            this.value = this.category.config.config.get(this.category.name, this.name, (double[]) this.deff, this.getComment());
        }
        if (this.mode == 8) {
            this.value = this.category.config.config.get(this.category.name, this.name, (int[]) this.deff, this.getComment());
        }
        if (this.mode == 9) {
            this.value = this.category.config.config.get(this.category.name, this.name, (String[]) this.deff, this.getComment());
        }
    }

    public boolean isChanged() {
        return !(this.side != FMLCommonHandler.instance().getSide() && this.side != null) && this.value != null && this.value.hasChanged();
    }

    public ConfigValue<T> setRequiresMcRestart(boolean requiresMcRestart) {
        this.requiresMcRestart = requiresMcRestart;
        return this;
    }

    public ConfigValue<T> setRequiresWorldRestart(boolean requiresWorldRestart) {
        this.requiresWorldRestart = requiresWorldRestart;
        return this;
    }

    public ConfigValue<T> setShowInGui(boolean showInGui) {
        this.showInGui = showInGui;
        return this;
    }

    @Override
    public boolean isProperty() {
        return true;
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
        return this.name;
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
        return null;
    }

    @Override
    public ConfigGuiType getType() {
        switch (this.mode) {
            case 1:
            case 6:
                return ConfigGuiType.BOOLEAN;
            case 2:
            case 7:
                return ConfigGuiType.DOUBLE;
            case 3:
            case 8:
                return ConfigGuiType.INTEGER;
            case 4:
            case 9:
            default:
                return ConfigGuiType.STRING;
        }
    }

    @Override
    public boolean isList() {
        return this.mode > 4 && this.mode < 10;
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
        return this.value.isDefault();
    }

    @Override
    public Object getDefault() {
        return this.deff;
    }

    @Override
    public Object[] getDefaults() {
        if (this.mode == 6) {
            Boolean[] values = new Boolean[((boolean[]) this.deff).length];
            for (int i = 0; i < values.length; i++) {
                values[i] = ((boolean[]) this.deff)[i];
            }
            return values;
        } else if (this.mode == 7) {
            Double[] values = new Double[((double[]) this.deff).length];
            for (int i = 0; i < values.length; i++) {
                values[i] = ((double[]) this.deff)[i];
            }
            return values;
        } else if (this.mode == 8) {
            Integer[] values = new Integer[((int[]) this.deff).length];
            for (int i = 0; i < values.length; i++) {
                values[i] = ((int[]) this.deff)[i];
            }
            return values;
        } else if (this.mode == 9) {
            return (IConfigurable[]) this.deff;
        } else {
            return (String[]) this.deff;
        }
    }

    @Override
    public void setToDefault() {
        this.value.setToDefault();
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
        return this.value.getString();
    }

    @Override
    public Object[] getList() {
        return (Object[]) this.getValue();
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
        if (this.mode == 5) {
            String[] strValues = this.value.getStringList();
            IConfigurable[] values = new IConfigurable[strValues.length];
            for (int i = 0; i < values.length; i++) {
                values[i] = ((IConfigurable[]) this.deff)[0].load(strValues[i]);
            }
            return (T) values;
        }
        if (this.mode == 6) {
            return (T) this.value.getBooleanList();
        }
        if (this.mode == 7) {
            return (T) this.value.getDoubleList();
        }
        if (this.mode == 8) {
            return (T) this.value.getIntList();
        }
        if (this.mode == 9) {
            return (T) this.value.getString();
        }

        return this.deff;
    }

    public void setValue(T value) {
        if (this.value == null || (this.side != FMLCommonHandler.instance().getSide() && this.side != null)) {
            return;
        }

        if (this.mode == 0) {
            this.value.set((String) value);
        }
        if (this.mode == 1) {
            this.value.set((Boolean) value);
        }
        if (this.mode == 2) {
            this.value.set((Double) value);
        }
        if (this.mode == 3) {
            this.value.set((Integer) value);
        }
        if (this.mode == 4) {
            this.value.set((String) value);
        }
        if (this.mode == 5) {
            String[] values = new String[((IConfigurable[]) this.deff).length];
            for (int i = 0; i < values.length; i++) {
                values[i] = ((IConfigurable[]) this.deff)[i].serialize();
            }
            this.value.set(values);
        }
        if (this.mode == 6) {
            this.value.set((boolean[]) value);
        }
        if (this.mode == 7) {
            this.value.set((double[]) value);
        }
        if (this.mode == 8) {
            this.value.set((int[]) value);
        }
        if (this.mode == 9) {
            this.value.set((String[]) value);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void set(Object value) {
        this.setValue((T) value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void set(Object[] value) {
        this.setValue((T) value);
    }

    @Override
    public String[] getValidValues() {
        if (this.mode == 0) {
            return ((IConfigurable) this.deff).getValidValues();
        }
        if (this.mode == 6) {
            return ((IConfigurable[]) this.deff)[0].getValidValues();
        }
        return this.value.getValidValues();
    }

    @SuppressWarnings("unchecked")
    @Override
    public T getMinValue() {
        return (T) this.value.getMinValue();
    }

    @SuppressWarnings("unchecked")
    @Override
    public T getMaxValue() {
        return (T) this.value.getMaxValue();
    }

    @Override
    public Pattern getValidationPattern() {
        return this.value.getValidationPattern();
    }

}
