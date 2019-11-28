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

import org.apache.commons.lang3.StringUtils;

import com.github.rvesse.airline.io.printers.UsagePrinter;
import com.github.rvesse.airline.prompts.Prompt;

/**
 * Prompt format for simple questions with a limited number of options
 *
 * @param <TOption>
 *            Option type
 */
public class QuestionFormat<TOption> implements PromptFormatter {

    private final int columns;

    public QuestionFormat() {
        this(80);
    }

    public QuestionFormat(int columns) {
        this.columns = columns;
    }

    @Override
    public <T> void displayPrompt(Prompt<T> prompt) {
        UsagePrinter printer = new UsagePrinter(prompt.getProvider().getPromptStream(), this.columns);
        printer.append(String.format("%s? [%s] ", prompt.getMessage(), StringUtils.join(prompt.getOptions(), "/")));
        printer.flush();
    }

}
