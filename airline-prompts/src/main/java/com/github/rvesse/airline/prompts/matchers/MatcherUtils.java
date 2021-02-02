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

import java.util.Locale;

import org.apache.commons.collections4.Predicate;
import org.apache.commons.lang3.StringUtils;

/**
 * Option matcher utility functions
 *
 */
public class MatcherUtils {

    public static final class Exact<TOption> implements Predicate<TOption> {
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

    public static final class ExactIgnoresCase<TOption> implements Predicate<TOption> {
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

    public static final class ExactOrStartsWith<TOption> implements Predicate<TOption> {
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

    public static final class ExactOrStartsWithIgnoresCase<TOption> implements Predicate<TOption> {
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

}
