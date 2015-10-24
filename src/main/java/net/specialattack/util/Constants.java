package net.specialattack.util;

public final class Constants {

    public static boolean has(int flags, int flag) {
        return (flags & flag) == flag;
    }

    private Constants() {
    }
}
