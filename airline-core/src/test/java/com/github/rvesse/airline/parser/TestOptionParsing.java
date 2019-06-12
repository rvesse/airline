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
package com.github.rvesse.airline.parser;

import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.rvesse.airline.Cli;
import com.github.rvesse.airline.annotations.Arguments;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.DefaultOption;
import com.github.rvesse.airline.annotations.Option;
import com.github.rvesse.airline.builder.CliBuilder;
import com.github.rvesse.airline.parser.errors.ParseArgumentsUnexpectedException;
import com.github.rvesse.airline.parser.errors.ParseOptionMissingValueException;
import com.github.rvesse.airline.parser.errors.ParseOptionUnexpectedException;
import com.github.rvesse.airline.parser.options.AbstractNameValueOptionParser;
import com.github.rvesse.airline.parser.options.ClassicGetOptParser;
import com.github.rvesse.airline.parser.options.ListValueOptionParser;
import com.github.rvesse.airline.parser.options.LongGetOptParser;
import com.github.rvesse.airline.parser.options.MaybeListValueOptionParser;
import com.github.rvesse.airline.parser.options.MaybePairValueOptionParser;
import com.github.rvesse.airline.parser.options.StandardOptionParser;

public class TestOptionParsing {

    @Command(name = "OptionParsing1")
    public static class OptionParsing {

        @Option(name = { "-a", "--alpha" })
        boolean alpha;

        @Option(name = { "-b", "--beta" }, arity = 1)
        String beta;

        @Option(name = { "-c", "--charlie" }, arity = 2) 
        List<String> charlie = new ArrayList<String>();
    }
    
    @Command(name = "OptionParsing1")
    public static class OptionAndArgumentParsing extends OptionParsing {
        
        @Arguments
        List<String> args;
    }
    
    @Command(name = "OptionParsing1")
    public static class OptionAndDefaultParsing extends OptionParsing {
        
        @Option(name = { "-d", "--delta" }, arity = 1)
        @DefaultOption
        String delta;
    }

    private <T> T testParsing(Cli<T> parser, String... args) {
        return parser.parse(args);
    }

    @Test(expectedExceptions = ParseOptionMissingValueException.class)
    public void option_parsing_default_01() {
        Cli<OptionParsing> parser = createDefaultParser(OptionParsing.class);
        testParsing(parser, "OptionParsing1", "-c");
    }

    @Test(expectedExceptions = ParseOptionMissingValueException.class)
    public void option_parsing_default_02() {
        Cli<OptionParsing> parser = createDefaultParser(OptionParsing.class);
        testParsing(parser, "OptionParsing1", "-c", "one");
    }

    @Test
    public void option_parsing_default_03() {
        Cli<OptionParsing> parser = createDefaultParser(OptionParsing.class);
        OptionParsing cmd = testParsing(parser, "OptionParsing1", "-c", "one", "two");

        Assert.assertEquals(cmd.charlie.size(), 2);
        Assert.assertEquals(cmd.charlie.get(0), "one");
        Assert.assertEquals(cmd.charlie.get(1), "two");
    }

    private final <T> Cli<T> createDefaultParser(Class<? extends T> cls) {
        //@formatter:off
        CliBuilder<T> builder = Cli.<T>builder("test")
                                   .withCommand(cls);
        builder.withParser()
               .withDefaultOptionParsers();
        //@formatter:on
        return builder.build();
    }

    @Test
    public void option_parsing_standard_01() {
        Cli<OptionParsing> parser = createStandardParser(OptionParsing.class);
        OptionParsing cmd = testParsing(parser, "OptionParsing1", "-a", "--beta", "foo");

        Assert.assertTrue(cmd.alpha);
        Assert.assertEquals(cmd.beta, "foo");
    }

    @Test(expectedExceptions = ParseOptionMissingValueException.class)
    public void option_parsing_standard_02() {
        Cli<OptionParsing> parser = createStandardParser(OptionParsing.class);
        testParsing(parser, "OptionParsing1", "-a", "--beta");
    }

    private <T> Cli<T> createStandardParser(Class<? extends T> cls) {
        //@formatter:off
        CliBuilder<T> builder = Cli.<T>builder("test")
                                .withCommand(cls);
        builder.withParser()
               .withOptionParser(new StandardOptionParser<T>());
        //@formatter:on
        return builder.build();
    }

    @Test
    public void option_parsing_classic_getopt_01() {
        Cli<OptionParsing> parser = createClassicGetOptParser(OptionParsing.class);
        OptionParsing cmd = testParsing(parser, "OptionParsing1", "-abfoo");

        Assert.assertTrue(cmd.alpha);
        Assert.assertEquals(cmd.beta, "foo");
    }

    @Test
    public void option_parsing_classic_getopt_02() {
        Cli<OptionParsing> parser = createClassicGetOptParser(OptionParsing.class);
        OptionParsing cmd = testParsing(parser, "OptionParsing1", "-bfooa");

        Assert.assertFalse(cmd.alpha);
        Assert.assertEquals(cmd.beta, "fooa");
    }
    
    @Test
    public void option_parsing_classic_getopt_03() {
        Cli<OptionParsing> parser = createClassicGetOptParser(OptionParsing.class);
        OptionParsing cmd = testParsing(parser, "OptionParsing1", "-b", "foo");

        Assert.assertEquals(cmd.beta, "foo");
    }

    @Test(expectedExceptions = ParseOptionUnexpectedException.class)
    public void option_parsing_classic_getopt_04() {
        Cli<OptionParsing> parser = createClassicGetOptParser(OptionParsing.class);
        // This should error because classic get-opt style can only be used with
        // arity 0/1 arguments
        testParsing(parser, "OptionParsing1", "-ac");
    }

    @Test(expectedExceptions = ParseArgumentsUnexpectedException.class)
    public void option_parsing_classic_getopt_05() {
        Cli<OptionParsing> parser = createClassicGetOptParser(OptionParsing.class);
        // This should error because classic get-opt style can only be used with
        // arity 0/1 arguments
        // The error in this case is different because since this is the first
        // option in the group the ClassicGetOptParser does not try and parse it
        // unlike in the previous test case where it is part of an option group
        testParsing(parser, "OptionParsing1", "-c");
    }

    private <T> Cli<T> createClassicGetOptParser(Class<? extends T> cls) {
        //@formatter:off
        CliBuilder<T> builder = Cli.<T>builder("test")
                                   .withCommand(cls);
        builder.withParser()
               .withOptionParser(new ClassicGetOptParser<T>());
        //@formatter:on
        return builder.build();
    }

    @Test
    public void option_parsing_long_getopt_01() {
        Cli<OptionParsing> parser = createLongGetOptParser(OptionParsing.class);
        OptionParsing cmd = testParsing(parser, "OptionParsing1", "-b=foo");

        Assert.assertEquals(cmd.beta, "foo");
    }

    @Test
    public void option_parsing_long_getopt_02() {
        Cli<OptionParsing> parser = createLongGetOptParser(OptionParsing.class);
        OptionParsing cmd = testParsing(parser, "OptionParsing1", "--beta=foo");

        Assert.assertEquals(cmd.beta, "foo");
    }

    @Test(expectedExceptions = ParseArgumentsUnexpectedException.class)
    public void option_parsing_long_getopt_03() {
        Cli<OptionParsing> parser = createLongGetOptParser(OptionParsing.class);
        testParsing(parser, "OptionParsing1", "--beta", "foo");
    }
    
    @Test(expectedExceptions = ParseArgumentsUnexpectedException.class)
    public void option_parsing_long_getopt_04() {
        Cli<OptionParsing> parser = createLongGetOptParser(OptionParsing.class);
        testParsing(parser, "OptionParsing1", "--charlie=foo");
    }

    private <T> Cli<T> createLongGetOptParser(Class<? extends T> cls) {
        //@formatter:off
        CliBuilder<T> builder = Cli.<T>builder("test")
                                   .withCommand(cls);
        builder.withParser()
               .withOptionParser(new LongGetOptParser<T>());
        //@formatter:on
        return builder.build();
    }
    
    @Test
    public void option_parsing_key_value_01() {
        Cli<OptionParsing> parser = createKeyValueParser(OptionParsing.class, ':');
        OptionParsing cmd = testParsing(parser, "OptionParsing1", "-b:foo");

        Assert.assertEquals(cmd.beta, "foo");
    }

    @Test
    public void option_parsing_key_value_02() {
        Cli<OptionParsing> parser = createKeyValueParser(OptionParsing.class, ':');
        OptionParsing cmd = testParsing(parser, "OptionParsing1", "--beta:foo");

        Assert.assertEquals(cmd.beta, "foo");
    }
    
    @Test
    public void option_parsing_key_value_03() {
        Cli<OptionParsing> parser = createKeyValueParser(OptionParsing.class, ';');
        OptionParsing cmd = testParsing(parser, "OptionParsing1", "-b;foo");

        Assert.assertEquals(cmd.beta, "foo");
    }

    @Test
    public void option_parsing_key_value_04() {
        Cli<OptionParsing> parser = createKeyValueParser(OptionParsing.class, ';');
        OptionParsing cmd = testParsing(parser, "OptionParsing1", "--beta;foo");

        Assert.assertEquals(cmd.beta, "foo");
    }

    @Test(expectedExceptions = ParseArgumentsUnexpectedException.class)
    public void option_parsing_key_value_05() {
        Cli<OptionParsing> parser = createKeyValueParser(OptionParsing.class, ':');
        testParsing(parser, "OptionParsing1", "--beta", "foo");
    }

    private <T> Cli<T> createKeyValueParser(Class<? extends T> cls, char separator) {
        //@formatter:off
        CliBuilder<T> builder = Cli.<T>builder("test")
                                .withCommand(cls);
        builder.withParser()
               .withOptionParser(new KeyValueOptionParser<T>(separator));
        //@formatter:on
        return builder.build();
    }

    public static class KeyValueOptionParser<T> extends AbstractNameValueOptionParser<T> {

        public KeyValueOptionParser(char separator) {
            super(separator);
        }
    }
    
    @Test(expectedExceptions = ParseOptionMissingValueException.class, expectedExceptionsMessageRegExp = "Required values.*")
    public void option_parsing_list_value_bad_01() {
        Cli<OptionParsing> parser = createListValueParser(OptionParsing.class, ',');
        testParsing(parser, "OptionParsing1", "-c");
    }

    @Test(expectedExceptions = ParseOptionMissingValueException.class, expectedExceptionsMessageRegExp = "Too few.*")
    public void option_parsing_list_value_bad_02() {
        Cli<OptionParsing> parser = createListValueParser(OptionParsing.class, ',');
        testParsing(parser, "OptionParsing1", "-c", "one");
    }
    
    @Test(expectedExceptions = ParseOptionUnexpectedException.class, expectedExceptionsMessageRegExp = "Too many.*")
    public void option_parsing_list_value_bad_03() {
        Cli<OptionParsing> parser = createListValueParser(OptionParsing.class, ',');
        testParsing(parser, "OptionParsing1", "-c", "one,two,three");
    }
    
    @Test(expectedExceptions = ParseOptionMissingValueException.class, expectedExceptionsMessageRegExp = "Too few.*")
    public void option_parsing_list_value_bad_04() {
        Cli<OptionParsing> parser = createListValueParser(OptionParsing.class, ',');
        testParsing(parser, "OptionParsing1", "-cone");
    }
    
    @Test(expectedExceptions = ParseOptionUnexpectedException.class, expectedExceptionsMessageRegExp = "Too many.*")
    public void option_parsing_list_value_bad_05() {
        Cli<OptionParsing> parser = createListValueParser(OptionParsing.class, ',');
        testParsing(parser, "OptionParsing1", "-cone,two,three");
    }
    
    @Test
    public void option_parsing_list_value_01() {
        Cli<OptionParsing> parser = createListValueParser(OptionParsing.class, ',');
        OptionParsing cmd = testParsing(parser, "OptionParsing1", "-b", "foo");

        Assert.assertEquals(cmd.beta, "foo");
    }
    
    @Test
    public void option_parsing_list_value_02() {
        Cli<OptionParsing> parser = createListValueParser(OptionParsing.class, ',');
        OptionParsing cmd = testParsing(parser, "OptionParsing1", "-bfoo");

        Assert.assertEquals(cmd.beta, "foo");
    }

    @Test
    public void option_parsing_list_value_03() {
        Cli<OptionParsing> parser = createListValueParser(OptionParsing.class, ',');
        OptionParsing cmd = testParsing(parser, "OptionParsing1", "-c", "one,two");

        Assert.assertEquals(cmd.charlie.size(), 2);
        Assert.assertEquals(cmd.charlie.get(0), "one");
        Assert.assertEquals(cmd.charlie.get(1), "two");
    }
    
    @Test
    public void option_parsing_list_value_04() {
        Cli<OptionParsing> parser = createListValueParser(OptionParsing.class, ',');
        OptionParsing cmd = testParsing(parser, "OptionParsing1", "-cone,two");

        Assert.assertEquals(cmd.charlie.size(), 2);
        Assert.assertEquals(cmd.charlie.get(0), "one");
        Assert.assertEquals(cmd.charlie.get(1), "two");
    }
    
    @Test
    public void option_parsing_list_value_05() {
        Cli<OptionParsing> parser = createListValueParser(OptionParsing.class, ',');
        OptionParsing cmd = testParsing(parser, "OptionParsing1", "-b", "foo,bar");

        Assert.assertEquals(cmd.beta, "bar");
    }
    
    @Test
    public void option_parsing_list_value_06() {
        Cli<OptionParsing> parser = createListValueParser(OptionParsing.class, ',');
        OptionParsing cmd = testParsing(parser, "OptionParsing1", "-cone,two,three,four");

        Assert.assertEquals(cmd.charlie.size(), 4);
        Assert.assertEquals(cmd.charlie.get(0), "one");
        Assert.assertEquals(cmd.charlie.get(1), "two");
        Assert.assertEquals(cmd.charlie.get(2), "three");
        Assert.assertEquals(cmd.charlie.get(3), "four");
    }
    
    private <T> Cli<T> createListValueParser(Class<? extends T> cls, char listSeparator) {
        //@formatter:off
        CliBuilder<T> builder = Cli.<T>builder("test")
                                   .withCommand(cls);
        builder.withParser()
               .withOptionParser(new ListValueOptionParser<T>(listSeparator));
        //@formatter:on
        return builder.build();
    }
    
    private <T> Cli<T> createMaybeListValueParser(Class<? extends T> cls, char listSeparator) {
        //@formatter:off
        CliBuilder<T> builder = Cli.<T>builder("test")
                                   .withCommand(cls);
        builder.withParser()
               .withOptionParser(new MaybeListValueOptionParser<T>(listSeparator));
        //@formatter:on
        return builder.build();
    }
    
    @Test(expectedExceptions = ParseOptionMissingValueException.class, expectedExceptionsMessageRegExp = "Required values.*")
    public void option_parsing_maybe_list_value_bad_01() {
        Cli<OptionParsing> parser = createMaybeListValueParser(OptionParsing.class, ',');
        testParsing(parser, "OptionParsing1", "-c");
    }

    @Test(expectedExceptions = ParseOptionMissingValueException.class, expectedExceptionsMessageRegExp = "Too few.*")
    public void option_parsing_maybe_list_value_bad_02() {
        Cli<OptionParsing> parser = createMaybeListValueParser(OptionParsing.class, ',');
        testParsing(parser, "OptionParsing1", "-c", "one");
    }
    
    @Test(expectedExceptions = ParseOptionUnexpectedException.class, expectedExceptionsMessageRegExp = "Too many.*")
    public void option_parsing_maybe_list_value_bad_03() {
        Cli<OptionParsing> parser = createMaybeListValueParser(OptionParsing.class, ',');
        testParsing(parser, "OptionParsing1", "-c", "one,two,three");
    }
    
    @Test(expectedExceptions = ParseOptionMissingValueException.class, expectedExceptionsMessageRegExp = "Too few.*")
    public void option_parsing_maybe_list_value_bad_04() {
        Cli<OptionParsing> parser = createMaybeListValueParser(OptionParsing.class, ',');
        testParsing(parser, "OptionParsing1", "-cone");
    }
    
    @Test(expectedExceptions = ParseOptionUnexpectedException.class, expectedExceptionsMessageRegExp = "Too many.*")
    public void option_parsing_maybe_list_value_bad_05() {
        Cli<OptionParsing> parser = createMaybeListValueParser(OptionParsing.class, ',');
        testParsing(parser, "OptionParsing1", "-cone,two,three");
    }
    
    @Test(expectedExceptions = ParseOptionUnexpectedException.class, expectedExceptionsMessageRegExp = "Too many.*")
    public void option_parsing_maybe_list_value_bad_06() {
        Cli<OptionParsing> parser = createMaybeListValueParser(OptionParsing.class, ',');
        testParsing(parser, "OptionParsing1", "-c", "one", "two", "three");
    }
    
    @Test(expectedExceptions = ParseOptionUnexpectedException.class, expectedExceptionsMessageRegExp = "Too many.*")
    public void option_parsing_maybe_list_value_bad_07() {
        Cli<OptionParsing> parser = createMaybeListValueParser(OptionParsing.class, ',');
        testParsing(parser, "OptionParsing1", "-c", "one", "two", "three", "-a");
    }
    
    @Test(expectedExceptions = ParseOptionUnexpectedException.class, expectedExceptionsMessageRegExp = "Too many.*")
    public void option_parsing_maybe_list_value_bad_08() {
        Cli<OptionParsing> parser = createMaybeListValueParser(OptionParsing.class, ',');
        testParsing(parser, "OptionParsing1", "-c", "one", "two", "three", "-afoo");
    }
    
    @Test(expectedExceptions = ParseOptionUnexpectedException.class, expectedExceptionsMessageRegExp = "Too many.*")
    public void option_parsing_maybe_list_value_bad_09() {
        Cli<OptionAndArgumentParsing> parser = createMaybeListValueParser(OptionAndArgumentParsing.class, ',');
        testParsing(parser, "OptionParsing1", "-c", "one", "two,three", "--", "foo");
    }
    
    @Test
    public void option_parsing_maybe_list_value_01() {
        Cli<OptionParsing> parser = createMaybeListValueParser(OptionParsing.class, ',');
        OptionParsing cmd = testParsing(parser, "OptionParsing1", "-b", "foo");

        Assert.assertEquals(cmd.beta, "foo");
    }
    
    @Test
    public void option_parsing_maybe_list_value_02() {
        Cli<OptionParsing> parser = createMaybeListValueParser(OptionParsing.class, ',');
        OptionParsing cmd = testParsing(parser, "OptionParsing1", "-bfoo");

        Assert.assertEquals(cmd.beta, "foo");
    }

    @Test
    public void option_parsing_maybe_list_value_03() {
        Cli<OptionParsing> parser = createMaybeListValueParser(OptionParsing.class, ',');
        OptionParsing cmd = testParsing(parser, "OptionParsing1", "-c", "one,two");

        Assert.assertEquals(cmd.charlie.size(), 2);
        Assert.assertEquals(cmd.charlie.get(0), "one");
        Assert.assertEquals(cmd.charlie.get(1), "two");
    }
    
    @Test
    public void option_parsing_maybe_list_value_04() {
        Cli<OptionParsing> parser = createMaybeListValueParser(OptionParsing.class, ',');
        OptionParsing cmd = testParsing(parser, "OptionParsing1", "-cone,two");

        Assert.assertEquals(cmd.charlie.size(), 2);
        Assert.assertEquals(cmd.charlie.get(0), "one");
        Assert.assertEquals(cmd.charlie.get(1), "two");
    }
    
    @Test
    public void option_parsing_maybe_list_value_05() {
        Cli<OptionParsing> parser = createMaybeListValueParser(OptionParsing.class, ',');
        OptionParsing cmd = testParsing(parser, "OptionParsing1", "-b", "foo,bar");

        Assert.assertEquals(cmd.beta, "bar");
    }
    
    @Test
    public void option_parsing_maybe_list_value_06() {
        Cli<OptionParsing> parser = createMaybeListValueParser(OptionParsing.class, ',');
        OptionParsing cmd = testParsing(parser, "OptionParsing1", "-cone,two,three,four");

        Assert.assertEquals(cmd.charlie.size(), 4);
        Assert.assertEquals(cmd.charlie.get(0), "one");
        Assert.assertEquals(cmd.charlie.get(1), "two");
        Assert.assertEquals(cmd.charlie.get(2), "three");
        Assert.assertEquals(cmd.charlie.get(3), "four");
    }
    
    @Test
    public void option_parsing_maybe_list_value_07() {
        Cli<OptionParsing> parser = createMaybeListValueParser(OptionParsing.class, ',');
        OptionParsing cmd = testParsing(parser, "OptionParsing1", "-cone", "two", "three,four");

        Assert.assertEquals(cmd.charlie.size(), 4);
        Assert.assertEquals(cmd.charlie.get(0), "one");
        Assert.assertEquals(cmd.charlie.get(1), "two");
        Assert.assertEquals(cmd.charlie.get(2), "three");
        Assert.assertEquals(cmd.charlie.get(3), "four");
    }
    
    @Test
    public void option_parsing_maybe_list_value_08() {
        Cli<OptionParsing> parser = createMaybeListValueParser(OptionParsing.class, ',');
        OptionParsing cmd = testParsing(parser, "OptionParsing1", "-c", "one,two", "three,four");

        Assert.assertEquals(cmd.charlie.size(), 4);
        Assert.assertEquals(cmd.charlie.get(0), "one");
        Assert.assertEquals(cmd.charlie.get(1), "two");
        Assert.assertEquals(cmd.charlie.get(2), "three");
        Assert.assertEquals(cmd.charlie.get(3), "four");
    }
    
    @Test
    public void option_parsing_maybe_list_value_09() {
        Cli<OptionParsing> parser = createMaybeListValueParser(OptionParsing.class, ',');
        OptionParsing cmd = testParsing(parser, "OptionParsing1", "-c", "one,two", "three", "four");

        Assert.assertEquals(cmd.charlie.size(), 4);
        Assert.assertEquals(cmd.charlie.get(0), "one");
        Assert.assertEquals(cmd.charlie.get(1), "two");
        Assert.assertEquals(cmd.charlie.get(2), "three");
        Assert.assertEquals(cmd.charlie.get(3), "four");
    }
    
    @Test
    public void option_parsing_maybe_list_value_10() {
        Cli<OptionAndArgumentParsing> parser = createMaybeListValueParser(OptionAndArgumentParsing.class, ',');
        OptionAndArgumentParsing cmd = testParsing(parser, "OptionParsing1", "-c", "one,two", "three", "four");

        Assert.assertEquals(cmd.charlie.size(), 2);
        Assert.assertEquals(cmd.charlie.get(0), "one");
        Assert.assertEquals(cmd.charlie.get(1), "two");
        Assert.assertEquals(cmd.args.size(), 2);
        Assert.assertEquals(cmd.args.get(0), "three");
        Assert.assertEquals(cmd.args.get(1), "four");
    }
    
    @Test
    public void option_parsing_maybe_list_value_11() {
        Cli<OptionAndArgumentParsing> parser = createMaybeListValueParser(OptionAndArgumentParsing.class, ',');
        OptionAndArgumentParsing cmd = testParsing(parser, "OptionParsing1", "-c", "one", "two,three", "four");

        Assert.assertEquals(cmd.charlie.size(), 4);
        Assert.assertEquals(cmd.charlie.get(0), "one");
        Assert.assertEquals(cmd.charlie.get(1), "two");
        Assert.assertEquals(cmd.charlie.get(2), "three");
        Assert.assertEquals(cmd.charlie.get(3), "four");
    }
    
    @Test
    public void option_parsing_maybe_list_value_12() {
        Cli<OptionAndArgumentParsing> parser = createMaybeListValueParser(OptionAndArgumentParsing.class, ',');
        OptionAndArgumentParsing cmd = testParsing(parser, "OptionParsing1", "-c", "one", "two", "three", "four");

        Assert.assertEquals(cmd.charlie.size(), 2);
        Assert.assertEquals(cmd.charlie.get(0), "one");
        Assert.assertEquals(cmd.charlie.get(1), "two");
        Assert.assertEquals(cmd.args.size(), 2);
        Assert.assertEquals(cmd.args.get(0), "three");
        Assert.assertEquals(cmd.args.get(1), "four");
    }
    
    @Test
    public void option_parsing_maybe_list_value_13() {
        Cli<OptionAndArgumentParsing> parser = createMaybeListValueParser(OptionAndArgumentParsing.class, ',');
        OptionAndArgumentParsing cmd = testParsing(parser, "OptionParsing1", "-c", "one", "two", "--", "three", "four");

        Assert.assertEquals(cmd.charlie.size(), 2);
        Assert.assertEquals(cmd.charlie.get(0), "one");
        Assert.assertEquals(cmd.charlie.get(1), "two");
        Assert.assertEquals(cmd.args.size(), 2);
        Assert.assertEquals(cmd.args.get(0), "three");
        Assert.assertEquals(cmd.args.get(1), "four");
    }
    
    @Test
    public void option_parsing_maybe_list_value_14() {
        Cli<OptionAndDefaultParsing> parser = createMaybeListValueParser(OptionAndDefaultParsing.class, ',');
        OptionAndDefaultParsing cmd = testParsing(parser, "OptionParsing1", "-c", "one", "two", "three", "four");

        Assert.assertEquals(cmd.charlie.size(), 2);
        Assert.assertEquals(cmd.charlie.get(0), "one");
        Assert.assertEquals(cmd.charlie.get(1), "two");
        Assert.assertEquals(cmd.delta, "four");
    }
    
    @Test
    public void option_parsing_maybe_list_value_15() {
        Cli<OptionAndDefaultParsing> parser = createMaybeListValueParser(OptionAndDefaultParsing.class, ',');
        OptionAndDefaultParsing cmd = testParsing(parser, "OptionParsing1", "-c", "one", "two", "-b", "three", "four");

        Assert.assertEquals(cmd.charlie.size(), 2);
        Assert.assertEquals(cmd.charlie.get(0), "one");
        Assert.assertEquals(cmd.charlie.get(1), "two");
        Assert.assertEquals(cmd.beta, "three");
        Assert.assertEquals(cmd.delta, "four");
    }
    
    @Test
    public void option_parsing_maybe_pair_value_01() {
        Cli<OptionParsing> parser = createMaybePairValueParser(OptionParsing.class, '=');
        OptionParsing cmd = testParsing(parser, "OptionParsing1", "-c", "foo", "bar");

        Assert.assertEquals(cmd.charlie.get(0), "foo");
        Assert.assertEquals(cmd.charlie.get(1), "bar");
    }
    
    @Test
    public void option_parsing_maybe_pair_value_02() {
        Cli<OptionParsing> parser = createMaybePairValueParser(OptionParsing.class, '=');
        OptionParsing cmd = testParsing(parser, "OptionParsing1", "-c", "foo=bar");

        Assert.assertEquals(cmd.charlie.get(0), "foo");
        Assert.assertEquals(cmd.charlie.get(1), "bar");
    }
    
    @Test
    public void option_parsing_maybe_pair_value_03() {
        Cli<OptionParsing> parser = createMaybePairValueParser(OptionParsing.class, '=');
        OptionParsing cmd = testParsing(parser, "OptionParsing1", "-cfoo", "bar");

        Assert.assertEquals(cmd.charlie.get(0), "foo");
        Assert.assertEquals(cmd.charlie.get(1), "bar");
    }
    
    @Test
    public void option_parsing_maybe_pair_value_04() {
        Cli<OptionParsing> parser = createMaybePairValueParser(OptionParsing.class, '=');
        OptionParsing cmd = testParsing(parser, "OptionParsing1", "-cfoo=bar");

        Assert.assertEquals(cmd.charlie.get(0), "foo");
        Assert.assertEquals(cmd.charlie.get(1), "bar");
    }
    
    @Test(expectedExceptions = ParseOptionMissingValueException.class)
    public void option_parsing_maybe_pair_value_bad_01() {
        Cli<OptionParsing> parser = createMaybePairValueParser(OptionParsing.class, '=');
        testParsing(parser, "OptionParsing1", "-c", "foo");
    }
    
    @Test(expectedExceptions = ParseOptionMissingValueException.class)
    public void option_parsing_maybe_pair_value_bad_02() {
        Cli<OptionParsing> parser = createMaybePairValueParser(OptionParsing.class, '=');
        testParsing(parser, "OptionParsing1", "-cfoo");
    }
    
    private <T> Cli<T> createMaybePairValueParser(Class<? extends T> cls, char pairSeparator) {
        //@formatter:off
        CliBuilder<T> builder = Cli.<T>builder("test")
                                   .withCommand(cls);
        builder.withParser()
               .withOptionParser(new MaybePairValueOptionParser<T>(pairSeparator));
        //@formatter:on
        return builder.build();
    }
}
