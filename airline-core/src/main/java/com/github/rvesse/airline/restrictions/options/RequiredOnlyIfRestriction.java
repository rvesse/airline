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
package com.github.rvesse.airline.restrictions.options;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.github.rvesse.airline.help.sections.HelpFormat;
import com.github.rvesse.airline.help.sections.HelpHint;
import com.github.rvesse.airline.model.OptionMetadata;
import com.github.rvesse.airline.parser.ParseState;
import com.github.rvesse.airline.parser.errors.ParseOptionMissingException;
import com.github.rvesse.airline.restrictions.OptionRestriction;
import com.github.rvesse.airline.utils.predicates.parser.ParsedOptionFinder;

public class RequiredOnlyIfRestriction implements OptionRestriction, HelpHint {

    private final Set<String> names = new LinkedHashSet<>();

    public RequiredOnlyIfRestriction(String... names) {
        this.names.addAll(Arrays.asList(names));
    }

    @Override
    public <T> void finalValidate(ParseState<T> state, OptionMetadata option) {
        if (this.names.isEmpty())
            return;

        Collection<Pair<OptionMetadata, Object>> parsedOptions = CollectionUtils.select(state.getParsedOptions(),
                new ParsedOptionFinder(option));

        // If this option was seen then the required criteria has been fulfilled
        // regardless of whether any of the triggering options was actually
        // present
        if (parsedOptions.size() > 0)
            return;

        // Were any of the options that would trigger the required restriction
        // present?
        for (Pair<OptionMetadata, Object> otherOption : state.getParsedOptions()) {
            if (otherOption.getLeft().equals(option))
                continue;

            for (String name : this.names) {
                if (otherOption.getLeft().getOptions().contains(name))
                    throw new ParseOptionMissingException(option.getTitle(0));
            }
        }
    }

    @Override
    public <T> void preValidate(ParseState<T> state, OptionMetadata option, String value) {
        // No pre-validation
    }

    @Override
    public String getPreamble() {
        return null;
    }

    @Override
    public HelpFormat getFormat() {
        return HelpFormat.PROSE;
    }

    @Override
    public int numContentBlocks() {
        return 1;
    }

    @Override
    public String[] getContentBlock(int blockNumber) {
        if (blockNumber != 0)
            throw new IndexOutOfBoundsException();
        return new String[] { String.format("This option is required if any of the following options are specified: %s",
                StringUtils.join(this.names, ", ")) };
    }

    @Override
    public <T> void postValidate(ParseState<T> state, OptionMetadata option, Object value) {
        // No post-validation
    }

}
