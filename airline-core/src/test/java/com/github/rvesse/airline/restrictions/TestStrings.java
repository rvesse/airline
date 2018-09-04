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
package com.github.rvesse.airline.restrictions;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.rvesse.airline.SingleCommand;
import com.github.rvesse.airline.TestingUtil;
import com.github.rvesse.airline.parser.errors.ParseRestrictionViolatedException;

public class TestStrings {

    private SingleCommand<Strings> parser() {
        return TestingUtil.singleCommandParser(Strings.class);
    }

    @Test
    public void not_empty_valid() {
        Strings cmd = parser().parse("--not-empty", "foo");
        Assert.assertEquals(cmd.notEmpty, "foo");
    }

    @Test
    public void not_empty_valid_blank() {
        Strings cmd = parser().parse("--not-empty", " ");
        Assert.assertEquals(cmd.notEmpty, " ");
    }

    @Test(expectedExceptions = ParseRestrictionViolatedException.class)
    public void not_empty_invalid() {
        parser().parse("--not-empty", "");
    }

    @Test
    public void not_blank_valid() {
        Strings cmd = parser().parse("--not-blank", "foo");
        Assert.assertEquals(cmd.notBlank, "foo");
    }

    @Test(expectedExceptions = ParseRestrictionViolatedException.class)
    public void not_blank_invalid() {
        parser().parse("--not-blank", "");
    }

    @Test(expectedExceptions = ParseRestrictionViolatedException.class)
    public void not_blank_invalid_blank() {
        parser().parse("--not-blank", " ");
    }

    @Test
    public void pattern_tel_valid() {
        Strings cmd = parser().parse("--tel", "555-123-4567");
        Assert.assertEquals(cmd.tel, "555-123-4567");
    }

    @Test
    public void pattern_tel_valid_prefixed() {
        Strings cmd = parser().parse("--tel", "+1-555-123-4567");
        Assert.assertEquals(cmd.tel, "+1-555-123-4567");
    }

    @Test(expectedExceptions = ParseRestrictionViolatedException.class, expectedExceptionsMessageRegExp = ".*standard US format.*")
    public void pattern_tel_invalid() {
        parser().parse("--tel", "foo");
    }

    @Test
    public void min_length_valid() {
        Strings cmd = parser().parse("--min", "foobar");
        Assert.assertEquals(cmd.minLength, "foobar");
    }

    @Test(expectedExceptions = ParseRestrictionViolatedException.class, expectedExceptionsMessageRegExp = ".*below the minimum required length of 4.*")
    public void min_length_invalid() {
        parser().parse("--min", "foo");
    }

    @Test
    public void max_length_valid() {
        Strings cmd = parser().parse("--max", "foo");
        Assert.assertEquals(cmd.maxLength, "foo");
    }

    @Test(expectedExceptions = ParseRestrictionViolatedException.class, expectedExceptionsMessageRegExp = ".*exceeds the maximum permitted length of 4.*")
    public void max_length_invalid() {
        parser().parse("--max", "foobar");
    }

    @Test
    public void exact_length_valid() {
        Strings cmd = parser().parse("--exact", "fooba");
        Assert.assertEquals(cmd.exact, "fooba");
    }

    @Test(expectedExceptions = ParseRestrictionViolatedException.class, expectedExceptionsMessageRegExp = ".*required length of 5.*")
    public void exact_length_invalid_01() {
        parser().parse("--exact", "foobar");
    }

    @Test(expectedExceptions = ParseRestrictionViolatedException.class, expectedExceptionsMessageRegExp = ".*required length of 5.*")
    public void exact_length_invalid_02() {
        parser().parse("--exact", "foo");
    }
    
    @Test
    public void range_exact_length_valid() {
        Strings cmd = parser().parse("--range-exact", "foob");
        Assert.assertEquals(cmd.rangeExact, "foob");
    }

    @Test(expectedExceptions = ParseRestrictionViolatedException.class, expectedExceptionsMessageRegExp = ".*required length of 4.*")
    public void range_exact_length_invalid_01() {
        parser().parse("--range-exact", "foobar");
    }

    @Test(expectedExceptions = ParseRestrictionViolatedException.class, expectedExceptionsMessageRegExp = ".*required length of 4.*")
    public void range_exact_length_invalid_02() {
        parser().parse("--range-exact", "foo");
    }

    @Test
    public void range_length_valid_01() {
        String test = "foobar";
        for (int i = 4; i <= 6; i++) {
            Strings cmd = parser().parse("--range", test.substring(0, i));
            Assert.assertEquals(cmd.range, test.substring(0, i));
        }
    }

    @Test(expectedExceptions = ParseRestrictionViolatedException.class, expectedExceptionsMessageRegExp = ".*in the accepted length range of 4 to 6 characters.*")
    public void range_length_invalid_01() {
        parser().parse("--range", "foobartaz");
    }

    @Test(expectedExceptions = ParseRestrictionViolatedException.class, expectedExceptionsMessageRegExp = ".*in the accepted length range of 4 to 6 characters.*")
    public void range_length_invalid_02() {
        parser().parse("--range", "foo");
    }

    @Test
    public void pattern_case_insensitive_01() {
        parser().parse("--other", "foo");
    }

    @Test
    public void pattern_case_insensitive_02() {
        parser().parse("--other", "BaR");
    }

    @Test
    public void pattern_case_insensitive_03() {
        parser().parse("--other", "fooBAR");
    }

    @Test(expectedExceptions = ParseRestrictionViolatedException.class)
    public void pattern_case_insensitive_invalid() {
        parser().parse("--other", "test");
    }
}
