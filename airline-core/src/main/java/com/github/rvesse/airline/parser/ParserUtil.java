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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.github.rvesse.airline.utils.ListUtils;

import com.github.rvesse.airline.Accessor;
import com.github.rvesse.airline.CommandFactory;
import com.github.rvesse.airline.DefaultCommandFactory;
import com.github.rvesse.airline.model.ArgumentsMetadata;
import com.github.rvesse.airline.model.OptionMetadata;
import com.github.rvesse.airline.parser.errors.ParseException;
import com.github.rvesse.airline.utils.AirlineUtils;

public class ParserUtil {

    public static <T> T createInstance(Class<T> type) {
        if (type != null) {
            try {
                return type.getConstructor().newInstance();
            } catch (Exception e) {
                throw new ParseException(e, "Unable to create instance %s", type.getName());
            }
        }
        return null;
    }

    public static <T> T createInstance(Class<?> type, Iterable<OptionMetadata> options,
            List<Map.Entry<OptionMetadata, Object>> parsedOptions, ArgumentsMetadata arguments,
            Iterable<Object> parsedArguments, Iterable<Accessor> metadataInjection, Map<Class<?>, Object> bindings) {
        return createInstance(type, options, parsedOptions, arguments, parsedArguments, metadataInjection, bindings,
                new DefaultCommandFactory<T>());
    }

    public static <T> T injectOptions(T commandInstance, Iterable<OptionMetadata> options,
            List<Map.Entry<OptionMetadata, Object>> parsedOptions, ArgumentsMetadata arguments,
            Iterable<Object> parsedArguments, Iterable<Accessor> metadataInjection, Map<Class<?>, Object> bindings) {
        // inject options
        for (OptionMetadata option : options) {
            List<Object> values = new ArrayList<>();
            for (Map.Entry<OptionMetadata, Object> parsedOption : parsedOptions) {
                if (option.equals(parsedOption.getKey()))
                    values.add(parsedOption.getValue());
            }
            if (values != null && !values.isEmpty()) {
                for (Accessor accessor : option.getAccessors()) {
                    accessor.addValues(commandInstance, values);
                }
            }
        }

        // inject args
        if (arguments != null && parsedArguments != null) {
            for (Accessor accessor : arguments.getAccessors()) {
                accessor.addValues(commandInstance, parsedArguments);
            }
        }

        for (Accessor accessor : metadataInjection) {
            Object injectee = bindings.get(accessor.getJavaType());

            if (injectee != null) {
                accessor.addValues(commandInstance, ListUtils.unmodifiableList(AirlineUtils.singletonList(injectee)));
            }
        }

        return commandInstance;
    }

    public static <T> T createInstance(Class<?> type, Iterable<OptionMetadata> options,
            List<Map.Entry<OptionMetadata, Object>> parsedOptions, ArgumentsMetadata arguments,
            Iterable<Object> parsedArguments, Iterable<Accessor> metadataInjection, Map<Class<?>, Object> bindings,
            CommandFactory<T> commandFactory) {
        // create the command instance
        T commandInstance = (T) commandFactory.createInstance(type);

        return injectOptions(commandInstance, options, parsedOptions, arguments, parsedArguments, metadataInjection,
                bindings);
    }
}
