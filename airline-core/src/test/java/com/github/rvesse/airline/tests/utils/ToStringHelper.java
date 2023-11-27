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
package com.github.rvesse.airline.tests.utils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ToStringHelper {

    private final Map<String, Object> data = new LinkedHashMap<>();
    private final Class<?> type;
    
    public ToStringHelper(Class<?> type) {
        this.type = type;
    }
    
    public ToStringHelper add(String key, Object value) {
        this.data.put(key, value);
        return this;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.type.getCanonicalName());
        builder.append('\n').append('{').append('\n');
        for (Entry<String, Object> item : this.data.entrySet()) {
            builder.append(' ');
            builder.append(item.getKey()).append('=');
            builder.append(item.getValue());
            builder.append('\n');
        }
        builder.append('\n').append('}');
        return builder.toString();
    }
}
