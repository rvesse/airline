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

import com.github.rvesse.airline.model.OptionMetadata;
import com.github.rvesse.airline.parser.ParseState;

/**
 * Greedy variant of the {@link ClassicGetOptParser} that allows consuming values that could otherwise be treated as
 * options in their own right.
 * <p>
 * For example consider a command that defines options {@code -a}, {@code -b} and {@code -c} where {@code -b} takes a
 * value (i.e. {code arity = 1}) and the others are flag options. With that definition a user can provide the options as
 * {@code -abc} and that would result in the {@code -a} flag being set and the value of {@code -b} set to {@code -c}.
 * </p>
 * <p>
 * However an input of {@code -acbfoo} would set the {@code -a} and {@code -c} flags while setting the value of
 * {@code -b} to {@code foo}.  If you prefer non-greedy behaviour use the {@link ClassicGetOptParser} instead.
 * </p>
 * 
 * @author rvesse
 * @since 2.8.2
 *
 * @param <T>
 *            Command type
 */
public class GreedyClassicGetOptParser<T> extends ClassicGetOptParser<T> {

    @Override
    protected boolean isSeparatorOrOption(ParseState<T> state, List<OptionMetadata> allowedOptions,
            String argsSeparator, boolean shortForm, String peekedToken) {
        // To get greedy behaviour always treat the next token as the value regardless of whether it might normally be
        // considered an option
        return false;
    }

}
