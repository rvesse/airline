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

import java.util.ArrayList;
import java.util.List;

import com.github.rvesse.airline.utils.StringUtils;

import com.github.rvesse.airline.model.AliasMetadata;

public class AliasBuilder<C> {
    private final String name;
    private final List<String> arguments = new ArrayList<String>();

    public AliasBuilder(String name) {
        if (StringUtils.isBlank(name))
            throw new IllegalArgumentException("Alias name cannot be null/empty/whitespace");
        this.name = name;
    }

    public AliasBuilder<C> withArgument(String arg) {
        if (StringUtils.isEmpty(arg))
            throw new IllegalArgumentException("Alias argument cannot be null");
        arguments.add(arg);
        return this;
    }

    public AliasBuilder<C> withArguments(String... args) {
        for (String arg : args) {
            if (arg == null)
                throw new NullPointerException("Alias argument cannot be null");
            arguments.add(arg);
        }
        return this;
    }

    public AliasMetadata build() {
        return new AliasMetadata(name, arguments);
    }
}
