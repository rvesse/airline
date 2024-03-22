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

import java.util.List;

import org.apache.commons.collections4.IteratorUtils;

import com.github.rvesse.airline.model.ArgumentsMetadata;
import com.github.rvesse.airline.model.CommandMetadata;
import com.github.rvesse.airline.model.OptionMetadata;
import com.github.rvesse.airline.model.ParserMetadata;
import com.github.rvesse.airline.parser.AbstractCommandParser;
import com.github.rvesse.airline.parser.ParseResult;
import com.github.rvesse.airline.parser.ParseState;
import com.github.rvesse.airline.parser.errors.ParseException;
import com.github.rvesse.airline.restrictions.ArgumentsRestriction;
import com.github.rvesse.airline.restrictions.GlobalRestriction;
import com.github.rvesse.airline.restrictions.OptionRestriction;

public class SingleCommandParser<T> extends AbstractCommandParser<T> {

    public ParseResult<T> parseWithResult(ParserMetadata<T> parserConfig, CommandMetadata commandMetadata,
            Iterable<GlobalRestriction> restrictions, Iterable<String> args) {
        if (args == null)
            throw new NullPointerException("args is null");

        ParseState<T> state = tryParse(parserConfig, commandMetadata, args);
        validate(state, IteratorUtils.toList(restrictions.iterator()));

        return state.getParserConfiguration().getErrorHandler().finished(state);

    }

    public T parse(ParserMetadata<T> parserConfig, CommandMetadata commandMetadata,
            Iterable<GlobalRestriction> restrictions, Iterable<String> args) {
        ParseResult<T> result = parseWithResult(parserConfig, commandMetadata, restrictions, args);
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
    protected void validate(ParseState<T> state, List<GlobalRestriction> restrictions) {
        // Global restrictions
        for (GlobalRestriction restriction : restrictions) {
            if (restriction == null)
                continue;
            try {
                restriction.validate(state);
            } catch (ParseException e) {
                state.getParserConfiguration().getErrorHandler().handleError(e);
            }
        }
        this.applyFinalOptionAndArgumentValidation(state);
    }
}
