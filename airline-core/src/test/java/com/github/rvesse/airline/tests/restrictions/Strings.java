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
package com.github.rvesse.airline.tests.restrictions;

import com.github.rvesse.airline.HelpOption;
import com.github.rvesse.airline.annotations.AirlineModule;
import com.github.rvesse.airline.annotations.Arguments;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.Option;
import com.github.rvesse.airline.annotations.restrictions.*;
import com.github.rvesse.airline.annotations.restrictions.ranges.LengthRange;

import java.util.ArrayList;
import java.util.List;

@Command(name = "strings")
public class Strings {

    @Option(name = "--not-empty")
    @NotEmpty
    public String notEmpty;
    
    @Option(name = "--not-blank")
    @NotBlank
    public String notBlank;
    
    @Option(name = "--min")
    @MinLength(length = 4)
    public String minLength;
    
    @Option(name = "--max")
    @MaxLength(length = 4)
    public String maxLength;
    
    @Option(name = "--tel")
    @Pattern(pattern = "(\\+1-)?\\d{3}-\\d{3}-\\d{4}", description = "Must provide a telephone number in standard US format e.g. +1-800-123-4567")
    public String tel;
    
    @Option(name = "--other")
    @Pattern(pattern = "foo|bar|foobar", flags = java.util.regex.Pattern.CASE_INSENSITIVE)
    public String other;
    
    @Option(name = "--exact")
    @ExactLength(length = 5)
    public String exact;
    
    @Option(name = "--range")
    @LengthRange(min = 4, max = 6)
    public String range;
    
    @Option(name = "--range-exact")
    @LengthRange(min = 4, max = 4)
    public String rangeExact;
    
    @Option(name = "--images")
    @EndsWith(suffixes = { ".jpg", ".png", ".gif" })
    public List<String> images = new ArrayList<>();
    
    @Option(name = "--images-ci")
    @EndsWith(ignoreCase = true, suffixes = { ".jpg", ".png", ".gif" })
    public List<String> imagesCaseInsensitive = new ArrayList<>();
    
    @Option(name = "--urls")
    @StartsWith(prefixes = { "http", "https", "ftp" })
    public List<String> urls = new ArrayList<>();
    
    @Option(name = "--urls-ci")
    @StartsWith(ignoreCase = true, prefixes = { "http", "https", "ftp" })
    public List<String> urlsCaseInsensitive = new ArrayList<>();
    
    @Option(name = "--not-option-like")
    @NoOptionLikeValues
    public List<String> notOptionLike = new ArrayList<>();

    @Option(name = "--not-option-like-long")
    @NoOptionLikeValues(optionPrefixes = { "--" })
    public List<String> notOptionLikeLong = new ArrayList<>();

    @Arguments
    @NoOptionLikeValues
    public List<String> args = new ArrayList<>();
    
    @AirlineModule
    public HelpOption<Strings> helpOption = new HelpOption<>();
    
}
