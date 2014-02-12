
package net.specialattack.util;

import java.util.Collection;
import java.util.TreeMap;
import java.util.TreeSet;

public class Table<K, V, W> {

    private final TreeMap<K, Value<V, W>> entries;

    public Table() {
        this.entries = new TreeMap<K, Value<V, W>>();
    }

    public int size() {
        return this.entries.size();
    }

    public void insert(K key, V value1, W value2) {
        if (this.entries.containsKey(key)) {
            return;
        }
        this.entries.put(key, new Value<V, W>(value1, value2));
    }

    public boolean containsKey(K key) {
        return this.entries.containsKey(key);
    }

    public boolean containsValue1(V value1) {
        for (K key : this.entries.keySet()) {
            if (this.entries.get(key).getValue1().equals(value1)) {
                return true;
            }
        }

        return false;
    }

    public boolean containsValue2(W value2) {
        for (K key : this.entries.keySet()) {
            if (this.entries.get(key).getValue2().equals(value2)) {
                return true;
            }
        }

        return false;
    }

    public Value<V, W> deleteEntry(K key) {
        return this.entries.remove(key);
    }

    public Value<V, W> getValue(K key) {
        return this.entries.get(key);
    }

    public V getValue1(K key) {
        Value<V, W> entry = this.entries.get(key);

        return entry != null ? this.entries.get(key).getValue1() : null;
    }

    public W getValue2(K key) {
        Value<V, W> entry = this.entries.get(key);

        return entry != null ? this.entries.get(key).getValue2() : null;
    }

    public K getKey1(V value1) {
        for (K key : this.entries.keySet()) {
            if (this.entries.get(key).getValue1().equals(value1)) {
                return key;
            }
        }

        return null;
    }

    public K getKey2(W value2) {
        for (K key : this.entries.keySet()) {
            if (this.entries.get(key).getValue2().equals(value2)) {
                return key;
            }
        }

        return null;
    }

    public Collection<Entry<K, V, W>> getEntries() {
        TreeSet<Entry<K, V, W>> result = new TreeSet<Entry<K, V, W>>();

        for (java.util.Map.Entry<K, Value<V, W>> entry : this.entries.entrySet()) {
            result.add(new Entry<K, V, W>(entry.getKey(), entry.getValue().value1, entry.getValue().value2));
        }

        return result;
    }

    public static class Value<V, W> {

        private V value1;
        private W value2;

        Value(V value1, W value2) {
            this.value1 = value1;
            this.value2 = value2;
        }

        public V getValue1() {
            return this.value1;
        }

        public W getValue2() {
            return this.value2;
        }

        public void setValue1(V value) {
            this.value1 = value;
        }

        public void setValue2(W value) {
            this.value2 = value;
        }

    }

    public static class Entry<K, V, W> {

        private final K key;
        private final V value1;
        private final W value2;

        Entry(K key, V value1, W value2) {
            this.key = key;
            this.value1 = value1;
            this.value2 = value2;
        }

        public K getKey() {
            return this.key;
        }

        public V getValue1() {
            return this.value1;
        }

        public W getValue2() {
            return this.value2;
        }

    }

}
