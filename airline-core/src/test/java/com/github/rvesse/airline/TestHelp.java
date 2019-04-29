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
package com.github.rvesse.airline;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.github.rvesse.airline.CommandLineInterface;
import com.github.rvesse.airline.SingleCommand;
import com.github.rvesse.airline.Git.Add;
import com.github.rvesse.airline.Git.RemoteAdd;
import com.github.rvesse.airline.Git.RemoteShow;
import com.github.rvesse.airline.args.Args1;
import com.github.rvesse.airline.args.Args2;
import com.github.rvesse.airline.args.ArgsAllowedValues;
import com.github.rvesse.airline.args.ArgsArityString;
import com.github.rvesse.airline.args.ArgsBooleanArity;
import com.github.rvesse.airline.args.ArgsCopyrightAndLicense;
import com.github.rvesse.airline.args.ArgsExamples;
import com.github.rvesse.airline.args.ArgsExitCodes;
import com.github.rvesse.airline.args.ArgsHiddenDiscussion;
import com.github.rvesse.airline.args.ArgsInherited;
import com.github.rvesse.airline.args.ArgsInheritedDiscussion;
import com.github.rvesse.airline.args.ArgsMultiLineDescription;
import com.github.rvesse.airline.args.ArgsMultiParagraphDiscussion;
import com.github.rvesse.airline.args.ArgsRequired;
import com.github.rvesse.airline.args.ArgsRestoredDiscussion;
import com.github.rvesse.airline.args.ArgsVersion;
import com.github.rvesse.airline.args.ArgsVersion2;
import com.github.rvesse.airline.args.ArgsVersion3;
import com.github.rvesse.airline.args.ArgsVersionMissing;
import com.github.rvesse.airline.args.ArgsVersionMissingSuppressed;
import com.github.rvesse.airline.args.CommandHidden;
import com.github.rvesse.airline.args.GlobalOptionsHidden;
import com.github.rvesse.airline.args.OptionsHidden;
import com.github.rvesse.airline.args.OptionsRequired;
import com.github.rvesse.airline.builder.CliBuilder;
import com.github.rvesse.airline.command.CommandRemove;
import com.github.rvesse.airline.help.Help;
import com.github.rvesse.airline.help.UsageHelper;
import com.github.rvesse.airline.help.cli.CliCommandUsageGenerator;
import com.github.rvesse.airline.help.cli.CliGlobalUsageGenerator;
import com.github.rvesse.airline.help.cli.CliGlobalUsageSummaryGenerator;
import com.github.rvesse.airline.help.common.AbstractCommandUsageGenerator;
import com.github.rvesse.airline.help.sections.CliWithSections;
import com.github.rvesse.airline.help.sections.common.VersionSection;
import com.github.rvesse.airline.model.CommandMetadata;
import com.github.rvesse.airline.parser.resources.ClasspathLocator;
import com.github.rvesse.airline.parser.resources.ResourceLocator;
import com.github.rvesse.airline.restrictions.partial.PartialAnnotated;
import com.github.rvesse.airline.utils.predicates.parser.CommandFinder;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import static com.github.rvesse.airline.SingleCommand.singleCommand;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Test
@SuppressWarnings("unchecked")
public class TestHelp {
    private final Charset utf8 = Charset.forName("utf-8");

    /**
     * Helper method for if you're trying to determine the differences between
     * actual and expected output when debugging a new test and can't visually
     * see the difference e.g. differing white space
     * 
     * @param actual
     *            Actual
     * @param expected
     *            Expected
     */
    private void testStringAssert(String actual, String expected) {
        if (!actual.equals(expected)) {
            if (actual.length() != expected.length()) {
                System.err.println("Different lengths, expected " + expected.length() + " but got " + actual.length());
            }
            for (int i = 0; i < expected.length(); i++) {
                char e = expected.charAt(i);
                if (i >= actual.length()) {
                    System.err.println("Expected character '" + e + "' (Code " + (int) e + ") is at position " + i
                            + " which is beyond the length of the actual string");
                    break;
                }
                char a = actual.charAt(i);
                if (e != a) {
                    System.err.println("Expected character '" + e + "' (Code " + (int) e + ") at position " + i
                            + " does not match actual character '" + a + "' (Code " + (int) a + ")");
                    int start = Math.max(0, i - 10);
                    int end = Math.min(expected.length(), i + 10);
                    System.err.println("Expected Context:");
                    System.err.println(expected.substring(start, end));
                    System.err.println("Actual Context:");
                    end = Math.min(actual.length(), i + 10);
                    System.err.println(actual.substring(start, end));
                    break;
                }
            }
        }
        assertEquals(actual, expected);
    }

    public void testMultiLineDescriptions() throws IOException {
        SingleCommand<ArgsMultiLineDescription> cmd = singleCommand(ArgsMultiLineDescription.class);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Help.help(cmd.getCommandMetadata(), out);
        //@formatter:off
        assertEquals(new String(out.toByteArray(), utf8), 
                "NAME\n" +
                "        ArgsMultiLineDescription - Has\n" +
                "        some\n" +
                "        new lines\n" +
                "\n" +
                "SYNOPSIS\n" +
                "        ArgsMultiLineDescription [ -v ]\n" +
                "\n" +
                "OPTIONS\n" + 
                "        -v\n" +
                "            Verbose descriptions\n" +
                "            have new lines\n" +
                "\n");
        //@formatter:on
    }

    public void testMultiParagraphDiscussion() throws IOException {
        SingleCommand<ArgsMultiParagraphDiscussion> cmd = singleCommand(ArgsMultiParagraphDiscussion.class);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Help.help(cmd.getCommandMetadata(), out);
        //@formatter:off
        testStringAssert(new String(out.toByteArray(), utf8), 
                "NAME\n" +
                "        ArgsMultiParagraphDiscussion -\n" +
                "\n" +
                "SYNOPSIS\n" +
                "        ArgsMultiParagraphDiscussion\n" +
                "\n" +
                "DISCUSSION\n" +
                "        First paragraph\n" +
                "\n" +
                "        Middle paragraph\n" +
                "\n" +
                "        Final paragraph\n" + 
                "\n");
        //@formatter:on
    }

    public void testInheritedDiscussion() throws IOException {
        SingleCommand<ArgsInheritedDiscussion> cmd = singleCommand(ArgsInheritedDiscussion.class);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Help.help(cmd.getCommandMetadata(), out);
        //@formatter:off
        testStringAssert(new String(out.toByteArray(), utf8), 
                "NAME\n" +
                "        ArgsInheritedDiscussion -\n" +
                "\n" +
                "SYNOPSIS\n" +
                "        ArgsInheritedDiscussion\n" +
                "\n" +
                "DISCUSSION\n" +
                "        First paragraph\n" +
                "\n" +
                "        Middle paragraph\n" +
                "\n" +
                "        Final paragraph\n" + 
                "\n");
        //@formatter:on
    }

    public void testHiddenDiscussion() throws IOException {
        SingleCommand<ArgsHiddenDiscussion> cmd = singleCommand(ArgsHiddenDiscussion.class);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Help.help(cmd.getCommandMetadata(), out);
        //@formatter:off
        testStringAssert(new String(out.toByteArray(), utf8), 
                "NAME\n" +
                "        ArgsHiddenDiscussion -\n" +
                "\n" +
                "SYNOPSIS\n" +
                "        ArgsHiddenDiscussion\n" +
                "\n");
        //@formatter:on
    }

    public void testRestoredDiscussion() throws IOException {
        SingleCommand<ArgsRestoredDiscussion> cmd = singleCommand(ArgsRestoredDiscussion.class);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Help.help(cmd.getCommandMetadata(), out);
        //@formatter:off
        testStringAssert(new String(out.toByteArray(), utf8), 
                "NAME\n" +
                "        ArgsRestoredDiscussion -\n" +
                "\n" +
                "SYNOPSIS\n" +
                "        ArgsRestoredDiscussion\n" +
                "\n" +
                "DISCUSSION\n" +
                "        Restored\n" +
                "\n");
        //@formatter:on
    }

    public void testExamples() throws IOException {
        SingleCommand<ArgsExamples> cmd = singleCommand(ArgsExamples.class);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Help.help(cmd.getCommandMetadata(), out);
        //@formatter:off
        testStringAssert(new String(out.toByteArray(), utf8), 
                "NAME\n" +
                "        ArgsExamples -\n" +
                "\n" +
                "SYNOPSIS\n" +
                "        ArgsExamples\n" +
                "\n" +
                "EXAMPLES\n" +
                "        ArgsExample\n" +
                "\n" +
                "            Does nothing\n" +
                "\n" +
                "        ArgsExample foo bar\n" +
                "\n" + 
                "            Foos a bar\n" +
                "\n");
        //@formatter:on
    }

    public void testCopyrightLicense() throws IOException {
        SingleCommand<ArgsCopyrightAndLicense> cmd = singleCommand(ArgsCopyrightAndLicense.class);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Help.help(cmd.getCommandMetadata(), out);
        //@formatter:off
        testStringAssert(new String(out.toByteArray(), utf8), 
                "NAME\n" +
                "        ArgsCopyrightAndLicense -\n" +
                "\n" +
                "SYNOPSIS\n" +
                "        ArgsCopyrightAndLicense\n" +
                "\n" +
                "COPYRIGHT\n" +
                "        Copyright (c) Acme Inc 2015-2016\n" +
                "\n" +
                "LICENSE\n" +
                "        This software is open source under the Apache License 2.0\n" +
                "\n" +
                "        Please see http://apache.org/licenses/LICENSE-2.0 for more information\n" +
                "\n");
        //@formatter:on
    }

    public void testGit() throws IOException {
        //@formatter:off
        CliBuilder<Runnable> builder = CommandLineInterface.<Runnable>builder("git")
                .withDescription("the stupid content tracker")
                .withDefaultCommand(Help.class)
                .withCommand(Help.class)
                .withCommand(Add.class);

        builder.withGroup("remote")
                .withDescription("Manage set of tracked repositories")
                .withDefaultCommand(RemoteShow.class)
                .withCommand(RemoteShow.class)
                .withCommand(RemoteAdd.class);

        CommandLineInterface<Runnable> gitParser = builder.build();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Help.help(gitParser.getMetadata(), Collections.<String>emptyList(), out);
        assertEquals(new String(out.toByteArray(), utf8), 
                "usage: git [ -v ] <command> [ <args> ]\n" +
                "\n" +
                "Commands are:\n" +
                "    add      Add file contents to the index\n" +
                "    help     Display help information\n" +
                "    remote   Manage set of tracked repositories\n" +
                "\n" +
                "See 'git help <command>' for more information on a specific command.\n");

        out = new ByteArrayOutputStream();
        Help.help(gitParser.getMetadata(), Collections.singletonList("add"), out);
        assertEquals(new String(out.toByteArray(), utf8), 
                "NAME\n" +
                "        git add - Add file contents to the index\n" +
                "\n" +
                "SYNOPSIS\n" +
                "        git [ -v ] add [ -i ] [--] [ <patterns>... ]\n" +
                "\n" +
                "OPTIONS\n" +
                "        -i\n" +
                "            Add modified contents interactively.\n" +
                "\n" +
                "        -v\n" +
                "            Verbose mode\n" +
                "\n" +
                "        --\n" +
                "            This option can be used to separate command-line options from the\n" +
                "            list of arguments (useful when arguments might be mistaken for\n" +
                "            command-line options)\n" +
                "\n" +
                "        <patterns>\n" +
                "            Patterns of files to be added\n" +
                "\n");

        out = new ByteArrayOutputStream();
        Help.help(gitParser.getMetadata(), Collections.singletonList("remote"), out);
        assertEquals(new String(out.toByteArray(), utf8), 
                "NAME\n" +
                "        git remote - Manage set of tracked repositories\n" +
                "\n" +
                "SYNOPSIS\n" +
                "        git [ -v ] remote { add | show* } [--] [cmd-options] <cmd-args>\n" +
                "\n" +
                "        Where command-specific options [cmd-options] are:\n" +
                "            add: [ -t <branch> ]\n" +
                "            show: [ -n ]\n" +
                "\n" +
                "        Where command-specific arguments <cmd-args> are:\n" +
                "            add: [ <name> <url>... ]\n" +
                "            show: [ <remote> ]\n" +
                "\n" +
                "        Where * indicates the default command(s)\n" +
                "        See 'git help remote <command>' for more information on a specific command.\n" +
                "OPTIONS\n" +
                "        -v\n" +
                "            Verbose mode\n" +
                "\n");
        
        out = new ByteArrayOutputStream();
        Help.help(gitParser.getMetadata(), Arrays.asList(new String[] { "remote", "add" }), out);
        assertEquals(new String(out.toByteArray(), utf8), 
                "NAME\n" +
                "        git remote add - Adds a remote\n" +
                "\n" +
                "SYNOPSIS\n" +
                "        git [ -v ] remote add [ -t <branch> ] [--] [ <name> <url>... ]\n" +
                "\n" +
                "OPTIONS\n" +
                "        -t <branch>\n" +
                "            Track only a specific branch\n" +
                "\n" +
                "        -v\n" +
                "            Verbose mode\n" +
                "\n" +
                "        --\n" +
                "            This option can be used to separate command-line options from the\n" +
                "            list of arguments (useful when arguments might be mistaken for\n" +
                "            command-line options)\n" +
                "\n" +
                "        <name> <url>\n" +
                "            Name and URL of remote repository to add\n" +
                "\n"
                );
        //@formatter:on
    }

    @Test
    public void testArgs1() throws IOException {
        //@formatter:off
        CliBuilder<Object> builder = CommandLineInterface.builder("test")
                .withDescription("Test commandline")
                .withDefaultCommand(Help.class)
                .withCommands(Help.class,
                        Args1.class);

        CommandLineInterface<Object> parser = builder.build();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Help.help(parser.getMetadata(), Collections.singletonList("Args1"), out);
        testStringAssert(new String(out.toByteArray(), utf8),
                "NAME\n" +
                "        test Args1 - args1 description\n" +
                "\n" +
                "SYNOPSIS\n" +
                "        test Args1 [ -bigdecimal <bigd> ] [ -date <date> ] [ -debug ]\n" +
                "                [ -double <doub> ] [ -float <floa> ] [ -groups <groups> ]\n" +
                "                [ {-log | -verbose} <verbose> ] [ -long <l> ] [--] [ <parameters>... ]\n" +
                "\n" +
                "OPTIONS\n" +
                "        -bigdecimal <bigd>\n" +
                "            A BigDecimal number\n" +
                "\n" +
                "        -date <date>\n" +
                "            An ISO 8601 formatted date.\n" +
                "\n" +
                "        -debug\n" +
                "            Debug mode\n" +
                "\n" +
                "        -double <doub>\n" +
                "            A double number\n" +
                "\n" +
                "        -float <floa>\n" +
                "            A float number\n" +
                "\n" +
                "        -groups <groups>\n" +
                "            Comma-separated list of group names to be run\n" +
                "\n" +
                "        -log <verbose>, -verbose <verbose>\n" +
                "            Level of verbosity\n" +
                "\n" +
                "        -long <l>\n" +
                "            A long number\n" +
                "\n" +
                "        --\n" +
                "            This option can be used to separate command-line options from the\n" +
                "            list of arguments (useful when arguments might be mistaken for\n" +
                "            command-line options)\n" +
                "\n" +
                "        <parameters>\n" +
                "\n" +
                "\n");
        //@formatter:on
    }

    @Test
    public void testArgs2() throws IOException {
        //@formatter:off
        CliBuilder<Object> builder = CommandLineInterface.builder("test")
                .withDescription("Test commandline")
                .withDefaultCommand(Help.class)
                .withCommands(Help.class,
                        Args2.class);

        CommandLineInterface<Object> parser = builder.build();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Help.help(parser.getMetadata(), Collections.singletonList("Args2"), out);
        assertEquals(new String(out.toByteArray(), utf8), 
                "NAME\n" +
                "        test Args2 -\n" +
                "\n" +
                "SYNOPSIS\n" +
                "        test Args2 [ -debug ] [ -groups <groups> ] [ -host <hosts>... ]\n" +
                "                [ {-log | -verbose} <verbose> ] [--] [ <parameters>... ]\n" +
                "\n" +
                "OPTIONS\n" +
                "        -debug\n" +
                "            Debug mode\n" +
                "\n" +
                "        -groups <groups>\n" +
                "            Comma-separated list of group names to be run\n" +
                "\n" +
                "        -host <hosts>\n" +
                "            The host\n" +
                "\n" +
                "        -log <verbose>, -verbose <verbose>\n" +
                "            Level of verbosity\n" +
                "\n" +
                "        --\n" +
                "            This option can be used to separate command-line options from the\n" +
                "            list of arguments (useful when arguments might be mistaken for\n" +
                "            command-line options)\n" +
                "\n" +
                "        <parameters>\n" +
                "            List of parameters\n" +
                "\n");
        //@formatter:on
    }

    @Test
    public void testArgsAllowedValues() throws IOException {
        //@formatter:off
        SingleCommand<ArgsAllowedValues> command = singleCommand(ArgsAllowedValues.class);
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        new CliCommandUsageGenerator().usage("test", null, command.getCommandMetadata().getName(), command.getCommandMetadata(), null, out);
        assertEquals(new String(out.toByteArray(), utf8),
                "NAME\n" +
                "        test ArgsAllowedValues - ArgsAllowedValues description\n" +
                "\n" +
                "SYNOPSIS\n" +
                "        test ArgsAllowedValues [ -mode <mode> ]\n" +
                "\n" +
                "OPTIONS\n" +
                "        -mode <mode>\n" +
                "            A string from a restricted set of values\n" +
                "\n" +
                "            This options value is restricted to the following set of values:\n" +
                "                a\n" +
                "                b\n" +
                "                c\n" +
                "\n");
        
        //@formatter:on
    }

    @Test
    public void testPartialRestriction() throws IOException {
        //@formatter:off
        SingleCommand<PartialAnnotated> command = singleCommand(PartialAnnotated.class);
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        new CliCommandUsageGenerator().usage("test", null, command.getCommandMetadata().getName(), command.getCommandMetadata(), null, out);
        assertEquals(new String(out.toByteArray()), StringUtils.join(new String[] {
                "NAME",
                "        test partial -",
                "",
                "SYNOPSIS",
                "        test partial [ --kvp <kvps>... ] [--] [ <args>... ]",
                "",
                "OPTIONS",
                "        --kvp <kvps> <kvps>",
                "",
                "",
                "            The following restriction only applies to the 1st value:",
                "            This options value cannot be blank (empty or all whitespace)",
                "",
                "",
                "        --",
                "            This option can be used to separate command-line options from the",
                "            list of arguments (useful when arguments might be mistaken for",
                "            command-line options)",
                "",
                "        <args>",
                "",
                "",
                ""
                }, '\n'));
        //@formatter:on
    }

    @Test
    public void testArgsAritySting() throws IOException {
        //@formatter:off
        CliBuilder<Object> builder = CommandLineInterface.builder("test")
                .withDescription("Test commandline")
                .withDefaultCommand(Help.class)
                .withCommands(Help.class,
                        ArgsArityString.class);

        CommandLineInterface<Object> parser = builder.build();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Help.help(parser.getMetadata(), Collections.singletonList("ArgsArityString"), out);
        assertEquals(new String(out.toByteArray(), utf8),
                "NAME\n" +
                "        test ArgsArityString -\n" +
                "\n" +
                "SYNOPSIS\n" +
                "        test ArgsArityString [ -pairs <pairs>... ] [--] [ <rest>... ]\n" +
                "\n" +
                "OPTIONS\n" +
                "        -pairs <pairs> <pairs>\n" +
                "            Pairs\n" +
                "\n" +
                "        --\n" +
                "            This option can be used to separate command-line options from the\n" +
                "            list of arguments (useful when arguments might be mistaken for\n" +
                "            command-line options)\n" +
                "\n" +
                "        <rest>\n" +
                "            Rest\n" +
                "\n");
        //@formatter:on
    }

    @Test
    public void testArgsBooleanArity() throws IOException {
        //@formatter:off
        CliBuilder<Object> builder = CommandLineInterface.builder("test")
                .withDescription("Test commandline")
                .withDefaultCommand(Help.class)
                .withCommands(Help.class,
                        ArgsBooleanArity.class);

        CommandLineInterface<Object> parser = builder.build();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Help.help(parser.getMetadata(), Collections.singletonList("ArgsBooleanArity"), out);
        assertEquals(new String(out.toByteArray(), utf8),
                "NAME\n" +
                "        test ArgsBooleanArity -\n" +
                "\n" +
                "SYNOPSIS\n" +
                "        test ArgsBooleanArity [ -debug <debug> ]\n" +
                "\n" +
                "OPTIONS\n" +
                "        -debug <debug>\n" +
                "\n" +
                "\n");
        //@formatter:on
    }

    @Test
    public void testArgsInherited() throws IOException {
        //@formatter:off
        CliBuilder<Object> builder = CommandLineInterface.builder("test")
                .withDescription("Test commandline")
                .withDefaultCommand(Help.class)
                .withCommands(Help.class,
                        ArgsInherited.class);

        CommandLineInterface<Object> parser = builder.build();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Help.help(parser.getMetadata(), Collections.singletonList("ArgsInherited"), out);
        assertEquals(new String(out.toByteArray(), utf8), "NAME\n" +
                "        test ArgsInherited -\n" +
                "\n" +
                "SYNOPSIS\n" +
                "        test ArgsInherited [ -child <child> ] [ -debug ] [ -groups <groups> ]\n" +
                "                [ -level <level> ] [ -log <log> ] [--] [ <parameters>... ]\n" +
                "\n" +
                "OPTIONS\n" +
                "        -child <child>\n" +
                "            Child parameter\n" +
                "\n" +
                "        -debug\n" +
                "            Debug mode\n" +
                "\n" +
                "        -groups <groups>\n" +
                "            Comma-separated list of group names to be run\n" +
                "\n" +
                "        -level <level>\n" +
                "            A long number\n" +
                "\n" +
                "        -log <log>\n" +
                "            Level of verbosity\n" +
                "\n" +
                "        --\n" +
                "            This option can be used to separate command-line options from the\n" +
                "            list of arguments (useful when arguments might be mistaken for\n" +
                "            command-line options)\n" +
                "\n" +
                "        <parameters>\n" +
                "\n" +
                "\n");
        //@formatter:on
    }

    @Test
    public void testArgsRequired() throws IOException {
        //@formatter:off
        CliBuilder<Object> builder = CommandLineInterface.builder("test")
                .withDescription("Test commandline")
                .withDefaultCommand(Help.class)
                .withCommands(Help.class,
                        ArgsRequired.class);

        CommandLineInterface<Object> parser = builder.build();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Help.help(parser.getMetadata(), Collections.singletonList("ArgsRequired"), out);
        assertEquals(new String(out.toByteArray(), utf8),
                "NAME\n" +
                "        test ArgsRequired -\n" +
                "\n" +
                "SYNOPSIS\n" +
                "        test ArgsRequired [--] <parameters>...\n" +
                "\n" +
                "OPTIONS\n" +
                "        --\n" +
                "            This option can be used to separate command-line options from the\n" +
                "            list of arguments (useful when arguments might be mistaken for\n" +
                "            command-line options)\n" +
                "\n" +
                "        <parameters>\n" +
                "            List of files\n" +
                "\n");
        //@formatter:on
    }

    @Test
    public void testOptionsRequired() throws IOException {
        //@formatter:off
        CliBuilder<Object> builder = CommandLineInterface.builder("test")
                .withDescription("Test commandline")
                .withDefaultCommand(Help.class)
                .withCommands(Help.class,
                        OptionsRequired.class);

        CommandLineInterface<Object> parser = builder.build();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Help.help(parser.getMetadata(), Collections.singletonList("OptionsRequired"), out);
        assertEquals(new String(out.toByteArray(), utf8),
                "NAME\n" +
                "        test OptionsRequired -\n" +
                "\n" +
                "SYNOPSIS\n" +
                "        test OptionsRequired [ --optional <optionalOption> ]\n" +
                "                --required <requiredOption>\n" +
                "\n" +
                "OPTIONS\n" +
                "        --optional <optionalOption>\n" +
                "\n" +
                "\n" +
                "        --required <requiredOption>\n" +
                "\n" +
                "\n");
        //@formatter:on
    }

    @Test
    public void testOptionsHidden01() throws IOException {
        //@formatter:off
        CliBuilder<Object> builder = CommandLineInterface.builder("test")
                .withDescription("Test commandline")
                .withDefaultCommand(Help.class)
                .withCommands(Help.class,
                        OptionsHidden.class);

        CommandLineInterface<Object> parser = builder.build();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Help.help(parser.getMetadata(), Collections.singletonList("OptionsHidden"), out);
        assertEquals(new String(out.toByteArray(), utf8), 
                "NAME\n" +
                "        test OptionsHidden -\n" +
                "\n" +
                "SYNOPSIS\n" +
                "        test OptionsHidden [ --optional <optionalOption> ]\n" +
                "\n" +
                "OPTIONS\n" +
                "        --optional <optionalOption>\n" +
                "\n" +
                "\n");
        //@formatter:on
    }

    @Test
    public void testOptionsHidden02() throws IOException {
        //@formatter:off
        CliBuilder<Object> builder = CommandLineInterface.builder("test")
                .withDescription("Test commandline")
                .withDefaultCommand(Help.class)
                .withCommands(Help.class,
                        OptionsHidden.class);

        CommandLineInterface<Object> parser = builder.build();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        AbstractCommandUsageGenerator generator = new CliCommandUsageGenerator(true);
        CommandMetadata metadata = CollectionUtils.find(parser.getMetadata().getDefaultGroupCommands(), new CommandFinder("OptionsHidden"));
        Assert.assertNotNull(metadata);
        generator.usage("test", null, "OptionsHidden", metadata, null, out);
        
        assertEquals(new String(out.toByteArray(), utf8), 
                "NAME\n" +
                "        test OptionsHidden -\n" +
                "\n" +
                "SYNOPSIS\n" +
                "        test OptionsHidden [ --hidden <hiddenOption> ]\n" +
                "                [ --optional <optionalOption> ]\n" +
                "\n" +
                "OPTIONS\n" +
                "        --hidden <hiddenOption>\n" +
                "\n\n" +
                "        --optional <optionalOption>\n" +
                "\n" +
                "\n");
        //@formatter:on
    }

    @Test
    public void testGlobalOptionsHidden() throws IOException {
        //@formatter:off
        CliBuilder<Object> builder = CommandLineInterface.builder("test")
                .withDescription("Test commandline")
                .withDefaultCommand(Help.class)
                .withCommands(Help.class,
                        GlobalOptionsHidden.class);

        CommandLineInterface<Object> parser = builder.build();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Help.help(parser.getMetadata(), Collections.singletonList("GlobalOptionsHidden"), out);
        assertEquals(new String(out.toByteArray(), utf8),
                "NAME\n" +
                "        test GlobalOptionsHidden -\n" +
                "\n" +
                "SYNOPSIS\n" +
                "        test [ {-op | --optional} ] GlobalOptionsHidden\n" +
                "\n" +
                "OPTIONS\n" +
                "        -op, --optional\n" +
                "\n" +
                "\n");
        //@formatter:on
    }

    @Test
    public void testCommandHidden() throws IOException {
        //@formatter:off
        CliBuilder<Object> builder = CommandLineInterface.builder("test")
                .withDescription("Test commandline")
                .withDefaultCommand(Help.class)
                .withCommands(Help.class,
                        ArgsRequired.class, CommandHidden.class);

        CommandLineInterface<Object> parser = builder.build();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Help.help(parser.getMetadata(), Collections.<String>emptyList(), out);
        assertEquals(new String(out.toByteArray(), utf8),
                "usage: test <command> [ <args> ]\n" +
                "\n" +
                "Commands are:\n" +
                "    ArgsRequired\n" +
                "    help           Display help information\n" +
                "\n" +
                "See 'test help <command>' for more information on a specific command.\n");

        out = new ByteArrayOutputStream();
        Help.help(parser.getMetadata(), Collections.singletonList("CommandHidden"), out);
        assertEquals(new String(out.toByteArray(), utf8),
                "NAME\n" +
                "        test CommandHidden -\n" +
                "\n" +
                "SYNOPSIS\n" +
                "        test CommandHidden [ --optional <optionalOption> ]\n" +
                "\n" +
                "OPTIONS\n" +
                "        --optional <optionalOption>\n" +
                "\n" +
                "\n");
        //@formatter:on
    }

    @Test
    public void testGroups() throws IOException {
        //@formatter:off
        CliBuilder<Object> builder = CommandLineInterface.builder("test")
                .withDescription("Test commandline")
                .withCommand(Help.class);
        builder.withGroup("visible")
               .withDescription("Visible group")
               .withCommands(ArgsRequired.class);

        CommandLineInterface<Object> parser = builder.build();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Help.help(parser.getMetadata(), Collections.<String>emptyList(), out);
        Assert.assertEquals(new String(out.toByteArray(), utf8),
                "usage: test <command> [ <args> ]\n" +
                "\n" +
                "Commands are:\n" +
                "    help      Display help information\n" +
                "    visible   Visible group\n" +
                "\n" +
                "See 'test help <command>' for more information on a specific command.\n");
        //@formatter:on
    }

    @Test
    public void testGroupsHidden01() throws IOException {
        //@formatter:off
        CliBuilder<Object> builder = CommandLineInterface.builder("test")
                .withDescription("Test commandline")
                .withCommand(Help.class);
        builder.withGroup("visible")
               .withDescription("Visible group")
               .withCommands(ArgsRequired.class);
        builder.withGroup("hidden")
               .withDescription("Hidden group")
               .withCommands(ArgsRequired.class)
               .makeHidden();

        CommandLineInterface<Object> parser = builder.build();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Help.help(parser.getMetadata(), Collections.<String>emptyList(), out);
        assertEquals(new String(out.toByteArray(), utf8),
                "usage: test <command> [ <args> ]\n" +
                "\n" +
                "Commands are:\n" +
                "    help      Display help information\n" +
                "    visible   Visible group\n" +
                "\n" +
                "See 'test help <command>' for more information on a specific command.\n");
        //@formatter:on
    }

    @Test
    public void testGroupsHidden02() throws IOException {
        //@formatter:off
        CliBuilder<Object> builder = CommandLineInterface.builder("test")
                .withDescription("Test commandline")
                .withCommand(Help.class);
        builder.withGroup("visible")
               .withDescription("Visible group")
               .withCommands(ArgsRequired.class);
        builder.withGroup("hidden")
               .withDescription("Hidden group")
               .withCommands(ArgsRequired.class)
               .makeHidden();

        CommandLineInterface<Object> parser = builder.build();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        CliGlobalUsageSummaryGenerator<Object> generator = new CliGlobalUsageSummaryGenerator<Object>(true);
        generator.usage(parser.getMetadata(), out);
        assertEquals(new String(out.toByteArray(), utf8),
                "usage: test <command> [ <args> ]\n" +
                "\n" +
                "Commands are:\n" +
                "    help      Display help information\n" +
                "    hidden    Hidden group\n" +
                "    visible   Visible group\n" +
                "\n" +
                "See 'test help <command>' for more information on a specific command.\n");
        //@formatter:on
    }

    @Test
    public void testExamplesAndDiscussion() throws IOException {
        //@formatter:off
        CommandLineInterface<Object> parser = CommandLineInterface.builder("git")
            .withCommand(CommandRemove.class)
            .build();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Help.help(parser.getMetadata(), Collections.singletonList("remove"), out);

        String discussion = "DISCUSSION\n" +
        "        More details about how this removes files from the index.\n" +
        "\n";

        String examples = "EXAMPLES\n" +
        "        $ git remove -i myfile.java\n\n" +
        "            This is a usage example";

        String usage = new String(out.toByteArray(), utf8);
        System.out.println(usage);
        assertTrue(usage.contains(discussion), "Expected the discussion section to be present in the help");
        assertTrue(usage.contains(examples), "Expected the examples section to be present in the help");
        //@formatter:on
    }

    @Test
    public void testSingleCommandArgs1() throws IOException {
        //@formatter:off
        SingleCommand<Args1> command = singleCommand(Args1.class);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        new CliCommandUsageGenerator().usage(null, null, "test", command.getCommandMetadata(), null, out);
        assertEquals(new String(out.toByteArray(), utf8),
                "NAME\n" +
                "        test - args1 description\n" +
                "\n" +
                "SYNOPSIS\n" +
                "        test [ -bigdecimal <bigd> ] [ -date <date> ] [ -debug ]\n" +
                "                [ -double <doub> ] [ -float <floa> ] [ -groups <groups> ]\n" +
                "                [ {-log | -verbose} <verbose> ] [ -long <l> ] [--] [ <parameters>... ]\n" +
                "\n" +
                "OPTIONS\n" +
                "        -bigdecimal <bigd>\n" +
                "            A BigDecimal number\n" +
                "\n" +
                "        -date <date>\n" +
                "            An ISO 8601 formatted date.\n" +
                "\n" +
                "        -debug\n" +
                "            Debug mode\n" +
                "\n" +
                "        -double <doub>\n" +
                "            A double number\n" +
                "\n" +
                "        -float <floa>\n" +
                "            A float number\n" +
                "\n" +
                "        -groups <groups>\n" +
                "            Comma-separated list of group names to be run\n" +
                "\n" +
                "        -log <verbose>, -verbose <verbose>\n" +
                "            Level of verbosity\n" +
                "\n" +
                "        -long <l>\n" +
                "            A long number\n" +
                "\n" +
                "        --\n" +
                "            This option can be used to separate command-line options from the\n" +
                "            list of arguments (useful when arguments might be mistaken for\n" +
                "            command-line options)\n" +
                "\n" +
                "        <parameters>\n" +
                "\n" +
                "\n");
        //@formatter:on
    }

    @Test
    public void testExitCodes() throws IOException {
        //@formatter:off
        SingleCommand<ArgsExitCodes> command = singleCommand(ArgsExitCodes.class);
    
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        new CliCommandUsageGenerator().usage(null, null, "test", command.getCommandMetadata(), null, out);
        testStringAssert(new String(out.toByteArray(), utf8),
                "NAME\n" +
                "        test - ArgsExitCodes description\n" +
                "\n" +
                "SYNOPSIS\n" +
                "        test\n" +
                "\n" +
                "EXIT CODES\n" +
                "        This command returns one of the following exit codes:\n" +
                "\n" +
                "            0   Success\n" +
                "            1\n" +
                "            2   Error 2\n\n");
        //@formatter:on
    }
    
    @Test
    public void testVersion() throws IOException {
        //@formatter:off
        SingleCommand<ArgsVersion> command = singleCommand(ArgsVersion.class);
    
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        new CliCommandUsageGenerator().usage(null, null, "test", command.getCommandMetadata(), null, out);
        testStringAssert(new String(out.toByteArray(), utf8),
                "NAME\n" +
                "        test - ArgsVersion description\n" +
                "\n" +
                "SYNOPSIS\n" +
                "        test\n" +
                "\n" +
                "VERSION\n" +
                "            Component: Airline Test\n" +
                "            Version: 1.2.3\n" +
                "            Build: 12345abcde\n");
        //@formatter:on
    }
    
    @Test
    public void testVersionCli() throws IOException {
        //@formatter:off
        CommandLineInterface<Args1> cli = new CliBuilder<Args1>("test")
                            .withCommand(Args1.class)
                            .withHelpSection(new VersionSection(new String[] { "/test.version" }, 
                                                                new ResourceLocator[] { new ClasspathLocator() }, 
                                                                "component",
                                                                "version", 
                                                                "build", 
                                                                "buildDate", 
                                                                new String[0], 
                                                                new String[0], 
                                                                false, 
                                                                false))
                            .build();
    
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        new CliGlobalUsageGenerator<Args1>().usage(cli.getMetadata(), out);
        testStringAssert(new String(out.toByteArray(), utf8),
                "NAME\n" +
                "        test -\n" +
                "\n" +
                "SYNOPSIS\n" + 
                "        test <command> [ <args> ]\n" + 
                "\n" + 
                "COMMANDS\n" + 
                "        Args1\n" + 
                "            args1 description\n" +
                "\n" +
                "VERSION\n" +
                "            Component: Airline Test\n" +
                "            Version: 1.2.3\n" +
                "            Build: 12345abcde\n");
        //@formatter:on
    }
    
    @Test
    public void testVersionCli2() throws IOException {
        //@formatter:off
        CommandLineInterface<Args1> cli = new CliBuilder<Args1>("test")
                            .withCommand(Args1.class)
                            .withHelpSection(new VersionSection(new String[] { "/test.version" }, 
                                                                new ResourceLocator[] { new ClasspathLocator() }, 
                                                                "component",
                                                                "version", 
                                                                "build", 
                                                                "buildDate", 
                                                                new String[0], 
                                                                new String[0], 
                                                                false, 
                                                                false))
                            .build();
    
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        new CliGlobalUsageSummaryGenerator<Args1>().usage(cli.getMetadata(), out);
        testStringAssert(new String(out.toByteArray(), utf8),
                "usage: test <command> [ <args> ]\n" + 
                "\n" + 
                "Commands are:\n" + 
                "    Args1   args1 description\n" + 
                "\n" + 
                "See 'test help <command>' for more information on a specific command.\n" +
                "\n" +
                "VERSION\n" +
                "            Component: Airline Test\n" +
                "            Version: 1.2.3\n" +
                "            Build: 12345abcde\n");
        //@formatter:on
    }
    
    @Test
    public void testVersionComponents() throws IOException {
        //@formatter:off
        SingleCommand<ArgsVersion2> command = singleCommand(ArgsVersion2.class);
    
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        new CliCommandUsageGenerator().usage(null, null, "test", command.getCommandMetadata(), null, out);
        testStringAssert(new String(out.toByteArray(), utf8),
                "NAME\n" +
                "        test - Multiple component versions\n" +
                "\n" +
                "SYNOPSIS\n" +
                "        test\n" +
                "\n" +
                "VERSION\n" +
                "            Component: Airline Test\n" +
                "            Version: 1.2.3\n" +
                "            Build: 12345abcde\n" + 
                "\n" +
                "            Component: Foo\n" +
                "            Build: 789\n" +
                "            Build Date: Feb 2016\n" +
                "            Author: Mr Foo\n" +
                "\n" +
                "            Component: Bar\n" +
                "            Version: 1.0.7\n" +
                "            Built With: Oracle JDK 1.7\n" +
                "            Author: Mrs Bar\n");
        //@formatter:on
    }
    
    @Test
    public void testVersionComponentsTabular() throws IOException {
        //@formatter:off
        SingleCommand<ArgsVersion3> command = singleCommand(ArgsVersion3.class);
    
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        new CliCommandUsageGenerator().usage(null, null, "test", command.getCommandMetadata(), null, out);
        testStringAssert(new String(out.toByteArray(), utf8),
                "NAME\n" +
                "        test - Multiple component versions\n" +
                "\n" +
                "SYNOPSIS\n" +
                "        test\n" +
                "\n" +
                "VERSION\n" +
                "            Component      Version   Build        Build Date   Author    Built With\n" +
                "            Airline Test   1.2.3     12345abcde\n" +
                "            Foo                      789          Feb 2016     Mr Foo\n" +
                "            Bar            1.0.7                               Mrs Bar   Oracle JDK 1.7\n\n");
        //@formatter:on
    }
    
    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = ".*missing\\.version.*")
    public void testVersionMissing() throws IOException {
        singleCommand(ArgsVersionMissing.class);
    }
    
    @Test
    public void testVersionMissingSupressed() throws IOException {
        //@formatter:off
        SingleCommand<ArgsVersionMissingSuppressed> command = singleCommand(ArgsVersionMissingSuppressed.class);
    
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        new CliCommandUsageGenerator().usage(null, null, "test", command.getCommandMetadata(), null, out);
        testStringAssert(new String(out.toByteArray(), utf8),
                "NAME\n" +
                "        test - Missing version information\n" +
                "\n" +
                "SYNOPSIS\n" +
                "        test\n" +
                "\n");
        //@formatter:on
    }

    @Test
    public void testCommandSorting() {
        //@formatter:off
        CliBuilder<Runnable> builder = CommandLineInterface.<Runnable>builder("git")
                .withDescription("the stupid content tracker")
                .withDefaultCommand(Help.class)
                .withCommand(Help.class)
                .withCommand(Add.class);

        builder.withGroup("remote")
                .withDescription("Manage set of tracked repositories")
                .withDefaultCommand(RemoteShow.class)
                .withCommand(RemoteShow.class)
                .withCommand(RemoteAdd.class);

        CommandLineInterface<Runnable> gitParser = builder.build();
        
        List<CommandMetadata> defCommands = new ArrayList<>(gitParser.getMetadata().getDefaultGroupCommands());
        Collections.sort(defCommands, UsageHelper.DEFAULT_COMMAND_COMPARATOR);
        
        Assert.assertEquals(defCommands.get(0).getName(), "add");
        Assert.assertEquals(defCommands.get(1).getName(), "help");
        
        // Check sort is stable
        Collections.sort(defCommands, UsageHelper.DEFAULT_COMMAND_COMPARATOR);
        
        Assert.assertEquals(defCommands.get(0).getName(), "add");
        Assert.assertEquals(defCommands.get(1).getName(), "help");
        
        //@formatter:on
    }
    
    
    @Test
    public void testCliSectionsAnnotated() throws IOException {
        CommandLineInterface<Object> cli = new CommandLineInterface<>(CliWithSections.class);
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        new CliGlobalUsageGenerator<Object>().usage(cli.getMetadata(), out);
        //@formatter:off
        testStringAssert(new String(out.toByteArray(), utf8),
                "NAME\n" + 
                "        test -\n" + 
                "\n" + 
                "SYNOPSIS\n" + 
                "        test [ -v ] <command> [ <args> ]\n" + 
                "\n" + 
                "OPTIONS\n" + 
                "        -v\n" + 
                "            Verbose mode\n" + 
                "\n" + 
                "COMMANDS\n" + 
                "        Args1\n" + 
                "            args1 description\n" + 
                "\n" + 
                "        help\n" + 
                "            Display help information\n" + 
                "\n" + 
                "        remove\n" + 
                "            Remove file contents to the index\n" + 
                "\n" + 
                "DISCUSSION\n" + 
                "        Foo\n" + 
                "\n" + 
                "        Bar\n" +
                "\n");
        //@formatter:on
    }
    
    @Test
    public void testCliSectionsAnnotated2() throws IOException {
        CommandLineInterface<Object> cli = new CommandLineInterface<>(CliWithSections.class);
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        new CliGlobalUsageSummaryGenerator<Object>().usage(cli.getMetadata(), out);
        //@formatter:off
        testStringAssert(new String(out.toByteArray(), utf8),
                "usage: test [ -v ] <command> [ <args> ]\n" + 
                "\n" + 
                "Commands are:\n" + 
                "    Args1    args1 description\n" + 
                "    help     Display help information\n" + 
                "    remove   Remove file contents to the index\n" + 
                "\n" + 
                "See 'test help <command>' for more information on a specific command.\n" +
                "\n" + 
                "DISCUSSION\n" + 
                "        Foo\n" + 
                "\n" + 
                "        Bar\n" +
                "\n");
        //@formatter:on
    }
}
