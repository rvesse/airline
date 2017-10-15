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
package com.github.rvesse.airline.utils.predicates.parser;

import com.github.rvesse.airline.utils.Predicate;
import com.github.rvesse.airline.model.CommandMetadata;

public class CommandTypeFinder implements Predicate<CommandMetadata> {

    private final Class<?> cls;

    public CommandTypeFinder(Class<?> cls) {
        this.cls = cls;
    }

    @Override
    public boolean evaluate(CommandMetadata command) {
        if (command == null)
            return false;
        if (this.cls == null) {
            return command.getClass() == null;
        } else {
            return this.cls.equals(command.getClass());
        }
    }
}
