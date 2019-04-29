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
package com.github.rvesse.airline.help.markdown;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.github.rvesse.airline.help.UsageHelper;
import com.github.rvesse.airline.help.common.AbstractPrintedCommandUsageGenerator;
import com.github.rvesse.airline.help.common.AbstractPrintedGlobalUsageGenerator;
import com.github.rvesse.airline.help.sections.HelpHint;
import com.github.rvesse.airline.help.sections.HelpSection;
import com.github.rvesse.airline.io.printers.UsagePrinter;
import com.github.rvesse.airline.model.CommandGroupMetadata;
import com.github.rvesse.airline.model.CommandMetadata;
import com.github.rvesse.airline.model.GlobalMetadata;
import com.github.rvesse.airline.model.OptionMetadata;
import com.github.rvesse.airline.parser.aliases.UserAliasesSource;
import com.github.rvesse.airline.utils.AirlineUtils;

import static com.github.rvesse.airline.help.UsageHelper.DEFAULT_OPTION_COMPARATOR;

public class MarkdownGlobalUsageGenerator<T> extends AbstractPrintedGlobalUsageGenerator<T> {

    private final MarkdownUsageHelper helper;
    protected final AbstractPrintedCommandUsageGenerator commandUsageGenerator;

    public MarkdownGlobalUsageGenerator() {
        this(DEFAULT_COLUMNS, UsageHelper.DEFAULT_HINT_COMPARATOR, UsageHelper.DEFAULT_OPTION_COMPARATOR,
                UsageHelper.DEFAULT_COMMAND_COMPARATOR, UsageHelper.DEFAULT_COMMAND_GROUP_COMPARATOR, false,
                new MarkdownCommandUsageGenerator(false));
    }

    public MarkdownGlobalUsageGenerator(boolean includeHidden) {
        this(DEFAULT_COLUMNS, UsageHelper.DEFAULT_HINT_COMPARATOR, UsageHelper.DEFAULT_OPTION_COMPARATOR,
                UsageHelper.DEFAULT_COMMAND_COMPARATOR, UsageHelper.DEFAULT_COMMAND_GROUP_COMPARATOR, includeHidden,
                new MarkdownCommandUsageGenerator(includeHidden));
    }

    public MarkdownGlobalUsageGenerator(int columns) {
        this(columns, UsageHelper.DEFAULT_HINT_COMPARATOR, DEFAULT_OPTION_COMPARATOR,
                UsageHelper.DEFAULT_COMMAND_COMPARATOR, UsageHelper.DEFAULT_COMMAND_GROUP_COMPARATOR, false,
                new MarkdownCommandUsageGenerator(columns));
    }

    public MarkdownGlobalUsageGenerator(int columns, boolean includeHidden) {
        this(columns, UsageHelper.DEFAULT_HINT_COMPARATOR, DEFAULT_OPTION_COMPARATOR,
                UsageHelper.DEFAULT_COMMAND_COMPARATOR, UsageHelper.DEFAULT_COMMAND_GROUP_COMPARATOR, includeHidden,
                new MarkdownCommandUsageGenerator(columns, includeHidden));
    }

    public MarkdownGlobalUsageGenerator(int columnSize, Comparator<? super HelpHint> hintComparator,
            Comparator<? super OptionMetadata> optionComparator, Comparator<? super CommandMetadata> commandComparator,
            Comparator<? super CommandGroupMetadata> commandGroupComparator, boolean includeHidden,
            AbstractPrintedCommandUsageGenerator commandUsageGenerator) {
        super(columnSize, hintComparator, optionComparator, commandComparator, commandGroupComparator, includeHidden);
        helper = createHelper(optionComparator, includeHidden);
        this.commandUsageGenerator = commandUsageGenerator;
    }

    protected MarkdownUsageHelper createHelper(Comparator<? super OptionMetadata> optionComparator,
            boolean includeHidden) {
        return new MarkdownUsageHelper(optionComparator, includeHidden);
    }

    @Override
    protected void usage(GlobalMetadata<T> global, UsagePrinter out) throws IOException {
        // Name and description
        outputDescription(out, global);

        // Find the help sections
        List<HelpSection> preSections = new ArrayList<HelpSection>();
        List<HelpSection> postSections = new ArrayList<HelpSection>();
        findHelpSections(global, preSections, postSections);

        // Output pre help sections
        for (HelpSection section : preSections) {
            helper.outputHelpSection(out, section);
        }

        // Synopsis
        outputSynopsis(out, global);

        // Options
        List<OptionMetadata> options = sortOptions(global.getOptions());
        if (options.size() > 0) {
            helper.outputOptions(out, options);
        }

        // Commands and Command Groups
        out.append("# COMMANDS").newline().newline();
        outputCommandList(out, global);
        outputCommandUsages(out, global);

        // Aliases
        if (global.getParserConfiguration().getUserAliasesSource() != null) {
            outputUserAliases(out, global, global.getParserConfiguration().getUserAliasesSource());
        }

        // Output post help sections
        for (HelpSection section : postSections) {
            helper.outputHelpSection(out, section);
        }
    }

    /**
     * Outputs a documentation section listing the commands
     * 
     * @param out
     *            Usage printer
     * @param global
     *            Global meta-data
     * @throws IOException
     *             Thrown if there is a problem generating usage output
     */
    protected void outputCommandList(UsagePrinter out, GlobalMetadata<T> global) throws IOException {
        for (CommandMetadata command : sortCommands(global.getDefaultGroupCommands())) {
            outputCommandDescription(out, null, command);
        }

        outputGroupCommandsList(out, global, global.getCommandGroups());
    }

    protected void outputGroupCommandsList(UsagePrinter out, GlobalMetadata<T> global,
            List<CommandGroupMetadata> groups) throws IOException {
        if (groups.size() == 0)
            return;

        for (CommandGroupMetadata group : sortCommandGroups(groups)) {
            if (group.isHidden() && !this.includeHidden())
                continue;

            for (CommandMetadata command : sortCommands(group.getCommands())) {
                outputCommandDescription(out, group, command);
            }

            if (group.getSubGroups().size() > 0) {
                UsagePrinter subGroupPrinter = out.newIndentedPrinter(4);
                outputGroupCommandsList(subGroupPrinter, global, group.getSubGroups());
            }
        }
    }

    /**
     * Outputs a documentation section with a synopsis of CLI usage
     * 
     * @param out
     *            Usage printer
     * @param global
     *            Global meta-data
     * 
     * @throws IOException
     *             Thrown if there is a problem generating usage output
     */
    protected void outputSynopsis(UsagePrinter out, GlobalMetadata<T> global) throws IOException {
        out.append("# SYNOPSIS").newline().newline();

        out.append(String.format("`%s`", global.getName()));
        helper.outputOptionsSynopsis(out, global.getOptions());

        if (global.getCommandGroups().size() > 0) {
            if (global.getDefaultGroupCommands().size() > 0)
                out.append("[");
            out.append("*group*");
            if (global.getDefaultGroupCommands().size() > 0)
                out.append("]");
        }

        out.append("*command* [ *command-args* ]").newline().newline();
    }

    /**
     * Outputs a documentation section with a description of the CLI
     * 
     * @param out
     *            Usage printer
     * @param global
     *            Global meta-data
     * @throws IOException
     *             Thrown if there is a problem generating usage output
     */
    protected void outputDescription(UsagePrinter out, GlobalMetadata<T> global) throws IOException {
        out.append("# NAME").newline().newline();

        out.append(String.format("`%s`", global.getName())).append(" -").append(global.getDescription()).newline()
                .newline();
    }

    /**
     * Outputs the description for a command
     * 
     * @param out
     *            Usage printer
     * @param group
     *            Group meta-data
     * @param command
     *            Command meta-data
     * @throws IOException
     *             Thrown if there is a problem generating usage output
     */
    protected void outputCommandDescription(UsagePrinter out, CommandGroupMetadata group, CommandMetadata command)
            throws IOException {
        if (!command.isHidden() || this.includeHidden()) {
            // New bullet point
            out.append(" - ");
            out = out.newIndentedPrinter(2);

            // Build name and wrap in backticks
            StringBuilder name = new StringBuilder();
            name.append('`');
            if (group != null) {
                name.append(group.getName()).append(' ');
            }
            name.append(command.getName());
            name.append('`');
            out.append(name.toString()).newline();

            if (command.getDescription() != null) {
                out.newline().append(command.getDescription()).newline();
            }
            out.newline();
        }
    }

    protected void outputUserAliases(UsagePrinter out, GlobalMetadata<T> global, UserAliasesSource<T> userAliases)
            throws IOException {
        if (userAliases == null)
            return;

        out.append("# USER DEFINED ALIASES").newline().newline();

        out.append(String.format(
                "This CLI supports user defined aliases which may be placed in a %s file located in %s the following location(s):",
                userAliases.getFilename(), userAliases.getSearchLocations().size() > 1 ? "one/more of" : "")).newline()
                .newline();

        for (String location : userAliases.getSearchLocations()) {
            out.appendOnOneLine(String.format("1. `%s`", location)).newline();
        }
        out.newline();

        out.newline();
        if (userAliases.getSearchLocations().size() > 1) {
            out.append(
                    "Where the file exists in multiple locations then the files are merged together with the earlier locations taking precedence.")
                    .newline().newline();
        }
        out.append("This file contains aliases defined in Java properties file style e.g.").newline().newline();

        UsagePrinter examplePrinter = out.newIndentedPrinter(4);
        examplePrinter
                .append(String.format("%sfoo=bar --flag",
                        StringUtils.isNotBlank(userAliases.getPrefix()) ? userAliases.getPrefix() : ""))
                .newline().newline();
        examplePrinter.flush();

        out.append(
                "Here an alias foo is defined which causes the bar command to be invoked with the `--flag` option passed to it.");
        if (StringUtils.isNotBlank(userAliases.getPrefix())) {
            out.append("Aliases are distinguished from other properties in the file by the prefix `"
                    + userAliases.getPrefix() + "` as seen in the example.").newline();
        }
        out.newline();
        out.append("Alias definitions are subject to the following conditions:").newline().newline();

        UsagePrinter restrictionsPrinter = out.newIndentedPrinter(2);
        if (global.getParserConfiguration().aliasesOverrideBuiltIns()) {
            restrictionsPrinter.append("- Aliases may override existing commands");
        } else {
            restrictionsPrinter.append("- Aliases cannot override existing commands");
        }
        restrictionsPrinter.newline();
        if (global.getParserConfiguration().aliasesMayChain()) {
            restrictionsPrinter.append(
                    "- Aliases may be defined in terms of other aliases provided circular references are not created");
        } else {
            restrictionsPrinter.append("- Aliases cannot be defined in terms of other aliases");
        }
        restrictionsPrinter.newline();
        restrictionsPrinter.flush();
    }

    /**
     * Outputs the command usages for all groups
     * 
     * @param printer
     *            Usage printer
     * @param global
     *            Global meta-data
     * 
     * @throws IOException
     *             Thrown if there is a problem generating usage output
     */
    protected void outputCommandUsages(UsagePrinter printer, GlobalMetadata<T> global) throws IOException {
        // Default group usages
        outputDefaultGroupCommandUsages(printer, global);

        // Other group usages
        for (CommandGroupMetadata group : sortCommandGroups(global.getCommandGroups())) {
            if (group.isHidden() && !this.includeHidden())
                continue;

            List<CommandGroupMetadata> groupPath = new ArrayList<CommandGroupMetadata>();
            groupPath.add(group);
            outputGroupCommandUsages(printer, global, groupPath);
        }
    }

    /**
     * Outputs the command usages for the commands in the default group
     * 
     * @param printer
     *            Usage printer
     * @param global
     *            Global meta-data
     * 
     * @throws IOException
     *             Thrown if there is a problem generating usage output
     */
    protected void outputDefaultGroupCommandUsages(UsagePrinter printer, GlobalMetadata<T> global) throws IOException {
        for (CommandMetadata command : sortCommands(global.getDefaultGroupCommands())) {
            if (command.isHidden() && !this.includeHidden())
                continue;

            // Horizontal rule
            printer.append("---").newline().newline();

            printer.flush();
            commandUsageGenerator.usage(global.getName(), (String[]) null, command.getName(), command,
                    global.getParserConfiguration(), printer);
        }
    }

    /**
     * Outputs the command usages for the commands in the given group
     * 
     * @param printer
     *            Usage printer
     * @param global
     *            Global Meta-data
     * @param groups
     *            Groups Meta-data
     * 
     * @throws IOException
     *             Thrown if there is a problem generating usage output
     */
    protected void outputGroupCommandUsages(UsagePrinter printer, GlobalMetadata<T> global,
            List<CommandGroupMetadata> groups) throws IOException {
        CommandGroupMetadata group = groups.get(groups.size() - 1);

        // Commands in the group
        for (CommandMetadata command : sortCommands(group.getCommands())) {
            if (command.isHidden() && !this.includeHidden())
                continue;

            // Horizontal rule
            printer.append("---").newline().newline();

            printer.flush();
            commandUsageGenerator.usage(global.getName(), UsageHelper.toGroupNames(groups), command.getName(), command,
                    global.getParserConfiguration(), printer);
        }

        // Sub-groups
        for (CommandGroupMetadata subGroup : sortCommandGroups(group.getSubGroups())) {
            if (subGroup.isHidden() && !this.includeHidden())
                continue;

            List<CommandGroupMetadata> subGroupPath = AirlineUtils.listCopy(groups);
            subGroupPath.add(subGroup);
            outputGroupCommandUsages(printer, global, subGroupPath);
        }
    }
}
