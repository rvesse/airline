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
package com.github.rvesse.airline.help.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import com.github.rvesse.airline.utils.ListUtils;
import com.github.rvesse.airline.utils.StringUtils;

import com.github.rvesse.airline.help.UsageHelper;
import com.github.rvesse.airline.help.sections.HelpHint;
import com.github.rvesse.airline.model.ArgumentsMetadata;
import com.github.rvesse.airline.model.CommandMetadata;
import com.github.rvesse.airline.model.OptionMetadata;
import com.github.rvesse.airline.restrictions.ArgumentsRestriction;
import com.github.rvesse.airline.restrictions.OptionRestriction;

public class AbstractUsageGenerator {

    protected static final int DEFAULT_COLUMNS = 79;
    private final Comparator<? super HelpHint> hintComparator;
    private final Comparator<? super OptionMetadata> optionComparator;
    private final Comparator<? super CommandMetadata> commandComparator;
    private final boolean includeHidden;

    public AbstractUsageGenerator() {
        this(UsageHelper.DEFAULT_HINT_COMPARATOR, UsageHelper.DEFAULT_OPTION_COMPARATOR,
                UsageHelper.DEFAULT_COMMAND_COMPARATOR, false);
    }

    public AbstractUsageGenerator(Comparator<? super HelpHint> hintComparator,
            Comparator<? super OptionMetadata> optionComparator, Comparator<? super CommandMetadata> commandComparator,
            boolean includeHidden) {
        this.hintComparator = hintComparator;
        this.optionComparator = optionComparator;
        this.commandComparator = commandComparator;
        this.includeHidden = includeHidden;
    }

    /**
     * Gets whether hidden commands and options should be included in the output
     * 
     * @return True if hidden commands/options should be included
     */
    protected boolean includeHidden() {
        return this.includeHidden;
    }

    protected final Comparator<? super OptionMetadata> getOptionComparator() {
        return this.optionComparator;
    }

    protected final Comparator<? super CommandMetadata> getCommandComparator() {
        return this.commandComparator;
    }

    /**
     * Sorts the options assuming a non-null comparator was provided at
     * instantiation time
     * 
     * @param options
     *            Options
     * @return Sorted options
     */
    protected List<OptionMetadata> sortOptions(List<OptionMetadata> options) {
        if (optionComparator != null) {
            options = new ArrayList<OptionMetadata>(options);
            Collections.sort(options, optionComparator);
        }
        return options;
    }

    protected List<HelpHint> sortOptionRestrictions(List<OptionRestriction> restrictions) {
        List<HelpHint> hints = new ArrayList<>();
        for (OptionRestriction restriction : restrictions) {
            if (restriction instanceof HelpHint) {
                hints.add((HelpHint)restriction);
            }
        }
        if (hintComparator != null) {
            Collections.sort(hints, hintComparator);
        }
        return hints;
    }
    
    protected List<HelpHint> sortArgumentsRestrictions(List<ArgumentsRestriction> restrictions) {
        List<HelpHint> hints = new ArrayList<>();
        for (ArgumentsRestriction restriction : restrictions) {
            if (restriction instanceof HelpHint) {
                hints.add((HelpHint)restriction);
            }
        }
        if (hintComparator != null) {
            Collections.sort(hints, hintComparator);
        }
        return hints;
    }

    /**
     * Sorts the commands assuming a non-null comparator was provided at
     * instantiation time
     * 
     * @param commands
     *            Commands
     * @return Sorted commands
     */
    protected List<CommandMetadata> sortCommands(List<CommandMetadata> commands) {
        if (commandComparator != null) {
            commands = new ArrayList<>(commands);
            Collections.sort(commands, commandComparator);
        }
        return commands;
    }

    /**
     * HTMLizes a string i.e. escapes HTML special characters into HTML entities
     * and new lines into HTML line breaks
     * 
     * @param value
     *            String to HTMLize
     * @return HTMLized string
     */
    protected final String htmlize(final String value) {
        if (StringUtils.isEmpty(value))
            return "";
        return value.replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\n", "<br/>");
    }

    /**
     * Converts a command into the default command representation for the usage
     * documentation
     * 
     * @param command
     *            Default command name
     * @return
     */
    protected String toDefaultCommand(String command) {
        if (StringUtils.isEmpty(command)) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[ ");
        stringBuilder.append(command);
        stringBuilder.append(" ]");

        return stringBuilder.toString();
    }

    /**
     * Converts the options into their synopsis representation for the usage
     * documentation
     * 
     * @param options
     *            Options
     * @return
     */
    protected List<String> toSynopsisUsage(List<OptionMetadata> options) {
        List<String> synopsisOptions = new ArrayList<String>();
        for (OptionMetadata option : options) {
            if (option.isHidden() && !includeHidden)
                continue;
            synopsisOptions.add(toUsage(option));
        }
        return ListUtils.unmodifiableList(synopsisOptions);
    }

    protected String toUsage(ArgumentsMetadata arguments) {
        boolean required = arguments.isRequired();
        StringBuilder stringBuilder = new StringBuilder();
        if (!required) {
            // TODO: be able to handle required arguments individually, like
            // arity for the options
            stringBuilder.append("[ ");
        }

        stringBuilder.append(toDescription(arguments));

        if (arguments.isMultiValued()) {
            stringBuilder.append("...");
        }

        if (!required) {
            stringBuilder.append(" ]");
        }
        return stringBuilder.toString();
    }

    protected String toUsage(OptionMetadata option) {
        Set<String> options = option.getOptions();
        boolean required = option.isRequired();
        StringBuilder stringBuilder = new StringBuilder();
        if (!required) {
            stringBuilder.append("[ ");
        }

        if (options.size() > 1) {
            stringBuilder.append('{');
        }

        final String argumentString;
        if (option.getArity() > 0) {
            argumentString = String.format("<%s>", option.getTitle());
        } else {
            argumentString = null;
        }

        boolean first = true;
        for (String name : options) {
            if (!first) {
                stringBuilder.append(" | ");
            } else {
                first = false;
            }
            stringBuilder.append(name);
        }

        if (options.size() > 1) {
            stringBuilder.append('}');
        }

        if (argumentString != null) {
            stringBuilder.append(" " + argumentString);
        }

        if (option.isMultiValued()) {
            stringBuilder.append("...");
        }

        if (!required) {
            stringBuilder.append(" ]");
        }
        return stringBuilder.toString();
    }

    protected String toDescription(ArgumentsMetadata arguments) {
        List<String> descriptionTitles = arguments.getTitle();
        StringBuilder stringBuilder = new StringBuilder();
        for (String title : descriptionTitles) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append(" ");
            }
            stringBuilder.append("<");
            stringBuilder.append(title);
            stringBuilder.append(">");
        }

        return stringBuilder.toString();
    }

    protected String toDescription(OptionMetadata option) {
        Set<String> options = option.getOptions();
        StringBuilder stringBuilder = new StringBuilder();

        final String argumentString;
        if (option.getArity() > 0) {
            argumentString = String.format("<%s>", option.getTitle());
        } else {
            argumentString = null;
        }

        boolean first = true;
        for (String name : options) {
            if (!first) {
                stringBuilder.append(", ");
            } else {
                first = false;
            }
            stringBuilder.append(name);
            if (argumentString != null) {
                for (int i = 0; i < option.getArity(); i++) {
                    stringBuilder.append(' ').append(argumentString);
                }
            }
        }

        return stringBuilder.toString();
    }
}