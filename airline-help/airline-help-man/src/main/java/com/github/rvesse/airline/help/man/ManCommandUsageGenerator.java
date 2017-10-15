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

import com.github.rvesse.airline.utils.StringUtils;

import com.github.rvesse.airline.help.UsageHelper;
import com.github.rvesse.airline.help.common.AbstractCommandUsageGenerator;
import com.github.rvesse.airline.help.sections.HelpSection;
import com.github.rvesse.airline.io.printers.TroffPrinter;
import com.github.rvesse.airline.model.CommandMetadata;
import com.github.rvesse.airline.model.MetadataLoader;
import com.github.rvesse.airline.model.OptionMetadata;
import com.github.rvesse.airline.model.ParserMetadata;

/**
 * A command usage generator which generates help in man page (Troff) format
 */
public class ManCommandUsageGenerator extends AbstractCommandUsageGenerator {

    private final int manSection;
    private final ManUsageHelper helper;

    public ManCommandUsageGenerator() {
        this(ManSections.GENERAL_COMMANDS, false);
    }

    /**
     * Creates a new man page usage generator
     * 
     * @param manSection
     *            Man section to which this command belongs, use constants from
     *            {@link ManSections}
     */
    public ManCommandUsageGenerator(int manSection, boolean includeHidden) {
        super(includeHidden);
        this.manSection = manSection;
        this.helper = createHelper(includeHidden);
    }

    protected ManUsageHelper createHelper(boolean includeHidden) {
        return new ManUsageHelper(UsageHelper.DEFAULT_OPTION_COMPARATOR, includeHidden);
    }

    @Override
    public <T> void usage(String programName, String[] groupNames, String commandName, CommandMetadata command,
            ParserMetadata<T> parserConfig, OutputStream output) throws IOException {
        
        // Get the parser metadata
        if (parserConfig == null) {
            parserConfig = MetadataLoader.loadParser(command.getType());
        }

        // Fall back to metadata declared name if necessary
        if (commandName == null)
            commandName = command.getName();

        TroffPrinter printer = new TroffPrinter(new PrintWriter(output));

        outputTitle(printer, programName, groupNames, commandName, command);

        // Find the help sections
        List<HelpSection> preSections = new ArrayList<HelpSection>();
        List<HelpSection> postSections = new ArrayList<HelpSection>();
        findHelpSections(command, preSections, postSections);

        // Output pre help sections
        for (HelpSection section : preSections) {
            helper.outputHelpSection(printer, section);
        }

        List<OptionMetadata> options = outputSynopsis(printer, programName, groupNames, commandName, command);

        if (options.size() > 0 || command.getArguments() != null) {
            outputOptions(printer, command, options, parserConfig);
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
     * Outputs a documentation section detailing the options and their usages
     * 
     * @param printer
     *            Troff Printer
     * @param command
     *            Command
     * @param options
     *            Option meta-data
     * 
     * @throws IOException
     */
    protected <T> void outputOptions(TroffPrinter printer, CommandMetadata command, List<OptionMetadata> options, ParserMetadata<T> parserConfig)
            throws IOException {
        // Options
        // Can end the list if there are no arguments
        int optionsOutput = helper.outputOptions(printer, options, command.getArguments() == null);

        // Arguments
        // Must start the list if there are no visible options
        helper.outputArguments(printer, command.getArguments(), optionsOutput == 0, parserConfig);
    }

    /**
     * Outputs a synopsis section for the documentation showing how to use a
     * command
     * 
     * @param printer
     *            Troff printer
     * @param programName
     *            Program name
     * @param groupNames
     *            Group name(s)
     * @param commandName
     *            Command name
     * @param command
     *            Command
     * @return List of all the available options (global, group and command)
     * @throws IOException
     */
    protected List<OptionMetadata> outputSynopsis(TroffPrinter printer, String programName, String[] groupNames,
            String commandName, CommandMetadata command) throws IOException {
        printer.nextSection("SYNOPSIS");

        List<OptionMetadata> options = new ArrayList<>();
        List<OptionMetadata> aOptions;
        if (programName != null) {
            // Program name
            printer.printBold(programName);

            // Program Options
            aOptions = command.getGlobalOptions();
            if (aOptions != null && aOptions.size() > 0) {
                printer.print(" ");
                aOptions = sortOptions(aOptions);
                this.helper.outputOptionsSynopsis(printer, aOptions);
                options.addAll(aOptions);
            }
        }
        if (groupNames != null) {
            // Group Name(s)
            for (int i = 0; i < groupNames.length; i++) {
                printer.print(" ");
                printer.printBold(groupNames[i]);
            }
            // Group Options
            aOptions = command.getGroupOptions();
            if (aOptions != null && aOptions.size() > 0) {
                printer.print(" ");
                aOptions = sortOptions(aOptions);
                this.helper.outputOptionsSynopsis(printer, aOptions);
                options.addAll(aOptions);
            }
        }
        // Command Name

        if (programName != null || groupNames != null)
            printer.print(" ");
        printer.printBold(commandName);
        printer.print(" ");

        // Command options
        aOptions = command.getCommandOptions();
        aOptions = sortOptions(aOptions);
        this.helper.outputOptionsSynopsis(printer, aOptions);
        options.addAll(aOptions);

        // Command arguments (optional)
        if (command.getArguments() != null) {
            printer.print(" [ ");
            printer.printBold("--");
            printer.print(" ] ");
            this.helper.outputArgumentsSynopsis(printer, command.getArguments());
        }

        printer.println();

        printer.println(command.getDescription());
        return options;
    }

    /**
     * Outputs a title section for the document
     * 
     * @param writer
     *            Writer
     * @param programName
     *            Program name
     * @param groupNames
     *            Group name(s)
     * @param commandName
     *            Command name
     * @param command
     *            Command meta-data
     * @throws IOException
     */
    protected void outputTitle(TroffPrinter printer, String programName, String[] groupNames, String commandName,
            CommandMetadata command) throws IOException {
        String fullName = getFullCommandName(programName, groupNames, commandName);
        printer.start(fullName, manSection);

        printer.nextSection("NAME");
        printer.printBold(fullName);
        if (!StringUtils.isEmpty(command.getDescription())) {
            printer.print(String.format(" - %s", command.getDescription()));
        }
        printer.println();
    }

    /**
     * Gets the full command name in man page syntax
     * 
     * @param programName
     *            Program name
     * @param groupNames
     *            Group name(s)
     * @param command
     *            Command meta-data
     * @param writer
     *            Writer
     * @throws IOException
     */
    protected String getFullCommandName(String programName, String[] groupNames, String commandName)
            throws IOException {
        StringBuilder builder = new StringBuilder();
        if (programName != null) {
            builder.append(programName).append("-");
        }
        if (groupNames != null) {
            for (int i = 0; i < groupNames.length; i++) {
                builder.append(groupNames[i]).append("-");
            }
        }
        builder.append(commandName);
        return builder.toString();
    }

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
