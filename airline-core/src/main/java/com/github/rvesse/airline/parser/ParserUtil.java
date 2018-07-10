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
import com.github.rvesse.airline.CommandFactory;
import com.github.rvesse.airline.DefaultCommandFactory;
import com.github.rvesse.airline.model.ArgumentsMetadata;
import com.github.rvesse.airline.model.OptionMetadata;
import com.github.rvesse.airline.parser.errors.ParseException;
import com.github.rvesse.airline.parser.resources.ResourceLocator;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;

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
            List<Pair<OptionMetadata, Object>> parsedOptions, ArgumentsMetadata arguments,
            Iterable<Object> parsedArguments, Iterable<Accessor> metadataInjection, Map<Class<?>, Object> bindings) {
        return createInstance(type, options, parsedOptions, arguments, parsedArguments, metadataInjection, bindings,
                new DefaultCommandFactory<T>());
    }


    public static <T> T createInstance(Class<?> type, Iterable<OptionMetadata> options,
            List<Pair<OptionMetadata, Object>> parsedOptions, ArgumentsMetadata arguments,
            Iterable<Object> parsedArguments, Iterable<Accessor> metadataInjection, Map<Class<?>, Object> bindings,
            CommandFactory<T> commandFactory) {

        CommandContext<T> context = new DefaultCommandContext<>(options, parsedOptions, arguments, parsedArguments, metadataInjection, bindings);
        return commandFactory.createInstance(context, type);
    }

    public static ResourceLocator[] createResourceLocators(Class<? extends ResourceLocator>[] locatorClasses) {
        ResourceLocator[] locators = new ResourceLocator[locatorClasses.length];
        int i = 0;
        for (Class<? extends ResourceLocator> locatorClass :locatorClasses) {
            ResourceLocator locator = ParserUtil.createInstance(locatorClass);
            locators[i] = locator;
            i++;
        }
        return locators;
    }
}
