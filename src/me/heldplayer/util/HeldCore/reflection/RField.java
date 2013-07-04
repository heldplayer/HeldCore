
package me.heldplayer.util.HeldCore.reflection;

import java.lang.reflect.Field;
import java.util.logging.Level;

import me.heldplayer.util.HeldCore.Updater;

@SuppressWarnings("unchecked")
public class RField<T, V> {

    protected final RClass<T> clazz;
    protected final Field field;

    public RField(RClass<T> clazz, Field field) {
        this.clazz = clazz;
        this.field = field;
    }

    public V getStatic() {
        return get(null);
    }

    public V get(T classInstance) {
        try {
            V result = (V) field.get(classInstance);

            Updater.log.log(Level.WARNING, "Reflection: Fetched field value");
            return result;
        }
        catch (Exception e) {
            Updater.log.log(Level.WARNING, "Reflection: Exception while getting field value", e);
        }

        return null;
    }

}
