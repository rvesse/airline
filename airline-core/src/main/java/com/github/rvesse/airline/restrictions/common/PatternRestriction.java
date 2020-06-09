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

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.github.rvesse.airline.help.sections.HelpFormat;
import com.github.rvesse.airline.help.sections.HelpHint;
import com.github.rvesse.airline.model.ArgumentsMetadata;
import com.github.rvesse.airline.model.OptionMetadata;
import com.github.rvesse.airline.parser.ParseState;
import com.github.rvesse.airline.parser.errors.ParseRestrictionViolatedException;
import com.github.rvesse.airline.restrictions.AbstractCommonRestriction;
import com.github.rvesse.airline.utils.AirlineUtils;

/**
 * A restriction which requires the raw values to match a given regular
 * expression
 */
public class PatternRestriction extends AbstractCommonRestriction implements HelpHint {

    private final Pattern pattern;
    private final String description;

    /**
     * Creates a pattern restriction
     * 
     * @param pattern
     *            Regular expression pattern
     * @param flags
     *            Regular expression flags
     * @param description
     *            Friendly description of the intent of the pattern, included in
     *            errors when the restriction is violated
     */
    public PatternRestriction(String pattern, int flags, String description) {
        this.pattern = Pattern.compile(pattern, flags);
        this.description = description;
    }

    @Override
    public <T> void preValidate(ParseState<T> state, OptionMetadata option, String value) {
        if (!this.pattern.matcher(value).find())
            throw new ParseRestrictionViolatedException(
                    "Option %s value '%s' was given value '%s' which does not match the regular expression '%s'.  %s",
                    AirlineUtils.first(option.getOptions()),
                    AbstractCommonRestriction.getOptionTitle(state, option), value, this.pattern.toString(),
                    StringUtils.isNotBlank(this.description) ? this.description : "");
    }

    @Override
    public <T> void preValidate(ParseState<T> state, ArgumentsMetadata arguments, String value) {
        if (!this.pattern.matcher(value).find())
            throw new ParseRestrictionViolatedException(
                    "Argument '%s' was given value '%s' which does not match the regular expression '%s'.  %s",
                    AbstractCommonRestriction.getArgumentTitle(state, arguments), value, this.pattern.toString(),
                    StringUtils.isNotBlank(this.description) ? this.description : "");
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

        return new String[] { String.format("This options value must match the regular expression '%s'.  %s",
                this.pattern.toString(), StringUtils.isNotBlank(this.description) ? this.description : "") };
    }

}
