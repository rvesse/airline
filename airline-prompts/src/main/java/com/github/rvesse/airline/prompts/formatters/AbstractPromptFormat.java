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

import com.github.rvesse.airline.io.printers.UsagePrinter;
import com.github.rvesse.airline.prompts.Prompt;

/**
 * An abstract implementation of a {@link PromptFormatter} that provides basic configuration of output columns and
 * overrideable helper method for formatting individual option values into strings
 *
 */
public abstract class AbstractPromptFormat implements PromptFormatter {
    protected final int columns;

    public AbstractPromptFormat(int columns) {
        this.columns = columns;
    }

    @Override
    public abstract <T> void displayPrompt(Prompt<T> prompt);

    /**
     * Formats a single option into a string for display
     * <p>
     * By default, this just calls {@link Object#toString()} on the option, you can create more advanced formatting of
     * options by overriding this format and overriding this method.  Note that the {@link #displayPrompt(Prompt)}
     * implementation of this format uses a {@link UsagePrinter} to print the options list so long string
     * representations may be wrapped across lines with a hanging indent.
     * </p>
     *
     * @param option Option
     * @param <T>    Option type
     * @return Formatted option
     */
    protected <T> String formatOption(T option) {
        return option.toString();
    }
}
