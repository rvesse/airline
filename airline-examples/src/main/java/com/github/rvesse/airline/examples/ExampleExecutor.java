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
package com.github.rvesse.airline.examples;

import com.github.rvesse.airline.CommandLineInterface;
import com.github.rvesse.airline.SingleCommand;
import com.github.rvesse.airline.model.ParserMetadata;
import com.github.rvesse.airline.parser.errors.ParseException;

/**
 * Helper class that launches and runs the actual example commands and CLIs
 * 
 * @author rvesse
 *
 */
public class ExampleExecutor {

    private static <T extends ExampleRunnable> void execute(T cmd) {
        try {
            int exitCode = cmd.run();
            System.out.println();
            System.out.println("Exiting with Code " + exitCode);
            System.exit(exitCode);
        } catch (Throwable e) {
            System.err.println("Command threw error: " + e.getMessage());
            e.printStackTrace(System.err);
        }
    }

    public static <T extends ExampleRunnable> void executeSingleCommand(Class<T> cls, String[] args) {
        SingleCommand<T> parser = SingleCommand.singleCommand(cls);
        try {
            T cmd = parser.parse(args);
            execute(cmd);
        } catch (ParseException e) {
            System.err.println("Parser error: " + e.getMessage());
            System.exit(127);
        } catch (Throwable e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace(System.err);
            System.exit(128);
        }
    }
    
    public static <T extends ExampleRunnable> void executeSingleCommand(Class<T> cls, ParserMetadata<T> parserConfig, String[] args) {
        SingleCommand<T> parser = SingleCommand.singleCommand(cls, parserConfig);
        try {
            T cmd = parser.parse(args);
            execute(cmd);
        } catch (ParseException e) {
            System.err.println("Parser error: " + e.getMessage());
            System.exit(127);
        } catch (Throwable e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace(System.err);
            System.exit(128);
        }
    }

    public static <T extends ExampleRunnable> void executeCli(CommandLineInterface<T> cli, String[] args) {
        try {
            T cmd = cli.parse(args);
            execute(cmd);
        } catch (ParseException e) {
            System.err.println("Parser error: " + e.getMessage());
            System.exit(127);
        } catch (Throwable e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace(System.err);
            System.exit(128);
        }
    }
}
