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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.github.rvesse.airline.utils.StringUtils;

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

public class CliUsageHelper extends AbstractUsageGenerator {

    public CliUsageHelper(Comparator<? super OptionMetadata> optionComparator, boolean includeHidden) {
        super(UsageHelper.DEFAULT_HINT_COMPARATOR, optionComparator, UsageHelper.DEFAULT_COMMAND_COMPARATOR,
                includeHidden);
    }

    public void outputOptions(UsagePrinter out, List<OptionMetadata> options) throws IOException {
        out.append("OPTIONS").newline();

        options = sortOptions(options);
        for (OptionMetadata option : options) {
            // Skip hidden options
            if (option.isHidden() && !this.includeHidden()) {
                continue;
            }

            // Option names
            UsagePrinter optionPrinter = out.newIndentedPrinter(8);
            optionPrinter.append(toDescription(option)).newline();
            optionPrinter.flush();

            // Description
            UsagePrinter descriptionPrinter = optionPrinter.newIndentedPrinter(4);
            descriptionPrinter.append(option.getDescription()).newline();

            // Restrictions
            List<HelpHint> hints = sortOptionRestrictions(option.getRestrictions());
            for (HelpHint hint : hints) {
                // Safe to cast back to OptionRestriction as must have come from
                // an OptionRestriction to start with
                outputOptionRestriction(descriptionPrinter, option, (OptionRestriction) hint, hint);
            }

            descriptionPrinter.newline();
            descriptionPrinter.flush();
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
                    out.appendOnOneLine(hint.getContentBlock(0)[e]);
                    out.newline().newline();
                    out.flush();

                    // Print example description with additional indent
                    UsagePrinter examplePrinter = out.newIndentedPrinter(4);
                    for (int d = 1; d < hint.numContentBlocks(); d++) {
                        String[] descriptions = hint.getContentBlock(d);
                        if (e >= descriptions.length)
                            continue;
                        examplePrinter.append(descriptions[e]);
                        examplePrinter.newline().newline();
                    }
                    examplePrinter.flush();
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

                // Print out table
                UsagePrinter tablePrinter = out.newIndentedPrinter(4);
                tablePrinter.appendTable(rows, 0);
                tablePrinter.newline();
                tablePrinter.flush();
                break;
            case LIST:
                // Print content blocks as indented lists
                for (int i = 0; i < hint.numContentBlocks(); i++) {
                    if (i > 0)
                        out.newline();

                    UsagePrinter listPrinter = out.newIndentedPrinter(4);
                    for (String item : hint.getContentBlock(i)) {
                        listPrinter.append(item).newline();
                    }
                    listPrinter.flush();
                }
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
            UsagePrinter optionPrinter = out.newIndentedPrinter(8);
            optionPrinter.append(parserConfig.getArgumentsSeparator()).newline();
            optionPrinter.flush();

            // Description
            UsagePrinter descriptionPrinter = optionPrinter.newIndentedPrinter(4);
            descriptionPrinter
                    .append("This option can be used to separate command-line options from the list of arguments (useful when arguments might be mistaken for command-line options)")
                    .newline();
            descriptionPrinter.newline();

            // Arguments name(s)
            optionPrinter.append(toDescription(arguments)).newline();

            // Description
            descriptionPrinter.append(arguments.getDescription()).newline();

            // Restrictions
            List<HelpHint> hints = sortArgumentsRestrictions(arguments.getRestrictions());
            for (HelpHint hint : hints) {
                // Safe to cast back to ArgumentsRestriction as must have come
                // from an ArgumentsRestriction to start with
                outputArgumentsRestriction(descriptionPrinter, arguments, (ArgumentsRestriction) hint, hint);
            }

            descriptionPrinter.newline();
            descriptionPrinter.flush();
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
     * @throws IOException
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
     * @throws IOException
     */
    public void outputHelpSection(UsagePrinter out, HelpSection section) throws IOException {
        if (section.getFormat() == HelpFormat.NONE_PRINTABLE)
            return;

        // Section title
        if (!StringUtils.isBlank(section.getTitle())) {
            out.append(section.getTitle().toUpperCase());
            out.newline();
        }

        UsagePrinter sectionPrinter = out.newIndentedPrinter(8);

        // Content
        outputHint(sectionPrinter, section, true);

        // Post-amble
        if (!StringUtils.isBlank(section.getPostamble())) {
            sectionPrinter.append(section.getPostamble());
            sectionPrinter.newline();
        }

        sectionPrinter.flush();
        out.flush();
    }
}
