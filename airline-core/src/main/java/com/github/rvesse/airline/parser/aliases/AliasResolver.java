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
package com.github.rvesse.airline.parser.aliases;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.iterators.PeekingIterator;

import com.github.rvesse.airline.model.AliasMetadata;
import com.github.rvesse.airline.model.CommandGroupMetadata;
import com.github.rvesse.airline.model.CommandMetadata;
import com.github.rvesse.airline.model.GlobalMetadata;
import com.github.rvesse.airline.parser.AbstractParser;
import com.github.rvesse.airline.parser.ParseState;
import com.github.rvesse.airline.parser.errors.ParseAliasCircularReferenceException;
import com.github.rvesse.airline.utils.predicates.parser.AliasFinder;
import com.github.rvesse.airline.utils.predicates.parser.CommandFinder;
import com.github.rvesse.airline.utils.predicates.parser.GroupFinder;

/**
 * Resolves aliases
 *
 * @param <T>
 *            Command type
 */
public class AliasResolver<T> extends AbstractParser<T> {

    public PeekingIterator<String> resolveAliases(PeekingIterator<String> tokens, ParseState<T> state) {
        Predicate<? super CommandGroupMetadata> findGroupPredicate;
        Predicate<? super CommandMetadata> findCommandPredicate;

        // Nothing to do if no further tokens
        if (!tokens.hasNext())
            return tokens;

        // Nothing to do if no aliases defined
        if (state.getParserConfiguration().getAliases().size() == 0)
            return tokens;

        Set<String> aliasesSeen = new TreeSet<String>();

        do {
            // Try to find an alias
            AliasMetadata alias = CollectionUtils.find(state.getParserConfiguration().getAliases(),
                    new AliasFinder(tokens.peek()));

            // Nothing further to do if no aliases found
            if (alias == null) {
                // Has the user provided a prefix to force a built-in?
                if (tokens.peek().startsWith(new String(new char[] { state.getParserConfiguration().getAliasForceBuiltInPrefix() }))) {
                    String nextToken = tokens.next().substring(1);
                    List<String> newTokens = new ArrayList<>();
                    newTokens.add(nextToken);
                    while (tokens.hasNext()) {
                        newTokens.add(tokens.next());
                    }
                    return new PeekingIterator<String>(newTokens.iterator());
                }
                return tokens;
            }

            // Check for circular references
            if (!aliasesSeen.add(alias.getName())) {
                // Handle the error and exit alias resolution
                state.getParserConfiguration().getErrorHandler()
                        .handleError(new ParseAliasCircularReferenceException(alias.getName(), aliasesSeen));
                return tokens;
            }

            // Can we override built-ins?
            if (!state.getParserConfiguration().aliasesOverrideBuiltIns()) {
                // If not we must check we don't have a default
                // group/command with the same name as otherwise that
                // would take precedence
                if (state.getGlobal() != null) {
                    GlobalMetadata<T> metadata = state.getGlobal();
                    findGroupPredicate = new GroupFinder(tokens.peek());
                    findCommandPredicate = new CommandFinder(tokens.peek());
                    if (CollectionUtils.find(metadata.getCommandGroups(), findGroupPredicate) != null
                            || CollectionUtils.find(metadata.getDefaultGroupCommands(), findCommandPredicate) != null)
                        return tokens;
                }
            }

            // Discard the alias token
            tokens.next();

            // Apply the alias
            List<String> newParams = new ArrayList<String>();
            List<String> remainingParams = new ArrayList<String>();
            while (tokens.hasNext()) {
                remainingParams.add(tokens.next());
            }

            // Process alias arguments
            Set<Integer> used = new TreeSet<Integer>();
            for (String arg : alias.getArguments()) {
                if (arg.startsWith("$")) {
                    // May be a positional parameter
                    try {
                        int num = Integer.parseInt(arg.substring(1));
                        num--;

                        if (num >= 0 && num < remainingParams.size()) {
                            // Valid positional parameter
                            newParams.add(remainingParams.get(num));
                            used.add(num);
                            continue;
                        }
                    } catch (NumberFormatException e) {
                        // Ignore - the number was invalid so we'll
                        // treat it as an ordinary parameter
                    }
                }

                // Some other parameter
                newParams.add(arg);
            }

            // Remove used positional parameters
            int removed = 0;
            for (int pos : used) {
                remainingParams.remove(pos - removed);
                removed++;
            }

            // Pass through any remaining parameters
            for (String arg : remainingParams) {
                newParams.add(arg);
            }

            // Prepare a new tokens iterator
            tokens = new PeekingIterator<String>(newParams.iterator());
        } while (state.getParserConfiguration().aliasesMayChain());

        return tokens;
    }
}
