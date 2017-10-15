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
package com.github.rvesse.airline.model;

import java.util.List;

import com.github.rvesse.airline.utils.StringUtils;

import com.github.rvesse.airline.CommandFactory;
import com.github.rvesse.airline.DefaultCommandFactory;
import com.github.rvesse.airline.parser.aliases.UserAliasesSource;
import com.github.rvesse.airline.parser.errors.handlers.FailFast;
import com.github.rvesse.airline.parser.errors.handlers.ParserErrorHandler;
import com.github.rvesse.airline.parser.options.OptionParser;
import com.github.rvesse.airline.types.DefaultTypeConverter;
import com.github.rvesse.airline.types.TypeConverter;
import com.github.rvesse.airline.utils.AirlineUtils;

/**
 * Represents meta-data about the parser configuration
 */
public class ParserMetadata<T> {

    /**
     * Default separator used to separate arguments from options
     */
    public static final String DEFAULT_ARGUMENTS_SEPARATOR = "--";

    private final boolean allowAbbreviatedCommands, allowAbbreviatedOptions, aliasesOverrideBuiltIns, aliasesMayChain;
    private final List<OptionParser<T>> optionParsers;
    private final List<AliasMetadata> aliases;
    private final UserAliasesSource<T> userAliases;
    private final TypeConverter typeConverter;
    private final CommandFactory<T> commandFactory;
    private final String argsSeparator, flagNegationPrefix;
    private final ParserErrorHandler errorHandler;

    public ParserMetadata(CommandFactory<T> commandFactory, List<OptionParser<T>> optionParsers,
            TypeConverter typeConverter, ParserErrorHandler errorHandler, boolean allowAbbreviateCommands,
            boolean allowAbbreviatedOptions, List<AliasMetadata> aliases, UserAliasesSource<T> userAliases,
            boolean aliasesOverrideBuiltIns, boolean aliasesMayChain, String argumentsSeparator,
            String flagNegationPrefix) {
        if (optionParsers == null)
            throw new NullPointerException("optionParsers cannot be null");
        if (aliases == null)
            throw new NullPointerException("aliases cannot be null");

        // Error handling
        this.errorHandler = errorHandler != null ? errorHandler : new FailFast();

        // Command parsing
        this.commandFactory = commandFactory != null ? commandFactory : new DefaultCommandFactory<T>();
        this.allowAbbreviatedCommands = allowAbbreviateCommands;

        // Option Parsing
        this.typeConverter = typeConverter != null ? typeConverter : new DefaultTypeConverter();
        this.optionParsers = AirlineUtils.unmodifiableListCopy(optionParsers);
        this.allowAbbreviatedOptions = allowAbbreviatedOptions;

        // Aliases
        this.aliases = AirlineUtils.unmodifiableListCopy(aliases);
        this.userAliases = userAliases;
        this.aliasesOverrideBuiltIns = aliasesOverrideBuiltIns;
        this.aliasesMayChain = aliasesMayChain;

        // Arguments Separator
        if (StringUtils.isNotEmpty(argumentsSeparator)) {
            if (StringUtils.containsWhitespace(argumentsSeparator))
                throw new IllegalArgumentException("argumentsSeparator cannot contain any whitespace");
        }
        this.argsSeparator = StringUtils.isNotEmpty(argumentsSeparator) ? argumentsSeparator
                : DEFAULT_ARGUMENTS_SEPARATOR;

        // Flag negation
        this.flagNegationPrefix = StringUtils.isNotEmpty(flagNegationPrefix) ? flagNegationPrefix : null;

    }

    /**
     * Gets the command factory to use
     * 
     * @return Command factory
     */
    public CommandFactory<T> getCommandFactory() {
        return commandFactory;
    }

    /**
     * Gets the type converter to use
     * 
     * @return Type converter
     */
    public TypeConverter getTypeConverter() {
        return typeConverter;
    }

    /**
     * Gets the error handler to use
     * 
     * @return Error handler
     */
    public ParserErrorHandler getErrorHandler() {
        return errorHandler;
    }

    /**
     * Gets the defined command aliases
     * 
     * @return Aliases
     */
    public List<AliasMetadata> getAliases() {
        return aliases;
    }

    /**
     * Gets the user aliases source (if any)
     * 
     * @return User aliases source
     */
    public UserAliasesSource<T> getUserAliasesSource() {
        return userAliases;
    }

    /**
     * Gets whether aliases can override built-in commands
     * 
     * @return True if they can override, false otherwise
     */
    public boolean aliasesOverrideBuiltIns() {
        return aliasesOverrideBuiltIns;
    }

    /**
     * Gets whether aliases may chain i.e. whether one alias may reference
     * another
     * 
     * @return True if they can chain, false otherwise
     */
    public boolean aliasesMayChain() {
        return aliasesMayChain;
    }

    /**
     * Gets the option parsers to use
     * 
     * @return Option parsers
     */
    public List<OptionParser<T>> getOptionParsers() {
        return optionParsers;
    }

    /**
     * Gets whether command/group name abbreviation is allowed
     * 
     * @return True if allowed, false otherwise
     */
    public boolean allowsAbbreviatedCommands() {
        return allowAbbreviatedCommands;
    }

    /**
     * Gets whether option name abbreviation is allowed
     * 
     * @return True if allowed, false otherwise
     */
    public boolean allowsAbbreviatedOptions() {
        return allowAbbreviatedOptions;
    }

    /**
     * Gets the arguments separator to be used
     * 
     * @return Arguments separator
     */
    public String getArgumentsSeparator() {
        return this.argsSeparator;
    }

    /**
     * Gets whether this configuration allows flag negation
     * 
     * @return True if negation is allowed, false otherwise
     */
    public boolean allowsFlagNegation() {
        return StringUtils.isNotEmpty(this.flagNegationPrefix);
    }

    /**
     * Gets the flag negation prefix that is in use (if any)
     * 
     * @return Flag negation prefix, may be {@code null} if not enabled
     */
    public String getFlagNegationPrefix() {
        return this.flagNegationPrefix;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ParserMetadata {");
        sb.append("commandFactory=").append(commandFactory.getClass().getCanonicalName());
        sb.append(", allowAbbreviatedCommands=").append(allowAbbreviatedCommands);
        sb.append(", optionParsers=").append(optionParsers);
        sb.append(", typeConverter=").append(typeConverter.getClass().getCanonicalName());
        sb.append(", allowAbbreviatedOptions=").append(allowAbbreviatedOptions);
        sb.append(", aliases=").append(aliases);
        sb.append(", aliasesOverrideBuiltIns=").append(aliasesOverrideBuiltIns);
        sb.append(", argumentsSeparator='").append(argsSeparator).append("'");
        sb.append(", flagNegationPrefix='").append(flagNegationPrefix).append("'");
        sb.append("}");
        return sb.toString();
    }
}
