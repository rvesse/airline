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

import java.util.List;

import com.github.rvesse.airline.utils.IteratorUtils;
import com.github.rvesse.airline.model.CommandMetadata;
import com.github.rvesse.airline.model.MetadataLoader;
import com.github.rvesse.airline.model.ParserMetadata;
import com.github.rvesse.airline.parser.ParseResult;
import com.github.rvesse.airline.parser.command.SingleCommandParser;
import com.github.rvesse.airline.restrictions.GlobalRestriction;
import com.github.rvesse.airline.utils.AirlineUtils;

/**
 * Class for encapsulating and parsing single commands
 *
 * @param <C>
 *            Command type
 */
public class SingleCommand<C> {
    /**
     * Creates a new single command
     * 
     * @param command
     *            Command class
     * @return Single command parser
     */
    public static <C> SingleCommand<C> singleCommand(Class<C> command) {
        return new SingleCommand<C>(command, null, null);
    }

    /**
     * Creates a new single command
     * 
     * @param command
     *            Command class
     * @param parserConfig
     *            Parser configuration to use, if {@code null} the default
     *            configuration is used
     * @return Single command parser
     */
    public static <C> SingleCommand<C> singleCommand(Class<C> command, ParserMetadata<C> parserConfig) {
        return new SingleCommand<C>(command, null, parserConfig);
    }

    private final ParserMetadata<C> parserConfig;
    private final CommandMetadata commandMetadata;
    private final List<GlobalRestriction> restrictions;

    private SingleCommand(Class<C> command, Iterable<GlobalRestriction> restrictions, ParserMetadata<C> parserConfig) {
        if (command == null)
            throw new NullPointerException("command is null");
        this.parserConfig = parserConfig != null ? parserConfig : MetadataLoader.<C> loadParser(command);
        this.restrictions = restrictions != null ? IteratorUtils.toList(restrictions.iterator())
                : AirlineUtils.arrayToList(GlobalRestriction.DEFAULTS);
        if (this.restrictions.size() == 0)
            this.restrictions.addAll(AirlineUtils.arrayToList(GlobalRestriction.DEFAULTS));

        commandMetadata = MetadataLoader.loadCommand(command);
    }

    /**
     * Gets the command metadata
     * 
     * @return Command metadata
     */
    public CommandMetadata getCommandMetadata() {
        return commandMetadata;
    }

    /**
     * Gets the parser configuration
     * 
     * @return Parser configuration
     */
    public ParserMetadata<C> getParserConfiguration() {
        return parserConfig;
    }

    /**
     * Parses the arguments to produce a command instance
     * 
     * @param args
     *            Arguments
     * @return Command instance
     */
    public C parse(String... args) {
        return parse(AirlineUtils.arrayToList(args));
    }

    /**
     * Parses the arguments to produce a command instance
     * 
     * @param args
     *            Arguments
     * @return Command instance
     */
    public C parse(Iterable<String> args) {
        SingleCommandParser<C> parser = new SingleCommandParser<C>();
        return parser.parse(parserConfig, commandMetadata, restrictions, args);
    }

    /**
     * Parses the arguments to produce a result. The result can be inspected to
     * see errors (assuming a suitable error handler was used e.g.
     * {@code CollectAll}) and to get a command instance
     * 
     * @param args
     *            Arguments
     * @return Parse result
     */
    public ParseResult<C> parseWithResult(String... args) {
        return parseWithResult(AirlineUtils.arrayToList(args));
    }

    /**
     * Parses the arguments to produce a result. The result can be inspected to
     * see errors (assuming a suitable error handler was used e.g.
     * {@code CollectAll}) and to get a command instance
     * 
     * @param args
     *            Arguments
     * @return Parse result
     */
    public ParseResult<C> parseWithResult(Iterable<String> args) {
        SingleCommandParser<C> parser = new SingleCommandParser<C>();
        return parser.parseWithResult(parserConfig, commandMetadata, restrictions, args);
    }
}
