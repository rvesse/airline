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

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import com.github.rvesse.airline.model.ArgumentsMetadata;
import com.github.rvesse.airline.model.OptionMetadata;
import com.github.rvesse.airline.model.PositionalArgumentMetadata;
import com.github.rvesse.airline.parser.ParseState;
import com.github.rvesse.airline.parser.errors.ParseArgumentsIllegalValueException;
import com.github.rvesse.airline.parser.errors.ParseInvalidRestrictionException;
import com.github.rvesse.airline.parser.errors.ParseOptionIllegalValueException;
import com.github.rvesse.airline.restrictions.AbstractCommonRestriction;
import com.github.rvesse.airline.types.DefaultTypeConverter;
import com.github.rvesse.airline.types.TypeConverter;
import com.github.rvesse.airline.utils.AirlineUtils;

public class AllowedValuesRestriction extends AbstractAllowedValuesRestriction {

    private Object currentState = null;
    private Set<Object> allowedValues = null;

    public AllowedValuesRestriction(String... rawValues) {
        super(CASE_SENSITIVE);
        this.rawValues.addAll(Arrays.asList(rawValues));
    }

    @Override
    public <T> void postValidate(ParseState<T> state, OptionMetadata option, Object value) {
        // Not enforced if no values specified
        if (this.rawValues.isEmpty())
            return;

        Set<Object> allowedValues = createAllowedValues(state, AbstractCommonRestriction.getOptionTitle(state, option), option.getJavaType(),
                option.getTypeConverterProvider().getTypeConverter(option, state));
        if (!allowedValues.contains(value)) {
            throw new ParseOptionIllegalValueException(AirlineUtils.first(option.getOptions()), AbstractCommonRestriction.getOptionTitle(state, option), value, allowedValues);
        }
    }

    protected synchronized <T> Set<Object> createAllowedValues(ParseState<T> state, String title, Class<?> type,
            TypeConverter converter) {
        // Re-use cached values if possible
        if (currentState == state) {
            return allowedValues;
        }

        // Convert values
        Set<Object> actualValues = new LinkedHashSet<Object>();
        if (converter == null)
            converter = new DefaultTypeConverter();
        for (String rawValue : this.rawValues) {
            try {
                actualValues.add(converter.convert(title, type, rawValue));
            } catch (Exception e) {
                throw new ParseInvalidRestrictionException(e,
                        "Unable to parse raw value '%s' in order to apply allowed values restriction", rawValue);
            }
        }

        // Cache for re-use
        currentState = state;
        this.allowedValues = actualValues;

        return actualValues;
    }

    @Override
    public <T> void postValidate(ParseState<T> state, ArgumentsMetadata arguments, Object value) {
        // Not enforced if no values specified
        if (this.rawValues.isEmpty())
            return;

        String title = getArgumentTitle(state, arguments);
        Set<Object> allowedValues = createAllowedValues(state, title, arguments.getJavaType(),
                arguments.getTypeConverterProvider().getTypeConverter(arguments, state));
        if (!allowedValues.contains(value)) {
            throw new ParseArgumentsIllegalValueException(title, value, allowedValues);
        }
    }

    @Override
    public <T> void postValidate(ParseState<T> state, PositionalArgumentMetadata arguments, Object value) {
        // Not enforced if no values specified
        if (this.rawValues.isEmpty())
            return;

        String title = arguments.getTitle();
        Set<Object> allowedValues = createAllowedValues(state, title, arguments.getJavaType(),
                arguments.getTypeConverterProvider().getTypeConverter(arguments, state));
        if (!allowedValues.contains(value)) {
            throw new ParseArgumentsIllegalValueException(title, value, allowedValues);
        }
    }

}
