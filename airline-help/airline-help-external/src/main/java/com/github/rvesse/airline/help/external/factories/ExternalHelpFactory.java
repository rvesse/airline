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

package com.github.rvesse.airline.help.external.factories;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;

import com.github.rvesse.airline.annotations.help.external.ExternalExamples;
import com.github.rvesse.airline.annotations.help.external.ExternalExitCodes;
import com.github.rvesse.airline.annotations.help.external.ExternalProse;
import com.github.rvesse.airline.annotations.help.external.ExternalTabularExamples;
import com.github.rvesse.airline.help.external.parsers.ParagraphsParser;
import com.github.rvesse.airline.help.external.parsers.TabularParser;
import com.github.rvesse.airline.help.external.parsers.defaults.DefaultExternalHelpParser;
import com.github.rvesse.airline.help.sections.HelpSection;
import com.github.rvesse.airline.help.sections.common.ExamplesSection;
import com.github.rvesse.airline.help.sections.common.ExitCodesSection;
import com.github.rvesse.airline.help.sections.common.ProseSection;
import com.github.rvesse.airline.help.sections.factories.HelpSectionFactory;
import com.github.rvesse.airline.parser.ParserUtil;
import com.github.rvesse.airline.parser.resources.ResourceLocator;

/**
 * Help section factory that enables the additional annotations provided by this module
 *
 */
public class ExternalHelpFactory implements HelpSectionFactory {

    private final DefaultExternalHelpParser defaultParser = new DefaultExternalHelpParser();

    /**
     * Supported annotations
     */
    //@formatter:off
    private static final List<Class<? extends Annotation>> SUPPORTED 
        = Arrays.<Class<? extends Annotation>>asList(
                    ExternalExamples.class,
                    ExternalTabularExamples.class,
                    ExternalProse.class,
                    ExternalExitCodes.class
                );
    //@formatter:on

    @Override
    public HelpSection createSection(Annotation annotation) {
        if (annotation instanceof ExternalProse) {
            // External Prose e.g. Discussion
            ExternalProse extProse = (ExternalProse) annotation;
            String[] paragraphs = parseParagraphs(ParserUtil.createResourceLocators(extProse.sourceLocators()),
                    extProse.source(), loadParagraphsParser(extProse.parser()));
            return new ProseSection(extProse.title(), extProse.suggestedOrder(), paragraphs);

        } else if (annotation instanceof ExternalExitCodes) {
            // External Exit Codes (Tabular Data)
            ExternalExitCodes exitCodes = (ExternalExitCodes) annotation;
            List<List<String>> rawExitCodes = parseTabular(
                    ParserUtil.createResourceLocators(exitCodes.sourceLocators()), exitCodes.source(),
                    loadTabularParser(exitCodes.parser()));

            // Translate the tabular data into the two column arrays that our ExitCodesSection expects
            int[] parsedExitCodes = rowToNumericArray(rawExitCodes, 0, exitCodes.source());
            String[] exitCodeDescriptions = rowToArray(rawExitCodes, 1);
            return new ExitCodesSection(parsedExitCodes, exitCodeDescriptions);

        } else if (annotation instanceof ExternalExamples) {
            // External Examples (Textual Data)
            ExternalExamples extExamples = (ExternalExamples) annotation;
            ParagraphsParser parser = loadParagraphsParser(extExamples.parser());
            ResourceLocator[] locators = ParserUtil.createResourceLocators(extExamples.sourceLocators());
            String[] examples = parseParagraphs(locators, extExamples.exampleSource(), parser);
            String[] descriptions = parseParagraphs(locators, extExamples.descriptionSource(), parser);

            return new ExamplesSection(examples, descriptions);

        } else if (annotation instanceof ExternalTabularExamples) {
            // External Examples (Tabular Data)
            ExternalTabularExamples extExamples = (ExternalTabularExamples) annotation;
            List<List<String>> rows = parseTabular(ParserUtil.createResourceLocators(extExamples.sourceLocators()),
                    extExamples.source(), loadTabularParser(extExamples.parser()));

            // Translate into the two column array into the two arrays we need
            String[] examples = rowToArray(rows, 0);
            String[] descriptions = rowToArray(rows, 1);

            return new ExamplesSection(examples, descriptions);
        }

        return null;
    }

    @Override
    public List<Class<? extends Annotation>> supportedAnnotations() {
        return SUPPORTED;
    }

    /**
     * Opens the specified resource using the first resource locator that is able to open it
     * 
     * @param resourceLocators
     *            Resource locators to use
     * @param resource
     *            Resource
     * @return Input stream for the resource or {@code null} if it cannot be opened
     * @throws IOException
     *             Thrown if there's a problem opening the resource
     */
    protected InputStream openResource(ResourceLocator[] resourceLocators, String resource) throws IOException {
        for (ResourceLocator locator : resourceLocators) {
            if (locator == null)
                continue;

            InputStream input = locator.open(resource, "");
            if (input == null)
                continue;

            return input;
        }

        return null;
    }

    /**
     * Loads the paragraphs parser
     * 
     * @param parserCls
     *            Parser Class
     * @return Parser instance
     */
    protected ParagraphsParser loadParagraphsParser(Class<? extends ParagraphsParser> parserCls) {
        try {
            return parserCls.newInstance();
        } catch (Throwable e) {
            throw new IllegalArgumentException(
                    String.format("Annotation specifies paragraph parser %s which could not be instantiated",
                            parserCls.getCanonicalName()));
        }
    }

    /**
     * Parses paragraphs from a resource
     * 
     * @param resourceLocators
     *            Resource locators
     * @param resource
     *            Resource
     * @param parser
     *            Parser
     * @return Paragraphs
     */
    protected String[] parseParagraphs(ResourceLocator[] resourceLocators, String resource, ParagraphsParser parser) {
        if (resourceLocators.length == 0)
            throw new IllegalArgumentException(
                    String.format("Failed to locate resource %s as no resource locators were configured", resource));

        InputStream input;
        try {
            input = openResource(resourceLocators, resource);
        } catch (IOException e) {
            throw new IllegalArgumentException(
                    String.format("Failed to open resource %s - %s", resource, e.getMessage()), e);
        }
        if (input == null)
            throw new IllegalArgumentException(String
                    .format("Failed to locate resource %s with any of the configured resource locators", resource));

        return parser != null ? parser.parseParagraphs(resource, input)
                : this.defaultParser.parseParagraphs(resource, input);
    }

    /**
     * Loads the tabular parser
     * 
     * @param parserCls
     *            Parser class
     * @return Parser instance
     */
    protected TabularParser loadTabularParser(Class<? extends TabularParser> parserCls) {
        try {
            return parserCls.newInstance();
        } catch (Throwable e) {
            throw new IllegalArgumentException(
                    String.format("Annotation specifies tabular parser %s which could not be instantiated",
                            parserCls.getCanonicalName()));
        }
    }

    /**
     * Parses tabular data from a resource
     * 
     * @param resourceLocators
     *            Resource locators
     * @param resource
     *            Resource
     * @param parser
     *            Parser
     * @return Tabular data
     */
    protected List<List<String>> parseTabular(ResourceLocator[] resourceLocators, String resource,
            TabularParser parser) {
        if (resourceLocators.length == 0)
            throw new IllegalArgumentException(
                    String.format("Failed to locate resource %s as no resource locators were configured", resource));
        
        InputStream input;
        try {
            input = openResource(resourceLocators, resource);
        } catch (IOException e) {
            throw new IllegalArgumentException(
                    String.format("Failed to open resource %s - %s", resource, e.getMessage()), e);
        }
        if (input == null)
            throw new IllegalArgumentException(String
                    .format("Failed to locate resource %s with any of the configured resource locators", resource));

        return parser != null ? parser.parseRows(resource, input) : this.defaultParser.parseRows(resource, input);
    }

    /**
     * Converts one column of a list of rows into an array
     * 
     * @param rows
     *            Rows
     * @param columnIndex
     *            Column index of the column to convert
     * @return Column array
     */
    protected String[] rowToArray(List<List<String>> rows, int columnIndex) {
        String[] data = new String[rows.size()];

        for (int i = 0; i < data.length; i++) {
            List<String> row = rows.get(i);
            // Bad row
            if (row == null)
                continue;
            // Too few columns in the row so leave this entry in the array blank
            if (columnIndex >= row.size())
                continue;

            // Copy column data from row into array
            data[i] = row.get(columnIndex);
        }

        return data;
    }

    /**
     * Converts one column of a list of rows into an array
     * 
     * @param rows
     *            Rows
     * @param columnIndex
     *            Column index of the column to convert
     * @return Column array
     */
    protected int[] rowToNumericArray(List<List<String>> rows, int columnIndex, String resource) {
        int[] data = new int[rows.size()];

        for (int i = 0; i < data.length; i++) {
            List<String> row = rows.get(i);
            // Bad row
            if (row == null)
                continue;
            // Too few columns in the row so leave this entry in the array blank
            if (columnIndex >= row.size())
                continue;

            try {
                // Copy column data from row into array
                data[i] = Integer.parseInt(row.get(columnIndex));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(String.format(
                        "Tabular data from resource %s has invalid value %s in the column index %d at row index %d where an integer exit code was expected",
                        resource, row.get(columnIndex), columnIndex, i));
            }
        }

        return data;
    }

}
