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
import com.github.rvesse.airline.model.PositionalArgumentMetadata;
import com.github.rvesse.airline.parser.ParseState;
import com.github.rvesse.airline.parser.errors.ParseRestrictionViolatedException;
import com.github.rvesse.airline.restrictions.AbstractCommonRestriction;
import com.github.rvesse.airline.utils.predicates.PrefixMatcher;

/**
 * A restriction that requires raw values to start with one of a set of prefixes
 * 
 * @author rvesse
 *
 */
public class StartsWithRestriction extends AbstractLocaleAndCaseStringRestriction implements HelpHint {

    private final String[] prefixes;
    private final PrefixMatcher matcher;

    public StartsWithRestriction(boolean ignoreCase, Locale locale, String... prefixes) {
        super(ignoreCase, locale);
        this.prefixes = prefixes;
        this.matcher = new PrefixMatcher(ignoreCase, locale, prefixes);
    }

    @Override
    protected boolean isValid(String value) {
        return this.matcher.evaluate(value);
    }

    @Override
    protected <T> ParseRestrictionViolatedException violated(ParseState<T> state, OptionMetadata option, String value) {
        throw new ParseRestrictionViolatedException(
                "Option '%s' has value '%s' which does not start with one of the permitted prefixes: %s",
                option.getTitle(), value, StringUtils.join(this.prefixes, ", "));
    }

    @Override
    protected <T> ParseRestrictionViolatedException violated(ParseState<T> state, ArgumentsMetadata arguments,
            String value) {
        throw new ParseRestrictionViolatedException(
                "Argument '%s' has value '%s' which does not end with one of the permitted prefixes: %s",
                AbstractCommonRestriction.getArgumentTitle(state, arguments), value,
                StringUtils.join(this.prefixes, ", "));
    }
    
    @Override
    protected <T> ParseRestrictionViolatedException violated(ParseState<T> state, PositionalArgumentMetadata arguments,
            String value) {
        throw new ParseRestrictionViolatedException(
                "Positional argument %d ('%s') has value '%s' which does not end with one of the permitted prefixes: %s",
                arguments.getZeroBasedPosition(), arguments.getTitle(), value,
                StringUtils.join(this.prefixes, ", "));
    }

    @Override
    public String getPreamble() {
        return String.format("This options value must start with one of the following %s prefixes:",
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
        return this.prefixes;
    }

}
