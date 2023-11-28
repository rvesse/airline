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
package com.github.rvesse.airline.tests.command;

import com.github.rvesse.airline.Cli;
import com.github.rvesse.airline.CommandLineInterface;
import com.github.rvesse.airline.builder.CliBuilder;
import com.github.rvesse.airline.help.sections.HelpFormat;
import com.github.rvesse.airline.help.sections.HelpSection;
import com.github.rvesse.airline.help.sections.common.DiscussionSection;
import com.github.rvesse.airline.help.sections.common.ExamplesSection;
import com.github.rvesse.airline.model.CommandMetadata;
import com.github.rvesse.airline.parser.errors.ParseException;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.github.rvesse.airline.tests.TestingUtil.singleCommandParser;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertFalse;

public class TestCommand {
    @Test
    public void namedCommandTest1() {
        //@formatter:off
        CommandLineInterface<Object> parser = CommandLineInterface.builder("git")
                           .withCommand(CommandAdd.class)
                           .withCommand(CommandCommit.class)
                           .build();
        //@formatter:on

        Object command = parser.parse("add", "-i", "A.java");
        assertNotNull(command, "command is null");
        assertTrue(command instanceof CommandAdd);
        CommandAdd add = (CommandAdd) command;
        assertEquals(add.interactive.booleanValue(), true);
        assertEquals(add.patterns, Arrays.asList("A.java"));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldComplainIfNoAnnotations() {
        singleCommandParser(String.class);
    }

    @Test
    public void commandTest2() {
        //@formatter:off
        CommandLineInterface<Object> parser = CommandLineInterface.builder("git")
                           .withCommand(CommandAdd.class)
                           .withCommand(CommandCommit.class)
                           .build();
        //@formatter:on
        parser.parse("-v", "commit", "--amend", "--author", "cbeust", "A.java", "B.java");

        Object command = parser.parse("-v", "commit", "--amend", "--author", "cbeust", "A.java", "B.java");
        assertNotNull(command, "command is null");
        assertTrue(command instanceof CommandCommit);
        CommandCommit commit = (CommandCommit) command;

        assertTrue(commit.commandMain.verbose);
        assertTrue(commit.amend);
        assertEquals(commit.author, "cbeust");
        assertEquals(commit.files, Arrays.asList("A.java", "B.java"));
    }
    
    private HelpSection findHelpSection(Collection<HelpSection> sections, Class<? extends HelpSection> cls) {
        for (HelpSection section : sections) {
            if (section.getClass().equals(cls)) return section;
        }
        return null;
    }

    @Test
    public void testExample() {
        //@formatter:off
        CommandLineInterface<Object> parser = CommandLineInterface.builder("git")
                           .withCommand(CommandRemove.class)
                           .build();
        //@formatter:on

        final List<CommandMetadata> commandParsers = parser.getMetadata().getDefaultGroupCommands();

        assertEquals(1, commandParsers.size());

        CommandMetadata aMeta = commandParsers.get(0);

        assertEquals("remove", aMeta.getName());

        HelpSection section = findHelpSection(aMeta.getHelpSections(), ExamplesSection.class);
        Assert.assertNotNull(section);
        assertEquals(section.getFormat(), HelpFormat.EXAMPLES);
        assertEquals(section.numContentBlocks(), 2);
        assertEquals(section.getContentBlock(0).length, 1);
        assertEquals(section.getContentBlock(1).length, 1);
        assertEquals(section.getContentBlock(0)[0], "$ git remove -i myfile.java");
        assertEquals(section.getContentBlock(1)[0], "This is a usage example");
    }

    @Test
    public void testDiscussion() {
        //@formatter:off
        CommandLineInterface<Object> parser = CommandLineInterface.builder("git")
                           .withCommand(CommandRemove.class)
                           .build();
        //@formatter:on

        final List<CommandMetadata> commandParsers = parser.getMetadata().getDefaultGroupCommands();

        assertEquals(1, commandParsers.size());

        CommandMetadata aMeta = commandParsers.get(0);

        assertEquals("remove", aMeta.getName());

        HelpSection section = findHelpSection(aMeta.getHelpSections(), DiscussionSection.class);
        Assert.assertNotNull(section);
        assertEquals(section.getFormat(), HelpFormat.PROSE);
        assertEquals(section.numContentBlocks(), 1);
        assertEquals(section.getContentBlock(0).length, 1);
        assertEquals(section.getContentBlock(0)[0], "More details about how this removes files from the index.");
    }

    @Test
    public void testDefaultCommandInGroup() {
        //@formatter:off
        CommandLineInterface<Object> parser = CommandLineInterface.builder("git")
                           .withCommand(CommandAdd.class)
                           .withCommand(CommandCommit.class)
                           .withDefaultCommand(CommandAdd.class)
                           .build();
        //@formatter:on

        Object command = parser.parse("-i", "A.java");

        assertNotNull(command, "command is null");
        assertTrue(command instanceof CommandAdd);
        CommandAdd add = (CommandAdd) command;
        assertEquals(add.interactive.booleanValue(), true);
        assertEquals(add.patterns, Arrays.asList("A.java"));
    }

    @Test
    public void testCommandWithArgsSeparator() {
        //@formatter:off
        CommandLineInterface<Object> parser = CommandLineInterface.builder("git")
                           .withCommand(CommandHighArityOption.class)
                           .build();
        //@formatter:on

        Object command = parser.parse("-v", "cmd", "--option", "val1", "val2", "val3", "val4", "--", "arg1", "arg2",
                "arg3");
        assertNotNull(command, "command is null");
        assertTrue(command instanceof CommandHighArityOption);
        CommandHighArityOption cmdHighArity = (CommandHighArityOption) command;

        assertTrue(cmdHighArity.commandMain.verbose);
        assertEquals(cmdHighArity.option, Arrays.asList("val1", "val2", "val3", "val4"));
        assertEquals(cmdHighArity.args, Arrays.asList("arg1", "arg2", "arg3"));
    }

    @Test
    public void testCommandHighArityOptionNoSeparator() {
        //@formatter:off
        CommandLineInterface<Object> parser = CommandLineInterface.builder("git")
                           .withCommand(CommandHighArityOption.class)
                           .build();
        //@formatter:on

        // it should be able to stop parsing option values for --option if it
        // finds the right number of values
        Object command = parser.parse("-v", "cmd", "--option", "val1", "val2", "val3", "val4", "--option2", "val5",
                "arg1", "arg2", "arg3");
        assertNotNull(command, "command is null");
        assertTrue(command instanceof CommandHighArityOption);
        CommandHighArityOption cmdHighArity = (CommandHighArityOption) command;

        assertTrue(cmdHighArity.commandMain.verbose);
        assertEquals(cmdHighArity.option, Arrays.asList("val1", "val2", "val3", "val4"));
        assertEquals(cmdHighArity.option2, "val5");
        assertEquals(cmdHighArity.args, Arrays.asList("arg1", "arg2", "arg3"));
    }

    @Test
    public void abbreviatedCommands01() {
        //@formatter:off
        CliBuilder<Object> builder = CommandLineInterface.builder("git")
                                   .withCommand(CommandAdd.class)
                                   .withCommand(CommandCommit.class)
                                   .withDefaultCommand(CommandAdd.class);
        builder.withParser()
               .withCommandAbbreviation();
        //@formatter:on
        CommandLineInterface<Object> parser = builder.build();

        Object command = parser.parse("ad", "-i", "A.java");

        assertNotNull(command, "command is null");
        assertTrue(command instanceof CommandAdd);
        CommandAdd add = (CommandAdd) command;
        assertEquals(add.interactive.booleanValue(), true);
        assertEquals(add.patterns, Arrays.asList("A.java"));
    }

    @Test(expectedExceptions = ParseException.class)
    public void abbreviatedCommands02() {
        //@formatter:off
        CliBuilder<Object> builder = CommandLineInterface.builder("git")
                                   .withCommand(CommandRemove.class)
                                   .withCommand(CommandRemote.class);
        builder.withParser()
               .withCommandAbbreviation();
        //@formatter:on
        CommandLineInterface<Object> parser = builder.build();

        // Expect this to error as abbreviation is ambiguous
        parser.parse("rem");
    }

    @Test
    public void abbreviatedCommands03() {
        //@formatter:off
        CliBuilder<Object> builder = CommandLineInterface.builder("git")
                                   .withCommand(CommandRemove.class)
                                   .withCommand(CommandRemote.class);
        builder.withParser()
               .withCommandAbbreviation();
        //@formatter:on
        CommandLineInterface<Object> parser = builder.build();

        // Abbreviation is non-ambigious
        Object command = parser.parse("remot");

        assertNotNull(command, "command is null");
        assertTrue(command instanceof CommandRemote);
    }

    @Test
    public void abbreviatedCommands04() {
        //@formatter:off
        CliBuilder<Object> builder = CommandLineInterface.builder("git")
                                   .withCommand(CommandRemove.class)
                                   .withCommand(CommandRemote.class);
        builder.withParser()
               .withCommandAbbreviation();
        //@formatter:on
        CommandLineInterface<Object> parser = builder.build();

        // Abbreviation is non-ambigious
        Object command = parser.parse("remov");

        assertNotNull(command, "command is null");
        assertTrue(command instanceof CommandRemove);
    }

    @Test
    public void abbreviatedCommands05() {
        //@formatter:off
        CliBuilder<Object> builder = CommandLineInterface.builder("git")
                                   .withCommand(CommandRemotes.class)
                                   .withCommand(CommandRemote.class);
        builder.withParser()
               .withCommandAbbreviation();
        //@formatter:on
        CommandLineInterface<Object> parser = builder.build();

        // Command name which is also an abbreviation of another command name
        Object command = parser.parse("remote");

        assertNotNull(command, "command is null");
        assertTrue(command instanceof CommandRemote);
    }

    @Test
    public void abbreviatedOptions01() {
        //@formatter:off
        CliBuilder<Object> builder = CommandLineInterface.builder("git")
                                   .withCommand(CommandCommit.class);
        builder.withParser()
               .withOptionAbbreviation();
        //@formatter:on
        CommandLineInterface<Object> parser = builder.build();

        // Non-ambigious abbreviation of an option
        Object command = parser.parse("commit", "--am");

        assertNotNull(command);
        ;
        assertTrue(command instanceof CommandCommit);
        CommandCommit commit = (CommandCommit) command;
        assertTrue(commit.amend);
    }

    @Test
    public void abbreviatedOptions02() {
        //@formatter:off
        CliBuilder<Object> builder = CommandLineInterface.builder("git")
                                   .withCommand(CommandCommit.class);
        builder.withParser()
               .withOptionAbbreviation();
        //@formatter:on
        CommandLineInterface<Object> parser = builder.build();

        // Will parse but as abbreviation is ambigious will be treated as an
        // argument and not an option
        Object command = parser.parse("commit", "--a");
        assertTrue(command instanceof CommandCommit);
        CommandCommit commit = (CommandCommit) command;
        assertFalse(commit.amend);
        assertNull(commit.author);
        assertTrue(commit.files.contains("--a"));
    }

    @Test
    public void abbreviatedOptions03() {
        //@formatter:off
        CliBuilder<Object> builder = CommandLineInterface.builder("git")
                                   .withCommand(CommandCommits.class);
        builder.withParser()
               .withOptionAbbreviation();
        //@formatter:on
        CommandLineInterface<Object> parser = builder.build();

        // Option name which is also an abbreviation of another option name
        Object command = parser.parse("commits", "--author", "test");

        assertNotNull(command);
        assertTrue(command instanceof CommandCommits);
        CommandCommits commit = (CommandCommits) command;
        assertEquals(commit.author, "test");
    }

    @Test
    public void abbreviatedOptions04() {
        //@formatter:off
        CliBuilder<Object> builder = CommandLineInterface.builder("git")
                                   .withCommand(CommandCommits.class);
        builder.withParser()
               .withOptionAbbreviation();
        //@formatter:on
        CommandLineInterface<Object> parser = builder.build();

        // Option name which is also an abbreviation of another option name
        Object command = parser.parse("commits", "--author", "test");

        assertNotNull(command);
        assertTrue(command instanceof CommandCommits);
        CommandCommits commit = (CommandCommits) command;
        assertEquals(commit.author, "test");
    }

    @Test
    public void abbreviatedOptions05() {
        //@formatter:off
        CliBuilder<Object> builder = CommandLineInterface.builder("git")
                                   .withCommand(CommandCommits.class);
        builder.withParser()
               .withOptionAbbreviation();
        //@formatter:on
        CommandLineInterface<Object> parser = builder.build();

        // Option name whose name is a super string of another option name
        Object command = parser.parse("commits", "--authors", "test");

        assertNotNull(command);
        assertTrue(command instanceof CommandCommits);
        CommandCommits commit = (CommandCommits) command;
        assertTrue(commit.authors.contains("test"));
    }
}
