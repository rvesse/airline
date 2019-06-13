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

import com.github.rvesse.airline.io.decorations.AnsiDecorationProvider;
import com.github.rvesse.airline.io.decorations.BasicDecoration;
import com.github.rvesse.airline.io.decorations.sources.AnsiDecorationSource;
import com.github.rvesse.airline.io.output.AnsiBasicColorizedOutputStream;
import com.github.rvesse.airline.io.output.AnsiOutputStream;
import com.github.rvesse.airline.io.output.OutputStreamControlTracker;
import com.github.rvesse.airline.io.writers.AnsiWriter;
import com.github.rvesse.airline.prompts.PromptProvider;
import com.github.rvesse.airline.prompts.errors.PromptException;

public class StreamPrompt implements PromptProvider {

    private final OutputStream rawOutput;
    private final OutputStreamControlTracker<BasicDecoration> concealer;
    private final AnsiBasicColorizedOutputStream ansiOutput;
    private final PrintWriter writer;
    private final BufferedReader reader;

    public StreamPrompt(OutputStream output, InputStream input) {
        this.rawOutput = output;
        this.ansiOutput = new AnsiBasicColorizedOutputStream(output);
        this.concealer = new OutputStreamControlTracker<BasicDecoration>(output,
                new AnsiDecorationSource<BasicDecoration>());
        this.ansiOutput.registerControl(this.concealer);
        this.writer = new PrintWriter(this.ansiOutput);
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
    public char[] readSecureLine() {
        try {
            try {
                // Set output stream to concealed
                this.writer.flush();
                this.concealer.set(BasicDecoration.CONCEAL);
                this.writer.append(' ');
                this.writer.flush();
                
                return this.reader.readLine().toCharArray();
            } catch (IOException e) {
                throw new RuntimeException("Failed to read a secure line", e);
            }
        } finally {
            try {
                this.concealer.reset();
            } catch (IOException e) {
                throw new RuntimeException("Failed to restore input state after reading a secure line", e);
            }
        }
    }

    @Override
    public boolean supportsSecureReads() {
        return true;
    }

}
