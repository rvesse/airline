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
import java.util.List;
import java.util.Stack;


/**
 * Printer class for generating Troff output
 *
 */
public class TroffPrinter {

    private static final String REQUEST_TABLE_END = ".TE";

    private static final String REQUEST_TABLE_START = ".TS";

    private static final String REQUEST_FONT_ROMAN = "\\fR";

    private static final String REQUEST_FONT_BOLD = "\\fB";

    private static final String REQUEST_FONT_ITALIC = "\\fI";

    private static final String REQUEST_FONT_BOLD_ITALIC = "\\fBI";

    private static final String REQUEST_PARAGRAPH_TITLED = ".TP";

    private static final String REQUEST_PARAGRAPH_CURRENT_INDENTATION = ".IP";

    private static final String REQUEST_PARAGRAPH_NO_INDENTATION = ".IP \"\" 0";

    private static final String REQUEST_RESET_LEFT_MARGIN = ".RE";

    private static final String REQUEST_MOVE_LEFT_MARGIN = ".RS";

    private static final String REQUEST_BREAK = ".br";

    private enum ListType {
        BULLET, TITLED, PLAIN, NUMBERED
    }

    private static final String BULLET_STYLE_BULLET = "\"\\(bu\"";
    private static final String BULLET_STYLE_DASH = "\"-\"";
    private static final String BULLET_STYLE_EM_DASH = "\"\\(em\"";

    /**
     * Supported bullet style
     * @author rvesse
     *
     */
    public enum BulletStyle {
        //@formatter:off
        BULLET(BULLET_STYLE_BULLET), 
        DASH(BULLET_STYLE_DASH),
        EM_DASH(BULLET_STYLE_EM_DASH);
        //@formatter:on

        private final String glyph;

        BulletStyle(String glyph) {
            this.glyph = glyph;
        }
    }

    private static final int DEFAULT_INDENTATION = 4;

    private final PrintWriter writer;
    private int level = 0;
    private boolean newline = true;
    private boolean inSection = false;
    private final int indentation = DEFAULT_INDENTATION;
    private Stack<ListType> lists = new Stack<ListType>();
    private final String listGlyph;

    public TroffPrinter(PrintWriter writer) {
        this(writer, DEFAULT_INDENTATION, BulletStyle.DASH);
    }

    public TroffPrinter(PrintWriter writer, int indentation, BulletStyle style) {
        if (writer == null)
            throw new NullPointerException("writer cannot be null");
        this.writer = writer;
        this.listGlyph = style.glyph;
    }

    public void start(String title, int manSection) {
        start(title, manSection, null, null, null);
    }

    public void start(String title, int manSection, String header, String footer, String footerExtra) {
        if (!newline) {
            writer.println();
            newline = true;
        }

        writer.println(String.format(".TH %s %s %s %s %s", asArg(title), asArg(Integer.toString(manSection)),
                asArg(footer), asArg(footerExtra), asArg(header)));
        newline = true;
        inSection = false;
    }

    public void nextSection(String sectionTitle) {
        if (!newline) {
            writer.println();
            newline = true;
        }

        writer.println(String.format(".SH %s", sectionTitle));
        newline = true;
        inSection = true;
    }

    public void print(String value) {
        if (value == null || value.isEmpty())
            return;

        String[] lines = value.split("\n");
        if (lines.length == 0)
            return;
        if (lines.length == 1) {
            // Append some text value directly
            if (newline)
                prepareLine();
            writer.print(escape(value));
        } else {
            // Append a series of lines
            for (String line : lines) {
                this.appendLine(line);
            }
        }
    }

    public void println(String value) {
        print(value);
        if (!newline) {
            writer.println();
            newline = true;
        }
    }

    public void println() {
        if (newline)
            return;
        writer.println();
        newline = true;
    }

    public void lineBreak() {
        if (!newline)
            writer.println();
        writer.println(REQUEST_BREAK);
        newline = false;
    }

    public void printBold(String value) {
        print(String.format("%s%s%s", REQUEST_FONT_BOLD, value, REQUEST_FONT_ROMAN));
    }

    public void printItalic(String value) {
        print(String.format("%s%s%s", REQUEST_FONT_ITALIC, value, REQUEST_FONT_ROMAN));
    }

    public void printBoldItalic(String value) {
        print(String.format("%s%s%s", REQUEST_FONT_BOLD_ITALIC, value, REQUEST_FONT_ROMAN));
    }

    private void appendLine(String line) {
        if (line == null || line.isEmpty()) {
            writer.println();
            newline = true;
        }

        if (newline)
            prepareLine();

        writer.println(escape(line));
        newline = true;
    }

    public void startBulletedList() {
        if (!newline)
            writer.println();

        if (level > 0 || inSection) {
            writer.println(REQUEST_MOVE_LEFT_MARGIN);
        }
        lists.push(ListType.BULLET);
        printBullet();

        level++;
        newline = false;
    }

    public void startPlainList() {
        if (!newline)
            writer.println();

        if (level > 0 || inSection) {
            writer.println(REQUEST_MOVE_LEFT_MARGIN);
        }
        lists.push(ListType.PLAIN);
        printPlainBullet();

        level++;
        newline = false;
    }

    public void startNumberedList() {
        if (!newline)
            writer.println();

        if (level > 0 || inSection) {
            writer.println(REQUEST_MOVE_LEFT_MARGIN);
        }
        lists.push(ListType.NUMBERED);
        level++;
        printNumberedBullet(true);

        newline = false;
    }

    /**
     * Starts a titled list, the next line of text printed will form the title
     */
    public void startTitledList() {
        startTitledList(null);
    }

    /**
     * Starts a titled list with the given title
     * 
     * @param title
     */
    public void startTitledList(String title) {
        if (!newline)
            writer.println();

        if (level > 0 || inSection) {
            writer.println(REQUEST_MOVE_LEFT_MARGIN);
        }
        lists.push(ListType.TITLED);
        printTitledBullet();

        newline = false;
        level++;

        if (title != null) {
            writer.println(escape(title));
            writer.println(REQUEST_BREAK);
        }
    }

    public void nextBulletedListItem() {
        if (!newline)
            writer.println();

        if (level > 0) {
            if (lists.peek() != ListType.BULLET)
                throw new IllegalStateException(
                        "Cannot move to next bulleted list item when currently in another list type");
            printBullet();
            newline = false;
        } else {
            notInList();
        }
    }

    public void nextPlainListItem() {
        if (!newline)
            writer.println();

        if (level > 0) {
            if (lists.peek() != ListType.PLAIN)
                throw new IllegalStateException(
                        "Cannot move to next plain list item when currently in another list type");
            printPlainBullet();
            newline = false;
        } else {
            notInList();
        }
    }

    protected void notInList() {
        throw new IllegalStateException("Cannot start a new list item when not currently in a list");
    }

    public void nextNumberedListItem() {
        if (!newline)
            writer.println();

        if (level > 0) {
            if (lists.peek() != ListType.NUMBERED)
                throw new IllegalStateException(
                        "Cannot move to next numbered list item when currently in another list type");
            printNumberedBullet(false);
            newline = false;
        } else {
            notInList();
        }
    }

    /**
     * Moves to the next titled list item without providing a title. The next
     * line of text written will therefore be treated as the title for this
     * item.
     */
    public void nextTitledListItem() {
        nextTitledListItem(null);
    }

    public void nextTitledListItem(String title) {
        if (!newline)
            writer.println();

        if (level > 0) {
            if (lists.peek() != ListType.TITLED)
                throw new IllegalStateException(
                        "Cannot move to next titled list item when currently in another list type");
            printTitledBullet();
            newline = false;
        } else {
            throw new IllegalStateException("Cannot start a new titled list item when not currently in a list");
        }

        if ( ! (title == null || title.isEmpty())) {
            writer.println(escape(title));
            writer.println(REQUEST_BREAK);
        }
    }

    public void endList() {
        if (!newline)
            writer.println();

        if (level > 1) {
            // Reset indentation
            writer.println(REQUEST_RESET_LEFT_MARGIN);
        } else if (level == 1) {
            // Reset indentation
            writer.println(REQUEST_PARAGRAPH_NO_INDENTATION);
        } else {
            throw new IllegalStateException("Cannot end a list when not currently in a list");
        }

        ListType type = lists.pop();
        if (type == ListType.NUMBERED) {
            // Clear the register
            writer.println(String.format(".rr list%d", this.level));
        }
        level--;
        newline = true;
    }

    public void printTable(List<List<String>> rows, boolean hasHeader) {
        if (!newline)
            writer.println();

        writer.println(REQUEST_TABLE_START);
        writer.println("box;");

        // Find the maximum number of columns
        int maxColumns = 0;
        for (List<String> row : rows) {
            maxColumns = Math.max(maxColumns, row.size());
        }

        // Generate format
        if (hasHeader) {
            for (int i = 0; i < maxColumns; i++) {
                writer.print("cb");
                if (i < maxColumns - 1)
                    writer.print(" | ");
            }
            if (rows.size() == 1)
                writer.print(" .");
            writer.println();
        }
        if (rows.size() > 1) {
            for (int i = 0; i < maxColumns; i++) {
                writer.print("l");
                if (i < maxColumns - 1)
                    writer.print(" | ");
            }
            writer.println(" .");
        }

        // Output row data
        for (int r = 0; r < rows.size(); r++) {
            List<String> row = rows.get(r);

            if (r == 1 && hasHeader) {
                // Add divider between header and data
                for (int c = 0; c < maxColumns; c++) {
                    writer.print("_");
                    if (c < maxColumns - 1)
                        writer.print("\t|\t");
                }
                writer.println();
            }

            for (int c = 0; c < maxColumns; c++) {
                if (c >= row.size())
                    break;
                writer.print(escape(row.get(c)));
                if (c < row.size() - 1)
                    writer.print('\t');
            }
            writer.println();
        }

        writer.println(REQUEST_TABLE_END);
    }

    private void prepareLine() {
        if (level > 0) {
            // Continue the current indentation
            writer.println(REQUEST_PARAGRAPH_CURRENT_INDENTATION);
        } else if (inSection) {
            // When in a section and not in a list don't add extra indentation
            writer.println(REQUEST_PARAGRAPH_NO_INDENTATION);
        } else {
            writer.println(".");
        }
        newline = false;
    }

    protected void printBullet() {
        writer.println(String.format(".IP %s %d", this.listGlyph, this.indentation));
    }

    protected void printTitledBullet() {
        writer.println(String.format(REQUEST_PARAGRAPH_TITLED));
    }

    protected void printPlainBullet() {
        writer.println(String.format(".IP \"\" %d", this.indentation));
    }

    protected void printNumberedBullet(boolean first) {
        if (first) {
            // Create the register
            writer.println(String.format(".nr list%d 1 1", this.level));
            writer.println(String.format(".IP \\n[list%d]. %d", this.level, this.indentation));
        } else {
            writer.println(String.format(".IP \\n+[list%d]. %d", this.level, this.indentation));
        }
    }

    private String asArg(String arg) {
        return String.format("\"%s\"", escapeArg(arg));
    }

    private String escapeArg(String arg) {
        if (arg == null)
            return "";
        arg = arg.replace("-", "\\-");
        return arg.replace('"', ' ');
    }

    private String escape(String line) {
        if (line == null || line.isEmpty())
            return "";

        // A leading . must be escaped
        if (line.startsWith("."))
            line = "\\" + line;

        // Hyphen/Minus must be escaped
        line = line.replace("-", "\\-");

        // Tabs must be escaped
        line = line.replace("\t", "\\t");

        return line;
    }

    public void flush() {
        writer.flush();
    }

    public void finish() {
        while (level > 0) {
            this.endList();
        }
        writer.flush();
    }
}
