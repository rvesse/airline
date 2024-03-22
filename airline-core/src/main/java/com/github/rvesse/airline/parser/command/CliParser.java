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

import com.github.rvesse.airline.model.GlobalMetadata;
import com.github.rvesse.airline.parser.AbstractCommandParser;
import com.github.rvesse.airline.parser.ParseResult;
import com.github.rvesse.airline.parser.ParseState;
import com.github.rvesse.airline.parser.errors.ParseException;
import com.github.rvesse.airline.restrictions.GlobalRestriction;

public class CliParser<T> extends AbstractCommandParser<T> {

    public ParseResult<T> parseWithResult(GlobalMetadata<T> metadata, Iterable<String> args) {
        if (args == null)
            throw new NullPointerException("args cannot be null");

        // TODO Future Plugin Extension Point, allow the metadata to be extended prior to beginning parsing

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

        // TODO Future Plugin Extension Point, allow the state to be modified by plugins prior to final validation

        validate(state);
        return metadata.getParserConfiguration().getErrorHandler().finished(state);
    }

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
        applyFinalOptionAndArgumentValidation(state);
    }

}
