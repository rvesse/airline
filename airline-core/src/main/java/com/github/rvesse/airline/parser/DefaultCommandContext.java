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
package com.github.rvesse.airline.parser;

import com.github.rvesse.airline.Accessor;
import com.github.rvesse.airline.CommandContext;
import com.github.rvesse.airline.model.ArgumentsMetadata;
import com.github.rvesse.airline.model.OptionMetadata;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class DefaultCommandContext<T> implements CommandContext<T> {

    private Iterable<OptionMetadata> options;
    private List<Pair<OptionMetadata, Object>> parsedOptions;
    private ArgumentsMetadata arguments;
    private Iterable<Object> parsedArguments;
    private Iterable<Accessor> metadataInjection;
    private Map<Class<?>, Object> bindings;

    DefaultCommandContext(Iterable<OptionMetadata> options, List<Pair<OptionMetadata, Object>> parsedOptions, ArgumentsMetadata arguments, Iterable<Object> parsedArguments, Iterable<Accessor> metadataInjection, Map<Class<?>, Object> bindings) {
        this.options = options;
        this.parsedOptions = parsedOptions;
        this.arguments = arguments;
        this.parsedArguments = parsedArguments;
        this.metadataInjection = metadataInjection;
        this.bindings = bindings;
    }

    @Override
    public Map<Class<?>, Object> getBindings() {
        return bindings;
    }

    @Override
    public void processArguments(T commandInstance) {
        if (arguments != null && parsedArguments != null) {
            for (Accessor accessor : arguments.getAccessors()) {
                accessor.addValues(commandInstance, parsedArguments);
            }
        }
    }

    @Override
    public void processOptions(T commandInstance) {
        for (OptionMetadata option : options) {
            List<Object> values = new ArrayList<>();
            for (Pair<OptionMetadata, Object> parsedOption : parsedOptions) {
                if (option.equals(parsedOption.getLeft()))
                    values.add(parsedOption.getRight());
            }
            if (values != null && !values.isEmpty()) {
                for (Accessor accessor : option.getAccessors()) {
                    accessor.addValues(commandInstance, values);
                }
            }
        }
    }

    @Override
    public void processInjections(T commandInstance) {
        for (Accessor accessor : metadataInjection) {
            Object injectee = bindings.get(accessor.getJavaType());

            if (injectee != null) {
                accessor.addValues(commandInstance, ListUtils.unmodifiableList(Collections.singletonList(injectee)));
            }
        }
    }
}
