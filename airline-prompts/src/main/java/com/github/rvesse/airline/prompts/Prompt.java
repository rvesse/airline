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
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.github.rvesse.airline.prompts.errors.PromptException;
import com.github.rvesse.airline.prompts.formatters.PromptFormatter;
import com.github.rvesse.airline.prompts.matchers.DefaultMatcher;
import com.github.rvesse.airline.prompts.matchers.PromptOptionMatcher;
import com.github.rvesse.airline.types.DefaultTypeConverter;
import com.github.rvesse.airline.types.TypeConverter;

/**
 * Represents a prompt
 *
 * @param <TOption>
 *            Option type
 */
public class Prompt<TOption> {

    private final PromptProvider provider;
    private final PromptFormatter formatter;
    private final long timeout;
    private final boolean allowNumericOptionSelection;
    private final TimeUnit timeoutUnit;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final List<TOption> options;
    private final PromptOptionMatcher<TOption> optionMatcher;
    private final String message;
    private final TypeConverter converter;

    public Prompt(PromptProvider provider, PromptFormatter formatter, long timeout, TimeUnit timeoutUnit,
            String promptMessage, Collection<TOption> options, PromptOptionMatcher<TOption> optionMatcher,
            boolean allowNumericOptionSelection, TypeConverter converter) {
        this.provider = provider;
        this.formatter = formatter;
        this.timeout = timeout;
        this.timeoutUnit = timeoutUnit;
        this.message = promptMessage;
        this.options = options == null ? Collections.<TOption> emptyList()
                : Collections.unmodifiableList(new ArrayList<TOption>(options));
        this.optionMatcher = optionMatcher != null ? optionMatcher : new DefaultMatcher<TOption>();
        this.allowNumericOptionSelection = allowNumericOptionSelection;
        this.converter = converter != null ? converter : new DefaultTypeConverter();
    }

    /**
     * Gets the prompt provider
     * 
     * @return Provider
     */
    public PromptProvider getProvider() {
        return this.provider;
    }

    /**
     * Gets the prompt message
     * 
     * @return Message
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * Gets whether options can be selected numerically when using
     * {@link #promptForOption(boolean)}
     * 
     * @return True if numeric selection enabled, false otherwise
     */
    public boolean allowsNumericOptionSelection() {
        return this.allowNumericOptionSelection;
    }

    /**
     * Gets the available options (if any)
     * 
     * @return Options
     */
    public List<TOption> getOptions() {
        return this.options;
    }

    public PromptOptionMatcher<TOption> getOptionMatcher() {
        return this.optionMatcher;
    }

    /**
     * Gets the configured type converter
     * 
     * @return Type converter
     */
    public TypeConverter getTypeConverter() {
        return this.converter;
    }

    /**
     * Displays the prompt
     */
    private void displayPrompt() {
        this.formatter.displayPrompt(this);
    }

    /**
     * Wait for a prompt response
     * 
     * @param <T>
     *            Response type
     * @param future
     *            Future
     * @return Response type
     * @throws PromptException
     */
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

    /**
     * Prompts for a single key
     * 
     * @return Key code
     * @throws PromptException
     */
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

    /**
     * Prompts for a line of input
     * 
     * @return Input line
     * @throws PromptException
     */
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

    /**
     * Prompts for option
     * 
     * @param secure
     *            Does the response need to be secure?
     * @return Option value
     * @throws PromptException
     */
    public TOption promptForOption(boolean secure) throws PromptException {
        if (this.options.isEmpty())
            throw new PromptException("Cannot prompt for options as no options were configured");

        final String value = secure ? new String(this.promptForSecure()) : this.promptForLine();

        return this.optionMatcher.match(this, value);
    }

    /**
     * Prompts for a value
     * 
     * @param <T>
     *            Value type
     * @param cls
     *            Value type class
     * @param secure
     *            Does the response need to be secure?
     * @return Value
     * @throws PromptException
     */
    @SuppressWarnings("unchecked")
    public <T> T promptForValue(Class<T> cls, boolean secure) throws PromptException {
        String value = secure ? new String(this.promptForSecure()) : this.promptForLine();

        return (T) this.converter.convert("", cls, value);
    }

    /**
     * Prompts for a secure input line
     * 
     * @return Input line
     * @throws PromptException
     */
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
