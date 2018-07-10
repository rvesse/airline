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

import com.github.rvesse.airline.Channels;
import com.github.rvesse.airline.Context;
import com.github.rvesse.airline.annotations.Arguments;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.model.CommandGroupMetadata;
import com.github.rvesse.airline.model.CommandMetadata;
import com.github.rvesse.airline.model.GlobalMetadata;
import com.github.rvesse.airline.model.MetadataLoader;
import com.github.rvesse.airline.model.OptionMetadata;
import com.github.rvesse.airline.model.SuggesterMetadata;
import com.github.rvesse.airline.parser.ParseState;
import com.github.rvesse.airline.parser.suggester.SuggestionParser;
import com.github.rvesse.airline.utils.AirlineUtils;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import static com.github.rvesse.airline.parser.ParserUtil.createInstance;

@Command(name = "suggest")
public class SuggestCommand<T> implements Runnable, Callable<Void> {
    private static final Map<Context, Class<? extends Suggester>> BUILTIN_SUGGESTERS = new HashMap<>();

    static {
        BUILTIN_SUGGESTERS.put(Context.GLOBAL, GlobalSuggester.class);
        BUILTIN_SUGGESTERS.put(Context.GROUP, GroupSuggester.class);
        BUILTIN_SUGGESTERS.put(Context.COMMAND, CommandSuggester.class);
    }

    @Inject
    public GlobalMetadata<T> metadata;

    @Arguments
    public List<String> arguments = new ArrayList<>();

    public Iterable<String> generateSuggestions() {
        SuggestionParser<T> parser = new SuggestionParser<T>();
        ParseState<T> state = parser.parse(metadata, arguments);

        Class<? extends Suggester> suggesterClass = BUILTIN_SUGGESTERS.get(state.getLocation());
        if (suggesterClass != null) {
            SuggesterMetadata suggesterMetadata = MetadataLoader.loadSuggester(suggesterClass);

            if (suggesterMetadata != null) {
                Map<Class<?>, Object> bindings = new HashMap<Class<?>, Object>();
                bindings.put(GlobalMetadata.class, metadata);

                if (state.getGroup() != null) {
                    bindings.put(CommandGroupMetadata.class, state.getGroup());
                }

                if (state.getCommand() != null) {
                    bindings.put(CommandMetadata.class, state.getCommand());
                }

                Suggester suggester = createInstance(suggesterMetadata.getSuggesterClass(),
                        Collections.<OptionMetadata> emptyList(), null, null, null,
                        suggesterMetadata.getMetadataInjections(), AirlineUtils.unmodifiableMapCopy(bindings));

                return suggester.suggest();
            }
        }

        return Collections.emptyList();
    }

    @Override
    public void run() {
        Channels.output().println(StringUtils.join(generateSuggestions(), '\n'));
    }

    @Override
    public Void call() {
        run();
        return null;
    }
}
