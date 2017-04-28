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
package com.github.rvesse.airline.parser;

import com.github.rvesse.airline.Context;
import com.github.rvesse.airline.model.ArgumentsMetadata;
import com.github.rvesse.airline.model.CommandGroupMetadata;
import com.github.rvesse.airline.model.CommandMetadata;
import com.github.rvesse.airline.model.GlobalMetadata;
import com.github.rvesse.airline.model.OptionMetadata;
import com.github.rvesse.airline.model.ParserMetadata;
import com.github.rvesse.airline.parser.aliases.AliasResolver;
import com.github.rvesse.airline.parser.options.OptionParser;
import com.github.rvesse.airline.utils.AirlineUtils;
import com.github.rvesse.airline.utils.predicates.parser.AbbreviatedCommandFinder;
import com.github.rvesse.airline.utils.predicates.parser.AbbreviatedGroupFinder;
import com.github.rvesse.airline.utils.predicates.parser.CommandFinder;
import com.github.rvesse.airline.utils.predicates.parser.GroupFinder;

import java.util.List;

import com.github.rvesse.airline.utils.CollectionUtils;
import com.github.rvesse.airline.utils.Predicate;
import com.github.rvesse.airline.utils.PeekingIterator;

/**
 * Abstract implementation of a parser for commands that can cope with both CLI
 * and Single Command parsing
 *
 * @param <T>
 *            Command type
 */
public abstract class AbstractCommandParser<T> extends AbstractParser<T> {

    /**
     * Tries to parse the arguments
     * 
     * @param metadata
     *            Global Metadata
     * @param args
     *            Arguments
     * @return Parser State
     */
    protected ParseState<T> tryParse(GlobalMetadata<T> metadata, String... args) {
        return tryParse(metadata, AirlineUtils.unmodifiableListCopy(args));
    }

    /**
     * Tries to parse the arguments
     * 
     * @param metadata
     *            Global Metadata
     * @param args
     *            Arguments
     * @return Parser State
     */
    protected ParseState<T> tryParse(GlobalMetadata<T> metadata, Iterable<String> args) {
        PeekingIterator<String> tokens = new PeekingIterator<String>(args.iterator());

        //@formatter:off
        ParseState<T> state = ParseState.<T> newInstance()
                                        .pushContext(Context.GLOBAL)
                                        .withGlobal(metadata);
        //@formatter:on

        // Parse global options
        state = parseOptions(tokens, state, metadata.getOptions());

        // Apply aliases
        tokens = applyAliases(tokens, state);

        // Parse group
        state = parseGroup(tokens, state);

        // parse command
        state = parseCommand(tokens, state);

        return state;
    }

    protected PeekingIterator<String> applyAliases(PeekingIterator<String> tokens, ParseState<T> state) {
        AliasResolver<T> resolver = new AliasResolver<T>();
        return resolver.resolveAliases(tokens, state);
    }

    /**
     * Tries to parse the arguments
     * 
     * @param parserConfig
     *            Parser Configuration
     * @param command
     *            Command meta-data
     * @param args
     *            Arguments
     * @return Parser State
     */
    protected ParseState<T> tryParse(ParserMetadata<T> parserConfig, CommandMetadata command, Iterable<String> args) {
        PeekingIterator<String> tokens = new PeekingIterator<String>(args.iterator());
        //@formatter:off
        ParseState<T> state = ParseState.<T> newInstance()
                                        .pushContext(Context.GLOBAL)
                                        .withConfiguration(parserConfig)
                                        .withCommand(command)
                                        .pushContext(Context.COMMAND);
        //@formatter:off

        state = parseCommandOptionsAndArguments(tokens, state, command);
        return state;
    }

    protected ParseState<T> parseCommand(PeekingIterator<String> tokens, ParseState<T> state) {
        Predicate<CommandMetadata> findCommandPredicate;
        List<CommandMetadata> expectedCommands = state.getGlobal().getDefaultGroupCommands();
        if (state.getGroup() != null) {
            expectedCommands = state.getGroup().getCommands();
        }

        if (tokens.hasNext()) {
            //@formatter:off
            findCommandPredicate = state.getParserConfiguration().allowsAbbreviatedCommands() 
                                   ? new AbbreviatedCommandFinder(tokens.peek(), expectedCommands)
                                   : new CommandFinder(tokens.peek());
            //@formatter:on
            CommandMetadata command = AirlineUtils.find(expectedCommands, findCommandPredicate,
                    state.getGroup() != null ? state.getGroup().getDefaultCommand() : null);

            boolean usingDefault = false;
            if (command == null && state.getGroup() == null && state.getGlobal().getDefaultCommand() != null) {
                usingDefault = true;
                command = state.getGlobal().getDefaultCommand();
            }

            if (command == null) {
                while (tokens.hasNext()) {
                    state = state.withUnparsedInput(tokens.next());
                }
            } else {
                if (tokens.peek().equals(command.getName())
                        || (!usingDefault && state.getParserConfiguration().allowsAbbreviatedCommands())) {
                    tokens.next();
                }

                state = state.withCommand(command).pushContext(Context.COMMAND);

                state = parseCommandOptionsAndArguments(tokens, state, command);
            }
        }
        return state;
    }

    protected ParseState<T> parseCommandOptionsAndArguments(PeekingIterator<String> tokens, ParseState<T> state,
            CommandMetadata command) {
        while (tokens.hasNext()) {
            state = parseOptions(tokens, state, command.getCommandOptions());

            state = parseArgs(state, tokens, command.getArguments(), command.getDefaultOption());
        }
        return state;
    }

    protected ParseState<T> parseGroup(PeekingIterator<String> tokens, ParseState<T> state) {
        Predicate<CommandGroupMetadata> findGroupPredicate;
        if (tokens.hasNext()) {
            //@formatter:off
            findGroupPredicate = state.getParserConfiguration().allowsAbbreviatedCommands() 
                                 ? new AbbreviatedGroupFinder(tokens.peek(), state.getGlobal().getCommandGroups()) 
                                 : new GroupFinder(tokens.peek());
            //@formatter:on
            CommandGroupMetadata group = CollectionUtils.find(state.getGlobal().getCommandGroups(), findGroupPredicate);
            if (group != null) {
                tokens.next();
                state = state.withGroup(group).pushContext(Context.GROUP);
                state = parseOptions(tokens, state, state.getGroup().getOptions());

                // Possibly may have sub-groups specified
                while (tokens.hasNext() && state.getGroup().getSubGroups().size() > 0) {
                    //@formatter:off
                    findGroupPredicate = state.getParserConfiguration().allowsAbbreviatedCommands() 
                                         ? new AbbreviatedGroupFinder(tokens.peek(), state.getGroup().getSubGroups()) 
                                         : new GroupFinder(tokens.peek());
                    //@formatter:on
                    group = CollectionUtils.find(state.getGroup().getSubGroups(), findGroupPredicate);
                    if (group != null) {
                        tokens.next();
                        state = state.withGroup(group).pushContext(Context.GROUP);
                        state = parseOptions(tokens, state, state.getGroup().getOptions());
                    } else {
                        // Either a group that has a mixture of sub-groups and
                        // commands in which case we need to break out of this
                        // loop and flow will go onto to try the token as a
                        // command name instead or actually invalid input and
                        // we'll hit an error
                        break;
                    }
                }
            }
        }
        return state;
    }

    private ParseState<T> parseOptions(PeekingIterator<String> tokens, ParseState<T> state,
            List<OptionMetadata> allowedOptions) {

        // Get the option parsers in use
        List<OptionParser<T>> optionParsers = state.getParserConfiguration().getOptionParsers();

        while (tokens.hasNext()) {
            // Try to parse next option(s) using different styles. If code
            // matches it returns the next parser state, otherwise it returns
            // null.

            // Try each option parser in turn
            boolean matched = false;
            for (OptionParser<T> optionParser : optionParsers) {
                ParseState<T> nextState = optionParser.parseOptions(tokens, state, allowedOptions);

                if (nextState != null) {
                    // If the current parser matched an option this token is
                    // processed and we don't need to consider other parsers
                    state = nextState;
                    matched = true;
                    break;
                }
            }

            // Parsed an option so continue parsing options
            if (matched)
                continue;

            // Otherwise did not match an option so parse no further options
            break;
        }

        return state;
    }

    private ParseState<T> parseArgs(ParseState<T> state, PeekingIterator<String> tokens, ArgumentsMetadata arguments,
            OptionMetadata defaultOption) {
        String sep = state.getParserConfiguration().getArgumentsSeparator();

        if (tokens.hasNext()) {
            if (tokens.peek().equals(sep)) {
                state = state.pushContext(Context.ARGS);
                tokens.next();

                // Consume all remaining tokens as arguments
                // Default option can't possibly apply at this point because we
                // saw the arguments separator
                while (tokens.hasNext()) {
                    state = parseArg(state, tokens, arguments, null);
                }
            } else {
                state = parseArg(state, tokens, arguments, defaultOption);
            }
        }

        return state;
    }

    private ParseState<T> parseArg(ParseState<T> state, PeekingIterator<String> tokens, ArgumentsMetadata arguments,
            OptionMetadata defaultOption) {
        if (arguments != null) {
            // Argument
            state = state.withArgument(arguments, tokens.next());
        } else if (defaultOption != null) {
            // Default Option
            state = state.pushContext(Context.OPTION).withOption(defaultOption);
            state = state.withOptionValue(defaultOption, tokens.next()).popContext();
        } else {
            // Unparsed input
            state = state.withUnparsedInput(tokens.next());
        }
        return state;
    }
}
