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

import com.github.rvesse.airline.annotations.help.ExternalExitCodes;
import com.github.rvesse.airline.annotations.help.ExternalProse;
import com.github.rvesse.airline.help.external.parsers.ParagraphsParser;
import com.github.rvesse.airline.help.external.parsers.TabularParser;
import com.github.rvesse.airline.help.external.parsers.defaults.DefaultExternalHelpParser;
import com.github.rvesse.airline.help.sections.HelpSection;
import com.github.rvesse.airline.help.sections.common.ExitCodesSection;
import com.github.rvesse.airline.help.sections.common.ProseSection;
import com.github.rvesse.airline.help.sections.factories.HelpSectionFactory;
import com.github.rvesse.airline.parser.ParserUtil;
import com.github.rvesse.airline.parser.resources.ResourceLocator;

/**
 * Help section factory that enables the additonal annotations provided by this module
 *
 */
public class ExternalHelpFactory implements HelpSectionFactory {

    private final DefaultExternalHelpParser defaultParser = new DefaultExternalHelpParser();

    //@formatter:off
    private static final List<Class<? extends Annotation>> SUPPORTED 
        = Arrays.<Class<? extends Annotation>>asList(
                    ExternalProse.class,
                    ExternalExitCodes.class
                );
    //@formatter:on

    @Override
    public HelpSection createSection(Annotation annotation) {
        if (annotation instanceof ExternalProse) {
            ExternalProse extProse = (ExternalProse) annotation;
            String[] paragraphs = parseParagraphs(ParserUtil.createResourceLocators(extProse.sourceLocators()),
                    extProse.source(), loadParagraphsParser(extProse.parser()));
            return new ProseSection(extProse.title(), extProse.suggestedOrder(), paragraphs);
        } else if (annotation instanceof ExternalExitCodes) {
            ExternalExitCodes exitCodes = (ExternalExitCodes) annotation;
            List<List<String>> rawExitCodes = parseTabular(
                    ParserUtil.createResourceLocators(exitCodes.sourceLocators()), exitCodes.source(),
                    loadTabularParser(exitCodes.parser()));

            // Translate the tabular data into the two column arrays that our ExitCodesSection expects
            int[] parsedExitCodes = new int[rawExitCodes.size()];
            String[] exitCodeDescriptions = new String[rawExitCodes.size()];
            for (int i = 0; i < rawExitCodes.size(); i++) {
                List<String> row = rawExitCodes.get(i);
                if (row.size() < 2) {
                    // Ignore rows with too few columns
                    continue;
                }
                try {
                    parsedExitCodes[i] = Integer.parseInt(row.get(0));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException(String.format(
                            "Tabular Exit Code data from resource %s has invalid value %s in the exit code column at row index %d where an integer exit code was expected",
                            exitCodes.source(), row.get(0), i));
                }
                exitCodeDescriptions[i] = row.get(1);
            }
            return new ExitCodesSection(parsedExitCodes, exitCodeDescriptions);
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

    protected ParagraphsParser loadParagraphsParser(Class<? extends ParagraphsParser> parserCls) {
        try {
            return parserCls.newInstance();
        } catch (Throwable e) {
            throw new IllegalArgumentException(
                    String.format("Annotation specifies paragraph parser %s which could not be instantiated",
                            parserCls.getCanonicalName()));
        }
    }

    protected String[] parseParagraphs(ResourceLocator[] resourceLocators, String resource, ParagraphsParser parser) {
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

    protected TabularParser loadTabularParser(Class<? extends TabularParser> parserCls) {
        try {
            return parserCls.newInstance();
        } catch (Throwable e) {
            throw new IllegalArgumentException(
                    String.format("Annotation specifies tabular parser %s which could not be instantiated",
                            parserCls.getCanonicalName()));
        }
    }

    protected List<List<String>> parseTabular(ResourceLocator[] resourceLocators, String resource,
            TabularParser parser) {
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

}
