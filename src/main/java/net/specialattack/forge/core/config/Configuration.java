package net.specialattack.forge.core.config;

import cpw.mods.fml.relauncher.Side;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark a class with this annotation to mark it as a configuration class to be handled.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Configuration {

    /**
     * @return The name of the config file.
     */
    String value();

    /**
     * Use this to mark a field inside a configuration as an option.
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Option {

        /**
         * @return The category the option should be in, sepperated by a period if inside a nested category. (this is handled automatically)
         */
        String category();

        /**
         * @return The name to be used in the configuration, different from the field name.
         */
        String name() default "";

        /**
         * @return The side this option should be loaded at.
         */
        CSide side() default CSide.BOTH;

        /**
         * @return True if this option needs a restart to get applied. False if it can be changed during a session.
         */
        boolean needsRestart() default false;

        /**
         * @return True if this option needs the world to be reloaded to get applied. False if it can be changed while in a world.
         */
        boolean needsRelog() default false;
    }

    /**
     * Mark an Option with this to make it load the settings from a different location if the main location is not found.
     * Use this for backwards compatability.
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Alias {

        /**
         * @return The category the option should be in, sepperated by a period if inside a nested category. (this is handled automatically)
         */
        String category();

        /**
         * @return The name to be used in the configuration, different from the field name.
         */
        String name();
    }

    /**
     * Mark an Option with this to set a forced comment, rather than having it loaded through localization.
     * Useful for loading when localization is not yet available.
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Comment {

        /**
         * @return The comment to display.
         */
        String value();

    }

    /**
     * Mark an Option with this to prevent it from being enabled outside of dev.
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Debug {

    }

    /**
     * Mark an Option with this to prevent it from being visible in the in-game configuration.
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Hidden {

    }

    /**
     * Mark an Option with this to synchronize it between the server and the client.
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Syncronized {

        /**
         * @return The direction to synchronize to.
         */
        CSide value();
    }

    /**
     * Mark an Option that is an array with this to specify details about its length.
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Array {

        /**
         * @return The max length of the array, or the length of the array if it has a fixed length.
         */
        int maxLength() default -1;

        /**
         * Used in conjunction with maxLength.
         *
         * @return True of this array has to be a specific length, false if it can be up to a specific length.
         */
        boolean fixedLength() default false;
    }

    /**
     * Mark an Option that is an int with this to specify its minimum and maximum values.
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface IntMinMax {

        /**
         * @return The minimum value of the option.
         */
        int min() default Integer.MIN_VALUE;

        /**
         * @return The maximum value of the option.
         */
        int max() default Integer.MAX_VALUE;
    }

    /**
     * Mark an Option that is a double with this to specify its minimum and maximum values.
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface DoubleMinMax {

        /**
         * @return The minimum value of the option.
         */
        double min() default Double.MIN_VALUE;

        /**
         * @return The maximum value of the option.
         */
        double max() default Double.MAX_VALUE;
    }

    /**
     * Mark an Option that is a String with this to specify its validation pattern.
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface StringPattern {

        /**
         * @return The pattern to use for validation.
         */
        String value();
    }

    /**
     * Mark an Option that is a String with this to specify the valid values it can have.
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface StringOptions {

        /**
         * @return The valid values that can be entered.
         */
        String[] value();
    }

    /**
     * Used as a dummy for working with the annotations as null is not allowed.
     */
    enum CSide {
        BOTH(null), CLIENT(Side.CLIENT), SERVER(Side.SERVER);

        public final Side side;

        CSide(Side side) {
            this.side = side;
        }
    }
}
