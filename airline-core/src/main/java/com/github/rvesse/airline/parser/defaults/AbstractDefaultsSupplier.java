/*
 * Copyright (C) 2010-22 the original author or authors.
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

public abstract class AbstractDefaultsSupplier implements DefaultsSupplier {
    public final String getDefaultValue(String... keys) {
        for (String key : keys) {
            String value = getDefaultValue(key);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    protected abstract String getDefaultValue(String key);
}
