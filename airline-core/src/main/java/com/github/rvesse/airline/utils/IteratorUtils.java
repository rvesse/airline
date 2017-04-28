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
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class IteratorUtils {
    
    @SafeVarargs
    public static <E> Iterator<E> arrayIterator(final E... array) {
        return Arrays.asList(array).iterator();
    }
    
    public static <E> List<E> toList(final Iterator<? extends E> iterator) {
        List<E> list = new ArrayList<E>();
        while(iterator.hasNext()) {
            list.add(iterator.next());
        }
        return list;
    }

}
