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
package com.github.rvesse.airline.tests.args;

import com.github.rvesse.airline.annotations.Arguments;
import com.github.rvesse.airline.annotations.Option;

import java.util.ArrayList;
import java.util.List;

public class ArgsDefault
{
    @Arguments
    public List<String> parameters = new ArrayList<>();

    @Option(name = "-log", description = "Level of verbosity")
    public Integer log = 1;

    @Option(name = "-groups", description = "Comma-separated list of group names to be run")
    public String groups;

    @Option(name = "-debug", description = "Debug mode")
    public boolean debug = false;

    @Option(name = "-level", description = "A long number")
    public long level;
}
