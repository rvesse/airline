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
package com.github.rvesse.airline.command;

import java.util.Arrays;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.rvesse.airline.Cli;
import com.github.rvesse.airline.CommandLineInterface;
import com.github.rvesse.airline.model.CommandGroupMetadata;
import com.github.rvesse.airline.model.GlobalMetadata;
import com.github.rvesse.airline.model.OptionMetadata;
import com.github.rvesse.airline.parser.errors.ParseCommandUnrecognizedException;
import com.github.rvesse.airline.utils.AirlineUtils;

public class TestGroupAnnotations {
    /*
     * Tests for Groups -> Group annotations
     */
    @Test
    public void groupIsCreatedFromGroupsAnnotation() {
        CommandLineInterface<C> parser = Cli.builder("junk").withCommand(CommandWithGroupsAnnotation.class).build();
        
        GlobalMetadata<?> global = parser.getMetadata();
        Assert.assertEquals(global.getCommandGroups().size(), 1);

        Object command = parser.parse("groupInsideOfGroups", "commandWithGroupsAnno", "-i", "A.java");
        Assert.assertNotNull(command, "command is null");
        Assert.assertTrue(command instanceof CommandWithGroupsAnnotation);
        CommandWithGroupsAnnotation add = (CommandWithGroupsAnnotation) command;
        Assert.assertEquals(add.interactive.booleanValue(), true);
        Assert.assertEquals(add.patterns, Arrays.asList("A.java"));
    }
    
    @Test
    public void subGroupIsCreatedFromGroupsAnnotation() {
        CommandLineInterface<C> parser = Cli.builder("junk").withCommand(CommandWithSubGroupsAnnotation.class).build();
        
        GlobalMetadata<?> global = parser.getMetadata();
        Assert.assertEquals(global.getCommandGroups().size(), 1);
        CommandGroupMetadata parentGroup = global.getCommandGroups().get(0);
        Assert.assertEquals(parentGroup.getSubGroups().size(), 1);

        Object command = parser.parse("groupInsideOfGroups", "subGroup", "commandWithSubGroupsAnno", "-i", "A.java");
        Assert.assertNotNull(command, "command is null");
        Assert.assertTrue(command instanceof CommandWithSubGroupsAnnotation);
        CommandWithSubGroupsAnnotation add = (CommandWithSubGroupsAnnotation) command;
        Assert.assertEquals(add.interactive.booleanValue(), true);
        Assert.assertEquals(add.patterns, Arrays.asList("A.java"));
    }

    @Test
    public void extraCommandsAreAddedFromGroupsAnnotation() {
        CommandLineInterface<C> parser = Cli.builder("junk").withCommand(CommandWithGroupsAnnotation.class).build();

        Object command = parser.parse("groupInsideOfGroups", "add", "-i", "A.java");
        Assert.assertNotNull(command, "command is null");
        Assert.assertTrue(command instanceof CommandAdd);
        CommandAdd add = (CommandAdd) command;
        Assert.assertEquals(add.interactive.booleanValue(), true);
        Assert.assertEquals(add.patterns, Arrays.asList("A.java"));
    }
    
    @Test
    public void extraCommandsAreAddedFromSubGroupsAnnotation() {
        CommandLineInterface<C> parser = Cli.builder("junk").withCommand(CommandWithSubGroupsAnnotation.class).build();

        Object command = parser.parse("groupInsideOfGroups", "subGroup", "add", "-i", "A.java");
        Assert.assertNotNull(command, "command is null");
        Assert.assertTrue(command instanceof CommandAdd);
        CommandAdd add = (CommandAdd) command;
        Assert.assertEquals(add.interactive.booleanValue(), true);
        Assert.assertEquals(add.patterns, Arrays.asList("A.java"));
    }

    @Test(expectedExceptions = ParseCommandUnrecognizedException.class)
    public void commandRemovedFromDefaultGroupWithGroupsAnnotation() {
        CommandLineInterface<C> parser = Cli.builder("junk").withCommand(CommandWithGroupsAnnotation.class).build();

        parser.parse("commandWithGroupsAnno", "-i", "A.java");
    }
    
    @Test(expectedExceptions = ParseCommandUnrecognizedException.class)
    public void commandRemovedFromDefaultGroupWithSubGroupsAnnotation() {
        CommandLineInterface<C> parser = Cli.builder("junk").withCommand(CommandWithSubGroupsAnnotation.class).build();

        parser.parse("commandWithSubGroupsAnno", "-i", "A.java");
    }

    /*
     * Tests for Groups -> Group annotations
     */
    @Test
    public void groupOptionsAreAddedFromGroupsAnnotation() {
        CommandLineInterface<C> parser = Cli.builder("junk").withCommand(CommandWithGroupsAnnotation.class).build();
        Assert.assertEquals(parser.getMetadata().getCommandGroups().size(), 1);
        CommandGroupMetadata group = parser.getMetadata().getCommandGroups().get(0);
        Assert.assertEquals(group.getOptions().size(), 1);
        OptionMetadata option = group.getOptions().get(0);
        Assert.assertEquals("-v", AirlineUtils.first(option.getOptions()));
    }
    
    @Test
    public void groupOptionsAreAddedFromSubGroupsAnnotation() {
        CommandLineInterface<C> parser = Cli.builder("junk").withCommand(CommandWithSubGroupsAnnotation.class).build();
        Assert.assertEquals(parser.getMetadata().getCommandGroups().size(), 1);
        CommandGroupMetadata group = parser.getMetadata().getCommandGroups().get(0);
        
        // Check added to parent group
        Assert.assertEquals(group.getOptions().size(), 1);
        OptionMetadata option = group.getOptions().get(0);
        Assert.assertEquals("-v", AirlineUtils.first(option.getOptions()));
        
        // Check added to sub-group
        Assert.assertEquals(group.getSubGroups().size(), 1);
        group = group.getSubGroups().get(0);
        Assert.assertEquals(group.getOptions().size(), 1);
        option = group.getOptions().get(0);
        Assert.assertEquals("-v", AirlineUtils.first(option.getOptions()));
    }

    @Test
    public void defaultCommandIsAddedFromGroupsAnnotation() {
        CommandLineInterface<C> parser = Cli.builder("junk").withCommand(CommandWithGroupsAnnotation.class).build();

        Object command = parser.parse("groupInsideOfGroups", "-i", "A.java");
        Assert.assertNotNull(command, "command is null");
        Assert.assertTrue(command instanceof CommandWithGroupsAnnotation);
        CommandWithGroupsAnnotation add = (CommandWithGroupsAnnotation) command;
        Assert.assertEquals(add.interactive.booleanValue(), true);
        Assert.assertEquals(add.patterns, Arrays.asList("A.java"));
    }
    
    @Test
    public void defaultCommandIsAddedFromSubGroupsAnnotation() {
        CommandLineInterface<C> parser = Cli.builder("junk").withCommand(CommandWithSubGroupsAnnotation.class).build();

        Object command = parser.parse("groupInsideOfGroups", "subGroup", "-i", "A.java");
        Assert.assertNotNull(command, "command is null");
        Assert.assertTrue(command instanceof CommandWithSubGroupsAnnotation);
        CommandWithSubGroupsAnnotation add = (CommandWithSubGroupsAnnotation) command;
        Assert.assertEquals(add.interactive.booleanValue(), true);
        Assert.assertEquals(add.patterns, Arrays.asList("A.java"));
    }

    /*
     * Tests for Group annotation
     */
    @Test
    public void groupIsCreatedFromGroupAnnotation() {
        CommandLineInterface<C> parser = Cli.builder("junk").withCommand(CommandWithGroupAnnotation.class).build();

        Object command = parser.parse("singleGroup", "commandWithGroup", "-i", "A.java");
        Assert.assertNotNull(command, "command is null");
        Assert.assertTrue(command instanceof CommandWithGroupAnnotation);
        AbstractGroupAnnotationCommand add = (AbstractGroupAnnotationCommand) command;
        Assert.assertEquals(add.interactive.booleanValue(), true);
        Assert.assertEquals(add.patterns, Arrays.asList("A.java"));
    }
    
    @Test
    public void groupIsCreatedFromSubGroupAnnotation() {
        CommandLineInterface<C> parser = Cli.builder("junk").withCommand(CommandWithSubGroupAnnotation.class).build();

        Object command = parser.parse("singleGroup", "subGroup", "commandWithGroup", "-i", "A.java");
        Assert.assertNotNull(command, "command is null");
        Assert.assertTrue(command instanceof CommandWithSubGroupAnnotation);
        CommandWithSubGroupAnnotation add = (CommandWithSubGroupAnnotation) command;
        Assert.assertEquals(add.interactive.booleanValue(), true);
        Assert.assertEquals(add.patterns, Arrays.asList("A.java"));
    }

    @Test
    public void extraCommandsAreAddedFromGroupAnnotation() {
        CommandLineInterface<C> parser = Cli.builder("junk").withCommand(CommandWithGroupAnnotation.class).build();

        Object command = parser.parse("singleGroup", "add", "-i", "A.java");
        Assert.assertNotNull(command, "command is null");
        Assert.assertTrue(command instanceof CommandAdd);
        CommandAdd add = (CommandAdd) command;
        Assert.assertEquals(add.interactive.booleanValue(), true);
        Assert.assertEquals(add.patterns, Arrays.asList("A.java"));
    }
    
    @Test
    public void extraCommandsAreAddedFromSubGroupAnnotation() {
        CommandLineInterface<C> parser = Cli.builder("junk").withCommand(CommandWithSubGroupAnnotation.class).build();

        Object command = parser.parse("singleGroup", "subGroup", "add", "-i", "A.java");
        Assert.assertNotNull(command, "command is null");
        Assert.assertTrue(command instanceof CommandAdd);
        CommandAdd add = (CommandAdd) command;
        Assert.assertEquals(add.interactive.booleanValue(), true);
        Assert.assertEquals(add.patterns, Arrays.asList("A.java"));
    }

    @Test(expectedExceptions = ParseCommandUnrecognizedException.class)
    public void commandRemovedFromDefaultGroupWithGroupAnnotation() {
        CommandLineInterface<C> parser = Cli.builder("junk").withCommand(CommandWithGroupAnnotation.class).build();

        parser.parse("commandWithGroup", "-i", "A.java");
    }
    
    @Test(expectedExceptions = ParseCommandUnrecognizedException.class)
    public void commandRemovedFromDefaultGroupWithSubGroupAnnotation() {
        CommandLineInterface<C> parser = Cli.builder("junk").withCommand(CommandWithSubGroupAnnotation.class).build();

        parser.parse("commandWithGroup", "-i", "A.java");
    }

    @Test
    public void defaultCommandIsAddedFromGroupAnnotation() {
        CommandLineInterface<C> parser = Cli.builder("junk").withCommand(CommandWithGroupAnnotation.class).build();

        Object command = parser.parse("singleGroup", "-i", "A.java");
        Assert.assertNotNull(command, "command is null");
        Assert.assertTrue(command instanceof CommandWithGroupAnnotation);
        AbstractGroupAnnotationCommand add = (AbstractGroupAnnotationCommand) command;
        Assert.assertEquals(add.interactive.booleanValue(), true);
        Assert.assertEquals(add.patterns, Arrays.asList("A.java"));
    }
    
    @Test
    public void defaultCommandIsAddedFromSubGroupAnnotation() {
        CommandLineInterface<C> parser = Cli.builder("junk").withCommand(CommandWithSubGroupAnnotation.class).build();

        Object command = parser.parse("singleGroup", "subGroup", "-i", "A.java");
        Assert.assertNotNull(command, "command is null");
        Assert.assertTrue(command instanceof CommandWithSubGroupAnnotation);
        CommandWithSubGroupAnnotation add = (CommandWithSubGroupAnnotation) command;
        Assert.assertEquals(add.interactive.booleanValue(), true);
        Assert.assertEquals(add.patterns, Arrays.asList("A.java"));
    }

    /*
     * Tests for groupNames in Command annotation
     */
    @SuppressWarnings("unchecked")
    @Test
    public void addedToGroupFromGroupAnnotation() {
        CommandLineInterface<C> parser = Cli.builder("junk").withCommands(CommandWithGroupAnnotation.class, CommandWithGroupNames.class)
                .build();

        Object command = parser.parse("singleGroup", "commandWithGroupNames", "-i", "A.java");
        Assert.assertNotNull(command, "command is null");
        Assert.assertTrue(command instanceof CommandWithGroupNames);
        CommandWithGroupNames add = (CommandWithGroupNames) command;
        Assert.assertEquals(add.interactive.booleanValue(), true);
        Assert.assertEquals(add.patterns, Arrays.asList("A.java"));
        
        // Should also be placed in singletonGroup
        command = parser.parse("singletonGroup", "commandWithGroupNames", "-i", "A.java");
        Assert.assertNotNull(command, "command is null");
        Assert.assertTrue(command instanceof CommandWithGroupNames);
        add = (CommandWithGroupNames) command;
        Assert.assertEquals(add.interactive.booleanValue(), true);
        Assert.assertEquals(add.patterns, Arrays.asList("A.java"));
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void addedToGroupFromSubGroupAnnotation() {
        CommandLineInterface<C> parser = Cli.builder("junk").withCommands(CommandWithGroupAnnotation.class, CommandWithSubGroupNames.class)
                .build();

        Object command = parser.parse("singleGroup", "commandWithGroupNames", "-i", "A.java");
        Assert.assertNotNull(command, "command is null");
        Assert.assertTrue(command instanceof CommandWithSubGroupNames);
        CommandWithSubGroupNames add = (CommandWithSubGroupNames) command;
        Assert.assertEquals(add.interactive.booleanValue(), true);
        Assert.assertEquals(add.patterns, Arrays.asList("A.java"));
        
        // Should also be placed in parent child group
        command = parser.parse("parent", "child", "commandWithGroupNames", "-i", "A.java");
        Assert.assertNotNull(command, "command is null");
        Assert.assertTrue(command instanceof CommandWithSubGroupNames);
        add = (CommandWithSubGroupNames) command;
        Assert.assertEquals(add.interactive.booleanValue(), true);
        Assert.assertEquals(add.patterns, Arrays.asList("A.java"));
    }

    @Test(expectedExceptions = ParseCommandUnrecognizedException.class)
    public void commandRemovedFromDefaultGroupWithGroupNames() {
        CommandLineInterface<C> parser = Cli.builder("junk").withCommand(CommandWithGroupNames.class).build();

        parser.parse("commandWithGroupNames", "-i", "A.java");
    }
    
    @Test(expectedExceptions = ParseCommandUnrecognizedException.class)
    public void commandRemovedFromDefaultGroupWithSubGroupNames() {
        CommandLineInterface<C> parser = Cli.builder("junk").withCommand(CommandWithSubGroupNames.class).build();

        parser.parse("commandWithSubGroupNames", "-i", "A.java");
    }
}
