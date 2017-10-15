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
package com.github.rvesse.airline.parser.options;

import java.util.List;
import java.util.regex.Pattern;

import com.github.rvesse.airline.utils.Predicate;

import com.github.rvesse.airline.model.OptionMetadata;
import com.github.rvesse.airline.parser.AbstractParser;
import com.github.rvesse.airline.parser.ParseState;
import com.github.rvesse.airline.utils.AirlineUtils;
import com.github.rvesse.airline.utils.predicates.parser.AbbreviatedOptionFinder;
import com.github.rvesse.airline.utils.predicates.parser.OptionFinder;

/**
 * Abstract option parser that provides some useful helper methods to derived
 * classes
 */
public abstract class AbstractOptionParser<T> extends AbstractParser<T> implements OptionParser<T> {

    private static final Pattern SHORT_OPTIONS_PREFIX = Pattern.compile("-[^-].*");

    /**
     * Tries to find an option with the given name
     * 
     * @param state
     *            Current parser state
     * @param options
     *            Allowed options
     * @param name
     *            Name
     * @return Option if found, {@code null} otherwise
     */
    protected final OptionMetadata findOption(ParseState<T> state, List<OptionMetadata> options, final String name) {
        return findOption(state, options, name, null);
    }

    /**
     * Tries to find an option with the given name
     * 
     * @param state
     *            Current parser state
     * @param options
     *            Allowed options
     * @param name
     *            Name
     * @param defaultValue
     *            Default value to return if nothing found
     * @return Option if found, {@code defaultValue} otherwise
     */
    protected final OptionMetadata findOption(ParseState<T> state, List<OptionMetadata> options, final String name,
            OptionMetadata defaultValue) {
        Predicate<OptionMetadata> findOptionPredicate;
        if (state.getParserConfiguration().allowsAbbreviatedOptions()) {
            findOptionPredicate = new AbbreviatedOptionFinder(name, options);
        } else {
            findOptionPredicate = new OptionFinder(name);
        }

        return AirlineUtils.find(options, findOptionPredicate, defaultValue);
    }

    protected boolean hasShortNamePrefix(String name) {
        return SHORT_OPTIONS_PREFIX.matcher(name).matches();
    }
}