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

import java.util.Collections;
import java.util.List;

import com.github.rvesse.airline.utils.StringUtils;

import com.github.rvesse.airline.utils.AirlineUtils;

/**
 * Represents meta-data about a command group
 *
 */
public class CommandGroupMetadata {
    private final String name;
    private final String description;
    private final boolean hidden;
    private final List<OptionMetadata> options;
    private final CommandMetadata defaultCommand;
    private final List<CommandMetadata> commands;
    private final List<CommandGroupMetadata> subGroups;
    private CommandGroupMetadata parentGroup;

    //@formatter:off
    public CommandGroupMetadata(String name, 
                                String description, 
                                boolean hidden, 
                                Iterable<OptionMetadata> options,
                                Iterable<CommandGroupMetadata> subGroups, 
                                CommandMetadata defaultCommand, 
                                Iterable<CommandMetadata> commands) {
    //@formatter:on
        if (StringUtils.isEmpty(name))
            throw new IllegalArgumentException("Group name may not be null/empty");
        if (StringUtils.containsWhitespace(name))
            throw new IllegalArgumentException("Group name may not contain whitespace");

        this.name = name;
        this.description = description;
        this.hidden = hidden;
        this.options = AirlineUtils.unmodifiableListCopy(options);
        this.subGroups = AirlineUtils.listCopy(subGroups);
        this.defaultCommand = defaultCommand;
        this.commands = AirlineUtils.listCopy(commands);
        if (this.defaultCommand != null && !this.commands.contains(this.defaultCommand)) {
            this.commands.add(this.defaultCommand);
        }
    }

    /**
     * Gets the name of the group
     * 
     * @return Name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the description for the group
     * 
     * @return Description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets whether the group is hidden
     * 
     * @return True if hidden, false otherwise
     */
    public boolean isHidden() {
        return hidden;
    }

    /**
     * Gets the group options
     * 
     * @return Group options
     */
    public List<OptionMetadata> getOptions() {
        return options;
    }

    /**
     * Gets the default command for the group
     * 
     * @return Default command
     */
    public CommandMetadata getDefaultCommand() {
        return defaultCommand;
    }

    /**
     * Gets the commands for the group
     * 
     * @return Commands
     */
    public List<CommandMetadata> getCommands() {
        return Collections.unmodifiableList(commands);
    }

    /**
     * Adds a command to the group
     * 
     * @param command
     *            Command
     */
    public void addCommand(CommandMetadata command) {
        if (!commands.contains(command)) {
            commands.add(command);
        }
    }

    /**
     * Gets the sub-groups of this group
     * 
     * @return Sub-groups
     */
    public List<CommandGroupMetadata> getSubGroups() {
        return Collections.unmodifiableList(subGroups);
    }

    /**
     * Adds a sub-group to the group
     * 
     * @param subGroup
     *            Sub-group
     */
    public void addSubGroup(CommandGroupMetadata subGroup) {
        if (!subGroups.contains(subGroup)) {
            subGroups.add(subGroup);
        }
    }

    /**
     * Sets the parent for a group
     * 
     * @param parent
     *            Parent group
     */
    public void setParent(CommandGroupMetadata parent) {
        if (this.parentGroup != null && this.parentGroup != parent)
            throw new IllegalStateException("Parent group is already set");
        this.parentGroup = parent;
    }

    /**
     * Gets the parent group which may be null
     * 
     * @return Parent group, null if a top level group
     */
    public CommandGroupMetadata getParent() {
        return this.parentGroup;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("CommandGroupMetadata");
        sb.append("{name='").append(name).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", hidden=").append(hidden);
        sb.append(", options=").append(options);
        sb.append(", defaultCommand=").append(defaultCommand);
        sb.append(", commands=").append(commands);
        sb.append('}');
        return sb.toString();
    }
}
