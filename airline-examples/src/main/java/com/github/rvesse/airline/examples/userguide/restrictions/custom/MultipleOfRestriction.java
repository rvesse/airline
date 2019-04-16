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
package com.github.rvesse.airline.examples.userguide.restrictions.custom;

import com.github.rvesse.airline.help.sections.HelpFormat;
import com.github.rvesse.airline.help.sections.HelpHint;
import com.github.rvesse.airline.model.ArgumentsMetadata;
import com.github.rvesse.airline.model.OptionMetadata;
import com.github.rvesse.airline.model.PositionalArgumentMetadata;
import com.github.rvesse.airline.parser.ParseState;
import com.github.rvesse.airline.parser.errors.ParseRestrictionViolatedException;
import com.github.rvesse.airline.restrictions.AbstractCommonRestriction;

public class MultipleOfRestriction extends AbstractCommonRestriction implements HelpHint {

    private final int multipleOf;

    public MultipleOfRestriction(int multipleOf) {
        this.multipleOf = multipleOf;
    }

    private <T> void validate(ParseState<T> state, String optionType, String optionTitle, Object value) {
        if (value instanceof Number) {
            Number n = (Number) value;
            if (n.longValue() % this.multipleOf != 0) {
                throw new ParseRestrictionViolatedException(
                        "%s '%s' must be an integer multiple of %d but got value '%s'", optionType, optionTitle,
                        this.multipleOf, value);
            }
        } else {
            throw new ParseRestrictionViolatedException(
                    "%s '%s' must be an integer multiple of %d which requires a numeric value but got value '%s'",
                    optionType, optionTitle, this.multipleOf, value);
        }
    }

    @Override
    public <T> void postValidate(ParseState<T> state, OptionMetadata option, Object value) {
        validate(state, "Option", option.getTitle(), value);
    }

    @Override
    public <T> void postValidate(ParseState<T> state, ArgumentsMetadata arguments, Object value) {
        validate(state, "Argument", getArgumentTitle(state, arguments), value);
    }

    @Override
    public <T> void postValidate(ParseState<T> state, PositionalArgumentMetadata arguments, Object value) {
        validate(state, String.format("Positional argument %d", arguments.getZeroBasedPosition()), arguments.getTitle(),
                value);
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
        return new String[] { String.format(
                "This options value must be a numeric value that is an integer multiple of %d", this.multipleOf) };
    }
}
