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

import com.github.rvesse.airline.help.sections.HelpFormat;
import com.github.rvesse.airline.help.sections.HelpHint;
import com.github.rvesse.airline.model.ArgumentsMetadata;
import com.github.rvesse.airline.model.OptionMetadata;
import com.github.rvesse.airline.model.PositionalArgumentMetadata;
import com.github.rvesse.airline.parser.ParseState;
import com.github.rvesse.airline.parser.errors.ParseInvalidRestrictionException;
import com.github.rvesse.airline.parser.errors.ParseRestrictionViolatedException;
import com.github.rvesse.airline.restrictions.AbstractCommonRestriction;
import com.github.rvesse.airline.utils.AirlineUtils;

/**
 * A restriction that requires string values meet length constraints
 * 
 * @author rvesse
 *
 */
public class LengthRestriction extends AbstractStringRestriction implements HelpHint {

    private final boolean maximum, range;
    private final int min, max;

    /**
     * Creates a length restriction with either a minimum or maximum
     * 
     * @param length
     *            Length
     * @param maximum
     *            True if the {@code length} is a maximum, false if it is a minimum
     */
    public LengthRestriction(int length, boolean maximum) {
        this.min = maximum ? Integer.MIN_VALUE : length;
        this.max = maximum ? length : Integer.MAX_VALUE;
        this.maximum = maximum;
        this.range = false;
    }

    /**
     * Creates a length restriction with a minimum and maximum i.e. a range
     * 
     * @param min
     *            Minimum length
     * @param max
     *            Maximum length
     */
    public LengthRestriction(int min, int max) {
        this.min = min;
        this.max = max;
        if (min > max)
            throw new ParseInvalidRestrictionException("min (%s) is greater than max (%s)", min, max);
        this.maximum = false;
        this.range = true;
    }

    @Override
    protected boolean isValid(String value) {
        if (this.maximum) {
            return value.length() <= this.max;
        } else if (this.range) {
            return value.length() >= this.min && value.length() <= this.max;
        } else {
            return value.length() >= this.min;
        }
    }

    @Override
    protected <T> ParseRestrictionViolatedException violated(ParseState<T> state, OptionMetadata option, String value) {
        String title = AbstractCommonRestriction.getOptionTitle(state, option);
        String name = AirlineUtils.first(option.getOptions());
        if (this.maximum) {
            return new ParseRestrictionViolatedException(
                    "Option %s value '%s' was given value '%s' that has length %d which exceeds the maximum permitted length of %d",
                    name, title, value, value.length(), this.max);
        } else if (this.range) {
            if (this.min == this.max) {
                return new ParseRestrictionViolatedException(
                        "Option %s value '%s' was given value '%s' that has length %d which does not match the required length of %d",
                        name, title, value, value.length(), this.max);
            } else {
                return new ParseRestrictionViolatedException(
                        "Option %s value '%s' was given value '%s' that has length %d which is not in the accepted length range of %d to %d characters",
                        name, title, value, value.length(), this.min, this.max);
            }
        } else {
            return new ParseRestrictionViolatedException(
                    "Option %s value '%s' was given value '%s' that has length %d which is below the minimum required length of %d",
                    name, title, value, value.length(), this.min);
        }
    }

    @Override
    protected <T> ParseRestrictionViolatedException violated(ParseState<T> state, ArgumentsMetadata arguments,
            String value) {
        String title = AbstractCommonRestriction.getArgumentTitle(state, arguments);
        if (this.maximum) {
            return new ParseRestrictionViolatedException(
                    "Argument '%s' was given value '%s' that has length %d which exceeds the maximum permitted length of %d",
                    title, value, value.length(), this.max);
        } else if (this.range) {
            if (this.min == this.max) {
                return new ParseRestrictionViolatedException(
                        "Argument '%s' was given value '%s' that has length %d which exceeds the maximum permitted length of %d",
                        title, value, value.length(), this.max);
            } else {
                return new ParseRestrictionViolatedException(
                        "Argument '%s' was given value '%s' that has length %d which is not in the accepted length range of %d to %d characters",
                        title, value, value.length(), this.min, this.max);
            }
        } else {
            return new ParseRestrictionViolatedException(
                    "Argument '%s' was given value '%s' that has length %d which is below the minimum required length of %d",
                    title, value, value.length(), this.min);
        }
    }

    @Override
    protected <T> ParseRestrictionViolatedException violated(ParseState<T> state, PositionalArgumentMetadata arguments,
            String value) {
        if (this.maximum) {
            return new ParseRestrictionViolatedException(
                    "Positional Argument %d ('%s') was given value '%s' that has length %d which exceeds the maximum permitted length of %d",
                    arguments.getZeroBasedPosition(), arguments.getTitle(), value, value.length(), this.max);
        } else if (this.range) {
            if (this.min == this.max) {
                return new ParseRestrictionViolatedException(
                        "Positional Argument %d ('%s') was given value '%s' that has length %d which exceeds the maximum permitted length of %d",
                        arguments.getZeroBasedPosition(), arguments.getTitle(), value, value.length(), this.max);
            } else {
                return new ParseRestrictionViolatedException(
                        "Positional Argument %d ('%s') was given value '%s' that has length %d which is not in the accepted length range of %d to %d characters",
                        arguments.getZeroBasedPosition(), arguments.getTitle(), value, value.length(), this.min,
                        this.max);
            }
        } else {
            return new ParseRestrictionViolatedException(
                    "Positional Argument %d ('%s') was given value '%s' that has length %d which is below the minimum required length of %d",
                    arguments.getZeroBasedPosition(), arguments.getTitle(), value, value.length(), this.min);
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

        if (this.maximum) {
            return new String[] { String.format("This options value has a maximum length of %d characters", this.max) };
        } else if (this.range) {
            if (this.min == this.max) {
                return new String[] {
                        String.format("This options value must have a length of %d characters", this.max) };
            } else {
                return new String[] { String.format(
                        "This options value must have a length between %d and %d characters", this.min, this.max) };
            }
        } else {
            return new String[] { String.format("This options value has a minimum length of %d characters", this.min) };
        }
    }

}
