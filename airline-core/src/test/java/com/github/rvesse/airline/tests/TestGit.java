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
package com.github.rvesse.airline.tests;

import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Test;

public class TestGit
{
    @Test
    public void test()
    {
        // simple command parsing example
        git("add", "-p", "file");
        git("remote", "add", "origin", "git@github.com:airlift/airline.git");
        git("-v", "remote", "show", "origin");
        // test default command
        git("remote");
        git("remote", "origin");
        git("remote", "-n", "origin");
        git("-v", "remote", "origin");

        // show help
        git();
        git("help");
        git("help", "git");
        git("help", "add");
        git("help", "remote");
        git("help", "remote", "show");
    }
    
    private void git(String... args)
    {
        System.out.println("$ git " + StringUtils.join(args, ' '));
        Git.main(args);
        System.out.println();
    }
}
