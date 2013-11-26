
package me.heldplayer.util.HeldCore.reflection;

import java.util.logging.Level;

import me.heldplayer.util.HeldCore.Objects;

@SuppressWarnings("unchecked")
public final class ReflectionHelper {

    public static <T> RClass<? extends T> getClass(String classPath) {
        try {
            Class<T> clazz = (Class<T>) Class.forName(classPath);

            Objects.log.log(Level.FINE, "Reflection: Found class '" + classPath + "'");
            return new RClass<T>(clazz);
        }
        catch (ClassNotFoundException e) {
            Objects.log.log(Level.WARNING, "Reflection: Class not found for '" + classPath + "'", e);
        }
        catch (ClassCastException e) {
            Objects.log.log(Level.WARNING, "Reflection: Class found but of invalid type", e);
        }

        return null;
    }

    public static <T> RClass<T> getClass(Class<T> clazz) {
        Objects.log.log(Level.FINE, "Reflection: Constructed class for '" + clazz.getName() + "'");
        return new RClass<T>(clazz);
    }

}
