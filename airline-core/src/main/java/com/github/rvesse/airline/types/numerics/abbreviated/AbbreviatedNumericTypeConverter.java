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
import java.util.Locale;

import com.github.rvesse.airline.types.numerics.ComplexNumericTypeConverter;
import com.github.rvesse.airline.types.numerics.NumericCandidate;

public abstract class AbbreviatedNumericTypeConverter extends ComplexNumericTypeConverter {
    
    protected abstract boolean isCaseSensitive();

    protected abstract Collection<String> getPermittedPrefixes();
    
    protected abstract Collection<String> getPermittedSuffixes();

    @Override
    protected NumericCandidate parse(String value) {
        String origValue = value;
        
        if (!isCaseSensitive()) 
            value = value.toLowerCase(Locale.ROOT);
        
        String prefix = null;
        for (String p : getPermittedPrefixes()) {
            if (value.startsWith(isCaseSensitive() ? p : p.toLowerCase(Locale.ROOT))) {
                prefix = p;
                value = value.substring(p.length());
                break;
            }
        }
        
        String suffix = null;
        for (String s : getPermittedSuffixes()) {
            if (value.endsWith(isCaseSensitive() ? s : s.toLowerCase(Locale.ROOT))) {
                suffix = s;
                value = value.substring(0, value.length() - s.length());
                break;
            }
        }
        
        return new NumericCandidate(prefix, value, suffix, origValue);
    }

    @Override
    protected long getMultiplier(NumericCandidate candidate) {
        return candidate.hasSuffix() ? getMultiplier(candidate.getSuffix()) : super.getMultiplier(candidate);
    }

    @Override
    protected int getRadix(NumericCandidate candidate) {
        return candidate.hasPrefix() ? getRadix(candidate.getPrefix()) : super.getRadix(candidate);
    }
    
    protected abstract long getMultiplier(String suffix);
    
    protected abstract int getRadix(String prefix);
    
    
}
