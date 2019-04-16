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

import static com.github.rvesse.airline.help.UsageHelper.DEFAULT_OPTION_COMPARATOR;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.github.rvesse.airline.help.common.AbstractPrintedCommandUsageGenerator;
import com.github.rvesse.airline.help.sections.HelpSection;
import com.github.rvesse.airline.io.printers.UsagePrinter;
import com.github.rvesse.airline.model.ArgumentsMetadata;
import com.github.rvesse.airline.model.CommandMetadata;
import com.github.rvesse.airline.model.MetadataLoader;
import com.github.rvesse.airline.model.OptionMetadata;
import com.github.rvesse.airline.model.ParserMetadata;
import com.github.rvesse.airline.model.PositionalArgumentMetadata;

public class CliCommandUsageGenerator extends AbstractPrintedCommandUsageGenerator {

    private final CliUsageHelper helper;

    public CliCommandUsageGenerator() {
        this(DEFAULT_COLUMNS, DEFAULT_OPTION_COMPARATOR, false);
    }

    public CliCommandUsageGenerator(boolean includeHidden) {
        this(DEFAULT_COLUMNS, DEFAULT_OPTION_COMPARATOR, includeHidden);
    }

    public CliCommandUsageGenerator(int columns) {
        this(columns, DEFAULT_OPTION_COMPARATOR, false);
    }

    public CliCommandUsageGenerator(int columns, boolean includeHidden) {
        this(columns, DEFAULT_OPTION_COMPARATOR, includeHidden);
    }

    public CliCommandUsageGenerator(int columns, Comparator<? super OptionMetadata> optionComparator,
            boolean includeHidden) {
        super(columns, optionComparator, includeHidden);
        helper = createHelper(optionComparator, includeHidden);
    }

    protected CliUsageHelper createHelper(Comparator<? super OptionMetadata> optionComparator, boolean includeHidden) {
        return new CliUsageHelper(optionComparator, includeHidden);
    }

    @Override
    public <T> void usage(String programName, String[] groupNames, String commandName, CommandMetadata command,
            ParserMetadata<T> parserConfig, UsagePrinter out) throws IOException {

        if (parserConfig == null) {
            parserConfig = MetadataLoader.loadParser(command.getType());
        }

        // Name and description
        outputDescription(out, programName, groupNames, commandName, command);

        // Find the help sections
        List<HelpSection> preSections = new ArrayList<HelpSection>();
        List<HelpSection> postSections = new ArrayList<HelpSection>();
        findHelpSections(command, preSections, postSections);

        // Output pre help sections
        for (HelpSection section : preSections) {
            helper.outputHelpSection(out, section);
        }

        // Synopsis
        List<OptionMetadata> options = outputSynopsis(out, programName, groupNames, commandName, command, parserConfig);

        // Options
        ArgumentsMetadata arguments = command.getArguments();
        if (options.size() > 0 || command.hasAnyArguments()) {
            outputOptionsAndArguments(out, command, options, command.getPositionalArguments(), arguments, parserConfig);
        }

        // Output post help sections
        for (HelpSection section : postSections) {
            helper.outputHelpSection(out, section);
        }
    }

    /**
     * Outputs a documentation section detailing options and their usages
     * 
     * @param out
     *            Usage printer
     * @param command
     *            Command meta-data
     * @param options
     *            Options meta-data
     * @param positionalArgs
     *            Positional arguments meta-data
     * @param arguments
     *            Arguments meta-data
     * @param parserConfig
     *            Parser configuration
     * @param <T>
     *            Command type
     * @throws IOException
     *             Thrown if there is a problem generating usage output
     */
    protected <T> void outputOptionsAndArguments(UsagePrinter out, CommandMetadata command,
            List<OptionMetadata> options, List<PositionalArgumentMetadata> positionalArgs, ArgumentsMetadata arguments,
            ParserMetadata<T> parserConfig) throws IOException {
        helper.outputOptions(out, options);
        helper.outputArguments(out, positionalArgs, arguments, parserConfig);
    }

    /**
     * Outputs a documentation section with a synopsis of command usage
     * 
     * @param out
     *            Usage printer
     * @param programName
     *            Program name
     * @param groupNames
     *            Group name(s)
     * @param commandName
     *            Command name
     * @param command
     *            Command meta-data
     * @return Collection of all options (Global, Group and Command)
     * @throws IOException
     *             Thrown if there is a problem generating usage output
     */
    protected <T> List<OptionMetadata> outputSynopsis(UsagePrinter out, String programName, String[] groupNames,
            String commandName, CommandMetadata command, ParserMetadata<T> parserConfig) throws IOException {
        out.append("SYNOPSIS").newline();
        UsagePrinter synopsis = out.newIndentedPrinter(8).newPrinterWithHangingIndent(8);
        List<OptionMetadata> options = new ArrayList<>();
        if (programName != null) {
            synopsis.append(programName).appendWords(toSynopsisUsage(sortOptions(command.getGlobalOptions())));
            options.addAll(command.getGlobalOptions());
        }
        if (groupNames != null) {
            synopsis.appendWords(groupNames);
            synopsis.appendWords(toSynopsisUsage(sortOptions(command.getGroupOptions())));
            options.addAll(command.getGroupOptions());
        }
        synopsis.append(commandName).appendWords(toSynopsisUsage(sortOptions(command.getCommandOptions())));
        options.addAll(command.getCommandOptions());
        
        boolean needsArgumentsSeparator = command.hasAnyArguments();
        if (needsArgumentsSeparator) {
            synopsis.append(String.format("[%s]", parserConfig.getArgumentsSeparator()));
        }
        
        if (command.hasPositionalArguments()) {
            synopsis.append(toUsage(command.getPositionalArguments()));
        }

        // command arguments (optional)
        if (command.hasNonPositionalArguments()) {
            synopsis.append(toUsage(command.getArguments()));
        }
        synopsis.newline();
        synopsis.newline();
        return options;
    }

    /**
     * Outputs a documentation section describing the command
     * 
     * @param out
     *            Usage printer
     * @param programName
     *            Program name
     * @param groupNames
     *            Group name(s)
     * @param commandName
     *            Command name
     * @param command
     *            Command meta-data
     * @throws IOException
     *             Thrown if there is a problem generating usage output
     */
    protected void outputDescription(UsagePrinter out, String programName, String[] groupNames, String commandName,
            CommandMetadata command) throws IOException {
        out.append("NAME").newline();

        out = out.newIndentedPrinter(8).append(programName);
        if (groupNames != null) {
            out.appendWords(groupNames);
        }
        out.append(commandName).append("-").append(command.getDescription()).newline().newline();
    }

}
