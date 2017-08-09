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
package com.github.rvesse.airline.maven;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import com.github.rvesse.airline.help.CommandGroupUsageGenerator;
import com.github.rvesse.airline.help.CommandUsageGenerator;
import com.github.rvesse.airline.help.GlobalUsageGenerator;
import com.github.rvesse.airline.maven.formats.FormatMappingRegistry;
import com.github.rvesse.airline.maven.formats.FormatOptions;
import com.github.rvesse.airline.maven.formats.FormatProvider;
import com.github.rvesse.airline.maven.sources.PreparedSource;
import com.github.rvesse.airline.model.CommandGroupMetadata;
import com.github.rvesse.airline.model.CommandMetadata;
import com.github.rvesse.airline.model.GlobalMetadata;
import com.github.rvesse.airline.model.ParserMetadata;

/**
 * Generates Airline powered help
 *
 */
//@formatter:off
@Mojo(name = "generate", 
      defaultPhase = LifecyclePhase.PROCESS_CLASSES, 
      requiresOnline = false, 
      requiresDependencyResolution = ResolutionScope.RUNTIME,
      threadSafe = true,
      requiresProject = true
)
//@formatter:on
public class GenerateMojo extends AbstractAirlineMojo {

    /**
     * Formats to produce help in
     */
    @Parameter(defaultValue = "MAN")
    private List<String> formats;

    @Parameter(defaultValue = "true")
    private boolean failOnUnknownFormat = true;

    @Parameter(defaultValue = "false")
    private boolean failOnUnsupportedOutputMode = false;

    @Parameter(defaultValue = "true")
    private boolean skipBadSources = true;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (project == null)
            throw new MojoFailureException("Maven project was not injected into Mojo");
        if (pluginDescriptor == null)
            throw new MojoFailureException("Plugin Descriptor was not injected into Mojo");

        Log log = getLog();

        // Prepare the class realm
        prepareClassRealm();

        // Discover classes and get their meta-data as appropriate
        List<PreparedSource> sources = prepareSources(this.skipBadSources);
        if (sources.size() == 0) {
            log.info("No valid sources discovered so nothing to do");
            return;
        }

        // Ensure directory is created
        ensureOutputDirectory();

        // Prepare default format options
        FormatOptions defaultOptions = this.defaultOptions == null ? new FormatOptions()
                : new FormatOptions(this.defaultOptions);
        log.debug(String.format("Default format options are %s", defaultOptions));

        // Prepare format mappings
        Map<String, FormatOptions> mappedOptions = prepareFormatMappings(defaultOptions);

        for (String format : formats) {
            // Prepare the format provider and the appropriate formatting
            // options
            FormatProvider provider = FormatMappingRegistry.find(format);
            if (provider == null) {
                if (failOnUnknownFormat)
                    throw new MojoFailureException(
                            String.format("Format %s does not have a format mapping defined", format));
                log.debug(String.format("Format %s is unknown and was skipped", format));
            }
            FormatOptions options = mappedOptions.get(format);
            if (options == null)
                options = defaultOptions;
            if (options != defaultOptions)
                log.debug(String.format("Format %s format options are %s", format, options));

            CommandUsageGenerator commandGenerator = provider.getCommandGenerator(this.outputDirectory, options);
            if (commandGenerator == null) {
                if (failOnUnsupportedOutputMode)
                    throw new MojoFailureException(String.format("Command help is not supported by format %s", format));
                log.warn("Command help is not supported by format " + format);
            } else {
                log.info(String.format("Using command help generator %s as default for format %s",
                        commandGenerator.getClass(), format));

                // Generate command help
                for (PreparedSource source : sources) {
                    FormatOptions sourceOptions = source.getFormatOptions(options);
                    CommandUsageGenerator sourceCommandGenerator = commandGenerator;
                    if (source.isCommand()) {
                        if (!source.shouldOutputCommandHelp()) {
                            log.debug(String.format(
                                    "Skipping command help for class %s because configured output mode is %s",
                                    source.getSourceClass(), source.getOutputMode()));
                            continue;
                        }

                        if (sourceOptions != options) {
                            // Source specific options and thus potentially
                            // generator
                            sourceCommandGenerator = prepareCommandGenerator(provider, options, source, sourceOptions);
                        }

                        outputCommandHelp(format, provider, sourceOptions, sourceCommandGenerator, source);
                    } else if (source.isGlobal() && source.getOutputMode() == OutputMode.COMMAND) {
                        log.debug(String.format("Generating command help for all commands provided by CLI class %s",
                                source.getSourceClass()));

                        if (sourceOptions != options) {
                            sourceCommandGenerator = prepareCommandGenerator(provider, options, source, sourceOptions);
                        }

                        // Firstly dump the default commands group and then dump
                        // the command groups
                        GlobalMetadata<Object> global = source.getGlobal();
                        outputGroupCommandsHelp(format, provider, sourceOptions, sourceCommandGenerator, source,
                                global.getDefaultGroupCommands(), global.getParserConfiguration(), global.getName(),
                                (String[]) null);
                        for (CommandGroupMetadata group : global.getCommandGroups()) {
                            outputGroupCommandsHelp(format, provider, sourceOptions, sourceCommandGenerator, source,
                                    group, global.getParserConfiguration(), global.getName(), (String[]) null);
                        }
                    }
                }
            }

            CommandGroupUsageGenerator<Object> groupGenerator = provider.getGroupGenerator(this.outputDirectory,
                    options);
            // TODO Output group help

            GlobalUsageGenerator<Object> globalGenerator = provider.getGlobalGenerator(this.outputDirectory, options);
            if (globalGenerator == null) {
                if (failOnUnsupportedOutputMode)
                    throw new MojoFailureException(String.format("CLI help is not supported by format %s", format));
                log.warn("CLI help is not supported by format " + format);
            } else {
                log.info(
                        String.format("Using CLI help generator %s for format %s", globalGenerator.getClass(), format));

                // Generate global help
                for (PreparedSource source : sources) {
                    if (!source.isGlobal())
                        continue;

                    FormatOptions sourceOptions = source.getFormatOptions(options);
                    if (sourceOptions != options) {
                        log.debug(String.format("Source %s format options are %s", source.getSourceClass(), options));
                        // TODO Get modified global help generator based on
                        // source specific options
                    }

                    outputGlobalHelp(format, provider, sourceOptions, globalGenerator, source);
                }
            }

        }
    }

    private void outputGroupCommandsHelp(String format, FormatProvider provider, FormatOptions sourceOptions,
            CommandUsageGenerator commandGenerator, PreparedSource source, Collection<CommandMetadata> commands,
            ParserMetadata<Object> parser, String programName, String... groupNames) throws MojoFailureException {
        for (CommandMetadata command : commands) {
            outputCommandHelp(format, provider, sourceOptions, commandGenerator, source, command, parser, programName,
                    groupNames);
        }
    }

    private void outputGroupCommandsHelp(String format, FormatProvider provider, FormatOptions sourceOptions,
            CommandUsageGenerator commandGenerator, PreparedSource source, CommandGroupMetadata group,
            ParserMetadata<Object> parser, String programName, String... groupNames) throws MojoFailureException {

        // Add our group name to the group names path
        groupNames = concatGroupNames(groupNames, group.getName());

        // Output help for commands in this group
        outputGroupCommandsHelp(format, provider, sourceOptions, commandGenerator, source, group.getCommands(), parser,
                programName, groupNames);

        // Recurse to output help for commands in sub-groups
        for (CommandGroupMetadata subGroup : group.getSubGroups()) {
            outputGroupCommandsHelp(format, provider, sourceOptions, commandGenerator, source, subGroup, parser,
                    programName, groupNames);
        }
    }

    private String[] concatGroupNames(String[] names, String finalName) {
        String[] finalNames;
        if (names != null) {
            finalNames = Arrays.copyOf(names, names.length + 1);
        } else {
            finalNames = new String[1];
        }
        finalNames[finalNames.length - 1] = finalName;
        return finalNames;
    }

    private CommandUsageGenerator prepareCommandGenerator(FormatProvider provider, FormatOptions options,
            PreparedSource source, FormatOptions sourceOptions) {
        Log log = getLog();
        CommandUsageGenerator sourceCommandGenerator;
        log.debug(String.format("Source %s format options are %s", source.getSourceClass(), options));
        sourceCommandGenerator = provider.getCommandGenerator(this.outputDirectory, sourceOptions);
        log.info(String.format("Using command help generator %s for source %s", sourceCommandGenerator.getClass(),
                source.getSourceClass()));
        return sourceCommandGenerator;
    }
}