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

import org.apache.commons.collections4.IterableUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.rvesse.airline.Cli;
import com.github.rvesse.airline.help.sections.common.ProseSection;
import com.github.rvesse.airline.model.CommandMetadata;
import com.github.rvesse.airline.utils.predicates.parser.CommandFinder;
import static com.github.rvesse.airline.help.sections.TestHelpSectionDetection.HelpSectionFinder;

public class TestExternalHelpSections {

    @Test
    public void help_section_external_cli_01() {
        Cli<Object> cli = new Cli<>(CliWithExternalSections.class);
        CommandFinder finder = new CommandFinder("Args1");
        CommandMetadata cmd = IterableUtils.find(cli.getMetadata().getDefaultGroupCommands(), finder);
        Assert.assertNotNull(cmd);

        Assert.assertEquals(cmd.getHelpSections().size(), 1);
        HelpSection section = IterableUtils.find(cmd.getHelpSections(), new HelpSectionFinder("Discussion"));
        Assert.assertTrue(section instanceof ProseSection);
        ProseSection discussion = (ProseSection) section;
        String[] paragraphs = discussion.getContentBlock(0);
        Assert.assertEquals(paragraphs.length, 3);
        Assert.assertEquals(paragraphs[0], "This is the first paragraph");
        Assert.assertEquals(paragraphs[1], "A blank line starts a new paragraph");
        Assert.assertEquals(paragraphs[2], "Multiple blank lines are compacted");
    }
}
