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
package com.github.rvesse.airline.tests;

import com.github.rvesse.airline.Cli;
import com.github.rvesse.airline.SingleCommand;
import com.github.rvesse.airline.builder.CliBuilder;
import com.github.rvesse.airline.model.ParserMetadata;

public class TestingUtil
{
    public static <T> SingleCommand<T> singleCommandParser(Class<T> commandClass)
    {
        return SingleCommand.singleCommand(commandClass);
    }
    
    public static <T> SingleCommand<T> singleCommandParser(Class<T> commandClass, ParserMetadata<T> parser) {
        return SingleCommand.singleCommand(commandClass, parser);
    }
    
    public static <T> Cli<T> singleCli(Class<T> commandClass)
    {
        //@formatter:off
        CliBuilder<T> builder = Cli.<T>builder("parser")
                                   .withCommand(commandClass);
        //@formatter:off
        
        return builder.build();
    }
    
    public static <T> Cli<T> singleAbbreviatedCommandParser(Class<T> commandClass)
    {
        //@formatter:off
        CliBuilder<T> builder = Cli.<T>builder("parser")
                                   .withCommand(commandClass);
        builder.withParser()
               .withCommandAbbreviation();
        //@formatter:on
        
        return builder.build();
    }
    
    public static <T> Cli<T> singleAbbreviatedOptionParser(Class<T> commandClass)
    {
        //@formatter:off
        CliBuilder<T> builder = Cli.<T>builder("parser")
                                   .withCommand(commandClass);
        builder.withParser()
               .withOptionAbbreviation();
        //@formatter:on
        
        return builder.build();
    }
}
