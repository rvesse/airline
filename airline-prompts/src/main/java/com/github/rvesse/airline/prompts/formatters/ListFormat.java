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
import com.github.rvesse.airline.prompts.builders.ListFormatBuilder;

/**
 * A prompt formatter that presents a list of options
 * <p>
 * This will format the options as a list, using columns
 * </p>
 *
 * @param <TOption> Option type
 */
public class ListFormat<TOption> implements PromptFormatter {
    
    private final int columns;
    
    /**
     * Creates a new list format with default columns
     */
    public ListFormat() {
        this(ListFormatBuilder.DEFAULT_COLUMNS);
    }
    
    /**
     * Creates a new list format with the specified number of columns
     * @param columns
     */
    public ListFormat(int columns) {
        this.columns = columns;
    }

    @Override
    public <T> void displayPrompt(Prompt<T> prompt) {
        UsagePrinter printer = new UsagePrinter(prompt.getProvider().getPromptWriter(), this.columns);
        printer.append(String.format("%s: ", prompt.getMessage()));
        printer.newline();
        printer.flush();
        
        UsagePrinter optionPrinter = printer.newIndentedPrinter(2).newPrinterWithHangingIndent(2);
        
        int index = 0;
        for (T option : prompt.getOptions()) {
            if (prompt.allowsNumericOptionSelection()) {
                optionPrinter.append(String.format("- %d) %s", ++index, option.toString()));
            } else {
                optionPrinter.append(String.format("- %s", option.toString()));
            }
            optionPrinter.newline();
        }
        optionPrinter.flush();
        printer.flush();
        
    }

}
