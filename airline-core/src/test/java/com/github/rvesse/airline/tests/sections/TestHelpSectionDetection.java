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
package com.github.rvesse.airline.tests.sections;

import com.github.rvesse.airline.Cli;
import com.github.rvesse.airline.CommandLineInterface;
import com.github.rvesse.airline.builder.CliBuilder;
import com.github.rvesse.airline.help.sections.HelpFormat;
import com.github.rvesse.airline.help.sections.HelpSection;
import com.github.rvesse.airline.help.sections.common.*;
import com.github.rvesse.airline.model.CommandMetadata;
import com.github.rvesse.airline.tests.args.Args1;
import com.github.rvesse.airline.utils.comparators.HelpSectionComparator;
import com.github.rvesse.airline.utils.predicates.parser.CommandFinder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestHelpSectionDetection {

    public static class HelpSectionFinder implements Predicate<HelpSection> {
        private final String title;

        public HelpSectionFinder(String title) {
            this.title = title;
        }

        @Override
        public boolean evaluate(HelpSection section) {
            return StringUtils.equals(section.getTitle(), this.title);
        }
    }

    public CommandMetadata loadCommand() {
        final String commandName = "Args1";
        return loadCommand(commandName);
    }

    public CommandMetadata loadCommand(final String commandName) {
        CommandFinder finder = new CommandFinder(commandName);
        CommandLineInterface<Object> cli = new CommandLineInterface<>(CliWithSections.class);
        CommandMetadata cmd = IterableUtils.find(cli.getMetadata().getDefaultGroupCommands(), finder);
        Assert.assertNotNull(cmd, "Failed to find expected command " + commandName);
        return cmd;
    }

    @Test
    public void help_section_cli_01() {
        CommandMetadata cmd = loadCommand();

        HelpSection section = IterableUtils.find(cmd.getHelpSections(),
                new HelpSectionFinder(CommonSections.TITLE_DISCUSSION));
        Assert.assertTrue(section instanceof DiscussionSection);
        DiscussionSection discussion = (DiscussionSection) section;
        String[] paragraphs = discussion.getContentBlock(0);
        Assert.assertEquals(paragraphs.length, 2);
        Assert.assertEquals(paragraphs[0], "Foo");
        Assert.assertEquals(paragraphs[1], "Bar");
    }

    @Test
    public void help_section_cli_02() {
        CommandMetadata cmd = loadCommand("remove");

        HelpSection section = IterableUtils.find(cmd.getHelpSections(),
                new HelpSectionFinder(CommonSections.TITLE_DISCUSSION));
        Assert.assertTrue(section instanceof DiscussionSection);
        DiscussionSection discussion = (DiscussionSection) section;
        String[] paragraphs = discussion.getContentBlock(0);
        Assert.assertEquals(paragraphs.length, 1);
    }

    @Test
    public void help_section_cli_03() {
        CommandMetadata cmd = loadCommand("remove");

        HelpSection section = IterableUtils.find(cmd.getHelpSections(),
                new HelpSectionFinder(CommonSections.TITLE_SEE_ALSO));
        Assert.assertTrue(section instanceof ProseSection);
        ProseSection prose = (ProseSection) section;
        String[] paragraphs = prose.getContentBlock(0);
        Assert.assertEquals(paragraphs.length, 1);

        String seeAlso = paragraphs[0];
        Assert.assertTrue(seeAlso.contains("test help, "));
        Assert.assertTrue(seeAlso.contains("man, "));
        Assert.assertTrue(seeAlso.contains("grep"));
    }

    @Test
    public void help_section_cli_builder_01() {
        //@formatter:off
        CommandLineInterface<Object> cli = new CliBuilder<>("test")
                .withHelpSection(new DiscussionSection(new String[] { "A", "B" }))
                .withCommand(Args1.class)
                .build();
        //@formatter:on
        CommandFinder finder = new CommandFinder("Args1");
        CommandMetadata cmd = IterableUtils.find(cli.getMetadata().getDefaultGroupCommands(), finder);
        Assert.assertNotNull(cmd);

        Assert.assertEquals(cmd.getHelpSections().size(), 1);
        HelpSection section = IterableUtils.find(cmd.getHelpSections(), new HelpSectionFinder("Discussion"));
        Assert.assertTrue(section instanceof DiscussionSection);
        DiscussionSection discussion = (DiscussionSection) section;
        String[] paragraphs = discussion.getContentBlock(0);
        Assert.assertEquals(paragraphs.length, 2);
        Assert.assertEquals(paragraphs[0], "A");
        Assert.assertEquals(paragraphs[1], "B");
    }

    @Test
    public void help_section_cli_builder_02() {
        //@formatter:off
        CommandLineInterface<Object> cli = new CliBuilder<>("test")
                .withHelpSection(new DiscussionSection(new String[] { "A", "B" }))
                .withCommand(Args1HidesDiscussion.class)
                .build();
        //@formatter:on
        CommandFinder finder = new CommandFinder("Args1");
        CommandMetadata cmd = IterableUtils.find(cli.getMetadata().getDefaultGroupCommands(), finder);
        Assert.assertNotNull(cmd);
        Assert.assertEquals(cmd.getHelpSections().size(), 1);
        HelpSection section = IterableUtils.find(cmd.getHelpSections(), new HelpSectionFinder("Discussion"));
        Assert.assertTrue(section instanceof BasicSection);
        BasicSection basic = (BasicSection) section;
        Assert.assertEquals(basic.getTitle(), "Discussion");
        Assert.assertEquals(basic.getFormat(), HelpFormat.NONE_PRINTABLE);
    }

    @Test
    public void help_section_ordering_01() {
        List<HelpSection> sections = new ArrayList<>();
        sections.add(new DiscussionSection(new String[0]));
        sections.add(new ProseSection("test", CommonSections.ORDER_COPYRIGHT, new String[0]));
        
        Collections.sort(sections, new HelpSectionComparator());
        
        Assert.assertTrue(sections.get(0) instanceof DiscussionSection, "Expected Discussion first");
        Assert.assertTrue(sections.get(1) instanceof ProseSection, "Expected Copyright last");
    }
    
    @Test
    public void help_section_ordering_02() {
        List<HelpSection> sections = new ArrayList<>();
        sections.add(new ProseSection("test", CommonSections.ORDER_COPYRIGHT, new String[0]));
        sections.add(new DiscussionSection(new String[0]));
        sections.add(new ExitCodesSection(new int[0], new String[0]));
        
        Collections.sort(sections, new HelpSectionComparator());
        
        Assert.assertTrue(sections.get(0) instanceof DiscussionSection, "Expected Discussion first");
        Assert.assertTrue(sections.get(1) instanceof ExitCodesSection, "Expected Exit Codes second");
        Assert.assertTrue(sections.get(2) instanceof ProseSection, "Expected Copyright last");
    }
}
