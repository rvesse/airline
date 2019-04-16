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
package com.github.rvesse.airline.parser.command;

import com.github.rvesse.airline.model.CommandMetadata;
import com.github.rvesse.airline.model.GlobalMetadata;
import com.github.rvesse.airline.parser.AbstractCommandParser;
import com.github.rvesse.airline.parser.ParseResult;
import com.github.rvesse.airline.parser.ParseState;
import com.github.rvesse.airline.parser.errors.ParseException;
import com.github.rvesse.airline.restrictions.GlobalRestriction;

/**
 * A parser that parses full CLIs
 *
 * @param <T>
 *            Command type
 */
public class CliParser<T> extends AbstractCommandParser<T> {

    /**
     * Parses the input arguments returning the parse result
     * 
     * @param metadata
     *            CLI meta-data
     * @param args
     *            CLI arguments to parse
     * @return Parse result
     */
    public ParseResult<T> parseWithResult(GlobalMetadata<T> metadata, Iterable<String> args) {
        if (args == null)
            throw new NullPointerException("args cannot be null");

        ParseState<T> state = tryParse(metadata, args);

        // If we did not find a command choose the appropriate default command
        // (if any)
        if (state.getCommand() == null) {
            if (state.getGroup() != null) {
                state = state.withCommand(state.getGroup().getDefaultCommand());
            } else {
                state = state.withCommand(metadata.getDefaultCommand());
            }
        }

        validate(state);
        return metadata.getParserConfiguration().getErrorHandler().finished(state);
    }

    /**
     * Parses the input arguments returning the users specified command
     * 
     * @param metadata
     *            CLI meta-data
     * @param args
     *            CLI arguments to parse
     * @return Command which may be {@code null} if a command could not be
     *         parsed
     */
    public T parse(GlobalMetadata<T> metadata, Iterable<String> args) {
        ParseResult<T> result = parseWithResult(metadata, args);
        return result.getCommand();
    }

    /**
     * Validates the parser state
     * <p>
     * This includes things like verifying we ended in an appropriate state,
     * that all required options and arguments were present etc
     * </p>
     * 
     * @param state
     *            Parser state
     */
    protected void validate(ParseState<T> state) {
        // Global restrictions
        for (GlobalRestriction restriction : state.getGlobal().getRestrictions()) {
            if (restriction == null)
                continue;
            try {
                restriction.validate(state);
            } catch (ParseException e) {
                state.getParserConfiguration().getErrorHandler().handleError(e);
            }
        }
        CommandMetadata command = state.getCommand();
        validateCommand(state, command);
    }
}
