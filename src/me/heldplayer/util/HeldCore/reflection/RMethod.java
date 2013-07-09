
package me.heldplayer.util.HeldCore.reflection;

import java.lang.reflect.Method;
import java.util.logging.Level;

import me.heldplayer.util.HeldCore.Updater;

@SuppressWarnings("unchecked")
public class RMethod<T, V> {

    protected final RClass<T> clazz;
    protected final Method method;

    public RMethod(RClass<T> clazz, Method method) {
        this.clazz = clazz;
        this.method = method;
    }

    public V callStatic(Object... args) {
        return call(null, args);
    }

    public V call(T classInstance, Object... args) {
        try {
            V result = (V) method.invoke(classInstance, args);

            Updater.log.log(Level.FINE, "Reflection: [" + this.clazz.clazz.getName() + ":" + this.method.getName() + "] Called method");
            return result;
        }
        catch (Exception e) {
            Updater.log.log(Level.WARNING, "Reflection: [" + this.clazz.clazz.getName() + ":" + this.method.getName() + "] Exception while calling method", e);
        }

        return null;
    }

}
