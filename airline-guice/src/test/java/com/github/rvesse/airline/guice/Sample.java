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

import com.github.rvesse.airline.Cli;
import com.github.rvesse.airline.CommandFactory;
import com.github.rvesse.airline.HelpOption;
import com.github.rvesse.airline.annotations.Arguments;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.Option;
import com.github.rvesse.airline.annotations.restrictions.Required;
import com.github.rvesse.airline.builder.CliBuilder;
import com.github.rvesse.airline.help.Help;
import com.github.rvesse.airline.model.GlobalMetadata;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

public class Sample {

    public static void main(Output output, String... args) {
        Injector injector = Guice.createInjector(new SampleModule(output));
        CommandFactory<Runnable> commandFactory = new GuiceCommandFactory<>(Runnable.class, injector);

        CliBuilder<Runnable> builder = Cli.<Runnable>builder("sample")
                .withDescription("sample application")
                .withDefaultCommand(Help.class)
                .withCommand(ApplicationName.class)
                .withCommands(Help.class, ListRepositories.class, AddRepository.class, WhoamiCommand.class);

        builder.withParser()
                .withCommandFactory(commandFactory);

        Cli<Runnable> sampleParser = builder.build();

        sampleParser.parse(args).run();
    }

    public static class SampleModule extends AbstractModule {

        private final Output output;

        SampleModule(Output output) {
            this.output = output;
        }

        @Override
        protected void configure() {
            List<String> repositories = new ArrayList<>();
            repositories.add("airline");
            repositories.add("scm-manager");
            repositories.add("jenkins");

            bind(new TypeLiteral<List<String>>(){}).annotatedWith(Names.named("repositories")).toInstance(repositories);
            bind(Output.class).toInstance(output);
        }
    }

    public static abstract class SampleCommand implements Runnable {

        @Inject
        private HelpOption<Runnable> helpOption;

        @Override
        public void run() {
            if (helpOption.showHelpIfRequested()) {
                helpOption.showHelp();
            } else {
                execute();
            }
        }

        protected abstract void execute();
    }

    @Command(name = "app")
    public static class ApplicationName<T> extends SampleCommand {

        private GlobalMetadata<T> metadata;
        private final Output output;

        @Inject
        public ApplicationName(GlobalMetadata<T> metadata, Output output) {
            this.metadata = metadata;
            this.output = output;
        }

        @Override
        protected void execute() {
            output.println("=> " + metadata.getName());
        }
    }

    @Command(name = "list", groupNames = "repositories")
    public static class ListRepositories extends SampleCommand {

        private final Output output;
        private final List<String> repositories;

        @Inject
        public ListRepositories(Output output, @Named("repositories") List<String> repositories) {
            this.output = output;
            this.repositories = repositories;
        }

        @Override
        protected void execute() {
            output.println("repositories");
            for (String repository : repositories) {
                output.println("-> " + repository);
            }
        }
    }

    @Command(name = "add", groupNames = "repositories")
    public static class AddRepository extends SampleCommand {

        @Required
        @Option(name = {"--type", "-t"})
        private String type;

        @Required
        @Arguments
        private String name;

        @Inject
        private Output output;

        @Override
        protected void execute() {
            output.println("=> add repository: " + type + "/" + name);
        }
    }

}
