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
package com.github.rvesse.airline.restrictions.common;

import com.github.rvesse.airline.utils.CollectionUtils;

import com.github.rvesse.airline.model.ArgumentsMetadata;
import com.github.rvesse.airline.model.OptionMetadata;
import com.github.rvesse.airline.parser.ParseState;
import com.github.rvesse.airline.parser.errors.ParseArgumentsMissingException;
import com.github.rvesse.airline.parser.errors.ParseOptionMissingException;
import com.github.rvesse.airline.restrictions.AbstractCommonRestriction;
import com.github.rvesse.airline.utils.AirlineUtils;
import com.github.rvesse.airline.utils.predicates.parser.ParsedOptionFinder;

/**
 * A restriction that options/arguments are required
 */
public class IsRequiredRestriction extends AbstractCommonRestriction {

    @Override
    public <T> void finalValidate(ParseState<T> state, OptionMetadata option) {
        if (CollectionUtils.find(state.getParsedOptions(), new ParsedOptionFinder(option)) == null)
            throw new ParseOptionMissingException(AirlineUtils.first(option.getOptions()));
    }

    @Override
    public <T> void finalValidate(ParseState<T> state, ArgumentsMetadata arguments) {
        if (state.getParsedArguments().isEmpty())
            throw new ParseArgumentsMissingException(arguments.getTitle());
    }

}
