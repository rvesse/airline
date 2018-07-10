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
package com.github.rvesse.airline.help;

import javax.inject.Inject;

import com.github.rvesse.airline.Channels;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;

import com.github.rvesse.airline.annotations.Arguments;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.Option;
import com.github.rvesse.airline.help.cli.CliCommandGroupUsageGenerator;
import com.github.rvesse.airline.help.cli.CliCommandUsageGenerator;
import com.github.rvesse.airline.help.cli.CliGlobalUsageGenerator;
import com.github.rvesse.airline.help.cli.CliGlobalUsageSummaryGenerator;
import com.github.rvesse.airline.model.CommandGroupMetadata;
import com.github.rvesse.airline.model.CommandMetadata;
import com.github.rvesse.airline.model.GlobalMetadata;
import com.github.rvesse.airline.utils.predicates.parser.AbbreviatedCommandFinder;
import com.github.rvesse.airline.utils.predicates.parser.AbbreviatedGroupFinder;
import com.github.rvesse.airline.utils.predicates.parser.CommandFinder;
import com.github.rvesse.airline.utils.predicates.parser.GroupFinder;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

@Command(name = "help", description = "Display help information")
public class Help<T> implements Runnable, Callable<Void> {
    @Inject
    public GlobalMetadata<T> global;

    @Arguments
    public List<String> command = new ArrayList<>();

    @Option(name = "--include-hidden", description = "When set the help output will include hidden commands and options", hidden = true)
    public boolean includeHidden = false;

    @Override
    public void run() {
        try {
            help(global, command, this.includeHidden);
        } catch (IOException e) {
            throw new RuntimeException("Error generating usage documentation", e);
        }
    }

    @Override
    public Void call() {
        run();
        return null;
    }

    /**
     * Displays plain text format help for the given command to standard out
     *
     * @param command
     *            Command
     * @throws IOException
     */
    public static void help(CommandMetadata command) throws IOException {
        help(command, Channels.output());
    }

    /**
     * Displays plain text format help for the given command to standard out
     *
     * @param command
     *            Command
     * @throws IOException
     */
    public static void help(CommandMetadata command, boolean includeHidden) throws IOException {
        help(command, includeHidden, Channels.output());
    }

    /**
     * Displays plain text format help or the given command to the given output
     * stream
     *
     * @param command
     *            Command
     * @param out
     *            Output stream
     * @throws IOException
     */
    public static void help(CommandMetadata command, OutputStream out) throws IOException {
        help(command, false, out);
    }

    /**
     * Displays plain text format help or the given command to the given output
     * stream
     *
     * @param command
     *            Command
     * @param out
     *            Output stream
     * @throws IOException
     */
    public static void help(CommandMetadata command, boolean includeHidden, OutputStream out) throws IOException {
        new CliCommandUsageGenerator(includeHidden).usage(null, null, command.getName(), command, null, out);
    }

    /**
     * Displays plain text format program help to standard out
     *
     * @param global
     *            Program metadata
     * @param commandNames
     *            Command Names
     * @throws IOException
     */
    public static <T> void help(GlobalMetadata<T> global, List<String> commandNames) throws IOException {
        help(global, commandNames, false, Channels.output());
    }

    /**
     * Displays plain text format program help to standard out
     *
     * @param global
     *            Program metadata
     * @param commandNames
     *            Command Names
     * @param includeHidden
     *            Whether to include hidden commands and options in the output
     * @throws IOException
     */
    public static <T> void help(GlobalMetadata<T> global, List<String> commandNames, boolean includeHidden)
            throws IOException {
        help(global, commandNames, includeHidden, Channels.output());
    }

    /**
     * Displays plain text format program help to the given output stream
     *
     * @param global
     *            Program meta-data
     * @param commandNames
     *            Command Names
     * @param out
     *            Output Stream
     * @throws IOException
     */
    public static <T> void help(GlobalMetadata<T> global, List<String> commandNames, OutputStream out)
            throws IOException {
        help(global, commandNames, false, out);
    }

    /**
     * Displays plain text format program help to the given output stream
     *
     * @param global
     *            Program meta-data
     * @param commandNames
     *            Command Names
     * @param out
     *            Output Stream
     * @throws IOException
     */
    public static <T> void help(GlobalMetadata<T> global, List<String> commandNames, boolean includeHidden,
            OutputStream out) throws IOException {
        if (commandNames.isEmpty()) {
            new CliGlobalUsageSummaryGenerator<T>(includeHidden).usage(global, out);
            return;
        }

        String name = commandNames.get(0);

        // Main program?
        if (name.equals(global.getName())) {
            // Main program help
            new CliGlobalUsageGenerator<T>(includeHidden).usage(global, out);
            return;
        }

        // Predicates we may need
        Predicate<CommandGroupMetadata> findGroupPredicate;
        Predicate<CommandMetadata> findCommandPredicate;
        //@formatter:off
        findGroupPredicate = global.getParserConfiguration().allowsAbbreviatedCommands()
                             ? new AbbreviatedGroupFinder(name, global.getCommandGroups())
                             : new GroupFinder(name);
        //@formatter:on

        // A command in a group?
        CommandMetadata command;
        CommandGroupMetadata group = CollectionUtils.find(global.getCommandGroups(), findGroupPredicate);
        if (group != null) {
            List<CommandGroupMetadata> groupPath = new ArrayList<CommandGroupMetadata>();
            groupPath.add(group);

            // General group help or specific group command help?
            if (commandNames.size() == 1) {
                // General group help
                new CliCommandGroupUsageGenerator<T>(includeHidden).usage(global,
                        groupPath.toArray(new CommandGroupMetadata[0]), out);
                return;
            } else {
                // Group/Sub-Group command help
                int i = 1;
                String commandOrSubGroupName = commandNames.get(i);

                while (group.getSubGroups().size() > 0 && i < commandNames.size()) {
                    commandOrSubGroupName = commandNames.get(i);

                    //@formatter:off
                    findGroupPredicate = global.getParserConfiguration().allowsAbbreviatedCommands()
                                         ? new AbbreviatedGroupFinder(commandOrSubGroupName, group.getSubGroups())
                                         : new GroupFinder(commandOrSubGroupName);
                    //@formatter:on
                    CommandGroupMetadata subGroup = CollectionUtils.find(group.getSubGroups(), findGroupPredicate);
                    if (subGroup != null) {
                        // Found a valid sub-group
                        groupPath.add(subGroup);
                        group = subGroup;
                        i++;
                        if (i == commandNames.size()) {
                            // General sub-group help
                            new CliCommandGroupUsageGenerator<T>(includeHidden).usage(global,
                                    groupPath.toArray(new CommandGroupMetadata[0]), out);
                            return;
                        }
                    } else {
                        // No relevant sub-group found
                        break;
                    }
                }

                // Look for a command in the current group/sub-group
                commandOrSubGroupName = commandNames.get(i);

                //@formatter:off
                findCommandPredicate = global.getParserConfiguration().allowsAbbreviatedCommands()
                                       ? new AbbreviatedCommandFinder(commandOrSubGroupName, group.getCommands())
                                       : new CommandFinder(commandOrSubGroupName);
                //@formatter:on
                command = CollectionUtils.find(group.getCommands(), findCommandPredicate);
                if (command != null) {
                    new CliCommandUsageGenerator().usage(global.getName(), UsageHelper.toGroupNames(groupPath),
                            command.getName(), command, global.getParserConfiguration(), out);
                    return;
                }

                // Didn't find an appropriate command
                if (global.getParserConfiguration().allowsAbbreviatedCommands()) {
                    Channels.output().println(
                            "Unknown command " + name + " " + commandOrSubGroupName + " or an ambiguous abbreviation");
                } else {
                    Channels.output().println("Unknown command " + name + " " + commandOrSubGroupName);
                }
            }
        }

        // A command in the default group?
        //@formatter:off
        findCommandPredicate = global.getParserConfiguration().allowsAbbreviatedCommands()
                               ? new AbbreviatedCommandFinder(name, global.getDefaultGroupCommands())
                               : new CommandFinder(name);
        //@formatter:on
        command = CollectionUtils.find(global.getDefaultGroupCommands(), findCommandPredicate);
        if (command != null) {
            // Command in default group help
            new CliCommandUsageGenerator(includeHidden).usage(global.getName(), null, command.getName(), command,
                    global.getParserConfiguration(), out);
            return;
        }

        // Didn't find an appropriate group
        if (global.getParserConfiguration().allowsAbbreviatedCommands()) {
            Channels.output().println("Unknown command " + name + " or an ambiguous abbreviation");
        } else {
            Channels.output().println("Unknown command " + name);
        }
    }
}
