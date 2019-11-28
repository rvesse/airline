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

public class IndexMatcher<TOption> implements PromptOptionMatcher<TOption> {

    @Override
    public TOption match(Prompt<TOption> prompt, String response) throws PromptException {
        if (!prompt.allowsNumericOptionSelection()) {
            throw new PromptException(
                    "Incorrectly configured IndexMatcher with a prompt that does not allow numeric option selection");
        }

        try {
            // Allow users to specify an option index using a 1-based index so
            // need to adjust accordingly
            int index = Integer.parseInt(response);
            index = index - 1;

            if (index >= 0 && index < prompt.getOptions().size()) {
                return prompt.getOptions().get(index);
            }
        } catch (NumberFormatException e) {
            // Ignore and fall back to simple string matching because our
            // options could be numeric values
        }

        throw new PromptException(String.format(
                "User provided prompt response '%s' which was not a valid 1 based index for an option", response));
    }

}
