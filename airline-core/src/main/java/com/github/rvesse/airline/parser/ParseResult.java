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

import static com.github.rvesse.airline.parser.ParserUtil.createInstance;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.github.rvesse.airline.model.CommandGroupMetadata;
import com.github.rvesse.airline.model.CommandMetadata;
import com.github.rvesse.airline.model.GlobalMetadata;
import com.github.rvesse.airline.model.ParserMetadata;
import com.github.rvesse.airline.parser.errors.ParseException;
import com.github.rvesse.airline.utils.AirlineUtils;

/**
 * Represents parsing results
 *
 * @param <T>
 *            Command type
 */
public class ParseResult<T> {
    private final ParseState<T> state;
    private final Collection<ParseException> errors;

    public ParseResult(ParseState<T> state, Collection<ParseException> errors) {
        if (state == null)
            throw new NullPointerException("state cannot be null");
        this.state = state;
        this.errors = errors != null ? Collections.<ParseException> unmodifiableCollection(errors)
                : Collections.<ParseException> emptyList();
    }

    /**
     * Indicates whether parsing was successful
     * 
     * @return True if successful, false if any errors occurred
     */
    public boolean wasSuccessful() {
        return this.errors.size() == 0;
    }

    /**
     * Gets the final parser state
     * 
     * @return Parser state
     */
    public ParseState<T> getState() {
        return this.state;
    }

    /**
     * Gets the collection of errors that occurred, may be empty if parsing was
     * successful
     * 
     * @return Errors
     */
    public Collection<ParseException> getErrors() {
        return this.errors;
    }

    /**
     * Gets the command if one was successfully parsed
     * <p>
     * Note that a command may have been parsed even when errors have been
     * detected and handled by the error handler. Therefore you should check
     * {@link #wasSuccessful()} and act accordingly before assuming that it is
     * safe to run the parsed command.
     * </p>
     * 
     * @return Command, or {@code null} if no command was parsed
     */
    public T getCommand() {
        CommandMetadata command = this.state.getCommand();
        if (command == null)
            return null;

        // Prepare bindings
        Map<Class<?>, Object> bindings = new HashMap<Class<?>, Object>();
        bindings.put(GlobalMetadata.class, state.getGlobal());

        if (state.getGroup() != null) {
            bindings.put(CommandGroupMetadata.class, state.getGroup());
        }

        if (state.getCommand() != null) {
            bindings.put(CommandMetadata.class, state.getCommand());
        }

        bindings.put(ParserMetadata.class, state.getParserConfiguration());
        bindings = AirlineUtils.unmodifiableMapCopy(bindings);

        if (state.getGlobal() != null) {
            // Create instance
            return createInstance(command.getType(), command.getAllOptions(), state.getParsedOptions(),
                    command.getPositionalArguments(), state.getParsedPositionalArguments(), command.getArguments(),
                    state.getParsedArguments(), command.getMetadataInjections(), bindings,
                    state.getParserConfiguration().getCommandFactory());
        } else {
            return createInstance(command.getType(), command.getAllOptions(), state.getParsedOptions(),
                    command.getPositionalArguments(), state.getParsedPositionalArguments(), command.getArguments(),
                    state.getParsedArguments(), command.getMetadataInjections(), bindings,
                    state.getParserConfiguration().getCommandFactory());
        }

    }
}
