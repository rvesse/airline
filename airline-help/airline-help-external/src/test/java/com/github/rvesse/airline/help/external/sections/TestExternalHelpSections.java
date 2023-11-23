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
package com.github.rvesse.airline.help.external.sections;

import com.github.rvesse.airline.builder.ParserBuilder;
import org.apache.commons.collections4.IterableUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.rvesse.airline.Cli;
import com.github.rvesse.airline.help.external.commands.ArgsInvalidResourceLocators;
import com.github.rvesse.airline.help.external.commands.ArgsInvalidResourcesExamplesTabular;
import com.github.rvesse.airline.help.external.commands.ArgsInvalidResourcesExamplesTextual;
import com.github.rvesse.airline.help.external.commands.ArgsInvalidResourcesExitCodes;
import com.github.rvesse.airline.help.external.commands.ArgsInvalidResourcesProse;
import com.github.rvesse.airline.help.sections.HelpSection;
import com.github.rvesse.airline.help.sections.common.ExamplesSection;
import com.github.rvesse.airline.help.sections.common.ExitCodesSection;
import com.github.rvesse.airline.help.sections.common.ProseSection;
import com.github.rvesse.airline.model.CommandMetadata;
import com.github.rvesse.airline.model.MetadataLoader;
import com.github.rvesse.airline.utils.predicates.parser.CommandFinder;

import static com.github.rvesse.airline.tests.sections.TestHelpSectionDetection.HelpSectionFinder;

public class TestExternalHelpSections {

    public CommandMetadata loadCommand() {
        final String commandName = "Args1";
        return loadCommand(commandName);
    }

    public CommandMetadata loadCommand(final String commandName) {
        CommandFinder finder = new CommandFinder(commandName);
        Cli<Object> cli = new Cli<>(CliWithExternalSections.class);
        CommandMetadata cmd = IterableUtils.find(cli.getMetadata().getDefaultGroupCommands(), finder);
        Assert.assertNotNull(cmd, "Failed to find expected command " + commandName);
        return cmd;
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Failed to locate resource.*no resource locators were configured")
    public void help_section_external_no_locators_01() {
        MetadataLoader.loadCommand(ArgsInvalidResourceLocators.class,
                                   new ParserBuilder<ArgsInvalidResourceLocators>().build());
    }

    @Test
    public void help_section_external_discussion_01() {
        CommandMetadata cmd = loadCommand();

        HelpSection section = IterableUtils.find(cmd.getHelpSections(), new HelpSectionFinder("Discussion"));
        Assert.assertNotNull(section);
        Assert.assertTrue(section instanceof ProseSection);

        ProseSection discussion = (ProseSection) section;
        String[] paragraphs = discussion.getContentBlock(0);
        Assert.assertEquals(paragraphs.length, 3);
        Assert.assertEquals(paragraphs[0], "This is the first paragraph");
        Assert.assertEquals(paragraphs[1], "A blank line starts a new paragraph");
        Assert.assertEquals(paragraphs[2], "Multiple blank lines are compacted");
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Failed to locate resource.*")
    public void help_section_external_discussion_02() {
        MetadataLoader.loadCommand(ArgsInvalidResourcesProse.class,
                                   new ParserBuilder<ArgsInvalidResourcesProse>().build());
    }

    @Test
    public void help_section_external_exit_codes_01() {
        CommandMetadata cmd = loadCommand();

        HelpSection section = IterableUtils.find(cmd.getHelpSections(), new HelpSectionFinder("Exit Codes"));
        Assert.assertNotNull(section);
        Assert.assertTrue(section instanceof ExitCodesSection);

        ExitCodesSection exitCodes = (ExitCodesSection) section;
        Assert.assertEquals(exitCodes.numContentBlocks(), 2);
        String[] codes = exitCodes.getContentBlock(0);
        String[] descriptions = exitCodes.getContentBlock(1);

        Assert.assertEquals(codes[0], "0");
        Assert.assertEquals(codes[1], "1");
        Assert.assertEquals(codes[2], "2");
        Assert.assertEquals(codes[3], "127");

        Assert.assertEquals(descriptions[0], "Success");
        Assert.assertEquals(descriptions[3], "Catastrophic Failure");
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Failed to locate resource.*")
    public void help_section_external_exit_codes_02() {
        MetadataLoader.loadCommand(ArgsInvalidResourcesExitCodes.class,
                                   new ParserBuilder<ArgsInvalidResourcesExitCodes>().build());
    }

    @Test
    public void help_section_external_examples_text_01() {
        CommandMetadata cmd = loadCommand("args-examples-textual");

        HelpSection section = IterableUtils.find(cmd.getHelpSections(), new HelpSectionFinder("Examples"));
        Assert.assertNotNull(section);
        verifyExamples(cmd, section);
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Failed to locate resource.*")
    public void help_section_external_examples_text_02() {
        MetadataLoader.loadCommand(ArgsInvalidResourcesExamplesTextual.class,
                                   new ParserBuilder<ArgsInvalidResourcesExamplesTextual>().build());
    }

    @Test
    public void help_section_external_examples_tabular_01() {
        CommandMetadata cmd = loadCommand("args-examples-tabular");

        HelpSection section = IterableUtils.find(cmd.getHelpSections(), new HelpSectionFinder("Examples"));
        Assert.assertNotNull(section);
        verifyExamples(cmd, section);
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Failed to locate resource.*")
    public void help_section_external_examples_tabular_02() {
        MetadataLoader.loadCommand(ArgsInvalidResourcesExamplesTabular.class,
                                   new ParserBuilder<ArgsInvalidResourcesExamplesTabular>().build());
    }

    public void verifyExamples(CommandMetadata cmd, HelpSection section) {
        Assert.assertTrue(section instanceof ExamplesSection);

        ExamplesSection examples = (ExamplesSection) section;
        Assert.assertEquals(examples.numContentBlocks(), 2);

        String[] cmds = examples.getContentBlock(0);
        String[] descrips = examples.getContentBlock(1);

        Assert.assertEquals(cmds[0], cmd.getName() + " -v 7");
        Assert.assertEquals(cmds[1], cmd.getName() + " -debug");
        Assert.assertEquals(descrips[0], "Runs with high verbosity");
        Assert.assertEquals(descrips[1], "Runs in debug mode");
    }
}
