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

/**
 * A prompt builder is used to define a prompt in a Fluent API style
 *
 * @param <TOption>
 *            Option
 */
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

    /**
     * Specifies that the default prompt provider should be used
     * 
     * @return Builder
     */
    public PromptBuilder<TOption> withDefaultPromptProvider() {
        this.provider = Prompts.defaultProvider();
        return this;
    }

    /**
     * Specifies that the given prompt provider should be used
     * 
     * @param provider
     *            Prompt provider
     * @return Builder
     */
    public PromptBuilder<TOption> withPromptProvider(PromptProvider provider) {
        this.provider = provider;
        return this;
    }

    /**
     * Specifies the time unit used to apply timeouts to prompts
     * 
     * @param unit
     *            Time Unit
     * @return Builder
     */
    public PromptBuilder<TOption> withTimeoutUnit(TimeUnit unit) {
        this.timeoutUnit = unit;
        return this;
    }

    /**
     * Specifies the timeout
     * 
     * @param timeout
     *            Timeout
     * @return Builder
     */
    public PromptBuilder<TOption> withTimeout(long timeout) {
        this.timeout = timeout;
        return this;
    }

    /**
     * Specifies the timeout
     * 
     * @param timeout
     *            Timeout
     * @param unit
     *            Time Unit
     * @return Builder
     */
    public PromptBuilder<TOption> withTimeout(long timeout, TimeUnit unit) {
        this.timeout = timeout;
        this.timeoutUnit = unit;
        return this;
    }

    /**
     * Specifies one/more options for the prompt
     * 
     * @param options
     *            Options
     * @return Builder
     */
    @SuppressWarnings("unchecked")
    public PromptBuilder<TOption> withOptions(TOption... options) {
        for (TOption opt : options) {
            this.options.add(opt);
        }
        return this;
    }

    /**
     * Specifies an option for the prompt
     * 
     * @param option
     *            Option
     * @return Builder
     */
    public PromptBuilder<TOption> withOption(TOption option) {
        this.options.add(option);
        return this;
    }

    /**
     * Clears all previously specified options
     * 
     * @return Builder
     */
    public PromptBuilder<TOption> clearOptions() {
        this.options.clear();
        return this;
    }

    /**
     * Enables numeric option selection
     * 
     * @return Builder
     */
    public PromptBuilder<TOption> withNumericOptionSelection() {
        this.allowsNumericOptionSelection = true;
        return this;
    }

    /**
     * Disables numeric option selection
     * 
     * @return Builder
     */
    public PromptBuilder<TOption> withoutNumericOptionSelection() {
        this.allowsNumericOptionSelection = false;
        return this;
    }

    /**
     * Specifies the option matcher to use
     * 
     * @param matcher
     *            Option matcher
     * @return Builder
     */
    public PromptBuilder<TOption> withOptionMatcher(PromptOptionMatcher<TOption> matcher) {
        this.optionMatcher = matcher;
        return this;
    }

    /**
     * Specifies the prompt message to display
     * 
     * @param message
     *            Message
     * @return Builder
     */
    public PromptBuilder<TOption> withPromptMessage(String message) {
        this.message = message;
        return this;
    }

    /**
     * Specifies the prompt formatter to use
     * 
     * @param formatter
     *            Formatter
     * @return Builder
     */
    public PromptBuilder<TOption> withFormatter(PromptFormatter formatter) {
        this.formatter = formatter;
        this.formatBuilder = null;
        return this;
    }

    /**
     * Specifies the prompt formatter builder to use
     * 
     * @param formatBuilder
     *            Format Builder
     * @return Builder
     */
    public PromptBuilder<TOption> withFormatBuilder(PromptFormatBuilder<TOption> formatBuilder) {
        this.formatter = null;
        this.formatBuilder = formatBuilder;
        return this;
    }

    /**
     * Switches to a child builder for the prompt format
     * 
     * @return Prompt Format Builder
     */
    public PromptFormatBuilder<TOption> withFormatBuilder() {
        if (this.formatBuilder != null) {
            return this.formatBuilder;
        } else {
            return this.withListFormatBuilder();
        }
    }

    /**
     * Switches to a child builder for the list formatter
     * 
     * @return List Format Builder
     */
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

    /**
     * Specifies that a list formatter should be used
     * 
     * @return Builder
     */
    public PromptBuilder<TOption> withListFormatter() {
        this.formatBuilder = new ListFormatBuilder<>(this);
        this.formatter = null;
        return this;
    }

    /**
     * Specifies that a question formatter should be used
     * 
     * @return Builder
     */
    public PromptBuilder<TOption> withQuestionFormatter() {
        this.formatBuilder = null;
        this.formatter = new QuestionFormat<>();
        return this;
    }

    /**
     * Specifies that the default formatter should be used, this is the list formatter
     * 
     * @return Builder
     */
    public PromptBuilder<TOption> withDefaultFormatter() {
        this.formatter = null;
        this.formatBuilder = new ListFormatBuilder<>(this);
        return this;
    }

    /**
     * Specifies the type converter to use
     * 
     * @param converter
     *            Type Converter
     * @return Builder
     */
    public PromptBuilder<TOption> withTypeConverter(TypeConverter converter) {
        this.converter = converter;
        return this;
    }

    /**
     * Specifies that the default type converter be used
     * 
     * @return
     */
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
        return new Prompt<TOption>(this.provider, promptFormatter, this.timeout, this.timeoutUnit, this.message,
                this.options, this.optionMatcher, this.allowsNumericOptionSelection, this.converter);
    }

}
