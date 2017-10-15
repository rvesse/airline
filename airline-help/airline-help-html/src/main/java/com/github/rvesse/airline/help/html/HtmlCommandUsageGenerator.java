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
package com.github.rvesse.airline.help.html;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import com.github.rvesse.airline.utils.StringUtils;

import com.github.rvesse.airline.help.UsageHelper;
import com.github.rvesse.airline.help.common.AbstractCommandUsageGenerator;
import com.github.rvesse.airline.help.sections.HelpFormat;
import com.github.rvesse.airline.help.sections.HelpHint;
import com.github.rvesse.airline.model.ArgumentsMetadata;
import com.github.rvesse.airline.model.CommandMetadata;
import com.github.rvesse.airline.model.MetadataLoader;
import com.github.rvesse.airline.model.OptionMetadata;
import com.github.rvesse.airline.model.ParserMetadata;
import com.github.rvesse.airline.restrictions.OptionRestriction;

/**
 * A usage generator that generates HTML documentation
 */
public class HtmlCommandUsageGenerator extends AbstractCommandUsageGenerator {

    /**
     * Default stylesheet (Bootstrap)
     */
    public static final String DEFAULT_STYLESHEET = "css/bootstrap.min.css";
    /**
     * Constant for a new line (using a {@code <br>
     * })
     */
    protected static final String NEWLINE = "<br/>\n";

    /**
     * List of stylesheet URLs
     */
    protected final List<String> stylesheetUrls = new ArrayList<>();

    public HtmlCommandUsageGenerator() {
        this(UsageHelper.DEFAULT_OPTION_COMPARATOR, false, DEFAULT_STYLESHEET);
    }

    public HtmlCommandUsageGenerator(boolean includeHidden) {
        this(UsageHelper.DEFAULT_OPTION_COMPARATOR, includeHidden, DEFAULT_STYLESHEET);
    }

    public HtmlCommandUsageGenerator(String stylesheetUrl, boolean includeHidden) {
        this(UsageHelper.DEFAULT_OPTION_COMPARATOR, includeHidden, stylesheetUrl);
    }

    public HtmlCommandUsageGenerator(Comparator<? super OptionMetadata> optionComparator, boolean includeHidden,
            String... stylesheetUrls) {
        super(optionComparator, includeHidden);
        if (stylesheetUrls != null) {
            for (String stylesheet : stylesheetUrls) {
                if (StringUtils.isNotEmpty(stylesheet)) {
                    this.stylesheetUrls.add(stylesheet);
                }
            }
        }
    }

    @Override
    public <T> void usage(String programName, String[] groupNames, String commandName, CommandMetadata command,
            ParserMetadata<T> parserConfig, OutputStream output) throws IOException {

        if (parserConfig == null) {
            parserConfig = MetadataLoader.loadParser(command.getType());
        }

        Writer writer = new OutputStreamWriter(output);

        // Header
        outputHtmlHeader(writer);
        writer.append("<body>\n");

        // Page Header i.e. <h1>
        outputPageHeader(writer, programName, groupNames, command);

        // Name and description of command
        outputDescription(writer, programName, groupNames, command);

        // TODO Output pre help sections

        // Synopsis
        List<OptionMetadata> options = outputSynopsis(writer, programName, groupNames, command);

        // Options
        if (options.size() > 0 || command.getArguments() != null) {
            options = sortOptions(options);
            outputOptions(writer, options, command.getArguments(), parserConfig);
        }

        // TODO Output post help sections

        writer.append("</body>\n");
        writer.append("</html>\n");

        // Flush the output
        writer.flush();
        output.flush();
    }

    /**
     * Outputs a documentation section detailing the options
     * 
     * @param writer
     *            Writer
     * @param options
     *            Option meta-data
     * @throws IOException
     */
    protected <T> void outputOptions(Writer writer, List<OptionMetadata> options, ArgumentsMetadata arguments,
            ParserMetadata<T> parserConfig) throws IOException {
        writer.append(NEWLINE);
        writer.append("<h1 class=\"text-info\">OPTIONS</h1>\n").append(NEWLINE);

        for (OptionMetadata option : options) {
            // skip hidden options
            if (option.isHidden() && !this.includeHidden()) {
                continue;
            }

            // Option names
            writer.append("<div class=\"row\">\n");
            writer.append("<div class=\"span8 offset1\">\n");
            writer.append(htmlize(toDescription(option)));
            writer.append("</div>\n");
            writer.append("</div>\n");

            // Description
            writer.append("<div class=\"row\">\n");
            writer.append("<div class=\"span8 offset2\">\n");
            writer.append(htmlize(option.getDescription()));
            writer.append("</div>\n");
            writer.append("</div>\n");

            // Allowed values
            for (OptionRestriction restriction : option.getRestrictions()) {
                if (restriction instanceof HelpHint) {
                    outputOptionRestriction(writer, option, restriction, (HelpHint) restriction);
                }
            }
        }

        if (arguments != null) {
            // Arguments separator
            writer.append("<div class=\"row\">\n");
            writer.append("<div class=\"span8 offset1\">\n");

            writer.append(parserConfig.getArgumentsSeparator()).append("\n");

            writer.append("</div>\n");
            writer.append("</div>\n");

            // description
            writer.append("<div class=\"row\">\n");
            writer.append("<div class=\"span8 offset2\">\n");

            writer.append("This option can be used to separate command-line options from the "
                    + "list of argument, (useful when arguments might be mistaken for command-line options)\n");

            writer.append("</div>\n");
            writer.append("</div>\n");

            // arguments name
            writer.append("<div class=\"row\">\n");
            writer.append("<div class=\"span8 offset1\">\n");

            writer.append(htmlize(toDescription(arguments)));

            writer.append("</div>\n");
            writer.append("</div>\n");

            // description
            writer.append("<div class=\"row\">\n");
            writer.append("<div class=\"span8 offset2\">\n");

            writer.append(htmlize(arguments.getDescription()));

            writer.append("</div>\n");
            writer.append("</div>\n");
        }
    }

    /**
     * Outputs a documentation section detailing an allowed value for an option
     * 
     * @param writer
     *            Writer
     * @param option
     *            Option meta-data
     * @param restriction
     *            Restriction
     * @param hint
     *            Help hint
     * @throws IOException
     */
    protected void outputOptionRestriction(Writer writer, OptionMetadata option, OptionRestriction restriction,
            HelpHint hint) throws IOException {
        writer.append("<div class=\"row\">\n");
        writer.append("<div class=\"span8 offset3\">\n");

        // Append preamble if present
        if (!StringUtils.isEmpty(hint.getPreamble())) {
            writer.append(htmlize(hint.getPreamble()));
            writer.append(NEWLINE);
        }

        if (hint.numContentBlocks() > 0) {
            // Append help content
            switch (hint.getFormat()) {
            case EXAMPLES:
                // Treat as code examples
                String[] examples = hint.getContentBlock(0);
                for (int i = 0; i < examples.length; i++) {
                    writer.append("<pre>");
                    writer.append(examples[i]);
                    writer.append("</pre>").append(NEWLINE);

                    for (int j = 1; j < hint.numContentBlocks(); j++) {
                        String[] explanations = hint.getContentBlock(j);
                        if (i < explanations.length) {
                            writer.append("<p>").append(NEWLINE);
                            writer.append(htmlize(explanations[i]));
                            writer.append("</p>").append(NEWLINE);
                        }
                    }
                }
                break;
            case LIST:
                // Treat as a list
                writer.append("<ul>").append(NEWLINE);
                for (String item : hint.getContentBlock(0)) {
                    writer.append("<li>");
                    writer.append(htmlize(item));
                    writer.append("</li>").append(NEWLINE);
                }
                writer.append("</ul>");
                break;
            case TABLE:
            case TABLE_WITH_HEADERS:
                // Find max rows
                boolean useHeaders = hint.getFormat() == HelpFormat.TABLE_WITH_HEADERS;
                int maxRows = 0;
                for (int col = 0; col < hint.numContentBlocks(); col++) {
                    maxRows = Math.max(maxRows, hint.getContentBlock(col).length);
                }
                writer.append("<table>").append(NEWLINE);

                // Output table rows
                for (int row = 0; row < maxRows; row++) {
                    writer.append("<tr>").append(NEWLINE);
                    for (int col = 0; col < hint.numContentBlocks(); col++) {
                        String[] colData = hint.getContentBlock(col);
                        writer.append(useHeaders ? "<th>" : "<td>");
                        if (row < colData.length) {
                            writer.append(htmlize(colData[row]));
                        }
                        writer.append(useHeaders ? "</th>" : "</td>");
                        writer.append(NEWLINE);
                    }
                    useHeaders = false;
                    writer.append("</tr>").append(NEWLINE);
                }

                writer.append("</table>").append(NEWLINE);
                break;
            default:
                // Treat as paragraphs of text
                for (int i = 0; i < hint.numContentBlocks(); i++) {
                    for (String para : hint.getContentBlock(i)) {
                        writer.append("<p>").append(NEWLINE);
                        writer.append(htmlize(para));
                        writer.append("</p>").append(NEWLINE);
                    }
                }
                break;
            }
        }

        writer.append("</div>\n");
        writer.append("</div>\n");
    }

    /**
     * Outputs a documentation section with a synopsis of the command
     * 
     * @param writer
     *            Writer
     * @param programName
     *            Program name
     * @param groupNames
     *            Group name(s)
     * @param command
     *            Command name
     * @return List of all the available options (Global, Group and Command)
     * @throws IOException
     */
    protected List<OptionMetadata> outputSynopsis(Writer writer, String programName, String[] groupNames,
            CommandMetadata command) throws IOException {
        writer.append("<h1 class=\"text-info\">SYNOPSIS</h1>\n").append(NEWLINE);

        List<OptionMetadata> options = new ArrayList<>();
        writer.append("<div class=\"row\">\n");
        writer.append("<div class=\"span8 offset1\">\n");

        if (programName != null) {
            writer.append(htmlize(programName)).append(" ")
                    .append(htmlize(StringUtils.join(toSynopsisUsage(sortOptions(command.getGlobalOptions())), ' ')));
            options.addAll(command.getGlobalOptions());
        }
        if (groupNames != null) {
            for (int i = 0; i < groupNames.length; i++) {
                writer.append(htmlize(groupNames[i])).append(" ");
            }
            writer.append(htmlize(StringUtils.join(toSynopsisUsage(sortOptions(command.getGroupOptions())), ' ')));
            options.addAll(command.getGroupOptions());
        }
        writer.append(htmlize(command.getName())).append(" ")
                .append(htmlize(StringUtils.join(toSynopsisUsage(sortOptions(command.getCommandOptions())), ' ')));
        options.addAll(command.getCommandOptions());

        // command arguments (optional)
        ArgumentsMetadata arguments = command.getArguments();
        if (arguments != null) {
            writer.append(" [--] ").append(htmlize(toUsage(arguments)));
        }

        writer.append("</div>\n");
        writer.append("</div>\n");

        return options;
    }

    /**
     * Outputs a documentation section with the name and description of the
     * command
     * 
     * @param writer
     *            Writer
     * @param programName
     *            Program name
     * @param groupNames
     *            Group name(s)
     * @param command
     *            Command meta-data
     * @throws IOException
     */
    protected void outputDescription(Writer writer, String programName, String[] groupNames, CommandMetadata command)
            throws IOException {
        writer.append("<h2 class=\"text-info\">NAME</h1>\n").append(NEWLINE);

        writer.append("<div class=\"row\">");
        writer.append("<div class=\"span8 offset1\">");
        writer.append(htmlize(programName)).append(" ");
        if (groupNames != null) {
            for (int i = 0; i < groupNames.length; i++) {
                writer.append(htmlize(groupNames[i])).append(" ");
            }
        }
        writer.append(htmlize(command.getName())).append(" ");
        writer.append("&mdash;");
        writer.append(htmlize(command.getDescription()));
        writer.append("</div>\n");
        writer.append("</div>\n");

        writer.append(NEWLINE);
    }

    /**
     * Outputs a page header
     * 
     * @param writer
     *            Writer
     * @param programName
     *            Program name
     * @param groupNames
     *            Group name(s)
     * @param command
     *            Command meta-data
     * @throws IOException
     */
    protected void outputPageHeader(Writer writer, String programName, String[] groupNames, CommandMetadata command)
            throws IOException {
        writer.append("<hr/>\n");
        writer.append("<h1 class=\"text-info\">").append(htmlize(programName)).append(" ");
        if (groupNames != null) {
            for (int i = 0; i < groupNames.length; i++) {
                writer.append(htmlize(groupNames[i])).append(" ");
            }
        }
        writer.append(htmlize(command.getName())).append(" Manual Page\n");
        writer.append("<hr/>\n");
    }

    /**
     * Outputs the HTML header for the page
     * 
     * @param writer
     *            Writer
     * @throws IOException
     */
    protected void outputHtmlHeader(Writer writer) throws IOException {
        writer.append("<html>\n");
        writer.append("<head>\n");
        outputStylesheets(writer);
        writer.append("<style>\n");
        outputAdditionalCss(writer);
        writer.append("</style>\n");
        writer.append("</head>\n");
    }

    /**
     * Outputs additional CSS directly
     * 
     * @param writer
     *            Writer
     * @throws IOException
     */
    protected void outputAdditionalCss(Writer writer) throws IOException {
        writer.append("    body { margin: 50px; }\n");
    }

    /**
     * Outputs the style sheet declarations
     * 
     * @param writer
     *            Writer
     * @throws IOException
     */
    protected void outputStylesheets(Writer writer) throws IOException {
        for (String stylesheet : this.stylesheetUrls) {
            writer.append("<link href=\"").append(stylesheet).append("\" rel=\"stylesheet\">\n");
        }
    }

}
