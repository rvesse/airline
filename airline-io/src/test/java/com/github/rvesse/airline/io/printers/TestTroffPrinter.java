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
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

public class TestTroffPrinter {

    private static final String TABLE_END = ".TE";
    private static final String TABLE_START = ".TS";
    private static final String END_LIST = ".IP \"\" 0";
    private static final String BULLET = ".IP \"-\" 4";
    private static final String PLAIN = ".IP \"\" 4";
    private static final String NUMBER_REG = ".nr list1 1 1";
    private static final String NUMBER_REG_CLEAR = ".rr list1";
    private static final String NUMBER_BULLET_FIRST = ".IP \\n[list1]. 4";
    private static final String NUMBER_BULLET_REST = ".IP \\n+[list1]. 4";
    private static final String TITLED_BULLET = ".TP";
    private static final String BREAK = ".br";
    
    @Test
    public void title_01() {
        StringWriter strWriter = new StringWriter();
        TroffPrinter printer = new TroffPrinter(new PrintWriter(strWriter));
        
        printer.start("Test", 1);
        printer.finish();
        
        //@formatter:off
        String expected = join(new String[] { 
                ".TH \"Test\" \"1\" \"\" \"\" \"\"",
                "" 
            }, '\n');
        //@formatter:on
        Assert.assertEquals(strWriter.toString(), expected);
    }
    
    @Test
    public void title_02() {
        StringWriter strWriter = new StringWriter();
        TroffPrinter printer = new TroffPrinter(new PrintWriter(strWriter));
        
        printer.start("Test\"Quotes", 1);
        printer.finish();
        
        //@formatter:off
        String expected = join(new String[] { 
                ".TH \"Test Quotes\" \"1\" \"\" \"\" \"\"",
                "" 
            }, '\n');
        //@formatter:on
        Assert.assertEquals(strWriter.toString(), expected);
    }

    @Test
    public void basic_text() {
        StringWriter strWriter = new StringWriter();
        TroffPrinter printer = new TroffPrinter(new PrintWriter(strWriter));
        
        printer.println("This is a line");
        printer.println("Another line");
        printer.finish();
        
        //@formatter:off
        String expected = join(new String[] { 
                ".",
                "This is a line", 
                ".", 
                "Another line", 
                "" 
            }, '\n');
        //@formatter:on
        Assert.assertEquals(strWriter.toString(), expected);
        
    }
    
    @Test
    public void basic_list() {
        StringWriter strWriter = new StringWriter();
        TroffPrinter printer = new TroffPrinter(new PrintWriter(strWriter));
        
        printer.startBulletedList();
        printer.println("A");
        printer.nextBulletedListItem();
        printer.println("B");
        printer.nextBulletedListItem();
        printer.println("C");
        printer.endList();
        printer.finish();
        
        //@formatter:off
        String expected = join(new String[] { 
                BULLET,
                "A", 
                BULLET, 
                "B", 
                BULLET,
                "C",
                END_LIST,
                ""
            }, '\n');
        //@formatter:on
        Assert.assertEquals(strWriter.toString(), expected);
    }
    
    @Test
    public void plain_list() {
        StringWriter strWriter = new StringWriter();
        TroffPrinter printer = new TroffPrinter(new PrintWriter(strWriter));
        
        printer.startPlainList();
        printer.println("A");
        printer.nextPlainListItem();
        printer.println("B");
        printer.nextPlainListItem();
        printer.println("C");
        printer.endList();
        printer.finish();
        
        //@formatter:off
        String expected = join(new String[] { 
                PLAIN,
                "A", 
                PLAIN, 
                "B", 
                PLAIN,
                "C",
                END_LIST,
                ""
            }, '\n');
        //@formatter:on
        Assert.assertEquals(strWriter.toString(), expected);
    }
    
    @Test
    public void numbered_list() {
        StringWriter strWriter = new StringWriter();
        TroffPrinter printer = new TroffPrinter(new PrintWriter(strWriter));
        
        printer.startNumberedList();
        printer.println("A");
        printer.nextNumberedListItem();
        printer.println("B");
        printer.nextNumberedListItem();
        printer.println("C");
        printer.endList();
        printer.finish();
        
        //@formatter:off
        String expected = join(new String[] { 
                NUMBER_REG,
                NUMBER_BULLET_FIRST,
                "A", 
                NUMBER_BULLET_REST, 
                "B", 
                NUMBER_BULLET_REST,
                "C",
                END_LIST,
                NUMBER_REG_CLEAR,
                ""
            }, '\n');
        //@formatter:on
        Assert.assertEquals(strWriter.toString(), expected);
    }
    
    @Test
    public void nested_list_01() {
        StringWriter strWriter = new StringWriter();
        TroffPrinter printer = new TroffPrinter(new PrintWriter(strWriter));
        
        printer.startBulletedList();
        printer.println("A");
        printer.startBulletedList();
        printer.println("B");
        printer.endList();
        printer.nextBulletedListItem();
        printer.println("C");
        printer.endList();
        printer.finish();
        
        //@formatter:off
        // - A
        //   - B
        // - C
        String expected = join(new String[] { 
                BULLET,
                "A", 
                ".RS",
                BULLET, 
                "B", 
                ".RE",
                BULLET,
                "C",
                END_LIST,
                ""
            }, '\n');
        //@formatter:on
        Assert.assertEquals(strWriter.toString(), expected);
    }
    
    @Test
    public void nested_list_02() {
        StringWriter strWriter = new StringWriter();
        TroffPrinter printer = new TroffPrinter(new PrintWriter(strWriter));
        
        printer.startBulletedList();
        printer.println("A");
        printer.startBulletedList();
        printer.println("B");
        printer.endList();
        printer.println("C");
        printer.endList();
        printer.finish();
        
        //@formatter:off
        // - A
        //   - B
        // C
        String expected = join(new String[] { 
                BULLET,
                "A", 
                ".RS",
                BULLET, 
                "B", 
                ".RE",
                ".IP",
                "C",
                END_LIST,
                ""
            }, '\n');
        //@formatter:on
        Assert.assertEquals(strWriter.toString(), expected);
    }
    
    @Test
    public void nested_list_03() {
        StringWriter strWriter = new StringWriter();
        TroffPrinter printer = new TroffPrinter(new PrintWriter(strWriter));
        
        printer.startBulletedList();
        printer.println("A");
        printer.startPlainList();
        printer.println("B");
        printer.endList();
        printer.nextBulletedListItem();
        printer.println("C");
        printer.endList();
        printer.finish();
        
        //@formatter:off
        // - A
        //   B
        // - C
        String expected = join(new String[] { 
                BULLET,
                "A", 
                ".RS",
                PLAIN, 
                "B", 
                ".RE",
                BULLET,
                "C",
                END_LIST,
                ""
            }, '\n');
        //@formatter:on
        Assert.assertEquals(strWriter.toString(), expected);
    }
    
    @Test
    public void nested_list_04() {
        StringWriter strWriter = new StringWriter();
        TroffPrinter printer = new TroffPrinter(new PrintWriter(strWriter));
        
        printer.startNumberedList();
        printer.println("A");
        printer.startNumberedList();
        printer.println("B");
        printer.endList();
        printer.nextNumberedListItem();
        printer.println("C");
        printer.endList();
        printer.finish();
        
        //@formatter:off
        // 1. A
        //   1. B
        // 2. C
        String expected = join(new String[] {
                NUMBER_REG,
                NUMBER_BULLET_FIRST,
                "A", 
                ".RS",
                NUMBER_REG.replace("list1", "list2"),
                NUMBER_BULLET_FIRST.replace("list1", "list2"),
                "B", 
                ".RE",
                NUMBER_REG_CLEAR.replace("list1", "list2"),
                NUMBER_BULLET_REST,
                "C",
                END_LIST,
                NUMBER_REG_CLEAR,
                ""
            }, '\n');
        //@formatter:on
        Assert.assertEquals(strWriter.toString(), expected);
    }
    
    @Test
    public void titled_list_01() {
        StringWriter strWriter = new StringWriter();
        TroffPrinter printer = new TroffPrinter(new PrintWriter(strWriter));
        
        printer.startTitledList("A");
        printer.println("Item A");
        printer.nextTitledListItem("B");
        printer.println("Item B");
        printer.endList();
        printer.finish();
        
        //@formatter:off
        // - A
        //   Item A
        // - B
        //   Item B
        String expected = join(new String[] { 
                TITLED_BULLET,
                "A", 
                BREAK,
                "Item A",
                TITLED_BULLET, 
                "B", 
                BREAK,
                "Item B",
                END_LIST,
                ""
            }, '\n');
        //@formatter:on
        Assert.assertEquals(strWriter.toString(), expected);
    }
    
    @Test
    public void titled_list_02() {
        StringWriter strWriter = new StringWriter();
        TroffPrinter printer = new TroffPrinter(new PrintWriter(strWriter));
        
        printer.startTitledList();
        printer.print("A");
        printer.lineBreak();
        printer.println("Item A");
        printer.nextTitledListItem("B");
        printer.println("Item B");
        printer.endList();
        printer.finish();
        
        //@formatter:off
        // - A
        //   Item A
        // - B
        //   Item B
        String expected = join(new String[] { 
                TITLED_BULLET,
                "A", 
                BREAK,
                "Item A",
                TITLED_BULLET, 
                "B", 
                BREAK,
                "Item B",
                END_LIST,
                ""
            }, '\n');
        //@formatter:on
        Assert.assertEquals(strWriter.toString(), expected);
    }
    
    @Test
    public void titled_list_03() {
        StringWriter strWriter = new StringWriter();
        TroffPrinter printer = new TroffPrinter(new PrintWriter(strWriter));
        
        printer.startTitledList();
        printer.printBold("A");
        printer.lineBreak();
        printer.println("Item A");
        printer.nextTitledListItem("B");
        printer.println("Item B");
        printer.endList();
        printer.finish();
        
        //@formatter:off
        // - A
        //   Item A
        // - B
        //   Item B
        String expected = join(new String[] { 
                TITLED_BULLET,
                "\\fBA\\fR", 
                BREAK,
                "Item A",
                TITLED_BULLET, 
                "B", 
                BREAK,
                "Item B",
                END_LIST,
                ""
            }, '\n');
        //@formatter:on
        Assert.assertEquals(strWriter.toString(), expected);
    }
    
    @Test
    public void table_01() {
        StringWriter strWriter = new StringWriter();
        TroffPrinter printer = new TroffPrinter(new PrintWriter(strWriter));
        
        List<List<String>> rows = new ArrayList<List<String>>();
        List<String> header = new ArrayList<String>();
        header.add("A");
        header.add("B");
        rows.add(header);
        List<String> row = new ArrayList<String>();
        row.add("One");
        rows.add(row);
        
        printer.printTable(rows, true);
        
        //@formatter:off
        String expected = join(new String[] { 
                TABLE_START,
                "box;", 
                "cb | cb",
                "l | l .",
                "A\tB", 
                "_\t|\t_",
                "One", 
                TABLE_END,
                ""
            }, '\n');
        //@formatter:on
        Assert.assertEquals(strWriter.toString(), expected);
    }
    
    @Test
    public void table_02() {
        StringWriter strWriter = new StringWriter();
        TroffPrinter printer = new TroffPrinter(new PrintWriter(strWriter));
        
        List<List<String>> rows = new ArrayList<List<String>>();
        List<String> header = new ArrayList<String>();
        header.add("A");
        header.add("B");
        rows.add(header);
        
        printer.printTable(rows, true);
        
        //@formatter:off
        String expected = join(new String[] { 
                TABLE_START,
                "box;", 
                "cb | cb .",
                "A\tB", 
                TABLE_END,
                ""
            }, '\n');
        //@formatter:on
        Assert.assertEquals(strWriter.toString(), expected);
    }
    
    @Test
    public void table_03() {
        StringWriter strWriter = new StringWriter();
        TroffPrinter printer = new TroffPrinter(new PrintWriter(strWriter));
        
        List<List<String>> rows = new ArrayList<List<String>>();
        List<String> header = new ArrayList<String>();
        header.add("A");
        header.add("B");
        rows.add(header);
        
        printer.printTable(rows, true);
        
        //@formatter:off
        String expected = join(new String[] { 
                TABLE_START,
                "box;", 
                "cb | cb .",
                "A\tB", 
                TABLE_END,
                ""
            }, '\n');
        //@formatter:on
        Assert.assertEquals(strWriter.toString(), expected);
    }
    
    @Test
    public void table_04() {
        StringWriter strWriter = new StringWriter();
        TroffPrinter printer = new TroffPrinter(new PrintWriter(strWriter));
        
        List<List<String>> rows = new ArrayList<List<String>>();
        List<String> header = new ArrayList<String>();
        header.add("A");
        header.add("B");
        rows.add(header);
        List<String> row = new ArrayList<String>();
        row.add("One");
        row.add("Two");
        row.add("Three");
        rows.add(row);
        row = new ArrayList<String>();
        row.add("One");
        rows.add(row);
        row = new ArrayList<String>();
        rows.add(row);
        row = new ArrayList<String>();
        row.add("");
        row.add("Two");
        rows.add(row);
        
        printer.printTable(rows, true);
        
        //@formatter:off
        String expected = join(new String[] { 
                TABLE_START,
                "box;", 
                "cb | cb | cb",
                "l | l | l .",
                "A\tB", 
                "_\t|\t_\t|\t_",
                "One\tTwo\tThree", 
                "One",
                "",
                "\tTwo",
                TABLE_END,
                ""
            }, '\n');
        //@formatter:on
        Assert.assertEquals(strWriter.toString(), expected);
    }
    
    @Test(expectedExceptions = IllegalStateException.class)
    public void bad_state_01() {
        StringWriter strWriter = new StringWriter();
        TroffPrinter printer = new TroffPrinter(new PrintWriter(strWriter));
        
        printer.endList();
    }
    
    @Test(expectedExceptions = IllegalStateException.class)
    public void bad_state_02() {
        StringWriter strWriter = new StringWriter();
        TroffPrinter printer = new TroffPrinter(new PrintWriter(strWriter));
        
        printer.startBulletedList();
        printer.nextTitledListItem();
    }
    
    @Test(expectedExceptions = IllegalStateException.class)
    public void bad_state_03() {
        StringWriter strWriter = new StringWriter();
        TroffPrinter printer = new TroffPrinter(new PrintWriter(strWriter));
        
        printer.startTitledList();
        printer.nextBulletedListItem();
    }
    
    public static String join(String[] s, char c) {
    	StringBuilder sb = new StringBuilder();
    	boolean first = true;
    	for (String t : s) {
    		if (first) {
    			first = false;
    		}
    		else {
    			sb.append(c);
    		}
    		sb.append(t);
    	}
    	return sb.toString();
    }
}
