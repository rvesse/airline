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

package com.github.rvesse.airline.help.external.parsers.defaults;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;

import com.github.rvesse.airline.help.external.parsers.ParagraphsParser;
import com.github.rvesse.airline.help.external.parsers.TabularParser;

/**
 * Default external help parser
 *
 */
public class DefaultExternalHelpParser implements ParagraphsParser, TabularParser {

    @Override
    public String[] parseParagraphs(String resource, InputStream input) {
        if (input == null) {
            throw new IllegalArgumentException(
                    String.format("Annotation references resource %s which could not be loaded", resource));
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

            reader.close();
        } catch (IOException e) {
            throw new IllegalArgumentException(
                    String.format("Failed to read from resource %s: %s", resource, e.getMessage()), e);
        }

        // Trim out any excess new lines within paragraphs
        for (int i = 0; i < paragraphs.size(); i++) {
            paragraphs.set(i, paragraphs.get(i).replace('\n', ' '));
        }

        return paragraphs.toArray(new String[0]);
    }

    /**
     * Gets the tabular format in use for parsing tabular data
     * 
     * @return Tabular Format
     */
    protected CSVFormat getTabularFormat() {
        return CSVFormat.DEFAULT;
    }

    @Override
    public List<List<String>> parseRows(String resource, InputStream input) {
        if (input == null)
            throw new IllegalArgumentException(
                    String.format("Annotation references resource %s which could not be loaded", resource));

        List<List<String>> rows = new ArrayList<>();

        try (InputStreamReader reader = new InputStreamReader(input)) {
            Iterable<CSVRecord> records = this.getTabularFormat().parse(reader);
            
            for (CSVRecord record : records) {
                List<String> row = new ArrayList<>();
                Iterator<String> recordIter = record.iterator();
                while (recordIter.hasNext()) {
                    row.add(recordIter.next());
                }
                
                rows.add(row);
            }
            
            reader.close();
        } catch (IOException e) {
            throw new IllegalArgumentException(
                    String.format("Failed to read from resource %s: %s", resource, e.getMessage()), e);
        }
        
        return rows;
    }
}
