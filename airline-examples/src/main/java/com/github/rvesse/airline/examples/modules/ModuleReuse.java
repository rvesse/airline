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

import java.util.ArrayList;
import java.util.List;

import jakarta.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import com.github.rvesse.airline.HelpOption;
import com.github.rvesse.airline.annotations.Arguments;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.examples.ExampleExecutor;
import com.github.rvesse.airline.examples.ExampleRunnable;

/**
 * Here we have another command which reuses module classes we've defined and
 * also adds locally defined options, we can
 * 
 * @author rvesse
 *
 */
@Command(name = "module-reuse", description = "A command that demonstrates re-use of modules and composition with locally defined options")
public class ModuleReuse implements ExampleRunnable {

    @Inject
    private HelpOption<ExampleRunnable> help;

    /**
     * A field marked with {@link Inject} will also be scanned for options
     */
    @Inject
    private VerbosityModule verbosity = new VerbosityModule();

    @Arguments
    private List<String> args = new ArrayList<String>();

    public static void main(String[] args) {
        ExampleExecutor.executeSingleCommand(ModuleReuse.class, args);
    }

    @Override
    public int run() {
        if (!help.showHelpIfRequested()) {
            System.out.println("Verbosity is " + verbosity.verbosity);
            System.out.println("Arguments were " + StringUtils.join(args, ", "));
        }
        return 0;
    }
}
