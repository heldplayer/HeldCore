package net.specialattack.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.TreeMap;

public class Table<K, V1, V2> {

    private final TreeMap<K, Table.Value<V1, V2>> entries;

    public Table() {
        this.entries = new TreeMap<K, Table.Value<V1, V2>>();
    }

    public int size() {
        return this.entries.size();
    }

    public void insert(K key, V1 value1, V2 value2) {
        if (this.entries.containsKey(key)) {
            return;
        }
        this.entries.put(key, new Table.Value<V1, V2>(value1, value2));
    }

    public boolean containsKey(K key) {
        return this.entries.containsKey(key);
    }

    public boolean containsValue1(V1 value1) {
        for (K key : this.entries.keySet()) {
            if (this.entries.get(key).getValue1().equals(value1)) {
                return true;
            }
        }

        return false;
    }

    public boolean containsValue2(V2 value2) {
        for (K key : this.entries.keySet()) {
            if (this.entries.get(key).getValue2().equals(value2)) {
                return true;
            }
        }

        return false;
    }

    public Table.Value<V1, V2> deleteEntry(K key) {
        return this.entries.remove(key);
    }

    public Table.Value<V1, V2> getValue(K key) {
        return this.entries.get(key);
    }

    public V1 getValue1(K key) {
        Table.Value<V1, V2> entry = this.entries.get(key);

        return entry != null ? this.entries.get(key).getValue1() : null;
    }

    public V2 getValue2(K key) {
        Table.Value<V1, V2> entry = this.entries.get(key);

        return entry != null ? this.entries.get(key).getValue2() : null;
    }

    public K getKey1(V1 value1) {
        for (K key : this.entries.keySet()) {
            if (this.entries.get(key).getValue1().equals(value1)) {
                return key;
            }
        }

        return null;
    }

    public K getKey2(V2 value2) {
        for (K key : this.entries.keySet()) {
            if (this.entries.get(key).getValue2().equals(value2)) {
                return key;
            }
        }

        return null;
    }

    public Collection<Table.Entry<K, V1, V2>> getEntries() {
        HashSet<Table.Entry<K, V1, V2>> result = new HashSet<Table.Entry<K, V1, V2>>();

        for (java.util.Map.Entry<K, Table.Value<V1, V2>> entry : this.entries.entrySet()) {
            result.add(new Table.Entry<K, V1, V2>(entry.getKey(), entry.getValue().value1, entry.getValue().value2));
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

        public void setValue1(V value) {
            this.value1 = value;
        }

        public W getValue2() {
            return this.value2;
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
