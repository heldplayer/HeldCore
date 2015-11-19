package net.specialattack.forge.core.config;

import java.util.List;
import java.util.regex.Pattern;
import net.minecraftforge.fml.client.config.ConfigGuiType;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.GuiEditArrayEntries;
import net.minecraftforge.fml.client.config.IConfigElement;

public final class ElementStub {

    private ElementStub() {
    }

    public static abstract class Category implements IConfigElement {

        @Override
        public boolean isProperty() {
            return false;
        }

        @Override
        public Class<? extends GuiConfigEntries.IConfigEntry> getConfigEntryClass() {
            return null;
        }

        @Override
        public Class<? extends GuiEditArrayEntries.IArrayEntry> getArrayEntryClass() {
            return null;
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
            return 0;
        }

        @Override
        public boolean isDefault() {
            return false;
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
        public Object get() {
            return null;
        }

        @Override
        public Object[] getList() {
            return null;
        }

        @Override
        public String[] getValidValues() {
            return null;
        }

        @Override
        public Object getMinValue() {
            return null;
        }

        @Override
        public Object getMaxValue() {
            return null;
        }

        @Override
        public Pattern getValidationPattern() {
            return null;
        }

        @Override
        public void set(Object[] aVal) {
        }

        @Override
        public void set(Object value) {
        }
    }

    public static abstract class Element implements IConfigElement {

        @Override
        public boolean isProperty() {
            return true;
        }

        @Override
        public Class<? extends GuiConfigEntries.IConfigEntry> getConfigEntryClass() {
            return null;
        }

        @Override
        public Class<? extends GuiEditArrayEntries.IArrayEntry> getArrayEntryClass() {
            return null;
        }

        @Override
        public String getQualifiedName() {
            return null;
        }

        @Override
        public List<IConfigElement> getChildElements() {
            return null;
        }
    }
}
