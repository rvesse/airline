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
package com.github.rvesse.airline.builder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.github.rvesse.airline.CommandFactory;
import com.github.rvesse.airline.DefaultCommandFactory;
import com.github.rvesse.airline.model.AliasMetadata;
import com.github.rvesse.airline.model.ParserMetadata;
import com.github.rvesse.airline.parser.aliases.UserAliasesSource;
import com.github.rvesse.airline.parser.errors.handlers.ParserErrorHandler;
import com.github.rvesse.airline.parser.options.ClassicGetOptParser;
import com.github.rvesse.airline.parser.options.LongGetOptParser;
import com.github.rvesse.airline.parser.options.OptionParser;
import com.github.rvesse.airline.parser.options.StandardOptionParser;
import com.github.rvesse.airline.parser.resources.ResourceLocator;
import com.github.rvesse.airline.types.DefaultTypeConverter;
import com.github.rvesse.airline.types.TypeConverter;
import com.github.rvesse.airline.types.numerics.DefaultNumericConverter;
import com.github.rvesse.airline.types.numerics.NumericTypeConverter;

/**
 * Builder for parser configurations
 *
 * @param <C>
 *            Command type
 */
public class ParserBuilder<C> extends AbstractBuilder<ParserMetadata<C>> {

    protected TypeConverter typeConverter = new DefaultTypeConverter();
    protected NumericTypeConverter numericTypeConverter = new DefaultNumericConverter();
    protected final Map<String, AliasBuilder<C>> aliases = new HashMap<>();
    protected CommandFactory<C> commandFactory = new DefaultCommandFactory<C>();
    protected boolean allowAbbreviatedCommands, allowAbbreviatedOptions, aliasesOverrideBuiltIns, aliasesMayChain;
    private char forceBuiltInPrefix = '!';
    protected final List<OptionParser<C>> optionParsers = new ArrayList<>();
    protected String argsSeparator, flagNegationPrefix;
    protected UserAliasSourceBuilder<C> userAliasesBuilder = new UserAliasSourceBuilder<>(this);
    protected ParserErrorHandler errorHandler;

    /**
     * Gets the default configuration
     * 
     * @param <T>
     *            Command type to parse
     * @return Default configuraiton
     */
    public static <T> ParserMetadata<T> defaultConfiguration() {
        return new ParserBuilder<T>().build();
    }

    /**
     * Specifies the command factory to use
     * 
     * @param commandFactory
     *            Command Factory
     * @return Builder
     */
    public ParserBuilder<C> withCommandFactory(CommandFactory<C> commandFactory) {
        this.commandFactory = commandFactory;
        return this;
    }

    /**
     * Specifies that the default command factory should be used
     * 
     * @return Builder
     */
    public ParserBuilder<C> withDefaultCommandFactory() {
        this.commandFactory = null;
        return this;
    }

    /**
     * Adds an alias
     * 
     * @param name
     *            Alias name
     * @return Alias Builder
     */
    public AliasBuilder<C> withAlias(final String name) {
        checkNotBlank(name, "Alias name");

        if (aliases.containsKey(name)) {
            return aliases.get(name);
        }

        AliasBuilder<C> alias = new AliasBuilder<C>(this, name);
        aliases.put(name, alias);
        return alias;
    }

    /**
     * Retrieves an alias builder for the given alias
     * 
     * @param name
     *            Alias name
     * @return Alias Builder
     */
    public AliasBuilder<C> getAlias(final String name) {
        checkNotBlank(name, "Alias name");
        if (!aliases.containsKey(name))
            throw new IllegalArgumentException(String.format("Alias %s has not been declared", name));

        return aliases.get(name);
    }

    /**
     * Sets a prefix character used in alias definitions to force use of a
     * built-in as opposed to a chained alias
     * 
     * @param prefix
     *            Prefic character
     * @return Parser build
     */
    public ParserBuilder<C> withAliasForceBuiltInPrefix(char prefix) {
        this.forceBuiltInPrefix = prefix;
        return this;
    }

    /**
     * Gets a builder that provides detailed control over building user aliases
     * 
     * @return User aliases builder
     */
    public UserAliasSourceBuilder<C> withUserAliases() {
        return this.userAliasesBuilder;
    }

    /**
     * Reads in user aliases from the default configuration file in the default
     * location.
     * <p>
     * The default configuration file name is constructed by appending the
     * {@code .config} extension to the defined program name
     * </p>
     * <p>
     * The default search location is a {@code .program} directory under the
     * users home directory where {@code program} is the defined program name.
     * </p>
     * <p>
     * If you prefer to control these values explicitly and for more detail on
     * the configuration format please see the
     * {@link #withUserAliases(String, String, String...)} method
     * </p>
     * 
     * @param programName
     *            Program Name
     * @return Builder
     * @deprecated Use {@link #withUserAliases()} to access the user alias
     *             builder directly instead
     */
    @Deprecated
    public ParserBuilder<C> withUserAliases(String programName) {
        // Use default filename and search location
        this.userAliasesBuilder.withProgramName(programName);
        this.userAliasesBuilder.withDefaultSearchLocation(programName);
        return this;
    }

    /**
     * Reads in user aliases from the default configuration file in the default
     * location
     * <p>
     * The default configuration file name is constructed by appending the
     * {@code .config} extension to the defined program name
     * </p>
     * <p>
     * If you prefer to control this value explicitly and for more detail on the
     * configuration format please see the
     * {@link #withUserAliases(String, String, String...)} method
     * </p>
     * 
     * @param programName
     *            Program name
     * @param searchLocation
     *            Location to search
     * 
     * @return Builder
     * @deprecated Use {@link #withUserAliases()} to access the user alias
     *             builder directly instead
     */
    @Deprecated
    public ParserBuilder<C> withUserAliases(String programName, String searchLocation) {
        // Use default filename
        this.userAliasesBuilder.withProgramName(programName);
        this.userAliasesBuilder.withSearchLocation(searchLocation);
        return this;
    }

    /**
     * Reads in user aliases from the defined configuration file in the provided
     * search locations
     * <p>
     * This file is in standard Java properties format with the key being the
     * alias and the value being the arguments for this alias. Arguments are
     * whitespace separated though quotes ({@code "}) may be used to wrap
     * arguments that need to contain whitespace. Quotes may be escaped within
     * quoted arguments and whitespace may be escaped within unquoted arguments.
     * Note that since Java property values are interpreted as Java strings it
     * is necessary to double escape the backslash i.e. {@code \\"} for this to
     * work properly.
     * </p>
     * 
     * <pre>
     * example=command --option value
     * quoted=command "long argument"
     * escaped=command whitespace\\ escape "quote\\"escape"
     * </pre>
     * <p>
     * The search locations should be given in order of preference, the file
     * will be loaded from all search locations in which it exists such that
     * values from the locations occurring first in the search locations list
     * take precedence. This allows for having multiple locations for your
     * configuration file and layering different sets of aliases over each other
     * e.g. system, user and local aliases.
     * </p>
     * <p>
     * The {@code prefix} is used to filter properties from the properties file
     * such that you can include aliases with other configuration settings in
     * your configuration files. When a prefix is used only properties that
     * start with the prefix are interpreted as alias definitions and the actual
     * alias is the property name with the prefix removed. For example if your
     * prefix was {@code alias.} and you had a property {@code alias.foo} the
     * resulting alias would be {@code foo}.
     * </p>
     * <h3>Notes</h3>
     * <ul>
     * <li>Recursive aliases are only supported if
     * {@link #withAliasesChaining()}} is specified and will result in errors
     * when used otherwise. Even when recursive aliases are enabled aliases
     * cannot use circular references.</li>
     * <li>Aliases cannot override built-ins unless you have called
     * {@link #withAliasesOverridingBuiltIns()} on your builder</li>
     * </ul>
     * 
     * @param filename
     *            Filename to look for
     * @param prefix
     *            Prefix used to distinguish alias related properties from other
     *            properties
     * @param searchLocations
     *            Search locations in order of preference
     * 
     * @return Builder
     * @deprecated Use {@link #withUserAliases()} to access the user alias
     *             builder directly instead
     */
    @Deprecated
    public ParserBuilder<C> withUserAliases(final String filename, final String prefix,
            final String... searchLocations) {
        this.userAliasesBuilder.withFilename(filename);
        this.userAliasesBuilder.withPrefix(prefix);
        this.userAliasesBuilder.withSearchLocations(searchLocations);
        return this;
    }

    /**
     * Reads in user aliases from the defined configuration file in the provided
     * search location
     * <p>
     * This file is in standard Java properties format with the key being the
     * alias and the value being the arguments for this alias. Arguments are
     * whitespace separated though quotes ({@code "}) may be used to wrap
     * arguments that need to contain whitespace. Quotes may be escaped within
     * quoted arguments and whitespace may be escaped within unquoted arguments.
     * Note that since Java property values are interpreted as Java strings it
     * is necessary to double escape the backslash i.e. {@code \\"} for this to
     * work properly.
     * </p>
     * 
     * <pre>
     * example=command --option value
     * quoted=command "long argument"
     * escaped=command whitespace\\ escape "quote\\"escape"
     * </pre>
     * <p>
     * The search locations should be given in order of preference, the file
     * will be loaded from all search locations in which it exists such that
     * values from the locations occurring first in the search locations list
     * take precedence. This allows for having multiple locations for your
     * configuration file and layering different sets of aliases over each other
     * e.g. system, user and local aliases.
     * </p>
     * <p>
     * The {@code prefix} is used to filter properties from the properties file
     * such that you can include aliases with other configuration settings in
     * your configuration files. When a prefix is used only properties that
     * start with the prefix are interpreted as alias definitions and the actual
     * alias is the property name with the prefix removed. For example if your
     * prefix was {@code alias.} and you had a property {@code alias.foo} the
     * resulting alias would be {@code foo}.
     * </p>
     * <h3>Notes</h3>
     * <ul>
     * <li>Recursive aliases are only supported if
     * {@link #withAliasesChaining()}} is specified and will result in errors
     * when used otherwise. Even when recursive aliases are enabled aliases
     * cannot use circular references.</li>
     * <li>Aliases cannot override built-ins unless you have called
     * {@link #withAliasesOverridingBuiltIns()} on your builder</li>
     * </ul>
     * 
     * @param filename
     *            Filename to look for
     * @param prefix
     *            Prefix used to distinguish alias related properties from other
     *            properties
     * @param locators
     *            Locators used to resolve search locations to actual locations,
     *            this is what enables things like {@code ~/} to be used to
     *            refer to the users home directory. If {@code null} then a
     *            default set are used.
     * @param searchLocations
     *            Search locations in order of preference
     * 
     * @return Builder
     * @deprecated Use {@link #withUserAliases()} to access the user alias
     *             builder directly instead
     */
    @Deprecated
    public ParserBuilder<C> withUserAliases(final String filename, final String prefix,
            final List<ResourceLocator> locators, final String... searchLocations) {
        this.userAliasesBuilder.withFilename(filename);
        this.userAliasesBuilder.withPrefix(prefix);
        this.userAliasesBuilder.withSearchLocations(searchLocations);
        this.userAliasesBuilder.withLocators(locators);
        return this;
    }

    /**
     * Sets that aliases should override built-in commands
     * 
     * @return Builder
     */
    public ParserBuilder<C> withAliasesOverridingBuiltIns() {
        this.aliasesOverrideBuiltIns = true;
        return this;
    }

    /**
     * Sets that aliases may be defined in terms of other aliases
     * 
     * @return Builder
     */
    public ParserBuilder<C> withAliasesChaining() {
        this.aliasesMayChain = true;
        return this;
    }

    /**
     * Sets that command abbreviation is enabled
     * 
     * @return Builder
     */
    public ParserBuilder<C> withCommandAbbreviation() {
        this.allowAbbreviatedCommands = true;
        return this;
    }

    /**
     * Sets that option abbreviation is enabled
     * 
     * @return Builder
     */
    public ParserBuilder<C> withOptionAbbreviation() {
        this.allowAbbreviatedOptions = true;
        return this;
    }

    /**
     * Sets the type converter for the parser
     * 
     * @param converter
     *            Type converter
     * @return Builder
     */
    public ParserBuilder<C> withTypeConverter(TypeConverter converter) {
        this.typeConverter = converter;
        return this;
    }

    /**
     * Sets that the default type converter should be used
     * 
     * @return Builder
     */
    public ParserBuilder<C> withDefaultTypeConverter() {
        this.typeConverter = null;
        return this;
    }

    /**
     * Indicates the desired numeric type converter to use, this is passed as an
     * argument to the given type converter
     * 
     * @param converter
     *            Numeric type converter
     * @return Builder
     */
    public ParserBuilder<C> withNumericTypeConverter(NumericTypeConverter converter) {
        this.numericTypeConverter = converter;
        return this;
    }

    /**
     * Indicates that default numeric type conversion should be used
     * 
     * @return Builder
     */
    public ParserBuilder<C> withDefaultNumericTypeConverter() {
        this.numericTypeConverter = null;
        return this;
    }

    /**
     * Sets the error handler to use
     * 
     * @param errorHandler
     *            Error handler
     * @return Builder
     */
    public ParserBuilder<C> withErrorHandler(ParserErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
        return this;
    }

    /**
     * Sets that the default error handler should be used
     * 
     * @return Builder
     */
    public ParserBuilder<C> withDefaultErrorHandler() {
        this.errorHandler = null;
        return this;
    }

    /**
     * Configures the CLI to use the given option parser
     * <p>
     * Order of registration is important, if you have previously registered any
     * parsers then those will be used prior to the one given here
     * </p>
     * 
     * @param optionParser
     *            Option parser
     * @return Builder
     */
    public ParserBuilder<C> withOptionParser(OptionParser<C> optionParser) {
        if (optionParser != null) {
            this.optionParsers.add(optionParser);
        }
        return this;
    }

    /**
     * Configures the CLI to use the given option parsers
     * <p>
     * Order of registration is important, if you have previously registered any
     * parsers then those will be used prior to those given here
     * </p>
     * 
     * @param optionParsers
     *            Option parsers
     * @return Builder
     */
    @SuppressWarnings("unchecked")
    public ParserBuilder<C> withOptionParsers(OptionParser<C>... optionParsers) {
        if (optionParsers != null) {
            for (OptionParser<C> parser : optionParsers) {
                if (parser != null) {
                    this.optionParsers.add(parser);
                }
            }
        }
        return this;
    }

    /**
     * Configures the CLI to use only the default set of option parsers
     * <p>
     * This is the default behaviour so this need only be called if you have
     * previously configured some option parsers using the
     * {@link #withOptionParser(OptionParser)} or
     * {@link #withOptionParsers(OptionParser...)} methods and wish to reset the
     * configuration to the default.
     * </p>
     * <p>
     * If you wish to instead add the default parsers in addition to your custom
     * parsers you should instead call {@link #withDefaultOptionParsers()}
     * </p>
     * 
     * @return Builder
     */
    public ParserBuilder<C> withOnlyDefaultOptionParsers() {
        this.optionParsers.clear();
        return this.withDefaultOptionParsers();
    }

    /**
     * Configures the CLI to use the default set of option parsers in addition
     * to any previously registered
     * <p>
     * Order of registration is important, if you have previously registered any
     * parsers then those will be used prior to those in the default set.
     * </p>
     * 
     * @return Builder
     */
    @SuppressWarnings("unchecked")
    public ParserBuilder<C> withDefaultOptionParsers() {
        return this.withOptionParsers(new StandardOptionParser<C>(), new LongGetOptParser<C>(),
                new ClassicGetOptParser<C>());
    }

    /**
     * Sets the arguments separator, this is a token used to indicate the point
     * at which no further options will be seen and all further tokens should be
     * treated as arguments.
     * <p>
     * This is useful for disambiguating where arguments may be misinterpreted
     * as options. The default value of this is the standard {@code --} used by
     * many command line tools.
     * </p>
     * 
     * @param separator
     *            Arguments separator
     * @return Builder
     */
    public ParserBuilder<C> withArgumentsSeparator(String separator) {
        this.argsSeparator = separator;
        return this;
    }

    /**
     * Sets the flag negation prefix, this is used to determine whether to set a
     * flag option (a zero arity option) to {@code false} rather than the usual
     * behaviour of setting it to {@code true}. Options must have appropriately
     * prefixed names defined for this prefix to have any effect i.e. setting it
     * does not automatically enable negation for flag options.
     * 
     * @param prefix
     *            Flag negation prefix
     * @return Builder
     */
    public ParserBuilder<C> withFlagNegationPrefix(String prefix) {
        this.flagNegationPrefix = prefix;
        return this;
    }

    @Override
    public ParserMetadata<C> build() {
        // Ensure we have some option parsers if none configured
        if (this.optionParsers.size() == 0) {
            this.withDefaultOptionParsers();
        }

        // Load user aliases
        // These may override explicitly defined aliases
        UserAliasesSource<C> userAliases = null;
        if (this.userAliasesBuilder.isBuildable()) {
            try {
                userAliases = this.userAliasesBuilder.build();
                for (AliasMetadata alias : userAliases.load()) {
                    aliases.put(alias.getName(), new AliasBuilder<C>(this, alias.getName())
                            .withArguments(alias.getArguments().toArray(new String[alias.getArguments().size()])));
                }
            } catch (IOException e) {
                throw new IllegalStateException("Failed to load user aliases", e);
            }
        }

        // Build aliases
        List<AliasMetadata> aliasData;
        if (aliases != null) {
            aliasData = new ArrayList<AliasMetadata>();
            for (AliasBuilder<C> aliasBuilder : aliases.values()) {
                aliasData.add(aliasBuilder.build());
            }
        } else {
            aliasData = new ArrayList<>();
        }

        if (typeConverter == null) {
            typeConverter = new DefaultTypeConverter();
        }
        typeConverter.setNumericConverter(this.numericTypeConverter);

        return new ParserMetadata<C>(commandFactory, optionParsers, typeConverter, errorHandler,
                allowAbbreviatedCommands, allowAbbreviatedOptions, aliasData, userAliases, aliasesOverrideBuiltIns,
                aliasesMayChain, forceBuiltInPrefix, argsSeparator, flagNegationPrefix);
    }
}