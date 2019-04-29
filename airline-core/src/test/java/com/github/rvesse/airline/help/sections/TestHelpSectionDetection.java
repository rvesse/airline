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
package com.github.rvesse.airline.help.sections;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.rvesse.airline.Cli;
import com.github.rvesse.airline.args.Args1;
import com.github.rvesse.airline.builder.CliBuilder;
import com.github.rvesse.airline.help.sections.common.BasicSection;
import com.github.rvesse.airline.help.sections.common.DiscussionSection;
import com.github.rvesse.airline.model.CommandMetadata;
import com.github.rvesse.airline.utils.predicates.parser.CommandFinder;

public class TestHelpSectionDetection {
    
    private static class HelpSectionFinder implements Predicate<HelpSection> {
        private final String title;
        
        public HelpSectionFinder(String title) {
            this.title = title;
        }

        @Override
        public boolean evaluate(HelpSection section) {
            return StringUtils.equals(section.getTitle(), this.title);
        }
    }

    @Test
    public void help_section_cli_01() {
        Cli<Object> cli = new Cli<>(CliWithSections.class);
        CommandFinder finder = new CommandFinder("Args1");
        CommandMetadata cmd = CollectionUtils.find(cli.getMetadata().getDefaultGroupCommands(), finder);
        Assert.assertNotNull(cmd);
        
        Assert.assertEquals(cmd.getHelpSections().size(), 1);
        HelpSection section = CollectionUtils.find(cmd.getHelpSections(), new HelpSectionFinder("Discussion"));
        Assert.assertTrue(section instanceof DiscussionSection);
        DiscussionSection discussion = (DiscussionSection) section;
        String[] paragraphs = discussion.getContentBlock(0);
        Assert.assertEquals(paragraphs.length, 2);
        Assert.assertEquals(paragraphs[0], "Foo");
        Assert.assertEquals(paragraphs[1], "Bar");
    }
    
    @Test
    public void help_section_cli_02() {
        Cli<Object> cli = new Cli<>(CliWithSections.class);
        CommandFinder finder = new CommandFinder("remove");
        CommandMetadata cmd = CollectionUtils.find(cli.getMetadata().getDefaultGroupCommands(), finder);
        Assert.assertNotNull(cmd);
        
        Assert.assertEquals(cmd.getHelpSections().size(), 2);
        HelpSection section = CollectionUtils.find(cmd.getHelpSections(), new HelpSectionFinder("Discussion"));
        Assert.assertTrue(section instanceof DiscussionSection);
        DiscussionSection discussion = (DiscussionSection) section;
        String[] paragraphs = discussion.getContentBlock(0);
        Assert.assertEquals(paragraphs.length, 1);
    }
    
    @Test
    public void help_section_cli_builder_01() {
        //@formatter:off
        Cli<Object> cli = new CliBuilder<>("test")
                .withHelpSection(new DiscussionSection(new String[] { "A", "B" }))
                .withCommand(Args1.class)
                .build();
        //@formatter:on
        CommandFinder finder = new CommandFinder("Args1");
        CommandMetadata cmd = CollectionUtils.find(cli.getMetadata().getDefaultGroupCommands(), finder);
        Assert.assertNotNull(cmd);
        
        Assert.assertEquals(cmd.getHelpSections().size(), 1);
        HelpSection section = CollectionUtils.find(cmd.getHelpSections(), new HelpSectionFinder("Discussion"));
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
        Cli<Object> cli = new CliBuilder<>("test")
                .withHelpSection(new DiscussionSection(new String[] { "A", "B" }))
                .withCommand(Args1HidesDiscussion.class)
                .build();
        //@formatter:on
        CommandFinder finder = new CommandFinder("Args1");
        CommandMetadata cmd = CollectionUtils.find(cli.getMetadata().getDefaultGroupCommands(), finder);
        Assert.assertNotNull(cmd);
        Assert.assertEquals(cmd.getHelpSections().size(), 1);
        HelpSection section = CollectionUtils.find(cmd.getHelpSections(), new HelpSectionFinder("Discussion"));
        Assert.assertTrue(section instanceof BasicSection);
        BasicSection basic = (BasicSection) section;
        Assert.assertEquals(basic.getTitle(), "Discussion");
        Assert.assertEquals(basic.getFormat(), HelpFormat.NONE_PRINTABLE);
    }
}
