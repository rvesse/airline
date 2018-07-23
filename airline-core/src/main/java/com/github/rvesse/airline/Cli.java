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

import com.github.rvesse.airline.builder.CliBuilder;
import com.github.rvesse.airline.model.GlobalMetadata;
import com.github.rvesse.airline.model.MetadataLoader;
import com.github.rvesse.airline.model.ParserMetadata;

/**
 * Class for encapsulating and parsing CLIs
 * 
 * @author rvesse
 *
 * @param <C>
 *            Command type
 * @deprecated Renamed to {@link CommandLineInterface} to avoid ambiguity with
 *             {@link com.github.rvesse.airline.annotations.Cli} annotation
 */
@Deprecated
public class Cli<C> extends CommandLineInterface<C> {

    /**
     * Creates a builder for specifying a command line in fluent style
     * 
     * @param name
     *            Program name
     * @param <T>
     *            Command type to be built
     * @return CLI Builder
     * @deprecated Renamed to {@link CommandLineInterface} to avoid ambiguity
     *             with {@link com.github.rvesse.airline.annotations.Cli}
     *             annotation
     */
    @Deprecated
    public static <T> CliBuilder<T> builder(String name) {
        if (name == null)
            throw new NullPointerException("name cannot be null");
        return new CliBuilder<T>(name);
    }

    /**
     * Creates a new CLI from a class annotated with the
     * {@link com.github.rvesse.airline.annotations.Cli} annotation
     * 
     * @param cliClass
     *            CLI class
     * @deprecated Renamed to {@link CommandLineInterface} to avoid ambiguity
     *             with {@link com.github.rvesse.airline.annotations.Cli}
     *             annotation
     */
    @Deprecated
    public Cli(Class<?> cliClass) {
        this(MetadataLoader.<C> loadGlobal(cliClass));
    }

    /**
     * Creates a new CLI from a class annotated with the
     * {@link com.github.rvesse.airline.annotations.Cli} annotation
     * 
     * @param cliClass
     *            CLI class
     * @param parserConfig
     *            Parser configuration, this will override any configuration
     *            specified by the
     *            {@link com.github.rvesse.airline.annotations.Cli#parserConfiguration()}
     *            field
     * @deprecated Renamed to {@link CommandLineInterface} to avoid ambiguity
     *             with {@link com.github.rvesse.airline.annotations.Cli}
     *             annotation
     */
    @Deprecated
    public Cli(Class<?> cliClass, ParserMetadata<C> parserConfig) {
        this(MetadataLoader.<C> loadGlobal(cliClass, parserConfig));
    }

    /**
     * Creates a new CLI
     * 
     * @param metadata
     *            Metadata
     * @deprecated Renamed to {@link CommandLineInterface} to avoid ambiguity
     *             with {@link com.github.rvesse.airline.annotations.Cli}
     *             annotation
     */
    @Deprecated
    public Cli(GlobalMetadata<C> metadata) {
        super(metadata);
    }
}
