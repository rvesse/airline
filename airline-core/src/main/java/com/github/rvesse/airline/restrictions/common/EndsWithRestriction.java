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

import org.apache.commons.lang3.StringUtils;

import com.github.rvesse.airline.help.sections.HelpFormat;
import com.github.rvesse.airline.help.sections.HelpHint;
import com.github.rvesse.airline.model.ArgumentsMetadata;
import com.github.rvesse.airline.model.OptionMetadata;
import com.github.rvesse.airline.parser.ParseState;
import com.github.rvesse.airline.parser.errors.ParseRestrictionViolatedException;
import com.github.rvesse.airline.restrictions.AbstractCommonRestriction;
import com.github.rvesse.airline.utils.AirlineUtils;
import com.github.rvesse.airline.utils.predicates.SuffixMatcher;

/**
 * A restriction that requires raw values to end with one of a set of suffixes
 * 
 * @author rvesse
 *
 */
public class EndsWithRestriction extends AbstractLocaleAndCaseStringRestriction implements HelpHint {

    private final String[] suffixes;
    private final SuffixMatcher matcher;

    public EndsWithRestriction(boolean ignoreCase, Locale locale, String... suffixes) {
        super(ignoreCase, locale);
        this.suffixes = suffixes;
        this.matcher = new SuffixMatcher(ignoreCase, locale, suffixes);
    }

    @Override
    protected boolean isValid(String value) {
        return this.matcher.evaluate(value);
    }

    @Override
    protected <T> ParseRestrictionViolatedException violated(ParseState<T> state, OptionMetadata option, String value) {
        throw new ParseRestrictionViolatedException(
                "Option %s value '%s' has value '%s' which does not end with one of the permitted suffixes: %s",
                AirlineUtils.first(option.getOptions()), AbstractCommonRestriction.getOptionTitle(state, option), value, StringUtils.join(this.suffixes, ", "));
    }

    @Override
    protected <T> ParseRestrictionViolatedException violated(ParseState<T> state, ArgumentsMetadata arguments,
            String value) {
        throw new ParseRestrictionViolatedException(
                "Argument '%s' has value '%s' which does not end with one of the permitted suffixes: %s",
                AbstractCommonRestriction.getArgumentTitle(state, arguments), value,
                StringUtils.join(this.suffixes, ", "));
    }

    @Override
    public String getPreamble() {
        return String.format("This options value must end with one of the following %s suffixes:",
                this.ignoreCase ? "case insensitive" : "sensitive");
    }

    @Override
    public HelpFormat getFormat() {
        return HelpFormat.LIST;
    }

    @Override
    public int numContentBlocks() {
        return 1;
    }

    @Override
    public String[] getContentBlock(int blockNumber) {
        if (blockNumber != 0)
            throw new IndexOutOfBoundsException();
        return this.suffixes;
    }

}
