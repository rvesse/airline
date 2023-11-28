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
package com.github.rvesse.airline.help.cli;

import com.github.rvesse.airline.help.UsageHelper;
import com.github.rvesse.airline.help.common.AbstractPrintedGlobalUsageGenerator;
import com.github.rvesse.airline.help.sections.HelpHint;
import com.github.rvesse.airline.help.sections.HelpSection;
import com.github.rvesse.airline.io.printers.UsagePrinter;
import com.github.rvesse.airline.model.CommandGroupMetadata;
import com.github.rvesse.airline.model.CommandMetadata;
import com.github.rvesse.airline.model.GlobalMetadata;
import com.github.rvesse.airline.model.OptionMetadata;
import com.github.rvesse.airline.utils.predicates.parser.CommandFinder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections4.IterableUtils;

public class CliGlobalUsageSummaryGenerator<T> extends AbstractPrintedGlobalUsageGenerator<T> {
    
    private final CliUsageHelper helper;

    public CliGlobalUsageSummaryGenerator() {
        this(DEFAULT_COLUMNS, UsageHelper.DEFAULT_HINT_COMPARATOR, UsageHelper.DEFAULT_OPTION_COMPARATOR,
                UsageHelper.DEFAULT_COMMAND_COMPARATOR, UsageHelper.DEFAULT_COMMAND_GROUP_COMPARATOR, false);
    }

    public CliGlobalUsageSummaryGenerator(boolean includeHidden) {
        this(DEFAULT_COLUMNS, UsageHelper.DEFAULT_HINT_COMPARATOR, UsageHelper.DEFAULT_OPTION_COMPARATOR,
                UsageHelper.DEFAULT_COMMAND_COMPARATOR, UsageHelper.DEFAULT_COMMAND_GROUP_COMPARATOR, includeHidden);
    }

    public CliGlobalUsageSummaryGenerator(int columnSize) {
        this(columnSize, UsageHelper.DEFAULT_HINT_COMPARATOR, UsageHelper.DEFAULT_OPTION_COMPARATOR,
                UsageHelper.DEFAULT_COMMAND_COMPARATOR, UsageHelper.DEFAULT_COMMAND_GROUP_COMPARATOR, false);
    }

    public CliGlobalUsageSummaryGenerator(int columnSize, boolean includeHidden) {
        this(columnSize, UsageHelper.DEFAULT_HINT_COMPARATOR, UsageHelper.DEFAULT_OPTION_COMPARATOR,
                UsageHelper.DEFAULT_COMMAND_COMPARATOR, UsageHelper.DEFAULT_COMMAND_GROUP_COMPARATOR, includeHidden);
    }

    public CliGlobalUsageSummaryGenerator(int columnSize, Comparator<? super HelpHint> hintComparator,
            Comparator<? super OptionMetadata> optionComparator, Comparator<? super CommandMetadata> commandComparator,
            Comparator<? super CommandGroupMetadata> commandGroupComparator, boolean includeHidden) {
        super(columnSize, hintComparator, optionComparator, commandComparator, commandGroupComparator, includeHidden);
        helper = createHelper(optionComparator, includeHidden);
    }

    protected CliUsageHelper createHelper(Comparator<? super OptionMetadata> optionComparator, boolean includeHidden) {
        return new CliUsageHelper(optionComparator, includeHidden);
    }

    public void usage(GlobalMetadata<T> global, UsagePrinter out) throws IOException {
        // Synopsis
        outputSynopsis(out, global);

        // Command List
        outputCommandList(out, global);

        // Notes on how to get more help
        outputFooter(out, global);
               
        // Find the help sections
        List<HelpSection> preSections = new ArrayList<HelpSection>();
        List<HelpSection> postSections = new ArrayList<HelpSection>();
        findHelpSections(global, preSections, postSections);
        
        if (preSections.size() > 0 || postSections.size() > 0) {
            out.newline();
        }

        // Output help sections
        for (HelpSection section : preSections) {
            helper.outputHelpSection(out, section);
        }
        for (HelpSection section : postSections) {
            helper.outputHelpSection(out, section);
        }
    }

    /**
     * Outputs a documentation section detailing how to get more help
     * 
     * @param out
     *            Usage printer
     * @param global
     *            Global meta-data
     * 
     * @throws IOException
     */
    protected void outputFooter(UsagePrinter out, GlobalMetadata<T> global) throws IOException {
        boolean hasHelpCommand = IterableUtils.find(global.getDefaultGroupCommands(), new CommandFinder("help")) != null;

        if (hasHelpCommand) {
            out.newline();
            out.append("See").append("'" + global.getName())
                    .append("help <command>' for more information on a specific command.").newline();
        }
    }

    /**
     * Outputs a documentation section listing the common commands and groups
     * 
     * @param out
     *            Usage printer
     * @param global
     *            Global meta-data
     * @throws IOException
     */
    protected void outputCommandList(UsagePrinter out, GlobalMetadata<T> global) throws IOException {
        Map<String, String> commands = new LinkedHashMap<>();
        for (CommandMetadata commandMetadata : sortCommands(global.getDefaultGroupCommands())) {
            if (!commandMetadata.isHidden() || this.includeHidden()) {
                commands.put(commandMetadata.getName(), commandMetadata.getDescription());
            }
        }
        for (CommandGroupMetadata group : sortCommandGroups(global.getCommandGroups())) {
            if (group.isHidden() && !this.includeHidden())
                continue;

            commands.put(group.getName(), group.getDescription());
        }

        out.append("Commands are:").newline();
        List<Iterable<String>> commandDetails = new ArrayList<Iterable<String>>();
        for (Entry<String, String> details : commands.entrySet()) {
            List<String> data = new ArrayList<String>();
            data.add(details.getKey());
            data.add(details.getValue());
            commandDetails.add(data);
        }
        out.newIndentedPrinter(4).appendTable(commandDetails, 0);
    }

    /**
     * Outputs a documentation section with a brief synopsis of usage
     * 
     * @param out
     *            Usage printer
     * @param global
     *            Global meta-data
     * @throws IOException
     */
    protected void outputSynopsis(UsagePrinter out, GlobalMetadata<T> global) throws IOException {
        List<String> commandArguments = new ArrayList<>();
        for (OptionMetadata option : sortOptions(global.getOptions())) {
            if (option.isHidden() && !includeHidden())
                continue;

            commandArguments.add(toUsage(option));
        }
        //@formatter:off
        out.newPrinterWithHangingIndent(8)
           .append("usage:")
           .append(global.getName())
           .appendWords(commandArguments)
           .append("<command> [ <args> ]")
           .newline()
           .newline();
        //@formatter:on
    }
}
