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

import org.apache.commons.collections4.Predicate;

import com.github.rvesse.airline.model.OptionMetadata;
import com.github.rvesse.airline.parser.AbstractParser;
import com.github.rvesse.airline.parser.ParseState;
import com.github.rvesse.airline.parser.errors.ParseOptionMissingValueException;
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

    /**
     * Return true if the option has a short name prefix i.e. starts with a
     * single {@code -} character. This does not mean that it actually is a
     * short option since definitions of what are considered a short option will
     * vary by concrete implementation
     * 
     * @param name
     *            Option name
     * @return True if a short name prefix, false otherwise
     */
    protected boolean hasShortNamePrefix(String name) {
        return SHORT_OPTIONS_PREFIX.matcher(name).matches();
    }

    protected void noValueForOption(ParseState<T> state, OptionMetadata option) {
        state.getParserConfiguration().getErrorHandler()
                .handleError(new ParseOptionMissingValueException("No value received for option %s",
                        option.getTitle(0), AirlineUtils.first(option.getOptions())));
    }

    /**
     * Checks whether the next value is an arguments separator or option
     * 
     * @param state
     *            Parse State
     * @param allowedOptions
     *            Allowed options
     * @param argsSeparator
     *            Arguments separator
     * @param shortForm
     *            Whether to test only for short form, if {@code true} only consider the first character of
     *            {@code peekedToken}
     * @param peekedToken
     *            The peeked token to check whether it is the arguments separator or an option
     * @return True if the peeked token represents an arguments separator or an option
     */
    protected boolean isSeparatorOrOption(ParseState<T> state, List<OptionMetadata> allowedOptions, String argsSeparator, boolean shortForm, String peekedToken) {
        boolean hasSeparator = peekedToken.equals(argsSeparator);
        boolean foundNextOption = (shortForm ? findOption(state, allowedOptions, "-" + peekedToken.substring(0, 1))
                : findOption(state, allowedOptions, peekedToken)) != null;
        final boolean separatorOrOption = hasSeparator || foundNextOption;
        return separatorOrOption;
    }
}