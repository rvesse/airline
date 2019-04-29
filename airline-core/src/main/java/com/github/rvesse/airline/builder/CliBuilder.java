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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;

import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.collections4.ListUtils;

import com.github.rvesse.airline.CommandLineInterface;
import com.github.rvesse.airline.help.sections.HelpSection;
import com.github.rvesse.airline.model.CommandGroupMetadata;
import com.github.rvesse.airline.model.CommandMetadata;
import com.github.rvesse.airline.model.GlobalMetadata;
import com.github.rvesse.airline.model.MetadataLoader;
import com.github.rvesse.airline.restrictions.GlobalRestriction;
import com.github.rvesse.airline.restrictions.None;

/**
 * Builder for CLIs
 *
 * @param <C>
 *            Command type
 */
public class CliBuilder<C> extends AbstractBuilder<CommandLineInterface<C>> {

    protected final String name;
    protected String description;
    protected String optionSeparators;
    protected Class<? extends C> defaultCommand;
    protected final List<Class<? extends C>> defaultCommandGroupCommands = new ArrayList<>();
    protected final Map<String, GroupBuilder<C>> groups = new HashMap<>();
    protected final List<GlobalRestriction> restrictions = new ArrayList<>();
    protected final ParserBuilder<C> parserBuilder = new ParserBuilder<C>();
    protected final Map<String, HelpSection> baseHelpSections = new HashMap<>();

    public CliBuilder(String name) {
        checkNotBlank(name, "Program name");
        this.name = name;
    }

    public CliBuilder<C> withDescription(String description) {
        checkNotEmpty(description, "Description");
        this.description = description;
        return this;
    }

    public CliBuilder<C> withDefaultCommand(Class<? extends C> defaultCommand) {
        this.defaultCommand = defaultCommand;
        return this;
    }

    public CliBuilder<C> withCommand(Class<? extends C> command) {
        this.defaultCommandGroupCommands.add(command);
        return this;
    }

    @SuppressWarnings("unchecked")
    public CliBuilder<C> withCommands(Class<? extends C> command, Class<? extends C>... moreCommands) {
        this.defaultCommandGroupCommands.add(command);
        this.defaultCommandGroupCommands
                .addAll(ListUtils.unmodifiableList(IteratorUtils.toList(IteratorUtils.arrayIterator(moreCommands))));
        return this;
    }

    public CliBuilder<C> withCommands(Iterable<Class<? extends C>> commands) {
        this.defaultCommandGroupCommands.addAll(ListUtils.unmodifiableList(IteratorUtils.toList(commands.iterator())));
        return this;
    }

    public GroupBuilder<C> withGroup(String name) {
        checkNotBlank(name, "Group name");

        if (groups.containsKey(name)) {
            return groups.get(name);
        }

        GroupBuilder<C> group = new GroupBuilder<C>(this, name);
        groups.put(name, group);
        return group;
    }

    public GroupBuilder<C> getGroup(final String name) {
        checkNotBlank(name, "Group name");
        if (!groups.containsKey(name))
            throw new IllegalArgumentException(String.format("Group %s has not been declared", name));

        return groups.get(name);
    }

    public CliBuilder<C> withRestriction(GlobalRestriction restriction) {
        if (restriction != null)
            restrictions.add(restriction);
        return this;
    }

    public CliBuilder<C> withRestrictions(GlobalRestriction... restrictions) {
        for (GlobalRestriction restriction : restrictions) {
            if (restriction == null)
                continue;
            this.restrictions.add(restriction);
        }
        return this;
    }

    public CliBuilder<C> withNoRestrictions() {
        restrictions.clear();
        restrictions.add(new None());
        return this;
    }

    public CliBuilder<C> withDefaultRestrictions() {
        restrictions.addAll(Arrays.asList(GlobalRestriction.DEFAULTS));
        return this;
    }

    public CliBuilder<C> withOnlyDefaultRestrictions() {
        restrictions.clear();
        return withDefaultRestrictions();
    }

    public ParserBuilder<C> withParser() {
        return parserBuilder;
    }

    public CliBuilder<C> withHelpSection(HelpSection section) {
        if (section == null)
            return this;
        baseHelpSections.put(section.getTitle().toLowerCase(Locale.ENGLISH), section);
        return this;
    }

    @Override
    public CommandLineInterface<C> build() {
        CommandMetadata defaultCommandMetadata = null;
        List<CommandMetadata> allCommands = new ArrayList<CommandMetadata>();
        if (defaultCommand != null) {
            defaultCommandMetadata = MetadataLoader.loadCommand(defaultCommand, baseHelpSections);
        }

        List<CommandMetadata> defaultCommandGroup = defaultCommandGroupCommands != null
                ? MetadataLoader.loadCommands(defaultCommandGroupCommands, baseHelpSections)
                : new ArrayList<CommandMetadata>();

        allCommands.addAll(defaultCommandGroup);
        if (defaultCommandMetadata != null)
            allCommands.add(defaultCommandMetadata);

        // Build groups
        List<CommandGroupMetadata> commandGroups;
        if (groups != null) {
            commandGroups = new ArrayList<CommandGroupMetadata>();
            for (GroupBuilder<C> groupBuilder : groups.values()) {
                commandGroups.add(groupBuilder.build());
            }
        } else {
            commandGroups = new ArrayList<>();
        }

        // Find all commands registered in groups and sub-groups, we use this to
        // check this is a valid CLI with at least 1 command
        for (CommandGroupMetadata group : commandGroups) {
            allCommands.addAll(group.getCommands());
            if (group.getDefaultCommand() != null)
                allCommands.add(group.getDefaultCommand());

            // Make sure to scan sub-groups
            Queue<CommandGroupMetadata> subGroups = new LinkedList<CommandGroupMetadata>();
            subGroups.addAll(group.getSubGroups());
            while (!subGroups.isEmpty()) {
                CommandGroupMetadata subGroup = subGroups.poll();
                allCommands.addAll(subGroup.getCommands());
                if (subGroup.getDefaultCommand() != null)
                    allCommands.add(subGroup.getDefaultCommand());
                subGroups.addAll(subGroup.getSubGroups());
            }
        }

        // add commands to groups based on the value of groups in the @Command
        // annotations
        // rather than change the entire way metadata is loaded, I figured just
        // post-processing was an easier, yet uglier, way to go
        MetadataLoader.loadCommandsIntoGroupsByAnnotation(allCommands, commandGroups, defaultCommandGroup,
                baseHelpSections);

        // Build restrictions
        // Use defaults if none specified
        if (restrictions.size() == 0)
            withDefaultRestrictions();

        if (allCommands.size() == 0)
            throw new IllegalArgumentException("Must specify at least one command to create a CLI");

        // Build metadata objects
        GlobalMetadata<C> metadata = MetadataLoader.<C> loadGlobal(name, description, defaultCommandMetadata,
                ListUtils.unmodifiableList(defaultCommandGroup), ListUtils.unmodifiableList(commandGroups),
                ListUtils.unmodifiableList(restrictions), Collections.unmodifiableCollection(baseHelpSections.values()),
                this.parserBuilder.build());

        return new CommandLineInterface<C>(metadata);
    }
}
