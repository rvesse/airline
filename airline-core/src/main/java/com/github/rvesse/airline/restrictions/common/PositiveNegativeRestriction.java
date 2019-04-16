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
import com.github.rvesse.airline.parser.errors.ParseRestrictionViolatedException;
import com.github.rvesse.airline.restrictions.AbstractCommonRestriction;
import com.github.rvesse.airline.utils.comparators.ByteComparator;
import com.github.rvesse.airline.utils.comparators.DoubleComparator;
import com.github.rvesse.airline.utils.comparators.FloatComparator;
import com.github.rvesse.airline.utils.comparators.IntegerComparator;
import com.github.rvesse.airline.utils.comparators.LongComparator;
import com.github.rvesse.airline.utils.comparators.ShortComparator;

/**
 * Restriction that enforces that values must be either positive/negative with
 * zero optionally included
 * 
 * @author rvesse
 *
 */
public class PositiveNegativeRestriction extends AbstractCommonRestriction implements HelpHint {

    private final RangeRestriction byteRestrictor, shortRestrictor, integerRestrictor, longRestrictor, floatRestrictor,
            doubleRestrictor;
    private final boolean positive, includeZero;
    private final String type, range;

    /**
     * Creates a new restriction
     * 
     * @param positive
     *            Whether the value must be positive
     * @param includeZero
     *            Whether zero is included as a valid value
     */
    public PositiveNegativeRestriction(boolean positive, boolean includeZero) {
        this.positive = positive;
        this.includeZero = includeZero;
        this.type = this.positive ? "positive" : "negative";
        String comparator = this.positive ? (this.includeZero ? ">=" : ">") : (this.includeZero ? "<=" : "<");
        this.range = String.format("value %s 0", comparator);
        if (this.positive) {
            this.byteRestrictor = new RangeRestriction((byte) 0, includeZero, Byte.MAX_VALUE, true,
                    new ByteComparator());
            this.shortRestrictor = new RangeRestriction((short) 0, includeZero, Short.MAX_VALUE, true,
                    new ShortComparator());
            this.integerRestrictor = new RangeRestriction(0, includeZero, Integer.MAX_VALUE, true,
                    new IntegerComparator());
            this.longRestrictor = new RangeRestriction(0l, includeZero, Long.MAX_VALUE, true, new LongComparator());
            this.floatRestrictor = new RangeRestriction(0f, includeZero, Float.MAX_VALUE, true, new FloatComparator());
            this.doubleRestrictor = new RangeRestriction(0d, includeZero, Double.MAX_VALUE, true,
                    new DoubleComparator());
        } else {
            this.byteRestrictor = new RangeRestriction(Byte.MIN_VALUE, true, (byte) 0, includeZero,
                    new ByteComparator());
            this.shortRestrictor = new RangeRestriction(Short.MIN_VALUE, true, (short) 0, includeZero,
                    new ShortComparator());
            this.integerRestrictor = new RangeRestriction(Integer.MIN_VALUE, true, 0, includeZero,
                    new IntegerComparator());
            this.longRestrictor = new RangeRestriction(Long.MIN_VALUE, true, 0l, includeZero, new LongComparator());
            this.floatRestrictor = new RangeRestriction(Float.MIN_VALUE, true, 0f, includeZero, new FloatComparator());
            this.doubleRestrictor = new RangeRestriction(Double.MIN_VALUE, true, 0d, includeZero,
                    new DoubleComparator());
        }
    }

    protected boolean isValid(Object value) {
        if (value instanceof Byte) {
            return this.byteRestrictor.inRange(value);
        } else if (value instanceof Short) {
            return this.shortRestrictor.inRange(value);
        } else if (value instanceof Integer) {
            return this.integerRestrictor.inRange(value);
        } else if (value instanceof Long) {
            return this.longRestrictor.inRange(value);
        } else if (value instanceof Float) {
            return this.floatRestrictor.inRange(value);
        } else if (value instanceof Double) {
            return this.doubleRestrictor.inRange(value);
        } else {
            return false;
        }
    }

    @Override
    public <T> void postValidate(ParseState<T> state, OptionMetadata option, Object value) {
        if (!this.isValid(value)) {
            throw new ParseRestrictionViolatedException("Option '%s' must have a %s value (%s) but got %s",
                    option.getTitle(), this.type, this.range, value);
        }
    }

    @Override
    public <T> void postValidate(ParseState<T> state, ArgumentsMetadata arguments, Object value) {
        if (!this.isValid(value)) {
            throw new ParseRestrictionViolatedException("Argument '%s' must have a %s value (%s) but got %s",
                    AbstractCommonRestriction.getArgumentTitle(state, arguments), this.type, this.range, value);
        }
    }

    @Override
    public <T> void postValidate(ParseState<T> state, PositionalArgumentMetadata arguments, Object value) {
        if (!this.isValid(value)) {
            throw new ParseRestrictionViolatedException(
                    "Positional argument %d ('%s') must have a %s value (%s) but got %s",
                    arguments.getZeroBasedPosition(), arguments.getTitle(), this.type, this.range, value);
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
        return new String[] { String.format("This options value must be a %s value i.e. respects the range %s",
                this.type, this.range) };
    }

}
