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

import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.rvesse.airline.args.ArgsGlobal;
import com.github.rvesse.airline.builder.CliBuilder;
import com.github.rvesse.airline.parser.ParseResult;
import com.github.rvesse.airline.parser.errors.handlers.CollectAll;

public class TestOptionScope {

    private Cli<ArgsGlobal> createCli() {
        //@formatter:off
        CliBuilder<ArgsGlobal> builder 
            = new CliBuilder<ArgsGlobal>("test")
                    .withGroup("group")
                        .withCommand(ArgsGlobal.class)
                        .parent()
                    .withCommand(ArgsGlobal.class);
        builder.withParser()
                    .withErrorHandler(new CollectAll());
        //@formatter:on
        return builder.build();
    }
    
    @Test
    public void option_scope_global_01() {
        Cli<ArgsGlobal> cli = createCli();
        ArgsGlobal cmd = cli.parse("--global", "command");
        
        Assert.assertTrue(cmd.global);
    }
    
    @Test
    public void option_scope_global_02() {
        Cli<ArgsGlobal> cli = createCli();
        ArgsGlobal cmd = cli.parse("group", "--global", "command");
        
        Assert.assertTrue(cmd.global);
    }
    
    @Test
    public void option_scope_global_03() {
        Cli<ArgsGlobal> cli = createCli();
        ArgsGlobal cmd = cli.parse("group", "command", "--global");
        
        Assert.assertTrue(cmd.global);
    }
    
    @Test
    public void option_scope_group_01() {
        Cli<ArgsGlobal> cli = createCli();
        ParseResult<ArgsGlobal> result = cli.parseWithResult("--group");
        Assert.assertFalse(result.wasSuccessful());
    }
    
    @Test
    public void option_scope_group_02() {
        Cli<ArgsGlobal> cli = createCli();
        ArgsGlobal cmd = cli.parse("group", "--group", "command");
        
        Assert.assertTrue(cmd.group);
    }
    
    @Test
    public void option_scope_group_03() {
        Cli<ArgsGlobal> cli = createCli();
        ArgsGlobal cmd = cli.parse("group", "command", "--group");
        
        Assert.assertTrue(cmd.group);
    }
    
    @Test
    public void option_scope_command_01() {
        Cli<ArgsGlobal> cli = createCli();
        ParseResult<ArgsGlobal> result = cli.parseWithResult("--command");
        Assert.assertFalse(result.wasSuccessful());
    }
    
    @Test
    public void option_scope_command_02() {
        Cli<ArgsGlobal> cli = createCli();
        ParseResult<ArgsGlobal> result = cli.parseWithResult("group", "--command", "command");
        Assert.assertFalse(result.wasSuccessful());
    }
    
    @Test
    public void option_scope_command_03() {
        Cli<ArgsGlobal> cli = createCli();
        ArgsGlobal cmd = cli.parse("group", "command", "--command");
        
        Assert.assertTrue(cmd.command);
    }
}
