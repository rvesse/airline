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

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import com.github.rvesse.airline.model.CommandMetadata;
import com.github.rvesse.airline.model.MetadataLoader;
import com.github.rvesse.airline.model.ParserMetadata;
import com.github.rvesse.airline.parser.ParseResult;
import com.github.rvesse.airline.parser.command.SingleCommandParser;
import com.github.rvesse.airline.restrictions.GlobalRestriction;
import com.github.rvesse.airline.restrictions.factories.RestrictionRegistry;

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
     * @param <C>
     *            Command type we wish to parse to
     * @return Single command parser
     */
    public static <C> SingleCommand<C> singleCommand(Class<C> command) {
        return new SingleCommand<>(command, null, null);
    }

    /**
     * Creates a new single command
     * 
     * @param command
     *            Command class
     * @param parserConfig
     *            Parser configuration to use, if {@code null} the default
     *            configuration is used
     * @param <C>
     *            Command type we wish to parse to
     * @return Single command parser
     */
    public static <C> SingleCommand<C> singleCommand(Class<C> command, ParserMetadata<C> parserConfig) {
        return new SingleCommand<>(command, null, parserConfig);
    }

    private final ParserMetadata<C> parserConfig;
    private final CommandMetadata commandMetadata;
    private final List<GlobalRestriction> restrictions;

    private SingleCommand(Class<C> command, Iterable<GlobalRestriction> restrictions, ParserMetadata<C> parserConfig) {
        if (command == null)
            throw new NullPointerException("command is null");
        this.parserConfig = parserConfig != null ? parserConfig : MetadataLoader.loadParser(command);
        
        // Dynamically obtain restrictions if annotated onto the class
        this.restrictions = createRestrictions(command, restrictions);

        commandMetadata = MetadataLoader.loadCommand(command, this.parserConfig);
    }
    
    private List<GlobalRestriction> createRestrictions(Class<C> commandClass, Iterable<GlobalRestriction> restrictions) {
        List<GlobalRestriction> foundRestrictions = new ArrayList<>();
        
        // If any were explicitly provided use those
        if (restrictions != null && restrictions.iterator().hasNext()) {
            foundRestrictions.addAll(StreamSupport
                            .stream(restrictions.spliterator(), false)
                            .collect(Collectors.toList()));
        }
        
        // Look for annotation declared restrictions
        for (Class<? extends Annotation> annotationClass : RestrictionRegistry
                .getGlobalRestrictionAnnotationClasses()) {
            Annotation annotation = commandClass.getAnnotation(annotationClass);
            if (annotation == null)
                continue;
            GlobalRestriction restriction = RestrictionRegistry.getGlobalRestriction(annotationClass, annotation);
            if (restriction != null)
                foundRestrictions.add(restriction);
        }
        
        // Add defaults if none found
        if (foundRestrictions.isEmpty()) {
            foundRestrictions.addAll(Arrays.asList(GlobalRestriction.DEFAULTS));
        }
        
        return foundRestrictions;
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
        return parse(Arrays.asList(args));
    }

    /**
     * Parses the arguments to produce a command instance
     * 
     * @param args
     *            Arguments
     * @return Command instance
     */
    public C parse(Iterable<String> args) {
        SingleCommandParser<C> parser = new SingleCommandParser<>();
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
        return parseWithResult(Arrays.asList(args));
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
        SingleCommandParser<C> parser = new SingleCommandParser<>();
        return parser.parseWithResult(parserConfig, commandMetadata, restrictions, args);
    }
}
