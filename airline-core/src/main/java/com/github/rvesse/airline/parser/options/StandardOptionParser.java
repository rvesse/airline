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
import org.apache.commons.lang3.StringUtils;

import com.github.rvesse.airline.Context;
import com.github.rvesse.airline.model.OptionMetadata;
import com.github.rvesse.airline.parser.ParseState;
import com.github.rvesse.airline.parser.errors.ParseOptionMissingValueException;
import com.github.rvesse.airline.restrictions.AbstractCommonRestriction;

/**
 * An options parser that expects the name and value(s) to be white space
 * separated e.g. {@code --name value}
 *
 */
public class StandardOptionParser<T> extends AbstractOptionParser<T> {

    @Override
    public ParseState<T> parseOptions(PeekingIterator<String> tokens, ParseState<T> state,
            List<OptionMetadata> allowedOptions) {
        OptionMetadata option = findOption(state, allowedOptions, tokens.peek());
        if (option == null) {
            return null;
        }

        String optionName = tokens.next();
        state = state.pushContext(Context.OPTION).withOption(option);

        if (option.getArity() == 0) {
            // Determine what value to set
            // This will depend on whether flag negation is enabled and if so
            // whether the option name used started with the configured negation
            // prefix
            String rawBooleanValue = state.getParserConfiguration().allowsFlagNegation()
                    && StringUtils.startsWith(optionName, state.getParserConfiguration().getFlagNegationPrefix())
                            ? Boolean.FALSE.toString() : Boolean.TRUE.toString();
            state = state.withOptionValue(option, rawBooleanValue).popContext();
        } else if (option.getArity() == 1) {
            if (tokens.hasNext()) {
                state = state.withOptionValue(option, tokens.next()).popContext();
            }
        } else {
            int count = 0;

            boolean hasSeparator = false;
            boolean foundNextOption = false;
            String argsSeparator = state.getParserConfiguration().getArgumentsSeparator();
            while (count < option.getArity() && tokens.hasNext() && !hasSeparator) {
                String peekedToken = tokens.peek();
                hasSeparator = peekedToken.equals(argsSeparator);
                foundNextOption = findOption(state, allowedOptions, peekedToken) != null;

                if (hasSeparator || foundNextOption)
                    break;
                state = state.withOptionValue(option, tokens.next());
                ++count;
            }

            if (count != option.getArity()) {
                state.getParserConfiguration().getErrorHandler()
                        .handleError(new ParseOptionMissingValueException(
                                "Too few option values received for option %s (%d values expected but only found %d)",
                                option.getOptions().iterator().next(), option.getOptions().iterator().next(), option.getArity(), count));
                return state;

            }
            state = state.popContext();
        }
        return state;
    }

}
