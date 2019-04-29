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
package com.github.rvesse.airline.restrictions.common;

import java.util.Locale;

/**
 * An abstract string restriction that needs a locale and case sensitivity flag
 * 
 * @author rvesse
 *
 */
public abstract class AbstractLocaleAndCaseStringRestriction extends AbstractStringRestriction {

    protected final Locale locale;
    protected final boolean ignoreCase;

    /**
     * Creates the restriction
     * 
     * @param ignoreCase
     *            Whether to ignore case
     * @param locale
     *            Locale for comparisons
     */
    public AbstractLocaleAndCaseStringRestriction(boolean ignoreCase, Locale locale) {
        this.ignoreCase = ignoreCase;
        if (locale == null)
            locale = Locale.ENGLISH;
        this.locale = locale;
    }
}
