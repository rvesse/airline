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
import com.github.rvesse.airline.model.CommandGroupMetadata;
import com.github.rvesse.airline.model.CommandMetadata;
import com.github.rvesse.airline.model.OptionMetadata;

import java.util.ArrayList;
import java.util.List;

public class GroupSuggester implements Suggester {
    @AirlineModule
    public CommandGroupMetadata group;

    @Override
    public Iterable<String> suggest() {
        List<String> suggestions = new ArrayList<String>();
        for (CommandMetadata command : group.getCommands()) {
            suggestions.add(command.getName());
        }
        for (OptionMetadata option : group.getOptions()) {
            suggestions.addAll(option.getOptions());
        }
        return List.copyOf(suggestions);
    }
}
