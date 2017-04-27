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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import com.github.rvesse.airline.utils.StringUtils;

import com.github.rvesse.airline.help.UsageHelper;
import com.github.rvesse.airline.help.cli.CliUsageHelper;
import com.github.rvesse.airline.help.common.AbstractUsageGenerator;
import com.github.rvesse.airline.help.sections.HelpFormat;
import com.github.rvesse.airline.help.sections.HelpHint;
import com.github.rvesse.airline.help.sections.HelpSection;
import com.github.rvesse.airline.io.printers.TroffPrinter;
import com.github.rvesse.airline.model.ArgumentsMetadata;
import com.github.rvesse.airline.model.OptionMetadata;
import com.github.rvesse.airline.model.ParserMetadata;
import com.github.rvesse.airline.restrictions.ArgumentsRestriction;
import com.github.rvesse.airline.restrictions.OptionRestriction;

public class ManUsageHelper extends AbstractUsageGenerator {

    public ManUsageHelper(Comparator<? super OptionMetadata> optionComparator, boolean includeHidden) {
        super(UsageHelper.DEFAULT_HINT_COMPARATOR, optionComparator, UsageHelper.DEFAULT_COMMAND_COMPARATOR,
                includeHidden);
    }

    public int outputOptions(TroffPrinter printer, List<OptionMetadata> options, boolean endList) throws IOException {
        printer.nextSection("OPTIONS");

        int output = 0;
        options = sortOptions(options);
        if (!options.isEmpty()) {
            boolean first = true;
            for (OptionMetadata option : options) {
                // Skip hidden options
                if (option.isHidden() && !this.includeHidden()) {
                    continue;
                }

                // Option names
                if (first) {
                    printer.startTitledList();
                    this.outputOptionTitle(printer, option);
                    first = false;
                } else {
                    printer.nextTitledListItem();
                    this.outputOptionTitle(printer, option);
                }

                // Description
                printer.startPlainList();
                printer.println(option.getDescription());

                // Restrictions
                List<HelpHint> hints = sortOptionRestrictions(option.getRestrictions());
                for (HelpHint hint : hints) {
                    // Safe to cast back to OptionRestriction as must have come
                    // from an OptionRestriction to start with
                    outputOptionRestriction(printer, option, (OptionRestriction) hint, hint);
                }

                printer.endList();
                printer.flush();
                output++;
            }

            if (endList && !first) {
                printer.endList();
            }
        }
        return output;
    }

    public <T> void outputArguments(TroffPrinter printer, ArgumentsMetadata arguments, boolean startList,
            ParserMetadata<T> parserConfig) throws IOException {
        if (arguments != null) {
            // Arguments separator option

            if (startList) {
                printer.startTitledList();
            } else {
                printer.nextTitledListItem();
            }
            printer.printBold(parserConfig.getArgumentsSeparator());
            printer.println();

            // Description
            printer.startPlainList();
            printer.println(
                    "This option can be used to separate command-line options from the list of arguments (useful when arguments might be mistaken for command-line options)");
            printer.endList();

            // Arguments name(s)
            printer.nextTitledListItem();
            this.outputArgumentsTitle(printer, arguments);

            // Description
            printer.startPlainList();
            printer.println(arguments.getDescription());

            // Restrictions
            List<HelpHint> hints = sortArgumentsRestrictions(arguments.getRestrictions());
            for (HelpHint hint : hints) {
                // Safe to cast back to ArgumentsRestriction as must have come
                // from an ArgumentsRestriction to start with
                outputArgumentsRestriction(printer, arguments, (ArgumentsRestriction) hint, hint);
            }
            printer.endList();

            printer.endList();
        }
    }

    /**
     * Outputs documentation about a restriction on an option
     * 
     * @param out
     *            Usage printer
     * @param option
     *            Option meta-data
     * @param restriction
     *            Restriction
     * @param hint
     *            Help hint
     * @throws IOException
     */
    protected void outputOptionRestriction(TroffPrinter printer, OptionMetadata option, OptionRestriction restriction,
            HelpHint hint) throws IOException {
        outputHint(printer, hint);
    }

    /**
     * Outputs documentation about a restriction on an option
     * 
     * @param descriptionPrinter
     *            Description printer
     * @param arguments
     *            Arguments meta-data
     * @param restriction
     *            Restriction
     * @param hint
     *            Help hint
     * @throws IOException
     */
    protected void outputArgumentsRestriction(TroffPrinter printer, ArgumentsMetadata arguments,
            ArgumentsRestriction restriction, HelpHint hint) throws IOException {
        outputHint(printer, hint);
    }

    /**
     * Outputs a help section
     * 
     * @param out
     *            Usage printer
     * @param section
     *            Help section
     * @throws IOException
     */
    public void outputHelpSection(TroffPrinter printer, HelpSection section) throws IOException {
        if (section.getFormat() == HelpFormat.NONE_PRINTABLE)
            return;

        // Section title
        if (!StringUtils.isBlank(section.getTitle())) {
            printer.nextSection(section.getTitle().toUpperCase());
        }

        // Content
        outputHint(printer, section);

        // Post-amble
        if (!StringUtils.isBlank(section.getPostamble())) {
            printer.println(section.getPostamble());
        }

        printer.flush();
    }

    protected void outputHint(TroffPrinter printer, HelpHint hint) {
        // Don't bother with non-printable hints
        if (hint.getFormat() == HelpFormat.NONE_PRINTABLE)
            return;

        // Pre-amble
        if (!StringUtils.isBlank(hint.getPreamble())) {
            printer.println(hint.getPreamble());
        }

        // Hint Content
        switch (hint.getFormat()) {
        case EXAMPLES:
            // Print as text with indents
            for (int e = 0; e < hint.getContentBlock(0).length; e++) {
                // Example will be in first content block
                printer.println(hint.getContentBlock(0)[e]);

                // Print example description with additional indent
                printer.startPlainList();
                for (int d = 1; d < hint.numContentBlocks(); d++) {
                    String[] descriptions = hint.getContentBlock(d);
                    if (e >= descriptions.length)
                        continue;
                    printer.println(descriptions[e]);
                    if (d < hint.numContentBlocks() - 1 && hint.getContentBlock(d + 1).length > e)
                        printer.nextPlainListItem();
                }
                printer.endList();
            }
            break;

        case TABLE:
        case TABLE_WITH_HEADERS:
            // Convert to form that printTable() understands
            // i.e. columns -> rows
            int maxRows = CliUsageHelper.calculateMaxRows(hint);
            List<List<String>> rows = new ArrayList<List<String>>();
            for (int row = 0; row < maxRows; row++) {
                List<String> rowData = new ArrayList<String>();
                for (int col = 0; col < hint.numContentBlocks(); col++) {
                    String[] colData = hint.getContentBlock(col);
                    rowData.add(row < colData.length ? colData[row] : null);
                }
                rows.add(rowData);
            }

            printer.printTable(rows, hint.getFormat() == HelpFormat.TABLE_WITH_HEADERS);
            break;

        case LIST:
            // Print potentially many lists
            for (int i = 0; i < hint.numContentBlocks(); i++) {
                String[] items = hint.getContentBlock(i);
                if (items.length == 0)
                    continue;

                printer.startBulletedList();
                for (int j = 0; j < items.length; j++) {
                    printer.println(items[j]);
                    if (j < items.length - 1)
                        printer.nextBulletedListItem();
                }
                printer.endList();
            }

            break;
        default:
            for (int i = 0; i < hint.numContentBlocks(); i++) {
                for (String para : hint.getContentBlock(i)) {
                    printer.println(para);
                }
            }
            break;
        }

        printer.flush();
    }

    public void outputOptionsSynopsis(TroffPrinter printer, List<OptionMetadata> options) {
        boolean first = true;
        for (int i = 0; i < options.size(); i++) {
            OptionMetadata option = options.get(i);
            if (option.isHidden() && !this.includeHidden())
                continue;

            if (first) {
                first = false;
            } else {
                printer.print(" ");
            }

            this.outputOptionSynopsis(printer, option);
        }
    }

    public void outputOptionSynopsis(TroffPrinter printer, OptionMetadata option) {
        Set<String> options = option.getOptions();
        boolean required = option.isRequired();
        if (!required) {
            printer.print("[ ");
        }

        if (options.size() > 1) {
            printer.print("{");
        }

        boolean first = true;
        for (String name : options) {
            if (!first) {
                printer.print(" | ");
            } else {
                first = false;
            }
            printer.printBold(name);
        }

        if (options.size() > 1) {
            printer.print("}");
        }

        if (option.getArity() > 0) {
            printer.print(" ");
            printer.printItalic(option.getTitle());
        }

        if (option.isMultiValued()) {
            printer.printItalic("...");
        }

        if (!required) {
            printer.print(" ]");
        }
    }

    public void outputArgumentsSynopsis(TroffPrinter printer, ArgumentsMetadata arguments) {
        if (!arguments.isRequired()) {
            printer.print("[ ");
        }

        for (String title : arguments.getTitle()) {
            printer.printItalic(title);
            printer.print(" ");
        }

        if (!arguments.isRequired()) {
            printer.print("]");
        }
    }

    public void outputOptionTitle(TroffPrinter printer, OptionMetadata option) {
        int i = 0;
        for (String name : option.getOptions()) {
            printer.printBold(name);
            if (option.getArity() > 0) {
                printer.print(" ");
                printer.printItalic(option.getTitle());
            }
            if (i < option.getOptions().size() - 1)
                printer.print(", ");
            i++;
        }
        printer.println();
    }

    public void outputArgumentsTitle(TroffPrinter printer, ArgumentsMetadata arguments) {
        int i = 0;
        for (String title : arguments.getTitle()) {
            printer.printItalic(title);
            if (i < arguments.getTitle().size() - 1)
                printer.print(" ");
            i++;
        }
        printer.println();
    }
}
