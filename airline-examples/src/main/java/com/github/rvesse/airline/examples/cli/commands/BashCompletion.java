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
package com.github.rvesse.airline.examples.cli.commands;

import java.io.FileOutputStream;
import java.io.IOException;

import jakarta.inject.Inject;

import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.Option;
import com.github.rvesse.airline.examples.ExampleRunnable;
import com.github.rvesse.airline.help.cli.bash.BashCompletionGenerator;
import com.github.rvesse.airline.model.GlobalMetadata;

@Command(name = "generate-completions", description = "Generates a Bash completion script, the file can then be sourced to provide completion for this CLI")
public class BashCompletion implements ExampleRunnable {

    @Inject
    private GlobalMetadata<ExampleRunnable> global;
    
    @Option(name = "--include-hidden", description = "When set hidden commands and options are shown in help", hidden = true)
    private boolean includeHidden = false;

    @Override
    public int run() {
        try (FileOutputStream out = new FileOutputStream(this.global.getName() + "-completions.bash")) {
            new BashCompletionGenerator<ExampleRunnable>(this.includeHidden, false).usage(global, out);
            System.out.println("Generated completion script " + this.global.getName() + "-completions.bash");
        } catch (IOException e) {
            System.err.println("Error generating completion script: " + e.getMessage());
            e.printStackTrace(System.err);
        }
        return 0;
    }
}
