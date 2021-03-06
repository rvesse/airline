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
    
    @Test
    public void ends_with_01() {
        Strings cmd = parser().parse("--images", "test.jpg");
        Assert.assertEquals(cmd.images.size(), 1);
        Assert.assertEquals(cmd.images.get(0), "test.jpg");
    }
    
    @Test
    public void ends_with_02() {
        Strings cmd = parser().parse("--images", "test.jpg", "--images", "test.png", "--images", "test.gif");
        Assert.assertEquals(cmd.images.size(), 3);
        Assert.assertEquals(cmd.images.get(0), "test.jpg");
        Assert.assertEquals(cmd.images.get(1), "test.png");
        Assert.assertEquals(cmd.images.get(2), "test.gif");
    }
    
    @Test(expectedExceptions = ParseRestrictionViolatedException.class)
    public void ends_with_03() {
        parser().parse("--images", "test.txt");
    }
    
    @Test(expectedExceptions = ParseRestrictionViolatedException.class)
    public void ends_with_04() {
        // Case sensitive so fails
        parser().parse("--images", "test.JPG");
    }
    
    @Test
    public void ends_with_case_insensitive_01() {
        Strings cmd = parser().parse("--images-ci", "test.jpg");
        Assert.assertEquals(cmd.imagesCaseInsensitive.size(), 1);
        Assert.assertEquals(cmd.imagesCaseInsensitive.get(0), "test.jpg");
    }
    
    @Test
    public void ends_with_case_insensitive_02() {
        Strings cmd = parser().parse("--images-ci", "test.jpg", "--images-ci", "test.PNG", "--images-ci", "test.GiF");
        Assert.assertEquals(cmd.imagesCaseInsensitive.size(), 3);
        Assert.assertEquals(cmd.imagesCaseInsensitive.get(0), "test.jpg");
        Assert.assertEquals(cmd.imagesCaseInsensitive.get(1), "test.PNG");
        Assert.assertEquals(cmd.imagesCaseInsensitive.get(2), "test.GiF");
    }
    
    @Test(expectedExceptions = ParseRestrictionViolatedException.class)
    public void ends_with_case_insensitive_03() {
        parser().parse("--images-ci", "test.txt");
    }
    
    @Test
    public void ends_with_case_insensitive_04() {
        // Case insensitive so passes
        Strings cmd = parser().parse("--images-ci", "test.JPG");
        Assert.assertEquals(cmd.imagesCaseInsensitive.size(), 1);
        Assert.assertEquals(cmd.imagesCaseInsensitive.get(0), "test.JPG");
    }
    
    @Test
    public void starts_with_01() {
        Strings cmd = parser().parse("--urls", "http://test.com");
        Assert.assertEquals(cmd.urls.size(), 1);
        Assert.assertEquals(cmd.urls.get(0), "http://test.com");
    }
    
    @Test
    public void starts_with_02() {
        Strings cmd = parser().parse("--urls", "http://test.com", "--urls", "https://secure.com", "--urls", "ftp://uploads.com");
        Assert.assertEquals(cmd.urls.size(), 3);
        Assert.assertEquals(cmd.urls.get(0), "http://test.com");
        Assert.assertEquals(cmd.urls.get(1), "https://secure.com");
        Assert.assertEquals(cmd.urls.get(2), "ftp://uploads.com");
    }
    
    @Test(expectedExceptions = ParseRestrictionViolatedException.class)
    public void starts_with_03() {
        parser().parse("--urls", "test.txt");
    }
    
    @Test(expectedExceptions = ParseRestrictionViolatedException.class)
    public void starts_with_04() {
        // Case sensitive so fails
        parser().parse("--urls", "HTTP://test.com");
    }
    
    @Test
    public void starts_with_case_insensitive_01() {
        Strings cmd = parser().parse("--urls-ci", "http://test.com");
        Assert.assertEquals(cmd.urlsCaseInsensitive.size(), 1);
        Assert.assertEquals(cmd.urlsCaseInsensitive.get(0), "http://test.com");
    }
    
    @Test
    public void starts_with_case_insensitive_02() {
        Strings cmd = parser().parse("--urls-ci", "http://test.com", "--urls-ci", "HTTPS://secure.com", "--urls-ci", "FtP://uploads.com");
        Assert.assertEquals(cmd.urlsCaseInsensitive.size(), 3);
        Assert.assertEquals(cmd.urlsCaseInsensitive.get(0), "http://test.com");
        Assert.assertEquals(cmd.urlsCaseInsensitive.get(1), "HTTPS://secure.com");
        Assert.assertEquals(cmd.urlsCaseInsensitive.get(2), "FtP://uploads.com");
    }
    
    @Test(expectedExceptions = ParseRestrictionViolatedException.class)
    public void starts_with_case_insensitive_03() {
        parser().parse("--urls-ci", "urn:foo");
    }
    
    @Test
    public void starts_with_case_insensitive_04() {
        // Case insensitive so passes
        Strings cmd = parser().parse("--urls-ci", "HTTP://test.com");
        Assert.assertEquals(cmd.urlsCaseInsensitive.size(), 1);
        Assert.assertEquals(cmd.urlsCaseInsensitive.get(0), "HTTP://test.com");
    }
}
