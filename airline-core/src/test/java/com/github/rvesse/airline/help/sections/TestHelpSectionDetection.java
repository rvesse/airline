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
import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.rvesse.airline.Cli;
import com.github.rvesse.airline.help.sections.common.DiscussionSection;
import com.github.rvesse.airline.model.CommandMetadata;
import com.github.rvesse.airline.utils.predicates.parser.CommandFinder;

public class TestHelpSectionDetection {

    @Test
    public void help_section_cli_01() {
        Cli<Object> cli = new Cli<>(CliWithSections.class);
        CommandFinder finder = new CommandFinder("Args1");
        CommandMetadata cmd = CollectionUtils.find(cli.getMetadata().getDefaultGroupCommands(), finder);
        Assert.assertNotNull(cmd);
        
        Assert.assertEquals(cmd.getHelpSections().size(), 1);
        HelpSection section = cmd.getHelpSections().get(0);
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
        HelpSection section = cmd.getHelpSections().get(0);
        Assert.assertTrue(section instanceof DiscussionSection);
        DiscussionSection discussion = (DiscussionSection) section;
        String[] paragraphs = discussion.getContentBlock(0);
        Assert.assertEquals(paragraphs.length, 1);
    }
}
