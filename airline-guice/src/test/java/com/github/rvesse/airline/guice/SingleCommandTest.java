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
package com.github.rvesse.airline.guice;

import com.github.rvesse.airline.CommandFactory;
import com.github.rvesse.airline.HelpOption;
import com.github.rvesse.airline.SingleCommand;
import com.github.rvesse.airline.annotations.Arguments;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.builder.ParserBuilder;
import com.github.rvesse.airline.model.ParserMetadata;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.testng.annotations.Test;

import javax.inject.Inject;

import static org.testng.Assert.assertEquals;

public class SingleCommandTest {

    @Test
    public void testSingleCommand() {
        Output output = new Output();

        Injector injector = Guice.createInjector(new SingleCommandModule(output));
        CommandFactory<HelloCommand> commandFactory = new GuiceCommandFactory<>(HelloCommand.class, injector);

        ParserMetadata<HelloCommand> parserMetadata = new ParserBuilder<HelloCommand>()
                .withCommandFactory(commandFactory)
                .build();

        SingleCommand<HelloCommand> command = SingleCommand.singleCommand(HelloCommand.class, parserMetadata);
        command.parse("world").run();

        assertEquals(output.getLines().get(0), "hello world");
    }

    public static class SingleCommandModule extends AbstractModule {

        private final Output output;

        SingleCommandModule(Output output) {
            this.output = output;
        }

        @Override
        protected void configure() {
            bind(Output.class).toInstance(output);
        }
    }

    @Command(name = "hello")
    public static class HelloCommand implements Runnable {

        @Arguments
        private String name;

        @Inject
        private HelpOption<HelloCommand> helpOption;

        @Inject
        private Output output;

        @Override
        public void run() {
            output.println("hello " + name);
        }
    }

}
