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

import org.apache.commons.collections4.IterableUtils;

import com.github.rvesse.airline.model.ArgumentsMetadata;
import com.github.rvesse.airline.model.OptionMetadata;
import com.github.rvesse.airline.parser.ParseState;
import com.github.rvesse.airline.parser.errors.ParseArgumentsIllegalValueException;
import com.github.rvesse.airline.parser.errors.ParseOptionIllegalValueException;
import com.github.rvesse.airline.restrictions.AbstractCommonRestriction;
import com.github.rvesse.airline.utils.predicates.LocaleSensitiveStringFinder;

public class AllowedRawValuesRestriction extends AbstractAllowedValuesRestriction {

    private final Locale locale;

    public AllowedRawValuesRestriction(boolean ignoreCase, Locale locale, String... values) {
        super(ignoreCase);
        if (locale == null)
            locale = Locale.ENGLISH;
        this.locale = locale;
        for (String value : values) {
            if (ignoreCase)
                value = value.toLowerCase(locale);
            rawValues.add(value);
        }
    }

    @Override
    public <T> void preValidate(ParseState<T> state, OptionMetadata option, String value) {
        // Not enforced if no values specified
        if (rawValues.isEmpty())
            return;

        // Normalize to lower case when ignoring case
        if (this.caseInsensitive) {
            value = value.toLowerCase(locale);
        }

        // Check in list of values
        if (!IterableUtils.matchesAny(this.rawValues, new LocaleSensitiveStringFinder(value, this.locale)))
            throw new ParseOptionIllegalValueException(option.getTitle(), value, asObjects(rawValues));
    }

    @Override
    public <T> void preValidate(ParseState<T> state, ArgumentsMetadata arguments, String value) {
        // Not enforced if no values specified
        if (rawValues.isEmpty())
            return;

        // Normalize to lower case when ignoring case
        if (this.caseInsensitive) {
            value = value.toLowerCase(locale);
        }

        // Check in list of values
        if (!IterableUtils.matchesAny(this.rawValues, new LocaleSensitiveStringFinder(value, this.locale))) {
            throw new ParseArgumentsIllegalValueException(AbstractCommonRestriction.getArgumentTitle(state, arguments),
                    value, asObjects(rawValues));
        }
    }
}
