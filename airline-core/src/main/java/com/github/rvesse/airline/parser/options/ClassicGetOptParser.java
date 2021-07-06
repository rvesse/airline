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

import org.apache.commons.collections4.iterators.PeekingIterator;

import com.github.rvesse.airline.Context;
import com.github.rvesse.airline.model.OptionMetadata;
import com.github.rvesse.airline.parser.ParseState;
import com.github.rvesse.airline.parser.errors.ParseOptionMissingValueException;
import com.github.rvesse.airline.parser.errors.ParseOptionUnexpectedException;

/**
 * An options parsing that parses options given in classic get-opt style where multiple options may be concatenated
 * together, potentially including a value for the last option in the concatenation.
 * <p>
 * This is the default variant of the parser used by default configuration and since 2.8.2 was updated to be non-greedy
 * in its value consumption for {@code arity = 1} options. For the old greedy behaviour please use
 * {@link GreedyClassicGetOptParser} instead.
 * </p>
 * <p>
 * For example consider a command that defines options {@code -a}, {@code -b} and {@code -c} where {@code -b} takes a
 * value (i.e. {@code arity = 1}) and the others are flag options. With that definition a user can provide the options
 * as {@code -abc} and that would result in an error because the {@code c} is considered an option and not a value for
 * {@code -b} resulting in a {@link ParseOptionMissingValueException} being generated.
 * </p>
 * <p>
 * However an input of {@code -acbfoo} would set the {@code -a} and {@code -c} flags while setting the value of
 * {@code -b} to {@code foo}.
 * </p>
 *
 * @param <T>
 */
public class ClassicGetOptParser<T> extends AbstractOptionParser<T> {
    public ParseState<T> parseOptions(PeekingIterator<String> tokens, ParseState<T> state,
            List<OptionMetadata> allowedOptions) {
        if (!hasShortNamePrefix(tokens.peek())) {
            return null;
        }

        // remove leading dash from token
        String remainingToken = tokens.peek().substring(1);

        ParseState<T> nextState = state;
        String argsSeparator = state.getParserConfiguration().getArgumentsSeparator();
        boolean first = true;
        while (!remainingToken.isEmpty()) {
            char tokenCharacter = remainingToken.charAt(0);

            // is the current token character a single letter option?
            OptionMetadata option = findOption(state, allowedOptions, "-" + tokenCharacter);
            if (option == null) {
                return null;
            }

            nextState = nextState.pushContext(Context.OPTION).withOption(option);

            // remove current token character
            remainingToken = remainingToken.substring(1);

            // for no argument options, process the option and remove the
            // character from the token
            if (option.getArity() == 0) {
                // Note - Flag negation is not usable with single character
                // options so value will always be set as true for flag i.e.
                // zero arity options
                nextState = nextState.withOptionValue(option, Boolean.TRUE.toString()).popContext();
                first = false;
                continue;
            }

            if (option.getArity() == 1) {
                // we must, consume the current token so we can see the next
                // token
                tokens.next();

                // if current token has more characters, this is the value;
                // otherwise it is the next token
                if (!remainingToken.isEmpty()) {
                    // Check the next character is not itself an option or the arguments separator
                    if (isSeparatorOrOption(nextState, allowedOptions, argsSeparator, true, remainingToken)) {
                        noValueForOption(nextState, option);
                        return nextState;
                    }

                    // Otherwise consume the options value
                    nextState = nextState.withOptionValue(option, remainingToken).popContext();

                } else if (tokens.hasNext()) {
                    // Check the next token is not itself an option or the arguments separator
                    String peekedToken = tokens.peek();
                    if (isSeparatorOrOption(nextState, allowedOptions, argsSeparator, false, peekedToken)) {
                        noValueForOption(nextState, option);
                        return nextState;
                    }

                    // Otherwise consume the options value
                    nextState = nextState.withOptionValue(option, tokens.next()).popContext();
                }

                first = false;
                return nextState;
            }

            // Don't throw an error if this is the first option we have seen as
            // in that case the option may legitimately be processed by another
            // option parser
            if (first)
                return null;

            // Produce an error, can't use short style options with an option
            // with an arity greater than one
            // Return the modified state anyway as we don't want to retry
            // processing this option in that case
            state.getParserConfiguration().getErrorHandler().handleError(new ParseOptionUnexpectedException(
                    "Short options style can not be used with option %s as the arity was not 0 or 1", option));
            return nextState;
        }

        // consume the current token
        tokens.next();

        return nextState;
    }
}
