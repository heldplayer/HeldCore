
package me.heldplayer.util.HeldCore.reflection;

import java.lang.reflect.Constructor;
import java.util.logging.Level;

import me.heldplayer.util.HeldCore.Updater;

public class RConstructor<T> {

    protected final RClass<T> clazz;
    protected final Constructor<T> constructor;

    public RConstructor(RClass<T> clazz, Constructor<T> constructor) {
        this.clazz = clazz;
        this.constructor = constructor;
    }

    public T newInstance(Object... args) {
        try {
            T result = constructor.newInstance(args);

            Updater.log.log(Level.FINE, "Reflection: [" + this.clazz.clazz.getName() + "] Called constructor");
            return result;
        }
        catch (Exception e) {
            Updater.log.log(Level.WARNING, "Reflection: [" + this.clazz.clazz.getName() + "] Exception while calling constructor", e);
        }

        return null;
    }

}
