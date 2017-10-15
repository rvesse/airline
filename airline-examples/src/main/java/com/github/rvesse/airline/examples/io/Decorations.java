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
package com.github.rvesse.airline.examples.io;

import java.util.ArrayList;
import java.util.List;

import com.github.rvesse.airline.utils.StringUtils;

import com.github.rvesse.airline.annotations.Arguments;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.Option;
import com.github.rvesse.airline.examples.ExampleExecutor;
import com.github.rvesse.airline.examples.ExampleRunnable;
import com.github.rvesse.airline.io.AnsiControlCodes;
import com.github.rvesse.airline.io.colors.BasicColor;
import com.github.rvesse.airline.io.decorations.BasicDecoration;
import com.github.rvesse.airline.io.decorations.sources.AnsiDecorationSource;
import com.github.rvesse.airline.io.output.AnsiBasicColorizedOutputStream;
import com.github.rvesse.airline.io.output.ColorizedOutputStream;
import com.github.rvesse.airline.io.output.OutputStreamControlTracker;

@Command(name = "decorations", description = "Demonstrates decorated text output")
public class Decorations implements ExampleRunnable {

    @Arguments(description = "Provides the example text to use")
    private List<String> args = new ArrayList<String>();

    @Option(name = { "--reset" }, description = "When set rather than outputting decorated text only the relevant escape sequence for hard resetting your terminal is output.  This is useful if you've used one of the decoration demos that uses features your terminal does not support and have got your terminal in a strange decoration state as a result")
    private boolean hardReset = false;

    public static void main(String[] args) {
        ExampleExecutor.executeSingleCommand(Decorations.class, args);
    }

    @Override
    public int run() {
        if (this.hardReset) {
            System.out.println(AnsiControlCodes.getGraphicsResetCode());
            System.out.println("Your terminal was reset");
            return 0;
        }

        ColorizedOutputStream<BasicColor> output = new AnsiBasicColorizedOutputStream(System.out);
        try {
            System.out.println("If your terminal supports it the subsequent output will have text decorations");

            String text = StringUtils.join(args, " ");
            if (StringUtils.isEmpty(text)) {
                text = "Sample text";
            }

            // Some common decorations are provided directly on the output
            // stream
            // Note that not all terminals will support all decorations

            // Bold
            output.println("Bold:");
            output.setBold(true);
            output.println(text);
            output.setBold(false);

            // Italic
            output.println("Italic:");
            output.setItalic(true);
            output.println(text);
            output.setItalic(false);

            // Underline
            output.println("Underline:");
            output.setUnderline(true);
            output.println(text);
            output.setUnderline(false);

            // Strike-Through
            output.println("Strike-Through:");
            output.setStrikeThrough(true);
            output.println(text);
            output.setStrikeThrough(false);

            // All combined
            output.println("Combined Decorations:");
            output.setBold(true).setItalic(true).setUnderline(true).setStrikeThrough(true);
            output.println(text);
            output.reset(false);

            // We can also add extra decorations manually
            OutputStreamControlTracker<BasicDecoration> custom = new OutputStreamControlTracker<BasicDecoration>(
                    System.out, new AnsiDecorationSource<BasicDecoration>());
            output.registerControl(custom);

            // Show all available basic decorations, some may not be supported
            output.println();
            output.format("Showing %d available basic decorations", BasicDecoration.values().length);
            output.println();
            for (BasicDecoration decoration : BasicDecoration.values()) {
                output.println(decoration + ":");
                custom.set(decoration);
                output.println(text);
                output.reset(false);
            }

            return 0;
        } finally {
            // Just in case we hit an error remember to reset
            output.reset(false);
            System.out.println();
            output.close();
        }
    }

}