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

import com.github.rvesse.airline.Context;
import com.github.rvesse.airline.builder.ParserBuilder;
import com.github.rvesse.airline.model.ArgumentsMetadata;
import com.github.rvesse.airline.model.CommandGroupMetadata;
import com.github.rvesse.airline.model.CommandMetadata;
import com.github.rvesse.airline.model.GlobalMetadata;
import com.github.rvesse.airline.model.OptionMetadata;
import com.github.rvesse.airline.model.ParserMetadata;
import com.github.rvesse.airline.parser.errors.ParseException;
import com.github.rvesse.airline.restrictions.ArgumentsRestriction;
import com.github.rvesse.airline.restrictions.OptionRestriction;
import com.github.rvesse.airline.types.TypeConverter;
import com.github.rvesse.airline.utils.AirlineUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

public class ParseState<T> {
    private final List<Context> locationStack;
    private final GlobalMetadata<T> global;
    private final ParserMetadata<T> parserConfig;
    private final CommandGroupMetadata group;
    private final CommandMetadata command;
    private final List<Pair<OptionMetadata, Object>> parsedOptions;
    private final Map<OptionMetadata, Integer> optionsCount;
    private final List<Object> parsedArguments;
    private final OptionMetadata currentOption;
    private final List<String> unparsedInput;

    ParseState(GlobalMetadata<T> global, ParserMetadata<T> parserConfig, CommandGroupMetadata group,
            CommandMetadata command, List<Pair<OptionMetadata, Object>> parsedOptions,
            Map<OptionMetadata, Integer> optionsCount, List<Context> locationStack, List<Object> parsedArguments,
            OptionMetadata currentOption, List<String> unparsedInput) {
        this.global = global;
        if (global != null) {
            this.parserConfig = global.getParserConfiguration();
        } else if (parserConfig != null) {
            this.parserConfig = parserConfig;
        } else {
            this.parserConfig = ParserBuilder.<T> defaultConfiguration();
        }
        this.group = group;
        this.command = command;
        this.parsedOptions = parsedOptions;
        this.optionsCount = optionsCount;
        this.locationStack = locationStack;
        this.parsedArguments = parsedArguments;
        this.currentOption = currentOption;
        this.unparsedInput = unparsedInput;
    }

    public static <T> ParseState<T> newInstance() {
        return new ParseState<T>(null, null, null, null, new ArrayList<Pair<OptionMetadata, Object>>(),
                new HashMap<OptionMetadata, Integer>(), Collections.<Context> emptyList(),
                Collections.<Object> emptyList(), null, Collections.<String> emptyList());
    }

    public ParseState<T> pushContext(Context location) {
        List<Context> locations = AirlineUtils.listCopy(this.locationStack);
        locations.add(location);

        return new ParseState<T>(global, parserConfig, group, command, parsedOptions, optionsCount, locations,
                parsedArguments, currentOption, unparsedInput);
    }

    public ParseState<T> popContext() {
        List<Context> locationStack = AirlineUtils
                .unmodifiableListCopy(this.locationStack.subList(0, this.locationStack.size() - 1));
        return new ParseState<T>(global, parserConfig, group, command, parsedOptions, optionsCount, locationStack,
                parsedArguments, currentOption, unparsedInput);
    }

    public ParseState<T> withOptionValue(OptionMetadata option, String rawValue) {
        // Pre-validate
        for (OptionRestriction restriction : option.getRestrictions()) {
            try {
                restriction.preValidate(this, option, rawValue);
            } catch (ParseException e) {
                this.parserConfig.getErrorHandler().handleError(e);
            }
        }

        try {
            // Convert value
            Integer index = optionsCount.get(option);
            if (index == null)
                index = 0;
            TypeConverter converter = option.getTypeConverterProvider().getTypeConverter(option, this);
            Object value = converter.convert(option.getTitle(option.getArity() > 0 ? index % option.getArity() : 0), option.getJavaType(), rawValue);

            // Post-validate
            for (OptionRestriction restriction : option.getRestrictions()) {
                try {
                    restriction.postValidate(this, option, value);
                } catch (ParseException e) {
                    this.parserConfig.getErrorHandler().handleError(e);
                }
            }

            List<Pair<OptionMetadata, Object>> newOptions = AirlineUtils.listCopy(parsedOptions);
            newOptions.add(Pair.of(option, value));
            Map<OptionMetadata, Integer> newOptionsCount = new HashMap<>(optionsCount);
            newOptionsCount.put(option, ++index);

            return new ParseState<T>(global, parserConfig, group, command, newOptions, newOptionsCount, locationStack,
                    parsedArguments, currentOption, unparsedInput);
        } catch (ParseException e) {
            return withParsingError(rawValue, e);
        }
    }

    public ParseState<T> withGlobal(GlobalMetadata<T> global) {
        return new ParseState<T>(global, parserConfig, group, command, parsedOptions, optionsCount, locationStack,
                parsedArguments, currentOption, unparsedInput);
    }

    public ParseState<T> withConfiguration(ParserMetadata<T> parserConfig) {
        return new ParseState<T>(global, parserConfig, group, command, parsedOptions, optionsCount, locationStack,
                parsedArguments, currentOption, unparsedInput);
    }

    public ParseState<T> withGroup(CommandGroupMetadata group) {
        return new ParseState<T>(global, parserConfig, group, command, parsedOptions, optionsCount, locationStack,
                parsedArguments, currentOption, unparsedInput);
    }

    public ParseState<T> withCommand(CommandMetadata command) {
        return new ParseState<T>(global, parserConfig, group, command, parsedOptions, optionsCount, locationStack,
                parsedArguments, currentOption, unparsedInput);
    }

    public ParseState<T> withOption(OptionMetadata option) {
        return new ParseState<T>(global, parserConfig, group, command, parsedOptions, optionsCount, locationStack,
                parsedArguments, option, unparsedInput);
    }

    public ParseState<T> withArgument(ArgumentsMetadata arguments, String rawValue) {
        // Pre-validate
        for (ArgumentsRestriction restriction : arguments.getRestrictions()) {
            try {
                restriction.preValidate(this, arguments, rawValue);
            } catch (ParseException e) {
                this.parserConfig.getErrorHandler().handleError(e);
            }
        }

        // Convert value
        try {
            TypeConverter converter = arguments.getTypeConverterProvider().getTypeConverter(arguments, this);
            Object value = converter.convert(arguments.getTitle().get(0), arguments.getJavaType(), rawValue);

            // Post-validate
            for (ArgumentsRestriction restriction : arguments.getRestrictions()) {
                try {
                    restriction.postValidate(this, arguments, value);
                } catch (ParseException e) {
                    this.parserConfig.getErrorHandler().handleError(e);
                }
            }

            List<Object> newArguments = AirlineUtils.listCopy(parsedArguments);
            newArguments.add(value);

            return new ParseState<T>(global, parserConfig, group, command, parsedOptions, optionsCount, locationStack,
                    newArguments, currentOption, unparsedInput);
        } catch (ParseException e) {
            return withParsingError(rawValue, e);
        }
    }

    private ParseState<T> withParsingError(String rawValue, ParseException e) {
        this.parserConfig.getErrorHandler().handleError(e);

        List<String> newUnparsed = AirlineUtils.listCopy(unparsedInput);
        newUnparsed.add(rawValue);

        return new ParseState<T>(global, parserConfig, group, command, parsedOptions, optionsCount, locationStack,
                                 parsedArguments, currentOption, newUnparsed);
    }

    public ParseState<T> withUnparsedInput(String input) {
        List<String> newUnparsedInput = AirlineUtils.listCopy(unparsedInput);
        newUnparsedInput.add(input);

        return new ParseState<T>(global, parserConfig, group, command, parsedOptions, optionsCount, locationStack,
                parsedArguments, currentOption, newUnparsedInput);
    }

    @Override
    public String toString() {
        return "ParseState{" + "locationStack=" + locationStack + ", global=" + global + ", group=" + group
                + ", command=" + command + ", parsedOptions=" + parsedOptions + ", optionsCount=" + optionsCount
                + ", parsedArguments=" + parsedArguments + ", currentOption=" + currentOption + ", unparsedInput="
                + unparsedInput + '}';
    }

    public Context getLocation() {
        return locationStack.get(locationStack.size() - 1);
    }

    public GlobalMetadata<T> getGlobal() {
        return global;
    }

    public ParserMetadata<T> getParserConfiguration() {
        return this.global != null ? this.global.getParserConfiguration() : this.parserConfig;
    }

    public CommandGroupMetadata getGroup() {
        return group;
    }

    public CommandMetadata getCommand() {
        return command;
    }

    public OptionMetadata getCurrentOption() {
        return currentOption;
    }

    public List<Pair<OptionMetadata, Object>> getParsedOptions() {
        return parsedOptions;
    }

    public int getOptionValuesSeen(OptionMetadata option) {
        Integer count = optionsCount.get(option);
        return count == null ? 0 : count.intValue();
    }

    public List<Object> getParsedArguments() {
        return parsedArguments;
    }

    public List<String> getUnparsedInput() {
        return unparsedInput;
    }
}
