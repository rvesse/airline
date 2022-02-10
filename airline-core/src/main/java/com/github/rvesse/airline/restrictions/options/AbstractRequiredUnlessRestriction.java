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
package com.github.rvesse.airline.restrictions.options;

import com.github.rvesse.airline.model.ArgumentsMetadata;
import com.github.rvesse.airline.model.OptionMetadata;
import com.github.rvesse.airline.parser.ParseState;
import com.github.rvesse.airline.parser.errors.ParseArgumentsMissingException;
import com.github.rvesse.airline.parser.errors.ParseOptionMissingException;
import com.github.rvesse.airline.restrictions.AbstractCommonRestriction;
import com.github.rvesse.airline.utils.AirlineUtils;
import com.github.rvesse.airline.utils.predicates.parser.ParsedOptionFinder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * A restriction that options/arguments are required unless some other criteria is met
 */
public abstract class AbstractRequiredUnlessRestriction extends AbstractCommonRestriction {

    /**
     * Answers whether the unless condition of the restriction is met
     * <p>
     * If the unless condition is met then the restriction will not require the option to be present.
     * </p>
     *
     * @param state  Parse state
     * @param option Option Metadata
     * @param <T>    Command Type
     * @return True if unless condition met, false otherwise
     */
    protected abstract <T> boolean unless(ParseState<T> state, OptionMetadata option);

    /**
     * Answers whether the unless condition of the restriction is met
     * <p>
     * If the unless condition is met then the restriction will not require arguments to be present.
     * </p>
     *
     * @param state     Parse state
     * @param arguments Arguments Metadata
     * @param <T>       Command Type
     * @return True if unless condition met, false otherwise
     */
    protected abstract <T> boolean unless(ParseState<T> state, ArgumentsMetadata arguments);

    /**
     * Provides a description of the unless condition, this will be included in the error messages when this restriction
     * is not met
     *
     * @return Unless condition description
     */
    protected abstract String unlessDescription();

    @Override
    public <T> void finalValidate(ParseState<T> state, OptionMetadata option) {
        if (CollectionUtils.find(state.getParsedOptions(), new ParsedOptionFinder(option)) == null
                && !unless(state, option)) {
            throw new ParseOptionMissingException(AirlineUtils.first(option.getOptions()), unlessDescription());
        }
    }

    @Override
    public <T> void finalValidate(ParseState<T> state, ArgumentsMetadata arguments) {
        if (state.getParsedArguments().isEmpty() && !unless(state, arguments)) {
            throw new ParseArgumentsMissingException("Required arguments '%s' are missing and %s",
                                                     arguments.getTitle(), StringUtils.join(arguments.getTitle(), ", "),
                                                     unlessDescription());
        }
    }

}
