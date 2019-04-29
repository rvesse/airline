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
package com.github.rvesse.airline.utils.predicates;

import java.util.Locale;

public class PrefixMatcher extends AbstractLocaleSensitiveStringFinder {

    private final String[] prefixes;
    private final boolean ignoreCase;

    public PrefixMatcher(boolean ignoreCase, Locale locale, String... prefixes) {
        super(locale);
        this.ignoreCase = ignoreCase;
        this.prefixes = prefixes;
        
        if (ignoreCase) {
            for (int i = 0; i < this.prefixes.length; i++) {
                this.prefixes[i] = this.prefixes[i].toLowerCase(locale);
            }
        }
    }

    @Override
    public boolean evaluate(String str) {
        for (String prefix : this.prefixes) {
            if (str.length() < prefix.length())
                return false;

            String strPrefix = str.substring(0, prefix.length());
            if (ignoreCase)
                strPrefix = strPrefix.toLowerCase(this.locale);

            if (this.collator.compare(strPrefix, prefix) == 0)
                return true;
        }
        return false;
    }

}
