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

import java.util.List;

import com.github.rvesse.airline.utils.StringUtils;

import com.github.rvesse.airline.builder.ParserBuilder;
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

    public GlobalMetadata(String name, String description, Iterable<OptionMetadata> options,
            CommandMetadata defaultCommand, Iterable<CommandMetadata> defaultGroupCommands,
            Iterable<CommandGroupMetadata> commandGroups, Iterable<GlobalRestriction> restrictions,
            ParserMetadata<T> parserConfig) {
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
        this.parserConfig = parserConfig != null ? parserConfig : ParserBuilder.<T>defaultConfiguration();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<OptionMetadata> getOptions() {
        return options;
    }

    public CommandMetadata getDefaultCommand() {
        return defaultCommand;
    }

    public List<CommandMetadata> getDefaultGroupCommands() {
        return defaultGroupCommands;
    }

    public List<CommandGroupMetadata> getCommandGroups() {
        return commandGroups;
    }
    
    public List<GlobalRestriction> getRestrictions() {
        return restrictions;
    }

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
