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
package com.github.rvesse.airline.help.suggester;

import com.github.rvesse.airline.annotations.AirlineModule;
import com.github.rvesse.airline.model.CommandMetadata;
import com.github.rvesse.airline.model.MetadataLoader;
import com.github.rvesse.airline.model.OptionMetadata;
import com.github.rvesse.airline.model.ParserMetadata;

import java.util.ArrayList;
import java.util.List;

public class CommandSuggester
        implements Suggester
{
    @AirlineModule
    public CommandMetadata command;

    @Override
    public Iterable<String> suggest()
    {
        List<String> suggestions = new ArrayList<String>();
        for (OptionMetadata option : command.getCommandOptions()) {
            suggestions.addAll(option.getOptions());
        }

        if (command.getArguments() != null) {
            // Include arguments separator
            ParserMetadata<?> parserConfig = MetadataLoader.loadParser(command.getType());
            suggestions.add(parserConfig.getArgumentsSeparator());
        }

        return List.copyOf(suggestions);
    }
}
