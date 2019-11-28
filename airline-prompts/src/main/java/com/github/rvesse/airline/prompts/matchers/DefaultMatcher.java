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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.lang3.StringUtils;

import com.github.rvesse.airline.prompts.Prompt;
import com.github.rvesse.airline.prompts.errors.PromptException;

public class DefaultMatcher<TOption> implements PromptOptionMatcher<TOption> {

    protected static final class Exact<TOption> implements Predicate<TOption> {
        private final String response;

        protected Exact(String response) {
            this.response = response;
        }

        @Override
        public boolean evaluate(TOption object) {
            String optionStr = object.toString();
            return StringUtils.equals(response, optionStr);
        }
    }

    protected static final class ExactIgnoresCase<TOption> implements Predicate<TOption> {
        private final String response;

        protected ExactIgnoresCase(String response) {
            this.response = response;
        }

        @Override
        public boolean evaluate(TOption object) {
            String optionStr = object.toString();
            return StringUtils.equalsIgnoreCase(response, optionStr);
        }
    }

    protected static final class ExactOrStartsWith<TOption> implements Predicate<TOption> {
        private final String response;

        protected ExactOrStartsWith(String response) {
            this.response = response;
        }

        @Override
        public boolean evaluate(TOption object) {
            String optionStr = object.toString();
            return StringUtils.equals(response, optionStr) || optionStr.startsWith(response);
        }
    }

    protected static final class ExactOrStartsWithIgnoresCase<TOption> implements Predicate<TOption> {
        private final String response;

        protected ExactOrStartsWithIgnoresCase(String response) {
            this.response = response;
        }

        @Override
        public boolean evaluate(TOption object) {
            String optionStr = object.toString();
            return StringUtils.equalsIgnoreCase(response, optionStr)
                    || optionStr.toLowerCase(Locale.ROOT).startsWith(response.toLowerCase(Locale.ROOT));
        }
    }

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

        List<TOption> foundOptions = new ArrayList<>(prompt.getOptions());
        CollectionUtils.filter(foundOptions, getExactOrPartialMatcher(response));

        if (foundOptions.size() == 0) {
            throw new PromptException(
                    String.format("User provided prompt response '%s' which is not a valid response", response));
        } else if (foundOptions.size() > 1) {
            // Was there instead a single exact match?
            CollectionUtils.<TOption> filter(foundOptions, getExactMatcher(response));
            if (foundOptions.size() == 1) {
                return foundOptions.get(0);
            }

            throw new PromptException(String.format(
                    "User provided prompt response '%s' which is does not unambiguously identify a single response",
                    response));
        } else {
            return foundOptions.get(0);
        }
    }

    protected Predicate<TOption> getExactMatcher(final String response) {
        return new Exact<TOption>(response);
    }

    protected Predicate<TOption> getExactOrPartialMatcher(final String response) {
        return new ExactOrStartsWith<TOption>(response);
    }

}
