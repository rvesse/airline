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

package com.github.rvesse.airline.prompts.builders;

import com.github.rvesse.airline.prompts.formatters.ListFormat;

/**
 * A builder that helps building list formats
 *
 * @param <TOption> Option type
 */
public class ListFormatBuilder<TOption> extends PromptFormatBuilder<TOption> {

    /**
     * Default columns used to display list options
     */
    public static final int DEFAULT_COLUMNS = 80;
    private int columns = DEFAULT_COLUMNS;

    public ListFormatBuilder(PromptBuilder<TOption> parentBuilder) {
        super(parentBuilder);
    }
    
    public ListFormatBuilder<TOption> withColumns(int columns) {
        this.columns = columns;
        return this;
    }
    
    public ListFormatBuilder<TOption> withDefaultColumns() {
        return withColumns(80);
    }

    @Override
    public ListFormat<TOption> build() {
        return new ListFormat<>(columns);
    }

}
