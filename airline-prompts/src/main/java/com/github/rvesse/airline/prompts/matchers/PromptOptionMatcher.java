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

package com.github.rvesse.airline.prompts.matchers;

import com.github.rvesse.airline.prompts.Prompt;
import com.github.rvesse.airline.prompts.errors.PromptException;

/**
 * Interfaces for option matchers that controls how options are matched to the
 * prompt response when using {@link Prompt#promptForOption(boolean)}
 *
 * @param <TOption>
 *            Option type
 */
public interface PromptOptionMatcher<TOption> {

    /**
     * Matches the response to an option provided by the prompt
     * 
     * @param prompt
     *            Prompt
     * @param response
     *            Response value that has been read from the prompt
     * @return Matched option
     * @throws PromptException
     *             Thrown if the response value does not match any option
     */
    public TOption match(Prompt<TOption> prompt, String response) throws PromptException;
}
