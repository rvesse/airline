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

import org.apache.commons.lang3.StringUtils;

public class CommandMetadata {
    private final String name;
    private final String description;
    private final boolean hidden;
    private final List<OptionMetadata> globalOptions;
    private final List<OptionMetadata> groupOptions;
    private final List<OptionMetadata> commandOptions;
    private final OptionMetadata defaultOption;
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
        this.arguments = arguments;

        if (this.defaultOption != null && this.arguments != null) {
            throw new IllegalArgumentException("Command cannot declare both @Arguments and @DefaultOption");
        }

        this.metadataInjections = AirlineUtils.unmodifiableListCopy(metadataInjections);
        this.type = type;
        this.groupNames = groupNames;
        this.groups = groups;

        this.sections = AirlineUtils.unmodifiableListCopy(sections);
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
        return List.copyOf(allOptions);
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
