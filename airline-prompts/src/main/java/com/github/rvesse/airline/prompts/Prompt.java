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

import com.github.rvesse.airline.io.printers.UsagePrinter;
import com.github.rvesse.airline.prompts.errors.PromptException;
import com.github.rvesse.airline.types.DefaultTypeConverter;
import com.github.rvesse.airline.types.TypeConverter;

public class Prompt<TOption> {

    private final PromptProvider provider;
    private final long timeout;
    private final TimeUnit timeoutUnit;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final List<TOption> options;
    private final String message;
    private final TypeConverter converter;
    private final boolean withNumbering, withZeroIndex;
    private final int columns;

    public Prompt(PromptProvider provider, long timeout, TimeUnit timeoutUnit, String promptMessage,
            Collection<TOption> options, boolean showOptionNumbers, boolean useZeroIndexing, int columns, TypeConverter converter) {
        this.provider = provider;
        this.timeout = timeout;
        this.timeoutUnit = timeoutUnit;
        this.message = promptMessage;
        this.options = new ArrayList<>(options);
        this.withNumbering = showOptionNumbers;
        this.withZeroIndex = useZeroIndexing;
        this.columns = columns;
        this.converter = converter != null ? converter : new DefaultTypeConverter();
    }

    private void displayPrompt() {
        UsagePrinter printer = new UsagePrinter(this.provider.getPromptStream(), this.columns);
        printer.append(String.format("%s: ", this.message));
        printer.flush();
        
        UsagePrinter optionPrinter = printer.newIndentedPrinter(2);
        
        int index = this.withZeroIndex ? 0 : 1;
        for (TOption option : this.options) {
            if (this.withNumbering) {
                optionPrinter.append(String.format("- %d) %s", index, option.toString()));
                index++;
            } else {
                optionPrinter.append(String.format("- %s", option.toString()));
            }
            optionPrinter.newline();
        }
        optionPrinter.flush();
        printer.flush();
    }
    
    protected <T> T waitForPromptResponse(Future<T> future) throws PromptException {
        try {
            return future.get(this.timeout, this.timeoutUnit);
        } catch (InterruptedException e) {
            throw new PromptException("Interrupted while waiting for prompt response", e);
        } catch (ExecutionException e) {
            throw new PromptException("Failed to get prompt response", e);
        } catch (TimeoutException e) {
            throw new PromptException("Failed to receive a response to prompt within specified timeout", e);
        }
    }

    public int promptForKey() throws PromptException {
        this.displayPrompt();
        
        if (this.timeout > 0) {
            Callable<Integer> bgPrompt = new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    return provider.readKey();
                }
            };
            Future<Integer> future = this.executor.submit(bgPrompt);
            return waitForPromptResponse(future).intValue();
        } else {
            return this.provider.readKey();
        }
    }

    public String promptForLine() throws PromptException {
        this.displayPrompt();
        
        if (this.timeout > 0) {
            Callable<String> bgPrompt = new Callable<String>() {
                @Override
                public String call() throws Exception {
                    return provider.readLine();
                }
            };
            Future<String> future = this.executor.submit(bgPrompt);
            return waitForPromptResponse(future);
        } else {
            return this.provider.readLine();
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T promptForValue(Class<T> cls) throws PromptException {
        return (T) this.converter.convert("", cls, this.promptForLine());
    }

    public char[] promptForSecure() throws PromptException {
        if (!this.provider.supportsSecureReads())
            throw new PromptException("Underlying prompt provider does not support secure reads");
        
        this.displayPrompt();
        
        if (this.timeout > 0) {
            Callable<char[]> bgPrompt = new Callable<char[]>() {
                
                @Override
                public char[] call() throws Exception {
                    return provider.readSecureLine();
                }
            };
            Future<char[]> future = this.executor.submit(bgPrompt);
            return waitForPromptResponse(future);
        } else {
            return this.provider.readSecureLine();
        }
    }

}
