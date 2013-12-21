
package me.heldplayer.util.HeldCore.reflection;

import java.lang.reflect.Method;
import java.util.logging.Level;

import me.heldplayer.util.HeldCore.Objects;

@SuppressWarnings("unchecked")
public class RMethod<T, V> {

    protected final RClass<T> clazz;
    protected final Method method;

    public RMethod(RClass<T> clazz, Method method) {
        this.clazz = clazz;
        this.method = method;
    }

    public V callStatic(Object... args) {
        return this.call(null, args);
    }

    public V call(T classInstance, Object... args) {
        try {
            V result = (V) this.method.invoke(classInstance, args);

            //Objects.log.log(Level.FINE, "Reflection: [" + this.clazz.clazz.getName() + ":" + this.method.getName() + "] Called method");
            return result;
        }
        catch (Exception e) {
            Objects.log.log(Level.WARNING, "Reflection: [" + this.clazz.clazz.getName() + ":" + this.method.getName() + "] Exception while calling method", e);
        }

        return null;
    }

}
