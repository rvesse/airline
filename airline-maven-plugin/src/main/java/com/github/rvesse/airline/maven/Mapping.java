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
package com.github.rvesse.airline.maven;

import java.util.ServiceLoader;

import org.apache.maven.plugins.annotations.Parameter;

import com.github.rvesse.airline.maven.formats.FormatProvider;

public class Mapping {

    /**
     * Constant used in a mapping to indicate that the default provider may be
     * used
     */
    public static final String DEFAULT = "default";

    @Parameter(required = true)
    public String format;

    /**
     * Sets the class that implements the {@link FormatProvider} interface. If
     * {@code default} is provided as a value then the default
     * {@link ServiceLoader} discovered provider for the given format name is used
     */
    @Parameter(defaultValue = DEFAULT)
    public String provider;

    /**
     * Provides format specific options
     */
    @Parameter
    public RawFormatOptions options;
}
