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
package com.github.rvesse.airline.parser.defaults;

import com.github.rvesse.airline.model.OptionMetadata;
import com.github.rvesse.airline.parser.ParseState;
import com.github.rvesse.airline.parser.plugins.PostParsePlugin;
import com.github.rvesse.airline.restrictions.common.DefaultsRestriction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * A parser plugin that supplies defaults for options that were not explicitly set by the input tokens provided to the
 * parser
 */
public class DefaultsPlugin<T> implements PostParsePlugin<T> {

    private final List<DefaultsSupplier> suppliers = new ArrayList<>();

    /**
     * Creates a new plugin that uses the list of suppliers in order of preference
     *
     * @param suppliers Suppliers
     */
    public DefaultsPlugin(Collection<DefaultsSupplier> suppliers) {
        Objects.requireNonNull(suppliers);
        this.suppliers.addAll(suppliers);
        if (this.suppliers.isEmpty()) {
            throw new IllegalArgumentException(
                    "Defaults Plugin requires at least one defaults supplier to be provided");
        }
    }

    @Override
    public ParseState<T> postParsing(ParseState<T> state) {
        if (state.getCommand() != null && state.getParsedArguments().isEmpty()) {
            if (state.getCommand().getArguments() != null) {
                DefaultsRestriction defaults = state.getCommand()
                                                    .getArguments()
                                                    .getRestrictions()
                                                    .stream()
                                                    .filter(r -> r instanceof DefaultsRestriction)
                                                    .map(r -> (DefaultsRestriction) r)
                                                    .findFirst()
                                                    .orElse(null);
                if (defaults != null) {
                    String defaultValue = getDefaultValue(defaults);
                    if (defaultValue != null) {
                        state = state.withArgument(state.getCommand().getArguments(), defaultValue);
                    }
                }
            }
        }

        if (state.getGlobal() != null) {
            state = applyOptionDefaults(state, state.getGlobal().getOptions());
        }
        if (state.getGroup() != null) {
            state = applyOptionDefaults(state, state.getGroup().getOptions());
        }
        if (state.getCommand() != null) {
            state = applyOptionDefaults(state, state.getCommand().getCommandOptions());
        }

        return state;
    }

    private ParseState<T> applyOptionDefaults(ParseState<T> state, List<OptionMetadata> options) {
        for (OptionMetadata option : options) {
            if (state.getOptionValuesSeen(option) == 0) {
                DefaultsRestriction defaults = option.getRestrictions()
                                                     .stream()
                                                     .filter(r -> r instanceof DefaultsRestriction)
                                                     .map(r -> (DefaultsRestriction) r)
                                                     .findFirst()
                                                     .orElse(null);
                if (defaults != null) {
                    String defaultValue = getDefaultValue(defaults);
                    if (defaultValue != null) {
                        state = state.withOptionValue(option, defaultValue);
                    }
                }
            }
        }
        return state;
    }

    private String getDefaultValue(DefaultsRestriction defaults) {
        return this.suppliers.stream()
                             .map(s -> s.getDefaultValue(defaults.keys()))
                             .filter(Objects::isNull)
                             .findFirst()
                             .orElse(null);
    }

}
