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

package com.github.rvesse.airline.prompts.console;

import java.io.Console;
import java.io.IOException;
import java.io.PrintWriter;

import com.github.rvesse.airline.prompts.PromptProvider;

/**
 * A prompt that is backed by {@link System#console()}
 *
 */
public class ConsolePrompt implements PromptProvider {

    private final Console console;

    /**
     * Creates a new console prompt using the default {@link System#console()}
     */
    public ConsolePrompt() {
        this(System.console());
    }

    /**
     * Creates a new console prompt using the specified console
     * 
     * @param console
     *            Console
     */
    public ConsolePrompt(Console console) {
        if (console == null)
            throw new NullPointerException("Unable to create a Console prompt as a null console was provided");

        this.console = System.console();
    }

    @Override
    public PrintWriter getPromptWriter() {
        return this.console.writer();
    }

    @Override
    public String readLine() {
        return this.console.readLine();
    }

    @Override
    public int readKey() {
        try {
            return this.console.reader().read();
        } catch (IOException e) {
            return -1;
        }
    }

    @Override
    public char[] readSecureLine() {
        return this.console.readPassword();
    }

    @Override
    public boolean supportsSecureReads() {
        return true;
    }

}
