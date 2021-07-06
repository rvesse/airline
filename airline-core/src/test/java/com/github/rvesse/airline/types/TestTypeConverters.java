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
package com.github.rvesse.airline.types;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.rvesse.airline.SingleCommand;
import com.github.rvesse.airline.args.Args1;
import com.github.rvesse.airline.parser.errors.ParseOptionConversionException;
import com.github.rvesse.airline.types.numerics.DefaultNumericConverter;
import com.github.rvesse.airline.types.numerics.NumericTypeConverter;
import com.github.rvesse.airline.types.numerics.abbreviated.KiloAs1000;
import com.github.rvesse.airline.types.numerics.abbreviated.KiloAs1024;
import com.github.rvesse.airline.types.numerics.bases.Binary;
import com.github.rvesse.airline.types.numerics.bases.Hexadecimal;
import com.github.rvesse.airline.types.numerics.bases.Octal;

public class TestTypeConverters {

    private static final int NUMBER_RANDOM_TESTS = 100000;
    private static final Class<?>[] NUMERIC_TYPES = new Class<?>[] { Long.class, Integer.class, Short.class, Byte.class,
            Double.class, Float.class, BigInteger.class, BigDecimal.class };
    private static final Class<?>[] INTEGER_TYPES = new Class<?>[] { Long.class, Integer.class, Short.class, Byte.class,
            BigInteger.class };

    @Test
    public void numeric_default_bad_01() {
        String badValue = "test";
        checkBadConversion(badValue);
    }

    @Test
    public void numeric_default_bad_02() {
        String badValue = "NaN";
        checkBadIntegerConversion(badValue);
    }

    private void checkBadConversion(String badValue) {
        checkBadConversion(badValue, NUMERIC_TYPES);
    }

    private void checkBadIntegerConversion(String badValue) {
        checkBadConversion(badValue, INTEGER_TYPES);
    }

    private void checkBadConversion(String badValue, Class<?>... types) {
        for (Class<?> type : types) {
            NumericTypeConverter converter = new DefaultNumericConverter();
            ConvertResult result = converter.tryConvertNumerics("test", type, badValue);
            Assert.assertFalse(result.wasSuccessfull());
            Assert.assertNull(result.getConvertedValue());
        }
    }

    @Test
    public void numeric_case_insensitivity_01() {
        NumericTypeConverter converter = new KiloAs1024();
        ConvertResult result = converter.tryConvertNumerics("test", Long.class, "4GB");
        Assert.assertTrue(result.wasSuccessfull());
        Assert.assertEquals((long) result.getConvertedValue(), 4l * 1024l * 1024l * 1024l);
    }

    @Test
    public void numeric_default_int_01() {
        NumericTypeConverter converter = new DefaultNumericConverter();

        Random random = new Random();
        for (int i = 0; i < NUMBER_RANDOM_TESTS; i++) {
            int number = random.nextInt();
            ConvertResult result = converter.tryConvertNumerics("test", Integer.class, Integer.toString(number));
            Assert.assertTrue(result.wasSuccessfull());
            Assert.assertEquals((int) result.getConvertedValue(), number);
        }
    }

    @Test
    public void numeric_default_big_integer_01() {
        NumericTypeConverter converter = new DefaultNumericConverter();

        Random random = new Random();
        for (int i = 0; i < NUMBER_RANDOM_TESTS; i++) {
            long number = random.nextLong();
            ConvertResult result = converter.tryConvertNumerics("test", BigInteger.class,
                    BigInteger.valueOf(number).toString());
            Assert.assertTrue(result.wasSuccessfull());
            Assert.assertEquals(((BigInteger) result.getConvertedValue()).longValue(), number);
        }
    }

    @Test
    public void numeric_default_long_01() {
        NumericTypeConverter converter = new DefaultNumericConverter();

        Random random = new Random();
        for (int i = 0; i < NUMBER_RANDOM_TESTS; i++) {
            long number = random.nextLong();
            ConvertResult result = converter.tryConvertNumerics("test", Long.class, Long.toString(number));
            Assert.assertTrue(result.wasSuccessfull());
            Assert.assertEquals((long) result.getConvertedValue(), number);
        }
    }

    @Test
    public void numeric_default_short_01() {
        NumericTypeConverter converter = new DefaultNumericConverter();

        Random random = new Random();
        for (int i = 0; i < NUMBER_RANDOM_TESTS; i++) {
            short number = (short) random.nextInt(Short.MAX_VALUE + 1);
            ConvertResult result = converter.tryConvertNumerics("test", Short.class, Short.toString(number));
            Assert.assertTrue(result.wasSuccessfull());
            Assert.assertEquals((short) result.getConvertedValue(), number);
        }
    }

    @Test
    public void numeric_default_byte_01() {
        NumericTypeConverter converter = new DefaultNumericConverter();

        Random random = new Random();
        for (int i = 0; i < NUMBER_RANDOM_TESTS; i++) {
            byte number = (byte) random.nextInt(Byte.MAX_VALUE + 1);
            ConvertResult result = converter.tryConvertNumerics("test", Byte.class, Byte.toString(number));
            Assert.assertTrue(result.wasSuccessfull());
            Assert.assertEquals((byte) result.getConvertedValue(), number);
        }
    }

    @Test
    public void numeric_default_float_01() {
        NumericTypeConverter converter = new DefaultNumericConverter();

        Random random = new Random();
        for (int i = 0; i < NUMBER_RANDOM_TESTS; i++) {
            float number = random.nextFloat();
            ConvertResult result = converter.tryConvertNumerics("test", Float.class, Float.toString(number));
            Assert.assertTrue(result.wasSuccessfull());
            Assert.assertEquals((float) result.getConvertedValue(), number);
        }
    }

    @Test
    public void numeric_default_float_02() {
        NumericTypeConverter converter = new DefaultNumericConverter();
        ConvertResult result = converter.tryConvertNumerics("test", Float.class, "NaN");
        Assert.assertTrue(result.wasSuccessfull());
        Assert.assertEquals(result.getConvertedValue(), Float.NaN);
    }

    @Test
    public void numeric_default_double_01() {
        NumericTypeConverter converter = new DefaultNumericConverter();

        Random random = new Random();
        for (int i = 0; i < NUMBER_RANDOM_TESTS; i++) {
            double number = random.nextDouble();
            ConvertResult result = converter.tryConvertNumerics("test", Double.class, Double.toString(number));
            Assert.assertTrue(result.wasSuccessfull());
            Assert.assertEquals((double) result.getConvertedValue(), number);
        }
    }

    @Test
    public void numeric_default_double_02() {
        NumericTypeConverter converter = new DefaultNumericConverter();
        ConvertResult result = converter.tryConvertNumerics("test", Double.class, "NaN");
        Assert.assertTrue(result.wasSuccessfull());
        Assert.assertEquals(result.getConvertedValue(), Double.NaN);
    }

    @Test
    public void numeric_default_big_decimal_01() {
        NumericTypeConverter converter = new DefaultNumericConverter();

        Random random = new Random();
        for (int i = 0; i < NUMBER_RANDOM_TESTS; i++) {
            double number = random.nextDouble();
            ConvertResult result = converter.tryConvertNumerics("test", BigDecimal.class,
                    new BigDecimal(number).toString());
            Assert.assertTrue(result.wasSuccessfull());
            Assert.assertEquals(((BigDecimal) result.getConvertedValue()).doubleValue(), number);
        }
    }

    private void checkIntegerAbbreviationKilo(NumericTypeConverter converter, long multiplier, long min, long max,
            Class<?> type, long divisor, String suffix) {
        Random random = new Random();
        int good = 0, bad = 0;
        for (int i = 0; i < NUMBER_RANDOM_TESTS; i++) {
            long number = random.nextLong();
            if (number < divisor)
                number = number + divisor;
            if (number % divisor != 0)
                number -= (number % divisor);

            ConvertResult result = converter.tryConvertNumerics("test", type,
                    String.format("%d%s", number / divisor, suffix));
            if (number < min || number > max) {
                Assert.assertFalse(result.wasSuccessfull());
                bad++;
            } else {
                if (!result.wasSuccessfull())
                    System.out.println(String.format("Expected abbreviation %d%s to expand to %d but failed",
                            number / divisor, suffix, number));
                Assert.assertTrue(result.wasSuccessfull());
                if (type.equals(BigInteger.class)) {
                    Assert.assertEquals((BigInteger) result.getConvertedValue(), BigInteger.valueOf(number));
                } else {
                    Assert.assertEquals(result.getConvertedValue(), number);
                }
                good++;
            }
        }

        System.out.println(String.format(
                "Ran %,d test cases for %s with settings (mult=%,d, min=%,d, max=%,d, type=%s, divisor=%,d, suffix=%s) with %,d good values and %,d bad values",
                NUMBER_RANDOM_TESTS, converter.getClass(), multiplier, min, max, type, divisor, suffix, good, bad));
    }

    private ConvertResult doConversion(NumericTypeConverter converter, String value, Class<?> type) {
        return converter.tryConvertNumerics("test", type, value);
    }

    private void checkGoodConversion(NumericTypeConverter converter, String value, Class<?> type, Object expected) {
        ConvertResult result = doConversion(converter, value, type);
        Assert.assertTrue(result.wasSuccessfull());
        Assert.assertEquals(result.getConvertedValue(), expected);
    }

    private void checkBadConversion(NumericTypeConverter converter, String value, Class<?> type) {
        ConvertResult result = doConversion(converter, value, type);
        Assert.assertFalse(result.wasSuccessfull());
    }

    @Test
    public void numeric_kilo_1000_specifics() {
        KiloAs1000 converter = new KiloAs1000();
        checkGoodConversion(converter, "1k", Short.class, (short) 1000);
        checkGoodConversion(converter, "10k", Short.class, (short) 10000);
        checkBadConversion(converter, "100k", Short.class);
    }

    @Test
    public void numeric_kilo_1000_big_integer_01() {
        checkIntegerAbbreviationKilo(new KiloAs1000(), 1000, Long.MIN_VALUE, Long.MAX_VALUE, BigInteger.class, 1000l,
                "k");
    }

    @Test
    public void numeric_kilo_1000_big_integer_02() {
        checkIntegerAbbreviationKilo(new KiloAs1000(), 1000, Long.MIN_VALUE, Long.MAX_VALUE, BigInteger.class,
                1000l * 1000l, "m");
    }

    @Test
    public void numeric_kilo_1000_big_integer_03() {
        checkIntegerAbbreviationKilo(new KiloAs1000(), 1000, Long.MIN_VALUE, Long.MAX_VALUE, BigInteger.class,
                1000l * 1000l * 1000l, "b");
    }

    @Test
    public void numeric_kilo_1000_big_integer_04() {
        checkIntegerAbbreviationKilo(new KiloAs1000(), 1000, Long.MIN_VALUE, Long.MAX_VALUE, BigInteger.class,
                1000l * 1000l * 1000l * 1000l, "t");
    }

    @Test
    public void numeric_kilo_1000_long_01() {
        checkIntegerAbbreviationKilo(new KiloAs1000(), 1000, Long.MIN_VALUE, Long.MAX_VALUE, Long.class, 1000l, "k");
    }

    @Test
    public void numeric_kilo_1000_long_02() {
        checkIntegerAbbreviationKilo(new KiloAs1000(), 1000, Long.MIN_VALUE, Long.MAX_VALUE, Long.class, 1000l * 1000l,
                "m");
    }

    @Test
    public void numeric_kilo_1000_long_03() {
        checkIntegerAbbreviationKilo(new KiloAs1000(), 1000, Long.MIN_VALUE, Long.MAX_VALUE, Long.class,
                1000l * 1000l * 1000l, "b");
    }

    @Test
    public void numeric_kilo_1000_long_04() {
        checkIntegerAbbreviationKilo(new KiloAs1000(), 1000, Long.MIN_VALUE, Long.MAX_VALUE, Long.class,
                1000l * 1000l * 1000l * 1000l, "t");
    }

    @Test
    public void numeric_kilo_1000_integer_01() {
        checkIntegerAbbreviationKilo(new KiloAs1000(), 1000, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.class, 1000l,
                "k");
    }

    @Test
    public void numeric_kilo_1000_integer_02() {
        checkIntegerAbbreviationKilo(new KiloAs1000(), 1000, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.class,
                1000l * 1000l, "m");
    }

    @Test
    public void numeric_kilo_1000_integer_03() {
        checkIntegerAbbreviationKilo(new KiloAs1000(), 1000, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.class,
                1000l * 1000l * 1000l, "b");
    }

    @Test
    public void numeric_kilo_1000_short_01() {
        checkIntegerAbbreviationKilo(new KiloAs1000(), 1000, Short.MIN_VALUE, Short.MAX_VALUE, Short.class, 1000l, "k");
    }

    @Test
    public void numeric_kilo_1000_short_02() {
        checkIntegerAbbreviationKilo(new KiloAs1000(), 1000, Short.MIN_VALUE, Short.MAX_VALUE, Short.class,
                1000l * 1000l, "m");
    }

    @Test
    public void numeric_kilo_1024_long_01() {
        checkIntegerAbbreviationKilo(new KiloAs1024(), 1024, Long.MIN_VALUE, Long.MAX_VALUE, Long.class, 1024l, "k");
    }

    @Test
    public void numeric_kilo_1024_long_02() {
        checkIntegerAbbreviationKilo(new KiloAs1024(), 1024, Long.MIN_VALUE, Long.MAX_VALUE, Long.class, 1024l * 1024l,
                "m");
    }

    @Test
    public void numeric_kilo_1024_long_03() {
        checkIntegerAbbreviationKilo(new KiloAs1024(), 1024, Long.MIN_VALUE, Long.MAX_VALUE, Long.class,
                1024l * 1024l * 1024l, "g");
    }

    @Test
    public void numeric_kilo_1024_long_04() {
        checkIntegerAbbreviationKilo(new KiloAs1024(), 1024, Long.MIN_VALUE, Long.MAX_VALUE, Long.class,
                1024l * 1024l * 1024l * 1024l, "t");
    }

    @Test
    public void numeric_kilo_1024_integer_01() {
        checkIntegerAbbreviationKilo(new KiloAs1024(), 1024, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.class, 1024l,
                "k");
    }

    @Test
    public void numeric_kilo_1024_integer_02() {
        checkIntegerAbbreviationKilo(new KiloAs1024(), 1024, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.class,
                1024l * 1024l, "m");
    }

    @Test
    public void numeric_kilo_1024_integer_03() {
        checkIntegerAbbreviationKilo(new KiloAs1024(), 1024, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.class,
                1024l * 1024l * 1024l, "g");
    }

    @Test
    public void numeric_kilo_1024_integer_04() {
        checkIntegerAbbreviationKilo(new KiloAs1024(), 1024, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.class,
                1024l * 1024l * 1024l, "G");
    }

    @Test
    public void numeric_kilo_1024_short_01() {
        checkIntegerAbbreviationKilo(new KiloAs1024(), 1024, Short.MIN_VALUE, Short.MAX_VALUE, Short.class, 1024l, "k");
    }

    @Test
    public void numeric_kilo_1024_short_02() {
        checkIntegerAbbreviationKilo(new KiloAs1024(), 1024, Short.MIN_VALUE, Short.MAX_VALUE, Short.class, 1024l, "k");
    }

    private void checkAlternateRadix(NumericTypeConverter converter, int radix, long min, long max, Class<?> type) {
        Random random = new Random();
        int good = 0, bad = 0;
        for (int i = 0; i < NUMBER_RANDOM_TESTS; i++) {
            long number = random.nextLong();

            ConvertResult result = converter.tryConvertNumerics("test", type,
                    String.format("%s", Long.toString(number, radix)));
            if (number < min || number > max) {
                Assert.assertFalse(result.wasSuccessfull());
                bad++;
            } else {
                if (!result.wasSuccessfull())
                    System.out.println(String.format("Expected radix %d representation %s to expand to %d but failed",
                            radix, Long.toString(number, radix), number));
                Assert.assertTrue(result.wasSuccessfull());
                if (type.equals(BigInteger.class)) {
                    Assert.assertEquals((BigInteger) result.getConvertedValue(), BigInteger.valueOf(number));
                } else {
                    Assert.assertEquals(result.getConvertedValue(), number);
                }
                good++;
            }
        }

        System.out.println(String.format(
                "Ran %,d test cases for %s with settings (radix=%,d, min=%,d, max=%,d, type=%s) with %,d good values and %,d bad values",
                NUMBER_RANDOM_TESTS, converter.getClass(), radix, min, max, type, good, bad));
    }

    private void checkAlternateRadix(NumericTypeConverter converter, int radix) {
        checkAlternateRadix(converter, radix, Long.MIN_VALUE, Long.MAX_VALUE, Long.class);
        checkAlternateRadix(converter, radix, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.class);
        checkAlternateRadix(converter, radix, Short.MIN_VALUE, Short.MAX_VALUE, Short.class);
        checkAlternateRadix(converter, radix, Byte.MIN_VALUE, Byte.MAX_VALUE, Byte.class);
        checkAlternateRadix(converter, radix, Long.MIN_VALUE, Long.MAX_VALUE, BigInteger.class);
    }

    @Test
    public void numeric_radix_16() {
        NumericTypeConverter converter = new Hexadecimal();
        checkAlternateRadix(converter, 16);
    }

    @Test
    public void numeric_radix_8() {
        NumericTypeConverter converter = new Octal();
        checkAlternateRadix(converter, 8);
    }

    @Test
    public void numeric_radix_2() {
        NumericTypeConverter converter = new Binary();
        checkAlternateRadix(converter, 2);
    }

    @Test
    public void command_mixed_converters_01() {
        SingleCommand<ArgsRadix> cmd = SingleCommand.singleCommand(ArgsRadix.class);
        long value = 47000;
        //@formatter:off
        ArgsRadix radix 
            = cmd.parse(
                    "--normal", Long.toString(value), 
                    "--hex", Long.toString(value, 16), 
                    "--octal", Long.toString(value, 8), 
                    "--binary", Long.toString(value, 2), 
                    "--kilo", (value / 1000) + "k",
                    "--big-octal", BigInteger.valueOf(value).toString(8), 
                    "--big-hex", BigInteger.valueOf(value).toString(16), 
                    "--big-binary", BigInteger.valueOf(value).toString(2)
                );
        //@formatter:on

        Assert.assertEquals(radix.normal, value);
        Assert.assertEquals(radix.hex, value);
        Assert.assertEquals(radix.octal, value);
        Assert.assertEquals(radix.binary, value);
        Assert.assertEquals(radix.abbrev, value);
        Assert.assertEquals(radix.bigBinary, BigInteger.valueOf(value));
        Assert.assertEquals(radix.bigHex, BigInteger.valueOf(value));
        Assert.assertEquals(radix.bigOctal, BigInteger.valueOf(value));
    }

    @Test
    public void command_mixed_converters_02() {
        SingleCommand<ArgsRadix> cmd = SingleCommand.singleCommand(ArgsRadix.class);
        long value = 47000;
        //@formatter:off
        ArgsRadix radix 
            = cmd.parse(
                    "--normal", Long.toString(value), 
                    "--hex", Long.toString(value), 
                    "--octal", Long.toString(value),
                    "--big-octal", BigInteger.valueOf(value).toString(),
                    "--big-hex", BigInteger.valueOf(value).toString()
                );
        //@formatter:on

        Assert.assertEquals(radix.normal, value);
        Assert.assertEquals(radix.hex, Long.parseLong(Long.toString(value), 16));
        Assert.assertEquals(radix.octal, Long.parseLong(Long.toString(value), 8));
        Assert.assertEquals(radix.bigHex, new BigInteger(Long.toString(value), 16));
        Assert.assertEquals(radix.bigOctal, new BigInteger(Long.toString(value), 8));
    }

    @Test
    public void command_mixed_converters_03() {
        SingleCommand<ArgsRadix> cmd = SingleCommand.singleCommand(ArgsRadix.class);
        long value = 47000;
        ArgsRadix radix = cmd.parse("--kilo", Long.toString(value));

        Assert.assertEquals(radix.abbrev, value);
    }

    @Test(expectedExceptions = ParseOptionConversionException.class)
    public void command_mixed_converters_04() {
        SingleCommand<ArgsRadix> cmd = SingleCommand.singleCommand(ArgsRadix.class);
        long value = 47000;
        cmd.parse("--binary", Long.toString(value));
    }
    
    @Test
    public void path_01() {
        SingleCommand<Args1> cmd = SingleCommand.singleCommand(Args1.class);
        Args1 args = cmd.parse("-path", "/foo");
        Assert.assertEquals(args.path.toString(), "/foo");
    }
    
    @Test
    public void path_02() {
        SingleCommand<Args1> cmd = SingleCommand.singleCommand(Args1.class);
        Args1 args = cmd.parse("-path", "");
        Assert.assertEquals(args.path.toString(), "");
    }
}
