package net.specialattack.forge.core.reflection;

import java.lang.reflect.Field;

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
            return (V) this.field.get(classInstance);
        } catch (Exception e) {
        }

        return null;
    }

}
