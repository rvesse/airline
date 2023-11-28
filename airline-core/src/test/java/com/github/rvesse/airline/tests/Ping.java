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

import com.github.rvesse.airline.HelpOption;
import com.github.rvesse.airline.SingleCommand;
import com.github.rvesse.airline.annotations.AirlineModule;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.Option;

@Command(name = "ping", description = "network test utility")
public class Ping
{
    @AirlineModule
    public HelpOption<Ping> helpOption;

    @Option(name = {"-c", "--count"}, description = "Send count packets")
    public int count = 1;

    public static void main(String... args)
    {
        Ping ping = SingleCommand.singleCommand(Ping.class).parse(args);

        if (ping.helpOption.showHelpIfRequested()) {
            return;
        }

        ping.run();
    }

    public void run()
    {
        System.out.println("Ping count: " + count);
    }
}
