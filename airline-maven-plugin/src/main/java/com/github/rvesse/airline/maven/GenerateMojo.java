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

import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

import com.github.rvesse.airline.help.CommandGroupUsageGenerator;
import com.github.rvesse.airline.help.CommandUsageGenerator;
import com.github.rvesse.airline.help.GlobalUsageGenerator;
import com.github.rvesse.airline.maven.formats.FormatMappingRegistry;
import com.github.rvesse.airline.maven.formats.FormatOptions;
import com.github.rvesse.airline.maven.formats.FormatProvider;
import com.github.rvesse.airline.maven.sources.PreparedSource;
import com.github.rvesse.airline.model.CommandGroupMetadata;
import com.github.rvesse.airline.model.GlobalMetadata;

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
public class GenerateMojo extends AbstractAirlineOutputMojo {

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

        // See how many of each type of output we have
        int commandOutputs, groupOutputs, cliOutputs;
        commandOutputs = groupOutputs = cliOutputs = 0;
        for (PreparedSource source : sources) {
            if (source.shouldOutputCommandHelp()) {
                commandOutputs++;
            } else if (source.shouldOutputGroupHelp()) {
                groupOutputs++;
            } else if (source.shouldOutputGlobalHelp()) {
                cliOutputs++;
            }
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
                log.warn(String.format("Format %s is unknown and was skipped", format));
                continue;
            }
            FormatOptions options = mappedOptions.get(format);
            if (options == null)
                options = defaultOptions;
            if (options != defaultOptions)
                log.debug(String.format("Format %s format options are %s", format, options));

            if (commandOutputs > 0) {
                // Command outputs
                CommandUsageGenerator commandGenerator = provider.getCommandGenerator(this.outputDirectory, options);
                if (commandGenerator == null) {
                    if (failOnUnsupportedOutputMode)
                        throw new MojoFailureException(
                                String.format("Command help is not supported by format %s", format));
                    log.warn("Command help is not supported by format " + format);
                } else {
                    log.info(String.format("Using command help generator %s for format %s",
                            commandGenerator.getClass(), format));

                    // Generate command help
                    for (PreparedSource source : sources) {
                        FormatOptions sourceOptions = source.getFormatOptions(options);
                        CommandUsageGenerator sourceCommandGenerator = commandGenerator;
                        if (source.isCommand()) {
                            if (!source.shouldOutputCommandHelp()) {
                                log.info(String.format(
                                        "Skipping command help for %s because configured output mode is %s",
                                        source.getSourceClass(), source.getOutputMode()));
                                continue;
                            }

                            if (sourceOptions != options) {
                                // Source specific options and thus potentially
                                // generator
                                sourceCommandGenerator = prepareCommandGenerator(provider, source, sourceOptions);
                            }
                            
                            outputCommandHelp(format, provider, sourceOptions, sourceCommandGenerator, source);
                        } else if (source.isGlobal()) {
                            if (!source.shouldOutputCommandHelp()) {
                                log.info(String.format(
                                        "Skipping command help for %s because configured output mode is %s",
                                        source.getSourceClass(), source.getOutputMode()));
                                continue;
                            }
                            log.info(String.format("Generating command help for all commands provided by CLI %s",
                                    source.getSourceClass()));

                            if (sourceOptions != options) {
                                sourceCommandGenerator = prepareCommandGenerator(provider, source, sourceOptions);
                            }

                            // Firstly dump the default commands group and then
                            // dump
                            // the command groups
                            GlobalMetadata<Object> global = source.getGlobal();
                            outputCommandsInGroup(format, provider, sourceOptions, sourceCommandGenerator, source,
                                    global.getDefaultGroupCommands(), global.getParserConfiguration(), global.getName(),
                                    (String[]) null);
                            for (CommandGroupMetadata group : global.getCommandGroups()) {
                                if (group.isHidden() && !sourceOptions.includeHidden())
                                    continue;
                                
                                outputGroupCommandsHelp(format, provider, sourceOptions, sourceCommandGenerator, source,
                                        group, global.getParserConfiguration(), global.getName(), (String[]) null);
                            }
                        }
                    }
                }
            } else {
                log.info("Skipping command help as no configured sources were commands or had their output mode set to COMMAND");
            }

            if (groupOutputs > 0) {
                // Group Outputs
                CommandGroupUsageGenerator<Object> groupGenerator = provider.getGroupGenerator(this.outputDirectory,
                        options);
                if (groupGenerator == null) {
                    if (failOnUnsupportedOutputMode)
                        throw new MojoFailureException(
                                String.format("Group help is not supported by format %s", format));
                    log.warn("Group help is not supported by format " + format);
                } else {
                    log.info(String.format("Using group help generator %s for format %s", groupGenerator.getClass(),
                            format));

                    // Generate group help
                    for (PreparedSource source : sources) {
                        if (source.isCommand())
                            continue;

                        if (source.isGlobal()) {
                            if (!source.shouldOutputGroupHelp()) {
                                log.info(
                                        String.format("Skipping group help for %s because configured output mode is %s",
                                                source.getSourceClass(), source.getOutputMode()));
                                continue;
                            }
                            CommandGroupUsageGenerator<Object> sourceGroupGenerator = groupGenerator;
                            FormatOptions sourceOptions = source.getFormatOptions(options);
                            if (sourceOptions != options) {
                                sourceGroupGenerator = prepareCommandGroupUsageGenerator(provider, source,
                                        sourceOptions);
                            }

                            GlobalMetadata<Object> global = source.getGlobal();
                            for (CommandGroupMetadata group : global.getCommandGroups()) {
                                outputGroupsHelp(format, provider, sourceOptions, sourceGroupGenerator, source,
                                        new CommandGroupMetadata[] { group }, global.getParserConfiguration(),
                                        global.getName());
                            }
                        }
                    }
                }
            } else {
                log.info("Skipping group help as no configured sources had their output mode set to GROUP");
            }

            if (cliOutputs > 0) {
                // CLI outputs
                GlobalUsageGenerator<Object> globalGenerator = provider.getGlobalGenerator(this.outputDirectory,
                        options);
                if (globalGenerator == null) {
                    if (failOnUnsupportedOutputMode)
                        throw new MojoFailureException(String.format("CLI help is not supported by format %s", format));
                    log.warn("CLI help is not supported by format " + format);
                } else {
                    log.info(String.format("Using CLI help generator %s for format %s", globalGenerator.getClass(),
                            format));

                    // Generate global help
                    for (PreparedSource source : sources) {
                        if (!source.isGlobal())
                            continue;

                        if (!source.shouldOutputGlobalHelp()) {
                            log.info(String.format("Skipping global help for %s because configured output mode is %s",
                                    source.getSourceClass(), source.getOutputMode()));
                            continue;
                        }

                        GlobalUsageGenerator<Object> sourceGlobalGenerator = globalGenerator;
                        FormatOptions sourceOptions = source.getFormatOptions(options);
                        if (sourceOptions != options) {
                            globalGenerator = prepareGlobalUsageGenerator(provider, source, sourceOptions);
                        }

                        outputGlobalHelp(format, provider, sourceOptions, sourceGlobalGenerator, source);
                    }
                }
            } else {
                log.info("Skipping command help as no configured sources were CLIs");
            }

        }
    }
}
