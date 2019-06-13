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
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.github.rvesse.airline.prompts.errors.PromptException;
import com.github.rvesse.airline.types.DefaultTypeConverter;
import com.github.rvesse.airline.types.TypeConverter;

public class Prompt<TPrompt> {

    private final PromptProvider provider;
    private final long timeout;
    private final TimeUnit timeoutUnit;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final List<TPrompt> options;
    private final String message;
    private final PromptType type;
    private final TypeConverter converter;
    
    public Prompt(PromptProvider provider, long timeout, TimeUnit timeoutUnit, String promptMessage, Collection<TPrompt> options, PromptType type, TypeConverter converter) {
        this.provider = provider;
        this.timeout = timeout;
        this.timeoutUnit = timeoutUnit;
        this.message = promptMessage;
        this.options = new ArrayList<>(options);
        this.type = type;
        this.converter = converter != null ? converter : new DefaultTypeConverter();
    }
    
    private void displayPrompt() {
        
    }
    
    public int promptForKey() throws PromptException {
        if (this.type != PromptType.KEY)
            throw new UnsupportedOperationException(String.format("Cannot prompt for a key with prompt type %s", this.type));
        
        this.displayPrompt();
        if (this.timeout > 0) {
            Callable<Integer> bgPrompt = new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    return provider.readKey();
                }
            };
            Future<Integer> future = this.executor.submit(bgPrompt);
            try {
                return future.get(this.timeout, this.timeoutUnit).intValue();
            } catch (InterruptedException e) {
                throw new PromptException("Interrupted while waiting for prompt response", e);
            } catch (ExecutionException e) {
                throw new PromptException("Failed to get prompt response", e);
            } catch (TimeoutException e) {
                throw new PromptException("Failed to receive a response to prompt within specified timeout", e);
            }
        } else {
            return this.provider.readKey();
        }
    }
    
    public String promptForLine() throws PromptException {
        return null;
    }
    
    @SuppressWarnings("unchecked")
    public <T> T promptForValue(Class<T> cls) throws PromptException {
        return (T) this.converter.convert("", cls, this.promptForLine());
    }
    
    public char[] promptForSecure() throws PromptException {
        return null;
    }
    
}
