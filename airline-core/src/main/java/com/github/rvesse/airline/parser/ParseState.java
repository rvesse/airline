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
import com.github.rvesse.airline.model.PositionalArgumentMetadata;
import com.github.rvesse.airline.model.ArgumentsMetadata;
import com.github.rvesse.airline.model.CommandGroupMetadata;
import com.github.rvesse.airline.model.CommandMetadata;
import com.github.rvesse.airline.model.GlobalMetadata;
import com.github.rvesse.airline.model.OptionMetadata;
import com.github.rvesse.airline.model.ParserMetadata;
import com.github.rvesse.airline.parser.errors.ParseException;
import com.github.rvesse.airline.restrictions.AbstractCommonRestriction;
import com.github.rvesse.airline.restrictions.ArgumentsRestriction;
import com.github.rvesse.airline.restrictions.OptionRestriction;
import com.github.rvesse.airline.types.TypeConverter;
import com.github.rvesse.airline.utils.AirlineUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

/**
 * Used to track the state of the parser
 *
 * @param <T>
 *            Command type
 */
public class ParseState<T> {
    private final List<Context> locationStack;
    private final GlobalMetadata<T> global;
    private final ParserMetadata<T> parserConfig;
    private final CommandGroupMetadata group;
    private final CommandMetadata command;
    private final List<Pair<OptionMetadata, Object>> parsedOptions;
    private final List<Pair<PositionalArgumentMetadata, Object>> parsedPositionalArgs;
    private final List<Object> parsedArguments;
    private final OptionMetadata currentOption;
    private final List<String> unparsedInput;

    ParseState(GlobalMetadata<T> global, ParserMetadata<T> parserConfig, CommandGroupMetadata group,
            CommandMetadata command, List<Pair<OptionMetadata, Object>> parsedOptions, List<Context> locationStack,
            List<Pair<PositionalArgumentMetadata, Object>> parsedPositionalArgs, List<Object> parsedArguments,
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
        this.locationStack = locationStack;
        this.parsedPositionalArgs = parsedPositionalArgs;
        this.parsedArguments = parsedArguments;
        this.currentOption = currentOption;
        this.unparsedInput = unparsedInput;
    }

    public static <T> ParseState<T> newInstance() {
        return new ParseState<T>(null, null, null, null, new ArrayList<Pair<OptionMetadata, Object>>(),
                Collections.<Context> emptyList(), new ArrayList<Pair<PositionalArgumentMetadata, Object>>(),
                Collections.<Object> emptyList(), null, Collections.<String> emptyList());
    }

    public ParseState<T> pushContext(Context location) {
        List<Context> locations = AirlineUtils.listCopy(this.locationStack);
        locations.add(location);

        return new ParseState<T>(global, parserConfig, group, command, parsedOptions, locations, parsedPositionalArgs,
                parsedArguments, currentOption, unparsedInput);
    }

    public ParseState<T> popContext() {
        List<Context> locationStack = AirlineUtils
                .unmodifiableListCopy(this.locationStack.subList(0, this.locationStack.size() - 1));
        return new ParseState<T>(global, parserConfig, group, command, parsedOptions, locationStack,
                parsedPositionalArgs, parsedArguments, currentOption, unparsedInput);
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
            TypeConverter converter = option.getTypeConverterProvider().getTypeConverter(option, this);
            Object value = converter.convert(option.getTitle(), option.getJavaType(), rawValue);

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

            return new ParseState<T>(global, parserConfig, group, command, newOptions, locationStack,
                    parsedPositionalArgs, parsedArguments, currentOption, unparsedInput);
        } catch (ParseException e) {
            this.parserConfig.getErrorHandler().handleError(e);

            List<String> newUnparsed = AirlineUtils.listCopy(unparsedInput);
            newUnparsed.add(rawValue);

            return new ParseState<T>(global, parserConfig, group, command, parsedOptions, locationStack,
                    parsedPositionalArgs, parsedArguments, currentOption, newUnparsed);
        }
    }

    public ParseState<T> withGlobal(GlobalMetadata<T> global) {
        return new ParseState<T>(global, parserConfig, group, command, parsedOptions, locationStack,
                parsedPositionalArgs, parsedArguments, currentOption, unparsedInput);
    }

    public ParseState<T> withConfiguration(ParserMetadata<T> parserConfig) {
        return new ParseState<T>(global, parserConfig, group, command, parsedOptions, locationStack,
                parsedPositionalArgs, parsedArguments, currentOption, unparsedInput);
    }

    public ParseState<T> withGroup(CommandGroupMetadata group) {
        return new ParseState<T>(global, parserConfig, group, command, parsedOptions, locationStack,
                parsedPositionalArgs, parsedArguments, currentOption, unparsedInput);
    }

    public ParseState<T> withCommand(CommandMetadata command) {
        return new ParseState<T>(global, parserConfig, group, command, parsedOptions, locationStack,
                parsedPositionalArgs, parsedArguments, currentOption, unparsedInput);
    }

    public ParseState<T> withOption(OptionMetadata option) {
        return new ParseState<T>(global, parserConfig, group, command, parsedOptions, locationStack,
                parsedPositionalArgs, parsedArguments, option, unparsedInput);
    }

    public ParseState<T> withArgument(List<PositionalArgumentMetadata> positionalArgs, ArgumentsMetadata arguments,
            String rawValue) {
        // Are we still parsing positional arguments or are we on non-positional
        // arguments?
        boolean positional = positionalArgs.size() > 0 && parsedPositionalArgs.size() < positionalArgs.size();
        int posIndex = positional ? parsedPositionalArgs.size() : -1;
        if (!positional && arguments == null) {
            return withUnparsedInput(rawValue);
        }
        PositionalArgumentMetadata posArg = positional ? positionalArgs.get(posIndex) : null;
        List<ArgumentsRestriction> restrictions = positional ? posArg.getRestrictions() : arguments.getRestrictions();

        // Pre-validate
        for (ArgumentsRestriction restriction : restrictions) {
            try {
                if (positional) {
                    restriction.preValidate(this, posArg, rawValue);
                } else {
                    restriction.preValidate(this, arguments, rawValue);
                }
            } catch (ParseException e) {
                this.parserConfig.getErrorHandler().handleError(e);
            }
        }

        // Convert value
        try {
            TypeConverter converter = positional ? posArg.getTypeConverterProvider().getTypeConverter(posArg, this)
                    : arguments.getTypeConverterProvider().getTypeConverter(arguments, this);
            Class<?> javaType = positional ? posArg.getJavaType() : arguments.getJavaType();
            String title = positional ? posArg.getTitle() : AbstractCommonRestriction.getArgumentTitle(this, arguments);
            Object value = converter.convert(title, javaType, rawValue);

            // Post-validate
            for (ArgumentsRestriction restriction : restrictions) {
                try {
                    if (positional) {
                        restriction.postValidate(this, posArg, value);
                    } else {
                        restriction.postValidate(this, arguments, value);
                    }
                } catch (ParseException e) {
                    this.parserConfig.getErrorHandler().handleError(e);
                }
            }

            if (positional) {
                List<Pair<PositionalArgumentMetadata, Object>> newPosArgs = AirlineUtils.listCopy(parsedPositionalArgs);
                newPosArgs.add(Pair.of(posArg, value));

                return new ParseState<T>(global, parserConfig, group, command, parsedOptions, locationStack, newPosArgs,
                        parsedArguments, currentOption, unparsedInput);
            } else {

                List<Object> newArguments = AirlineUtils.listCopy(parsedArguments);
                newArguments.add(value);

                return new ParseState<T>(global, parserConfig, group, command, parsedOptions, locationStack,
                        parsedPositionalArgs, newArguments, currentOption, unparsedInput);
            }
        } catch (ParseException e) {
            this.parserConfig.getErrorHandler().handleError(e);

            List<String> newUnparsed = AirlineUtils.listCopy(unparsedInput);
            newUnparsed.add(rawValue);

            return new ParseState<T>(global, parserConfig, group, command, parsedOptions, locationStack,
                    parsedPositionalArgs, parsedArguments, currentOption, newUnparsed);
        }
    }

    public ParseState<T> withUnparsedInput(String input) {
        List<String> newUnparsedInput = AirlineUtils.listCopy(unparsedInput);
        newUnparsedInput.add(input);

        return new ParseState<T>(global, parserConfig, group, command, parsedOptions, locationStack,
                parsedPositionalArgs, parsedArguments, currentOption, newUnparsedInput);
    }

    @Override
    public String toString() {
        return "ParseState{" + "locationStack=" + locationStack + ", global=" + global + ", group=" + group
                + ", command=" + command + ", parsedOptions=" + parsedOptions + ", parsedPositionalArguments="
                + parsedPositionalArgs + ", parsedArguments=" + parsedArguments + ", currentOption=" + currentOption
                + ", unparsedInput=" + unparsedInput + '}';
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

    public List<Pair<PositionalArgumentMetadata, Object>> getParsedPositionalArguments() {
        return parsedPositionalArgs;
    }

    public List<Object> getParsedArguments() {
        return parsedArguments;
    }

    public List<String> getUnparsedInput() {
        return unparsedInput;
    }
}
