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

package com.github.rvesse.airline.prompts;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.github.rvesse.airline.builder.AbstractBuilder;
import com.github.rvesse.airline.types.DefaultTypeConverter;
import com.github.rvesse.airline.types.TypeConverter;

public class PromptBuilder<TOption> extends AbstractBuilder<Prompt<TOption>> {

    private PromptProvider provider;
    private long timeout = 0;
    private TimeUnit timeoutUnit = TimeUnit.SECONDS;
    private List<TOption> options = new ArrayList<TOption>();
    private String message = null;
    private TypeConverter converter = new DefaultTypeConverter();
    private boolean withNumbering = false, withZeroIndex = false;
    private int columns = 80;
    
    public PromptBuilder() { }
    
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
    
    public PromptBuilder<TOption> withPromptMessage(String message) {
        this.message = message;
        return this;
    }

    @Override
    public Prompt<TOption> build() {
        return new Prompt<>(this.provider, timeout, timeoutUnit, this.message, options, this.withNumbering,
                this.withZeroIndex, columns, converter);
    }

}
