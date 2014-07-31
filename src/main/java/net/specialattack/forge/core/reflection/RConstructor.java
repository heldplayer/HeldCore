package net.specialattack.forge.core.reflection;

import java.lang.reflect.Constructor;

public class RConstructor<T> {

    protected final RClass<T> clazz;
    protected final Constructor<T> constructor;

    public RConstructor(RClass<T> clazz, Constructor<T> constructor) {
        this.clazz = clazz;
        this.constructor = constructor;
    }

    public T newInstance(Object... args) {
        try {
            return this.constructor.newInstance(args);
        } catch (Exception e) {
        }

        return null;
    }

}
