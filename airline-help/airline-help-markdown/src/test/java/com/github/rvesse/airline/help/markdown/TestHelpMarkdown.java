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
package com.github.rvesse.airline.help.markdown;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import com.github.rvesse.airline.Cli;
import com.github.rvesse.airline.SingleCommand;
import com.github.rvesse.airline.builder.CliBuilder;
import com.github.rvesse.airline.Git.Add;
import com.github.rvesse.airline.Git.RemoteAdd;
import com.github.rvesse.airline.Git.RemoteShow;
import com.github.rvesse.airline.args.Args1;
import com.github.rvesse.airline.args.ArgsAllowedValues;
import com.github.rvesse.airline.args.ArgsCopyrightAndLicense;
import com.github.rvesse.airline.args.ArgsExamples;
import com.github.rvesse.airline.args.ArgsExitCodes;
import com.github.rvesse.airline.args.ArgsMultiParagraphDiscussion;
import com.github.rvesse.airline.help.Help;
import com.github.rvesse.airline.help.markdown.MarkdownCommandUsageGenerator;
import com.github.rvesse.airline.help.markdown.MarkdownGlobalUsageGenerator;
import com.github.rvesse.airline.parser.aliases.TestAliases;

import com.github.rvesse.airline.utils.StringUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import static com.github.rvesse.airline.SingleCommand.singleCommand;
import static org.testng.Assert.assertEquals;

@Test
public class TestHelpMarkdown {
    private final Charset utf8 = Charset.forName("utf-8");
    private static final File f = new File("target/test.config");

    @AfterClass
    public static void cleanup() {
        if (f.exists()) {
            f.delete();
        }
    }

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

    /**
     * Helper method that reads in the given file
     * 
     * @param f
     *            File
     * @return File contents
     * @throws IOException
     */
    @SuppressWarnings("unused")
    private String readFile(File f) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(f));
        String line = reader.readLine();
        StringBuilder builder = new StringBuilder();
        while (line != null) {
            builder.append(line).append('\n');
            line = reader.readLine();
        }
        reader.close();
        return builder.toString();
    }

    public void testMultiParagraphDiscussionMarkdown() throws IOException {
        SingleCommand<ArgsMultiParagraphDiscussion> cmd = singleCommand(ArgsMultiParagraphDiscussion.class);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MarkdownCommandUsageGenerator generator = new MarkdownCommandUsageGenerator();
        generator.usage(null, null, "ArgsMultiParagraphDiscussion", cmd.getCommandMetadata(),
                cmd.getParserConfiguration(), out);
        //@formatter:off
        testStringAssert(new String(out.toByteArray(), utf8),
                "# NAME\n" +
                "\n" +
                "`ArgsMultiParagraphDiscussion` -\n" +
                "\n" +
                "# SYNOPSIS\n" +
                "\n" +
                "`ArgsMultiParagraphDiscussion`\n" +
                "\n" +
                "# DISCUSSION\n" +
                "\n" +
                "First paragraph\n" +
                "\n" +
                "Middle paragraph\n" +
                "\n" +
                "Final paragraph\n" +
                "\n");
        //@formatter:on
    }

    public void testExamplesMarkdown() throws IOException {
        SingleCommand<ArgsExamples> cmd = singleCommand(ArgsExamples.class);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MarkdownCommandUsageGenerator generator = new MarkdownCommandUsageGenerator();
        generator.usage(null, null, "ArgsExamples", cmd.getCommandMetadata(), cmd.getParserConfiguration(), out);
        //@formatter:off
        testStringAssert(new String(out.toByteArray(), utf8), 
                "# NAME\n" +
                "\n" +
                "`ArgsExamples` -\n" +
                "\n" +
                "# SYNOPSIS\n" +
                "\n" +
                "`ArgsExamples`\n" +
                "\n" +
                "# EXAMPLES\n" +
                "\n" +
                "    ArgsExample\n" +
                "\n" +
                "Does nothing\n" +
                "\n" +
                "    ArgsExample foo bar\n" +
                "\n" +
                "Foos a bar\n" +
                "\n");
        //@formatter:on
    }

    public void testMarkdown() throws IOException {
        //@formatter:off
        CliBuilder<Runnable> builder = Cli.<Runnable>builder("git")
                .withDescription("the stupid content tracker")
                .withDefaultCommand(Help.class)
                .withCommand(Help.class)
                .withCommand(Add.class);

        builder.withGroup("remote")
                .withDescription("Manage set of tracked repositories")
                .withDefaultCommand(RemoteShow.class)
                .withCommand(RemoteShow.class)
                .withCommand(RemoteAdd.class);

        Cli<Runnable> gitParser = builder.build();
        
        MarkdownGlobalUsageGenerator<Runnable> generator = new MarkdownGlobalUsageGenerator<Runnable>();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        generator.usage(gitParser.getMetadata(), out);
        String usage = new String(out.toByteArray(), utf8);
        assertEquals(usage,
                "# NAME\n" +
                "\n" +
                "`git` - the stupid content tracker\n" +
                "\n" +
                "# SYNOPSIS\n" +
                "\n" +
                "`git` [ `-v` ] [ *group* ] *command* [ *command-args* ]\n" +
                "\n" +
                "# OPTIONS\n" +
                "\n" +
                "- `-v`\n" +
                "\n" +
                "  Verbose mode\n" +
                "\n" +
                "# COMMANDS\n" +
                "\n" +
                "- `add`\n" +
                "\n" +
                "  Add file contents to the index\n" +
                "\n" +
                "- `help`\n" +
                "\n" +
                "  Display help information\n" +
                "\n" +
                "- `remote add`\n" +
                "\n" +
                "  Adds a remote\n" +
                "\n" +
                "- `remote show`\n" +
                "\n" +
                "  Gives some information about the remote <name>\n" +
                "\n" +
                "---\n" +
                "\n" +
                "# NAME\n" +
                "\n" +
                "`git` `add` - Add file contents to the index\n" +
                "\n" +
                "# SYNOPSIS\n" +
                "\n" +
                "`git` [ `-v` ] `add` [ `-i` ] [ `--` ] [ *patterns* ]\n" +
                "\n" +
                "# OPTIONS\n" +
                "\n" +
                "- `-i`\n" +
                "\n" +
                "  Add modified contents interactively.\n" +
                "\n" +
                "- `-v`\n" +
                "\n" +
                "  Verbose mode\n" +
                "\n" +
                "- `--`\n" +
                "\n" +
                "  This option can be used to separate command-line options from the list of\n" +
                "  arguments (useful when arguments might be mistaken for command-line options)\n" +
                "\n" +
                "- *patterns*\n" +
                "\n" +
                "  Patterns of files to be added\n" +
                "\n" +
                "---\n" +
                "\n" +
                "# NAME\n" +
                "\n" +
                "`git` `help` - Display help information\n" +
                "\n" +
                "# SYNOPSIS\n" +
                "\n" +
                "`git` `help` [ `--` ] [ *command* ]\n" +
                "\n" +
                "# OPTIONS\n" +
                "\n" +
                "- `--`\n" +
                "\n" +
                "  This option can be used to separate command-line options from the list of\n" +
                "  arguments (useful when arguments might be mistaken for command-line options)\n" +
                "\n" +
                "- *command*\n" +
                "\n" +
                "\n" +
                "\n" +
                "---\n" +
                "\n" +
                "# NAME\n" +
                "\n" +
                "`git` `remote` `add` - Adds a remote\n" +
                "\n" +
                "# SYNOPSIS\n" +
                "\n" +
                "`git` [ `-v` ] `remote` `add` [ `-t` *branch* ] [ `--` ] [ *name* *url* ]\n" +
                "\n" +
                "# OPTIONS\n" +
                "\n" +
                "- `-t` *branch*\n" +
                "\n" +
                "  Track only a specific branch\n" +
                "\n" +
                "- `-v`\n" +
                "\n" +
                "  Verbose mode\n" +
                "\n" +
                "- `--`\n" +
                "\n" +
                "  This option can be used to separate command-line options from the list of\n" +
                "  arguments (useful when arguments might be mistaken for command-line options)\n" +
                "\n" +
                "- *name* *url*\n" +
                "\n" +
                "  Name and URL of remote repository to add\n" +
                "\n" +
                "---\n" +
                "\n" +
                "# NAME\n" +
                "\n" +
                "`git` `remote` `show` - Gives some information about the remote <name>\n" +
                "\n" +
                "# SYNOPSIS\n" +
                "\n" +
                "`git` [ `-v` ] `remote` `show` [ `-n` ] [ `--` ] [ *remote* ]\n" +
                "\n" +
                "# OPTIONS\n" +
                "\n" +
                "- `-n`\n" +
                "\n" +
                "  Do not query remote heads\n" +
                "\n" +
                "- `-v`\n" +
                "\n" +
                "  Verbose mode\n" +
                "\n" +
                "- `--`\n" +
                "\n" +
                "  This option can be used to separate command-line options from the list of\n" +
                "  arguments (useful when arguments might be mistaken for command-line options)\n" +
                "\n" +
                "- *remote*\n" +
                "\n" +
                "  Remote to show\n" +
                "\n");
        //@formatter:on
    }

    public void testMarkdownMultiPage() throws IOException {
        //@formatter:off
        CliBuilder<Runnable> builder = Cli.<Runnable>builder("git")
                .withDescription("the stupid content tracker")
                .withDefaultCommand(Help.class)
                .withCommand(Help.class)
                .withCommand(Add.class);

        builder.withGroup("remote")
                .withDescription("Manage set of tracked repositories")
                .withDefaultCommand(RemoteShow.class)
                .withCommand(RemoteShow.class)
                .withCommand(RemoteAdd.class);

        Cli<Runnable> gitParser = builder.build();
        
        MarkdownMultiPageGlobalUsageGenerator<Runnable> generator = new MarkdownMultiPageGlobalUsageGenerator<Runnable>();
        FileOutputStream out = new FileOutputStream("target/git.md");
        generator.usage(gitParser.getMetadata(), out);
        String usage = readFile(new File("target/git.md"));
        //@formatter:off
        assertEquals(usage,
                "# NAME\n" +
                "\n" +
                "`git` - the stupid content tracker\n" +
                "\n" +
                "# SYNOPSIS\n" +
                "\n" +
                "`git` [ `-v` ] [ *group* ] *command* [ *command-args* ]\n" +
                "\n" +
                "# OPTIONS\n" +
                "\n" +
                "- `-v`\n" +
                "\n" +
                "  Verbose mode\n" +
                "\n" +
                "# COMMANDS\n" +
                "\n" +
                "- `add`\n" +
                "\n" +
                "  Add file contents to the index\n" +
                "\n" +
                "- `help`\n" +
                "\n" +
                "  Display help information\n" +
                "\n" +
                "- `remote add`\n" +
                "\n" +
                "  Adds a remote\n" +
                "\n" +
                "- `remote show`\n" +
                "\n" +
                "  Gives some information about the remote <name>\n" +
                "\n");
        //@formatter:on
        
        File gitHelp = new File("git-help.md");
        Assert.assertTrue(gitHelp.exists());
        usage = readFile(gitHelp);
        
        //@formatter:off
        assertEquals(usage,
                "# NAME\n" +
                "\n" +
                "`git` `help` - Display help information\n" +
                "\n" +
                "# SYNOPSIS\n" +
                "\n" +
                "`git` `help` [ `--` ] [ *command* ]\n" +
                "\n" +
                "# OPTIONS\n" +
                "\n" +
                "- `--`\n" +
                "\n" +
                "  This option can be used to separate command-line options from the list of\n" +
                "  arguments (useful when arguments might be mistaken for command-line options)\n" +
                "\n" +
                "- *command*\n" +
                "\n" +
                "\n" +
                "\n");
        //@formatter:on
        gitHelp.delete();
        
        File gitAdd = new File("git-add.md");
        Assert.assertTrue(gitAdd.exists());
        usage = readFile(gitAdd);
        
        //@formatter:off
        assertEquals(usage,
                "# NAME\n" +
                "\n" +
                "`git` `add` - Add file contents to the index\n" +
                "\n" +
                "# SYNOPSIS\n" +
                "\n" +
                "`git` [ `-v` ] `add` [ `-i` ] [ `--` ] [ *patterns* ]\n" +
                "\n" +
                "# OPTIONS\n" +
                "\n" +
                "- `-i`\n" +
                "\n" +
                "  Add modified contents interactively.\n" +
                "\n" +
                "- `-v`\n" +
                "\n" +
                "  Verbose mode\n" +
                "\n" +
                "- `--`\n" +
                "\n" +
                "  This option can be used to separate command-line options from the list of\n" +
                "  arguments (useful when arguments might be mistaken for command-line options)\n" +
                "\n" +
                "- *patterns*\n" +
                "\n" +
                "  Patterns of files to be added\n" +
                "\n");
        gitAdd.delete();
        
        File gitRemoteAdd = new File("git-remote-add.md");
        Assert.assertTrue(gitRemoteAdd.exists());
        usage = readFile(gitRemoteAdd);
        
        //@formatter:off
        assertEquals(usage,
                "# NAME\n" +
                "\n" +
                "`git` `remote` `add` - Adds a remote\n" +
                "\n" +
                "# SYNOPSIS\n" +
                "\n" +
                "`git` [ `-v` ] `remote` `add` [ `-t` *branch* ] [ `--` ] [ *name* *url* ]\n" +
                "\n" +
                "# OPTIONS\n" +
                "\n" +
                "- `-t` *branch*\n" +
                "\n" +
                "  Track only a specific branch\n" +
                "\n" +
                "- `-v`\n" +
                "\n" +
                "  Verbose mode\n" +
                "\n" +
                "- `--`\n" +
                "\n" +
                "  This option can be used to separate command-line options from the list of\n" +
                "  arguments (useful when arguments might be mistaken for command-line options)\n" +
                "\n" +
                "- *name* *url*\n" +
                "\n" +
                "  Name and URL of remote repository to add\n" +
                "\n");
        gitRemoteAdd.delete();
        
        File gitRemoteShow = new File("git-remote-show.md");
        Assert.assertTrue(gitRemoteShow.exists());
        usage = readFile(gitRemoteShow);
        
        //@formatter:off
        assertEquals(usage,
                "# NAME\n" +
                "\n" +
                "`git` `remote` `show` - Gives some information about the remote <name>\n" +
                "\n" +
                "# SYNOPSIS\n" +
                "\n" +
                "`git` [ `-v` ] `remote` `show` [ `-n` ] [ `--` ] [ *remote* ]\n" +
                "\n" +
                "# OPTIONS\n" +
                "\n" +
                "- `-n`\n" +
                "\n" +
                "  Do not query remote heads\n" +
                "\n" +
                "- `-v`\n" +
                "\n" +
                "  Verbose mode\n" +
                "\n" +
                "- `--`\n" +
                "\n" +
                "  This option can be used to separate command-line options from the list of\n" +
                "  arguments (useful when arguments might be mistaken for command-line options)\n" +
                "\n" +
                "- *remote*\n" +
                "\n" +
                "  Remote to show\n" +
                "\n");
        //@formatter:on
        gitRemoteShow.delete();
         
    }

    public void testExitCodesMarkdown() throws IOException {
        //@formatter:off
        SingleCommand<ArgsExitCodes> command = singleCommand(ArgsExitCodes.class);
    
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        new MarkdownCommandUsageGenerator().usage(null, null, "test", command.getCommandMetadata(), command.getParserConfiguration(), out);
        assertEquals(new String(out.toByteArray(), utf8),
                "# NAME\n" +
                "\n" +
                "`test` - ArgsExitCodes description\n" +
                "\n" +
                "# SYNOPSIS\n" +
                "\n" +
                "`test`\n" +
                "\n" +
                "# EXIT CODES\n" +
                "\n" +
                "This command returns one of the following exit codes:\n" +
                "\n" +
                "| | |\n" +
                "| ---- | ---- |\n" +
                "| 0 | Success |\n" +
                "| 1 | |\n" +
                "| 2 | Error 2 |\n" +
                "\n");
        //@formatter:on
    }

    @Test
    public void user_aliases_help_markdown() throws IOException {
        TestAliases.prepareConfig(f, "a.foo=Args1 bar", "b.foo=Args1 faz");

        //@formatter:off
        CliBuilder<Args1> builder = Cli.<Args1>builder("test")
                                       .withCommand(Args1.class);
        builder.withParser()
               .withUserAliases(f.getName(), "b.", "target/");
        Cli<Args1> cli = builder.build();
        //@formatter:on

        // Alias Help
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        new MarkdownGlobalUsageGenerator<Args1>().usage(cli.getMetadata(), output);
        //@formatter:off
        Assert.assertEquals(new String(output.toByteArray(), StandardCharsets.UTF_8),
                StringUtils.join(new String[] {
                        "# NAME",
                        "",
                        "`test` -",
                        "",
                        "# SYNOPSIS",
                        "",
                        "`test` *command* [ *command-args* ]",
                        "",
                        "# COMMANDS",
                        "",
                        "- `Args1`",
                        "",
                        "  args1 description",
                        "",
                        "---",
                        "",
                        "# NAME",
                        "",
                        "`test` `Args1` - args1 description",
                        "",
                        "# SYNOPSIS",
                        "",
                        "`test` `Args1` [ `-bigdecimal` *bigd* ] [ `-date` *date* ] [ `-debug` ] [",
                        "`-double` *doub* ] [ `-float` *floa* ] [ `-groups` *groups* ] [ { `-log` |",
                        "`-verbose` } *verbose* ] [ `-long` *l* ] [ `--` ] [ *parameters* ]",
                        "",
                        "# OPTIONS",
                        "",
                        "- `-bigdecimal` *bigd*",
                        "",
                        "  A BigDecimal number",
                        "",
                        "- `-date` *date*",
                        "",
                        "  An ISO 8601 formatted date.",
                        "",
                        "- `-debug`",
                        "",
                        "  Debug mode",
                        "",
                        "- `-double` *doub*",
                        "",
                        "  A double number",
                        "",
                        "- `-float` *floa*",
                        "",
                        "  A float number",
                        "",
                        "- `-groups` *groups*",
                        "",
                        "  Comma-separated list of group names to be run",
                        "",
                        "- `-log` *verbose* , `-verbose` *verbose*",
                        "",
                        "  Level of verbosity",
                        "",
                        "- `-long` *l*",
                        "",
                        "  A long number",
                        "",
                        "- `--`",
                        "",
                        "  This option can be used to separate command-line options from the list of",
                        "  arguments (useful when arguments might be mistaken for command-line options)",
                        "",
                        "- *parameters*",
                        "",
                        "",
                        "",
                        "# USER DEFINED ALIASES",
                        "",
                        "This CLI supports user defined aliases which may be placed in a test.config file",
                        "located in the following location(s):",
                        "",
                        "1. `target/`",
                        "",
                        "",
                        "This file contains aliases defined in Java properties file style e.g.",
                        "",
                        "    b.foo=bar --flag",
                        "",
                        "Here an alias foo is defined which causes the bar command to be invoked with the",
                        "`--flag` option passed to it. Aliases are distinguished from other properties in",
                        "the file by the prefix `b.` as seen in the example.",
                        "",
                        "Alias definitions are subject to the following conditions:",
                        "",
                        "  - Aliases cannot override existing commands",
                        "  - Aliases cannot be defined in terms of other aliases",
                        ""
                }, '\n'));
        //@formatter:on
    }

    public void testArgsAllowedValuesMarkdown() throws IOException {
        //@formatter:off
        SingleCommand<ArgsAllowedValues> command = singleCommand(ArgsAllowedValues.class);
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        new MarkdownCommandUsageGenerator().usage("test", null, command.getCommandMetadata().getName(), command.getCommandMetadata(), null, out);
        assertEquals(new String(out.toByteArray(), utf8),
                StringUtils.join(new String[] {
                        "# NAME",
                        "",
                        "`test` `ArgsAllowedValues` - ArgsAllowedValues description",
                        "",
                        "# SYNOPSIS",
                        "",
                        "`test` `ArgsAllowedValues` [ `-mode` *mode* ]",
                        "",
                        "# OPTIONS",
                        "",
                        "- `-mode` *mode*",
                        "",
                        "  A string from a restricted set of values",
                        "",
                        "  This options value is restricted to the following set of values:",
                        "  - a",
                        "  - b",
                        "  - c",
                        "",
                        "",
                        ""
                }, '\n'));
        
        //@formatter:on
    }

    public void testCopyrightLicenseMarkdown() throws IOException {
        SingleCommand<ArgsCopyrightAndLicense> cmd = singleCommand(ArgsCopyrightAndLicense.class);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        new MarkdownCommandUsageGenerator().usage(null, null, cmd.getCommandMetadata().getName(),
                cmd.getCommandMetadata(), null, out);
        //@formatter:off
        testStringAssert(new String(out.toByteArray(), utf8), 
                StringUtils.join(new String[] {
                        "# NAME",
                        "",
                        "`ArgsCopyrightAndLicense` -",
                        "",
                        "# SYNOPSIS",
                        "",
                        "`ArgsCopyrightAndLicense`",
                        "",
                        "# COPYRIGHT",
                        "",
                        "Copyright (c) Acme Inc 2015-2016",
                        "",
                        "# LICENSE",
                        "",
                        "This software is open source under the Apache License 2.0",
                        "",
                        "Please see http://apache.org/licenses/LICENSE-2.0 for more information",
                        "",
                        ""
                }, '\n'));
        //@formatter:on
    }
}
