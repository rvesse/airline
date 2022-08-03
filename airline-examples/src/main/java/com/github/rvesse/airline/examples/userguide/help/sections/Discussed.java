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

import com.github.rvesse.airline.annotations.AirlineModule;
import com.github.rvesse.airline.annotations.Arguments;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.help.Discussion;
import com.github.rvesse.airline.examples.ExampleExecutor;
import com.github.rvesse.airline.examples.ExampleRunnable;
import com.github.rvesse.airline.help.Help;
import com.github.rvesse.airline.model.CommandMetadata;

import java.io.IOException;
import java.util.List;

@Command(name = "discussed", description = "A command with a discussion section")
//@formatter:off
@Discussion(paragraphs = 
    { 
        "This command uses the @Discussion annotation to add a discussion section to its help",
        "This is an example of using a Help Section annotation to add content into our generated help output." 
    })
//@formatter:on
public class Discussed implements ExampleRunnable {

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
        ExampleExecutor.executeSingleCommand(Discussed.class, args);
    }

}
