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

import com.github.rvesse.airline.Accessor;
import com.github.rvesse.airline.annotations.Group;
import com.github.rvesse.airline.help.sections.HelpSection;
import com.github.rvesse.airline.utils.AirlineUtils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;

public class CommandMetadata {
    private final String name;
    private final String description;
    private final boolean hidden;
    private final List<OptionMetadata> globalOptions;
    private final List<OptionMetadata> groupOptions;
    private final List<OptionMetadata> commandOptions;
    private final OptionMetadata defaultOption;
    private final List<PositionalArgumentMetadata> positionalArgs;
    private final ArgumentsMetadata arguments;
    private final List<Accessor> metadataInjections;
    private final Class<?> type;
    private final List<String> groupNames;
    private final List<Group> groups;
    private final List<HelpSection> sections;

    //@formatter:off
    public CommandMetadata(String name, 
                           String description, 
                           boolean hidden, 
                           Iterable<OptionMetadata> globalOptions, 
                           Iterable<OptionMetadata> groupOptions,
                           Iterable<OptionMetadata> commandOptions, 
                           OptionMetadata defaultOption,
                           List<PositionalArgumentMetadata> positionalArguments,
                           ArgumentsMetadata arguments,
                           Iterable<Accessor> metadataInjections, 
                           Class<?> type, 
                           List<String> groupNames, 
                           List<Group> groups,
                           List<HelpSection> sections) {
    //@formatter:on
        if (StringUtils.isEmpty(name))
            throw new IllegalArgumentException("Command name may not be null/empty");
        if (StringUtils.containsWhitespace(name))
            throw new IllegalArgumentException("Command name may not contain whitespace");

        this.name = name;
        this.description = description;
        this.hidden = hidden;
        this.globalOptions = AirlineUtils.unmodifiableListCopy(globalOptions);
        this.groupOptions = AirlineUtils.unmodifiableListCopy(groupOptions);
        this.commandOptions = AirlineUtils.unmodifiableListCopy(commandOptions);
        this.defaultOption = defaultOption;
        this.positionalArgs = positionalArguments;
        this.arguments = arguments;

        // Check that we don't have required positional/non-positional arguments
        // after optional positional arguments
        boolean posArgsRequired = false;
        if (this.positionalArgs != null) {
            for (int i = 0; i < this.positionalArgs.size(); i++) {
                if (i == 0) {
                    posArgsRequired = this.positionalArgs.get(i).isRequired();
                } else if (!posArgsRequired && this.positionalArgs.get(i).isRequired()) {
                    throw new IllegalArgumentException(String.format(
                            "Positional argument %d (%s) is declared as @Required but one/more preceeding positional arguments are optional",
                            i, this.positionalArgs.get(i).getTitle()));
                } else {
                    posArgsRequired = this.positionalArgs.get(i).isRequired();
                }
            }
            if (this.positionalArgs.size() > 0 && !posArgsRequired) {
                if (this.arguments != null && this.arguments.isRequired()) {
                    throw new IllegalArgumentException(
                            "Non-positional arguments are declared as required but one/more preceding positional arguments are optional");
                }
            }
        }

        if (this.defaultOption != null && hasAnyArguments()) {
            throw new IllegalArgumentException(
                    "Command cannot declare both @Arguments/@PositionalArgument and use @DefaultOption");
        }

        this.metadataInjections = AirlineUtils.unmodifiableListCopy(metadataInjections);
        this.type = type;
        this.groupNames = groupNames;
        this.groups = groups;

        this.sections = AirlineUtils.unmodifiableListCopy(sections);
    }

    /**
     * Gets whether this command has any positional and/or non-positional
     * arguments
     * 
     * @return True if any arguments are defined, false otherwise
     */
    public boolean hasAnyArguments() {
        return hasNonPositionalArguments() || hasPositionalArguments();
    }

    /**
     * Gets whether this command has any positional arguments
     * 
     * @return True if positional arguments are defined, false otherwise
     */
    public boolean hasPositionalArguments() {
        return this.positionalArgs != null && this.positionalArgs.size() > 0;
    }

    /**
     * Gets whether this command has any non-positional arguments
     * 
     * @return True if non-positional arguments are defined, false otherwise
     */
    public boolean hasNonPositionalArguments() {
        return this.arguments != null;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isHidden() {
        return hidden;
    }

    public List<OptionMetadata> getAllOptions() {
        List<OptionMetadata> allOptions = new ArrayList<OptionMetadata>();
        allOptions.addAll(globalOptions);
        allOptions.addAll(groupOptions);
        allOptions.addAll(commandOptions);
        return ListUtils.unmodifiableList(allOptions);
    }

    /**
     * Gets the additional help sections
     * 
     * @return Help sections
     */
    public List<HelpSection> getHelpSections() {
        return sections;
    }

    public List<OptionMetadata> getGlobalOptions() {
        return globalOptions;
    }

    public List<OptionMetadata> getGroupOptions() {
        return groupOptions;
    }

    public List<OptionMetadata> getCommandOptions() {
        return commandOptions;
    }

    public OptionMetadata getDefaultOption() {
        return defaultOption;
    }

    public List<PositionalArgumentMetadata> getPositionalArguments() {
        return positionalArgs;
    }

    public ArgumentsMetadata getArguments() {
        return arguments;
    }

    public List<Accessor> getMetadataInjections() {
        return metadataInjections;
    }

    /**
     * Gets the command type i.e. the class that will be instantiated to
     * represent and execute this command
     * 
     * @return Command type
     */
    public Class<?> getType() {
        return type;
    }

    public List<String> getGroupNames() {
        return groupNames;
    }

    public List<Group> getGroups() {
        return groups;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("CommandMetadata {").append('\n');
        sb.append(" name='").append(name).append('\'').append('\n');
        sb.append(" , description='").append(description).append('\'').append('\n');
        sb.append(" , sections=").append(sections).append('\n');
        sb.append(" , globalOptions=").append(globalOptions).append('\n');
        sb.append(" , groupOptions=").append(groupOptions).append('\n');
        sb.append(" , commandOptions=").append(commandOptions).append('\n');
        sb.append(" , positionalArguments=").append(positionalArgs).append('\n');
        sb.append(" , arguments=").append(arguments).append('\n');
        sb.append(" , metadataInjections=").append(metadataInjections).append('\n');
        sb.append(" , type=").append(type).append('\n');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;

        if (!(other instanceof CommandMetadata))
            return false;

        CommandMetadata cmd = (CommandMetadata) other;

        // TODO This should ideally be more robust
        return StringUtils.equals(this.name, cmd.name) && this.type.equals(cmd.type);
    }
}
