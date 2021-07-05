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

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections4.iterators.PeekingIterator;
import org.apache.commons.lang3.StringUtils;

import com.github.rvesse.airline.Context;
import com.github.rvesse.airline.model.OptionMetadata;
import com.github.rvesse.airline.parser.ParseState;
import com.github.rvesse.airline.parser.errors.ParseOptionMissingValueException;
import com.github.rvesse.airline.parser.errors.ParseOptionUnexpectedException;

/**
 * An options parser that requires the values to be a
 * non-whitespace separated list
 * <p>
 * So for example {@code --name foo,bar} would be treated as the values
 * {@code foo} and {@code bar} passed to the {@code --name} option. This parser
 * differs from the {@link StandardOptionParser} in that the standard parser
 * would treat {@code foo,bar} as a single value passed to the name option. This
 * parser expects that the list it receives contains the correct number of items
 * for the arity of the option, or an exact multiple thereof and if not produces
 * an error
 * </p>
 * <p>
 * You can also omit the whitespace between the name and the value list when
 * using a single character name of the option similar to how the
 * {@link ClassicGetOptParser} works. For example {@code -nfoo,bar} is
 * equivalent to our previous example assuming that {@code -n} is an alternative
 * name for the same option as {@code --name}.
 * </p>
 * <p>
 * The default separator for values is {@code ,} but this can be configured as
 * desired.
 * </p>
 *
 */
public class ListValueOptionParser<T> extends AbstractOptionParser<T> {

    private static final char DEFAULT_SEPARATOR = ',';
    private final char separator;

    public ListValueOptionParser() {
        this(DEFAULT_SEPARATOR);
    }

    public ListValueOptionParser(char separator) {
        if (Character.isWhitespace(separator))
            throw new IllegalArgumentException("List separator character cannot be a whitespace character");
        this.separator = separator;
    }

    protected final List<String> getValues(String list) {
        List<String> values = new ArrayList<>();
        for (String value : StringUtils.split(list, this.separator)) {
            values.add(value);
        }
        return values;
    }

    @Override
    public ParseState<T> parseOptions(PeekingIterator<String> tokens, ParseState<T> state,
            List<OptionMetadata> allowedOptions) {
        String name = tokens.peek();
        boolean noSep = false;
        OptionMetadata option = findOption(state, allowedOptions, name);
        if (option == null) {
            // Check if we are looking at a maven style -Pa,b,c argument
            if (hasShortNamePrefix(name) && name.length() > 2) {
                String shortName = name.substring(0, 2);
                option = findOption(state, allowedOptions, shortName);
                noSep = option != null;
            }

            if (!noSep)
                return null;
        }

        tokens.next();
        state = state.pushContext(Context.OPTION).withOption(option);

        String list = noSep ? name.substring(2) : null;
        if (option.getArity() == 0) {
            // Zero arity option, consume token and continue
            // Determine what value to set
            // This will depend on whether flag negation is enabled and if so
            // whether the option name used started with the configured negation
            // prefix
            String rawBooleanValue = state.getParserConfiguration().allowsFlagNegation()
                    && StringUtils.startsWith(name, state.getParserConfiguration().getFlagNegationPrefix())
                            ? Boolean.FALSE.toString()
                            : Boolean.TRUE.toString();
            state = state.withOptionValue(option, rawBooleanValue).popContext();
        } else {
            if (list == null) {
                // Can't parse list value if there are no further tokens
                if (!tokens.hasNext())
                    return state;
                
                // Can't parse list value if the next value is another option/arguments separator
                if (isSeparatorOrOption(state, allowedOptions, state.getParserConfiguration().getArgumentsSeparator(), false, tokens.peek())) {
                    noValueForOption(state, option);
                    return state;
                }

                // Consume the value immediately, this option parser will now
                // either succeed to parse the option or will error
                list = tokens.next();
            }

            // Parse value as a list
            // Check the size of the list
            // Must receive either the exact arity of the option OR an exact
            // multiple of the arity of the option
            List<String> listValues = getValues(list);
            if (listValues.size() < option.getArity()) {
                // Too few arguments
                state.getParserConfiguration().getErrorHandler().handleError(new ParseOptionMissingValueException(
                        "Too few option values received for option %s in list value '%s' (%d values expected but only found %d)",
                        option.getOptions().iterator().next(), option.getOptions().iterator().next(), list, option.getArity(),
                        listValues.size()));
                return state;
            }
            if (listValues.size() > option.getArity() && listValues.size() % option.getArity() != 0) {
                // Too many arguments
                state.getParserConfiguration().getErrorHandler().handleError(new ParseOptionUnexpectedException(
                        "Too many option values received for option %s in list value '%s' (%d values expected but found %d)",
                        option.getOptions().iterator().next(), list, option.getArity(), listValues.size()));
                return state;
            }

            // Parse individual values and assign to option
            for (String value : listValues) {
                state = state.withOptionValue(option, value);
            }

            state = state.popContext();

        }
        return state;
    }

}
