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

import java.util.concurrent.TimeUnit;

import com.github.rvesse.airline.prompts.builders.PromptBuilder;
import com.github.rvesse.airline.prompts.console.ConsolePrompt;
import com.github.rvesse.airline.prompts.console.StdIOPrompt;

/**
 * Provides builds for some common
 *
 */
public class Prompts {

    /**
     * Gets the default provider
     * <p>
     * If {@link System#console()} is not {@code null} then {@link ConsolePrompt} will be used, otherwise
     * {@code StdIOPrompt} will be used
     * <p>
     * 
     * @return Default prompt provider
     */
    public static PromptProvider defaultProvider() {
        if (System.console() != null) {
            return new ConsolePrompt();
        } else {
            return new StdIOPrompt();
        }
    }

    /**
     * Creates a simple prompt using the default provider (from {@link #defaultProvider()}
     * 
     * @param <TOption>
     *            Option type
     * @return Prompt builder
     */
    public static <TOption> PromptBuilder<TOption> defaultPrompt() {
        //@formatter:off
        return new PromptBuilder<TOption>()
                    .withPromptProvider(defaultProvider())
                    .withoutTimeout();
        //@formatter:on
    }

    /**
     * Creates a free form question prompt
     * 
     * @param question
     *            Prompt question
     * @return Prompt builder
     */
    public static <TOption> PromptBuilder<TOption> newFreeFormPrompt(String question) {
        //@formatter:off
        return Prompts.<TOption> defaultPrompt()
                      .withPromptMessage(question)
                      .withQuestionFormatter();
        //@formatter:on
    }

    /**
     * Creates a Yes/No prompt
     * 
     * @param question
     *            Prompt question
     * @return Prompt builder
     */
    public static PromptBuilder<String> newYesNoPrompt(String question) {
        //@formatter:off
        return Prompts.<String>defaultPrompt()
                    .withPromptMessage(question)
                    .withOptions("Yes", "No")
                    .withQuestionFormatter();
        //@formatter:on
    }

    /**
     * Creates a Yes/No/Abort prompt
     * 
     * @param question
     *            Prompt question
     * @return Prompt builder
     */
    public static PromptBuilder<String> newYesNoAbortPrompt(String question) {
        //@formatter:off
        return Prompts.<String>defaultPrompt()
                    .withPromptMessage(question)
                    .withOptions("Yes", "No", "Abort")
                    .withQuestionFormatter();
        //@formatter:on
    }

    /**
     * Creates a new Yes/No/Cancel prompt
     * 
     * @param question
     *            Prompt question
     * @return Prompt builder
     */
    public static PromptBuilder<String> newYesNoCancelPrompt(String question) {
        //@formatter:off
        return Prompts.<String>defaultPrompt()
                    .withPromptMessage(question)
                    .withOptions("Yes", "No", "Cancel")
                    .withQuestionFormatter();
        //@formatter:on
    }

    /**
     * Creates a new list prompt
     * 
     * @param <TOption>
     *            Option type
     * @param message
     *            Prompt message
     * @param options
     *            Options
     * @return Prompt builder
     */
    @SuppressWarnings("unchecked")
    public static <TOption> PromptBuilder<TOption> newOptionsPrompt(String message, TOption... options) {
        //@formatter:off
        return Prompts.<TOption>defaultPrompt()
                    .withPromptMessage(message)
                    .withOptions(options)
                    .withListFormatter();
        //@formatter:on
    }
}
