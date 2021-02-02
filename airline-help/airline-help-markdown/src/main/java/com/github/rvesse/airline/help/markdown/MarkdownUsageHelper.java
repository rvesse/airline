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
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.github.rvesse.airline.help.UsageHelper;
import com.github.rvesse.airline.help.common.AbstractUsageGenerator;
import com.github.rvesse.airline.help.sections.HelpFormat;
import com.github.rvesse.airline.help.sections.HelpHint;
import com.github.rvesse.airline.help.sections.HelpSection;
import com.github.rvesse.airline.io.printers.UsagePrinter;
import com.github.rvesse.airline.model.ArgumentsMetadata;
import com.github.rvesse.airline.model.OptionMetadata;
import com.github.rvesse.airline.model.ParserMetadata;
import com.github.rvesse.airline.restrictions.ArgumentsRestriction;
import com.github.rvesse.airline.restrictions.OptionRestriction;

public class MarkdownUsageHelper extends AbstractUsageGenerator {

    public MarkdownUsageHelper(Comparator<? super OptionMetadata> optionComparator, boolean includeHidden) {
        super(UsageHelper.DEFAULT_HINT_COMPARATOR, optionComparator, UsageHelper.DEFAULT_COMMAND_COMPARATOR,
                includeHidden);
    }

    public void outputOptions(UsagePrinter out, List<OptionMetadata> options) throws IOException {
        out.append("# OPTIONS").newline().newline();

        // Sort Options for consistent display across JVMs
        options = sortOptions(options);

        for (OptionMetadata option : options) {
            // Skip hidden options
            if (option.isHidden() && !this.includeHidden()) {
                continue;
            }

            // Option names
            out.append(" -");
            UsagePrinter optionPrinter = out.newIndentedPrinter(2);
            this.outputOptionTitle(optionPrinter, option);
            optionPrinter.newline();

            // Description
            optionPrinter.append(option.getDescription()).newline();

            // Restrictions
            List<HelpHint> hints = sortOptionRestrictions(option.getRestrictions());
            for (HelpHint hint : hints) {
                // Safe to cast back to OptionRestriction as must have come from
                // an OptionRestriction to start with
                outputOptionRestriction(optionPrinter, option, (OptionRestriction) hint, hint);
            }

            optionPrinter.newline();
            optionPrinter.flush();
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
     * @throws IOException Thrown if there is a problem generating usage output
     */
    protected void outputOptionRestriction(UsagePrinter out, OptionMetadata option, OptionRestriction restriction,
            HelpHint hint) throws IOException {
        out.newline();
        outputHint(out, hint, false);
    }

    protected void outputHint(UsagePrinter out, HelpHint hint, boolean newPara) throws IOException {
        // Ignore non-printable help
        if (hint.getFormat() == HelpFormat.NONE_PRINTABLE)
            return;

        // Print preamble if present
        if (!StringUtils.isBlank(hint.getPreamble())) {
            out.append(hint.getPreamble());
            out.newline();
            if (newPara)
                out.newline();
        }
        out.flush();

        // Print more details if there are some content blocks present
        if (hint.numContentBlocks() > 0) {
            // Decide how to print based on the format
            int maxRows;
            switch (hint.getFormat()) {
            case EXAMPLES:
                // Print as text with indents
                for (int e = 0; e < hint.getContentBlock(0).length; e++) {
                    // Example will be in first content block
                    UsagePrinter examplePrinter = out.newIndentedPrinter(4);
                    examplePrinter.appendOnOneLine(hint.getContentBlock(0)[e]);
                    examplePrinter.newline().newline();
                    examplePrinter.flush();

                    // Print example description with additional indent

                    for (int d = 1; d < hint.numContentBlocks(); d++) {
                        String[] descriptions = hint.getContentBlock(d);
                        if (e >= descriptions.length)
                            continue;
                        out.append(descriptions[e]);
                        out.newline().newline();
                    }
                    out.flush();
                }
                break;
            case TABLE:
            case TABLE_WITH_HEADERS:
                // Print as table
                // Convert to form that appendTable() understands
                // i.e. columns -> rows
                maxRows = calculateMaxRows(hint);
                List<List<String>> rows = new ArrayList<List<String>>();
                for (int row = 0; row < maxRows; row++) {
                    List<String> rowData = new ArrayList<String>();
                    for (int col = 0; col < hint.numContentBlocks(); col++) {
                        String[] colData = hint.getContentBlock(col);
                        rowData.add(row < colData.length ? colData[row] : null);
                    }
                    rows.add(rowData);
                }

                // Print out table header
                StringBuilder headerLine = new StringBuilder();
                headerLine.append("| ");
                if (hint.getFormat() != HelpFormat.TABLE_WITH_HEADERS) {
                    // Create empty header row
                    for (int col = 0; col < hint.numContentBlocks(); col++) {
                        headerLine.append(" | ");
                    }
                } else {
                    // Create header row
                    for (String col : rows.get(0)) {
                        headerLine.append(col);
                        headerLine.append(" | ");
                    }
                }
                out.appendOnOneLine(headerLine.toString());
                out.newline();

                // Print the header/content divider
                StringBuilder dividerLine = new StringBuilder();
                char[] headerChars = headerLine.toString().toCharArray();
                int lastPipePos = 0;
                for (int i = 0; i < headerChars.length; i++) {
                    char c = headerChars[i];
                    if (c == '|') {
                        if (i > 0 && i - lastPipePos <= 3) {
                            dividerLine.append("--- ");
                        }
                        lastPipePos = i;
                        dividerLine.append(c);
                        dividerLine.append(' ');
                        i++;
                    } else {
                        dividerLine.append('-');
                    }
                }
                out.appendOnOneLine(dividerLine.toString());
                out.newline();

                // Print out the rows
                int firstRow = hint.getFormat() == HelpFormat.TABLE_WITH_HEADERS ? 1 : 0;
                for (int row = firstRow; row < rows.size(); row++) {
                    StringBuilder contentLine = new StringBuilder();
                    contentLine.append("| ");

                    List<String> rowData = rows.get(row);
                    for (int col = 0; col < rowData.size(); col++) {
                        contentLine.append(rowData.get(col));
                        contentLine.append(" | ");
                    }
                    out.appendOnOneLine(contentLine.toString());
                    out.newline();
                }
                out.newline();

                break;
            case LIST:
                // Print first content block as an indented list list
                UsagePrinter listPrinter = out.newPrinterWithHangingIndent(2);
                for (String item : hint.getContentBlock(0)) {
                    listPrinter.append(" -").append(item).newline();
                }
                listPrinter.newline();
                listPrinter.flush();
                break;
            default:
                // Print content blocks as text
                for (int i = 0; i < hint.numContentBlocks(); i++) {
                    for (String line : hint.getContentBlock(i)) {
                        out.append(line);
                        out.newline().newline();
                    }
                }
                break;
            }
        }
    }

    public static int calculateMaxRows(HelpHint hint) {
        int maxRows = 0;
        for (int col = 0; col < hint.numContentBlocks(); col++) {
            maxRows = Math.max(maxRows, hint.getContentBlock(col).length);
        }
        return maxRows;
    }

    public <T> void outputArguments(UsagePrinter out, ArgumentsMetadata arguments, ParserMetadata<T> parserConfig)
            throws IOException {
        if (arguments != null) {
            // Arguments separator option
            out.append(" -");
            UsagePrinter optionPrinter = out.newIndentedPrinter(2);
            optionPrinter.append(String.format("`%s`", parserConfig.getArgumentsSeparator())).newline().newline();
            optionPrinter.flush();

            // Description
            optionPrinter
                    .append("This option can be used to separate command-line options from the list of arguments (useful when arguments might be mistaken for command-line options)")
                    .newline();
            optionPrinter.newline();
            optionPrinter.flush();

            // Arguments name(s)
            out.append(" -");
            optionPrinter = out.newIndentedPrinter(2);
            this.outputArgumentsTitle(optionPrinter, arguments);
            optionPrinter.newline();

            // Description
            optionPrinter.append(arguments.getDescription()).newline();

            // Restrictions
            List<HelpHint> hints = sortArgumentsRestrictions(arguments.getRestrictions());
            for (HelpHint hint : hints) {
                // Safe to cast back to ArgumentsRestriction as must have come from
                // an ArgumentsRestriction to start with
                outputArgumentsRestriction(optionPrinter, arguments, (ArgumentsRestriction) hint, hint);
            }

            optionPrinter.newline();
            optionPrinter.flush();
        }
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
     * @throws IOException Thrown if there is a problem generating usage output
     */
    protected void outputArgumentsRestriction(UsagePrinter descriptionPrinter, ArgumentsMetadata arguments,
            ArgumentsRestriction restriction, HelpHint hint) throws IOException {
        descriptionPrinter.newline();
        outputHint(descriptionPrinter, hint, false);
    }

    /**
     * Outputs a help section
     * 
     * @param out
     *            Usage printer
     * @param section
     *            Help section
     * @throws IOException Thrown if there is a problem generating usage output
     */
    public void outputHelpSection(UsagePrinter out, HelpSection section) throws IOException {
        if (section.getFormat() == HelpFormat.NONE_PRINTABLE)
            return;

        // Section title
        if (!StringUtils.isBlank(section.getTitle())) {
            out.append("#");
            out.append(section.getTitle().toUpperCase());
            out.newline().newline();
        }

        // Content
        outputHint(out, section, true);

        // Post-amble
        if (!StringUtils.isBlank(section.getPostamble())) {
            out.append(section.getPostamble());
            out.newline().newline();
        }

        out.flush();
        out.flush();
    }

    public void outputOptionsSynopsis(UsagePrinter printer, List<OptionMetadata> options) {
        // Sort options for consistent display across different JVMs
        options = sortOptions(options);

        for (int i = 0; i < options.size(); i++) {
            OptionMetadata option = options.get(i);
            if (option.isHidden() && !this.includeHidden())
                continue;

            this.outputOptionSynopsis(printer, option);
        }
    }

    public void outputOptionSynopsis(UsagePrinter printer, OptionMetadata option) {
        Set<String> options = option.getOptions();
        boolean required = option.isRequired();
        if (!required) {
            printer.append("[ ");
        }

        if (options.size() > 1) {
            printer.append("{");
        }

        boolean first = true;
        for (String name : options) {
            if (!first) {
                printer.append("|");
            } else {
                first = false;
            }
            printer.append(String.format("`%s`", name));
        }

        if (options.size() > 1) {
            printer.append("}");
        }

        if (option.getArity() > 0) {
            for (int i = 0; i < option.getArity(); i++) {
                printer.append(String.format(" *%s*", option.getTitle(i)));
            }
        }

        if (option.isMultiValued()) {
            printer.append("*...*");
        }

        if (!required) {
            printer.append("]");
        }
    }

    public void outputArgumentsSynopsis(UsagePrinter printer, ArgumentsMetadata arguments) {
        if (!arguments.isRequired()) {
            printer.append("[");
        }

        for (String title : arguments.getTitle()) {
            printer.append(String.format("*%s*", title));
        }

        if (!arguments.isRequired()) {
            printer.append("]");
        }
    }

    public void outputOptionTitle(UsagePrinter printer, OptionMetadata option) {
        int i = 0;
        for (String name : option.getOptions()) {
            printer.append(String.format("`%s`", name));
            if (option.getArity() > 0) {
                for (int index = 0; index < option.getArity(); index++) {
                    printer.append(String.format(" *%s*", option.getTitle(index)));
                }
            }
            if (i < option.getOptions().size() - 1)
                printer.append(",");
            i++;
        }
        printer.newline();
    }

    public void outputArgumentsTitle(UsagePrinter printer, ArgumentsMetadata arguments) {
        for (String title : arguments.getTitle()) {
            printer.append(String.format("*%s*", title));
        }
        printer.newline();
    }
}
