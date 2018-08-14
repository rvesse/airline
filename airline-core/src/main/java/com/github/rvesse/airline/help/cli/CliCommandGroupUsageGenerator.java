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

import java.io.IOException;
import java.util.*;

import com.github.rvesse.airline.help.UsageHelper;
import com.github.rvesse.airline.help.common.AbstractPrintedCommandGroupUsageGenerator;
import com.github.rvesse.airline.help.sections.HelpHint;
import com.github.rvesse.airline.io.printers.UsagePrinter;
import com.github.rvesse.airline.model.CommandGroupMetadata;
import com.github.rvesse.airline.model.CommandMetadata;
import com.github.rvesse.airline.model.GlobalMetadata;
import com.github.rvesse.airline.model.OptionMetadata;
import static com.github.rvesse.airline.help.UsageHelper.DEFAULT_COMMAND_COMPARATOR;
import static com.github.rvesse.airline.help.UsageHelper.DEFAULT_OPTION_COMPARATOR;
import static com.github.rvesse.airline.help.UsageHelper.DEFAULT_HINT_COMPARATOR;

public class CliCommandGroupUsageGenerator<T> extends AbstractPrintedCommandGroupUsageGenerator<T> {
    private final boolean hideGlobalOptions;

    public CliCommandGroupUsageGenerator() {
        this(DEFAULT_COLUMNS, false, DEFAULT_HINT_COMPARATOR, DEFAULT_OPTION_COMPARATOR, DEFAULT_COMMAND_COMPARATOR,
                false);
    }

    public CliCommandGroupUsageGenerator(boolean includeHidden) {
        this(DEFAULT_COLUMNS, false, DEFAULT_HINT_COMPARATOR, DEFAULT_OPTION_COMPARATOR, DEFAULT_COMMAND_COMPARATOR,
                includeHidden);
    }

    public CliCommandGroupUsageGenerator(int columns) {
        this(columns, false, DEFAULT_HINT_COMPARATOR, DEFAULT_OPTION_COMPARATOR, DEFAULT_COMMAND_COMPARATOR, false);
    }

    public CliCommandGroupUsageGenerator(int columns, boolean includeHidden) {
        this(columns, false, DEFAULT_HINT_COMPARATOR, DEFAULT_OPTION_COMPARATOR, DEFAULT_COMMAND_COMPARATOR,
                includeHidden);
    }

    public CliCommandGroupUsageGenerator(int columns, boolean hideGlobalOptions,
            Comparator<? super HelpHint> hintComparator, Comparator<? super OptionMetadata> optionComparator,
            Comparator<? super CommandMetadata> commandComparator, boolean includeHidden) {
        super(columns, hintComparator, optionComparator, commandComparator, includeHidden);
        this.hideGlobalOptions = hideGlobalOptions;
    }

    @Override
    protected void usage(GlobalMetadata<T> global, CommandGroupMetadata[] groups, UsagePrinter out) throws IOException {
        // Description and Name
        outputDescription(out, global, groups);

        //
        // SYNOPSIS
        //
        outputSynopsis(out, global, groups);

        //
        // OPTIONS
        //
        outputOptions(out, global, groups);
    }

    /**
     * Outputs a documentation section detailing the available options and their
     * usages
     * 
     * @param out
     *            Usage printer
     * @param global
     *            Global meta-data
     * @param groups
     *            Group(s) meta-data
     * 
     * @throws IOException Thrown if there is a problem generating usage output
     */
    protected void outputOptions(UsagePrinter out, GlobalMetadata<T> global, CommandGroupMetadata[] groups)
            throws IOException {
        List<OptionMetadata> options = new ArrayList<>();
        options.addAll(groups[groups.length - 1].getOptions());
        if (global != null && !hideGlobalOptions) {
            options.addAll(global.getOptions());
        }
        if (options.size() > 0) {
            options = sortOptions(options);

            out.append("OPTIONS").newline();

            for (OptionMetadata option : options) {

                if (option.isHidden() && !this.includeHidden()) {
                    continue;
                }

                // option names
                UsagePrinter optionPrinter = out.newIndentedPrinter(8);
                optionPrinter.append(toDescription(option)).newline();

                // description
                UsagePrinter descriptionPrinter = optionPrinter.newIndentedPrinter(4);
                descriptionPrinter.append(option.getDescription()).newline();

                descriptionPrinter.newline();
            }
        }
    }

    /**
     * Outputs a documentation section detailing a usage synopsis
     * 
     * @param out
     *            Usage printer
     * @param global
     *            Global meta-data
     * @param groups
     *            Groups meta-data
     * @throws IOException Thrown if there is a problem generating usage output
     */
    protected void outputSynopsis(UsagePrinter out, GlobalMetadata<T> global, CommandGroupMetadata[] groups)
            throws IOException {
        out.append("SYNOPSIS").newline();
        UsagePrinter synopsis = out.newIndentedPrinter(8).newPrinterWithHangingIndent(8);

        CommandGroupMetadata group = groups[groups.length - 1];
        List<CommandMetadata> commands = sortCommands(group.getCommands());

        // Populate group info via an extra for loop through commands
        boolean hasDefaultCommand = group.getDefaultCommand() != null;

        List<OptionMetadata> commonGroupOptions = null;
        String commonGroupArgs = null;
        List<String> allCommandNames = new ArrayList<>();
        List<String> groupNames = new ArrayList<>();
        boolean hasCommandSpecificOptions = false, hasCommandSpecificArgs = false;
        for (CommandMetadata command : commands) {
            if (group.getDefaultCommand() != null && group.getDefaultCommand().equals(command)) {
                allCommandNames.add(command.getName() + "*");
            } else {
                allCommandNames.add(command.getName());
            }
            if (commonGroupOptions == null) {
                commonGroupOptions = new ArrayList<>(command.getCommandOptions());
            }
            if (commonGroupArgs == null) {
                commonGroupArgs = (command.getArguments() != null ? toUsage(command.getArguments()) : "");
            }

            commonGroupOptions.retainAll(command.getCommandOptions());
            if (command.getCommandOptions().size() > commonGroupOptions.size()) {
                hasCommandSpecificOptions = true;
            }
            if (commonGroupArgs != (command.getArguments() != null ? toUsage(command.getArguments()) : "")) {
                hasCommandSpecificArgs = true;
            }
        }
        for (CommandGroupMetadata subGroup : group.getSubGroups()) {
            groupNames.add(subGroup.getName());
            if (commonGroupOptions == null) {
                commonGroupOptions = new ArrayList<>(subGroup.getOptions());
            }
            commonGroupOptions.retainAll(subGroup.getOptions());
        }
        // Print group summary line
        if (global != null) {
            synopsis.append(global.getName());
            if (!hideGlobalOptions && commands.size() > 0) {
                synopsis.appendWords(toSynopsisUsage(commands.get(0).getGlobalOptions()));
            }
        }
        for (int i = 0; i < groups.length; i++) {
            synopsis.append(groups[i].getName()).append(" ");
        }
        if (commands.size() > 0) {
            synopsis.appendWords(toSynopsisUsage(commands.get(0).getGroupOptions()));
        }
        synopsis.append(" {");
        if (allCommandNames.size() > 0) {
            if (groupNames.size() > 0)
                synopsis.append(" {");
            for (int i = 0; i < allCommandNames.size(); i++) {
                synopsis.append(allCommandNames.get(i));
                if (i < allCommandNames.size() - 1)
                    synopsis.append(" | ");
            }
        }
        if (groupNames.size() > 0) {
            if (allCommandNames.size() > 0)
                synopsis.append("} | {");
            for (int i = 0; i < groupNames.size(); i++) {
                synopsis.append(groupNames.get(i)).append(" <sub-command>");
                if (i < groupNames.size() - 1)
                    synopsis.append(" | ");
            }
        }
        synopsis.append("} [--]");
        if (commonGroupOptions.size() > 0) {
            synopsis.appendWords(toSynopsisUsage(commonGroupOptions));
        }
        if (hasCommandSpecificOptions) {
            synopsis.append(" [cmd-options]");
        }
        if (hasCommandSpecificArgs) {
            synopsis.append(" <cmd-args>");
        }
        synopsis.newline();
        Map<String, String> cmdOptions = new TreeMap<>();
        Map<String, String> cmdArguments = new TreeMap<>();
        Map<String, String> subGroups = new TreeMap<>();

        for (CommandGroupMetadata subGroup : group.getSubGroups()) {
            if (!subGroup.isHidden() || this.includeHidden()) {
                StringBuilder groupSB = new StringBuilder();
                for (CommandGroupMetadata subSubGroup : subGroup.getSubGroups()) {
                    if (groupSB.length() > 0)
                        groupSB.append(", ");
                    groupSB.append(subSubGroup.getName()).append(' ').append("<sub-command>");
                }
                for (CommandMetadata subCommand : subGroup.getCommands()) {
                    if (groupSB.length() > 0)
                        groupSB.append(", ");
                    groupSB.append(subCommand.getName());
                    if (subGroup.getDefaultCommand() != null && subGroup.getDefaultCommand().equals(subCommand)) {
                        groupSB.append("*");
                        hasDefaultCommand = true;
                    }
                }
                subGroups.put(subGroup.getName(), groupSB.toString());
            }
        }

        if (subGroups.size() > 0) {
            synopsis.newline().append("Where command groups contain the following sub-groups and commands:").newline();
            UsagePrinter grps = synopsis.newIndentedPrinter(4);
            for (String groupName : subGroups.keySet()) {
                grps.append(groupName + ": " + subGroups.get(groupName)).newline();
            }
        }

        for (CommandMetadata command : commands) {

            if (!command.isHidden() || this.includeHidden()) {
                if (hasCommandSpecificOptions) {
                    List<OptionMetadata> thisCmdOptions = new ArrayList<>(command.getCommandOptions());
                    thisCmdOptions.removeAll(commonGroupOptions);
                    StringBuilder optSB = new StringBuilder();
                    for (String s : toSynopsisUsage(thisCmdOptions)) {
                        optSB.append(s + " ");
                    }
                    cmdOptions.put(command.getName(), optSB.toString());
                }
                if (hasCommandSpecificArgs) {
                    cmdArguments.put(command.getName(),
                            (command.getArguments() != null ? toUsage(command.getArguments()) : ""));
                }
            }
        }
        if (hasCommandSpecificOptions) {
            synopsis.newline().append("Where command-specific options [cmd-options] are:").newline();
            UsagePrinter opts = synopsis.newIndentedPrinter(4);
            for (String cmd : cmdOptions.keySet()) {
                opts.append(cmd + ": " + cmdOptions.get(cmd)).newline();
            }
        }
        if (hasCommandSpecificArgs) {
            synopsis.newline().append("Where command-specific arguments <cmd-args> are:").newline();
            UsagePrinter args = synopsis.newIndentedPrinter(4);
            for (String arg : cmdArguments.keySet()) {
                args.append(arg + ": " + cmdArguments.get(arg)).newline();
            }
        }
        if (hasDefaultCommand) {
            synopsis.newline().append("Where * indicates the default command(s)");
        }
        synopsis.newline().append("See").append("'" + global.getName()).append("help ")
                .appendWords(UsageHelper.toGroupNames(Arrays.asList(groups)))
                .appendOnOneLine(" <command>' for more information on a specific command.").newline();
    }

    /**
     * Outputs a description of the group
     * 
     * @param out
     *            Usage printer
     * @param global
     *            Global meta-data
     * @param groups
     *            Groups meta-data
     * @throws IOException Thrown if there is a problem generating usage output
     */
    protected void outputDescription(UsagePrinter out, GlobalMetadata<T> global, CommandGroupMetadata[] groups)
            throws IOException {
        out.append("NAME").newline();

        out.newIndentedPrinter(8).append(global.getName()).append(" ");
        CommandGroupMetadata group = null;
        for (int i = 0; i < groups.length; i++) {
            group = groups[i];
            out.append(group.getName());
            if (i < groups.length - 1)
                out.append(" ");
        }
        out.append(" - ").append(group.getDescription()).newline().newline();
    }
}
