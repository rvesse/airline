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
package com.github.rvesse.airline.types.numerics;

import com.github.rvesse.airline.utils.StringUtils;

public class NumericCandidate {

    private final String prefix, value, suffix, origValue;
    
    public NumericCandidate(String value) {
        this(null, value, null, value);
    }
    
    public NumericCandidate(String prefix, String value, String suffix, String origValue) {
        this.prefix = prefix;
        this.value = value;
        this.suffix = suffix;
        this.origValue = origValue;
    }
    
    public boolean hasPrefix() {
        return StringUtils.isNotBlank(this.prefix);
    }
    
    public boolean hasSuffix() {
        return StringUtils.isNotBlank(this.suffix);
    }
    
    public String getPrefix() {
        return this.prefix;
    }
    
    public String getValue() {
        return this.value;
    }
    
    public String getSuffix() {
        return this.suffix;
    }
    
    public String getOriginalValue() {
        return this.origValue;
    }
    
    @Override
    public String toString() {
        return this.origValue;
    }
}
