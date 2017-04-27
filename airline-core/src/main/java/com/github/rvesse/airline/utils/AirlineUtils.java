/**
 * Copyright (C) 2010-16 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.rvesse.airline.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.Predicate;

public class AirlineUtils {

    public static <T> List<T> arrayToList(T[] array) {
        return IteratorUtils.toList(IteratorUtils.arrayIterator(array));
    }

    public static <T> T first(Iterable<T> iterable) {
        return first(iterable.iterator(), null);
    }

    public static <T> T first(Iterator<T> iterator) {
        return first(iterator, null);
    }

    public static <T> T first(Iterable<T> iterable, T defaultValue) {
        return first(iterable.iterator(), defaultValue);
    }

    public static <T> T first(Iterator<T> iter, T defaultValue) {
        if (iter.hasNext()) {
            return iter.next();
        } else {
            return defaultValue;
        }
    }

    public static <T> T last(Iterable<T> iterable) {
        return last(iterable.iterator(), null);
    }

    public static <T> T last(Iterator<T> iterator) {
        return last(iterator, null);
    }

    public static <T> T last(Iterable<T> iterable, T defaultValue) {
        return last(iterable.iterator(), defaultValue);
    }

    public static <T> T last(Iterator<T> iter, T defaultValue) {
        T value = defaultValue;
        while (iter.hasNext()) {
            value = iter.next();
        }
        return value;
    }

    public static <T> List<T> singletonList(T item) {
        List<T> list = new ArrayList<T>();
        list.add(item);
        return list;
    }

    public static <T> Set<T> singletonSet(T item) {
        Set<T> set = new HashSet<T>();
        set.add(item);
        return set;
    }

    public static <K, V> Map<K, V> singletonMap(K key, V value) {
        Map<K, V> map = new HashMap<K, V>();
        map.put(key, value);
        return map;
    }

    public static <T> Set<T> intersection(Set<T> a, Set<T> b) {
        Set<T> intersection = new HashSet<T>();
        for (T item : a) {
            if (b.contains(item))
                intersection.add(item);
        }
        return intersection;
    }

    public static <T> List<T> listCopy(Collection<T> collection) {
        if (collection == null)
            return new ArrayList<T>();
        return new ArrayList<T>(collection);
    }

    public static <T> List<T> listCopy(Iterable<T> iterable) {
        if (iterable == null)
            return new ArrayList<T>();
        return IteratorUtils.toList(iterable.iterator());
    }

    public static <T> List<T> unmodifiableListCopy(Collection<T> collection) {
        if (collection == null)
            return Collections.emptyList();
        return ListUtils.unmodifiableList(new ArrayList<T>(collection));
    }

    public static <T> List<T> unmodifiableListCopy(Iterable<T> iterable) {
        if (iterable == null)
            return Collections.emptyList();
        return ListUtils.unmodifiableList(IteratorUtils.toList(iterable.iterator()));
    }

    public static <T> List<T> unmodifiableListCopy(T[] array) {
        if (array == null)
            return Collections.emptyList();
        return ListUtils.unmodifiableList(arrayToList(array));
    }

    public static <K, V> Map<K, V> unmodifiableMapCopy(Map<K, V> map) {
        if (map == null)
            return Collections.emptyMap();
        return Collections.unmodifiableMap(new LinkedHashMap<K, V>(map));
    }

    public static <T> Set<T> unmodifiableSetCopy(Iterable<T> iterable) {
        if (iterable == null)
            return Collections.emptySet();
        LinkedHashSet<T> set = new LinkedHashSet<T>();
        Iterator<T> iter = iterable.iterator();
        while (iter.hasNext()) {
            set.add(iter.next());
        }
        return Collections.unmodifiableSet(set);
    }

    public static <T> Set<T> unmodifiableSetCopy(Set<T> set) {
        if (set == null)
            return Collections.emptySet();
        return Collections.unmodifiableSet(new LinkedHashSet<T>(set));
    }

    public static <T> T find(Iterable<T> collection, Predicate<T> predicate, T defaultValue) {
        if (collection == null)
            return defaultValue;
        T value = CollectionUtils.find(collection, predicate);
        if (value == null)
            return defaultValue;
        return value;
    }

    /**
     * Formats the range for display
     * 
     * @param min
     *            Minimum (may be null for no minimum)
     * @param minInclusive
     *            Whether the minimum is inclusive
     * @param max
     *            Maximum (may be null for no maximum)
     * @param maxInclusive
     *            Whether the maximum is inclusive
     * @return Human readable range
     */
    public static String toRangeString(Object min, boolean minInclusive, Object max, boolean maxInclusive) {
        StringBuilder builder = new StringBuilder();

        if (min != null) {
            if (max != null) {
                // min < value < max
                builder.append(min);
                builder.append(minInclusive ? " <=" : " <");
                builder.append(" value ");
            } else {
                // value > min
                builder.append("value ");
                builder.append(minInclusive ? ">= " : ">");
                builder.append(min);
            }
        }
        if (max != null) {
            // [min <] value < max
            builder.append(maxInclusive ? "<= " : "< ");
            builder.append(max);
        }

        return builder.toString();
    }

    public static String toOrdinal(int value) {
        StringBuilder builder = new StringBuilder();
        builder.append(Integer.toString(value));

        switch (value % 100) {
        case 11:
        case 12:
        case 13:
            builder.append("th");
            break;
        default:
            switch (value % 10) {
            case 1:
                builder.append("st");
                break;
            case 2:
                builder.append("nd");
                break;
            case 3:
                builder.append("rd");
                break;
            default:
                builder.append("th");
                break;
            }
        }
        
        return builder.toString();
    }
    
    public static Integer[] toObject(final int[] array) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return new Integer[] {};
        }
        final Integer[] result = new Integer[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = Integer.valueOf(array[i]);
        }
        return result;
    }
    
    private static class ImmutableEntry<K,V> implements Map.Entry<K, V> {
        private final K key;
        private final V value;
        
        public ImmutableEntry(K key, V value) {
            super();
            this.key = key;
            this.value = value;
        }
        
        @Override
        public K getKey() {
            return key;
        }
        @Override
        public V getValue() {
            return value;
        }
        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException("setValue is not yet supported");
        }
    }
    public static <K,V> Map.Entry<K, V> pairOf(K k, V v) {
        return new ImmutableEntry<K,V>(k, v);
    }
}
