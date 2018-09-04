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

import com.github.rvesse.airline.model.ArgumentsMetadata;
import com.github.rvesse.airline.model.OptionMetadata;
import com.github.rvesse.airline.parser.ParseState;
import com.github.rvesse.airline.restrictions.AbstractCommonRestriction;
import com.github.rvesse.airline.utils.comparators.ByteComparator;
import com.github.rvesse.airline.utils.comparators.IntegerComparator;
import com.github.rvesse.airline.utils.comparators.LongComparator;
import com.github.rvesse.airline.utils.comparators.ShortComparator;

public class PositiveNegativeRestriction extends AbstractCommonRestriction {

    private final RangeRestriction byteRestrictor, shortRestrictor, integerRestrictor, longRestrictor, floatRestrictor,
            doubleRestrictor;
    
    public PositiveNegativeRestriction(boolean positive, boolean includeZero) {
        if (positive) {
            this.byteRestrictor = new RangeRestriction((byte)0, includeZero, Byte.MAX_VALUE, true, new ByteComparator());
            this.shortRestrictor = new RangeRestriction((short)0, includeZero, Short.MAX_VALUE, true, new ShortComparator());
            this.integerRestrictor = new RangeRestriction(0, includeZero, Integer.MAX_VALUE, true, new IntegerComparator());
            this.longRestrictor = new RangeRestriction(0l, includeZero, Integer.MAX_VALUE, true, new LongComparator());
            //etc
        } else {
            this.byteRestrictor = new RangeRestriction(Byte.MIN_VALUE, true, (byte)0, includeZero, new ByteComparator());
            this.shortRestrictor = new RangeRestriction(Short.MIN_VALUE, true, (byte)0, includeZero, new ShortComparator());
            this.integerRestrictor = new RangeRestriction(Integer.MIN_VALUE, true, 0, includeZero, new IntegerComparator());
        }
    }
    
    protected boolean isValid(Object value) {
        if (value instanceof Byte) {
            return this.byteRestrictor.inRange(value);
        } else if (value instanceof Short) {
            return this.shortRestrictor.inRange(value);
        } else if (value instanceof Integer) {
            return this.integerRestrictor.inRange(value);
            // TODO Other numeric types
        } else {
            return false;
        }
    }

    @Override
    public <T> void postValidate(ParseState<T> state, OptionMetadata option, Object value) {
        if (!this.isValid(value)) {
            // TODO
        }
    }

    @Override
    public <T> void postValidate(ParseState<T> state, ArgumentsMetadata arguments, Object value) {
        if (!this.isValid(value)) {
            // TODO
        }
    }

}
