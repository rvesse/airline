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
package com.github.rvesse.airline.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.github.rvesse.airline.builder.ParserBuilder;
import com.github.rvesse.airline.help.sections.HelpSection;
import com.github.rvesse.airline.restrictions.GlobalRestriction;
import com.github.rvesse.airline.utils.AirlineUtils;

/**
 * Represents metadata about a CLI
 */
public class GlobalMetadata<T> {

    private final String name;
    private final String description;
    private final List<OptionMetadata> options;
    private final CommandMetadata defaultCommand;
    private final List<CommandMetadata> defaultGroupCommands;
    private final List<CommandGroupMetadata> commandGroups;
    private final ParserMetadata<T> parserConfig;
    private final List<GlobalRestriction> restrictions;
    private final List<HelpSection> baseHelpSections;

    public GlobalMetadata(String name, String description, Iterable<OptionMetadata> options,
            CommandMetadata defaultCommand, Iterable<CommandMetadata> defaultGroupCommands,
            Iterable<CommandGroupMetadata> commandGroups, Iterable<GlobalRestriction> restrictions,
            Iterable<HelpSection> baseHelpSections, ParserMetadata<T> parserConfig) {
        if (StringUtils.isBlank(name))
            throw new IllegalArgumentException("Program name cannot be null/empty/whitespace");
        if (parserConfig == null)
            throw new NullPointerException("parserConfig cannot be null");

        this.name = name;
        this.description = description;
        this.options = AirlineUtils.unmodifiableListCopy(options);
        this.defaultCommand = defaultCommand;
        this.defaultGroupCommands = AirlineUtils.unmodifiableListCopy(defaultGroupCommands);
        this.commandGroups = AirlineUtils.unmodifiableListCopy(commandGroups);
        this.restrictions = AirlineUtils.unmodifiableListCopy(restrictions);
        this.baseHelpSections = AirlineUtils.unmodifiableListCopy(baseHelpSections);
        this.parserConfig = parserConfig != null ? parserConfig : ParserBuilder.<T> defaultConfiguration();

        // Look for duplicate command names on different classes
        checkForSuppressedCommands(this.defaultGroupCommands, this.defaultCommand, "");
        for (CommandGroupMetadata group : this.commandGroups) {
            checkForSuppressedCommands(group, "");
        }
    }

    private static void checkForSuppressedCommands(CommandGroupMetadata group, String groupPath) {
        StringBuilder groupName = new StringBuilder();
        if (groupPath.length() > 0) {
            groupName.append(groupPath);
            groupName.append(' ');
        }
        groupName.append(group.getName());

        checkForSuppressedCommands(group.getCommands(), group.getDefaultCommand(), groupName.toString());

        for (CommandGroupMetadata subGroup : group.getSubGroups()) {
            checkForSuppressedCommands(subGroup, groupName.toString());
        }
    }

    private static void checkForSuppressedCommands(List<CommandMetadata> commands, CommandMetadata defaultCommand,
            String groupName) {
        Map<String, Class<?>> nameToClass = new HashMap<>();

        for (CommandMetadata cmd : commands) {
            Class<?> cls = nameToClass.get(cmd.getName());
            if (cls == null) {
                nameToClass.put(cmd.getName(), cmd.getType());
            } else {
                // If a different command class this is illegal
                // Duplicate command classes are fine (although non-sensical)
                if (!cls.equals(cmd.getType())) {
                    suppressedCommand(cls, cmd, groupName);
                }
            }
        }

        if (defaultCommand != null) {
            Class<?> cls = nameToClass.get(defaultCommand.getName());
            if (cls != null && !cls.equals(defaultCommand.getType())) {
                suppressedCommand(cls, defaultCommand, groupName);
            }
        }
    }

    private static void suppressedCommand(Class<?> cls, CommandMetadata cmd, String groupName) {
        throw new IllegalArgumentException(String.format(
                "Command classes '%s' and '%s' both declare the command name '%s' and are both in the %s and as such one of these commands would be inaccessible.  Please correct your @Command annotations to use distinct command names.",
                cls, cmd.getType(), cmd.getName(),
                groupName != null ? String.format("group '%s'", groupName) : "default group"));
    }

    /**
     * Gets the name of the CLI
     * 
     * @return Name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the description of the CLI
     * 
     * @return Description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the global scoped options
     * 
     * @return Options
     */
    public List<OptionMetadata> getOptions() {
        return options;
    }

    /**
     * Gets the default command for the CLI
     * 
     * @return Default command
     */
    public CommandMetadata getDefaultCommand() {
        return defaultCommand;
    }

    /**
     * Gets the default group of commands for the CLI i.e. commands that don't
     * need a group to be specified
     * 
     * @return Default group commands
     */
    public List<CommandMetadata> getDefaultGroupCommands() {
        return defaultGroupCommands;
    }

    /**
     * Gets the command groups for the CLI
     * 
     * @return Command groups
     */
    public List<CommandGroupMetadata> getCommandGroups() {
        return commandGroups;
    }

    /**
     * Gets the global restrictions
     * 
     * @return Global restrictions
     */
    public List<GlobalRestriction> getRestrictions() {
        return restrictions;
    }

    /**
     * Gets the base help sections used by all commands unless specifically
     * overridden by individual commands
     * 
     * @return Base help sections
     */
    public List<HelpSection> getBaseHelpSections() {
        return baseHelpSections;
    }

    /**
     * Gets the parser configuration for the CLI
     * 
     * @return Parser configuration
     */
    public ParserMetadata<T> getParserConfiguration() {
        return parserConfig;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("GlobalMetadata");
        sb.append("{name='").append(name).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", options=").append(options);
        sb.append(", defaultCommand=").append(defaultCommand);
        sb.append(", defaultGroupCommands=").append(defaultGroupCommands);
        sb.append(", commandGroups=").append(commandGroups);
        sb.append(", parserConfig=").append('\n').append(parserConfig);
        sb.append('\n').append('}');
        return sb.toString();
    }
}
