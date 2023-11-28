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
package com.github.rvesse.airline.tests.args.positional;

import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.PositionalArgument;
import com.github.rvesse.airline.annotations.restrictions.Required;

@Command(name = "ArgsPositional", description = "ArgsPositional description")
public class ArgsPositionalNoExtras
{
    @PositionalArgument(position = PositionalArgument.FIRST, title = "File", description = "File to operate on")
    @Required
    public String file;
    
    @PositionalArgument(position = PositionalArgument.SECOND, title = "Mode", description = "Mode to set on the file")
    public Integer mode;
}
