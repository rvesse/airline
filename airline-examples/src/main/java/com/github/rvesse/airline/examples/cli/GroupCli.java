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
import com.github.rvesse.airline.builder.CliBuilder;
import com.github.rvesse.airline.examples.ExampleExecutor;
import com.github.rvesse.airline.examples.ExampleRunnable;
import com.github.rvesse.airline.examples.cli.commands.Help;
import com.github.rvesse.airline.examples.inheritance.Child;
import com.github.rvesse.airline.examples.inheritance.GoodGrandchild;
import com.github.rvesse.airline.examples.inheritance.Parent;
import com.github.rvesse.airline.examples.simple.Simple;

/**
 * An example of creating a CLI using command groups
 */
public class GroupCli {
    
    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        //@formatter:off
        // The program name is cli
        CliBuilder<ExampleRunnable> builder = CommandLineInterface.<ExampleRunnable>builder("cli")
                                                 // Add a description
                                                 .withDescription("A simple CLI with several commands available in groups");
        // Add a basic group
        builder.withGroup("basic")
               .withDescription("Basic commands")
               .withCommand(Simple.class);
        // Add another group
        builder.withGroup("inheritance")
               .withDescription("Commands that demonstrate option inheritance")
               .withCommands(Parent.class, Child.class, GoodGrandchild.class);
        // You can still define top level commands as well 
        builder.withCommand(Help.class);
        //@formatter:on
        
        ExampleExecutor.executeCli(builder.build(), args);
    }

}
