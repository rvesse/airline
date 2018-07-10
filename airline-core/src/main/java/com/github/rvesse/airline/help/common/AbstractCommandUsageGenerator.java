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

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;

import com.github.rvesse.airline.Channels;
import com.github.rvesse.airline.help.CommandUsageGenerator;
import com.github.rvesse.airline.help.UsageHelper;
import com.github.rvesse.airline.help.sections.HelpHint;
import com.github.rvesse.airline.help.sections.HelpSection;
import com.github.rvesse.airline.model.CommandMetadata;
import com.github.rvesse.airline.model.OptionMetadata;
import com.github.rvesse.airline.model.ParserMetadata;
import com.github.rvesse.airline.utils.comparators.HelpSectionComparator;

/**
 * Abstract command usage generator
 *
 */
public abstract class AbstractCommandUsageGenerator extends AbstractUsageGenerator implements CommandUsageGenerator {

    private final Comparator<? super Entry<Integer, String>> exitCodeComparator;

    public AbstractCommandUsageGenerator() {
        this(UsageHelper.DEFAULT_OPTION_COMPARATOR);
    }

    public AbstractCommandUsageGenerator(boolean includeHidden) {
        this(UsageHelper.DEFAULT_HINT_COMPARATOR, UsageHelper.DEFAULT_OPTION_COMPARATOR,
                UsageHelper.DEFAULT_EXIT_CODE_COMPARATOR, includeHidden);
    }

    public AbstractCommandUsageGenerator(Comparator<? super OptionMetadata> optionComparator) {
        this(UsageHelper.DEFAULT_HINT_COMPARATOR, optionComparator, UsageHelper.DEFAULT_EXIT_CODE_COMPARATOR, false);
    }

    public AbstractCommandUsageGenerator(Comparator<? super OptionMetadata> optionComparator, boolean includeHidden) {
        this(UsageHelper.DEFAULT_HINT_COMPARATOR, optionComparator, UsageHelper.DEFAULT_EXIT_CODE_COMPARATOR,
                includeHidden);
    }

    public AbstractCommandUsageGenerator(Comparator<? super HelpHint> hintComparator,
            Comparator<? super OptionMetadata> optionComparator,
            Comparator<? super Entry<Integer, String>> exitCodeComparator, boolean includeHidden) {
        super(hintComparator, optionComparator, UsageHelper.DEFAULT_COMMAND_COMPARATOR, includeHidden);
        this.exitCodeComparator = exitCodeComparator;
    }

    @Override
    public <T> void usage(String programName, String[] groupNames, String commandName, CommandMetadata command,
            ParserMetadata<T> parserConfig) throws IOException {
        usage(programName, groupNames, commandName, command, parserConfig, Channels.output());
    }

    /**
     * @deprecated Please use the overload that explicitly takes a parser
     *             configuration
     */
    @Override
    @Deprecated
    public void usage(String programName, String[] groupNames, String commandName, CommandMetadata command)
            throws IOException {
        usage(programName, groupNames, commandName, command, null, Channels.output());
    }

    /**
     * @deprecated Please use the overload that explicitly takes a parser
     *             configuration
     */
    @Override
    @Deprecated
    public void usage(String programName, String[] groupNames, String commandName, CommandMetadata command,
            OutputStream output) throws IOException {
        usage(programName, groupNames, commandName, command, null, output);
    }

    /**
     * Sorts the exit codes assuming a non-null comparator was provided at
     * instantiation time
     *
     * @param exitCodes
     *            Exit codes
     * @return Sorted exit codes
     */
    protected List<Entry<Integer, String>> sortExitCodes(List<Entry<Integer, String>> exitCodes) {
        if (exitCodeComparator != null) {
            exitCodes = new ArrayList<>(exitCodes);
            Collections.sort(exitCodes, exitCodeComparator);
        }
        return exitCodes;
    }

    /**
     * Finds the help sections
     *
     * @param command
     *            Command meta-data
     * @param preSections
     *            Sections that should be placed before base content
     * @param postSections
     *            Sections that should be placed after base content
     */
    protected void findHelpSections(CommandMetadata command, List<HelpSection> preSections,
            List<HelpSection> postSections) {
        for (HelpSection section : command.getHelpSections()) {
            if (section.suggestedOrder() < 0) {
                preSections.add(section);
            } else {
                postSections.add(section);
            }
        }
        HelpSectionComparator comparator = new HelpSectionComparator();
        Collections.sort(preSections, comparator);
        Collections.sort(postSections, comparator);
    }
}
