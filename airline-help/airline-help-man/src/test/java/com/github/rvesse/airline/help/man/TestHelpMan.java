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
package com.github.rvesse.airline.help.man;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;

import com.github.rvesse.airline.CommandLineInterface;
import com.github.rvesse.airline.Git.Add;
import com.github.rvesse.airline.Git.RemoteAdd;
import com.github.rvesse.airline.Git.RemoteShow;
import com.github.rvesse.airline.SingleCommand;
import com.github.rvesse.airline.args.Args1;
import com.github.rvesse.airline.args.ArgsCopyrightAndLicense;
import com.github.rvesse.airline.args.ArgsExamples;
import com.github.rvesse.airline.args.ArgsExitCodes;
import com.github.rvesse.airline.args.ArgsMultiParagraphDiscussion;
import com.github.rvesse.airline.args.ArgsVersion;
import com.github.rvesse.airline.args.ArgsVersion2;
import com.github.rvesse.airline.args.ArgsVersion3;
import com.github.rvesse.airline.args.ArgsVersionMissing;
import com.github.rvesse.airline.args.ArgsVersionMissingSuppressed;
import com.github.rvesse.airline.builder.CliBuilder;
import com.github.rvesse.airline.help.Help;
import org.apache.commons.lang3.StringUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import static com.github.rvesse.airline.SingleCommand.singleCommand;
import static org.testng.Assert.assertEquals;

@Test
public class TestHelpMan {
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

    /**
     * Helper method that reads in the given file
     * 
     * @param f
     *            File
     * @return File contents
     * @throws IOException
     */
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

    public void testMultiParagraphDiscussionMan() throws IOException {
        SingleCommand<ArgsMultiParagraphDiscussion> cmd = singleCommand(ArgsMultiParagraphDiscussion.class);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ManCommandUsageGenerator generator = new ManCommandUsageGenerator();
        generator.usage(null, null, "ArgsMultiParagraphDiscussion", cmd.getCommandMetadata(), null, out);
        //@formatter:off
        testStringAssert(new String(out.toByteArray(), utf8), 
                StringUtils.join(new String[] {
                        ".TH \"ArgsMultiParagraphDiscussion\" \"1\" \"\" \"\" \"\"",
                        ".SH NAME",
                        ".IP \"\" 0",
                        "\\fBArgsMultiParagraphDiscussion\\fR",
                        ".SH SYNOPSIS",
                        ".IP \"\" 0",
                        "\\fBArgsMultiParagraphDiscussion\\fR ",
                        ".SH DISCUSSION",
                        ".IP \"\" 0",
                        "First paragraph",
                        ".IP \"\" 0",
                        "Middle paragraph",
                        ".IP \"\" 0",
                        "Final paragraph",
                        ""
                }, '\n'));
        //@formatter:on
    }

    public void testExamplesMan() throws IOException {
        SingleCommand<ArgsExamples> cmd = singleCommand(ArgsExamples.class);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ManCommandUsageGenerator generator = new ManCommandUsageGenerator();
        generator.usage(null, null, "ArgsExamples", cmd.getCommandMetadata(), null, out);
        //@formatter:off
        testStringAssert(new String(out.toByteArray(), utf8), 
            StringUtils.join(new String[] {
                ".TH \"ArgsExamples\" \"1\" \"\" \"\" \"\"",
                ".SH NAME",
                ".IP \"\" 0",
                "\\fBArgsExamples\\fR",
                ".SH SYNOPSIS",
                ".IP \"\" 0",
                "\\fBArgsExamples\\fR ", 
                ".SH EXAMPLES",
                ".IP \"\" 0",
                "ArgsExample",
                ".RS",
                ".IP \"\" 4",
                "Does nothing",
                ".IP \"\" 0",
                ".IP \"\" 0",
                "ArgsExample foo bar",
                ".RS",
                ".IP \"\" 4",
                "Foos a bar",
                ".IP \"\" 0",
                ""
            }, '\n')
        );
        //@formatter:on
    }
    
    public void testCopyrightLicenseMan() throws IOException {
        SingleCommand<ArgsCopyrightAndLicense> cmd = singleCommand(ArgsCopyrightAndLicense.class);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ManCommandUsageGenerator generator = new ManCommandUsageGenerator();
        generator.usage(null, null, "ArgsCopyrightAndLicense", cmd.getCommandMetadata(), null, out);
        //@formatter:off
        testStringAssert(new String(out.toByteArray(), utf8), 
                StringUtils.join(new String[] 
                {
                    ".TH \"ArgsCopyrightAndLicense\" \"1\" \"\" \"\" \"\"",
                    ".SH NAME",
                    ".IP \"\" 0",
                    "\\fBArgsCopyrightAndLicense\\fR",
                    ".SH SYNOPSIS",
                    ".IP \"\" 0",
                    "\\fBArgsCopyrightAndLicense\\fR ",
                    ".SH COPYRIGHT",
                    ".IP \"\" 0",
                    "Copyright (c) Acme Inc 2015\\-2016",
                    ".SH LICENSE",
                    ".IP \"\" 0",
                    "This software is open source under the Apache License 2.0",
                    ".IP \"\" 0",
                    "Please see http://apache.org/licenses/LICENSE\\-2.0 for more information",
                    ""
                }, '\n'));
        //@formatter:on
    }

    public void testMan() throws IOException {
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
        
        ManGlobalUsageGenerator<Runnable> generator = new ManGlobalUsageGenerator<Runnable>();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        generator.usage(gitParser.getMetadata(), out);
        String usage = new String(out.toByteArray(), utf8);
        assertEquals(usage,
                StringUtils.join(new String[] {
                        ".TH \"git\" \"1\" \"\" \"\" \"\"", 
                        ".SH NAME", 
                        ".IP \"\" 0", 
                        "\\fBgit\\fR \\-\\- the stupid content tracker", 
                        ".SH SYNOPSIS", 
                        ".IP \"\" 0", 
                        "\\fBgit\\fR [ \\fB\\-v\\fR ] [ \\fIgroup\\fR ] \\fIcommand\\fR [ \\fIcommand\\-args\\fR ]", 
                        ".IP \"\" 0", 
                        "the stupid content tracker", 
                        ".SH OPTIONS", 
                        ".RS", 
                        ".TP", 
                        "\\fB\\-v\\fR", 
                        ".RS", 
                        ".IP \"\" 4", 
                        "Verbose mode", 
                        ".RE", 
                        ".IP \"\" 0", 
                        ".SH COMMAND GROUPS", 
                        ".IP \"\" 0", 
                        "Commands are grouped as follows:", 
                        ".RS", 
                        ".TP", 
                        "Default (no \\fIgroup\\fR specified)", 
                        ".RS", 
                        ".TP", 
                        "\\fBadd\\fR", 
                        ".IP", 
                        "Add file contents to the index", 
                        ".TP", 
                        "\\fBhelp\\fR", 
                        ".IP", 
                        "Display help information", 
                        ".RE", 
                        ".TP", 
                        "\\fBremote\\fR", 
                        ".IP", 
                        "Manage set of tracked repositories", 
                        ".RS", 
                        ".TP", 
                        "\\fBadd\\fR", 
                        ".IP", 
                        "Adds a remote", 
                        ".TP", 
                        "\\fBshow\\fR", 
                        ".IP", 
                        "Gives some information about the remote <name>", 
                        ".RE", 
                        ".IP \"\" 0", 
                        ".TH \"git\\-add\" \"1\" \"\" \"\" \"\"", 
                        ".SH NAME", 
                        ".IP \"\" 0", 
                        "\\fBgit\\-add\\fR \\- Add file contents to the index", 
                        ".SH SYNOPSIS", 
                        ".IP \"\" 0", 
                        "\\fBgit\\fR [ \\fB\\-v\\fR ] \\fBadd\\fR [ \\fB\\-i\\fR ] [ \\fB\\-\\-\\fR ] [ \\fIpatterns\\fR ]", 
                        ".IP \"\" 0", 
                        "Add file contents to the index", 
                        ".SH OPTIONS", 
                        ".RS", 
                        ".TP", 
                        "\\fB\\-i\\fR", 
                        ".RS", 
                        ".IP \"\" 4", 
                        "Add modified contents interactively.", 
                        ".RE", 
                        ".TP", 
                        "\\fB\\-v\\fR", 
                        ".RS", 
                        ".IP \"\" 4", 
                        "Verbose mode", 
                        ".RE", 
                        ".TP", 
                        "\\fB\\-\\-\\fR", 
                        ".RS", 
                        ".IP \"\" 4", 
                        "This option can be used to separate command\\-line options from the list of arguments (useful when arguments might be mistaken for command\\-line options)", 
                        ".RE", 
                        ".TP", 
                        "\\fIpatterns\\fR", 
                        ".RS", 
                        ".IP \"\" 4", 
                        "Patterns of files to be added", 
                        ".RE", 
                        ".IP \"\" 0", 
                        ".TH \"git\\-help\" \"1\" \"\" \"\" \"\"", 
                        ".SH NAME", 
                        ".IP \"\" 0", 
                        "\\fBgit\\-help\\fR \\- Display help information", 
                        ".SH SYNOPSIS", 
                        ".IP \"\" 0", 
                        "\\fBgit\\fR \\fBhelp\\fR  [ \\fB\\-\\-\\fR ] [ \\fIcommand\\fR ]", 
                        ".IP \"\" 0", 
                        "Display help information", 
                        ".SH OPTIONS", 
                        ".RS", 
                        ".TP", 
                        "\\fB\\-\\-\\fR", 
                        ".RS", 
                        ".IP \"\" 4", 
                        "This option can be used to separate command\\-line options from the list of arguments (useful when arguments might be mistaken for command\\-line options)", 
                        ".RE", 
                        ".TP", 
                        "\\fIcommand\\fR", 
                        ".RS", 
                        ".IP \"\" 4", 
                        "", 
                        ".RE", 
                        ".IP \"\" 0", 
                        ".TH \"git\\-remote\\-add\" \"1\" \"\" \"\" \"\"", 
                        ".SH NAME", 
                        ".IP \"\" 0", 
                        "\\fBgit\\-remote\\-add\\fR \\- Adds a remote", 
                        ".SH SYNOPSIS", 
                        ".IP \"\" 0", 
                        "\\fBgit\\fR [ \\fB\\-v\\fR ] \\fBremote\\fR \\fBadd\\fR [ \\fB\\-t\\fR \\fIbranch\\fR ] [ \\fB\\-\\-\\fR ] [ \\fIname\\fR \\fIurl\\fR ]", 
                        ".IP \"\" 0", 
                        "Adds a remote", 
                        ".SH OPTIONS", 
                        ".RS", 
                        ".TP", 
                        "\\fB\\-t\\fR \\fIbranch\\fR", 
                        ".RS", 
                        ".IP \"\" 4", 
                        "Track only a specific branch", 
                        ".RE", 
                        ".TP", 
                        "\\fB\\-v\\fR", 
                        ".RS", 
                        ".IP \"\" 4", 
                        "Verbose mode", 
                        ".RE", 
                        ".TP", 
                        "\\fB\\-\\-\\fR", 
                        ".RS", 
                        ".IP \"\" 4", 
                        "This option can be used to separate command\\-line options from the list of arguments (useful when arguments might be mistaken for command\\-line options)", 
                        ".RE", 
                        ".TP", 
                        "\\fIname\\fR \\fIurl\\fR", 
                        ".RS", 
                        ".IP \"\" 4", 
                        "Name and URL of remote repository to add", 
                        ".RE", 
                        ".IP \"\" 0", 
                        ".TH \"git\\-remote\\-show\" \"1\" \"\" \"\" \"\"", 
                        ".SH NAME", 
                        ".IP \"\" 0", 
                        "\\fBgit\\-remote\\-show\\fR \\- Gives some information about the remote <name>", 
                        ".SH SYNOPSIS", 
                        ".IP \"\" 0", 
                        "\\fBgit\\fR [ \\fB\\-v\\fR ] \\fBremote\\fR \\fBshow\\fR [ \\fB\\-n\\fR ] [ \\fB\\-\\-\\fR ] [ \\fIremote\\fR ]", 
                        ".IP \"\" 0", 
                        "Gives some information about the remote <name>", 
                        ".SH OPTIONS", 
                        ".RS", 
                        ".TP", 
                        "\\fB\\-n\\fR", 
                        ".RS", 
                        ".IP \"\" 4", 
                        "Do not query remote heads", 
                        ".RE", 
                        ".TP", 
                        "\\fB\\-v\\fR", 
                        ".RS", 
                        ".IP \"\" 4", 
                        "Verbose mode", 
                        ".RE", 
                        ".TP", 
                        "\\fB\\-\\-\\fR", 
                        ".RS", 
                        ".IP \"\" 4", 
                        "This option can be used to separate command\\-line options from the list of arguments (useful when arguments might be mistaken for command\\-line options)", 
                        ".RE", 
                        ".TP", 
                        "\\fIremote\\fR", 
                        ".RS", 
                        ".IP \"\" 4", 
                        "Remote to show", 
                        ".RE", 
                        ".IP \"\" 0",
                        ""
                }, '\n'));
        //@formatter:on
    }

    public void testManMultiPage() throws IOException {
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
        
        ManGlobalUsageGenerator<Runnable> generator = new ManMultiPageGlobalUsageGenerator<Runnable>();
        FileOutputStream out = new FileOutputStream("target/git.1");
        generator.usage(gitParser.getMetadata(), out);
        
        File git = new File("target/git.1");
        Assert.assertTrue(git.exists());
        String usage = readFile(git);
        assertEquals(usage,
                StringUtils.join(new String[] {
                        ".TH \"git\" \"1\" \"\" \"\" \"\"",
                        ".SH NAME",
                        ".IP \"\" 0",
                        "\\fBgit\\fR \\-\\- the stupid content tracker",
                        ".SH SYNOPSIS",
                        ".IP \"\" 0",
                        "\\fBgit\\fR [ \\fB\\-v\\fR ] [ \\fIgroup\\fR ] \\fIcommand\\fR [ \\fIcommand\\-args\\fR ]",
                        ".IP \"\" 0",
                        "the stupid content tracker",
                        ".SH OPTIONS",
                        ".RS",
                        ".TP",
                        "\\fB\\-v\\fR",
                        ".RS",
                        ".IP \"\" 4",
                        "Verbose mode",
                        ".RE",
                        ".IP \"\" 0",
                        ".SH COMMAND GROUPS",
                        ".IP \"\" 0",
                        "Commands are grouped as follows:",
                        ".RS",
                        ".TP",
                        "Default (no \\fIgroup\\fR specified)",
                        ".RS",
                        ".TP",
                        "\\fBadd\\fR",
                        ".IP",
                        "Add file contents to the index",
                        ".TP",
                        "\\fBhelp\\fR",
                        ".IP",
                        "Display help information",
                        ".RE",
                        ".TP",
                        "\\fBremote\\fR",
                        ".IP",
                        "Manage set of tracked repositories",
                        ".RS",
                        ".TP",
                        "\\fBadd\\fR",
                        ".IP",
                        "Adds a remote",
                        ".TP",
                        "\\fBshow\\fR",
                        ".IP",
                        "Gives some information about the remote <name>",
                        ".RE",
                        ".IP \"\" 0",
                        ""
                }, '\n'));
        
        File gitHelp = new File("git-help.1");
        Assert.assertTrue(gitHelp.exists());
        usage = readFile(gitHelp);
        assertEquals(usage,
                StringUtils.join(new String[] {
                        ".TH \"git\\-help\" \"1\" \"\" \"\" \"\"",
                        ".SH NAME",
                        ".IP \"\" 0",
                        "\\fBgit\\-help\\fR \\- Display help information",
                        ".SH SYNOPSIS",
                        ".IP \"\" 0",
                        "\\fBgit\\fR \\fBhelp\\fR  [ \\fB\\-\\-\\fR ] [ \\fIcommand\\fR ]",
                        ".IP \"\" 0",
                        "Display help information",
                        ".SH OPTIONS",
                        ".RS",
                        ".TP",
                        "\\fB\\-\\-\\fR",
                        ".RS",
                        ".IP \"\" 4",
                        "This option can be used to separate command\\-line options from the list of arguments (useful when arguments might be mistaken for command\\-line options)",
                        ".RE",
                        ".TP",
                        "\\fIcommand\\fR",
                        ".RS",
                        ".IP \"\" 4",
                        "",
                        ".RE",
                        ".IP \"\" 0",
                        ""
                }, '\n'));
        gitHelp.delete();
        
        File gitAdd = new File("git-add.1");
        Assert.assertTrue(gitAdd.exists());
        usage = readFile(gitAdd);
        assertEquals(usage,
                StringUtils.join(new String[] {
                        ".TH \"git\\-add\" \"1\" \"\" \"\" \"\"",
                        ".SH NAME",
                        ".IP \"\" 0",
                        "\\fBgit\\-add\\fR \\- Add file contents to the index",
                        ".SH SYNOPSIS",
                        ".IP \"\" 0",
                        "\\fBgit\\fR [ \\fB\\-v\\fR ] \\fBadd\\fR [ \\fB\\-i\\fR ] [ \\fB\\-\\-\\fR ] [ \\fIpatterns\\fR ]",
                        ".IP \"\" 0",
                        "Add file contents to the index",
                        ".SH OPTIONS",
                        ".RS",
                        ".TP",
                        "\\fB\\-i\\fR",
                        ".RS",
                        ".IP \"\" 4",
                        "Add modified contents interactively.",
                        ".RE",
                        ".TP",
                        "\\fB\\-v\\fR",
                        ".RS",
                        ".IP \"\" 4",
                        "Verbose mode",
                        ".RE",
                        ".TP",
                        "\\fB\\-\\-\\fR",
                        ".RS",
                        ".IP \"\" 4",
                        "This option can be used to separate command\\-line options from the list of arguments (useful when arguments might be mistaken for command\\-line options)",
                        ".RE",
                        ".TP",
                        "\\fIpatterns\\fR",
                        ".RS",
                        ".IP \"\" 4",
                        "Patterns of files to be added",
                        ".RE",
                        ".IP \"\" 0",
                        ""
                }, '\n'));
        gitAdd.delete();
        
        File gitRemoteShow = new File("git-remote-show.1");
        Assert.assertTrue(gitRemoteShow.exists());
        usage = readFile(gitRemoteShow);
        assertEquals(usage,
                StringUtils.join(new String[] {
                        ".TH \"git\\-remote\\-show\" \"1\" \"\" \"\" \"\"",
                        ".SH NAME",
                        ".IP \"\" 0",
                        "\\fBgit\\-remote\\-show\\fR \\- Gives some information about the remote <name>",
                        ".SH SYNOPSIS",
                        ".IP \"\" 0",
                        "\\fBgit\\fR [ \\fB\\-v\\fR ] \\fBremote\\fR \\fBshow\\fR [ \\fB\\-n\\fR ] [ \\fB\\-\\-\\fR ] [ \\fIremote\\fR ]",
                        ".IP \"\" 0",
                        "Gives some information about the remote <name>",
                        ".SH OPTIONS",
                        ".RS",
                        ".TP",
                        "\\fB\\-n\\fR",
                        ".RS",
                        ".IP \"\" 4",
                        "Do not query remote heads",
                        ".RE",
                        ".TP",
                        "\\fB\\-v\\fR",
                        ".RS",
                        ".IP \"\" 4",
                        "Verbose mode",
                        ".RE",
                        ".TP",
                        "\\fB\\-\\-\\fR",
                        ".RS",
                        ".IP \"\" 4",
                        "This option can be used to separate command\\-line options from the list of arguments (useful when arguments might be mistaken for command\\-line options)",
                        ".RE",
                        ".TP",
                        "\\fIremote\\fR",
                        ".RS",
                        ".IP \"\" 4",
                        "Remote to show",
                        ".RE",
                        ".IP \"\" 0",
                        ""
                }, '\n'));
        gitRemoteShow.delete();
        
        File gitRemoteAdd = new File("git-remote-add.1");
        Assert.assertTrue(gitRemoteAdd.exists());
        usage = readFile(gitRemoteAdd);
        assertEquals(usage,
                StringUtils.join(new String[] {
                        ".TH \"git\\-remote\\-add\" \"1\" \"\" \"\" \"\"",
                        ".SH NAME",
                        ".IP \"\" 0",
                        "\\fBgit\\-remote\\-add\\fR \\- Adds a remote",
                        ".SH SYNOPSIS",
                        ".IP \"\" 0",
                        "\\fBgit\\fR [ \\fB\\-v\\fR ] \\fBremote\\fR \\fBadd\\fR [ \\fB\\-t\\fR \\fIbranch\\fR ] [ \\fB\\-\\-\\fR ] [ \\fIname\\fR \\fIurl\\fR ]",
                        ".IP \"\" 0",
                        "Adds a remote",
                        ".SH OPTIONS",
                        ".RS",
                        ".TP",
                        "\\fB\\-t\\fR \\fIbranch\\fR",
                        ".RS",
                        ".IP \"\" 4",
                        "Track only a specific branch",
                        ".RE",
                        ".TP",
                        "\\fB\\-v\\fR",
                        ".RS",
                        ".IP \"\" 4",
                        "Verbose mode",
                        ".RE",
                        ".TP",
                        "\\fB\\-\\-\\fR",
                        ".RS",
                        ".IP \"\" 4",
                        "This option can be used to separate command\\-line options from the list of arguments (useful when arguments might be mistaken for command\\-line options)",
                        ".RE",
                        ".TP",
                        "\\fIname\\fR \\fIurl\\fR",
                        ".RS",
                        ".IP \"\" 4",
                        "Name and URL of remote repository to add",
                        ".RE",
                        ".IP \"\" 0",
                        ""
                }, '\n'));
        gitRemoteAdd.delete();
        //@formatter:on
    }
    
    public void testManMultiPageNoGroups() throws IOException {
        @SuppressWarnings("unchecked")
        CliBuilder<Object> builder = new CliBuilder<Object>("multi-page")
                .withDefaultCommand(Args1.class)
                .withCommands(Args1.class, ArgsVersion.class);
        CommandLineInterface<Object> cli = builder.build();
        
        ManMultiPageGlobalUsageGenerator<Object> generator = new ManMultiPageGlobalUsageGenerator<>(ManSections.GENERAL_COMMANDS, false, new File("target/"));
        FileOutputStream mainFile = new FileOutputStream("target/multi-page.1");
        generator.usage(cli.getMetadata(), mainFile);
        mainFile.close();
        new File("target/multi-page.1").delete();
        
        File args1File = new File("target/multi-page-Args1.1");
        Assert.assertTrue(args1File.exists());
        args1File.delete();
        File argsVersionFile = new File("target/multi-page-ArgsVersion.1");
        Assert.assertTrue(argsVersionFile.exists());
        argsVersionFile.delete();
    }

    public void testExitCodesMan() throws IOException {
        //@formatter:off
        SingleCommand<ArgsExitCodes> command = singleCommand(ArgsExitCodes.class);
    
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        new ManCommandUsageGenerator().usage(null, null, "test", command.getCommandMetadata(), null, out);
        assertEquals(new String(out.toByteArray(), utf8),
                StringUtils.join(new String[] {
                        ".TH \"test\" \"1\" \"\" \"\" \"\"",
                        ".SH NAME",
                        ".IP \"\" 0",
                        "\\fBtest\\fR \\- ArgsExitCodes description",
                        ".SH SYNOPSIS",
                        ".IP \"\" 0",
                        "\\fBtest\\fR ",
                        ".IP \"\" 0",
                        "ArgsExitCodes description",
                        ".SH EXIT CODES",
                        ".IP \"\" 0",
                        "This command returns one of the following exit codes:",
                        ".TS",
                        "box;",
                        "l | l .",
                        "0\tSuccess",
                        "1\t",
                        "2\tError 2",
                        ".TE",
                        ""
                }, '\n'));
        //@formatter:on
    }
    
    public void testManOptionsAndArgsIndentation_01() throws IOException {
        SingleCommand<ArgsManMixed> command = singleCommand(ArgsManMixed.class);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        new ManCommandUsageGenerator().usage(null, null, "test", command.getCommandMetadata(), null, out);
        
        //@formatter:off
        assertEquals(new String(out.toByteArray(), utf8),
                StringUtils.join(new String[] {
                        ".TH \"test\" \"1\" \"\" \"\" \"\"",
                        ".SH NAME",
                        ".IP \"\" 0",
                        "\\fBtest\\fR",
                        ".SH SYNOPSIS",
                        ".IP \"\" 0",
                        "\\fBtest\\fR [ \\fB\\-\\-flag\\fR ] [ \\fB\\-\\-\\fR ] [ \\fIarguments\\fR ]",
                        ".SH OPTIONS",
                        ".RS",
                        ".TP",
                        "\\fB\\-\\-flag\\fR",
                        ".RS",
                        ".IP \"\" 4",
                        "",
                        ".RE",
                        ".TP",
                        "\\fB\\-\\-\\fR",
                        ".RS",
                        ".IP \"\" 4",
                        "This option can be used to separate command\\-line options from the list of arguments (useful when arguments might be mistaken for command\\-line options)",
                        ".RE",
                        ".TP",
                        "\\fIarguments\\fR",
                        ".RS",
                        ".IP \"\" 4",
                        "",
                        ".RE",
                        ".IP \"\" 0",
                        ""
                }, '\n'));
        //@formatter:on
    }
    
    public void testManOptionsAndArgsIndentation_02() throws IOException {
        SingleCommand<ArgsManMixed2> command = singleCommand(ArgsManMixed2.class);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        new ManCommandUsageGenerator().usage(null, null, "test", command.getCommandMetadata(), null, out);
        
        //@formatter:off
        assertEquals(new String(out.toByteArray(), utf8),
                StringUtils.join(new String[] {
                        ".TH \"test\" \"1\" \"\" \"\" \"\"",
                        ".SH NAME",
                        ".IP \"\" 0",
                        "\\fBtest\\fR",
                        ".SH SYNOPSIS",
                        ".IP \"\" 0",
                        "\\fBtest\\fR  [ \\fB\\-\\-\\fR ] [ \\fIarguments\\fR ]",
                        ".SH OPTIONS",
                        ".RS",
                        ".TP",
                        "\\fB\\-\\-\\fR",
                        ".RS",
                        ".IP \"\" 4",
                        "This option can be used to separate command\\-line options from the list of arguments (useful when arguments might be mistaken for command\\-line options)",
                        ".RE",
                        ".TP",
                        "\\fIarguments\\fR",
                        ".RS",
                        ".IP \"\" 4",
                        "",
                        ".RE",
                        ".IP \"\" 0",
                        ""
                }, '\n'));
        //@formatter:on
    }
    
    public void testManOptionsAndArgsIndentation_03() throws IOException {
        SingleCommand<ArgsManMixed3> command = singleCommand(ArgsManMixed3.class);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        new ManCommandUsageGenerator().usage(null, null, "test", command.getCommandMetadata(), null, out);
        
        //@formatter:off
        assertEquals(new String(out.toByteArray(), utf8),
                StringUtils.join(new String[] {
                        ".TH \"test\" \"1\" \"\" \"\" \"\"",
                        ".SH NAME",
                        ".IP \"\" 0",
                        "\\fBtest\\fR",
                        ".SH SYNOPSIS",
                        ".IP \"\" 0",
                        "\\fBtest\\fR [ \\fB\\-\\-flag\\fR ] [ \\fB\\-\\-\\fR ] [ \\fIarguments\\fR ]",
                        ".SH OPTIONS",
                        ".RS",
                        ".TP",
                        "\\fB\\-\\-flag\\fR",
                        ".RS",
                        ".IP \"\" 4",
                        "",
                        ".RE",
                        ".TP",
                        "\\fB\\-\\-\\fR",
                        ".RS",
                        ".IP \"\" 4",
                        "This option can be used to separate command\\-line options from the list of arguments (useful when arguments might be mistaken for command\\-line options)",
                        ".RE",
                        ".TP",
                        "\\fIarguments\\fR",
                        ".RS",
                        ".IP \"\" 4",
                        "",
                        ".RE",
                        ".IP \"\" 0",
                        ""
                }, '\n'));
        //@formatter:on
    }
    
    public void testManOptionsOnly_01() throws IOException {
        SingleCommand<ArgsManOption> command = singleCommand(ArgsManOption.class);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        new ManCommandUsageGenerator().usage(null, null, "test", command.getCommandMetadata(), null, out);
        
        //@formatter:off
        assertEquals(new String(out.toByteArray(), utf8),
                StringUtils.join(new String[] {
                        ".TH \"test\" \"1\" \"\" \"\" \"\"",
                        ".SH NAME",
                        ".IP \"\" 0",
                        "\\fBtest\\fR",
                        ".SH SYNOPSIS",
                        ".IP \"\" 0",
                        "\\fBtest\\fR [ \\fB\\-\\-flag\\fR ]",
                        ".SH OPTIONS",
                        ".RS",
                        ".TP",
                        "\\fB\\-\\-flag\\fR",
                        ".RS",
                        ".IP \"\" 4",
                        "",
                        ".RE",
                        ".IP \"\" 0",
                        ""
                }, '\n'));
        //@formatter:on
    }
    
    public void testManOptionsOnly_02() throws IOException {
        SingleCommand<ArgsManOption2> command = singleCommand(ArgsManOption2.class);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        new ManCommandUsageGenerator().usage(null, null, "test", command.getCommandMetadata(), null, out);
        
        //@formatter:off
        assertEquals(new String(out.toByteArray(), utf8),
                StringUtils.join(new String[] {
                        ".TH \"test\" \"1\" \"\" \"\" \"\"",
                        ".SH NAME",
                        ".IP \"\" 0",
                        "\\fBtest\\fR",
                        ".SH SYNOPSIS",
                        ".IP \"\" 0",
                        "\\fBtest\\fR ",
                        ".SH OPTIONS",
                        ""
                }, '\n'));
        //@formatter:on
    }
    
    public void testManArgsOnly_01() throws IOException {
        SingleCommand<ArgsManArgs> command = singleCommand(ArgsManArgs.class);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        new ManCommandUsageGenerator().usage(null, null, "test", command.getCommandMetadata(), null, out);
        
        //@formatter:off
        assertEquals(new String(out.toByteArray(), utf8),
                StringUtils.join(new String[] {
                        ".TH \"test\" \"1\" \"\" \"\" \"\"",
                        ".SH NAME",
                        ".IP \"\" 0",
                        "\\fBtest\\fR",
                        ".SH SYNOPSIS",
                        ".IP \"\" 0",
                        "\\fBtest\\fR  [ \\fB\\-\\-\\fR ] [ \\fIarguments\\fR ]",
                        ".SH OPTIONS",
                        ".RS",
                        ".TP",
                        "\\fB\\-\\-\\fR",
                        ".RS",
                        ".IP \"\" 4",
                        "This option can be used to separate command\\-line options from the list of arguments (useful when arguments might be mistaken for command\\-line options)",
                        ".RE",
                        ".TP",
                        "\\fIarguments\\fR",
                        ".RS",
                        ".IP \"\" 4",
                        "",
                        ".RE",
                        ".IP \"\" 0",
                        ""
                }, '\n'));
        //@formatter:on
    }
    
    public void testManArgsOnly_02() throws IOException {
        SingleCommand<ArgsManPositionalArgs> command = singleCommand(ArgsManPositionalArgs.class);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        new ManCommandUsageGenerator().usage(null, null, "test", command.getCommandMetadata(), null, out);
        
        //@formatter:off
        assertEquals(new String(out.toByteArray(), utf8),
                StringUtils.join(new String[] {
                        ".TH \"test\" \"1\" \"\" \"\" \"\"",
                        ".SH NAME",
                        ".IP \"\" 0",
                        "\\fBtest\\fR",
                        ".SH SYNOPSIS",
                        ".IP \"\" 0",
                        "\\fBtest\\fR  [ \\fB\\-\\-\\fR ] \\fIFile\\fR [ \\fIMode\\fR ] [ \\fIExtraArg\\fR ]",
                        ".SH OPTIONS",
                        ".RS",
                        ".TP",
                        "\\fB\\-\\-\\fR",
                        ".RS",
                        ".IP \"\" 4",
                        "This option can be used to separate command\\-line options from the list of arguments (useful when arguments might be mistaken for command\\-line options)",
                        ".RE",
                        ".TP",
                        "\\fIFile\\fR",
                        ".RS",
                        ".IP \"\" 4",
                        "File to operate on",
                        ".RE",
                        ".TP",
                        "\\fIMode\\fR",
                        ".RS",
                        ".IP \"\" 4",
                        "File mode to set",
                        ".RE",
                        ".TP",
                        "\\fIExtraArg\\fR",
                        ".RS",
                        ".IP \"\" 4",
                        "Additional arguments",
                        ".RE",
                        ".IP \"\" 0",
                        ""
                }, '\n'));
        //@formatter:on
    }
    
    public void testManNone_01() throws IOException {
        SingleCommand<ArgsManNone> command = singleCommand(ArgsManNone.class);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        new ManCommandUsageGenerator().usage(null, null, "test", command.getCommandMetadata(), null, out);
        
        //@formatter:off
        assertEquals(new String(out.toByteArray(), utf8),
                StringUtils.join(new String[] {
                        ".TH \"test\" \"1\" \"\" \"\" \"\"",
                        ".SH NAME",
                        ".IP \"\" 0",
                        "\\fBtest\\fR",
                        ".SH SYNOPSIS",
                        ".IP \"\" 0",
                        "\\fBtest\\fR ",
                        ""
                }, '\n'));
        //@formatter:on
    }
    
    @Test
    public void testVersion() throws IOException {
        //@formatter:off
        SingleCommand<ArgsVersion> command = singleCommand(ArgsVersion.class);
    
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        new ManCommandUsageGenerator().usage(null, null, "test", command.getCommandMetadata(), null, out);
        testStringAssert(new String(out.toByteArray(), utf8),
                StringUtils.join(new String[] {
                        ".TH \"test\" \"1\" \"\" \"\" \"\"",
                        ".SH NAME",
                        ".IP \"\" 0",
                        "\\fBtest\\fR \\- ArgsVersion description",
                        ".SH SYNOPSIS",
                        ".IP \"\" 0",
                        "\\fBtest\\fR ",
                        ".IP \"\" 0",
                        "ArgsVersion description",
                        ".SH VERSION",
                        ".RS",
                        ".IP \"-\" 4",
                        "Component: Airline Test",
                        ".IP \"-\" 4",
                        "Version: 1.2.3",
                        ".IP \"-\" 4",
                        "Build: 12345abcde",
                        ".IP \"\" 0",
                        ""
                }, '\n'));
        //@formatter:on
    }
    
    @Test
    public void testVersionComponents() throws IOException {
        //@formatter:off
        SingleCommand<ArgsVersion2> command = singleCommand(ArgsVersion2.class);
    
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        new ManCommandUsageGenerator().usage(null, null, "test", command.getCommandMetadata(), null, out);
        testStringAssert(new String(out.toByteArray(), utf8),
                StringUtils.join(new String[] {
                        ".TH \"test\" \"1\" \"\" \"\" \"\"",
                        ".SH NAME",
                        ".IP \"\" 0",
                        "\\fBtest\\fR \\- Multiple component versions",
                        ".SH SYNOPSIS",
                        ".IP \"\" 0",
                        "\\fBtest\\fR ",
                        ".IP \"\" 0",
                        "Multiple component versions",
                        ".SH VERSION",
                        ".RS",
                        ".IP \"-\" 4",
                        "Component: Airline Test",
                        ".IP \"-\" 4",
                        "Version: 1.2.3",
                        ".IP \"-\" 4",
                        "Build: 12345abcde",
                        ".IP \"\" 0",
                        ".RS",
                        ".IP \"-\" 4",
                        "Component: Foo",
                        ".IP \"-\" 4",
                        "Build: 789",
                        ".IP \"-\" 4",
                        "Build Date: Feb 2016",
                        ".IP \"-\" 4",
                        "Author: Mr Foo",
                        ".IP \"\" 0",
                        ".RS",
                        ".IP \"-\" 4",
                        "Component: Bar",
                        ".IP \"-\" 4",
                        "Version: 1.0.7",
                        ".IP \"-\" 4",
                        "Built With: Oracle JDK 1.7",
                        ".IP \"-\" 4",
                        "Author: Mrs Bar",
                        ".IP \"\" 0",
                        ""
                   }, '\n'));
        //@formatter:on
    }
    
    @Test
    public void testVersionComponentsTabular() throws IOException {
        //@formatter:off
        SingleCommand<ArgsVersion3> command = singleCommand(ArgsVersion3.class);
    
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        new ManCommandUsageGenerator().usage(null, null, "test", command.getCommandMetadata(), null, out);
        testStringAssert(new String(out.toByteArray(), utf8),
                StringUtils.join(new String[] {
                        ".TH \"test\" \"1\" \"\" \"\" \"\"",
                        ".SH NAME",
                        ".IP \"\" 0",
                        "\\fBtest\\fR \\- Multiple component versions",
                        ".SH SYNOPSIS",
                        ".IP \"\" 0",
                        "\\fBtest\\fR ",
                        ".IP \"\" 0",
                        "Multiple component versions",
                        ".SH VERSION",
                        ".TS",
                        "box;",
                        "cb | cb | cb | cb | cb | cb",
                        "l | l | l | l | l | l .",
                        "Component\tVersion\tBuild\tBuild Date\tAuthor\tBuilt With",
                        "_\t|\t_\t|\t_\t|\t_\t|\t_\t|\t_",
                        "Airline Test\t1.2.3\t12345abcde\t\t\t",
                        "Foo\t\t789\tFeb 2016\tMr Foo\t",
                        "Bar\t1.0.7\t\t\tMrs Bar\tOracle JDK 1.7",
                        ".TE",
                        ""
                   }, '\n'));
        //@formatter:on
    }
    
    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = ".*missing\\.version.*")
    public void testVersionMissing() throws IOException {
        //@formatter:off
        SingleCommand<ArgsVersionMissing> command = singleCommand(ArgsVersionMissing.class);
    
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        new ManCommandUsageGenerator().usage(null, null, "test", command.getCommandMetadata(), null, out);
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
    public void testVersionMissingSupressed() throws IOException {
        //@formatter:off
        SingleCommand<ArgsVersionMissingSuppressed> command = singleCommand(ArgsVersionMissingSuppressed.class);
    
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        new ManCommandUsageGenerator().usage(null, null, "test", command.getCommandMetadata(), null, out);
        testStringAssert(new String(out.toByteArray(), utf8),
                StringUtils.join(new String[] {
                        ".TH \"test\" \"1\" \"\" \"\" \"\"",
                        ".SH NAME",
                        ".IP \"\" 0",
                        "\\fBtest\\fR \\- Missing version information",
                        ".SH SYNOPSIS",
                        ".IP \"\" 0",
                        "\\fBtest\\fR ",
                        ".IP \"\" 0",
                        "Missing version information",
                        ""
                }, '\n'));
        //@formatter:on
    }
}
