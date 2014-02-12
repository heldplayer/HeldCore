
package net.specialattack.forge.core.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

@SuppressWarnings("rawtypes")
public class RClass<T> {

    protected final Class<T> clazz;

    public RClass(Class<T> clazz) {
        this.clazz = clazz;
    }

    public <V> RField<T, V> getField(String name) {
        try {
            Field field = this.clazz.getDeclaredField(name);
            field.setAccessible(true);

            return new RField<T, V>(this, field);
        }
        catch (Exception e) {}

        return null;
    }

    public <V> RMethod<T, V> getMethod(String name, Class... args) {
        try {
            Method method = this.clazz.getDeclaredMethod(name, args);
            method.setAccessible(true);

            return new RMethod<T, V>(this, method);
        }
        catch (Exception e) {}

        return null;
    }

    public RConstructor<T> getConstructor(Class... args) {
        try {
            Constructor<T> constructor = this.clazz.getDeclaredConstructor(args);
            constructor.setAccessible(true);

            return new RConstructor<T>(this, constructor);
        }
        catch (Exception e) {}

        return null;
    }

}
