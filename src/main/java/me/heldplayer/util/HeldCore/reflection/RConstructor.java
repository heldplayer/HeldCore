
package me.heldplayer.util.HeldCore.reflection;

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
            T result = this.constructor.newInstance(args);

            return result;
        }
        catch (Exception e) {}

        return null;
    }

}
