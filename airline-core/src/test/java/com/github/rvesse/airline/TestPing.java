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

import com.github.rvesse.airline.utils.StringUtils;
import org.testng.annotations.Test;

public class TestPing
{
    @Test
    public void test()
    {
        // simple command parsing example
        ping();
        ping("-c", "5");
        ping("--count", "9");
        ping("--count=8");

        // show help
        ping("-h");
        ping("--help");
    }

    private void ping(String... args)
    {
        System.out.println("$ ping " + StringUtils.join(args, ' '));
        Ping.main(args);
        System.out.println();
    }
}
