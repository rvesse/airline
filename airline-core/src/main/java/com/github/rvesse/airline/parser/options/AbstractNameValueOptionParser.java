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
 * Abstract option parser for options that are specified in {@code --name=value}
 * style while the separator character (in this example {@code =}) can be
 * configured as desired.
 * 
 * @author rvesse
 *
 */
public abstract class AbstractNameValueOptionParser<T> extends AbstractOptionParser<T> {

    private static final char DEFAULT_SEPARATOR = '=';
    private final char separator;

    public AbstractNameValueOptionParser() {
        this(DEFAULT_SEPARATOR);
    }

    public AbstractNameValueOptionParser(char sep) {
        this.separator = sep;
    }

    @Override
    public ParseState<T> parseOptions(PeekingIterator<String> tokens, ParseState<T> state, List<OptionMetadata> allowedOptions) {
        List<String> parts = AirlineUtils.unmodifiableListCopy(StringUtils.split(tokens.peek(), new String(new char[] { this.separator }), 2));
        if (parts.size() != 2) {
            return null;
        }

        OptionMetadata option = findOption(state, allowedOptions, parts.get(0));
        if (option == null || option.getArity() != 1) {
            // Only supported for arity 1 options currently
            return null;
        }

        // we have a match so consume the token
        tokens.next();

        // update state
        state = state.pushContext(Context.OPTION).withOption(option);
        state = state.withOptionValue(option, parts.get(1)).popContext();

        return state;
    }

}
