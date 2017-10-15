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

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.github.rvesse.airline.utils.CollectionUtils;

import com.github.rvesse.airline.help.sections.HelpFormat;
import com.github.rvesse.airline.help.sections.HelpHint;
import com.github.rvesse.airline.model.OptionMetadata;
import com.github.rvesse.airline.parser.ParseState;
import com.github.rvesse.airline.parser.errors.ParseOptionGroupException;
import com.github.rvesse.airline.restrictions.OptionRestriction;
import com.github.rvesse.airline.utils.predicates.parser.ParsedOptionFinder;
import com.github.rvesse.airline.utils.predicates.restrictions.MutuallyExclusiveWithFinder;
import com.github.rvesse.airline.utils.predicates.restrictions.MutuallyExclusiveWithOptionFinder;
import com.github.rvesse.airline.utils.predicates.restrictions.MutuallyExclusiveWithTagParsedOptionFinder;

public class MutuallyExclusiveRestriction implements OptionRestriction, HelpHint {

    private final String tag;

    public MutuallyExclusiveRestriction(String tag) {
        this.tag = tag;
    }

    @Override
    public <T> void finalValidate(ParseState<T> state, OptionMetadata option) {
        Collection<Map.Entry<OptionMetadata, Object>> parsedOptions = CollectionUtils.select(state.getParsedOptions(),
                new ParsedOptionFinder(option));

        Collection<OptionRestriction> restrictions = CollectionUtils.select(option.getRestrictions(),
                new MutuallyExclusiveWithFinder(this.tag));

        for (@SuppressWarnings("unused")
        OptionRestriction restriction : restrictions) {
            // Find other parsed options which have the same tag
            Collection<Map.Entry<OptionMetadata, Object>> otherParsedOptions = CollectionUtils
                    .select(state.getParsedOptions(), new MutuallyExclusiveWithTagParsedOptionFinder(this.tag));

            // There are some parsed options but ONLY for this option
            if (otherParsedOptions.size() > 0 && otherParsedOptions.size() == parsedOptions.size())
                continue;

            // Otherwise may need to error
            if (parsedOptions.size() > 0 && otherParsedOptions.size() > parsedOptions.size()) {
                Collection<OptionMetadata> taggedOptions = getTaggedOptions(state);
                throw new ParseOptionGroupException(
                        "Only one of the following options may be specified but %d were found: %s", tag, taggedOptions,
                        otherParsedOptions.size(), toOptionsList(taggedOptions));
            }
        }
    }

    private static String toOptionsList(Iterable<OptionMetadata> options) {
        StringBuilder builder = new StringBuilder();
        Iterator<OptionMetadata> ops = options.iterator();
        while (ops.hasNext()) {
            OptionMetadata option = ops.next();

            Iterator<String> names = option.getOptions().iterator();
            while (names.hasNext()) {
                builder.append(names.next());
                if (names.hasNext() || ops.hasNext())
                    builder.append(", ");
            }
        }
        return builder.toString();
    }
    
    private <T> Collection<OptionMetadata> getTaggedOptions(ParseState<T> state) {
        List<OptionMetadata> options = state.getCommand() != null ? state.getCommand().getAllOptions() : null;
        if (options == null)
            options = state.getGroup() != null ? state.getGroup().getOptions() : null;
        if (options == null)
            options = state.getGlobal() != null ? state.getGlobal().getOptions()
                    : Collections.<OptionMetadata> emptyList();
        return CollectionUtils.select(options, new MutuallyExclusiveWithOptionFinder(this.tag));
    }
    
    public String getTag() {
        return tag;
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
        return new String[] { String.format(
                "This option is part of the group '%s' from which only one option may be specified", this.tag) };
    }

    @Override
    public <T> void preValidate(ParseState<T> state, OptionMetadata option, String value) {
        // No pre-validation
    }

    @Override
    public <T> void postValidate(ParseState<T> state, OptionMetadata option, Object value) {
        // No post-validation
    }
}
