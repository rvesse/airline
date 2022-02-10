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

import com.github.rvesse.airline.help.sections.HelpFormat;
import com.github.rvesse.airline.model.ArgumentsMetadata;
import com.github.rvesse.airline.model.OptionMetadata;
import com.github.rvesse.airline.parser.ParseState;
import com.github.rvesse.airline.parser.errors.ParseRestrictionViolatedException;
import com.github.rvesse.airline.restrictions.AbstractCommonRestriction;
import com.github.rvesse.airline.restrictions.GlobalRestriction;
import com.github.rvesse.airline.utils.AirlineUtils;
import org.apache.commons.lang3.tuple.Pair;

/**
 * A restriction that enforces that values <strong>CANNOT</strong> look like options
 * <p>
 * Please see {@link com.github.rvesse.airline.annotations.restrictions.NoOptionLikeValues} for more discussion of how
 * this restriction applies.
 * </p>
 *
 * @author rvesse
 */
public class NoOptionLikeValuesRestriction extends StartsWithRestriction implements GlobalRestriction {

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

    @Override
    public <T> void validate(ParseState<T> state) {
        for (Pair<OptionMetadata, Object> parsedOption : state.getParsedOptions()) {
            if (parsedOption.getValue() instanceof String) {
                String value = (String) parsedOption.getValue();
                if (!isValid(value)) {
                    violated(state, parsedOption.getKey(), value);
                }
            }
        }
        for (Object parsedArg : state.getParsedArguments()) {
            if (parsedArg instanceof String) {
                String value = (String) parsedArg;
                if (!isValid(value)) {
                    violated(state, state.getCommand().getArguments(), value);
                }
            }
        }
    }

    @Override
    public String getPreamble() {
        return null;
    }

    @Override
    public HelpFormat getFormat() {
        return HelpFormat.PROSE;
    }

    @Override
    public int numContentBlocks() {
        return 1;
    }

    @Override
    public String[] getContentBlock(int blockNumber) {
        if (blockNumber != 0)
            throw new IndexOutOfBoundsException();
        return new String[]{String.format(
                "This options value must not look like it could be an option.  Any option-like value will be rejected.")};
    }
}
