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

import com.github.rvesse.airline.prompts.Prompt;

/**
 * Interface for prompt formatters
 *
 */
public interface PromptFormatter {

    /**
     * Displays the prompt
     * 
     * @param <TOption>
     *            Option type
     * @param prompt
     *            Prompt
     */
    public <TOption> void displayPrompt(Prompt<TOption> prompt);
}
