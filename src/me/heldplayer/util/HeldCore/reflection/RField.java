
package me.heldplayer.util.HeldCore.reflection;

import java.lang.reflect.Field;
import java.util.logging.Level;

import me.heldplayer.util.HeldCore.HeldCore;

@SuppressWarnings("unchecked")
public class RField<T, V> {

    protected final RClass<T> clazz;
    protected final Field field;

    public RField(RClass<T> clazz, Field field) {
        this.clazz = clazz;
        this.field = field;
    }

    public V getStatic() {
        return this.get(null);
    }

    public V get(T classInstance) {
        try {
            V result = (V) this.field.get(classInstance);

            HeldCore.log.log(Level.FINE, "Reflection: [" + this.clazz.clazz.getName() + ":" + this.field.getName() + "] Fetched field value");
            return result;
        }
        catch (Exception e) {
            HeldCore.log.log(Level.WARNING, "Reflection: [" + this.clazz.clazz.getName() + ":" + this.field.getName() + "] Exception while getting field value", e);
        }

        return null;
    }

}
