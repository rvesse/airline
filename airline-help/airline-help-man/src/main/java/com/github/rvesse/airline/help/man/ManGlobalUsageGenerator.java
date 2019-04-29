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
package com.github.rvesse.airline.help.man;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.github.rvesse.airline.help.CommandUsageGenerator;
import com.github.rvesse.airline.help.UsageHelper;
import com.github.rvesse.airline.help.common.AbstractGlobalUsageGenerator;
import com.github.rvesse.airline.help.man.ManSections;
import com.github.rvesse.airline.help.sections.HelpSection;
import com.github.rvesse.airline.io.printers.TroffPrinter;
import com.github.rvesse.airline.model.CommandGroupMetadata;
import com.github.rvesse.airline.model.CommandMetadata;
import com.github.rvesse.airline.model.GlobalMetadata;
import com.github.rvesse.airline.model.OptionMetadata;
import com.github.rvesse.airline.utils.AirlineUtils;

/**
 * <p>
 * A global usage generator which generates Man pages in Troff format
 * 
 * @author rvesse
 *
 */
public class ManGlobalUsageGenerator<T> extends AbstractGlobalUsageGenerator<T> {

    protected final CommandUsageGenerator commandUsageGenerator;
    protected final int manSection;
    protected final ManUsageHelper helper;

    public ManGlobalUsageGenerator() {
        this(ManSections.GENERAL_COMMANDS, false, new ManCommandUsageGenerator(ManSections.GENERAL_COMMANDS, false));
    }

    public ManGlobalUsageGenerator(int manSection) {
        this(manSection, false, new ManCommandUsageGenerator(manSection, false));
    }

    public ManGlobalUsageGenerator(int manSection, boolean includeHidden) {
        this(manSection, includeHidden, new ManCommandUsageGenerator(manSection, includeHidden));
    }

    protected ManGlobalUsageGenerator(int manSection, boolean includeHidden,
            CommandUsageGenerator commandUsageGenerator) {
        super(includeHidden);
        this.commandUsageGenerator = commandUsageGenerator;
        this.manSection = manSection;
        this.helper = createHelper(includeHidden);
    }

    protected ManUsageHelper createHelper(boolean includeHidden) {
        return new ManUsageHelper(UsageHelper.DEFAULT_OPTION_COMPARATOR, includeHidden);
    }

    @Override
    public void usage(GlobalMetadata<T> global, OutputStream output) throws IOException {
        TroffPrinter printer = new TroffPrinter(new PrintWriter(output));

        outputTitle(global, printer);
        
        // Find the help sections
        List<HelpSection> preSections = new ArrayList<HelpSection>();
        List<HelpSection> postSections = new ArrayList<HelpSection>();
        findHelpSections(global, preSections, postSections);

        // Output pre help sections
        for (HelpSection section : preSections) {
            helper.outputHelpSection(printer, section);
        }

        List<OptionMetadata> options = new ArrayList<>();
        if (global.getOptions() != null && global.getOptions().size() > 0) {
            options.addAll(global.getOptions());
            options = sortOptions(options);
        }
        outputSynopsis(printer, global);

        if (options.size() > 0) {
            helper.outputOptions(printer, options, true);
        }

        printer.flush();
        output.flush();

        // Commands and Command Groups
        if (global.getCommandGroups().size() > 0) {
            // Groups
            outputGroupList(printer, global);
            outputCommandUsages(output, printer, global);
        } else {
            // No groups
            outputCommandList(printer, global);
            outputCommandUsages(output, printer, global);
        }
        
        // Output post help sections
        for (HelpSection section : postSections) {
            helper.outputHelpSection(printer, section);
        }

        // Flush the output
        printer.flush();
        output.flush();
    }

    /**
     * Outputs a documentation section that lists the available groups and the
     * commands they contain
     * <p>
     * Used only when a CLI has command groups, if no groups are present then
     * {@link #outputCommandList(TroffPrinter, GlobalMetadata)} is used instead.
     * </p>
     * 
     * @param printer
     *            Troff printer
     * @param global
     *            Global meta-data
     * 
     * @throws IOException Thrown if there is a problem generating usage output
     */
    protected void outputGroupList(TroffPrinter printer, GlobalMetadata<T> global) throws IOException {
        printer.nextSection("COMMAND GROUPS");
        printer.println("Commands are grouped as follows:");

        // Start titled list for top level groups
        printer.startTitledList();

        if (global.getDefaultGroupCommands().size() > 0) {
            // First item in the titled list of top level groups
            printer.print("Default (no ");
            printer.printItalic("group");
            printer.println(" specified)");

            boolean first = true;
            for (CommandMetadata command : sortCommands(global.getDefaultGroupCommands())) {
                if (command.isHidden() && !this.includeHidden())
                    continue;

                if (first) {
                    printer.startTitledList();
                    first = false;
                } else {
                    printer.nextTitledListItem();
                }
                printer.printBold(getCommandName(global, null, command));
                printer.println();
                printer.println(command.getDescription());
            }
            // End the titled list for commands in the default group
            if (!first)
                printer.endList();
        }

        // Commands in this group
        outputGroupCommandsList(printer, global, global.getCommandGroups());

        // End titled list for top level groups
        printer.endList();
    }

    protected void outputGroupCommandsList(TroffPrinter printer, GlobalMetadata<T> global,
            List<CommandGroupMetadata> groups) throws IOException {
        if (groups.size() == 0)
            return;

        for (CommandGroupMetadata group : sortCommandGroups(groups)) {
            if (group.isHidden() && !this.includeHidden())
                continue;

            // Add to existing titled list this group
            printer.nextTitledListItem();
            printer.printBold(group.getName());
            printer.println();
            printer.println(group.getDescription());

            // New titled list for command and sub-groups of this group
            printer.startTitledList();

            boolean first = true;
            for (CommandMetadata command : sortCommands(group.getCommands())) {
                if (command.isHidden() && !this.includeHidden())
                    continue;

                if (first) {
                    first = false;
                } else {
                    printer.nextTitledListItem();
                }
                printer.printBold(getCommandName(global, new String[] { group.getName() }, command));
                printer.println();
                printer.println(command.getDescription());
            }

            outputGroupCommandsList(printer, global, group.getSubGroups());

            // End titled list for commands and sub-groups of this group
            printer.endList();
        }

    }

    /**
     * Outputs a documentation section that lists the available commands
     * <p>
     * Used only when a CLI does not have command groups, if groups are present
     * then {@link #outputGroupList(TroffPrinter, GlobalMetadata)} is used instead.
     * </p>
     * 
     * @param printer
     *            Troff Printer
     * @param global
     *            Global meta-data
     * 
     * @throws IOException Thrown if there is a problem generating usage output
     */
    protected void outputCommandList(TroffPrinter printer, GlobalMetadata<T> global) throws IOException {
        printer.nextSection("COMMANDS");
        printer.println("The following commands are available:");

        boolean first = true;
        for (CommandMetadata command : sortCommands(global.getDefaultGroupCommands())) {
            if (command.isHidden() && !this.includeHidden())
                continue;

            if (first) {
                printer.startTitledList();
                first = false;
            } else {
                printer.nextTitledListItem();
            }
            printer.printBold(getCommandName(global, null, command));
            printer.println();
            printer.println(command.getDescription());
        }

        if (!first)
            printer.endList();
    }

    /**
     * Outputs a documentation section with a synopsis of how to use the CLI
     * 
     * @param printer
     *            Troff Printer
     * @param global
     *            Global meta-data
     * 
     * @throws IOException Thrown if there is a problem generating usage output
     */
    protected void outputSynopsis(TroffPrinter printer, GlobalMetadata<T> global) throws IOException {
        printer.nextSection("SYNOPSIS");
        printer.printBold(global.getName());
        if (global.getOptions() != null && global.getOptions().size() > 0) {
            printer.print(" ");
            this.helper.outputOptionsSynopsis(printer, sortOptions(global.getOptions()));
        }
        printer.print(" ");
        if (global.getCommandGroups().size() > 0) {
            if (global.getDefaultGroupCommands().size() > 0)
                printer.print("[ ");
            printer.printItalic("group");
            if (global.getDefaultGroupCommands().size() > 0)
                printer.print(" ]");
            printer.print(" ");
        }
        printer.printItalic("command");
        printer.print(" [ ");
        printer.printItalic("command-args");
        printer.print(" ]");

        printer.println();
        printer.println(global.getDescription());
    }

    /**
     * Outputs the title section for the documentation
     * 
     * @param global
     *            Global meta-data
     * @param printer
     *            Troff printer
     * @throws IOException Thrown if there is a problem generating usage output
     */
    protected void outputTitle(GlobalMetadata<T> global, TroffPrinter printer) throws IOException {
        printer.start(global.getName(), this.manSection);

        printer.nextSection("NAME");
        printer.printBold(global.getName());
        printer.print(" -- ");
        printer.print(global.getDescription());
    }

    /**
     * Outputs the command usages for all groups
     * 
     * @param output
     *            Output stream
     * @param printer
     *            Writer
     * @param global
     *            Global meta-data
     * 
     * @throws IOException Thrown if there is a problem generating usage output
     */
    protected void outputCommandUsages(OutputStream output, TroffPrinter printer, GlobalMetadata<T> global)
            throws IOException {
        // TODO Do we want a horizontal line?

        // Default group usages
        outputDefaultGroupCommandUsages(output, printer, global);

        // Other group usages
        for (CommandGroupMetadata group : sortCommandGroups(global.getCommandGroups())) {
            if (group.isHidden() && !this.includeHidden())
                continue;

            List<CommandGroupMetadata> groupPath = new ArrayList<CommandGroupMetadata>();
            groupPath.add(group);
            outputGroupCommandUsages(output, printer, global, groupPath);
        }
    }

    /**
     * Gets the display name for a command
     * 
     * @param global
     *            Global meta-data
     * @param groupNames
     *            Group name(s) (may be null)
     * @param command
     *            Command meta-data
     * @return Display name for the command
     */
    protected String getCommandName(GlobalMetadata<T> global, String[] groupNames, CommandMetadata command) {
        return command.getName();
    }

    /**
     * Outputs the command usages for the commands in the given group
     * 
     * @param output
     *            Output
     * @param printer
     *            Writer
     * @param global
     *            Global Meta-data
     * @param groups
     *            Groups Meta-data
     * 
     * @throws IOException Thrown if there is a problem generating usage output
     */
    protected void outputGroupCommandUsages(OutputStream output, TroffPrinter printer, GlobalMetadata<T> global,
            List<CommandGroupMetadata> groups) throws IOException {
        CommandGroupMetadata group = groups.get(groups.size() - 1);

        // Commands in the group
        for (CommandMetadata command : sortCommands(group.getCommands())) {
            if (command.isHidden() && !this.includeHidden())
                continue;

            printer.flush();
            output.flush();
            commandUsageGenerator.usage(global.getName(), UsageHelper.toGroupNames(groups), command.getName(), command,
                    global.getParserConfiguration(), output);
        }

        // Sub-groups
        for (CommandGroupMetadata subGroup : sortCommandGroups(group.getSubGroups())) {
            if (subGroup.isHidden() && !this.includeHidden())
                continue;

            List<CommandGroupMetadata> subGroupPath = AirlineUtils.listCopy(groups);
            subGroupPath.add(subGroup);
            outputGroupCommandUsages(output, printer, global, subGroupPath);
        }
    }

    /**
     * Outputs the command usages for the commands in the default group
     * 
     * @param output
     *            Output
     * @param printer
     *            Writer
     * @param global
     *            Global meta-data
     * 
     * @throws IOException Thrown if there is a problem generating usage output
     */
    protected void outputDefaultGroupCommandUsages(OutputStream output, TroffPrinter printer, GlobalMetadata<T> global)
            throws IOException {
        for (CommandMetadata command : sortCommands(global.getDefaultGroupCommands())) {
            if (command.isHidden() && !this.includeHidden())
                continue;

            printer.flush();
            output.flush();
            commandUsageGenerator.usage(global.getName(), null, command.getName(), command,
                    global.getParserConfiguration(), output);
        }
    }

    /**
     * Converts an option to its description form
     */
    @Override
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
            stringBuilder.append('`').append(name).append('`');
            if (argumentString != null)
                stringBuilder.append(' ').append(argumentString);
        }

        return stringBuilder.toString();
    }
}
