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

import java.util.Locale;

import com.github.rvesse.airline.model.ArgumentsMetadata;
import com.github.rvesse.airline.model.OptionMetadata;
import com.github.rvesse.airline.parser.ParseState;
import com.github.rvesse.airline.parser.errors.ParseRestrictionViolatedException;
import com.github.rvesse.airline.restrictions.AbstractCommonRestriction;
import com.github.rvesse.airline.utils.AirlineUtils;

/**
 * A restriction that enforces that values <strong>CANNOT</strong> look like options
 * 
 * @author rvesse
 *
 */
public class NoOptionLikeValuesRestriction extends StartsWithRestriction {

    public NoOptionLikeValuesRestriction(String[] prefixes) {
        super(false, Locale.ROOT, prefixes);
    }

    @Override
    protected boolean isValid(String value) {
        return !super.isValid(value);
    }

    @Override
    protected <T> ParseRestrictionViolatedException violated(ParseState<T> state, OptionMetadata option, String value) {
        throw new ParseRestrictionViolatedException(
                "Option %s value '%s' has value '%s' which appears to be an option, but was not recognized as such, was this option entered incorrectly?",
                AirlineUtils.first(option.getOptions()), AbstractCommonRestriction.getOptionTitle(state, option),
                value);
    }

    @Override
    protected <T> ParseRestrictionViolatedException violated(ParseState<T> state, ArgumentsMetadata arguments,
            String value) {
        throw new ParseRestrictionViolatedException(
                "Argument '%s' has value '%s' which appears to be an option, but was not recognized as such, was this option entered incorrectly?",
                AbstractCommonRestriction.getArgumentTitle(state, arguments), value);
    }

}
