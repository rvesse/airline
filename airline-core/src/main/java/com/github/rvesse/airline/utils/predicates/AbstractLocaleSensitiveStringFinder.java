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

import java.text.Collator;
import java.util.Locale;

import org.apache.commons.collections4.Predicate;

public abstract class AbstractLocaleSensitiveStringFinder implements Predicate<String> {

    protected final Locale locale;
    protected final Collator collator;

    public AbstractLocaleSensitiveStringFinder(Locale locale) {
        if (locale == null)
            throw new NullPointerException("locale cannot be null");
        this.locale = locale;
        this.collator = Collator.getInstance(locale);
    }

}