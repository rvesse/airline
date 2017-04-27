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
package com.github.rvesse.airline.io.printers;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Helper for printing out usage information
 * <p>
 * Provides support for maintaining indents and wrapping text to a column width
 * </p>
 * 
 */
public class UsagePrinter {
    private final PrintWriter out;
    private final int maxSize;
    private final int indent;
    private final int hangingIndent;
    private final AtomicInteger currentPosition;

    public UsagePrinter(PrintWriter out) {
        this(out, 79);
    }

    public UsagePrinter(PrintWriter out, int maxSize) {
        this(out, maxSize, 0, 0, new AtomicInteger());
    }

    public UsagePrinter(PrintWriter out, int maxSize, int indent, int hangingIndent, AtomicInteger currentPosition) {
        if (out == null)
            throw new NullPointerException("Writer cannot be null");
        this.out = out;
        this.maxSize = maxSize;
        this.indent = indent;
        this.hangingIndent = hangingIndent;
        this.currentPosition = currentPosition;
    }

    public UsagePrinter newIndentedPrinter(int size) {
        return new UsagePrinter(out, maxSize, indent + size, hangingIndent, currentPosition);
    }

    public UsagePrinter newPrinterWithHangingIndent(int size) {
        return new UsagePrinter(out, maxSize, indent, hangingIndent + size, currentPosition);
    }

    public UsagePrinter newline()  {
        out.append("\n");
        currentPosition.set(0);
        return this;
    }

    public UsagePrinter appendTable(Iterable<? extends Iterable<String>> table, int rowSpacing)  {
        List<Integer> columnSizes = new ArrayList<>();
        for (Iterable<String> row : table) {
            int column = 0;
            for (String value : row) {
                while (column >= columnSizes.size()) {
                    columnSizes.add(0);
                }
                int valueLength = value != null ? value.length() : 0;
                columnSizes.set(column, Math.max(valueLength, columnSizes.get(column)));
                column++;
            }
        }

        if (currentPosition.get() != 0) {
            currentPosition.set(0);
            out.append("\n");
        }

        for (Iterable<String> row : table) {
            int column = 0;
            StringBuilder line = new StringBuilder();
            for (String value : row) {
                int columnSize = columnSizes.get(column);
                if (value != null) {
                    line.append(value);
                    line.append(spaces(columnSize - value.length()));
                } else {
                    line.append(spaces(columnSize));
                }
                line.append("   ");
                column++;
            }
            out.append(spaces(indent)).append(trimEnd(line.toString())).append("\n");

            for (int i = 0; i < rowSpacing; i++) {
                out.append('\n');
            }
        }

        return this;
    }

    public static String trimEnd(final String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        int end = str.length();
        while ((end != 0) && Character.isWhitespace(str.charAt(end - 1))) {
            end--;
        }

        return str.substring(0, end);
    }

    public UsagePrinter append(String value)  {
        return append(value, false);
    }

    public UsagePrinter appendOnOneLine(String value)  {
        return append(value, true);
    }
    
    public UsagePrinter appendWords(String[] words) {
        return appendWords(words, false);
    }

    public UsagePrinter appendWords(Iterable<String> words)  {
        return appendWords(words, false);
    }

    public UsagePrinter append(String value, boolean avoidNewlines)  {
        if (value == null)
            return this;
        if (avoidNewlines) {
            return appendWords(arrayToList(value.split("\\s+")), avoidNewlines);
        } else {
            return appendLines(arrayToList(value.split(value, '\n')), avoidNewlines);
        }
    }

    public UsagePrinter appendLines(Iterable<String> lines)  {
        return appendLines(lines, false);
    }

    public UsagePrinter appendLines(Iterable<String> lines, boolean avoidNewlines)  {
        Iterator<String> iter = lines.iterator();
        while (iter.hasNext()) {
            String line = iter.next();
            if (line == null || line.isEmpty())
                continue;
            appendWords(arrayToList(line.split("\\s+")), avoidNewlines);
            if (iter.hasNext()) {
                this.newline();
            }
        }
        return this;
    }
    
    public UsagePrinter appendWords(String[] words, boolean avoidNewlines) {
        return appendWords(arrayToList(words), avoidNewlines);
    }

    public UsagePrinter appendWords(Iterable<String> words, boolean avoidNewlines)  {
        int bracketCount = 0;
        for (String word : words) {
            if (null == word || "".equals(word)) {
                continue;
            }
            if (currentPosition.get() == 0) {
                // beginning of line
                out.append(spaces(indent));
                currentPosition.getAndAdd((indent));
            } else if (word.length() > maxSize || currentPosition.get() + word.length() <= maxSize || bracketCount > 0
                    || avoidNewlines) {
                // between words
                out.append(" ");
                currentPosition.getAndIncrement();
            } else {
                // wrap line
                out.append("\n").append(spaces(indent)).append(spaces(hangingIndent));
                currentPosition.set(indent);
            }

            out.append(word);
            currentPosition.getAndAdd((word.length()));
            if (word.contains("{") || word.contains("[") || word.contains("<")) {
                bracketCount++;
            }
            if (word.contains("}") || word.contains("]") || word.contains(">")) {
                bracketCount--;
            }
        }
        return this;
    }

    public void flush()  {
        this.out.flush();
    }

    public void close()  {
        this.out.close();
    }

    private static String spaces(int count) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < count; i++) {
            result.append(" ");
        }
        return result.toString();
    }
    
    private static List<String> arrayToList(String[] values) {
        List<String> list = new ArrayList<String>();
        for (String value : values) {
            list.add(value);
        }
        return list;
    }
}
