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
package com.github.rvesse.airline.types.numerics;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.github.rvesse.airline.parser.errors.ParseOptionConversionException;
import com.github.rvesse.airline.types.ConvertResult;

/**
 * Abstract numeric type converter that supports numerics given in the form
 * {@code 1234suffix} where {@code suffix} denotes some multiplier. For example
 * {@code 1234k} might treat {@code k} as a multiplier of {@code 1000}. Derived
 * implementations should be created to provide the set of supported suffixes
 * and their multipliers.
 * 
 * @author rvesse
 *
 */
public abstract class ComplexNumericTypeConverter extends DefaultNumericConverter {

    protected NumericCandidate parse(String value) {
        return new NumericCandidate(value);
    }
    
    protected BigDecimal getBigDecimal(NumericCandidate candidate) {
        return new BigDecimal(candidate.getValue());
    }
    
    protected BigInteger getBigInteger(NumericCandidate candidate) {
        return new BigInteger(candidate.getValue(), getRadix(candidate));
    }

    protected Long getLong(NumericCandidate candidate) {
        return Long.valueOf(candidate.getValue(), getRadix(candidate));
    }

    protected Double getDouble(NumericCandidate candidate) {
        return Double.valueOf(candidate.getValue());
    }

    protected Float getFloat(NumericCandidate candidate) {
        return Float.valueOf(candidate.getValue());
    }

    protected long getMultiplier(NumericCandidate candidate) {
        return 1l;
    }

    protected int getRadix(NumericCandidate candidate) {
        return 10;
    }
    
    @Override
    protected ConvertResult tryConvertBigDecimal(String name, String value) {
        NumericCandidate candidate = parse(value);
        BigDecimal base = getBigDecimal(candidate);
        long multiplier = getMultiplier(candidate);
        BigDecimal result = multiplier != 1l ? base.multiply(BigDecimal.valueOf(multiplier)) : base;
        
        return new ConvertResult(result);
    }

    @Override
    protected ConvertResult tryConvertDouble(String name, String value) {
        NumericCandidate candidate = parse(value);
        double base = getDouble(candidate);
        long multiplier = getMultiplier(candidate);
        double result = multiplier != 1l ? base * multiplier : base;

        if (result < Double.MIN_VALUE || result > Double.MAX_VALUE)
            throw new ParseOptionConversionException(String.format(
                    "%s: Abbreviated numeric value \"%s\" evaluates to a value outside the range of the numeric type %s",
                    name, value, Double.class.getSimpleName()), name, value, Double.class.getSimpleName());

        return new ConvertResult(result);
    }

    @Override
    protected ConvertResult tryConvertFloat(String name, String value) {
        NumericCandidate candidate = parse(value);
        float base = getFloat(candidate);
        long multiplier = getMultiplier(candidate);
        double result = multiplier != 1l ? base * multiplier : base;

        if (result < Float.MIN_VALUE || result > Float.MAX_VALUE)
            throw new ParseOptionConversionException(String.format(
                    "%s: Abbreviated numeric value \"%s\" evaluates to a value outside the range of the numeric type %s",
                    name, value, Float.class.getSimpleName()), name, value, Float.class.getSimpleName());

        return new ConvertResult((float) result);
    }
    
    @Override
    protected ConvertResult tryConvertBigInteger(String name, String value) {
        NumericCandidate candidate = parse(value);
        BigInteger base = getBigInteger(candidate);
        long multiplier = getMultiplier(candidate);
        BigInteger result = multiplier != 1l ? base.multiply(BigInteger.valueOf(multiplier)) : base;
        
        return new ConvertResult(result);
    }

    @Override
    protected ConvertResult tryConvertLong(String name, String value) {
        NumericCandidate candidate = parse(value);
        long base = getLong(candidate);
        long multiplier = getMultiplier(candidate);
        long result = multiplier != 1l ? base * multiplier : base;

        return new ConvertResult(result);
    }

    @Override
    protected ConvertResult tryConvertInteger(String name, String value) {
        NumericCandidate candidate = parse(value);
        long base = getLong(candidate);
        long multiplier = getMultiplier(candidate);
        long result = multiplier != 1l ? base * multiplier : base;

        if (result < Integer.MIN_VALUE || result > Integer.MAX_VALUE)
            throw new ParseOptionConversionException(String.format(
                    "%s: Abbreviated numeric value \"%s\" evaluates to a value outside the range of the numeric type %s",
                    name, value, Integer.class.getSimpleName()), name, value, Integer.class.getSimpleName());

        return new ConvertResult((int) result);
    }

    @Override
    protected ConvertResult tryConvertShort(String name, String value) {
        NumericCandidate candidate = parse(value);
        long base = getLong(candidate);
        long multiplier = getMultiplier(candidate);
        long result = multiplier != 1l ? base * multiplier : base;

        if (result < Short.MIN_VALUE || result > Short.MAX_VALUE)
            throw new ParseOptionConversionException(String.format(
                    "%s: Abbreviated numeric value \"%s\" evaluates to a value outside the range of the numeric type %s",
                    name, value, Short.class.getSimpleName()), name, value, Short.class.getSimpleName());

        return new ConvertResult((short) result);
    }

    @Override
    protected ConvertResult tryConvertByte(String name, String value) {
        NumericCandidate candidate = parse(value);
        long base = getLong(candidate);
        long multiplier = getMultiplier(candidate);
        long result = multiplier != 1l ? base * multiplier : base;

        if (result < Byte.MIN_VALUE || result > Byte.MAX_VALUE)
            throw new ParseOptionConversionException(String.format(
                    "%s: Abbreviated numeric value \"%s\" evaluates to a value outside the range of the numeric type %s",
                    name, value, Byte.class.getSimpleName()), name, value, Byte.class.getSimpleName());

        return new ConvertResult((byte) result);
    }
}
