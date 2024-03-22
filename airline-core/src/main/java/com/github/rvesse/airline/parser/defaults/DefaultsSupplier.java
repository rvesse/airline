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
package com.github.rvesse.airline.parser.defaults;

/**
 * A supplier of raw default values
 */
public interface DefaultsSupplier {

    /**
     * Gets the raw default value
     *
     * @param keys Keys to use to look up a default, in order of preference
     * @return Default value, if any, or {@code null} if no available default
     */
    String getDefaultValue(String... keys);

    /**
     * Describes the source of the default values for use in constructing help output
     *
     * @return Description
     */
    String description();
}
