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

public class SuffixMatcher extends AbstractLocaleSensitiveStringFinder {

    private final String[] suffixes;
    private final boolean ignoreCase;

    public SuffixMatcher(boolean ignoreCase, Locale locale, String... suffixes) {
        super(locale);
        this.ignoreCase = ignoreCase;
        this.suffixes = suffixes;
        
        if (ignoreCase) {
            for (int i = 0; i < this.suffixes.length; i++) {
                this.suffixes[i] = this.suffixes[i].toLowerCase(locale);
            }
        }
    }

    @Override
    public boolean evaluate(String str) {
        for (String suffix : this.suffixes) {
            if (str.length() < suffix.length())
                return false;

            String strSuffix = str.substring(str.length() - suffix.length());
            if (ignoreCase)
                strSuffix = strSuffix.toLowerCase(this.locale);

            if (this.collator.compare(strSuffix, suffix) == 0)
                return true;
        }
        return false;
    }

}
