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
package com.github.rvesse.airline.examples.inheritance;

import jakarta.inject.Inject;

import com.github.rvesse.airline.HelpOption;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.Option;
import com.github.rvesse.airline.examples.ExampleExecutor;
import com.github.rvesse.airline.examples.ExampleRunnable;

/**
 * We can use standard Java inheritance with commands and the child commands
 * will inherit options defined on their parents
 */
@Command(name = "parent", description = "A parent command")
public class Parent implements ExampleRunnable {

    @Inject
    protected HelpOption<ExampleRunnable> help;

    @Option(name = "--parent", description = "An option provided by the parent")
    private boolean parent;

    public static void main(String[] args) {
        ExampleExecutor.executeSingleCommand(Parent.class, args);
    }

    @Override
    public int run() {
        if (!help.showHelpIfRequested()) {
            System.out.println("--parent was " + (this.parent ? "set" : "not set"));
        }
        return 0;
    }

}
