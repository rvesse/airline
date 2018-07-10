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
package com.github.rvesse.airline.guice;

import org.testng.annotations.Test;
import org.testng.collections.Lists;

import java.util.List;

import static org.testng.Assert.*;

public class GuiceTest {

    @Test
    public void test() {
        assertOutput(
                Lists.newArrayList("repositories", "list"),
                Lists.newArrayList("repositories", "-> airline", "-> scm-manager", "-> jenkins")
        );
        assertOutput(
                Lists.newArrayList("repositories", "add", "--type", "git", "airline"),
                Lists.newArrayList("=> add repository: git/airline")
        );
        assertOutput(
                Lists.newArrayList("whoami"),
                Lists.newArrayList("=> root")
        );
        assertOutput(
                Lists.newArrayList("app"),
                Lists.newArrayList("=> sample")
        );
    }

    private void assertOutput(List<String> args, List<String> expectedOutput) {
        Output output = new Output();
        Sample.main(output, args.toArray(new String[0]));

        assertEquals(output.getLines(), expectedOutput);
    }

}
