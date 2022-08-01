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
package com.github.rvesse.airline.examples.modules;

import com.github.rvesse.airline.HelpOption;
import com.github.rvesse.airline.annotations.AirlineModule;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.examples.ExampleExecutor;
import com.github.rvesse.airline.examples.ExampleRunnable;

/**
 * If you have some set of options that make sense together you can modularize
 * them out as a class and inject them into your actual command class
 * <p>
 * This is particularly useful if you want to avoid using inheritance for
 * options, especially in cases where different commands may use different
 * combinations of some common option sets
 * </p>
 *
 */
@Command(name = "modules", description = "A command that demonstrates the use of modules to group together sets of options for composition and reuse")
public class Modules implements ExampleRunnable {

    @AirlineModule
    private HelpOption<ExampleRunnable> help;

    /**
     * A field marked with {@link AirlineModule} will also be scanned for options
     */
    @AirlineModule
    public CredentialsModule credentials = new CredentialsModule();

    /**
     * A field marked with {@link AirlineModule} will also be scanned for options
     */
    @AirlineModule
    public VerbosityModule verbosity = new VerbosityModule();

    public static void main(String[] args) {
        ExampleExecutor.executeSingleCommand(Modules.class, args);
    }

    @Override
    public int run() {
        if (!help.showHelpIfRequested()) {
            switch (verbosity.verbosity) {
            case 3:
                System.out.println("Password is " + credentials.password);
            case 2:
                System.out.println("Verbosity set to " + verbosity.verbosity);
            case 1:
                System.out.println("User is " + credentials.user);
                break;
            default:
                System.err.println("Unexpected verbosity");
            }
        }
        return 0;
    }
}
