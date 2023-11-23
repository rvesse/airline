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
package com.github.rvesse.airline.tests.parser;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.rvesse.airline.parser.errors.ParseOptionConversionException;
import com.github.rvesse.airline.types.DefaultTypeConverter;
import com.github.rvesse.airline.types.TypeConverter;

public class TestDefaultTypeConverter {

    private static final String OPTION_NAME = "--test";
    private TypeConverter converter = new DefaultTypeConverter();

    @Test(expectedExceptions = NullPointerException.class)
    public void convert_null_value() {
        converter.convert(OPTION_NAME, String.class, null);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void convert_null_name() {
        converter.convert(null, String.class, "value");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void convert_null_type() {
        converter.convert(OPTION_NAME, null, "value");
    }

    @SuppressWarnings("unchecked")
    private <T> T testConvert(Class<T> cls, String value) {
        return (T) converter.convert(OPTION_NAME, cls, value);
    }

    private static class Unconvertible {

    }

    @Test(expectedExceptions = ParseOptionConversionException.class)
    public void convert_unsupported_type_failure() {
        testConvert(Unconvertible.class, "test");
    }

    @Test(expectedExceptions = ParseOptionConversionException.class)
    public void convert_out_of_range_byte_failure() {
        testConvert(Byte.class, Long.toString(Byte.MAX_VALUE + 1l));
    }

    @Test(expectedExceptions = ParseOptionConversionException.class)
    public void convert_out_of_range_short_failure() {
        testConvert(Short.class, Long.toString(Short.MAX_VALUE + 1l));
    }

    @Test(expectedExceptions = ParseOptionConversionException.class)
    public void convert_out_of_range_int_failure() {
        testConvert(Integer.class, Long.toString(Integer.MAX_VALUE + 1l));
    }

    @Test
    public void convert_string() {
        String value = "test";
        String converted = testConvert(String.class, value);
        Assert.assertEquals(converted, value);
    }

    @Test
    public void convert_boolean() {
        String value = "true";
        Boolean converted = testConvert(Boolean.class, value);
        Assert.assertEquals(converted, Boolean.TRUE);

        value = "false";
        converted = testConvert(Boolean.class, value);
        Assert.assertEquals(converted, Boolean.FALSE);
    }

    @Test
    public void convert_byte() {
        for (byte b = 0; b < Byte.MAX_VALUE; b++) {
            String value = Byte.toString(b);
            Byte converted = testConvert(Byte.class, value);
            Assert.assertEquals(converted.byteValue(), b);
        }
    }

    @Test
    public void convert_short() {
        for (short s = 0, increment = 0; s < Short.MAX_VALUE; increment++, s += increment) {
            String value = Short.toString(s);
            Short converted = testConvert(Short.class, value);
            Assert.assertEquals(converted.shortValue(), s);
        }
    }

    @Test
    public void convert_integer() {
        for (int i = 0, increment = 1; i < Integer.MAX_VALUE; i += increment, increment *= 2) {
            String value = Integer.toString(i);
            Integer converted = testConvert(Integer.class, value);
            Assert.assertEquals(converted.intValue(), i);
        }
    }

    @Test
    public void convert_long() {
        for (long i = 0, increment = 1; i < Long.MAX_VALUE; i += increment, increment *= 2) {
            String value = Long.toString(i);
            Long converted = testConvert(Long.class, value);
            Assert.assertEquals(converted.longValue(), i);
        }
    }

    @Test
    public void convert_float() {
        float[] fs = new float[] { Float.MIN_VALUE, Float.MAX_VALUE, 0.0f, 123.456f, Float.NaN };
        for (float f : fs) {
            String value = Float.toString(f);
            Float converted = testConvert(Float.class, value);
            Assert.assertEquals(converted.floatValue(), f);
        }
    }

    @Test
    public void convert_double() {
        double[] ds = new double[] { Float.MIN_VALUE, Float.MAX_VALUE, 0.0f, 123.456f, Float.NaN, Double.MIN_VALUE,
                Double.MAX_VALUE, Double.NaN, 0.0d, 123.456d };
        for (double d : ds) {
            String value = Double.toString(d);
            Double converted = testConvert(Double.class, value);
            Assert.assertEquals(converted.doubleValue(), d);
        }
    }

    public static class ConversionExample {
        public final String value;

        public ConversionExample(String value) {
            this.value = value;
        }
    }

    public static class FromStringable extends ConversionExample {

        private FromStringable(String value) {
            super(value);
        }

        public static FromStringable fromString(String value) {
            return new FromStringable(value);
        }
    }

    @Test
    public void convert_static_fromString() {
        String value = "test";
        FromStringable converted = testConvert(FromStringable.class, value);
        Assert.assertEquals(converted.value, value);
    }

    @Test
    public void convert_constructor() {
        String value = "test";
        ConversionExample converted = testConvert(ConversionExample.class, value);
        Assert.assertEquals(converted.value, value);
    }

    public static enum ConversionEnum {
        FOO, BAR
    }

    @Test
    public void convert_enum() {
        for (ConversionEnum item : ConversionEnum.values()) {
            String value = item.name();
            ConversionEnum converted = testConvert(ConversionEnum.class, value);
            Assert.assertEquals(converted, item);
        }
    }
}
