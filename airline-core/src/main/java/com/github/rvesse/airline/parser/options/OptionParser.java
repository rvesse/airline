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

import com.github.rvesse.airline.model.OptionMetadata;
import com.github.rvesse.airline.parser.ParseState;

/**
 * Interface for option parsers
 *
 */
public interface OptionParser<T> {

    /**
     * Parses one/more options from the token stream
     * 
     * @param tokens
     *            Tokens
     * @param state
     *            Current parser state
     * @param allowedOptions
     *            Allowed options at this point of the parsing
     * @return New parser state, may return {@code null} if this parser could
     *         not parse the next token as an option
     */
    public abstract ParseState<T> parseOptions(PeekingIterator<String> tokens, ParseState<T> state,
            List<OptionMetadata> allowedOptions);
}
