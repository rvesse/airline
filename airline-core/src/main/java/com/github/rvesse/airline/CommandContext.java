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
package com.github.rvesse.airline;

import java.util.Map;

/**
 * The CommandContext provides the context for command creation and handles the injection of arguments, options and
 * dependencies.
 *
 * @param <T> type of command
 */
public interface CommandContext<T> {

    /**
     * Returns bindings for injection.
     *
     * @return injection bindings
     */
    Map<Class<?>, Object> getBindings();

    /**
     * Process arguments of command instance.
     *
     * @param commandInstance command instance
     */
    void processArguments(T commandInstance);

    /**
     * Process options of command instance.
     *
     * @param commandInstance command instance
     */
    void processOptions(T commandInstance);

    /**
     * Process injections of command instance.
     *
     * @param commandInstance command instance
     */
    void processInjections(T commandInstance);
}
