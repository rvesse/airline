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
package com.github.rvesse.airline.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.rvesse.airline.utils.IteratorUtils;
import com.github.rvesse.airline.utils.StringUtils;

import com.github.rvesse.airline.model.CommandGroupMetadata;
import com.github.rvesse.airline.model.CommandMetadata;
import com.github.rvesse.airline.model.MetadataLoader;
import com.github.rvesse.airline.utils.AirlineUtils;

public class GroupBuilder<C> extends AbstractBuilder<CommandGroupMetadata> {

    private final String name;
    private String description = null;
    private Class<? extends C> defaultCommand = null;
    private boolean hidden = false;
    protected final Map<String, GroupBuilder<C>> subGroups = new HashMap<>();

    private final List<Class<? extends C>> commands = new ArrayList<>();

    GroupBuilder(String name) {
        if (StringUtils.isBlank(name))
            throw new IllegalArgumentException("Group name cannot be null/empty/whitespace");
        this.name = name;
    }

    public GroupBuilder<C> withDescription(String description) {
        if (description == null)
            throw new NullPointerException("description cannot be null");
        if (StringUtils.isEmpty(description))
            throw new IllegalArgumentException("description cannot be empty");
        if (this.description != null)
            throw new IllegalStateException("description is already set");
        this.description = description;
        return this;
    }

    public GroupBuilder<C> makeHidden() {
        return withHiddenState(true);
    }

    public GroupBuilder<C> makeVisible() {
        return withHiddenState(false);
    }

    public GroupBuilder<C> withHiddenState(boolean hidden) {
        this.hidden = hidden;
        return this;
    }

    public GroupBuilder<C> withSubGroup(String name) {
        checkNotBlank(name, "Group name");

        if (subGroups.containsKey(name)) {
            return subGroups.get(name);
        }

        GroupBuilder<C> group = new GroupBuilder<C>(name);
        subGroups.put(name, group);
        return group;
    }

    public GroupBuilder<C> getSubGroup(final String name) {
        checkNotBlank(name, "Group name");
        if (!subGroups.containsKey(name))
            throw new IllegalArgumentException(String.format("Group %s has not been declared", name));

        return subGroups.get(name);
    }

    public GroupBuilder<C> withDefaultCommand(Class<? extends C> defaultCommand) {
        if (defaultCommand == null)
            throw new NullPointerException("defaultCommand cannot be null");
        if (this.defaultCommand != null)
            throw new IllegalStateException("defaultCommand is already set");
        this.defaultCommand = defaultCommand;
        return this;
    }

    public GroupBuilder<C> withCommand(Class<? extends C> command) {
        if (command == null)
            throw new NullPointerException("command cannot be null");
        commands.add(command);
        return this;
    }

    @SuppressWarnings("unchecked")
    public GroupBuilder<C> withCommands(Class<? extends C> command, Class<? extends C>... moreCommands) {
        this.commands.add(command);
        this.commands.addAll(AirlineUtils.arrayToList(moreCommands));
        return this;
    }

    public GroupBuilder<C> withCommands(Iterable<Class<? extends C>> commands) {
        this.commands.addAll(IteratorUtils.toList(commands.iterator()));
        return this;
    }

    @Override
    public CommandGroupMetadata build() {
        CommandMetadata groupDefault = MetadataLoader.loadCommand(defaultCommand);
        List<CommandMetadata> groupCommands = MetadataLoader.loadCommands(commands);
        List<CommandGroupMetadata> subGroups = new ArrayList<CommandGroupMetadata>();
        for (GroupBuilder<C> builder : this.subGroups.values()) {
            subGroups.add(builder.build());
        }

        CommandGroupMetadata group = MetadataLoader.loadCommandGroup(name, description, hidden, subGroups,
                groupDefault, groupCommands);
        for (CommandGroupMetadata subGroup : subGroups) {
            subGroup.setParent(group);
        }
        return group;
    }
}
