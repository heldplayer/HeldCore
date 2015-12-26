package net.specialattack.util;

/**
 * Simple interface for an object that takes values and returns nothing.
 *
 * @param <T>
 *         The type of valoue to take.
 */
public interface Consumer<T> {

    /**
     * Used to accept a value.
     *
     * @param value
     *         The value to accept.
     */
    void accept(T value);
}
