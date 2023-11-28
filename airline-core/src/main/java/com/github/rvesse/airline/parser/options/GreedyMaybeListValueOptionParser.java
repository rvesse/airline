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

import com.github.rvesse.airline.annotations.Arguments;
import com.github.rvesse.airline.annotations.DefaultOption;
import com.github.rvesse.airline.parser.ParseState;

/**
 * A variation on the {@link MaybeListValueOptionParser} that is greedy
 * <p>
 * This primarily makes a difference when used with commands that also use {@link Arguments} or {@link DefaultOption}.
 * If that is the case using this parser could incorrectly consume values not intended to be part of the list. Users
 * creating CLIs should be certain that this represents the behaviour they want.
 * </p>
 *
 * @param <T> Command Type
 */
public class GreedyMaybeListValueOptionParser<T> extends MaybeListValueOptionParser<T> {

    public GreedyMaybeListValueOptionParser() {
        super();
    }

    public GreedyMaybeListValueOptionParser(char separator) {
        super(separator);
    }

    @Override
    protected boolean canGreedySearch(ParseState<T> state) {
        // Always search greedily
        return true;
    }
}
