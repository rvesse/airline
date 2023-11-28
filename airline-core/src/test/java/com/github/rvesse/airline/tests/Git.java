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

import java.util.List;

import com.github.rvesse.airline.CommandLineInterface;
import com.github.rvesse.airline.annotations.Arguments;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.Option;
import com.github.rvesse.airline.builder.CliBuilder;
import com.github.rvesse.airline.help.Help;

import static com.github.rvesse.airline.annotations.OptionType.GLOBAL;

public class Git
{
    public static void main(String... args)
    {
        //@formatter:off
        CliBuilder<Runnable> builder = CommandLineInterface.<Runnable>builder("git")
                .withDescription("the stupid content tracker")
                .withDefaultCommand(Help.class)
                .withCommand(Help.class)
                .withCommand(Add.class)
                .withGroup("remote")
                    .withDescription("Manage set of tracked repositories")
                    .withDefaultCommand(RemoteShow.class)
                    .withCommand(RemoteShow.class)
                    .withCommand(RemoteAdd.class)
                    .parent()
                .withParser()
                    .withCommandAbbreviation()
                    .parent();
        //@formatter:on

        CommandLineInterface<Runnable> gitParser = builder.build();

        gitParser.parse(args).run();
    }

    public static class GitCommand
            implements Runnable
    {
        @Option(type = GLOBAL, name = "-v", description = "Verbose mode")
        public boolean verbose;

        public void run()
        {
            System.out.println(getClass().getSimpleName());
        }
    }

    @Command(name = "add", description = "Add file contents to the index")
    public static class Add
            extends GitCommand
    {
        @Arguments(description = "Patterns of files to be added")
        public List<String> patterns;

        @Option(name = "-i", description = "Add modified contents interactively.")
        public boolean interactive;
    }

    @Command(name = "show", description = "Gives some information about the remote <name>")
    public static class RemoteShow
            extends GitCommand
    {
        @Option(name = "-n", description = "Do not query remote heads")
        public boolean noQuery;

        @Arguments(description = "Remote to show")
        public String remote;
    }

    @Command(name = "add", description = "Adds a remote")
    public static class RemoteAdd
            extends GitCommand
    {
        @Option(name = "-t", description = "Track only a specific branch")
        public String branch;

        @Arguments(description = "Name and URL of remote repository to add", title={"name", "url"})
        public List<String> remote;
    }
}
