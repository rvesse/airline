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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import com.github.rvesse.airline.model.ArgumentsMetadata;
import com.github.rvesse.airline.model.OptionMetadata;
import com.github.rvesse.airline.model.PositionalArgumentMetadata;
import com.github.rvesse.airline.parser.ParseState;
import com.github.rvesse.airline.parser.errors.ParseArgumentsIllegalValueException;
import com.github.rvesse.airline.parser.errors.ParseOptionIllegalValueException;
import com.github.rvesse.airline.restrictions.AbstractCommonRestriction;

public class AllowedEnumValuesRestriction extends AbstractAllowedValuesRestriction {

    public AllowedEnumValuesRestriction(Class<? extends Enum<?>> cls) {
        super(true);
        this.rawValues.addAll(getValues(cls));
    }

    private Collection<String> getValues(Class<? extends Enum<?>> cls) {
        if (cls.isEnum()) {
            List<String> values = new ArrayList<>();
            for (Enum<?> member : cls.getEnumConstants()) {
                values.add(member.name());
            }
            return values;
        }
        return Collections.emptySet();
    }

    @Override
    public <T> void preValidate(ParseState<T> state, OptionMetadata option, String value) {
        // Not enforced if no values specified
        if (rawValues.isEmpty())
            return;

        // Check in list of values
        if (!this.rawValues.contains(value))
            throw new ParseOptionIllegalValueException(option.getTitle(), value, asObjects(rawValues));
    }

    @Override
    public <T> void preValidate(ParseState<T> state, ArgumentsMetadata arguments, String value) {
        // Not enforced if no values specified
        if (rawValues.isEmpty())
            return;

        // Check in list of values
        if (!this.rawValues.contains(value)) {
            throw new ParseArgumentsIllegalValueException(AbstractCommonRestriction.getArgumentTitle(state, arguments), value, asObjects(rawValues));
        }
    }

    @Override
    public <T> void preValidate(ParseState<T> state, PositionalArgumentMetadata arguments, String value) {
        // Not enforced if no values specified
        if (rawValues.isEmpty())
            return;

        // Check in list of values
        if (!this.rawValues.contains(value)) {
            throw new ParseArgumentsIllegalValueException(arguments.getTitle(), value, asObjects(rawValues));
        }
    }
}
