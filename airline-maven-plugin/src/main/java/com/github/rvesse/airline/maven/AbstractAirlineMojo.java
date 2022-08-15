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

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.classworlds.realm.ClassRealm;

import com.github.rvesse.airline.maven.sources.PreparedSource;

public abstract class AbstractAirlineMojo extends AbstractMojo {

    @Parameter(defaultValue = "${plugin}", readonly = true)
    protected PluginDescriptor pluginDescriptor;

    @Parameter(defaultValue = "${project}", readonly = true)
    protected MavenProject project;

    @Parameter
    protected List<Source> sources;

    @Parameter(defaultValue = "true")
    protected boolean failOnNoSources = true;

    public AbstractAirlineMojo() {
        super();
    }

    /**
     * Prepares the sources for which help will be generated
     * 
     * @param skipBadSources
     *            True if bad sources should be skipped and ignored
     * 
     * @return Prepared sources
     * @throws MojoFailureException
     *             Thrown if sources cannot be successfully prepared
     */
    protected List<PreparedSource> prepareSources(boolean skipBadSources) throws MojoFailureException {
        List<PreparedSource> prepared = new ArrayList<>();
        Log log = getLog();
        if (this.sources != null) {
            for (Source source : this.sources) {
                prepared.addAll(source.prepare(log, skipBadSources));
            }
        }
        if (prepared.size() == 0) {
            if (failOnNoSources)
                throw new MojoFailureException(
                        "Failed to locate any valid @Cli or @Command annotated classes to generate help for");
        }
        return prepared;
    }

    /**
     * Prepares the class realm failing the build if unable to do so
     * 
     * @throws MojoFailureException
     *             Thrown if the class realm cannot be successfully prepared
     */
    protected void prepareClassRealm() throws MojoFailureException {
        try {
            ClassRealm realm = pluginDescriptor.getClassRealm();

            Set<String> processed = new HashSet<>();
            List<String> compileClasspathElements = project.getCompileClasspathElements();
            processClasspathElements(realm, processed, compileClasspathElements);
            processClasspathElements(realm, processed, project.getRuntimeClasspathElements());
        } catch (DependencyResolutionRequiredException e) {
            throw new MojoFailureException("Failed to resolve dependencies", e);
        }
    }

    private void processClasspathElements(ClassRealm realm, Set<String> processed,
            List<String> compileClasspathElements) {
        for (String element : compileClasspathElements) {

            File elementFile = new File(element);
            try {
                realm.addURL(elementFile.toURI().toURL());
            } catch (MalformedURLException e) {
                getLog().warn(String.format("Failed to resolve classpath element %s", element));
            }

            processed.add(element);
        }
    }

}