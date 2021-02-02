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
 * An option matcher that matches based on actual value rather than strings
 * <p>
 * This allows for matching options where there are multiple ways of writing down a value. For example if you have an
 * option that is specified as a numeric value there could be multiple ways to specify the number e.g. {@code 1.0},
 * {@code 1}, {@code 01}
 * </p>
 *
 * @param <TOption> Option type
 */
public class ValueMatcher<TOption> implements PromptOptionMatcher<TOption> {

    private final Class<TOption> optionType;

    /**
     * Creates a new value matcher
     * @param optionType Option class
     */
    public ValueMatcher(Class<TOption> optionType) {
        if (optionType == null)
            throw new NullPointerException("optionType cannot be null");
        this.optionType = optionType;
    }

    @SuppressWarnings("unchecked")
    @Override
    public TOption match(Prompt<TOption> prompt, String response) throws PromptException {
        try {
            return (TOption) prompt.getTypeConverter().convert("", this.optionType, response);
        } catch (Throwable e) {
            throw new PromptException(
                    String.format("User provided prompt response '%s' which cannot be converted to the target type %s",
                            response, this.optionType.getCanonicalName()));
        }
    }

}
