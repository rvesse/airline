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
package com.github.rvesse.airline.restrictions.ranges;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.rvesse.airline.SingleCommand;
import com.github.rvesse.airline.TestingUtil;
import com.github.rvesse.airline.help.Help;
import com.github.rvesse.airline.model.CommandMetadata;
import com.github.rvesse.airline.model.OptionMetadata;
import com.github.rvesse.airline.parser.errors.ParseInvalidRestrictionException;
import com.github.rvesse.airline.parser.errors.ParseOptionOutOfRangeException;
import com.github.rvesse.airline.parser.errors.ParseRestrictionViolatedException;
import com.github.rvesse.airline.restrictions.OptionRestriction;
import com.github.rvesse.airline.restrictions.common.PositiveNegativeRestriction;
import com.github.rvesse.airline.restrictions.common.RangeRestriction;

public class TestRangeRestrictions {

    private void hasRangeRestriction(CommandMetadata metadata) {
        hasRestriction(metadata, RangeRestriction.class);
    }
    
    private void hasRestriction(CommandMetadata metadata, Class<?> restrictionType) {
        for (OptionMetadata option : metadata.getAllOptions()) {
            for (OptionRestriction restriction : option.getRestrictions()) {
                if (restriction.getClass().isAssignableFrom(restrictionType));
                    return;
            }
        }
        Assert.fail("No RangeRestriction found");
    }

    private void checkHelp(SingleCommand<? extends OptionRangeBase> parser, String[] included, String[] excluded) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Help.help(parser.getCommandMetadata(), output);
        String usage = new String(output.toByteArray(), StandardCharsets.UTF_8);

        for (String include : included) {
            Assert.assertTrue(usage.contains(include));
        }
        for (String exclude : excluded) {
            Assert.assertFalse(usage.contains(exclude));
        }
    }

    @Test
    public void integer_range_inclusive() throws IOException {
        SingleCommand<? extends OptionRangeBase> parser = TestingUtil
                .singleCommandParser(OptionIntegerRangeInclusive.class);
        hasRangeRestriction(parser.getCommandMetadata());

        for (int i = 0; i <= 100; i++) {
            OptionRangeBase cmd = parser.parse("-i", Integer.toString(i));
            Assert.assertEquals(cmd.i, i);
        }
        
        checkHelp(parser, new String[] { "0 <= value <= 100" }, new String[0]);
    }

    @Test(expectedExceptions = ParseOptionOutOfRangeException.class, expectedExceptionsMessageRegExp = ".*(0 <= value <= 100).*")
    public void integer_range_inclusive_below_min() {
        SingleCommand<? extends OptionRangeBase> parser = TestingUtil
                .singleCommandParser(OptionIntegerRangeInclusive.class);
        hasRangeRestriction(parser.getCommandMetadata());
        parser.parse("-i", "-1");
    }

    @Test(expectedExceptions = ParseOptionOutOfRangeException.class, expectedExceptionsMessageRegExp = ".*(0 <= value <= 100).*")
    public void integer_range_inclusive_above_max() {
        SingleCommand<? extends OptionRangeBase> parser = TestingUtil
                .singleCommandParser(OptionIntegerRangeInclusive.class);
        hasRangeRestriction(parser.getCommandMetadata());
        parser.parse("-i", "101");
    }

    @Test
    public void integer_range_inclusive_min() throws IOException {
        SingleCommand<? extends OptionRangeBase> parser = TestingUtil
                .singleCommandParser(OptionIntegerRangeInclusiveMin.class);
        hasRangeRestriction(parser.getCommandMetadata());

        for (int i = 0; i < 100; i++) {
            OptionRangeBase cmd = parser.parse("-i", Integer.toString(i));
            Assert.assertEquals(cmd.i, i);
        }
        
        checkHelp(parser, new String[] { "0 <= value < 100" }, new String[0]);
    }

    @Test(expectedExceptions = ParseOptionOutOfRangeException.class, expectedExceptionsMessageRegExp = ".*(0 <= value < 100).*")
    public void integer_range_inclusive_min_below_min() {
        SingleCommand<? extends OptionRangeBase> parser = TestingUtil
                .singleCommandParser(OptionIntegerRangeInclusiveMin.class);
        hasRangeRestriction(parser.getCommandMetadata());
        parser.parse("-i", "-1");
    }

    @Test(expectedExceptions = ParseOptionOutOfRangeException.class, expectedExceptionsMessageRegExp = ".*(0 <= value < 100).*")
    public void integer_range_inclusive_min_at_max() {
        SingleCommand<? extends OptionRangeBase> parser = TestingUtil
                .singleCommandParser(OptionIntegerRangeInclusiveMin.class);
        hasRangeRestriction(parser.getCommandMetadata());
        parser.parse("-i", "100");
    }

    @Test(expectedExceptions = ParseOptionOutOfRangeException.class, expectedExceptionsMessageRegExp = ".*(0 <= value < 100).*")
    public void integer_range_inclusive_min_above_max() {
        SingleCommand<? extends OptionRangeBase> parser = TestingUtil
                .singleCommandParser(OptionIntegerRangeInclusiveMin.class);
        hasRangeRestriction(parser.getCommandMetadata());
        parser.parse("-i", "101");
    }

    @Test
    public void integer_range_inclusive_max() throws IOException {
        SingleCommand<? extends OptionRangeBase> parser = TestingUtil
                .singleCommandParser(OptionIntegerRangeInclusiveMax.class);
        hasRangeRestriction(parser.getCommandMetadata());

        for (int i = 1; i <= 100; i++) {
            OptionRangeBase cmd = parser.parse("-i", Integer.toString(i));
            Assert.assertEquals(cmd.i, i);
        }
        
        checkHelp(parser, new String[] { "0 < value <= 100" }, new String[0]);
    }

    @Test(expectedExceptions = ParseOptionOutOfRangeException.class, expectedExceptionsMessageRegExp = ".*(0 < value <= 100).*")
    public void integer_range_inclusive_max_below_min() {
        SingleCommand<? extends OptionRangeBase> parser = TestingUtil
                .singleCommandParser(OptionIntegerRangeInclusiveMax.class);
        hasRangeRestriction(parser.getCommandMetadata());
        parser.parse("-i", "-1");
    }

    @Test(expectedExceptions = ParseOptionOutOfRangeException.class, expectedExceptionsMessageRegExp = ".*(0 < value <= 100).*")
    public void integer_range_inclusive_max_at_min() {
        SingleCommand<? extends OptionRangeBase> parser = TestingUtil
                .singleCommandParser(OptionIntegerRangeInclusiveMax.class);
        hasRangeRestriction(parser.getCommandMetadata());
        parser.parse("-i", "0");
    }

    @Test(expectedExceptions = ParseOptionOutOfRangeException.class, expectedExceptionsMessageRegExp = ".*(0 < value <= 100).*")
    public void integer_range_inclusive_max_above_max() {
        SingleCommand<? extends OptionRangeBase> parser = TestingUtil
                .singleCommandParser(OptionIntegerRangeInclusiveMax.class);
        hasRangeRestriction(parser.getCommandMetadata());
        parser.parse("-i", "101");
    }

    @Test
    public void integer_range_single_value() throws IOException {
        SingleCommand<? extends OptionRangeBase> parser = TestingUtil
                .singleCommandParser(OptionIntegerRangeSingleValue.class);
        hasRangeRestriction(parser.getCommandMetadata());

        OptionRangeBase cmd = parser.parse("-i", "0");
        Assert.assertEquals(cmd.i, 0);
        
        checkHelp(parser, new String[] { "value == 0" }, new String[0]);
    }
    
    @Test
    public void integer_range_complete() throws IOException {
        SingleCommand<? extends OptionRangeBase> parser = TestingUtil
                .singleCommandParser(OptionIntegerRangeComplete.class);
        hasRangeRestriction(parser.getCommandMetadata());

        OptionRangeBase cmd = parser.parse("-i", Long.toString(Long.MAX_VALUE));
        Assert.assertEquals(cmd.i, Long.MAX_VALUE);
        
        checkHelp(parser, new String[0], new String[] { String.format("%s <= value <= %s", Long.MIN_VALUE, Long.MAX_VALUE) });
    }
    
    @Test
    public void integer_range_complete_exclusive() throws IOException {
        SingleCommand<? extends OptionRangeBase> parser = TestingUtil
                .singleCommandParser(OptionIntegerRangeCompleteExclusive.class);
        hasRangeRestriction(parser.getCommandMetadata());

        OptionRangeBase cmd = parser.parse("-i", Long.toString(Long.MAX_VALUE - 1));
        Assert.assertEquals(cmd.i, Long.MAX_VALUE - 1);
        
        checkHelp(parser, new String[] { String.format("%s < value < %s", Long.MIN_VALUE, Long.MAX_VALUE) }, new String[0]);
    }

    @Test(expectedExceptions = ParseInvalidRestrictionException.class)
    public void integer_range_invalid_01() {
        TestingUtil.singleCommandParser(OptionIntegerRangeInvalid.class);
    }

    @Test(expectedExceptions = ParseInvalidRestrictionException.class)
    public void integer_range_invalid_02() {
        TestingUtil.singleCommandParser(OptionIntegerRangeInvalidSingleValue.class);
    }
    
    @Test
    public void integer_range_positive_01() throws IOException {
        SingleCommand<? extends OptionRangeBase> parser = TestingUtil
                .singleCommandParser(OptionRangePositive.class);
        hasRestriction(parser.getCommandMetadata(), PositiveNegativeRestriction.class);

        OptionRangeBase cmd = parser.parse("-i", Integer.toString(0));
        for (int i = 1; i <= 32678d; i *= 2) {
            cmd = parser.parse("-i", Integer.toString(i));
            Assert.assertEquals(cmd.i, i);
        }
        
        checkHelp(parser, new String[] { "positive value", "value >= 0" }, new String[] { "negative value" });
    }
    
    @Test
    public void integer_range_positive_02() throws IOException {
        SingleCommand<? extends OptionRangeBase> parser = TestingUtil
                .singleCommandParser(OptionRangePositiveExcludesZero.class);
        hasRestriction(parser.getCommandMetadata(), PositiveNegativeRestriction.class);

        for (int i = 1; i <= 32678d; i *= 2) {
            OptionRangeBase cmd = parser.parse("-i", Integer.toString(i));
            Assert.assertEquals(cmd.i, i);
        }
        
        checkHelp(parser, new String[] { "positive value", "value > 0" }, new String[] { "negative value" });
    }
    
    @Test(expectedExceptions = ParseRestrictionViolatedException.class)
    public void integer_range_positive_03() throws IOException {
        SingleCommand<? extends OptionRangeBase> parser = TestingUtil
                .singleCommandParser(OptionRangePositiveExcludesZero.class);
        hasRestriction(parser.getCommandMetadata(), PositiveNegativeRestriction.class);
        parser.parse("-i", Integer.toString(0));
    }
    
    @Test
    public void integer_range_negative_01() throws IOException {
        SingleCommand<? extends OptionRangeBase> parser = TestingUtil
                .singleCommandParser(OptionRangeNegative.class);
        hasRestriction(parser.getCommandMetadata(), PositiveNegativeRestriction.class);

        for (int i = -1; i >= -32678d; i = -(i * -2)) {
            OptionRangeBase cmd = parser.parse("-i", Integer.toString(i));
            Assert.assertEquals(cmd.i, i);
        }
        
        checkHelp(parser, new String[] { "negative value", "value < 0" }, new String[] { "positive value" });
    }
    
    @Test
    public void integer_range_negative_02() throws IOException {
        SingleCommand<? extends OptionRangeBase> parser = TestingUtil
                .singleCommandParser(OptionRangeNegativeIncludesZero.class);
        hasRestriction(parser.getCommandMetadata(), PositiveNegativeRestriction.class);

        OptionRangeBase cmd = parser.parse("-i", Integer.toString(0));
        for (int i = -1; i >= -32678d; i = -(i * -2)) {
            cmd = parser.parse("-i", Integer.toString(i));
            Assert.assertEquals(cmd.i, i);
        }
        
        checkHelp(parser, new String[] { "negative value", "value <= 0" }, new String[] { "positive value" });
    }

    @Test
    public void double_range_inclusive() throws IOException {
        SingleCommand<? extends OptionRangeBase> parser = TestingUtil
                .singleCommandParser(OptionDoubleRangeInclusive.class);
        hasRangeRestriction(parser.getCommandMetadata());

        for (double d = 0d; d <= 1.0d; d += 0.01d) {
            OptionRangeBase cmd = parser.parse("-d", Double.toString(d));
            Assert.assertEquals(cmd.d, d);
        }
        
        checkHelp(parser, new String[] { "0 <= value <= 1.0" }, new String[0]);
    }
    
    @Test
    public void double_range_positive() throws IOException {
        SingleCommand<? extends OptionRangeBase> parser = TestingUtil
                .singleCommandParser(OptionRangePositive.class);
        hasRestriction(parser.getCommandMetadata(), PositiveNegativeRestriction.class);

        OptionRangeBase cmd = parser.parse("-d", Double.toString(0d));
        for (double d = 1d; d <= 32678d; d *= 2d) {
            cmd = parser.parse("-d", Double.toString(d));
            Assert.assertEquals(cmd.d, d);
        }
        
        checkHelp(parser, new String[] { "positive value", "value >= 0" }, new String[] { "negative value" });
    }
    
    @Test
    public void double_range_negative() throws IOException {
        SingleCommand<? extends OptionRangeBase> parser = TestingUtil
                .singleCommandParser(OptionRangeNegative.class);
        hasRestriction(parser.getCommandMetadata(), PositiveNegativeRestriction.class);

        for (double d = -1d; d >= -32678d; d = -(d * -2d)) {
            OptionRangeBase cmd = parser.parse("-d", Double.toString(d));
            Assert.assertEquals(cmd.d, d);
        }
        
        checkHelp(parser, new String[] { "negative value", "value < 0" }, new String[] { "positive value" });
    }

    @Test(expectedExceptions = ParseOptionOutOfRangeException.class, expectedExceptionsMessageRegExp = ".*(0.0 <= value <= 1.0).*")
    public void double_range_inclusive_below_min() {
        SingleCommand<? extends OptionRangeBase> parser = TestingUtil
                .singleCommandParser(OptionDoubleRangeInclusive.class);
        hasRangeRestriction(parser.getCommandMetadata());
        parser.parse("-d", "-1.0");
    }

    @Test(expectedExceptions = ParseOptionOutOfRangeException.class, expectedExceptionsMessageRegExp = ".*(0.0 <= value <= 1.0).*")
    public void double_range_inclusive_above_max() {
        SingleCommand<? extends OptionRangeBase> parser = TestingUtil
                .singleCommandParser(OptionDoubleRangeInclusive.class);
        hasRangeRestriction(parser.getCommandMetadata());
        parser.parse("-d", "1.01");
    }

    @Test
    public void lexical_range_inclusive() throws IOException {
        SingleCommand<? extends OptionRangeBase> parser = TestingUtil
                .singleCommandParser(OptionLexicalRangeInclusive.class);
        hasRangeRestriction(parser.getCommandMetadata());
        parser.parse("-s", "aardvark");
        parser.parse("-s", "bear");
        parser.parse("-s", "coyote");
        parser.parse("-s", "d");
        
        checkHelp(parser, new String[] { "a <= value <= d" }, new String[0]);
    }

    @Test(expectedExceptions = ParseOptionOutOfRangeException.class, expectedExceptionsMessageRegExp = ".*(a <= value <= d).*")
    public void lexical_range_inclusive_above_max() {
        SingleCommand<? extends OptionRangeBase> parser = TestingUtil
                .singleCommandParser(OptionLexicalRangeInclusive.class);
        hasRangeRestriction(parser.getCommandMetadata());
        parser.parse("-s", "deer");
    }

    @Test(expectedExceptions = ParseOptionOutOfRangeException.class, expectedExceptionsMessageRegExp = ".*(a <= value <= d).*")
    public void lexical_range_inclusive_below_max() {
        SingleCommand<? extends OptionRangeBase> parser = TestingUtil
                .singleCommandParser(OptionLexicalRangeInclusive.class);
        hasRangeRestriction(parser.getCommandMetadata());
        parser.parse("-s", "0");
    }
}
