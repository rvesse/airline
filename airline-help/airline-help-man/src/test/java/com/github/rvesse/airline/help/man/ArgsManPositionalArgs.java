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
package com.github.rvesse.airline.help.man;

import java.util.List;

import com.github.rvesse.airline.annotations.Arguments;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.PositionalArgument;
import com.github.rvesse.airline.annotations.restrictions.Required;

@Command(name = "args-man-pos-args")
public class ArgsManPositionalArgs {
    
    @PositionalArgument(title = "File", position = PositionalArgument.FIRST, description = "File to operate on")
    @Required
    public String file;
    
    @PositionalArgument(title = "Mode", position = PositionalArgument.SECOND, description = "File mode to set")
    public Integer mode;
    
    @Arguments(title = "ExtraArg", description = "Additional arguments")
    public List<String> arguments;
}
