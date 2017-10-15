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

import com.github.rvesse.airline.utils.Predicate;

public class CollectionUtils {


    public static <T> T find(final Iterable<T> collection, final Predicate<? super T> predicate) {
        if (collection == null) return null;
        for (T t : collection) {
            if (predicate.evaluate(t)) {
                return t;
            }
        }
        return null;
    }
    
    public static <C> boolean exists(final Iterable<C> input, final Predicate<? super C> predicate) {
        if (input == null) return false;
        for (C t : input) {
            if (predicate.evaluate(t)) {
                return true;
            }
        }
        return false;
    }
    
    public static <O> Collection<O> select(final Iterable<? extends O> inputCollection,
            final Predicate<? super O> predicate) {
        Collection<O> list = new ArrayList<O>();
        for (O o : inputCollection) {
            if (predicate.evaluate(o)) {
                list.add(o);
            }
        }
        return list;
    
    }
    
    public static <C> int countMatches(final Iterable<C> input, final Predicate<? super C> predicate) {
        if (input == null) return 0;
        int i = 0;
        for (C t : input) {
            if (predicate.evaluate(t)) {
                i++;
            }
        }
        return i;
    }
    
    public static <C> boolean addAll(final Collection<C> collection, final Iterable<? extends C> iterable) {
        if (iterable instanceof Collection<?>) {
            return collection.addAll((Collection<? extends C>) iterable);
        }
        else {
            boolean changed = false;
            for (C i : iterable) {
                collection.add(i);
                changed = true;
            }
            return changed;
        }
    }
    
}

