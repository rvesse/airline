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

package com.github.rvesse.airline.examples.sendit;

import java.io.IOException;

import com.github.rvesse.airline.Cli;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.examples.ExampleRunnable;
import com.github.rvesse.airline.help.GlobalUsageGenerator;
import com.github.rvesse.airline.help.cli.CliGlobalUsageGenerator;

@Command(name = "generate-help")
public class GenerateHelp {

    public static void main(String[] args) {
        Cli<ExampleRunnable> cli = new Cli<ExampleRunnable>(SendItCli.class);
        
        GlobalUsageGenerator<ExampleRunnable> helpGenerator = new CliGlobalUsageGenerator<>();
        try {
            helpGenerator.usage(cli.getMetadata(), System.out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
