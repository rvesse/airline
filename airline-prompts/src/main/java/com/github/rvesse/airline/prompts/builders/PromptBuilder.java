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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.github.rvesse.airline.builder.AbstractBuilder;
import com.github.rvesse.airline.prompts.Prompt;
import com.github.rvesse.airline.prompts.PromptProvider;
import com.github.rvesse.airline.prompts.Prompts;
import com.github.rvesse.airline.prompts.formatters.PromptFormatter;
import com.github.rvesse.airline.prompts.formatters.QuestionFormat;
import com.github.rvesse.airline.prompts.matchers.DefaultMatcher;
import com.github.rvesse.airline.prompts.matchers.PromptOptionMatcher;
import com.github.rvesse.airline.types.DefaultTypeConverter;
import com.github.rvesse.airline.types.TypeConverter;

public class PromptBuilder<TOption> extends AbstractBuilder<Prompt<TOption>> {

    private PromptProvider provider;
    private PromptFormatter formatter;
    private PromptFormatBuilder<TOption> formatBuilder = new ListFormatBuilder<>(this);
    private long timeout = 0;
    private TimeUnit timeoutUnit = TimeUnit.SECONDS;
    private boolean allowsNumericOptionSelection = true;
    private List<TOption> options = new ArrayList<TOption>();
    private PromptOptionMatcher<TOption> optionMatcher = new DefaultMatcher<>();
    private String message = null;
    private TypeConverter converter = new DefaultTypeConverter();

    public PromptBuilder() {
    }
    
    public PromptBuilder<TOption> withDefaultPromptProvider() {
        this.provider = Prompts.defaultProvider();
        return this;
    }

    public PromptBuilder<TOption> withPromptProvider(PromptProvider provider) {
        this.provider = provider;
        return this;
    }

    public PromptBuilder<TOption> withTimeoutUnit(TimeUnit unit) {
        this.timeoutUnit = unit;
        return this;
    }

    public PromptBuilder<TOption> withTimeout(long timeout) {
        this.timeout = timeout;
        return this;
    }

    public PromptBuilder<TOption> withTimeout(long timeout, TimeUnit unit) {
        this.timeout = timeout;
        this.timeoutUnit = unit;
        return this;
    }

    @SuppressWarnings("unchecked")
    public PromptBuilder<TOption> withOptions(TOption... options) {
        for (TOption opt : options) {
            this.options.add(opt);
        }
        return this;
    }

    public PromptBuilder<TOption> withOption(TOption option) {
        this.options.add(option);
        return this;
    }

    public PromptBuilder<TOption> clearOptions() {
        this.options.clear();
        return this;
    }

    public PromptBuilder<TOption> withNumericOptionSelection() {
        this.allowsNumericOptionSelection = true;
        return this;
    }

    public PromptBuilder<TOption> withoutNumericOptionSelection() {
        this.allowsNumericOptionSelection = false;
        return this;
    }

    public PromptBuilder<TOption> withOptionMatcher(PromptOptionMatcher<TOption> matcher) {
        this.optionMatcher = matcher;
        return this;
    }

    public PromptBuilder<TOption> withPromptMessage(String message) {
        this.message = message;
        return this;
    }

    public PromptBuilder<TOption> withFormatter(PromptFormatter formatter) {
        this.formatter = formatter;
        this.formatBuilder = null;
        return this;
    }

    public PromptBuilder<TOption> withFormatBuilder(PromptFormatBuilder<TOption> formatBuilder) {
        this.formatter = null;
        this.formatBuilder = formatBuilder;
        return this;
    }
    
    public PromptFormatBuilder<TOption> withFormatBuilder() {
        if (this.formatBuilder != null) {
            return this.formatBuilder;
        } else {
            return this.withListFormatBuilder();
        }
    }
    
    public ListFormatBuilder<TOption> withListFormatBuilder() {
        if (this.formatBuilder != null) {
            if (this.formatBuilder instanceof ListFormatBuilder<?>) {
                return (ListFormatBuilder<TOption>) this.formatBuilder;
            } else {
                this.formatBuilder = null;
                return withListFormatBuilder();
            }
        } else {
            this.formatter = null;
            this.formatBuilder = new ListFormatBuilder<>(this);
            return withListFormatter().withListFormatBuilder();
        }
    }

    public PromptBuilder<TOption> withListFormatter() {
        this.formatBuilder = new ListFormatBuilder<>(this);
        this.formatter = null;
        return this;
    }
    
    public PromptBuilder<TOption> withQuestionFormatter() {
        this.formatBuilder = null;
        this.formatter = new QuestionFormat<>();
        return this;
    }

    public PromptBuilder<TOption> withDefaultFormatter() {
        this.formatter = null;
        this.formatBuilder = new ListFormatBuilder<>(this);
        return this;
    }
    
    public PromptBuilder<TOption> withTypeConverter(TypeConverter converter) {
        this.converter = converter;
        return this;
    }
    
    public PromptBuilder<TOption> withDefaultTypeConverter() {
        this.converter = new DefaultTypeConverter();
        return this;
    }

    @Override
    public Prompt<TOption> build() {
        PromptFormatter promptFormatter = this.formatter;
        if (promptFormatter == null) {
            if (this.formatBuilder != null) {
                promptFormatter = this.formatBuilder.build();
            } else {
                throw new IllegalStateException("No prompt format specified");
            }
        }
        return new Prompt<TOption>(this.provider, promptFormatter, this.timeout, this.timeoutUnit, this.message, this.options,
                this.optionMatcher, this.allowsNumericOptionSelection, this.converter);
    }

}
