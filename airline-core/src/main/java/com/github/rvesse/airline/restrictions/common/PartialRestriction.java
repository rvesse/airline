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

package com.github.rvesse.airline.restrictions.common;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import com.github.rvesse.airline.utils.CollectionUtils;

import com.github.rvesse.airline.help.sections.HelpFormat;
import com.github.rvesse.airline.help.sections.HelpHint;
import com.github.rvesse.airline.model.ArgumentsMetadata;
import com.github.rvesse.airline.model.OptionMetadata;
import com.github.rvesse.airline.parser.ParseState;
import com.github.rvesse.airline.restrictions.AbstractCommonRestriction;
import com.github.rvesse.airline.restrictions.ArgumentsRestriction;
import com.github.rvesse.airline.restrictions.OptionRestriction;
import com.github.rvesse.airline.utils.AirlineUtils;
import com.github.rvesse.airline.utils.predicates.parser.ParsedOptionFinder;

public class PartialRestriction extends AbstractCommonRestriction implements HelpHint {

    private final Set<Integer> indices = new TreeSet<>();
    private final OptionRestriction optionRestriction;
    private final ArgumentsRestriction argumentsRestriction;
    private final HelpHint hint;

    private PartialRestriction(OptionRestriction optionRestriction) {
        this.optionRestriction = optionRestriction;
        this.argumentsRestriction = optionRestriction instanceof ArgumentsRestriction
                ? (ArgumentsRestriction) optionRestriction : null;
        this.hint = optionRestriction instanceof HelpHint ? (HelpHint) optionRestriction : null;
    }

    private PartialRestriction(ArgumentsRestriction argumentsRestriction) {
        this.optionRestriction = argumentsRestriction instanceof OptionRestriction
                ? (OptionRestriction) argumentsRestriction : null;
        this.argumentsRestriction = argumentsRestriction;
        this.hint = argumentsRestriction instanceof HelpHint ? (HelpHint) argumentsRestriction : null;
    }

    public PartialRestriction(int[] indices, OptionRestriction optionRestriction) {
        this(optionRestriction);
        for (int i : indices) {
            this.indices.add(i);
        }
    }

    public PartialRestriction(int[] indices, ArgumentsRestriction argumentsRestriction) {
        this(argumentsRestriction);
        for (int i : indices) {
            this.indices.add(i);
        }
    }

    public PartialRestriction(Collection<Integer> indices, OptionRestriction optionRestriction) {
        this(optionRestriction);
        this.indices.addAll(indices);
    }

    public PartialRestriction(Collection<Integer> indices, ArgumentsRestriction argumentsRestriction) {
        this(argumentsRestriction);
        this.indices.addAll(indices);
    }

    private <T> boolean isApplicableToOption(ParseState<T> state, OptionMetadata option) {
        int index = CollectionUtils.countMatches(state.getParsedOptions(), new ParsedOptionFinder(option))
                % option.getArity();
        return indices.contains(index);
    }

    @Override
    public <T> void preValidate(ParseState<T> state, OptionMetadata option, String value) {
        if (optionRestriction == null)
            return;
        if (!isApplicableToOption(state, option))
            return;

        this.optionRestriction.preValidate(state, option, value);
    }

    @Override
    public <T> void postValidate(ParseState<T> state, OptionMetadata option, Object value) {
        if (optionRestriction == null)
            return;
        if (!isApplicableToOption(state, option))
            return;

        this.optionRestriction.postValidate(state, option, value);
    }

    private <T> boolean isApplicableToArgument(ParseState<T> state) {
        int index = state.getParsedArguments().size();
        return indices.contains(index);
    }

    @Override
    public <T> void preValidate(ParseState<T> state, ArgumentsMetadata arguments, String value) {
        if (argumentsRestriction == null)
            return;
        if (!isApplicableToArgument(state))
            return;

        this.argumentsRestriction.preValidate(state, arguments, value);
    }

    @Override
    public <T> void postValidate(ParseState<T> state, ArgumentsMetadata arguments, Object value) {
        if (argumentsRestriction == null)
            return;
        if (!isApplicableToArgument(state))
            return;

        this.argumentsRestriction.postValidate(state, arguments, value);
    }

    @Override
    public String getPreamble() {
        if (this.hint == null)
            return null;

        // Add a note about the restriction to the specific arguments
        StringBuilder builder = new StringBuilder();
        builder.append("The following restriction only applies to the ");
        int count = 0;
        for (int i : this.indices) {
            if (count > 0)
                builder.append(", ");
            builder.append(AirlineUtils.toOrdinal(i + 1));
            count++;
        }
        builder.append(" value");
        if (this.indices.size() > 1)
            builder.append('s');
        builder.append(": ");

        if (this.hint.getPreamble() != null)
            builder.append(this.hint.getPreamble());
        return builder.toString();
    }

    @Override
    public HelpFormat getFormat() {
        return this.hint != null ? this.hint.getFormat() : HelpFormat.NONE_PRINTABLE;
    }

    @Override
    public int numContentBlocks() {
        return this.hint != null ? this.hint.numContentBlocks() : 0;
    }

    @Override
    public String[] getContentBlock(int blockNumber) {
        if (this.hint != null) {
            return this.hint.getContentBlock(blockNumber);
        } else {
            throw new IndexOutOfBoundsException();
        }
    }
}
