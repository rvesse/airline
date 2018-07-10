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
package com.github.rvesse.airline;

import org.testng.annotations.Test;

import java.io.*;

import static org.testng.Assert.assertEquals;

public class ChannelsTest {

    @Test
    public void testOutput() {
        PrintStream oldOutput = System.out;
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            System.setOut(new PrintStream(output));

            Channels.output().append("some output");
            assertEquals(output.toString(), "some output");
        } finally {
          System.setOut(oldOutput);
        }
    }

    @Test
    public void testError() {
        PrintStream oldError = System.err;
        try {
            ByteArrayOutputStream error = new ByteArrayOutputStream();
            System.setErr(new PrintStream(error));

            Channels.error().append("some error");
            assertEquals(error.toString(), "some error");
        } finally {
            System.setErr(oldError);
        }
    }

    @Test
    public void testInput() throws IOException {
        InputStream oldInput = System.in;
        try {
            byte[] inputData = "some input".getBytes();
            ByteArrayInputStream input = new ByteArrayInputStream(inputData);
            System.setIn(input);

            byte[] readData = new byte[inputData.length];
            Channels.input().read(readData);
            assertEquals(new String(readData), "some input");
        } finally {
            System.setIn(oldInput);
        }
    }
}
