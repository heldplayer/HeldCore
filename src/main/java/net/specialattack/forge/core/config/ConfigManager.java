package net.specialattack.forge.core.config;

import cpw.mods.fml.client.config.ConfigGuiType;
import cpw.mods.fml.client.config.IConfigElement;
import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.ModMetadata;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.config.Property;

public class ConfigManager {

    public static File configFolder;
    public static boolean debug;
    private static boolean initialized = false;
    public static Map<String, ConfigManager> configs = new HashMap<String, ConfigManager>();

    public static ConfigManager registerConfig(Object object) {
        ModContainer mod;
        if (ConfigManager.initialized) {
            mod = Loader.instance().activeModContainer();
            if (mod == null) {
                throw new IllegalStateException("Failed getting the active mod container. You should not be registering configs at this time!");
            }
        } else {
            ModMetadata md = new ModMetadata();
            md.modId = "ASM";
            mod = new DummyModContainer(md);
        }
        Class<?> clazz = object.getClass();

        Configuration ann = clazz.getAnnotation(Configuration.class);
        if (ann == null) {
            throw new IllegalStateException(String.format("Tried registering config class for mod %s, but the class is not annotated with @Configuration", mod.getModId()));
        }

        ConfigManager manager = new ConfigManager(ann.value(), mod.getModId(), object);

        // Resolving options
        for (Field field : clazz.getFields()) {
            Configuration.Option option = field.getAnnotation(Configuration.Option.class);
            if (option != null) {
                String name = field.getName();
                if (!option.name().trim().isEmpty()) {
                    name = option.name().trim();
                }

                Option opt;
                Class<?> type = field.getType();

                boolean isArray = false;
                Configuration.Array array = field.getAnnotation(Configuration.Array.class);
                if (type.isArray()) {
                    isArray = true;
                    type = type.getComponentType();
                } else if (array != null) {
                    throw new IllegalStateException(String.format("Mod %s tried loading a config with an Array tag on a non-array for option %s", mod.getModId(), name));
                }

                Category category = manager.resolveCategory(option.category());

                if (type == int.class) {
                    opt = new IntOption(name, category);
                } else if (type == double.class) {
                    opt = new DoubleOption(name, category);
                } else if (type == boolean.class) {
                    opt = new BooleanOption(name, category);
                } else if (type == String.class) {
                    opt = new StringOption(name, category);
                } else {
                    throw new IllegalStateException(String.format("Mod %s tried loading a config with an unsupported type for option %s", mod.getModId(), name));
                }

                opt.manager = manager;
                opt.field = field;
                opt.side = option.side();
                opt.needsRelog = option.needsRelog();
                opt.needsRestart = option.needsRestart();
                opt.languageKey = manager.name + ":" + category.name + "." + opt.name;
                if (isArray) {
                    opt.array = true;
                    if (array != null) {
                        opt.maxLength = array.maxLength();
                        opt.fixedLength = array.fixedLength();
                    } else {
                        opt.maxLength = -1;
                        opt.fixedLength = false;
                    }
                }

                Configuration.Alias alias = field.getAnnotation(Configuration.Alias.class);
                if (alias != null) {
                    opt.setAlias(alias.name(), alias.category());
                }

                if (field.getAnnotation(Configuration.Hidden.class) != null) {
                    opt.hidden = true;
                }
                if (field.getAnnotation(Configuration.Debug.class) != null) {
                    opt.debug = true;
                    if (!ConfigManager.debug) {
                        opt.hidden = true;
                    }
                }
                Configuration.Syncronized syncronized = field.getAnnotation(Configuration.Syncronized.class);
                if (syncronized != null) {
                    opt.syncSide = syncronized.value();
                }

                try {
                    opt.loadDefault(field, object);
                    opt.firstLoad(manager.configuration);
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException(String.format("Mod %s tried loading a config with in inaccessible option %s", mod.getModId(), name), e);
                }

                Configuration.Comment comment = field.getAnnotation(Configuration.Comment.class);
                if (comment != null) {
                    opt.property.comment = comment.value();
                }
            }
        }

        if (manager.configuration.hasChanged()) {
            manager.configuration.save();
        }

        ConfigManager.configs.put(manager.name, manager);

        return manager;
    }

    public static void initialized() {
        ConfigManager.initialized = true;
    }

    private static String translate(String key) {
        if (ConfigManager.initialized) {
            return ConfigManager.translate_(key);
        } else {
            return null;
        }
    }

    private static String translate_(String key) {
        return StatCollector.translateToLocal(key);
    }

    private String name;
    public String modId;
    public net.minecraftforge.common.config.Configuration configuration;
    private Object obj;
    public Map<String, Category> categories = new HashMap<String, Category>();
    private Runnable reloadListener;

    public ConfigManager(String name, String modId, Object obj) {
        this.name = name;
        this.modId = modId;
        this.configuration = new net.minecraftforge.common.config.Configuration(new File(ConfigManager.configFolder, name));
        this.obj = obj;
    }

    public void setReloadListener(Runnable reloadListener) {
        this.reloadListener = reloadListener;
    }

    public void reload() {
        try {
            for (Category category : this.categories.values()) {
                for (Option option : category.options.values()) {
                    option.reload();
                }
            }
            if (this.reloadListener != null) {
                this.reloadListener.run();
            }
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Failed updating config object members!", e);
        }
    }

    private Category resolveCategory(String name) {
        Category result = this.categories.get(name);
        if (result == null) {
            result = new Category(name);
            result.languageKey = this.name + ":" + name;
            this.categories.put(name, result);
        }
        return result;
    }

    private static class Category extends ElementStub.Category {

        public String name;
        public String languageKey;
        public Map<String, Category> children;
        public Map<String, Option> options;

        public Category(String name) {
            this.name = name;
        }

        public Map<String, Option> getOptions() {
            if (this.options == null) {
                this.options = new HashMap<String, Option>();
            }
            return this.options;
        }

        @Override
        public String getName() {
            return this.name + " (" + StatCollector.translateToLocal(this.languageKey) + ")";
        }

        @Override
        public String getQualifiedName() {
            return this.name;
        }

        @Override
        public String getLanguageKey() {
            return this.languageKey;
        }

        @Override
        public String getComment() {
            return StatCollector.translateToLocal(this.languageKey + ".comment");
        }

        @Override
        public List<IConfigElement> getChildElements() {
            ArrayList<IConfigElement> result = new ArrayList<IConfigElement>();

            if (this.children != null) {
                for (Category category : this.children.values()) {
                    result.add(category);
                }
            }

            if (this.options != null) {
                for (Option key : this.options.values()) {
                    result.add(key);
                }
            }

            return result;
        }

        @Override
        public ConfigGuiType getType() {
            return ConfigGuiType.CONFIG_CATEGORY;
        }

        @Override
        public boolean requiresWorldRestart() {
            return false;
        }

        @Override
        public boolean showInGui() {
            return true;
        }

        @Override
        public boolean requiresMcRestart() {
            return false;
        }
    }

    private static abstract class Option extends ElementStub.Element {

        public String name, alias, aliasCategory;
        public Category category;
        public String languageKey;
        public Configuration.CSide side, syncSide;
        public boolean needsRestart, needsRelog;
        public boolean debug, hidden;
        public int maxLength;
        public boolean array, fixedLength;

        protected Field field;
        protected ConfigManager manager;
        protected Property property;

        public Option(String name, Category category) {
            this.name = name;
            this.category = category;
            category.getOptions().put(this.name, this);
        }

        public void setAlias(String alias, String aliasCategory) {
            this.alias = alias;
            this.aliasCategory = aliasCategory;
        }

        public abstract void loadDefault(Field field, Object obj) throws IllegalAccessException;

        public abstract void firstLoad(net.minecraftforge.common.config.Configuration configuration) throws IllegalAccessException;

        public abstract void reload() throws IllegalAccessException;

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public String getLanguageKey() {
            return this.languageKey;
        }

        @Override
        public String getComment() {
            return StatCollector.translateToLocal(this.languageKey + ".comment");
        }

        @Override
        public boolean isList() {
            return this.array;
        }

        @Override
        public boolean isListLengthFixed() {
            return this.fixedLength;
        }

        @Override
        public int getMaxListLength() {
            return this.maxLength;
        }

        @Override
        public boolean isDefault() {
            return this.property.isDefault();
        }

        @Override
        public void setToDefault() {
            this.property.setToDefault();
        }

        @Override
        public boolean requiresWorldRestart() {
            return this.needsRelog;
        }

        @Override
        public boolean showInGui() {
            return !this.hidden;
        }

        @Override
        public boolean requiresMcRestart() {
            return this.needsRestart;
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
    }

    private static class IntOption extends Option {

        private int def;
        private int[] defA;
        private int min, max;

        public IntOption(String name, Category category) {
            super(name, category);
        }

        @Override
        public void loadDefault(Field field, Object obj) throws IllegalAccessException {
            if (!this.array) {
                this.def = field.getInt(obj);
            } else {
                int[] def = (int[]) field.get(obj);
                this.defA = new int[def.length];
                System.arraycopy(def, 0, this.defA, 0, def.length);
            }
            Configuration.IntMinMax minMax = field.getAnnotation(Configuration.IntMinMax.class);
            if (minMax != null) {
                this.min = minMax.min();
                this.max = minMax.max();
            } else {
                this.min = Integer.MIN_VALUE;
                this.max = Integer.MAX_VALUE;
            }
        }

        @Override
        public void firstLoad(net.minecraftforge.common.config.Configuration configuration) throws IllegalAccessException {
            if (!this.array) {
                int value;
                if (configuration.hasKey(this.category.name, this.name)) {
                    this.property = configuration.get(this.category.name, this.name, this.def, ConfigManager.translate(this.languageKey + ".comment"), this.min, this.max);
                    value = this.property.getInt();
                } else if (this.aliasCategory != null && configuration.hasKey(this.aliasCategory, this.alias)) {
                    this.property = configuration.get(this.aliasCategory, this.alias, this.def, null, this.min, this.max);
                    value = this.property.getInt();
                    configuration.getCategory(this.aliasCategory).remove(this.alias);
                    this.property = configuration.get(this.category.name, this.name, this.def, ConfigManager.translate(this.languageKey + ".comment"), this.min, this.max);
                    this.property.set(value);
                } else {
                    value = this.def;
                    this.property = configuration.get(this.category.name, this.name, this.def, ConfigManager.translate(this.languageKey + ".comment"), this.min, this.max);
                    this.property.set(value);
                }
                this.field.setInt(this.manager.obj, value);
            } else {
                int[] value;
                if (configuration.hasKey(this.category.name, this.name)) {
                    this.property = configuration.get(this.category.name, this.name, this.defA, ConfigManager.translate(this.languageKey + ".comment"), this.min, this.max);
                    this.property.setIsListLengthFixed(this.fixedLength);
                    this.property.setMaxListLength(this.maxLength);
                    value = this.property.getIntList();
                } else if (this.aliasCategory != null && configuration.hasKey(this.aliasCategory, this.alias)) {
                    this.property = configuration.get(this.aliasCategory, this.alias, this.defA, null, this.min, this.max);
                    this.property.setIsListLengthFixed(this.fixedLength);
                    this.property.setMaxListLength(this.maxLength);
                    value = this.property.getIntList();
                    configuration.getCategory(this.aliasCategory).remove(this.alias);
                    this.property = configuration.get(this.category.name, this.name, this.defA, ConfigManager.translate(this.languageKey + ".comment"), this.min, this.max);
                    this.property.setIsListLengthFixed(this.fixedLength);
                    this.property.setMaxListLength(this.maxLength);
                    this.property.set(value);
                } else {
                    value = this.defA;
                    this.property = configuration.get(this.category.name, this.name, this.defA, ConfigManager.translate(this.languageKey + ".comment"), this.min, this.max);
                    this.property.setIsListLengthFixed(this.fixedLength);
                    this.property.setMaxListLength(this.maxLength);
                    this.property.set(value);
                }
                this.field.set(this.manager.obj, value);
            }
            if (this.debug && !ConfigManager.debug) {
                if (configuration.hasKey(this.category.name, this.name)) {
                    configuration.getCategory(this.category.name).remove(this.name);
                }
            }
        }

        @Override
        public void reload() throws IllegalAccessException {
            if (!this.array) {
                this.field.setInt(this.manager.obj, this.property.getInt());
            } else {
                this.field.set(this.manager.obj, this.property.getIntList());
            }
        }

        @Override
        public ConfigGuiType getType() {
            return ConfigGuiType.INTEGER;
        }

        @Override
        public Object getDefault() {
            return this.def;
        }

        @Override
        public Object[] getDefaults() {
            Integer[] ints = new Integer[this.defA.length];
            for (int i = 0; i < ints.length; i++) {
                ints[i] = this.defA[i];
            }
            return ints;
        }

        @Override
        public Object get() {
            return this.property.getInt();
        }

        @Override
        public Object[] getList() {
            int[] val = this.property.getIntList();
            Integer[] ints = new Integer[val.length];
            for (int i = 0; i < ints.length; i++) {
                ints[i] = val[i];
            }
            return ints;
        }

        @Override
        public void set(Object value) {
            int val = (Integer) value;
            this.property.set(val);
            try {
                this.field.setInt(this.manager.obj, val);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Failed updating field", e);
            }
        }

        @Override
        public void set(Object[] aVal) {
            int[] val = new int[aVal.length];
            for (int i = 0; i < val.length; i++) {
                val[i] = (Integer) aVal[i];
            }
            try {
                this.field.set(this.manager.obj, val);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Failed updating field", e);
            }
        }

        @Override
        public Object getMinValue() {
            return this.min;
        }

        @Override
        public Object getMaxValue() {
            return this.max;
        }
    }

    private static class DoubleOption extends Option {

        private double def;
        private double[] defA;
        private double min, max;

        public DoubleOption(String name, Category category) {
            super(name, category);
        }

        @Override
        public void loadDefault(Field field, Object obj) throws IllegalAccessException {
            if (!this.array) {
                this.def = field.getDouble(obj);
            } else {
                double[] def = (double[]) field.get(obj);
                this.defA = new double[def.length];
                System.arraycopy(def, 0, this.defA, 0, def.length);
            }
            Configuration.DoubleMinMax minMax = field.getAnnotation(Configuration.DoubleMinMax.class);
            if (minMax != null) {
                this.min = minMax.min();
                this.max = minMax.max();
            } else {
                this.min = Double.MIN_VALUE;
                this.max = Double.MAX_VALUE;
            }
        }

        @Override
        public void firstLoad(net.minecraftforge.common.config.Configuration configuration) throws IllegalAccessException {
            if (!this.array) {
                double value;
                if (configuration.hasKey(this.category.name, this.name)) {
                    this.property = configuration.get(this.category.name, this.name, this.def, ConfigManager.translate(this.languageKey + ".comment"), this.min, this.max);
                    value = this.property.getDouble();
                } else if (this.aliasCategory != null && configuration.hasKey(this.aliasCategory, this.alias)) {
                    this.property = configuration.get(this.aliasCategory, this.alias, this.def, null, this.min, this.max);
                    value = this.property.getDouble();
                    configuration.getCategory(this.aliasCategory).remove(this.alias);
                    this.property = configuration.get(this.category.name, this.name, this.def, ConfigManager.translate(this.languageKey + ".comment"), this.min, this.max);
                    this.property.set(value);
                } else {
                    value = this.def;
                    this.property = configuration.get(this.category.name, this.name, this.def, ConfigManager.translate(this.languageKey + ".comment"), this.min, this.max);
                    this.property.set(value);
                }
                this.field.setDouble(this.manager.obj, value);
            } else {
                double[] value;
                if (configuration.hasKey(this.category.name, this.name)) {
                    this.property = configuration.get(this.category.name, this.name, this.defA, ConfigManager.translate(this.languageKey + ".comment"), this.min, this.max);
                    this.property.setIsListLengthFixed(this.fixedLength);
                    this.property.setMaxListLength(this.maxLength);
                    value = this.property.getDoubleList();
                } else if (this.aliasCategory != null && configuration.hasKey(this.aliasCategory, this.alias)) {
                    this.property = configuration.get(this.aliasCategory, this.alias, this.defA, null, this.min, this.max);
                    this.property.setIsListLengthFixed(this.fixedLength);
                    this.property.setMaxListLength(this.maxLength);
                    value = this.property.getDoubleList();
                    configuration.getCategory(this.aliasCategory).remove(this.alias);
                    this.property = configuration.get(this.category.name, this.name, this.defA, ConfigManager.translate(this.languageKey + ".comment"), this.min, this.max);
                    this.property.setIsListLengthFixed(this.fixedLength);
                    this.property.setMaxListLength(this.maxLength);
                    this.property.set(value);
                } else {
                    value = this.defA;
                    this.property = configuration.get(this.category.name, this.name, this.defA, ConfigManager.translate(this.languageKey + ".comment"), this.min, this.max);
                    this.property.setIsListLengthFixed(this.fixedLength);
                    this.property.setMaxListLength(this.maxLength);
                    this.property.set(value);
                }
                this.field.set(this.manager.obj, value);
            }
            if (this.debug && !ConfigManager.debug) {
                if (configuration.hasKey(this.category.name, this.name)) {
                    configuration.getCategory(this.category.name).remove(this.name);
                }
            }
        }

        @Override
        public void reload() throws IllegalAccessException {
            if (!this.array) {
                this.field.setDouble(this.manager.obj, this.property.getDouble());
            } else {
                this.field.set(this.manager.obj, this.property.getDoubleList());
            }
        }

        @Override
        public ConfigGuiType getType() {
            return ConfigGuiType.DOUBLE;
        }

        @Override
        public Object getDefault() {
            return this.def;
        }

        @Override
        public Object[] getDefaults() {
            Double[] doubles = new Double[this.defA.length];
            for (int i = 0; i < doubles.length; i++) {
                doubles[i] = this.defA[i];
            }
            return doubles;
        }

        @Override
        public Object get() {
            return this.property.getDouble();
        }

        @Override
        public Object[] getList() {
            double[] val = this.property.getDoubleList();
            Double[] doubles = new Double[val.length];
            for (int i = 0; i < doubles.length; i++) {
                doubles[i] = val[i];
            }
            return doubles;
        }

        @Override
        public void set(Object value) {
            double val = (Double) value;
            this.property.set(val);
            try {
                this.field.setDouble(this.manager.obj, val);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Failed updating field", e);
            }
        }

        @Override
        public void set(Object[] aVal) {
            double[] val = new double[aVal.length];
            for (int i = 0; i < val.length; i++) {
                val[i] = (Double) aVal[i];
            }
            try {
                this.field.set(this.manager.obj, val);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Failed updating field", e);
            }
        }

        @Override
        public Object getMinValue() {
            return this.min;
        }

        @Override
        public Object getMaxValue() {
            return this.max;
        }
    }

    private static class BooleanOption extends Option {

        private boolean def;
        private boolean[] defA;

        public BooleanOption(String name, Category category) {
            super(name, category);
        }

        @Override
        public void loadDefault(Field field, Object obj) throws IllegalAccessException {
            if (!this.array) {
                this.def = field.getBoolean(obj);
            } else {
                boolean[] def = (boolean[]) field.get(obj);
                this.defA = new boolean[def.length];
                System.arraycopy(def, 0, this.defA, 0, def.length);
            }
        }

        @Override
        public void firstLoad(net.minecraftforge.common.config.Configuration configuration) throws IllegalAccessException {
            if (!this.array) {
                boolean value;
                if (configuration.hasKey(this.category.name, this.name)) {
                    this.property = configuration.get(this.category.name, this.name, this.def, ConfigManager.translate(this.languageKey + ".comment"));
                    value = this.property.getBoolean();
                } else if (this.aliasCategory != null && configuration.hasKey(this.aliasCategory, this.alias)) {
                    this.property = configuration.get(this.aliasCategory, this.alias, this.def);
                    value = this.property.getBoolean();
                    configuration.getCategory(this.aliasCategory).remove(this.alias);
                    this.property = configuration.get(this.category.name, this.name, this.def, ConfigManager.translate(this.languageKey + ".comment"));
                    this.property.set(value);
                } else {
                    value = this.def;
                    this.property = configuration.get(this.category.name, this.name, this.def, ConfigManager.translate(this.languageKey + ".comment"));
                    this.property.set(value);
                }
                this.field.setBoolean(this.manager.obj, value);
            } else {
                boolean[] value;
                if (configuration.hasKey(this.category.name, this.name)) {
                    this.property = configuration.get(this.category.name, this.name, this.defA, ConfigManager.translate(this.languageKey + ".comment"));
                    this.property.setIsListLengthFixed(this.fixedLength);
                    this.property.setMaxListLength(this.maxLength);
                    value = this.property.getBooleanList();
                } else if (this.aliasCategory != null && configuration.hasKey(this.aliasCategory, this.alias)) {
                    this.property = configuration.get(this.aliasCategory, this.alias, this.defA);
                    this.property.setIsListLengthFixed(this.fixedLength);
                    this.property.setMaxListLength(this.maxLength);
                    value = this.property.getBooleanList();
                    configuration.getCategory(this.aliasCategory).remove(this.alias);
                    this.property = configuration.get(this.category.name, this.name, this.defA, ConfigManager.translate(this.languageKey + ".comment"));
                    this.property.setIsListLengthFixed(this.fixedLength);
                    this.property.setMaxListLength(this.maxLength);
                    this.property.set(value);
                } else {
                    value = this.defA;
                    this.property = configuration.get(this.category.name, this.name, this.defA, ConfigManager.translate(this.languageKey + ".comment"));
                    this.property.setIsListLengthFixed(this.fixedLength);
                    this.property.setMaxListLength(this.maxLength);
                    this.property.set(value);
                }
                this.field.set(this.manager.obj, value);
            }
            if (this.debug && !ConfigManager.debug) {
                if (configuration.hasKey(this.category.name, this.name)) {
                    configuration.getCategory(this.category.name).remove(this.name);
                }
            }
        }

        @Override
        public void reload() throws IllegalAccessException {
            if (!this.array) {
                this.field.setBoolean(this.manager.obj, this.property.getBoolean());
            } else {
                this.field.set(this.manager.obj, this.property.getBooleanList());
            }
        }

        @Override
        public ConfigGuiType getType() {
            return ConfigGuiType.BOOLEAN;
        }

        @Override
        public Object getDefault() {
            return this.def;
        }

        @Override
        public Object[] getDefaults() {
            Boolean[] bools = new Boolean[this.defA.length];
            for (int i = 0; i < bools.length; i++) {
                bools[i] = this.defA[i];
            }
            return bools;
        }

        @Override
        public Object get() {
            return this.property.getBoolean();
        }

        @Override
        public Object[] getList() {
            boolean[] val = this.property.getBooleanList();
            Boolean[] bools = new Boolean[val.length];
            for (int i = 0; i < bools.length; i++) {
                bools[i] = val[i];
            }
            return bools;
        }

        @Override
        public void set(Object value) {
            boolean val = (Boolean) value;
            this.property.set(val);
            try {
                this.field.setBoolean(this.manager.obj, val);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Failed updating field", e);
            }
        }

        @Override
        public void set(Object[] aVal) {
            boolean[] val = new boolean[aVal.length];
            for (int i = 0; i < val.length; i++) {
                val[i] = (Boolean) aVal[i];
            }
            try {
                this.field.set(this.manager.obj, val);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Failed updating field", e);
            }
        }
    }

    private static class StringOption extends Option {

        private String def;
        private String[] defA;
        private String[] options;
        private Pattern pattern;

        public StringOption(String name, Category category) {
            super(name, category);
        }

        @Override
        public void loadDefault(Field field, Object obj) throws IllegalAccessException {
            if (!this.array) {
                this.def = (String) field.get(obj);
            } else {
                String[] def = (String[]) field.get(obj);
                this.defA = new String[def.length];
                System.arraycopy(def, 0, this.defA, 0, def.length);
            }
            Configuration.StringPattern pattern = field.getAnnotation(Configuration.StringPattern.class);
            if (pattern != null) {
                this.pattern = Pattern.compile(pattern.value());
            }
            Configuration.StringOptions options = field.getAnnotation(Configuration.StringOptions.class);
            if (options != null) {
                String[] opts = options.value();
                this.options = new String[opts.length];
                System.arraycopy(opts, 0, this.options, 0, opts.length);
            }
        }

        @Override
        public void firstLoad(net.minecraftforge.common.config.Configuration configuration) throws IllegalAccessException {
            if (!this.array) {
                String value;
                if (configuration.hasKey(this.category.name, this.name)) {
                    this.property = configuration.get(this.category.name, this.name, this.def, ConfigManager.translate(this.languageKey + ".comment"));
                    this.property.setValidationPattern(this.pattern);
                    this.property.setValidValues(this.options);
                    value = this.property.getString();
                } else if (this.aliasCategory != null && configuration.hasKey(this.aliasCategory, this.alias)) {
                    this.property = configuration.get(this.aliasCategory, this.alias, this.def);
                    this.property.setValidationPattern(this.pattern);
                    this.property.setValidValues(this.options);
                    value = this.property.getString();
                    configuration.getCategory(this.aliasCategory).remove(this.alias);
                    this.property = configuration.get(this.category.name, this.name, this.def, ConfigManager.translate(this.languageKey + ".comment"));
                    this.property.setValidationPattern(this.pattern);
                    this.property.setValidValues(this.options);
                    this.property.set(value);
                } else {
                    value = this.def;
                    this.property = configuration.get(this.category.name, this.name, this.def, ConfigManager.translate(this.languageKey + ".comment"));
                    this.property.setValidationPattern(this.pattern);
                    this.property.setValidValues(this.options);
                    this.property.set(value);
                }
                this.field.set(this.manager.obj, value);
            } else {
                String[] value;
                if (configuration.hasKey(this.category.name, this.name)) {
                    this.property = configuration.get(this.category.name, this.name, this.defA, ConfigManager.translate(this.languageKey + ".comment"));
                    this.property.setValidationPattern(this.pattern);
                    this.property.setValidValues(this.options);
                    this.property.setIsListLengthFixed(this.fixedLength);
                    this.property.setMaxListLength(this.maxLength);
                    value = this.property.getStringList();
                } else if (this.aliasCategory != null && configuration.hasKey(this.aliasCategory, this.alias)) {
                    this.property = configuration.get(this.aliasCategory, this.alias, this.defA);
                    this.property.setValidationPattern(this.pattern);
                    this.property.setValidValues(this.options);
                    this.property.setIsListLengthFixed(this.fixedLength);
                    this.property.setMaxListLength(this.maxLength);
                    value = this.property.getStringList();
                    configuration.getCategory(this.aliasCategory).remove(this.alias);
                    this.property = configuration.get(this.category.name, this.name, this.defA, ConfigManager.translate(this.languageKey + ".comment"));
                    this.property.setValidationPattern(this.pattern);
                    this.property.setValidValues(this.options);
                    this.property.setIsListLengthFixed(this.fixedLength);
                    this.property.setMaxListLength(this.maxLength);
                    this.property.set(value);
                } else {
                    value = this.defA;
                    this.property = configuration.get(this.category.name, this.name, this.defA, ConfigManager.translate(this.languageKey + ".comment"));
                    this.property.setValidationPattern(this.pattern);
                    this.property.setValidValues(this.options);
                    this.property.setIsListLengthFixed(this.fixedLength);
                    this.property.setMaxListLength(this.maxLength);
                    this.property.set(value);
                }
                this.field.set(this.manager.obj, value);
            }
            if (this.debug && !ConfigManager.debug) {
                if (configuration.hasKey(this.category.name, this.name)) {
                    configuration.getCategory(this.category.name).remove(this.name);
                }
            }
        }

        @Override
        public void reload() throws IllegalAccessException {
            if (!this.array) {
                this.field.set(this.manager.obj, this.property.getString());
            } else {
                this.field.set(this.manager.obj, this.property.getStringList());
            }
        }

        @Override
        public ConfigGuiType getType() {
            return ConfigGuiType.STRING;
        }

        @Override
        public Object getDefault() {
            return this.def;
        }

        @Override
        public Object[] getDefaults() {
            return this.defA;
        }

        @Override
        public Object get() {
            return this.property.getString();
        }

        @Override
        public Object[] getList() {
            return this.property.getStringList();
        }

        @Override
        public void set(Object value) {
            this.property.set((String) value);
            try {
                this.field.set(this.manager.obj, value);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Failed updating field", e);
            }
        }

        @Override
        public void set(Object[] aVal) {
            this.property.set((String[]) aVal);
            try {
                this.field.set(this.manager.obj, aVal);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Failed updating field", e);
            }
        }

        @Override
        public String[] getValidValues() {
            return this.options;
        }

        @Override
        public Pattern getValidationPattern() {
            return this.pattern;
        }
    }
}
