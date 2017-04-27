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
package com.github.rvesse.airline.builder;

import com.github.rvesse.airline.utils.StringUtils;

/**
 * Abstract base class for builders
 *
 * @param <T>
 *            Type that the builder will produce
 */
public abstract class AbstractBuilder<T> {

    /**
     * Checks a value given for a parameter is not null
     * 
     * @param value
     *            Value
     * @param paramName
     *            Parameter
     */
    protected final void checkNotNull(String value, String paramName) {
        if (value == null)
            throw new NullPointerException(String.format("%s cannot be null", paramName));
    }

    /**
     * Checks a value given for a parameter is not null/empty
     * 
     * @param value
     *            Value
     * @param paramName
     *            Parameter
     */
    protected final void checkNotEmpty(String value, String paramName) {
        if (StringUtils.isEmpty(value))
            throw new IllegalArgumentException(String.format("%s cannot be null/empty", paramName));
    }

    /**
     * Checks a value given for a parameter is not blank i.e. not null, empty or
     * all whitespace
     * 
     * @param value
     *            Value
     * @param paramName
     *            Parameter
     */
    protected final void checkNotBlank(String value, String paramName) {
        if (StringUtils.isBlank(value))
            throw new IllegalArgumentException(String.format("%s cannot be null/empty/whitespace", paramName));
    }

    /**
     * Builds the type
     * 
     * @return Type instance
     */
    public abstract T build();
}
