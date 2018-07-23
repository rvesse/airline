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
import java.util.Arrays;
import java.util.Collections;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.rvesse.airline.Git.Add;
import com.github.rvesse.airline.Git.RemoteShow;
import com.github.rvesse.airline.annotations.Group;
import com.github.rvesse.airline.builder.CliBuilder;
import com.github.rvesse.airline.builder.GroupBuilder;
import com.github.rvesse.airline.help.Help;
import com.github.rvesse.airline.model.CommandGroupMetadata;
import com.github.rvesse.airline.model.GlobalMetadata;
import com.github.rvesse.airline.parser.errors.ParseCommandMissingException;
import com.github.rvesse.airline.parser.errors.ParseCommandUnrecognizedException;
import com.github.rvesse.airline.utils.AirlineUtils;
import com.github.rvesse.airline.utils.predicates.parser.GroupFinder;

import bsh.Remote;


public class TestSubGroups {

    @Test
    public void sub_groups_01() {
        //@formatter:off
        CliBuilder<Object> builder
            = Cli.<Object>builder("test");
        builder.withGroup("foo")
               .withSubGroup("bar")
               .withDefaultCommand(Help.class);
        //@formatter:on
        
        CommandLineInterface<C> cli = builder.build();
        GlobalMetadata<Object> global = cli.getMetadata();
        Assert.assertEquals(global.getDefaultGroupCommands().size(), 0);
        Assert.assertEquals(global.getCommandGroups().size(), 1);
        
        CommandGroupMetadata parentGroup = global.getCommandGroups().get(0);
        Assert.assertEquals(parentGroup.getName(), "foo");
        Assert.assertEquals(parentGroup.getCommands().size(), 0);
        Assert.assertEquals(parentGroup.getSubGroups().size(), 1);
        
        CommandGroupMetadata subGroup = parentGroup.getSubGroups().get(0);
        Assert.assertEquals(parentGroup, subGroup.getParent());
        Assert.assertEquals(subGroup.getName(), "bar");
        Assert.assertEquals(subGroup.getDefaultCommand().getType(), Help.class);
        
        Object cmd = cli.parse("foo", "bar");
        Assert.assertTrue(cmd instanceof Help);
    }
    
    @Test
    public void sub_groups_02() {
        //@formatter:off
        CliBuilder<Object> builder
            = Cli.<Object>builder("test");
        GroupBuilder<Object> fooBuilder = builder.withGroup("foo");
        fooBuilder.withSubGroup("bar")
                  .withDefaultCommand(Help.class);
        fooBuilder.withSubGroup("baz");
        //@formatter:on
        
        CommandLineInterface<C> cli = builder.build();
        GlobalMetadata<Object> global = cli.getMetadata();
        Assert.assertEquals(global.getDefaultGroupCommands().size(), 0);
        Assert.assertEquals(global.getCommandGroups().size(), 1);
        
        CommandGroupMetadata parentGroup = global.getCommandGroups().get(0);
        Assert.assertNull(parentGroup.getParent());
        Assert.assertEquals(parentGroup.getName(), "foo");
        Assert.assertEquals(parentGroup.getCommands().size(), 0);
        Assert.assertEquals(parentGroup.getSubGroups().size(), 2);
        
        CommandGroupMetadata subGroup = CollectionUtils.find(parentGroup.getSubGroups(), new GroupFinder("bar"));
        Assert.assertNotNull(subGroup);
        Assert.assertEquals(parentGroup, subGroup.getParent());
        Assert.assertEquals(subGroup.getName(), "bar");
        Assert.assertEquals(subGroup.getCommands().size(), 1);
        Assert.assertEquals(subGroup.getDefaultCommand().getType(), Help.class);
        
        subGroup = CollectionUtils.find(parentGroup.getSubGroups(), new GroupFinder("baz"));
        Assert.assertNotNull(subGroup);
        Assert.assertEquals(parentGroup, subGroup.getParent());
        Assert.assertEquals(subGroup.getName(), "baz");
        Assert.assertEquals(subGroup.getCommands().size(), 0);
        Assert.assertNull(subGroup.getDefaultCommand());
        
        Object cmd = cli.parse("foo", "bar");
        Assert.assertTrue(cmd instanceof Help);
    }
    
    @Test(expectedExceptions = ParseCommandMissingException.class)
    public void sub_groups_03() {
        //@formatter:off
        CliBuilder<Object> builder
            = Cli.<Object>builder("test");
        GroupBuilder<Object> fooBuilder = builder.withGroup("foo");
        fooBuilder.withSubGroup("bar")
                  .withDefaultCommand(Help.class);
        fooBuilder.withSubGroup("baz");
        //@formatter:on
        
        builder.build().parse("foo", "baz");
    }
    
    @Test
    public void sub_groups_help_01() throws IOException {
        //@formatter:off
        CliBuilder<Object> builder
            = Cli.<Object>builder("test");
        builder.withGroup("foo")
               .withSubGroup("bar")
               .withDefaultCommand(Help.class);
        //@formatter:on
        
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Help.help(builder.build().getMetadata(), Collections.<String>emptyList(), false, output);
        String actual = new String(output.toByteArray());
        
        //@formatter:off
        String expected = StringUtils.join(new String[] {
            "usage: test <command> [ <args> ]",
            "",
            "Commands are:",
            "    foo",
            "",
            "See 'test help <command>' for more information on a specific command.",
            ""
        }, '\n');
        //@formatter:on
        
        Assert.assertEquals(actual, expected);
    }
    
    @Test
    public void sub_groups_help_02() throws IOException {
        //@formatter:off
        CliBuilder<Object> builder
            = Cli.<Object>builder("test");
        builder.withGroup("foo")
               .withSubGroup("bar")
               .withDefaultCommand(Help.class);
        //@formatter:on
        
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Help.help(builder.build().getMetadata(), Arrays.asList(new String[] { "foo", "bar" }), false, output);
        String actual = new String(output.toByteArray());
        
        //@formatter:off
        String expected = StringUtils.join(new String[] {
            "NAME",
            "        test foo bar -",
            "",
            "SYNOPSIS",
            "        test foo bar { help* } [--] <cmd-args>",
            "",
            "        Where command-specific arguments <cmd-args> are:",
            "            help: [ <command>... ]",
            "",
            "        Where * indicates the default command(s)",
            "        See 'test help foo bar <command>' for more information on a specific command.",
            ""
        }, '\n');
        //@formatter:on
        
        Assert.assertEquals(actual, expected);
    }
    
    @Test
    public void sub_groups_help_03() throws IOException {
        //@formatter:off
        CliBuilder<Object> builder
            = Cli.<Object>builder("test");
        builder.withGroup("foo")
               .withSubGroup("bar")
               .withDefaultCommand(Help.class);
        //@formatter:on
        
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Help.help(builder.build().getMetadata(), Arrays.asList(new String[] { "foo" }), false, output);
        String actual = new String(output.toByteArray());
        
        //@formatter:off
        String expected = StringUtils.join(new String[] {
            "NAME",
            "        test foo -",
            "",
            "SYNOPSIS",
            "        test foo { bar <sub-command> } [--]",
            "",
            "        Where command groups contain the following sub-groups and commands:",
            "            bar: help*",
            "",
            "        Where * indicates the default command(s)",
            "        See 'test help foo <command>' for more information on a specific command.",
            ""
        }, '\n');
        //@formatter:on
        
        Assert.assertEquals(actual, expected);
    }
    
    @com.github.rvesse.airline.annotations.Cli(name = "test", groups = { @Group(name = "foo bar", defaultCommand = Help.class, commands = { Help.class }) })
    private static class SubGroupsCli01 {

    }
    
    @Test
    public void sub_groups_cli_annotation_01() {
        CommandLineInterface<C> cli = new Cli<Object>(SubGroupsCli01.class);
        GlobalMetadata<Object> global = cli.getMetadata();
        Assert.assertEquals(global.getDefaultGroupCommands().size(), 0);
        Assert.assertEquals(global.getCommandGroups().size(), 1);
        
        CommandGroupMetadata parentGroup = global.getCommandGroups().get(0);
        Assert.assertEquals(parentGroup.getName(), "foo");
        Assert.assertEquals(parentGroup.getCommands().size(), 0);
        Assert.assertEquals(parentGroup.getSubGroups().size(), 1);
        
        CommandGroupMetadata subGroup = parentGroup.getSubGroups().get(0);
        Assert.assertEquals(parentGroup, subGroup.getParent());
        Assert.assertEquals(subGroup.getName(), "bar");
        Assert.assertEquals(subGroup.getDefaultCommand().getType(), Help.class);
        
        Object cmd = cli.parse("foo", "bar");
        Assert.assertTrue(cmd instanceof Help);
    }
    
    @com.github.rvesse.airline.annotations.Cli(name = "test", groups = { @Group(name = "foo bar", defaultCommand = Help.class, commands = { Help.class }), @Group(name = "foo baz") })
    private static class SubGroupsCli02 {

    }
    
    @Test
    public void sub_groups_cli_annotation_02() {
        CommandLineInterface<C> cli = new Cli<Object>(SubGroupsCli02.class);
        GlobalMetadata<Object> global = cli.getMetadata();
        Assert.assertEquals(global.getDefaultGroupCommands().size(), 0);
        Assert.assertEquals(global.getCommandGroups().size(), 1);
        
        CommandGroupMetadata parentGroup = global.getCommandGroups().get(0);
        Assert.assertNull(parentGroup.getParent());
        Assert.assertEquals(parentGroup.getName(), "foo");
        Assert.assertEquals(parentGroup.getCommands().size(), 0);
        Assert.assertEquals(parentGroup.getSubGroups().size(), 2);
        
        CommandGroupMetadata subGroup = CollectionUtils.find(parentGroup.getSubGroups(), new GroupFinder("bar"));
        Assert.assertNotNull(subGroup);
        Assert.assertEquals(parentGroup, subGroup.getParent());
        Assert.assertEquals(subGroup.getName(), "bar");
        Assert.assertEquals(subGroup.getCommands().size(), 1);
        Assert.assertEquals(subGroup.getDefaultCommand().getType(), Help.class);
        
        subGroup = CollectionUtils.find(parentGroup.getSubGroups(), new GroupFinder("baz"));
        Assert.assertNotNull(subGroup);
        Assert.assertEquals(parentGroup, subGroup.getParent());
        Assert.assertEquals(subGroup.getName(), "baz");
        Assert.assertEquals(subGroup.getCommands().size(), 0);
        Assert.assertNull(subGroup.getDefaultCommand());
        
        Object cmd = cli.parse("foo", "bar");
        Assert.assertTrue(cmd instanceof Help);
    }
    
    @Test(expectedExceptions = ParseCommandMissingException.class)
    public void sub_groups_cli_annotation_03() {
        CommandLineInterface<C> cli = new Cli<Object>(SubGroupsCli02.class);
        cli.parse("foo", "baz");
    }
    
    @Test(timeOut = 3000)
    public void sub_groups_mixed_01() {
        //@formatter:off
        CliBuilder<Object> builder
            = Cli.<Object>builder("test");
        builder.withGroup("foo")
               .withCommand(Add.class)
               .withSubGroup("bar")
               .withDefaultCommand(Help.class);
        //@formatter:on
        
        CommandLineInterface<C> cli = builder.build();
        GlobalMetadata<Object> global = cli.getMetadata();
        Assert.assertEquals(global.getDefaultGroupCommands().size(), 0);
        Assert.assertEquals(global.getCommandGroups().size(), 1);
        
        CommandGroupMetadata parentGroup = global.getCommandGroups().get(0);
        Assert.assertEquals(parentGroup.getName(), "foo");
        Assert.assertEquals(parentGroup.getCommands().size(), 1);
        Assert.assertEquals(parentGroup.getSubGroups().size(), 1);
        
        CommandGroupMetadata subGroup = parentGroup.getSubGroups().get(0);
        Assert.assertEquals(parentGroup, subGroup.getParent());
        Assert.assertEquals(subGroup.getName(), "bar");
        Assert.assertEquals(subGroup.getDefaultCommand().getType(), Help.class);
        
        Object cmd = cli.parse("foo", "add");
        Assert.assertTrue(cmd instanceof Add);
    }
    
    @Test(timeOut = 3000)
    public void sub_groups_mixed_02() {
        //@formatter:off
        CliBuilder<Object> builder
            = Cli.<Object>builder("test");
        builder.withGroup("foo")
               .withCommand(Add.class)
               .withSubGroup("bar")
               .withDefaultCommand(Help.class);
        //@formatter:on
        
        CommandLineInterface<C> cli = builder.build();
        GlobalMetadata<Object> global = cli.getMetadata();
        Assert.assertEquals(global.getDefaultGroupCommands().size(), 0);
        Assert.assertEquals(global.getCommandGroups().size(), 1);
        
        CommandGroupMetadata parentGroup = global.getCommandGroups().get(0);
        Assert.assertEquals(parentGroup.getName(), "foo");
        Assert.assertEquals(parentGroup.getCommands().size(), 1);
        Assert.assertEquals(parentGroup.getSubGroups().size(), 1);
        
        CommandGroupMetadata subGroup = parentGroup.getSubGroups().get(0);
        Assert.assertEquals(parentGroup, subGroup.getParent());
        Assert.assertEquals(subGroup.getName(), "bar");
        Assert.assertEquals(subGroup.getDefaultCommand().getType(), Help.class);
        
        Object cmd = cli.parse("foo", "bar");
        Assert.assertTrue(cmd instanceof Help);
    }
    
    @Test(timeOut = 3000, expectedExceptions = ParseCommandUnrecognizedException.class)
    public void sub_groups_mixed_03() {
        //@formatter:off
        CliBuilder<Object> builder
            = Cli.<Object>builder("test");
        builder.withGroup("foo")
               .withCommand(Add.class)
               .withSubGroup("bar")
               .withDefaultCommand(Help.class);
        //@formatter:on
        
        CommandLineInterface<C> cli = builder.build();
        GlobalMetadata<Object> global = cli.getMetadata();
        Assert.assertEquals(global.getDefaultGroupCommands().size(), 0);
        Assert.assertEquals(global.getCommandGroups().size(), 1);
        
        CommandGroupMetadata parentGroup = global.getCommandGroups().get(0);
        Assert.assertEquals(parentGroup.getName(), "foo");
        Assert.assertEquals(parentGroup.getCommands().size(), 1);
        Assert.assertEquals(parentGroup.getSubGroups().size(), 1);
        
        CommandGroupMetadata subGroup = parentGroup.getSubGroups().get(0);
        Assert.assertEquals(parentGroup, subGroup.getParent());
        Assert.assertEquals(subGroup.getName(), "bar");
        Assert.assertEquals(subGroup.getDefaultCommand().getType(), Help.class);
        
        cli.parse("foo", "missing");
    }
    
    @Test(timeOut = 3000)
    public void sub_groups_mixed_04() {
        //@formatter:off
        CliBuilder<Object> builder
            = Cli.<Object>builder("test");
        builder.withGroup("foo")
               .withCommand(Add.class)
               .withSubGroup("bar")
               .withCommand(RemoteShow.class)
               .withSubGroup("faz")
               .withDefaultCommand(Help.class);
        //@formatter:on
        
        CommandLineInterface<C> cli = builder.build();
        GlobalMetadata<Object> global = cli.getMetadata();
        Assert.assertEquals(global.getDefaultGroupCommands().size(), 0);
        Assert.assertEquals(global.getCommandGroups().size(), 1);
        
        CommandGroupMetadata parentGroup = global.getCommandGroups().get(0);
        Assert.assertEquals(parentGroup.getName(), "foo");
        Assert.assertEquals(parentGroup.getCommands().size(), 1);
        Assert.assertEquals(parentGroup.getSubGroups().size(), 1);
        
        CommandGroupMetadata subGroup = parentGroup.getSubGroups().get(0);
        Assert.assertEquals(parentGroup, subGroup.getParent());
        Assert.assertEquals(subGroup.getName(), "bar");
        Assert.assertEquals(subGroup.getCommands().size(), 1);
        
        CommandGroupMetadata leafGroup = subGroup.getSubGroups().get(0);
        Assert.assertEquals(subGroup, leafGroup.getParent());
        Assert.assertEquals(leafGroup.getName(), "faz");
        Assert.assertEquals(leafGroup.getDefaultCommand().getType(), Help.class);
        
        Object cmd = cli.parse("foo", "bar", "faz");
        Assert.assertTrue(cmd instanceof Help);
        
        cmd = cli.parse("foo", "add");
        Assert.assertTrue(cmd instanceof Add);
        
        cmd = cli.parse("foo", "bar", "show");
        Assert.assertTrue(cmd instanceof RemoteShow);
    }
}
