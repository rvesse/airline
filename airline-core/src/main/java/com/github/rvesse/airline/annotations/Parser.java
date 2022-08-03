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
package com.github.rvesse.airline.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.github.rvesse.airline.CommandFactory;
import com.github.rvesse.airline.DefaultCommandFactory;
import com.github.rvesse.airline.SingleCommand;
import com.github.rvesse.airline.model.ParserMetadata;
import com.github.rvesse.airline.parser.errors.handlers.FailFast;
import com.github.rvesse.airline.parser.errors.handlers.ParserErrorHandler;
import com.github.rvesse.airline.parser.options.OptionParser;
import com.github.rvesse.airline.parser.resources.ResourceLocator;
import com.github.rvesse.airline.types.DefaultTypeConverter;
import com.github.rvesse.airline.types.TypeConverter;
import com.github.rvesse.airline.types.numerics.DefaultNumericConverter;
import com.github.rvesse.airline.types.numerics.NumericTypeConverter;

/**
 * Class annotation used to declaratively specify a parser configuration
 * <p>
 * When applied to a class that is also annotated with the {@link Command} annotation then if that class is used with
 * {@link SingleCommand#singleCommand(Class)} the parser configuration will automatically be detected from this
 * annotation.
 * </p>
 * <p>
 * When specifying a CLI via the {@link Cli} annotation then this annotation may be included as an argument to the
 * {@link Cli#parserConfiguration()} field to provide a parser configuration for the CLI.
 * </p>
 *
 * @author rvesse
 */
@Target(TYPE)
@Retention(RUNTIME)
@Documented
public @interface Parser {

    /**
     * Controls whether command names may be abbreviated provided such abbreviations are unambiguous (default false)
     *
     * @return True if command abbreviation allowed, false otherwise
     */
    boolean allowCommandAbbreviation() default false;

    /**
     * Controls whether options names may be abbreviated provided such abbreviations are unambiguous (default false)
     *
     * @return True if option abbreviation allowed, false otherwise
     */
    boolean allowOptionAbbreviation() default false;

    /**
     * Controls the separator that is used to distinguish options from arguments were arguments may be confused as
     * options
     * <p>
     * The default is {@code --} which is the widely used convention
     * </p>
     *
     * @return Arguments separator
     */
    String argumentsSeparator() default ParserMetadata.DEFAULT_ARGUMENTS_SEPARATOR;

    /**
     * Controls whether command alises may be chained i.e. can aliases be defined in terms of other aliases (default
     * false)
     * <p>
     * Note that even when enabled circular references are not permitted
     * </p>
     *
     * @return True if aliases may be chained, false otherwise
     */
    boolean aliasesMayChain() default false;

    /**
     * Controls whether aliases are allowed to override built-in commands i.e. if a command and an alias are defined
     * with the same name does the alias take precedence (default false)
     * <p>
     * This is particularly important if you allow users to define aliases since allowing overriding would allow them to
     * change the behaviour from the default expected
     * </p>
     *
     * @return True if aliases may override built-ins, false otherwise
     */
    boolean aliasesOverrideBuiltIns() default false;

    /**
     * Defines command aliases
     *
     * @return Command aliases
     */
    Alias[] aliases() default {};

    /**
     * Defines the name of a file from which user defined command aliases should be read
     *
     * @return User aliases filename
     */
    String userAliasesFile() default "";

    /**
     * Defines the search locations (i.e. directories) where the properties file containing the user defined aliases may
     * exist
     * <p>
     * These should be given in order of preference, properties from all locations will be merged together such that
     * properties from the locations earlier in this list take precedence
     * </p>
     * <p>
     * Search locations may start with {@code ~/} or {@code ~\} (depending on the target platform) to refer to the home
     * directory
     * </p>
     *
     * @return Search locations for alises
     */
    String[] userAliasesSearchLocation() default "";

    /**
     * Sets the prefix used for properties that define aliases
     * <p>
     * This is useful if you use the same properties file to store general properties for your application in the same
     * properties file. If set only properties whose names begin with these prefix are treated as alias definition with
     * the prefix being stripped. So for example if you have a prefix of {@code foo.} and defined a property
     * {@code foo.bar} then you would be defining an alias {@code bar}
     * </p>
     *
     * @return User defined aliases prefix
     */
    String userAliasesPrefix() default "";

    /**
     * Sets a character used in alias definitions to indicate that the built-in should be called regardless of the
     * setting of {@link #aliasesOverrideBuiltIns()}
     * <p>
     * This is useful since it allows users to redefine built-ins with their desired default arguments while still
     * allowing the alias to call the original built-in. This defaults to {@code !}
     * </p>
     *
     * @return Aliases force built-in prefix character
     */
    char aliasesForceBuiltInPrefix() default '!';

    /**
     * Sets whether to use the default user alias locators (default true)
     *
     * @return True if defaults are used, false otherwise
     */
    boolean useDefaultAliasLocators() default true;

    /**
     * Sets whether to use the default alias locators first before any additional alias locators that may be defined
     * (default false)
     *
     * @return True if defaults are used first, false otherwise
     */
    boolean defaultAliasLocatorsFirst() default false;

    /**
     * Sets the user alias locator classes to be used
     *
     * @return User alias locator classes
     */
    Class<? extends ResourceLocator>[] userAliasLocators() default {};

    /**
     * Sets whether to use the default set of option parsers (default true)
     *
     * @return True if default option parsers are used, false otherwise
     */
    boolean useDefaultOptionParsers() default true;

    /**
     * Sets whether to use the default option parsers first before any additional option parsers that may be defined
     * (default true)
     *
     * @return True if default parsers are used first, false otherwise
     */
    boolean defaultParsersFirst() default true;

    /**
     * Sets the option parser classes to be used
     *
     * @return Option parser classes
     */
    @SuppressWarnings("rawtypes")
    Class<? extends OptionParser>[] optionParsers() default {};

    /**
     * Sets the command factory class to use
     *
     * @return Command factory class
     */
    @SuppressWarnings("rawtypes")
    Class<? extends CommandFactory> commandFactory() default DefaultCommandFactory.class;

    /**
     * Sets the composition annotation classes to use.
     * <p>
     * This is set by providing the fully qualified names of the classes, rather than class references themselves, this
     * allows Airline to dynamically load the annotation at the point where it collects metadata for classes.  Thus
     * allowing the actual annotation types used to themselves be injected at runtime rather than compile time.
     * </p>
     * <p>
     * See {@link com.github.rvesse.airline.model.MetadataLoader#loadInjectionMetadata(Class, ParserMetadata)} for more
     * detailed description of how this configuration is used.
     * </p>
     * <p>
     * This configuration point was introduced in <strong>2.9.0</strong> as part of the introduction of the
     * {@link AirlineModule} annotation to provide for backwards compatibility and for users to better integrate with
     * their chosen dependency injection framework.
     * </p>
     *
     * @return Composition annotation classes
     * @since 2.9.0
     */
    String[] compositionAnnotationClasses() default {};

    /**
     * Sets the type converter class to use
     *
     * @return Type converter class
     */
    Class<? extends TypeConverter> typeConverter() default DefaultTypeConverter.class;

    /**
     * Sets the numeric type converter to use, this is used in conjunction with the value of the
     * {@link #typeConverter()}, if that class does not respect {@link NumericTypeConverter} instances then this field
     * has no effect
     *
     * @return Numeric type converter class
     */
    Class<? extends NumericTypeConverter> numericTypeConverter() default DefaultNumericConverter.class;

    /**
     * Sets the error handler to use, defaults to {@code FailFast} which throws errors as soon as they are encountered
     *
     * @return Error handler to use
     */
    Class<? extends ParserErrorHandler> errorHandler() default FailFast.class;

    /**
     * Sets the flag negation prefix
     * <p>
     * If set flag options (those with arity zero) will have their value set to {@code false} if the name used starts
     * with this prefix. For example if the prefix is set to {@code --no-} and the user specifies a flag that begins
     * with this the option will be set to {@code false}. Note that an appropriate name must be present in the
     * {@link Option#name()} for the flag option which you wish to allow to be negated.
     * </p>
     *
     * @return Flag negation prefix
     */
    String flagNegationPrefix() default "";
}
