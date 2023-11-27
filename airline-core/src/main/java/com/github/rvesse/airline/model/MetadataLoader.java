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

import com.github.rvesse.airline.Accessor;
import com.github.rvesse.airline.DefaultCommandFactory;
import com.github.rvesse.airline.annotations.*;
import com.github.rvesse.airline.annotations.restrictions.Partial;
import com.github.rvesse.airline.annotations.restrictions.Partials;
import com.github.rvesse.airline.builder.ParserBuilder;
import com.github.rvesse.airline.builder.UserAliasSourceBuilder;
import com.github.rvesse.airline.help.sections.HelpSection;
import com.github.rvesse.airline.help.sections.factories.HelpSectionRegistry;
import com.github.rvesse.airline.help.suggester.Suggester;
import com.github.rvesse.airline.parser.ParserUtil;
import com.github.rvesse.airline.parser.errors.handlers.FailFast;
import com.github.rvesse.airline.parser.options.OptionParser;
import com.github.rvesse.airline.parser.resources.ResourceLocator;
import com.github.rvesse.airline.restrictions.ArgumentsRestriction;
import com.github.rvesse.airline.restrictions.GlobalRestriction;
import com.github.rvesse.airline.restrictions.OptionRestriction;
import com.github.rvesse.airline.restrictions.common.PartialRestriction;
import com.github.rvesse.airline.restrictions.factories.RestrictionRegistry;
import com.github.rvesse.airline.types.DefaultTypeConverter;
import com.github.rvesse.airline.types.TypeConverterProvider;
import com.github.rvesse.airline.types.numerics.DefaultNumericConverter;
import com.github.rvesse.airline.utils.AirlineUtils;
import com.github.rvesse.airline.utils.comparators.StringHierarchyComparator;
import com.github.rvesse.airline.utils.predicates.parser.CommandTypeFinder;
import com.github.rvesse.airline.utils.predicates.parser.GroupFinder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Helper for loading meta-data
 */
public class MetadataLoader {

    /**
     * Constant for the {@link AirlineModule} annotation class
     */
    public static final String AIRLINE_MODULE = "com.github.rvesse.airline.annotations.AirlineModule";

    /**
     * Constant for the {@code javax.inject.Inject} annotation class
     */
    public static final String JAVAX_INJECT_INJECT = "javax.inject.Inject";
    /**
     * Constant for the {@code jakarta.inject.Inject} annotation class
     */
    public static final String JAKARTA_INJECT_INJECT = "jakarta.inject.Inject";
    /**
     * Constant for the {@code com.google.inject.Inject} annotation class
     */
    public static final String COM_GOOGLE_INJECT_INJECT = "com.google.inject.Inject";
    private static Map<String, Class<? extends Annotation>> dynamicAnnotationCache = new HashMap<>();

    public static <C> ParserMetadata<C> loadParser(Class<?> cliClass) {
        if (cliClass == null) {
            return ParserBuilder.<C>defaultConfiguration();
        }

        Annotation annotation = cliClass.getAnnotation(Parser.class);
        if (annotation == null) {
            return ParserBuilder.<C>defaultConfiguration();
        }

        return loadParser((Parser) annotation);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static <C> ParserMetadata<C> loadParser(Parser parserConfig) {
        ParserBuilder<C> builder = new ParserBuilder<C>();

        // Factory and converter options
        if (!parserConfig.typeConverter().equals(DefaultTypeConverter.class)) {
            builder = builder.withTypeConverter(ParserUtil.createInstance(parserConfig.typeConverter()));
        } else {
            builder = builder.withDefaultTypeConverter();
        }
        if (!parserConfig.numericTypeConverter().equals(DefaultNumericConverter.class)) {
            builder = builder.withNumericTypeConverter(ParserUtil.createInstance(parserConfig.numericTypeConverter()));
        } else {
            builder = builder.withDefaultNumericTypeConverter();
        }
        if (!parserConfig.commandFactory().equals(DefaultCommandFactory.class)) {
            builder = builder.withCommandFactory(ParserUtil.createInstance(parserConfig.commandFactory()));
        } else {
            builder = builder.withDefaultCommandFactory();
        }
        if (parserConfig.compositionAnnotationClasses().length > 0) {
            builder = builder.withCompositionAnnotations(parserConfig.compositionAnnotationClasses());
        } else {
            builder = builder.withDefaultCompositionAnnotations();
        }
        if (!parserConfig.errorHandler().equals(FailFast.class)) {
            builder = builder.withErrorHandler(ParserUtil.createInstance(parserConfig.errorHandler()));
        } else {
            builder = builder.withDefaultErrorHandler();
        }

        // Abbreviation options
        if (parserConfig.allowCommandAbbreviation()) {
            builder = builder.withCommandAbbreviation();
        }
        if (parserConfig.allowOptionAbbreviation()) {
            builder = builder.withOptionAbbreviation();
        }

        // Alias options
        if (parserConfig.aliasesOverrideBuiltIns()) {
            builder = builder.withAliasesOverridingBuiltIns();
        }
        if (parserConfig.aliasesMayChain()) {
            builder = builder.withAliasesChaining();
        }
        builder = builder.withAliasForceBuiltInPrefix(parserConfig.aliasesForceBuiltInPrefix());
        for (Alias alias : parserConfig.aliases()) {
            builder.withAlias(alias.name()).withArguments(alias.arguments());
        }
        if (!StringUtils.isEmpty(parserConfig.userAliasesFile())) {
            UserAliasSourceBuilder<C> userAliasBuilder = builder.withUserAliases();
            userAliasBuilder.withFilename(parserConfig.userAliasesFile());
            userAliasBuilder.withPrefix(parserConfig.userAliasesPrefix());

            // Determine the search locations that are in use
            if (parserConfig.userAliasesSearchLocation().length > 0) {
                userAliasBuilder.withSearchLocations(parserConfig.userAliasesSearchLocation());
            } else {
                // Use the working directory as no search location specified
                userAliasBuilder.withSearchLocation("." + File.separator);
            }

            // Determine the locators that are in use
            if (parserConfig.useDefaultAliasLocators() && parserConfig.defaultAliasLocatorsFirst()) {
                userAliasBuilder.withDefaultLocators();
            }
            for (ResourceLocator locator : ParserUtil.createResourceLocators(parserConfig.userAliasLocators())) {
                userAliasBuilder.withLocator(locator);
            }
            if (parserConfig.useDefaultAliasLocators() && !parserConfig.defaultAliasLocatorsFirst()) {
                userAliasBuilder.withDefaultLocators();
            }
        }

        // Parsing options
        builder.withArgumentsSeparator(parserConfig.argumentsSeparator());
        builder.withFlagNegationPrefix(parserConfig.flagNegationPrefix());
        if (parserConfig.defaultParsersFirst() && parserConfig.useDefaultOptionParsers()) {
            builder = builder.withDefaultOptionParsers();
        }
        for (Class<? extends OptionParser> optionParserClass : parserConfig.optionParsers()) {
            OptionParser<C> optionParser = ParserUtil.createInstance(optionParserClass);
            builder = builder.withOptionParser(optionParser);
        }
        if (!parserConfig.defaultParsersFirst() && parserConfig.useDefaultOptionParsers()) {
            builder = builder.withDefaultOptionParsers();
        }

        return builder.build();
    }

    public static <C> GlobalMetadata<C> loadGlobal(Class<?> cliClass) {
        return loadGlobal(cliClass, null);
    }

    /**
     * Loads the metadata for a CLI
     *
     * @param cliClass             Class that has the {@link com.github.rvesse.airline.annotations.Cli} annotation
     * @param parserConfigOverride Optional parser configuration, note that the
     *                             {@link com.github.rvesse.airline.annotations.Cli#parserConfiguration()} field is
     *                             normally used to provide a parser configuration via annotation but in some situations
     *                             this may not be possible, e.g. constructing user alias search paths programmatically,
     *                             in which case providing a parser configuration here
     *                             <strong>overrides</strong> anything specified directly on the
     *                             annotation
     * @return Global metadata
     */
    public static <C> GlobalMetadata<C> loadGlobal(Class<?> cliClass, ParserMetadata<C> parserConfigOverride) {
        Annotation annotation = cliClass.getAnnotation(com.github.rvesse.airline.annotations.Cli.class);
        if (annotation == null) {
            throw new IllegalArgumentException(String.format("Class %s does not have the @Cli annotation", cliClass));
        }

        com.github.rvesse.airline.annotations.Cli cliConfig = (com.github.rvesse.airline.annotations.Cli) annotation;

        // Find help sections defined at the CLI level
        Map<String, HelpSection> baseHelpSections = loadHelpSections(cliClass,
                                                                     Collections.<String, HelpSection>emptyMap());

        // Prepare parser configuration
        //@formatter:off
        ParserMetadata<C> parserConfig
                = parserConfigOverride != null
                  ? parserConfigOverride
                  : (cliConfig.parserConfiguration() != null
                     ? MetadataLoader.<C>loadParser(cliConfig.parserConfiguration())
                     : MetadataLoader.<C>loadParser(cliClass));
        //@formatter:on

        // Prepare commands
        CommandMetadata defaultCommand = null;
        if (!cliConfig.defaultCommand().equals(com.github.rvesse.airline.annotations.Cli.NO_DEFAULT.class)) {
            defaultCommand = loadCommand(cliConfig.defaultCommand(), new HashMap<>(baseHelpSections), parserConfig);
        }
        List<CommandMetadata> defaultGroupCommands = new ArrayList<CommandMetadata>();
        for (Class<?> cls : cliConfig.commands()) {
            defaultGroupCommands.add(loadCommand(cls, new HashMap<>(baseHelpSections), parserConfig));
        }

        // Prepare restrictions
        // We find restrictions in the following order:
        // 1 - Those declared via annotations
        // 2 - Those declared via the restrictions field of the @Cli annotation
        // 3 - Standard restrictions if the includeDefaultRestrictions field of
        // the @Cli annotation is true
        List<GlobalRestriction> restrictions = new ArrayList<GlobalRestriction>();
        for (Class<? extends Annotation> annotationClass : RestrictionRegistry
                .getGlobalRestrictionAnnotationClasses()) {
            annotation = cliClass.getAnnotation(annotationClass);
            if (annotation == null) {
                continue;
            }
            GlobalRestriction restriction = RestrictionRegistry.getGlobalRestriction(annotationClass, annotation);
            if (restriction != null) {
                restrictions.add(restriction);
            }
        }
        for (Class<? extends GlobalRestriction> cls : cliConfig.restrictions()) {
            restrictions.add(ParserUtil.createInstance(cls));
        }
        if (cliConfig.includeDefaultRestrictions()) {
            restrictions.addAll(Arrays.asList(GlobalRestriction.DEFAULTS));
        }

        // Prepare groups
        // We sort sub-groups by name length then lexically
        // This means that when we build the groups hierarchy we'll ensure we
        // build the parent groups first wherever possible
        Map<String, CommandGroupMetadata> subGroups = new TreeMap<String, CommandGroupMetadata>(
                new StringHierarchyComparator());
        List<CommandGroupMetadata> groups = new ArrayList<CommandGroupMetadata>();
        for (Group groupAnno : cliConfig.groups()) {
            String groupName = groupAnno.name();
            String subGroupPath = null;
            if (StringUtils.containsWhitespace(groupName)) {
                // Normalize the path
                subGroupPath = StringUtils.join(StringUtils.split(groupAnno.name()), ' ');
            }

            // Maybe a top level group we've already seen
            CommandGroupMetadata group = CollectionUtils.find(groups, new GroupFinder(groupName));
            if (group == null) {
                // Maybe a sub-group we've already seen
                group = subGroups.get(subGroupPath);
            }

            List<CommandMetadata> groupCommands = new ArrayList<CommandMetadata>();
            for (Class<?> cls : groupAnno.commands()) {
                groupCommands.add(loadCommand(cls, new HashMap<>(baseHelpSections), parserConfig));
            }

            if (group == null) {
                // Newly discovered group
                //@formatter:off
                group = loadCommandGroup(subGroupPath != null ? subGroupPath : groupName,
                                         groupAnno.description(),
                                         groupAnno.hidden(),
                                         Collections.<CommandGroupMetadata>emptyList(),
                                         !groupAnno.defaultCommand().equals(Group.NO_DEFAULT.class) ? loadCommand(groupAnno.defaultCommand(), baseHelpSections, parserConfig) : null,
                                         groupCommands);
                //@formatter:on
                if (subGroupPath == null) {
                    groups.add(group);
                } else {
                    // Remember sub-groups for later
                    subGroups.put(subGroupPath, group);
                }
            } else {
                for (CommandMetadata cmd : groupCommands) {
                    group.addCommand(cmd);
                }
            }
        }
        // Build sub-group hierarchy
        buildGroupsHierarchy(groups, subGroups);

        // Find all commands
        List<CommandMetadata> allCommands = new ArrayList<CommandMetadata>();
        allCommands.addAll(defaultGroupCommands);
        if (defaultCommand != null && !defaultGroupCommands.contains(defaultCommand)) {
            allCommands.add(defaultCommand);
        }
        for (CommandGroupMetadata group : groups) {
            allCommands.addAll(group.getCommands());
            if (group.getDefaultCommand() != null) {
                allCommands.add(group.getDefaultCommand());
            }

            Queue<CommandGroupMetadata> subGroupsQueue = new LinkedList<CommandGroupMetadata>();
            subGroupsQueue.addAll(group.getSubGroups());
            while (subGroupsQueue.size() > 0) {
                CommandGroupMetadata subGroup = subGroupsQueue.poll();
                allCommands.addAll(subGroup.getCommands());
                if (subGroup.getDefaultCommand() != null) {
                    allCommands.add(subGroup.getDefaultCommand());
                }
                subGroupsQueue.addAll(subGroup.getSubGroups());
            }
        }

        // Post-process to find possible further group assignments
        loadCommandsIntoGroupsByAnnotation(allCommands, groups, defaultGroupCommands, baseHelpSections, parserConfig);

        return loadGlobal(cliConfig.name(), cliConfig.description(), defaultCommand, defaultGroupCommands, groups,
                          restrictions, baseHelpSections.values(), parserConfig);
    }

    /**
     * Loads global meta-data
     *
     * @param name                 CLI name
     * @param description          CLI description
     * @param defaultCommand       Default Command
     * @param defaultGroupCommands Default Group Commands
     * @param groups               Command Groups
     * @param parserConfig         Parser Configuration
     * @param restrictions         Restrictions
     * @param baseHelpSections     Base help sections
     * @return Global meta-data
     */
    public static <C> GlobalMetadata<C> loadGlobal(String name, String description, CommandMetadata defaultCommand,
                                                   Iterable<CommandMetadata> defaultGroupCommands,
                                                   Iterable<CommandGroupMetadata> groups,
                                                   Iterable<GlobalRestriction> restrictions,
                                                   Iterable<HelpSection> baseHelpSections,
                                                   ParserMetadata<C> parserConfig) {
        List<OptionMetadata> globalOptions = new ArrayList<>();
        if (defaultCommand != null) {
            globalOptions.addAll(defaultCommand.getGlobalOptions());
        }
        for (CommandMetadata command : defaultGroupCommands) {
            globalOptions.addAll(command.getGlobalOptions());
        }
        for (CommandGroupMetadata group : groups) {
            for (CommandMetadata command : group.getCommands()) {
                globalOptions.addAll(command.getGlobalOptions());
            }

            // Remember to also search sub-groups for global options
            Queue<CommandGroupMetadata> subGroups = new LinkedList<CommandGroupMetadata>();
            subGroups.addAll(group.getSubGroups());
            while (subGroups.size() > 0) {
                CommandGroupMetadata subGroup = subGroups.poll();
                for (CommandMetadata command : subGroup.getCommands()) {
                    globalOptions.addAll(command.getGlobalOptions());
                }
                subGroups.addAll(subGroup.getSubGroups());
            }
        }
        globalOptions = ListUtils.unmodifiableList(mergeOptionSet(globalOptions));
        return new GlobalMetadata<C>(name, description, globalOptions, defaultCommand, defaultGroupCommands, groups,
                                     restrictions, baseHelpSections, parserConfig);
    }

    /**
     * Loads command group meta-data
     *
     * @param name           Group name
     * @param description    Group description
     * @param hidden         Whether the group is hidden
     * @param defaultCommand Default command for the group
     * @param commands       Commands for the group
     * @return Command group meta-data
     */
    public static CommandGroupMetadata loadCommandGroup(String name, String description, boolean hidden,
                                                        Iterable<CommandGroupMetadata> subGroups,
                                                        CommandMetadata defaultCommand,
                                                        Iterable<CommandMetadata> commands) {
        // Process the name
        if (StringUtils.containsWhitespace(name)) {
            String[] names = StringUtils.split(name);
            name = names[names.length - 1];
        }

        List<OptionMetadata> groupOptions = new ArrayList<OptionMetadata>();
        if (defaultCommand != null) {
            groupOptions.addAll(defaultCommand.getGroupOptions());
        }
        for (CommandMetadata command : commands) {
            groupOptions.addAll(command.getGroupOptions());
        }
        groupOptions = ListUtils.unmodifiableList(mergeOptionSet(groupOptions));
        return new CommandGroupMetadata(name, description, hidden, groupOptions, subGroups, defaultCommand, commands);
    }

    /**
     * Loads command meta-data
     *
     * @param defaultCommands Default command classes
     * @return Command meta-data
     */
    public static <T> List<CommandMetadata> loadCommands(Iterable<Class<? extends T>> defaultCommands,
                                                         Map<String, HelpSection> baseHelpSections,
                                                         ParserMetadata<?> parserConfig) {
        List<CommandMetadata> commandMetadata = new ArrayList<CommandMetadata>();
        Iterator<Class<? extends T>> iter = defaultCommands.iterator();
        while (iter.hasNext()) {
            commandMetadata.add(loadCommand(iter.next(), baseHelpSections, parserConfig));
        }
        return commandMetadata;
    }

    /**
     * Loads command meta-data
     *
     * @param commandType Command class
     * @return Command meta-data
     */
    public static CommandMetadata loadCommand(Class<?> commandType, ParserMetadata<?> parserConfig) {
        return loadCommand(commandType, new HashMap<String, HelpSection>(), parserConfig);
    }

    /**
     * Loads command meta-data
     *
     * @param commandType      Command Type
     * @param baseHelpSections Base set of help sections
     * @return Command meta-data
     */
    public static CommandMetadata loadCommand(Class<?> commandType, Map<String, HelpSection> baseHelpSections,
                                              ParserMetadata<?> parserConfig) {
        if (commandType == null) {
            return null;
        }
        Command command = null;
        List<Group> groups = new ArrayList<>();

        for (Class<?> cls = commandType; command == null && !Object.class.equals(cls); cls = cls.getSuperclass()) {
            command = cls.getAnnotation(Command.class);

            if (cls.isAnnotationPresent(Groups.class)) {
                groups.addAll(Arrays.asList(cls.getAnnotation(Groups.class).value()));
            }
            if (cls.isAnnotationPresent(Group.class)) {
                groups.add(cls.getAnnotation(Group.class));
            }
        }

        if (command == null) {
            throw new IllegalArgumentException(
                    String.format("Command %s is not annotated with @Command", commandType.getName()));
        }

        // Find help sections
        Map<String, HelpSection> helpSections = loadHelpSections(commandType, baseHelpSections);

        String name = command.name();
        String description = command.description().isEmpty() ? null : command.description();
        List<String> groupNames = Arrays.asList(command.groupNames());
        boolean hidden = command.hidden();

        InjectionMetadata injectionMetadata = loadInjectionMetadata(commandType, parserConfig);

        //@formatter:off
        CommandMetadata commandMetadata = new CommandMetadata(name, 
                                                              description, 
                                                              hidden, 
                                                              injectionMetadata.globalOptions, 
                                                              injectionMetadata.groupOptions,
                                                              injectionMetadata.commandOptions, 
                                                              injectionMetadata.defaultOption,
                                                              AirlineUtils.first(injectionMetadata.arguments, null),
                                                              injectionMetadata.metadataInjections, 
                                                              commandType, 
                                                              groupNames, 
                                                              groups, 
                                                              AirlineUtils.listCopy(helpSections.values()));
        //@formatter:on

        return commandMetadata;
    }

    protected static Map<String, HelpSection> loadHelpSections(Class<?> sourceClass,
                                                               Map<String, HelpSection> baseHelpSections) {
        Map<String, HelpSection> helpSections = new HashMap<>();

        // Search for help section annotations in the class hierarchy
        for (Class<?> cls = sourceClass; !Object.class.equals(cls); cls = cls.getSuperclass()) {
            for (Class<? extends Annotation> helpAnnotationClass : HelpSectionRegistry.getAnnotationClasses()) {
                Annotation annotation = cls.getAnnotation(helpAnnotationClass);
                if (annotation == null) {
                    continue;
                }
                HelpSection section = HelpSectionRegistry.getHelpSection(helpAnnotationClass, annotation);
                if (section == null) {
                    continue;
                }

                // Because we're going up the class hierarchy the titled section
                // lowest down the hierarchy should win so if we've already seen
                // a section with this title ignore it
                if (helpSections.containsKey(section.getTitle().toLowerCase(Locale.ENGLISH))) {
                    continue;
                }

                helpSections.put(section.getTitle().toLowerCase(Locale.ENGLISH), section);
            }
        }

        // Add in base sections (if any)
        // Need to do this afterwards as anything defined in the class hierarchy
        // will override any base definitions
        if (baseHelpSections.isEmpty()) {
            return helpSections;
        }

        for (String key : baseHelpSections.keySet()) {
            if (helpSections.containsKey(key.toLowerCase(Locale.ENGLISH))) {
                continue;
            }
            helpSections.put(key.toLowerCase(Locale.ENGLISH), baseHelpSections.get(key));
        }

        return helpSections;

    }

    /**
     * Loads suggester meta-data
     *
     * @param suggesterClass Suggester class
     * @return Suggester meta-data
     */
    public static SuggesterMetadata loadSuggester(Class<? extends Suggester> suggesterClass,
                                                  ParserMetadata<?> parserConfig) {
        InjectionMetadata injectionMetadata = loadInjectionMetadata(suggesterClass, parserConfig);
        return new SuggesterMetadata(suggesterClass, injectionMetadata.metadataInjections);
    }

    /**
     * Loads injection meta-data
     * <p>
     * Given a class that represents a command, searches its fields to find those that are annotated with Airline
     * annotations e.g. {@link Option}, {@link Arguments} in order to discover all the options and arguments for a
     * command.  This also includes walking back up the superclass hierarchy so options and arguments may be defined in
     * shared base classes and still discovered.
     * </p>
     * <p>
     * Additionally options and arguments may be modularised out into separate classes that can be composed into your
     * command classes by defining a field of the appropriate type and annotating it with an injection annotation to
     * tell Airline it also needs to discover options inside that class.
     * </p>
     * <p>
     * Historically Airline supported only the {@code javax.inject.Inject} annotation, but with the move of most
     * {@code javax} packages (Java EE) to the stewardship of the Eclipse Foundation those packages are gradually being
     * migrated into the {@code jakarta} namespace.  As of <strong>2.9.0</strong> Airline makes the choice of annotation
     * fully configurable via the parser configuration.  To avoid potential class loading issues these are specified as
     * string class names with the metadata loader dynamically loading the relevant annotation classes if they are
     * present on the runtime classpath.  As of <strong>3.10.0</strong> we only look for our own {@link AirlineModule}
     * annotation and any other composition annotation e.g. {@code jakarta.inject.Inject} <strong>MUST</strong> be
     * explicitly configured.
     *
     * @param type         Class
     * @param parserConfig Parser Configuration
     * @return Injection meta-data
     */
    public static InjectionMetadata loadInjectionMetadata(Class<?> type, ParserMetadata<?> parserConfig) {
        InjectionMetadata injectionMetadata = new InjectionMetadata();
        loadInjectionMetadata(type, injectionMetadata, Collections.<Field>emptyList(), parserConfig);
        injectionMetadata.compact();
        return injectionMetadata;
    }

    /**
     * Loads injection meta-data
     *
     * @param type              Class
     * @param injectionMetadata Injection meta-data
     * @param fields            Fields
     */
    public static void loadInjectionMetadata(Class<?> type, InjectionMetadata injectionMetadata, List<Field> fields,
                                             ParserMetadata<?> parserConfig) {
        if (type.isInterface()) {
            return;
        }
        for (Class<?> cls = type; !Object.class.equals(cls); cls = cls.getSuperclass()) {
            for (Field field : cls.getDeclaredFields()) {
                field.setAccessible(true);
                List<Field> path = new ArrayList<>(fields);
                path.add(field);

                // Check for various forms of @Inject annotation
                // See Issues #115 and #81 for broader context but basically most of the javax. namespaces are gradually
                // transitioning to jakarta. because those APIs were moved to the Eclipse Foundation and Oracle didn't
                // want them using the Java package in their package names.  This is a slow transition that is painful
                // for the Java community, so we're supporting both old and new forms for the time being.  Plus Guice
                // because it was in the codebase historically and people may still be using it.
                // See #81 for discussion of planned future changes around introducing our own annotation instead of
                // reusing the existing ones
                for (String injectionAnnotation : parserConfig.getCompositionAnnotations()) {
                    checkForInjectionAnnotation(injectionMetadata, field, path, injectionAnnotation, parserConfig);
                }

                Option optionAnnotation = field.getAnnotation(Option.class);
                DefaultOption defaultOptionAnnotation = field.getAnnotation(DefaultOption.class);
                if (optionAnnotation != null) {
                    OptionType optionType = optionAnnotation.type();
                    List<String> titles;
                    if (optionAnnotation.title().length > 0) {
                        if (optionAnnotation.title().length == 1 && StringUtils.isBlank(optionAnnotation.title()[0])) {
                            titles = Collections.singletonList(field.getName());
                        } else {
                            titles = Arrays.asList(optionAnnotation.title());
                        }
                    } else {
                        titles = Collections.singletonList(field.getName());
                    }

                    List<String> options = Arrays.asList(optionAnnotation.name());
                    String description = optionAnnotation.description();

                    int arity = optionAnnotation.arity();
                    if (arity < 0 && arity != Integer.MIN_VALUE) {
                        throw new IllegalArgumentException(String.format("Invalid arity for option %s", titles.get(0)));
                    }

                    if (optionAnnotation.arity() >= 0) {
                        arity = optionAnnotation.arity();
                    } else {
                        Class<?> fieldType = field.getType();
                        if (Boolean.class.isAssignableFrom(fieldType) || boolean.class.isAssignableFrom(fieldType)) {
                            arity = 0;
                        } else {
                            arity = 1;
                        }
                    }

                    boolean hidden = optionAnnotation.hidden();
                    boolean override = optionAnnotation.override();
                    boolean sealed = optionAnnotation.sealed();

                    // Find and create restrictions
                    Map<Class<? extends Annotation>, Set<Integer>> partials = loadPartials(field);
                    List<OptionRestriction> restrictions = new ArrayList<OptionRestriction>();
                    for (Class<? extends Annotation> annotationClass : RestrictionRegistry
                            .getOptionRestrictionAnnotationClasses()) {
                        Annotation annotation = field.getAnnotation(annotationClass);
                        if (annotation == null) {
                            continue;
                        }
                        OptionRestriction restriction = RestrictionRegistry.getOptionRestriction(annotationClass,
                                                                                                 annotation);
                        if (restriction != null) {
                            // Adjust for partial if necessary
                            if (partials.containsKey(annotationClass)) {
                                restriction = new PartialRestriction(partials.get(annotationClass), restriction);
                            }

                            restrictions.add(restriction);
                        }
                    }

                    // Type Converter provider
                    TypeConverterProvider provider = ParserUtil
                            .createInstance(optionAnnotation.typeConverterProvider());

                    //@formatter:off
                    OptionMetadata optionMetadata = new OptionMetadata(optionType, 
                                                                       options,
                                                                       titles, 
                                                                       description, 
                                                                       arity,
                                                                       hidden, 
                                                                       override, 
                                                                       sealed,
                                                                       restrictions,
                                                                       provider,
                                                                       path);
                    //@formatter:on
                    switch (optionType) {
                        case GLOBAL:
                            if (defaultOptionAnnotation != null) {
                                throw new IllegalArgumentException(String.format(
                                        "Field %s which defines a global option cannot be annotated with @DefaultOption as this may only be applied to command options",
                                        field));
                            }
                            injectionMetadata.globalOptions.add(optionMetadata);
                            break;
                        case GROUP:
                            if (defaultOptionAnnotation != null) {
                                throw new IllegalArgumentException(String.format(
                                        "Field %s which defines a global option cannot be annotated with @DefaultOption as this may only be applied to command options",
                                        field));
                            }
                            injectionMetadata.groupOptions.add(optionMetadata);
                            break;
                        case COMMAND:
                            // Do we also have a @DefaultOption annotation

                            if (defaultOptionAnnotation != null) {
                                // Can't have both @DefaultOption and @Arguments
                                if (injectionMetadata.arguments.size() > 0) {
                                    throw new IllegalArgumentException(String.format(
                                            "Field %s cannot be annotated with @DefaultOption because there are fields with @Arguments annotations present",
                                            field));
                                }
                                // Can't have more than one @DefaultOption
                                if (injectionMetadata.defaultOption != null) {
                                    throw new IllegalArgumentException(String.format(
                                            "Command type %s has more than one field with @DefaultOption declared upon it",
                                            type));
                                }
                                // Arity of associated @Option must be 1
                                if (optionMetadata.getArity() != 1) {
                                    throw new IllegalArgumentException(String.format(
                                            "Field %s annotated with @DefaultOption must also have an @Option annotation with an arity of 1",
                                            field));
                                }
                                injectionMetadata.defaultOption = optionMetadata;
                            }
                            injectionMetadata.commandOptions.add(optionMetadata);
                            break;
                    }
                }

                if (optionAnnotation == null && defaultOptionAnnotation != null) {
                    // Can't have @DefaultOption on a field without also @Option
                    throw new IllegalArgumentException(String.format(
                            "Field %s annotated with @DefaultOption must also have an @Option annotation", field));
                }

                Arguments argumentsAnnotation = field.getAnnotation(Arguments.class);
                if (field.isAnnotationPresent(Arguments.class)) {
                    // Can't have both @DefaultOption and @Arguments
                    if (injectionMetadata.defaultOption != null) {
                        throw new IllegalArgumentException(String.format(
                                "Field %s cannot be annotated with @Arguments because there is a field with @DefaultOption present",
                                field));
                    }

                    List<String> titles = new ArrayList<>();

                    if (!(argumentsAnnotation.title().length == 1 && argumentsAnnotation.title()[0].equals(""))) {
                        titles.addAll(Arrays.asList(argumentsAnnotation.title()));
                    } else {
                        titles.add(field.getName());
                    }

                    String description = argumentsAnnotation.description();
                    TypeConverterProvider provider = ParserUtil
                            .createInstance(argumentsAnnotation.typeConverterProvider());

                    Map<Class<? extends Annotation>, Set<Integer>> partials = loadPartials(field);
                    List<ArgumentsRestriction> restrictions = new ArrayList<>();
                    for (Class<? extends Annotation> annotationClass : RestrictionRegistry
                            .getArgumentsRestrictionAnnotationClasses()) {
                        Annotation annotation = field.getAnnotation(annotationClass);
                        if (annotation == null) {
                            continue;
                        }
                        ArgumentsRestriction restriction = RestrictionRegistry.getArgumentsRestriction(annotationClass,
                                                                                                       annotation);
                        if (restriction != null) {
                            // Adjust for partial if necessary
                            if (partials.containsKey(annotationClass)) {
                                restriction = new PartialRestriction(partials.get(annotationClass), restriction);
                            }

                            restrictions.add(restriction);
                        }
                    }

                    //@formatter:off
                    injectionMetadata.arguments.add(new ArgumentsMetadata(titles, 
                                                                          description,
                                                                          restrictions,
                                                                          provider,
                                                                          path));
                    //@formatter:on
                }
            }
        }
    }

    private static void checkForInjectionAnnotation(InjectionMetadata injectionMetadata, Field field, List<Field> path,
                                                    String annotationClass, ParserMetadata<?> parserConfig) {
        try {
            // Use a cache to avoid trying to dynamically create the annotation class multiple times, this also allows
            // us to short-circuit our logic if we already know a given annotation class is not present on the classpath
            Class<? extends Annotation> annotationType = dynamicAnnotationCache.get(annotationClass);
            if (annotationType == null) {
                // Short-circuit if we know this annotation class isn't on the classpath
                if (dynamicAnnotationCache.containsKey(annotationClass)) {
                    return;
                }

                // Otherwise, try and create an instance of it, caching for future reuse
                dynamicAnnotationCache.put(annotationClass,
                                           (Class<? extends Annotation>) Class.forName(annotationClass));
                annotationType = dynamicAnnotationCache.get(annotationClass);
            }
            if (annotationType == null) {
                return;
            }

            @SuppressWarnings("unchecked")
            Annotation annotation = field.getAnnotation(annotationType);
            if (annotation != null) {
                if (field.getType().equals(GlobalMetadata.class)
                        || field.getType().equals(CommandGroupMetadata.class)
                        || field.getType().equals(CommandMetadata.class)) {
                    injectionMetadata.metadataInjections.add(new Accessor(path));
                } else {
                    loadInjectionMetadata(field.getType(), injectionMetadata, path, parserConfig);
                }
            }
        } catch (ClassNotFoundException e) {
            // this is ok, means the particular variant of the injection annotation is not on the class path
            dynamicAnnotationCache.put(annotationClass, null);
        } catch (ClassCastException e) {
            // ignore this too, we're doing some funky cross your fingers type reflect stuff to play nicely with other
            // dependency injection frameworks
            dynamicAnnotationCache.put(annotationClass, null);
        }
    }

    private static Map<Class<? extends Annotation>, Set<Integer>> loadPartials(Field field) {
        Map<Class<? extends Annotation>, Set<Integer>> partials = new HashMap<>();

        Annotation partialsAnnotation = field.getAnnotation(Partials.class);
        if (partialsAnnotation != null) {
            for (Partial partial : ((Partials) partialsAnnotation).value()) {
                collectPartial(partials, partial);
            }
        }
        Annotation partialAnnotation = field.getAnnotation(Partial.class);
        if (partialAnnotation != null) {
            collectPartial(partials, (Partial) partialAnnotation);
        }

        return partials;
    }

    private static void collectPartial(Map<Class<? extends Annotation>, Set<Integer>> partials, Partial partial) {
        Set<Integer> indices = partials.get(partial.restriction());
        if (indices == null) {
            indices = new HashSet<>();
            partials.put(partial.restriction(), indices);
        }
        indices.addAll(Arrays.asList(ArrayUtils.toObject(partial.appliesTo())));
    }

    private static List<OptionMetadata> mergeOptionSet(List<OptionMetadata> options) {
        Map<OptionMetadata, List<OptionMetadata>> metadataIndex = new HashMap<>();
        for (OptionMetadata option : options) {
            List<OptionMetadata> list = metadataIndex.get(option);
            if (list == null) {
                list = new ArrayList<OptionMetadata>();
                metadataIndex.put(option, list);
            }
            list.add(option);
        }

        options = new ArrayList<OptionMetadata>();
        for (List<OptionMetadata> ops : metadataIndex.values()) {
            options.add(new OptionMetadata(ops));
        }
        options = ListUtils.unmodifiableList(options);

        Map<String, OptionMetadata> optionIndex = new LinkedHashMap<>();
        for (OptionMetadata option : options) {
            for (String optionName : option.getOptions()) {
                if (optionIndex.containsKey(optionName)) {
                    throw new IllegalArgumentException(
                            String.format("Fields %s and %s have conflicting definitions of option %s",
                                          optionIndex.get(optionName).getAccessors().iterator().next(),
                                          option.getAccessors().iterator().next(), optionName));
                }
                optionIndex.put(optionName, option);
            }
        }

        return options;
    }

    private static List<OptionMetadata> overrideOptionSet(List<OptionMetadata> options) {
        options = ListUtils.unmodifiableList(options);

        Map<Set<String>, OptionMetadata> optionIndex = new HashMap<>();
        for (OptionMetadata option : options) {
            Set<String> names = option.getOptions();
            if (optionIndex.containsKey(names)) {
                // Multiple classes in the hierarchy define this option
                // Determine if we can successfully override this option
                tryOverrideOptions(optionIndex, names, option);
            } else {
                // Need to check there isn't another option with partial overlap
                // of names, this is considered an illegal override
                for (Set<String> existingNames : optionIndex.keySet()) {
                    Set<String> intersection = AirlineUtils.intersection(names, existingNames);
                    if (intersection.size() > 0) {
                        throw new IllegalArgumentException(String.format(
                                "Fields %s and %s have overlapping definitions of option %s, options can only be overridden if they have precisely the same set of option names",
                                option.getAccessors().iterator().next(),
                                optionIndex.get(existingNames).getAccessors().iterator().next(), intersection));
                    }
                }

                optionIndex.put(names, option);
            }
        }

        return ListUtils.unmodifiableList(IteratorUtils.toList(optionIndex.values().iterator()));
    }

    private static void tryOverrideOptions(Map<Set<String>, OptionMetadata> optionIndex, Set<String> names,
                                           OptionMetadata parent) {

        // As the metadata is extracted from the deepest class in the hierarchy
        // going upwards we need to treat the passed option as the parent and
        // the pre-existing option definition as the child
        OptionMetadata child = optionIndex.get(names);

        Accessor parentField = parent.getAccessors().iterator().next();
        Accessor childField = child.getAccessors().iterator().next();

        // Check for duplicates
        boolean isDuplicate = parent == child || parent.equals(child);

        // Parent must not state it is sealed UNLESS it is a duplicate which can
        // happen when using @Inject to inject options via delegates
        if (parent.isSealed() && !isDuplicate) {
            throw new IllegalArgumentException(String.format(
                    "Fields %s and %s have conflicting definitions of option %s - parent field %s declares itself as sealed and cannot be overridden",
                    parentField, childField, names, parentField));
        }

        // Child must explicitly state that it overrides otherwise we cannot
        // override UNLESS it is the case that this is a duplicate which
        // can happen when using @Inject to inject options via delegates
        if (!child.isOverride() && !isDuplicate) {
            throw new IllegalArgumentException(String.format(
                    "Fields %s and %s have conflicting definitions of option %s - if you wanted to override this option you must explicitly specify override = true in your child field annotation",
                    parentField, childField, names));
        }

        // Attempt overriding, this will error if the overriding is not possible
        OptionMetadata merged = OptionMetadata.override(names, parent, child);
        optionIndex.put(names, merged);
    }

    public static void loadCommandsIntoGroupsByAnnotation(List<CommandMetadata> allCommands,
                                                          List<CommandGroupMetadata> commandGroups,
                                                          List<CommandMetadata> defaultCommandGroup,
                                                          Map<String, HelpSection> baseHelpSections,
                                                          ParserMetadata<?> parserConfig) {
        List<CommandMetadata> newCommands = new ArrayList<CommandMetadata>();

        // first, create any groups explicitly annotated
        createGroupsFromAnnotations(allCommands, newCommands, commandGroups, defaultCommandGroup, baseHelpSections,
                                    parserConfig);

        for (CommandMetadata command : allCommands) {
            boolean addedToGroup = false;

            // now add the command to any groupNames specified in the Command
            // annotation
            for (String groupName : command.getGroupNames()) {
                CommandGroupMetadata group = CollectionUtils.find(commandGroups, new GroupFinder(groupName));
                if (group != null) {
                    // Add to existing top level group
                    group.addCommand(command);
                    addedToGroup = true;
                } else {
                    if (StringUtils.containsWhitespace(groupName)) {
                        // Add to sub-group
                        String[] groups = StringUtils.split(groupName);
                        CommandGroupMetadata subGroup = null;
                        for (int i = 0; i < groups.length; i++) {
                            if (i == 0) {
                                // Find/create the necessary top level group
                                subGroup = CollectionUtils.find(commandGroups, new GroupFinder(groups[i]));
                                if (subGroup == null) {
                                    subGroup = new CommandGroupMetadata(groups[i], "", false,
                                                                        Collections.<OptionMetadata>emptyList(),
                                                                        Collections.<CommandGroupMetadata>emptyList(),
                                                                        null,
                                                                        Collections.<CommandMetadata>emptyList());
                                    commandGroups.add(subGroup);
                                }
                            } else {
                                // Find/create the next sub-group
                                CommandGroupMetadata nextSubGroup = CollectionUtils.find(subGroup.getSubGroups(),
                                                                                         new GroupFinder(groups[i]));
                                if (nextSubGroup == null) {
                                    nextSubGroup = new CommandGroupMetadata(groups[i], "", false,
                                                                            Collections.<OptionMetadata>emptyList(),
                                                                            Collections.<CommandGroupMetadata>emptyList(),
                                                                            null,
                                                                            Collections.<CommandMetadata>emptyList());
                                }
                                subGroup.addSubGroup(nextSubGroup);
                                subGroup = nextSubGroup;
                            }
                        }
                        if (subGroup == null) {
                            throw new IllegalStateException("Failed to resolve sub-group path");
                        }
                        subGroup.addCommand(command);
                        addedToGroup = true;
                    } else {
                        // Add to newly created top level group
                        CommandGroupMetadata newGroup = loadCommandGroup(groupName, "", false,
                                                                         Collections.<CommandGroupMetadata>emptyList(),
                                                                         null,
                                                                         Collections.singletonList(command));
                        commandGroups.add(newGroup);
                        addedToGroup = true;
                    }
                }
            }

            if (addedToGroup && defaultCommandGroup.contains(command)) {
                defaultCommandGroup.remove(command);
            }
        }

        allCommands.addAll(newCommands);
    }

    @SuppressWarnings("rawtypes")
    private static void createGroupsFromAnnotations(List<CommandMetadata> allCommands,
                                                    List<CommandMetadata> newCommands,
                                                    List<CommandGroupMetadata> commandGroups,
                                                    List<CommandMetadata> defaultCommandGroup,
                                                    Map<String, HelpSection> baseHelpSections,
                                                    ParserMetadata<?> parserConfig) {

        // We sort sub-groups by name length then lexically
        // This means that when we build the groups hierarchy we'll ensure we
        // build the parent groups first wherever possible
        Map<String, CommandGroupMetadata> subGroups = new TreeMap<String, CommandGroupMetadata>(
                new StringHierarchyComparator());
        for (CommandMetadata command : allCommands) {
            boolean addedToGroup = false;

            // first, create any groups explicitly annotated
            for (Group groupAnno : command.getGroups()) {
                Class defaultCommandClass = null;
                CommandMetadata defaultCommand = null;

                // load default command if needed
                if (!groupAnno.defaultCommand().equals(Group.NO_DEFAULT.class)) {
                    defaultCommandClass = groupAnno.defaultCommand();
                    defaultCommand = CollectionUtils.find(allCommands, new CommandTypeFinder(defaultCommandClass));
                    if (null == defaultCommand) {
                        defaultCommand = loadCommand(defaultCommandClass, baseHelpSections, parserConfig);
                        newCommands.add(defaultCommand);
                    }
                }

                // load other commands if needed
                List<CommandMetadata> groupCommands = new ArrayList<CommandMetadata>(groupAnno.commands().length);
                CommandMetadata groupCommand = null;
                for (Class commandClass : groupAnno.commands()) {
                    groupCommand = CollectionUtils.find(allCommands, new CommandTypeFinder(commandClass));
                    if (null == groupCommand) {
                        groupCommand = loadCommand(commandClass, baseHelpSections, parserConfig);
                        newCommands.add(groupCommand);
                        groupCommands.add(groupCommand);
                    }
                }

                // Find the group metadata
                // May already exist as a top level group
                CommandGroupMetadata groupMetadata = CollectionUtils.find(commandGroups,
                                                                          new GroupFinder(groupAnno.name()));
                if (groupMetadata == null) {
                    // Not a top level group

                    String subGroupPath = null;
                    if (StringUtils.containsWhitespace(groupAnno.name())) {
                        // Is this a sub-group we've already seen?
                        // Make sure to normalize white space in the path
                        subGroupPath = StringUtils.join(StringUtils.split(groupAnno.name()), " ");
                        groupMetadata = subGroups.get(subGroupPath);
                    }

                    if (groupMetadata == null) {
                        // Newly discovered group
                        groupMetadata = loadCommandGroup(groupAnno.name(), groupAnno.description(), groupAnno.hidden(),
                                                         Collections.<CommandGroupMetadata>emptyList(), defaultCommand,
                                                         groupCommands);
                        if (!StringUtils.containsWhitespace(groupAnno.name())) {
                            // Add as top level group
                            commandGroups.add(groupMetadata);
                        } else {
                            // This is a new sub-group, put aside for now and
                            // we'll build the sub-group tree later
                            subGroups.put(subGroupPath, groupMetadata);
                        }
                    }
                }

                groupMetadata.addCommand(command);
                addedToGroup = true;
            }

            if (addedToGroup && defaultCommandGroup.contains(command)) {
                defaultCommandGroup.remove(command);
            }
        }

        buildGroupsHierarchy(commandGroups, subGroups);
    }

    protected static void buildGroupsHierarchy(List<CommandGroupMetadata> commandGroups,
                                               Map<String, CommandGroupMetadata> subGroups) {
        // Add sub-groups into hierarchy as appropriate
        for (String subGroupPath : subGroups.keySet()) {
            CommandGroupMetadata subGroup = subGroups.get(subGroupPath);
            String[] groups = StringUtils.split(subGroupPath);
            CommandGroupMetadata parentGroup = null;
            for (int i = 0; i < groups.length - 1; i++) {
                if (i == 0) {
                    // Should be a top level group
                    parentGroup = CollectionUtils.find(commandGroups, new GroupFinder(groups[i]));
                    if (parentGroup == null) {
                        // Top level parent group does not exist so create empty
                        // top level group
                        parentGroup = new CommandGroupMetadata(groups[i], "", false,
                                                               Collections.<OptionMetadata>emptyList(),
                                                               Collections.<CommandGroupMetadata>emptyList(), null,
                                                               Collections.<CommandMetadata>emptyList());
                        commandGroups.add(parentGroup);
                    }
                } else {
                    // Should be a sub-group of the current parent
                    CommandGroupMetadata nextParent = CollectionUtils.find(parentGroup.getSubGroups(),
                                                                           new GroupFinder(groups[i]));
                    if (nextParent == null) {
                        // Next parent group does not exist so create empty
                        // group
                        nextParent = new CommandGroupMetadata(groups[i], "", false,
                                                              Collections.<OptionMetadata>emptyList(),
                                                              Collections.<CommandGroupMetadata>emptyList(), null,
                                                              Collections.<CommandMetadata>emptyList());
                    }
                    parentGroup.addSubGroup(nextParent);
                    nextParent.setParent(parentGroup);
                    parentGroup = nextParent;
                }
            }
            if (parentGroup == null) {
                throw new IllegalStateException("Failed to resolve sub-group path");
            }
            parentGroup.addSubGroup(subGroup);
            subGroup.setParent(parentGroup);
        }
    }

    private static class InjectionMetadata {
        private List<OptionMetadata> globalOptions = new ArrayList<>();
        private List<OptionMetadata> groupOptions = new ArrayList<>();
        private List<OptionMetadata> commandOptions = new ArrayList<>();
        private OptionMetadata defaultOption = null;
        private List<ArgumentsMetadata> arguments = new ArrayList<>();
        private List<Accessor> metadataInjections = new ArrayList<>();

        private void compact() {
            globalOptions = overrideOptionSet(globalOptions);
            groupOptions = overrideOptionSet(groupOptions);
            commandOptions = overrideOptionSet(commandOptions);
            if (defaultOption != null) {
                for (OptionMetadata option : commandOptions) {
                    boolean found = false;
                    for (String opt : defaultOption.getOptions()) {
                        if (option.getOptions().contains(opt)) {
                            defaultOption = option;
                            found = true;
                            break;
                        }
                    }
                    if (found) {
                        break;
                    }
                }
            }

            if (arguments.size() > 1) {
                arguments = ListUtils.unmodifiableList(Collections.singletonList(new ArgumentsMetadata(arguments)));
            }
        }
    }
}
