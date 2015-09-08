package net.specialattack.util;

import com.google.common.collect.Lists;
import java.lang.reflect.Array;
import java.util.*;

public final class CollectionUtils {

    private CollectionUtils() {
    }

    public static <T> List<T> intersection(List<T> left, List<T> right) {
        List<T> mid = new LinkedList<T>(left);
        mid.removeAll(right);
        List<T> result = new ArrayList<T>(left);
        result.removeAll(mid);
        return result;
    }

    public static <T extends Comparable<T>> Set<T> intersectComparable(Set<T> left, Set<T> right) {
        List<T> mid = new LinkedList<T>(left);
        mid.removeAll(right);
        Set<T> result = new TreeSet<T>(left);
        result.removeAll(mid);
        return result;
    }

    public static <T> Set<T> intersection(Set<T> left, Set<T> right) {
        List<T> mid = new LinkedList<T>(left);
        mid.removeAll(right);
        Set<T> result = new HashSet<T>(left);
        result.removeAll(mid);
        return result;
    }

    public static <T> Queue<T> intersection(Queue<T> left, Queue<T> right) {
        List<T> mid = new LinkedList<T>(left);
        mid.removeAll(right);
        Queue<T> result = new LinkedList<T>(left);
        result.removeAll(mid);
        return result;
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] intersection(T[] left, T[] right) {
        List<T> mid = Lists.newArrayList(left);
        mid.removeAll(Lists.newArrayList(right));
        List<T> result = Lists.newArrayList(left);
        result.removeAll(mid);
        Class<T> clazz = (Class<T>) left.getClass().getComponentType();
        T[] resultArray = (T[]) Array.newInstance(clazz, result.size());
        return result.toArray(resultArray);
    }
}
