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

import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.examples.ExampleExecutor;
import com.github.rvesse.airline.examples.ExampleRunnable;
import com.github.rvesse.airline.parser.resources.ClassGraphLocator;
import com.github.rvesse.airline.parser.resources.ResourceLocator;

import java.io.IOException;
import java.io.InputStream;

@Command(name = "external-help-debug")
public class ExternalHelpDebug implements ExampleRunnable {
    @Override
    public int run() {
        ResourceLocator locator = new ClassGraphLocator();
        String[] possibleLocations = new String[] {
                "/modules/com.github.rvesse.airline.examples/resources/exit-codes.csv",
                "/com.github.rvesse.airline.examples/resources/exit-codes.csv",
                "/exit-codes.csv",
                "exit-codes.csv",
                "/exit-codes.csv"

        };
        for (String location : possibleLocations) {
            try {
                InputStream input = locator.open(location, "");
                if (input == null) {
                    System.err.println("Failed to open " + location + " from the Module Path");
                } else {
                    input.close();
                    System.out.println("Opened " + location + " from the Module Path");
                    return 0;
                }

            } catch (IOException e) {
                System.err.println("Failed to open " + location + " from the Module Path: " + e.getMessage());
            }

            try (InputStream input = ExternalHelpDebug.class.getResourceAsStream(location)) {
                if (input != null) {
                    System.out.println("ExternalHelpDebug can directly open resource " + location);
                }
            } catch (IOException e) {
                // Ignore
            }
        }

        return 1;
    }

    public static void main(String[] args) {
        ExampleExecutor.executeSingleCommand(ExternalHelpDebug.class, args);
    }
}
