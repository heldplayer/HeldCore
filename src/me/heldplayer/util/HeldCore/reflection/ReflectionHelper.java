
package me.heldplayer.util.HeldCore.reflection;

import java.util.logging.Level;

import me.heldplayer.util.HeldCore.Updater;

@SuppressWarnings("unchecked")
public final class ReflectionHelper {

    public static <T> RClass<? extends T> getClass(String classPath) {
        try {
            Class<T> clazz = (Class<T>) Class.forName(classPath);

            Updater.log.log(Level.WARNING, "Reflection: Found class '" + classPath + "'");
            return new RClass<T>(clazz);
        }
        catch (ClassNotFoundException e) {
            Updater.log.log(Level.WARNING, "Reflection: Class not found", e);
        }
        catch (ClassCastException e) {
            Updater.log.log(Level.WARNING, "Reflection: Class found but of invalid type", e);
        }

        return null;
    }

    public static <T> RClass<T> getClass(Class<T> clazz) {
        return new RClass<T>(clazz);
    }

}
