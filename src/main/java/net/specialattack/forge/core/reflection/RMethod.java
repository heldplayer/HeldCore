package net.specialattack.forge.core.reflection;

import java.lang.reflect.Method;

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

            return result;
        } catch (Exception e) {
        }

        return null;
    }

}
