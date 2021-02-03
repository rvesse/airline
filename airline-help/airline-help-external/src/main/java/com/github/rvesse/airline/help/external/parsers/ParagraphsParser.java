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

package com.github.rvesse.airline.help.external.parsers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * Parser that translates a file into a series of paragraphs
 * <p>
 * A paragraph is a series of blocks of text separated by one/more blank lines.
 * </p>
 *
 */
public class ParagraphsParser {

    public String[] parseParagraphs(String resource) {
        InputStream input = ParagraphsParser.class.getResourceAsStream(resource);
        if (input == null) {
            throw new IllegalArgumentException(
                    String.format("Annotation references resource %s which is not a valid classpath resource"));
        }

        List<String> paragraphs = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
            String line = null;
            StringBuilder builder = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                if (StringUtils.isEmpty(line)) {
                    // End of paragraph
                    if (builder.length() > 0) {
                        // Trim trailing new line
                        builder.deleteCharAt(builder.length() - 1);
                        paragraphs.add(builder.toString());
                        builder = new StringBuilder();
                    }
                } else {
                    // Continuation of the current paragraph
                    builder.append(line);
                    builder.append('\n');
                }
            }

            // Possible trailing paragraph not terminated with a blank line
            if (builder.length() > 0) {
                // Trim trailing new line
                builder.deleteCharAt(builder.length() - 1);
                paragraphs.add(builder.toString());
            }
        } catch (IOException e) {
            throw new IllegalArgumentException(
                    String.format("Failed to read classpath resource %s - %s", resource, e.getMessage()), e);
        }

        // Trim out any excess new lines within paragraphs
        for (int i = 0; i < paragraphs.size(); i++) {
            paragraphs.set(i, paragraphs.get(i).replace('\n', ' '));
        }

        return paragraphs.toArray(new String[0]);
    }
}
