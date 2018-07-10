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
package com.github.rvesse.airline.guice;

import com.github.rvesse.airline.CommandContext;
import com.github.rvesse.airline.model.CommandGroupMetadata;
import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.util.Providers;
import com.google.inject.util.Types;

import java.lang.reflect.ParameterizedType;
import java.util.Map;

/**
 * Guice module to bind injection bindings to google guice binder.
 *
 * @param <T> type of commands
 */
final class CommandModule<T> extends AbstractModule {

    private final Class<T> type;
    private final CommandContext<T> context;

    CommandModule(Class<T> type, CommandContext<T> context) {
        this.type = type;
        this.context = context;
    }

    @Override
    protected void configure() {
        Map<Class<?>, Object> bindings = context.getBindings();
        for (Map.Entry<Class<?>, Object> entry : bindings.entrySet()) {
            bindBinding(entry.getKey(), entry.getValue());
        }

        if (!bindings.containsKey(CommandGroupMetadata.class)) {
            bind(CommandGroupMetadata.class).toProvider(Providers.<CommandGroupMetadata>of(null));
        }
    }

    private <I> void bindBinding(Class<I> bindingType, Object instance) {
        ParameterizedType parameterizedType = Types.newParameterizedType(bindingType, type);
        if (instance != null) {
            bindBinding(Key.get(parameterizedType), instance);
            bind(bindingType).toInstance((I) instance);
        } else {
            bindNullBinding(Key.get(parameterizedType));
            bind(bindingType).toProvider(Providers.<I>of(null));
        }
    }

    private <I> void bindBinding(Key<I> key, Object value) {
        bind(key).toInstance((I) value);
    }

    private <I> void bindNullBinding(Key<I> key) {
        bind(key).toProvider(Providers.<I>of(null));
    }

}
