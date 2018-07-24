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
package com.github.rvesse.airline.examples.userguide;

import com.github.rvesse.airline.CommandLineInterface;
import com.github.rvesse.airline.annotations.Cli;

//@formatter:off
@Cli(name = "basic", 
    description = "Provides a basic example CLI",
    defaultCommand = GettingStarted.class, 
    commands = { GettingStarted.class, Tool.class })
//@formatter:on
public class BasicCli {

    public static void main(String[] args) {
        CommandLineInterface<Runnable> cli = new CommandLineInterface<>(BasicCli.class);
        Runnable cmd = cli.parse(args);
        cmd.run();
    }
}
