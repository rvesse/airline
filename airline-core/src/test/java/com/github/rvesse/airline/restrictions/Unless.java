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
package com.github.rvesse.airline.restrictions;

import com.github.rvesse.airline.annotations.Arguments;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.Option;
import com.github.rvesse.airline.annotations.restrictions.RequiredUnlessEnvironment;
import org.apache.commons.lang3.StringUtils;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Command(name = "unless")
public class Unless {

    private static String fromEnv(String... vars) {
        String value = null;
        for (String var : vars) {
            value = System.getenv(var);
            if (StringUtils.isNotBlank(value))
                break;
            value = null;
        }
        return value;
    }

    private static List<String> fromEnvAsList(String... vars) {
        String value = fromEnv(vars);
        if (StringUtils.isNotBlank(value)) {
            return Arrays.asList(StringUtils.split(value, ","));
        }
        return new ArrayList<>();
    }
    
    @Option(name = "--path", description = "Sets the PATH.")
    @RequiredUnlessEnvironment(variables = "PATH")
    public String path = fromEnv("PATH");
    
    @Option(name = "--foo", description = "Foo something?")
    @RequiredUnlessEnvironment(variables = { "FOO", "FOO_BAR" })
    public String foo = fromEnv("FOO", "FOO_BAR");

    @Arguments(title = "arg", description = "Provides additional arguments.")
    @RequiredUnlessEnvironment(variables = { "ARGS", "ARGUMENTS" })
    public List<String> args = new ArrayList<>(fromEnvAsList("ARGS", "ARGUMENTS"));
}