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

import java.util.Objects;
import java.util.Properties;

public abstract class AbstractPropertiesSupplier
        extends AbstractDefaultsSupplier {
    private final Properties properties;

    public AbstractPropertiesSupplier(Properties properties) {
        this.properties = Objects.requireNonNull(properties);
    }

    @Override
    protected final String getDefaultValue(String key) {
        return this.properties.getProperty(key);
    }
}
