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

import com.github.rvesse.airline.utils.ListUtils;

import com.github.rvesse.airline.builder.CliBuilder;
import com.github.rvesse.airline.model.GlobalMetadata;
import com.github.rvesse.airline.model.MetadataLoader;
import com.github.rvesse.airline.parser.ParseResult;
import com.github.rvesse.airline.parser.command.CliParser;
import com.github.rvesse.airline.utils.AirlineUtils;

/**
 * Class for encapsulating and parsing CLIs
 * 
 * @author rvesse
 *
 * @param <C>
 *            Command type
 */
public class Cli<C> {
    /**
     * Creates a builder for specifying a command line in fluent style
     * 
     * @param name
     *            Program name
     * @return CLI Builder
     */
    public static <T> CliBuilder<T> builder(String name) {
        if (name == null)
            throw new NullPointerException("name cannot be null");
        return new CliBuilder<T>(name);
    }

    private final GlobalMetadata<C> metadata;

    /**
     * Creates a new CLI from a class annotated with the
     * {@link com.github.rvesse.airline.annotations.Cli} annotation
     * 
     * @param cliClass
     *            CLI class
     */
    public Cli(Class<?> cliClass) {
        this(MetadataLoader.<C> loadGlobal(cliClass));
    }

    /**
     * Creates a new CLI
     * 
     * @param metadata
     *            Metadata
     */
    public Cli(GlobalMetadata<C> metadata) {
        if (metadata == null)
            throw new NullPointerException("metadata cannot be null");
        this.metadata = metadata;
    }

    /**
     * Gets the global meta-data
     * 
     * @return Meta-data
     */
    public GlobalMetadata<C> getMetadata() {
        return metadata;
    }

    /**
     * Parses the arguments to produce a command instance, this may be
     * {@code null} if the arguments don't identify a command and there was no
     * appropriate default command configured
     * 
     * @param args
     *            Arguments
     * @return Command instance
     */
    public C parse(String... args) {
        return parse(ListUtils.unmodifiableList(AirlineUtils.arrayToList(args)));
    }

    /**
     * Parses the arguments to produce a command instance, this may be
     * {@code null} if the arguments don't identify a command and there was no
     * appropriate default command configured
     * 
     * @param args
     *            Arguments
     * @return Command instance
     */
    private C parse(Iterable<String> args) {
        CliParser<C> parser = new CliParser<C>();
        return parser.parse(metadata, args);
    }

    /**
     * Parses the arguments to produce a result. The result can be inspected to
     * see errors (assuming a suitable error handler was used e.g.
     * {@code CollectAll}) and to get a command instance. This may be
     * {@code null} if the arguments don't identify a command and there was no
     * appropriate default command configured
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
     * {@code CollectAll}) and to get a command instance. This may be
     * {@code null} if the arguments don't identify a command and there was no
     * appropriate default command configured
     * 
     * @param args
     *            Arguments
     * @return Parse result
     */
    public ParseResult<C> parseWithResult(Iterable<String> args) {
        CliParser<C> parser = new CliParser<C>();
        return parser.parseWithResult(metadata, args);
    }
}
