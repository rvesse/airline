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

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Parameter;

import com.github.rvesse.airline.annotations.Cli;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.maven.sources.PreparedSource;
import com.github.rvesse.airline.model.MetadataLoader;

public class Source {

    /**
     * Classes to produce help for, each must be annotated with either
     * {@link Cli} or {@link Command}
     */
    @Parameter(required = true)
    private List<String> classes;

    @Parameter(defaultValue = "DEFAULT")
    private OutputMode outputMode = OutputMode.DEFAULT;

    /**
     * Provides source specific formatting options that will inherit from the
     * default and format specific options
     */
    @Parameter
    private RawFormatOptions options;

    public List<PreparedSource> prepare(Log log, boolean skipBadSources) throws MojoFailureException {
        List<PreparedSource> prepared = new ArrayList<>();
        for (String className : this.classes) {
            try {
                Class<?> cls = getClass().getClassLoader().loadClass(className);
                if (cls.getAnnotation(Command.class) != null) {
                    prepared.add(new PreparedSource(cls, null, MetadataLoader.loadCommand(cls, MetadataLoader.loadParser(cls)), this.options,
                            this.outputMode));
                } else if (cls.getAnnotation(Cli.class) != null) {
                    prepared.add(new PreparedSource(cls, MetadataLoader.loadGlobal(cls), null, this.options,
                            this.outputMode));
                } else {
                    if (!skipBadSources)
                        throw new MojoFailureException(
                                String.format("Class %s is not annotated with @Cli or @Command", className));
                    log.warn(String.format("Class %s is not annotated with @Cli or @Command", className));

                }
            } catch (MojoFailureException e) {
                // NB - If we get this then we aren't skipping bad sources so no need to check again
                throw e;
            } catch (ClassNotFoundException e) {
                if (!skipBadSources)
                    throw new MojoFailureException(String.format("Failed to locate class %s", className), e);
                log.warn(String.format("Failed to locate class %s", className));
            } catch (Throwable e) {
                if (!skipBadSources)
                    throw new MojoFailureException(String.format("Bad Airline metadata on class %s", className), e);
                log.warn(String.format("Bad Airline metadata on class %s", className));
            }
        }
        return prepared;
    }
}
