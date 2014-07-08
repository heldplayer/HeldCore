package net.specialattack.forge.core.reflection;

@SuppressWarnings("unchecked")
public final class ReflectionHelper {

    public static <T> RClass<? extends T> getClass(String classPath) {
        try {
            Class<T> clazz = (Class<T>) Class.forName(classPath);

            return new RClass<T>(clazz);
        } catch (Exception e) {
        }

        return null;
    }

    public static <T> RClass<T> getClass(Class<T> clazz) {
        return new RClass<T>(clazz);
    }

}
