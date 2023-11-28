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

package com.github.rvesse.airline.prompts.formatters;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.github.rvesse.airline.io.printers.UsagePrinter;
import com.github.rvesse.airline.prompts.Prompt;
import com.github.rvesse.airline.prompts.builders.ListFormatBuilder;

/**
 * Prompt format for simple questions with either a free-form response or with a limited number of options
 *
 * @param <TOption>
 *            Option type
 */
public class QuestionFormat<TOption> implements PromptFormatter {

    private final int columns;

    /**
     * Creates a new question format with default columns
     */
    public QuestionFormat() {
        this(ListFormatBuilder.DEFAULT_COLUMNS);
    }

    /**
     * Creates a new question format with the specified columns
     * 
     * @param columns
     *            Columns
     */
    public QuestionFormat(int columns) {
        this.columns = columns;
    }

    @Override
    public <T> void displayPrompt(Prompt<T> prompt) {
        UsagePrinter printer = new UsagePrinter(prompt.getProvider().getPromptWriter(), this.columns);
        if (CollectionUtils.isNotEmpty(prompt.getOptions())) {
            printer.append(String.format("%s? [%s] ", prompt.getMessage(), StringUtils.join(prompt.getOptions(), "/")));
        } else {
            printer.append(String.format("%s? ", prompt.getMessage()));
        }
        printer.flush();
    }

}
