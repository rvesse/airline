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
package com.github.rvesse.airline.examples.cli;

import com.github.rvesse.airline.CommandLineInterface;
import java.io.IOException;

import com.github.rvesse.airline.annotations.Cli;
import com.github.rvesse.airline.annotations.Group;
import com.github.rvesse.airline.examples.ExampleExecutor;
import com.github.rvesse.airline.examples.ExampleRunnable;
import com.github.rvesse.airline.examples.cli.commands.Help;
import com.github.rvesse.airline.examples.inheritance.Child;
import com.github.rvesse.airline.examples.inheritance.GoodGrandchild;
import com.github.rvesse.airline.examples.inheritance.Parent;
import com.github.rvesse.airline.examples.simple.Simple;
import com.github.rvesse.airline.help.CommandGroupUsageGenerator;
import com.github.rvesse.airline.help.cli.CliCommandGroupUsageGenerator;
import com.github.rvesse.airline.model.CommandGroupMetadata;

/**
 * An example of creating a CLI using command groups
 */
//@formatter:off
@Cli(
    name = "cli", 
    description = "A simple CLI with several commands available in groups",
    groups = {
        @Group(
            name = "basic",
            description = "Basic commands",
            commands = { Simple.class }
        ),
        @Group(
            name = "inheritance",
            description = "Commands that demonstrate option inheritance",
            commands = { Parent.class, Child.class, GoodGrandchild.class }
        )
    },
    commands = { Help.class }
)
//@formatter:on
public class GroupCli {
    
    public static void main(String[] args) {
        CommandLineInterface<ExampleRunnable> cli = new CommandLineInterface<ExampleRunnable>(GroupCli.class);
        
        ExampleExecutor.executeCli(cli, args);
    }
    
    public static void generateHelp() throws IOException {
        CommandLineInterface<ExampleRunnable> cli = new CommandLineInterface<ExampleRunnable>(GroupCli.class);
        
        CommandGroupUsageGenerator<ExampleRunnable> helpGenerator = new CliCommandGroupUsageGenerator<>();
        try {
            helpGenerator.usage(cli.getMetadata(), new CommandGroupMetadata[] { cli.getMetadata().getCommandGroups().get(0) }, System.out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
