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

import com.github.rvesse.airline.utils.PeekingIterator;
import com.github.rvesse.airline.utils.StringUtils;

import com.github.rvesse.airline.Context;
import com.github.rvesse.airline.model.OptionMetadata;
import com.github.rvesse.airline.parser.ParseState;
import com.github.rvesse.airline.utils.AirlineUtils;

/**
 * An options parser that expects the name and values to be white space
 * separated e.g. {@code --name value} but which allows for the values to be a
 * non-whitespace separated pair
 * <p>
 * So for example {@code --name foo=bar} would be treated as the values
 * {@code foo} and {@code bar} passed to the {@code --name} option. This parser
 * would also support {@code --name foo bar} and interpret them in the same way
 * and as such is a convenient hybrid of the {@link StandardOptionParser} and
 * the {@link ListValueOptionParser} for cases where you have arity 2 options
 * that users may either express the two values as separate values or in pair
 * style.
 * </p>
 * <p>
 * You can also omit the whitespace between the name and the values when using a
 * single character name of the option similar to how the
 * {@link ClassicGetOptParser} works. For example {@code -nfoo=bar} is
 * equivalent to our previous example assuming that {@code -n} is an alternative
 * name for the same option as {@code --name}.
 * </p>
 * <p>
 * The default separator for values is {@code =} but this can be configured as
 * desired.
 * </p>
 *
 */
public class MaybePairValueOptionParser<T> extends AbstractOptionParser<T> {

    private static final char DEFAULT_SEPARATOR = '=';
    private final char separator;

    public MaybePairValueOptionParser() {
        this(DEFAULT_SEPARATOR);
    }

    public MaybePairValueOptionParser(char separator) {
        if (Character.isWhitespace(separator))
            throw new IllegalArgumentException("Pair separator character cannot be a whitespace character");
        this.separator = separator;
    }

    protected final List<String> getValues(String list) {
        return AirlineUtils.arrayToList(StringUtils.split(list, new String(new char[] { this.separator }), 2));
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
        // Only works with arity 2 options
        if (option.getArity() != 2)
            return null;

        tokens.next();
        state = state.pushContext(Context.OPTION).withOption(option);

        String maybePair = noSep ? name.substring(2) : null;
        if (maybePair == null) {
            // Can't parse pair value if there are no further tokens
            if (!tokens.hasNext())
                return state;

            // Consume the value immediately, this option parser will now
            // either succeed to parse the option or will error
            maybePair = tokens.next();
        }

        // Parse value as a pair
        List<String> pairValues = getValues(maybePair);
        if (pairValues.size() < 2) {
            // If we didn't get a pair as x=y then need to grab the second half
            // of the pair from the next token
            if (!tokens.hasNext())
                return state;

            // If the next thing is actually an option abort
            String peekedToken = tokens.peek();
            if (findOption(state, allowedOptions, peekedToken) != null)
                return state;

            pairValues.add(tokens.next());
        }

        // Parse the values and assign to option
        for (String value : pairValues) {
            state = state.withOptionValue(option, value);
        }

        state = state.popContext();

        return state;
    }

}
