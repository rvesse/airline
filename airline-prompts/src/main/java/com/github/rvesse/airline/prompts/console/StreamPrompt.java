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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

import com.github.rvesse.airline.prompts.PromptProvider;

public class StreamPrompt implements PromptProvider {

    @SuppressWarnings("unused")
    private final OutputStream rawOutput;
    private final PrintWriter writer;
    private final BufferedReader reader;

    public StreamPrompt(OutputStream output, InputStream input) {
        this.rawOutput = output;
        this.writer = new PrintWriter(output);
        this.reader = new BufferedReader(new InputStreamReader(input));
    }

    @Override
    public PrintWriter getPromptStream() {
        return this.writer;
    }

    @Override
    public String readLine() {
        try {
            return this.reader.readLine();
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public int readKey() {
        try {
            return this.reader.read();
        } catch (IOException e) {
            return -1;
        }
    }

    @Override
    public boolean supportsSecureReads() {
        return false;
    }

    @Override
    public char[] readSecureLine() {
        return null;
    }

}
