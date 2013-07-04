
package me.heldplayer.util.HeldCore.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.logging.Level;

import me.heldplayer.util.HeldCore.Updater;

@SuppressWarnings("rawtypes")
public class RClass<T> {

    protected final Class<T> clazz;

    public RClass(Class<T> clazz) {
        this.clazz = clazz;
    }

    public <V> RField<T, V> getField(String name) {
        try {
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);

            Updater.log.log(Level.WARNING, "Reflection: Found field '" + name + "'");
            return new RField<T, V>(this, field);
        }
        catch (NoSuchFieldException e) {
            Updater.log.log(Level.WARNING, "Reflection: Field not found", e);
        }
        catch (Exception e) {
            Updater.log.log(Level.WARNING, "Reflection: Exception while getting field", e);
        }

        return null;
    }

    public <V> RMethod<T, V> getMethod(String name, Class... args) {
        try {
            Method method = clazz.getDeclaredMethod(name, args);
            method.setAccessible(true);

            Updater.log.log(Level.WARNING, "Reflection: Found method '" + name + "'");
            return new RMethod<T, V>(this, method);
        }
        catch (NoSuchMethodException e) {
            Updater.log.log(Level.WARNING, "Reflection: Method not found", e);
        }
        catch (Exception e) {
            Updater.log.log(Level.WARNING, "Reflection: Exception while getting method", e);
        }

        return null;
    }

    public RConstructor<T> getConstructor(Class... args) {
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor(args);
            constructor.setAccessible(true);

            Updater.log.log(Level.WARNING, "Reflection: Found constructor");
            return new RConstructor<T>(this, constructor);
        }
        catch (NoSuchMethodException e) {
            Updater.log.log(Level.WARNING, "Reflection: Constructor not found", e);
        }
        catch (Exception e) {
            Updater.log.log(Level.WARNING, "Reflection: Exception while getting constructor", e);
        }

        return null;
    }

}
