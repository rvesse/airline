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

package com.github.rvesse.airline.examples.userguide.help.sections;

import java.io.IOException;
import java.util.List;

import com.github.rvesse.airline.annotations.AirlineModule;
import com.github.rvesse.airline.annotations.Arguments;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.help.external.ExternalExitCodes;
import com.github.rvesse.airline.annotations.help.external.ExternalProse;
import com.github.rvesse.airline.annotations.help.external.ExternalTabularExamples;
import com.github.rvesse.airline.examples.ExampleExecutor;
import com.github.rvesse.airline.examples.ExampleRunnable;
import com.github.rvesse.airline.help.Help;
import com.github.rvesse.airline.model.CommandMetadata;

@Command(name = "external-help", description = "A command with help sections provided via separate resources")
@ExternalProse(title = "Discussion", source = "/discussion.txt")
@ExternalExitCodes(source = "/exit-codes.csv")
@ExternalTabularExamples(source = "/examples.csv")
public class ExternalHelp implements ExampleRunnable {

    @AirlineModule
    private CommandMetadata metadata;
    
    @Arguments
    private List<String> arguments;

    @Override
    public int run() {
        try {
            Help.help(this.metadata);
            return 0;
        } catch (IOException e) {
            e.printStackTrace();
            return 1;
        }
    }
    
    public static void main(String[] args) {
        ExampleExecutor.executeSingleCommand(ExternalHelp.class, args);
    }

}
