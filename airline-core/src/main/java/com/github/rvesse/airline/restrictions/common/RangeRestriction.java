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

import java.util.Comparator;

import com.github.rvesse.airline.help.sections.HelpFormat;
import com.github.rvesse.airline.help.sections.HelpHint;
import com.github.rvesse.airline.model.ArgumentsMetadata;
import com.github.rvesse.airline.model.OptionMetadata;
import com.github.rvesse.airline.model.PositionalArgumentMetadata;
import com.github.rvesse.airline.parser.ParseState;
import com.github.rvesse.airline.parser.errors.ParseInvalidRestrictionException;
import com.github.rvesse.airline.parser.errors.ParseOptionOutOfRangeException;
import com.github.rvesse.airline.restrictions.AbstractCommonRestriction;
import com.github.rvesse.airline.utils.AirlineUtils;

/**
 * A restriction that requires the value (after type conversion) to be within a
 * given range
 *
 */
public class RangeRestriction extends AbstractCommonRestriction implements HelpHint {

    private final Object min, max;
    private final boolean minInclusive, maxInclusive, singleValue;
    private final Comparator<Object> comparator;

    public RangeRestriction(Object min, boolean minInclusive, Object max, boolean maxInclusive,
            Comparator<Object> comparator) {
        if (comparator == null)
            throw new ParseInvalidRestrictionException("comparator cannot be null");
        this.min = min;
        this.minInclusive = minInclusive;
        this.max = max;
        this.maxInclusive = maxInclusive;
        this.comparator = comparator;

        // Validate the range
        if (min != null && max != null) {
            int rangeComparison = this.comparator.compare(min, max);
            this.singleValue = rangeComparison == 0;
            if (rangeComparison > 0)
                throw new ParseInvalidRestrictionException("min (%s) is greater than max (%s)", min, max);
            if (rangeComparison == 0 && (!minInclusive || !maxInclusive))
                throw new ParseInvalidRestrictionException(
                        "min (%s) and max (%s) compare as equal but either minInclusive or maxInclusive was false", min,
                        max);

        } else {
            this.singleValue = false;
        }
    }

    @Override
    public <T> void postValidate(ParseState<T> state, OptionMetadata option, Object value) {
        // Not enforced if no range provided
        if (this.min == null && this.max == null)
            return;
        
        if (!inRange(value))
            throw new ParseOptionOutOfRangeException(option.getTitle(), value, min, minInclusive, max, maxInclusive);
    }
    
    @Override
    public <T> void postValidate(ParseState<T> state, ArgumentsMetadata arguments, Object value) {
        // Not enforced if no range provided
        if (this.min == null && this.max == null)
            return;
        
        if (!inRange(value))
            throw new ParseOptionOutOfRangeException(getArgumentTitle(state, arguments), value, min, minInclusive, max, maxInclusive);
    }
    
    @Override
    public <T> void postValidate(ParseState<T> state, PositionalArgumentMetadata arguments, Object value) {
        // Not enforced if no range provided
        if (this.min == null && this.max == null)
            return;
        
        if (!inRange(value))
            throw new ParseOptionOutOfRangeException(arguments.getTitle(), value, min, minInclusive, max, maxInclusive);
    }

    protected boolean inRange(Object value) {
        if (this.min != null) {
            int minComparison = this.comparator.compare(this.min, value);
            if (minComparison == 0)
                return this.minInclusive;
            if (minComparison > 0)
                return false;
        }
        if (this.max != null) {
            int maxComparison = this.comparator.compare(value, this.max);
            if (maxComparison == 0)
                return this.maxInclusive;
            if (maxComparison > 0)
                return false;
        }
        return true;
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
        if (this.singleValue) {
            return new String[] { String.format("This options value must meet the restriction value == %s", this.min) };
        } else {
            return new String[] { String.format("This options value must fall in the following range: %s",
                    AirlineUtils.toRangeString(this.min, this.minInclusive, this.max, this.maxInclusive)) };
        }
    }

}
