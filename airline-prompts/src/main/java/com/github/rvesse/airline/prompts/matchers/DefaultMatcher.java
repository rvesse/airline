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

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.Predicate;

import com.github.rvesse.airline.prompts.Prompt;
import com.github.rvesse.airline.prompts.errors.PromptException;

/**
 * Default prompt option matcher
 * <p>
 * This matcher tries to find the most likely options using either exact matching or partial matching. It also allows
 * for numeric option selection when the prompt has enabled that feature.
 * </p>
 *
 * @param <TOption>
 *            Option type
 */
public class DefaultMatcher<TOption> implements PromptOptionMatcher<TOption> {

    @Override
    public TOption match(Prompt<TOption> prompt, final String response) throws PromptException {
        if (prompt.allowsNumericOptionSelection()) {
            IndexMatcher<TOption> indexMatcher = new IndexMatcher<>();
            try {
                return indexMatcher.match(prompt, response);
            } catch (PromptException e) {
                // Ignore and fall back to string matching
            }
        }

        Set<TOption> foundOptions = new HashSet<>(prompt.getOptions());
        CollectionUtils.filter(foundOptions, getExactOrPartialMatcher(response));

        if (foundOptions.size() == 0) {
            // No matches
            throw MatcherUtils.invalidResponse(response);
        } else if (foundOptions.size() > 1) {
            // Multiple possible matches
            // Was there instead a single exact match?
            CollectionUtils.<TOption> filter(foundOptions, getExactMatcher(response));
            if (foundOptions.size() == 1) {
                // One exact match
                return IterableUtils.first(foundOptions);
            }

            // Ambiguous match
            throw MatcherUtils.ambiguousResponse(response);
        } else {
            // Exactly one match
            return IterableUtils.first(foundOptions);
        }
    }

    /**
     * Gets an exact matcher
     * 
     * @param response
     *            User response to match options against
     * @return Exact matcher
     */
    protected Predicate<TOption> getExactMatcher(final String response) {
        return new MatcherUtils.Exact<TOption>(response);
    }

    /**
     * Gets an exact or partial matcher
     * 
     * @param response
     *            User response to match options against
     * @return Exact or partial matcher
     */
    protected Predicate<TOption> getExactOrPartialMatcher(final String response) {
        return new MatcherUtils.ExactOrStartsWith<TOption>(response);
    }

}
