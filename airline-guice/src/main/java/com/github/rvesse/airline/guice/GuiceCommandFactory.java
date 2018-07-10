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
import com.github.rvesse.airline.CommandFactory;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.util.Types;

import java.lang.reflect.ParameterizedType;

/**
 * Command factory which uses google guice for the creation of commands and for dependency injection.
 *
 * @param <T> type of commands
 */
public class GuiceCommandFactory<T> implements CommandFactory<T> {

    private final Class<T> commandType;
    private final Injector injector;

    /**
     * Creates a new GuiceCommandFactory
     *
     * @param commandType type of commands
     * @param injector google guice parent injector
     */
    public GuiceCommandFactory(Class<T> commandType, Injector injector) {
        this.commandType = commandType;
        this.injector = injector;
    }

    @Override
    public T createInstance(CommandContext<T> context, Class<?> command) {
        Injector commandInjector = createCommandInjector(context);
        Key<T> key = createKey(command);

        T commandInstance = commandInjector.getInstance(key);
        context.processArguments(commandInstance);
        context.processOptions(commandInstance);
        return commandInstance;
    }

    private Injector createCommandInjector(CommandContext<T> context) {
        return injector.createChildInjector(new CommandModule<>(commandType, context));
    }

    private Key<T> createKey(Class<?> command) {
        ParameterizedType parameterizedType;
        Class<?> enclosingClass = command.getEnclosingClass();
        if (enclosingClass != null) {
            parameterizedType = Types.newParameterizedTypeWithOwner(enclosingClass, command, commandType);
        } else {
            parameterizedType = Types.newParameterizedType(command, commandType);
        }

        return (Key<T>) Key.get(parameterizedType);
    }
}
