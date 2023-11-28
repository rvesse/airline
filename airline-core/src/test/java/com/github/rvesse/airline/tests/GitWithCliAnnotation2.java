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

import com.github.rvesse.airline.CommandLineInterface;
import com.github.rvesse.airline.tests.Git.Add;
import com.github.rvesse.airline.tests.Git.RemoteAdd;
import com.github.rvesse.airline.tests.Git.RemoteShow;
import com.github.rvesse.airline.annotations.Alias;
import com.github.rvesse.airline.annotations.Cli;
import com.github.rvesse.airline.annotations.Group;
import com.github.rvesse.airline.annotations.Parser;
import com.github.rvesse.airline.help.Help;

//@formatter:off
@Cli(name = "git",
     description = "the stupid content tracker", 
     defaultCommand = Help.class, 
     commands = { Help.class, Add.class }, 
     groups = {
        @Group(name = "remote",
               description = "Manage set of tracked repositories",
               defaultCommand = RemoteShow.class,
               commands = { RemoteShow.class, RemoteAdd.class })
     },
     parserConfiguration = @Parser(
         aliases = {
             @Alias(
                 name = "foo", 
                 arguments = { "remote", "show" }
             ) 
         })
)
//@formatter:on
public class GitWithCliAnnotation2 extends Git {

    public static void run(String[] args) {
        CommandLineInterface<Runnable> gitParser = new CommandLineInterface<>(
                GitWithCliAnnotation2.class);

        gitParser.parse(args).run();
    }
}
