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
package com.github.rvesse.airline.restrictions.factories;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

import com.github.rvesse.airline.annotations.restrictions.ranges.ByteRange;
import com.github.rvesse.airline.annotations.restrictions.ranges.DoubleRange;
import com.github.rvesse.airline.annotations.restrictions.ranges.FloatRange;
import com.github.rvesse.airline.annotations.restrictions.ranges.IntegerRange;
import com.github.rvesse.airline.annotations.restrictions.ranges.LengthRange;
import com.github.rvesse.airline.annotations.restrictions.ranges.LexicalRange;
import com.github.rvesse.airline.annotations.restrictions.ranges.LongRange;
import com.github.rvesse.airline.annotations.restrictions.ranges.Negative;
import com.github.rvesse.airline.annotations.restrictions.ranges.Positive;
import com.github.rvesse.airline.annotations.restrictions.ranges.ShortRange;
import com.github.rvesse.airline.restrictions.AbstractCommonRestriction;
import com.github.rvesse.airline.restrictions.ArgumentsRestriction;
import com.github.rvesse.airline.restrictions.OptionRestriction;
import com.github.rvesse.airline.restrictions.common.LengthRestriction;
import com.github.rvesse.airline.restrictions.common.PositiveNegativeRestriction;
import com.github.rvesse.airline.restrictions.common.RangeRestriction;
import com.github.rvesse.airline.utils.comparators.ByteComparator;
import com.github.rvesse.airline.utils.comparators.DoubleComparator;
import com.github.rvesse.airline.utils.comparators.FloatComparator;
import com.github.rvesse.airline.utils.comparators.IntegerComparator;
import com.github.rvesse.airline.utils.comparators.LexicalComparator;
import com.github.rvesse.airline.utils.comparators.LongComparator;
import com.github.rvesse.airline.utils.comparators.ShortComparator;

/**
 * Factory which generates range restrictions from a variety of different range
 * annotations
 *
 */
public class RangeRestrictionFactory implements OptionRestrictionFactory, ArgumentsRestrictionFactory {

    private static final Comparator<Object> LONG_COMPARATOR = new LongComparator();
    private static final Comparator<Object> INTEGER_COMPARATOR = new IntegerComparator();
    private static final Comparator<Object> SHORT_COMPARATOR = new ShortComparator();
    private static final Comparator<Object> BYTE_COMPARATOR = new ByteComparator();
    private static final Comparator<Object> DOUBLE_COMPARATOR = new DoubleComparator();
    private static final Comparator<Object> FLOAT_COMPARATOR = new FloatComparator();

    @Override
    public final OptionRestriction createOptionRestriction(Annotation annotation) {
        return createCommon(annotation);
    }

    @Override
    public final ArgumentsRestriction createArgumentsRestriction(Annotation annotation) {
        return createCommon(annotation);
    }

    /**
     * Handles converting all the basic range annotations into appropriate range
     * restrictions
     * 
     * @param annotation
     *            Annotation to create range from
     * @return Range restriction
     */
    protected final AbstractCommonRestriction createCommon(Annotation annotation) {
        if (annotation instanceof LongRange) {
            return createLongRange(annotation);
        } else if (annotation instanceof IntegerRange) {
            return createIntegerRange(annotation);
        } else if (annotation instanceof ShortRange) {
            return createShortRange(annotation);
        } else if (annotation instanceof ByteRange) {
            return createByteRange(annotation);
        } else if (annotation instanceof DoubleRange) {
            return createDoubleRange(annotation);
        } else if (annotation instanceof FloatRange) {
            return createFloatRange(annotation);
        } else if (annotation instanceof LexicalRange) {
            return createLexicalRange(annotation);
        } else if (annotation instanceof LengthRange) {
            return createLengthRange(annotation);
        } else if (annotation instanceof Positive) {
            return new PositiveNegativeRestriction(true, ((Positive)annotation).includesZero());
        } else if (annotation instanceof Negative) {
            return new PositiveNegativeRestriction(false, ((Negative)annotation).includesZero());
        }
        return createUnknownRange(annotation);
    }

    /**
     * Handles turning unknown annotations into range restrictions, derived
     * factories can extend this to add support for additional range
     * restrictions
     * 
     * @param annotation
     *            Annotation to create range from
     * 
     * @return Range restriction or null if not a supported annotation
     */
    protected AbstractCommonRestriction createUnknownRange(Annotation annotation) {
        return null;
    }

    protected AbstractCommonRestriction createLengthRange(Annotation annotation) {
        LengthRange lRange = (LengthRange) annotation;
        return new LengthRestriction(lRange.min(), lRange.max());
    }

    protected RangeRestriction createLexicalRange(Annotation annotation) {
        LexicalRange lRange = (LexicalRange) annotation;
        return new RangeRestriction(StringUtils.isEmpty(lRange.min()) ? null : lRange.min(), lRange.minInclusive(),
                StringUtils.isEmpty(lRange.max()) ? null : lRange.max(), lRange.maxInclusive(),
                new LexicalComparator(Locale.forLanguageTag(lRange.locale())));
    }

    protected RangeRestriction createFloatRange(Annotation annotation) {
        FloatRange sRange = (FloatRange) annotation;
        return new RangeRestriction(
                sRange.min() != Float.MIN_VALUE || !sRange.minInclusive() ? Float.valueOf(sRange.min()) : null,
                sRange.minInclusive(),
                sRange.max() != Float.MAX_VALUE || !sRange.maxInclusive() ? Float.valueOf(sRange.max()) : null,
                sRange.maxInclusive(), FLOAT_COMPARATOR);
    }

    protected RangeRestriction createDoubleRange(Annotation annotation) {
        DoubleRange sRange = (DoubleRange) annotation;
        return new RangeRestriction(
                sRange.min() != Double.MIN_VALUE || !sRange.minInclusive() ? Double.valueOf(sRange.min()) : null,
                sRange.minInclusive(),
                sRange.max() != Double.MAX_VALUE || !sRange.maxInclusive() ? Double.valueOf(sRange.max()) : null,
                sRange.maxInclusive(), DOUBLE_COMPARATOR);
    }

    protected RangeRestriction createByteRange(Annotation annotation) {
        ByteRange sRange = (ByteRange) annotation;
        return new RangeRestriction(Byte.valueOf(sRange.min()), sRange.minInclusive(), Byte.valueOf(sRange.max()),
                sRange.maxInclusive(), BYTE_COMPARATOR);
    }

    protected RangeRestriction createShortRange(Annotation annotation) {
        ShortRange sRange = (ShortRange) annotation;
        return new RangeRestriction(
                sRange.min() != Short.MIN_VALUE || !sRange.minInclusive() ? Short.valueOf(sRange.min()) : null,
                sRange.minInclusive(),
                sRange.max() != Short.MAX_VALUE || !sRange.maxInclusive() ? Short.valueOf(sRange.max()) : null,
                sRange.maxInclusive(), SHORT_COMPARATOR);
    }

    protected RangeRestriction createIntegerRange(Annotation annotation) {
        IntegerRange iRange = (IntegerRange) annotation;
        return new RangeRestriction(
                iRange.min() != Integer.MIN_VALUE || !iRange.minInclusive() ? Integer.valueOf(iRange.min()) : null,
                iRange.minInclusive(),
                iRange.max() != Integer.MAX_VALUE || !iRange.maxInclusive() ? Integer.valueOf(iRange.max()) : null,
                iRange.maxInclusive(), INTEGER_COMPARATOR);
    }

    protected RangeRestriction createLongRange(Annotation annotation) {
        LongRange iRange = (LongRange) annotation;
        return new RangeRestriction(
                iRange.min() != Long.MIN_VALUE || !iRange.minInclusive() ? Long.valueOf(iRange.min()) : null,
                iRange.minInclusive(),
                iRange.max() != Long.MAX_VALUE || !iRange.maxInclusive() ? Long.valueOf(iRange.max()) : null,
                iRange.maxInclusive(), LONG_COMPARATOR);
    }

    protected List<Class<? extends Annotation>> supportedAnnotations() {
        List<Class<? extends Annotation>> supported = new ArrayList<>();
        supported.add(LongRange.class);
        supported.add(IntegerRange.class);
        supported.add(ShortRange.class);
        supported.add(ByteRange.class);
        supported.add(DoubleRange.class);
        supported.add(FloatRange.class);
        supported.add(LexicalRange.class);
        supported.add(LengthRange.class);
        supported.add(Negative.class);
        supported.add(Positive.class);
        return supported;
    }

    @Override
    public List<Class<? extends Annotation>> supportedArgumentsAnnotations() {
        return supportedAnnotations();
    }

    @Override
    public List<Class<? extends Annotation>> supportedOptionAnnotations() {
        return supportedAnnotations();
    }

}
