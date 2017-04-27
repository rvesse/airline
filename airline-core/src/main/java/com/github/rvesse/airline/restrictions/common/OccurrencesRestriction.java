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
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;

import com.github.rvesse.airline.help.sections.HelpFormat;
import com.github.rvesse.airline.help.sections.HelpHint;
import com.github.rvesse.airline.model.ArgumentsMetadata;
import com.github.rvesse.airline.model.OptionMetadata;
import com.github.rvesse.airline.parser.ParseState;
import com.github.rvesse.airline.parser.errors.ParseArgumentsMissingException;
import com.github.rvesse.airline.parser.errors.ParseRestrictionViolatedException;
import com.github.rvesse.airline.parser.errors.ParseTooManyArgumentsException;
import com.github.rvesse.airline.restrictions.AbstractCommonRestriction;
import com.github.rvesse.airline.utils.predicates.parser.ParsedOptionFinder;

public class OccurrencesRestriction extends AbstractCommonRestriction implements HelpHint {

    private final int occurrences;
    private final boolean maximum;

    public OccurrencesRestriction(int occurrences, boolean maximum) {
        this.occurrences = occurrences;
        this.maximum = maximum;
    }

    @Override
    public <T> void finalValidate(ParseState<T> state, OptionMetadata option) {
        if (occurrences <= 0)
            return;

        Collection<Map.Entry<OptionMetadata, Object>> parsedOptions = CollectionUtils.select(state.getParsedOptions(),
                new ParsedOptionFinder(option));

        if (maximum && parsedOptions.size() > this.occurrences) {
            throw new ParseRestrictionViolatedException(
                    "Option '%s' may be specified a maximum of %d times but was found %d times", option.getTitle(),
                    this.occurrences, parsedOptions.size());
        } else if (!maximum && parsedOptions.size() < this.occurrences) {
            throw new ParseRestrictionViolatedException(
                    "Option '%s' must be specified at least %d times but was only found %d times", option.getTitle(),
                    occurrences, parsedOptions.size());
        }
    }

    @Override
    public <T> void finalValidate(ParseState<T> state, ArgumentsMetadata arguments) {
        if (occurrences <= 0)
            return;

        if (maximum && state.getParsedArguments().size() > this.occurrences) {
            throw new ParseTooManyArgumentsException("At most %d arguments may be specified but %d were found",
                    occurrences, state.getParsedArguments().size());
        } else if (!maximum && state.getParsedArguments().size() < this.occurrences) {
            throw new ParseArgumentsMissingException("At least %d arguments must be specified but only %d were found",
                    titles(state, arguments), this.occurrences, state.getParsedArguments().size());
        }
    }

    private <T> List<String> titles(ParseState<T> state, ArgumentsMetadata arguments) {
        if (state.getParsedArguments().size() >= arguments.getTitle().size())
            return Collections.emptyList();
        if (occurrences >= arguments.getTitle().size())
            return arguments.getTitle().subList(state.getParsedArguments().size(), arguments.getTitle().size());
        return arguments.getTitle().subList(state.getParsedArguments().size(), occurrences);

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
        if (this.maximum) {
            return new String[] { String.format("This option may occur a maximum of %d times", this.occurrences) };
        } else {
            return new String[] { String.format("This option must occur a minimum of %d times", this.occurrences) };
        }
    }

}
