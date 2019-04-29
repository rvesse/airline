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
package com.github.rvesse.airline.types.numerics.abbreviated;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

public class MapAbbreviatedNumericTypeConverter extends AbbreviatedNumericTypeConverter {

    private final boolean caseSensitive;
    private final Map<String, Integer> prefixes = new HashMap<>();
    private final Map<String, Long> suffixes = new HashMap<>();

    public MapAbbreviatedNumericTypeConverter(boolean caseSensitive, Map<String, Integer> prefixes,
            Map<String, Long> suffixes) {
        this.caseSensitive = caseSensitive;
        if (this.caseSensitive) {
            this.prefixes.putAll(prefixes);
            this.suffixes.putAll(suffixes);
        } else {
            for (Entry<String, Integer> e : prefixes.entrySet()) {
                this.prefixes.put(e.getKey().toLowerCase(Locale.ROOT), e.getValue());
            }
            for (Entry<String, Long> e : suffixes.entrySet()) {
                this.suffixes.put(e.getKey().toLowerCase(Locale.ROOT), e.getValue());
            }
        }
    }
    
    @Override
    protected boolean isCaseSensitive() {
        return this.caseSensitive;
    }

    @Override
    protected Collection<String> getPermittedPrefixes() {
        return this.prefixes.keySet();
    }

    @Override
    protected Collection<String> getPermittedSuffixes() {
        return this.suffixes.keySet();
    }

    @Override
    protected long getMultiplier(String suffix) {
        suffix = this.caseSensitive ? suffix : suffix.toLowerCase(Locale.ROOT);
        Long m = null;
        if (this.suffixes.containsKey(suffix)) {
            m = this.suffixes.get(suffix);
        } else {
            m = 1l;
        }
        if (m == null)
            m = 1l;
        return m;
    }

    @Override
    protected int getRadix(String prefix) {
        prefix = this.caseSensitive ? prefix : prefix.toLowerCase(Locale.ROOT);
        Integer r = null;
        if (this.prefixes.containsKey(prefix)) {
            r = this.prefixes.get(prefix);
        } else {
            r = 10;
        }
        if (r == null)
            r = 10;
        return r;
    }

}
