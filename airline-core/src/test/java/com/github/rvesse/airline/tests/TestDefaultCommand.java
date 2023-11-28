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
package com.github.rvesse.airline.tests;

import java.util.ArrayList;
import java.util.List;

import com.github.rvesse.airline.Cli;
import com.github.rvesse.airline.CommandLineInterface;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.rvesse.airline.annotations.Arguments;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.DefaultOption;
import com.github.rvesse.airline.annotations.Option;
import com.github.rvesse.airline.builder.CliBuilder;

public class TestDefaultCommand {

    @Command(name = "args")
    public static final class Args {
        @Arguments
        List<String> args = new ArrayList<>();
    }
    
   
    protected <T> CommandLineInterface<T> createSimpleCli(Class<T> cls) {
        //@formatter:off
        CommandLineInterface<T> cli = new CliBuilder<T>("test")
                        .withDefaultCommand(cls)
                        .build();
        //@formatter:on
        return cli;
    }

    protected CommandLineInterface<Args> createSimpleCli() {
        return createSimpleCli(Args.class);
    }
    
    @Test
    public void default_command_01() {
        CommandLineInterface<Args> cli = createSimpleCli();
        Args cmd = cli.parse();
        Assert.assertNotNull(cmd);
        Assert.assertTrue(cmd.args.isEmpty());
    }
    
    @Test
    public void default_command_02() {
        CommandLineInterface<Args> cli = createSimpleCli();
        Args cmd = cli.parse("args");
        Assert.assertNotNull(cmd);
        Assert.assertTrue(cmd.args.isEmpty());
    }
    
    @Test
    public void default_command_03() {
        CommandLineInterface<Args> cli = createSimpleCli();
        Args cmd = cli.parse("foo");
        Assert.assertNotNull(cmd);
        Assert.assertFalse(cmd.args.isEmpty());
        Assert.assertEquals(cmd.args.size(), 1);
        Assert.assertEquals(cmd.args.get(0), "foo");
    }
    
    @Test
    public void default_command_04() {
        //@formatter:off
        CommandLineInterface<Args> cli = new CliBuilder<Args>("test")
                        .withGroup("group")
                            .withDefaultCommand(Args.class)
                            .parent()
                        .build();
        //@formatter:on
        Args cmd = cli.parse("group", "foo");
        Assert.assertNotNull(cmd);
        Assert.assertFalse(cmd.args.isEmpty());
        Assert.assertEquals(cmd.args.size(), 1);
        Assert.assertEquals(cmd.args.get(0), "foo");
    }
    
    @Test
    public void default_command_05() {
        //@formatter:off
        CommandLineInterface<Args> cli = new CliBuilder<Args>("test")
                        .withGroup("group")
                            .withDefaultCommand(Args.class)
                            .withCommand(Args.class)
                            .parent()
                        .build();
        //@formatter:on
        Args cmd = cli.parse("group", "foo");
        Assert.assertNotNull(cmd);
        Assert.assertFalse(cmd.args.isEmpty());
        Assert.assertEquals(cmd.args.size(), 1);
        Assert.assertEquals(cmd.args.get(0), "foo");
    }
    
    @Test
    public void default_command_abbreviation_01() {
        //@formatter:off
        CliBuilder<Args> builder 
            = new CliBuilder<Args>("test")
                        .withDefaultCommand(Args.class);
        builder.withParser()
               .withCommandAbbreviation();
        CommandLineInterface<Args> cli = builder.build();
        //@formatter:on
        Args cmd = cli.parse("foo");
        Assert.assertNotNull(cmd);
        Assert.assertFalse(cmd.args.isEmpty());
        Assert.assertEquals(cmd.args.size(), 1);
        Assert.assertEquals(cmd.args.get(0), "foo");
    }
    
    @Test
    public void default_command_abbreviation_02() {
        //@formatter:off
        CliBuilder<Args> builder 
            = new CliBuilder<Args>("test")
                        .withDefaultCommand(Args.class)
                        .withCommand(Args.class);
        builder.withParser()
               .withCommandAbbreviation();
        CommandLineInterface<Args> cli = builder.build();
        //@formatter:on
        Args cmd = cli.parse("foo");
        Assert.assertNotNull(cmd);
        Assert.assertFalse(cmd.args.isEmpty());
        Assert.assertEquals(cmd.args.size(), 1);
        Assert.assertEquals(cmd.args.get(0), "foo");
    }
    
    @Test
    public void default_command_abbreviation_03() {
        //@formatter:off
        CliBuilder<Args> builder 
            = new CliBuilder<Args>("test")
                        .withGroup("group")
                            .withDefaultCommand(Args.class)
                            .withCommand(Args.class)
                            .parent();
        builder.withParser()
               .withCommandAbbreviation();
        CommandLineInterface<Args> cli = builder.build();
        //@formatter:on
        Args cmd = cli.parse("group", "foo");
        Assert.assertNotNull(cmd);
        Assert.assertFalse(cmd.args.isEmpty());
        Assert.assertEquals(cmd.args.size(), 1);
        Assert.assertEquals(cmd.args.get(0), "foo");
    }
    
    @Test
    public void default_command_abbreviation_04() {
        //@formatter:off
        CliBuilder<Args> builder 
            = new CliBuilder<Args>("test")
                        .withGroup("group")
                            .withDefaultCommand(Args.class)
                            .withCommand(Args.class)
                            .parent();
        builder.withParser()
               .withCommandAbbreviation();
        CommandLineInterface<Args> cli = builder.build();
        //@formatter:on
        
        // As a valid abbreviation this actually selects the command rather than the default
        Args cmd = cli.parse("group", "ar", "foo");
        Assert.assertNotNull(cmd);
        Assert.assertFalse(cmd.args.isEmpty());
        Assert.assertEquals(cmd.args.size(), 1);
        Assert.assertEquals(cmd.args.get(0), "foo");
    }
    
    @Command(name = "args")
    public static final class ArgsWithDefaultOption {
        @Option(name = "-a")
        @DefaultOption
        List<String> args = new ArrayList<>();
    }
    
    @Test
    public void default_command_default_option_01() {
        CommandLineInterface<ArgsWithDefaultOption> cli = createSimpleCli(ArgsWithDefaultOption.class);
        ArgsWithDefaultOption cmd = cli.parse();
        Assert.assertNotNull(cmd);
        Assert.assertTrue(cmd.args.isEmpty());
    }
    
    @Test
    public void default_command_default_option_02() {
        CommandLineInterface<ArgsWithDefaultOption> cli = createSimpleCli(ArgsWithDefaultOption.class);
        ArgsWithDefaultOption cmd = cli.parse("args");
        Assert.assertNotNull(cmd);
        Assert.assertTrue(cmd.args.isEmpty());
    }
    
    @Test
    public void default_command_default_option_03() {
        CommandLineInterface<ArgsWithDefaultOption> cli = createSimpleCli(ArgsWithDefaultOption.class);
        ArgsWithDefaultOption cmd = cli.parse("foo");
        Assert.assertNotNull(cmd);
        Assert.assertFalse(cmd.args.isEmpty());
        Assert.assertEquals(cmd.args.size(), 1);
        Assert.assertEquals(cmd.args.get(0), "foo");
    }
    
    @Test
    public void default_command_default_option_04() {
        //@formatter:off
        CommandLineInterface<ArgsWithDefaultOption> cli = new CliBuilder<ArgsWithDefaultOption>("test")
                        .withGroup("group")
                            .withDefaultCommand(ArgsWithDefaultOption.class)
                            .parent()
                        .build();
        //@formatter:on
        ArgsWithDefaultOption cmd = cli.parse("group", "foo");
        Assert.assertNotNull(cmd);
        Assert.assertFalse(cmd.args.isEmpty());
        Assert.assertEquals(cmd.args.size(), 1);
        Assert.assertEquals(cmd.args.get(0), "foo");
    }
}
