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
package com.github.rvesse.airline.restrictions;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.lang3.tuple.Pair;

import com.github.rvesse.airline.model.ArgumentsMetadata;
import com.github.rvesse.airline.model.OptionMetadata;
import com.github.rvesse.airline.parser.ParseState;

public abstract class AbstractCommonRestriction implements OptionRestriction, ArgumentsRestriction {

    @Override
    public <T> void finalValidate(ParseState<T> state, OptionMetadata option) {
        // Does no validation
    }
    
    @Override
    public <T> void postValidate(ParseState<T> state, OptionMetadata option, Object value) {
        // Does no validation
    }    

    @Override
    public <T> void preValidate(ParseState<T> state, OptionMetadata option, String value) {
        // Does no validation
    }

    @Override
    public <T> void preValidate(ParseState<T> state, ArgumentsMetadata arguments, String value) {
        // Does no validation
    }
    
    @Override
    public <T> void postValidate(ParseState<T> state, ArgumentsMetadata arguments, Object value) {
        // Does no validation
    }

    @Override
    public <T> void finalValidate(ParseState<T> state, ArgumentsMetadata arguments) {
        // Does no validation
    }
    
    public static <T> String getOptionTitle(ParseState<T> state, final OptionMetadata option) {
        return option.getTitle(state.getOptionValuesSeen(option) % option.getArity());
    }

    public static <T> String getArgumentTitle(ParseState<T> state, ArgumentsMetadata arguments) {
        // Use empty string if no appropriate meta-data available
        if (arguments == null || arguments.getTitle().size() == 0 || state == null)
            return "";

        // If number of arguments parsed so far is less than the number of
        // titles available grab the next title
        if (state.getParsedArguments().size() < arguments.getTitle().size())
            return arguments.getTitle().get(state.getParsedArguments().size());

        // If number of arguments passed so far is greater than the number of
        // titles available just use the last title
        return arguments.getTitle().get(arguments.getTitle().size() - 1);
    }

    public static <T> String getArgumentTitle(ArgumentsMetadata arguments, int argIndex) {
        // Use empty string if no appropriate meta-data available
        if (arguments == null || arguments.getTitle().size() == 0)
            return "";

        // If index has a title available grab it
        if (argIndex < arguments.getTitle().size())
            return arguments.getTitle().get(argIndex);

        // If index is greater than available titles just use the last title
        return arguments.getTitle().get(arguments.getTitle().size() - 1);

    }
}
