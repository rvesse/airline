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
package com.github.rvesse.airline.annotations.help;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Properties;

import com.github.rvesse.airline.parser.resources.ClasspathLocator;
import com.github.rvesse.airline.parser.resources.FileLocator;
import com.github.rvesse.airline.parser.resources.ResourceLocator;

@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target({ TYPE })
public @interface Version {
    /**
     * Sets paths to resources that provide the version information
     * <p>
     * When this annotation is converted into a help section these are the files
     * that are scanned for the version information used to produce the Version
     * help section. Files are located using the resource locators specified
     * by {@link #sourceLocators()} which by default looks for resources on the
     * classpath then as files. You can add the prefixes {@code classpath:} or
     * {@code file://} to your source paths to force a specific location to be used
     * </p>
     * <p>
     * Each source will be provided as a separate list/table row within the help
     * section depending on the setting of the {@link #tabular()} field.
     * </p>
     * <p>
     * Sources are loaded using {@link Properties#load(java.io.InputStream)}
     * which supports either normal Java properties format (key value pairs
     * separated by {@code =} with one per line) or Java Manifest files.
     * </p>
     * 
     * @return Sources for version information
     */
    String[] sources();

    /**
     * Sets the property used to find the component information (if any)
     * 
     * @return Component property
     */
    String componentProperty() default "component";

    /**
     * Sets the property used to find version information (if any)
     * 
     * @return Version property
     */
    String versionProperty() default "version";

    /**
     * Sets the property used to find build information (if any)
     * 
     * @return Build property
     */
    String buildProperty() default "build";

    /**
     * Sets the property used to find build date information (if any)
     * 
     * @return Build Date property
     */
    String dateProperty() default "buildDate";

    /**
     * Any additional properties from which information should be obtained
     * 
     * @return Additional properties
     */
    String[] additionalProperties() default {};

    /**
     * The titles for the additional properties specified by
     * {@link #additionalProperties()} used to present this information in
     * generated help output
     * 
     * @return Additional titles
     */
    String[] additionalTitles() default {};

    /**
     * Sets whether to suppress this help section if there is an error obtaining
     * the version information.
     * <p>
     * When set to {@code false} a runtime error will be thrown if this
     * annotation is used and the version information cannot be successfully
     * obtained. When set to {@code true} then if the version information cannot
     * be obtained the help section will either be suppressed if no information
     * was found or if some information was found then partial information is
     * shown.
     * </p>
     * 
     * @return True if errors in obtaining version information are suppressed
     */
    boolean suppressOnError();

    /**
     * Whether to display the version information in a tabular format
     * 
     * @return True if tabular format, false for list format
     */
    boolean tabular() default false;

    /**
     * Resource locators used to find the properties files specified in
     * {@link #sources()}
     * 
     * @return Resource locators to use
     */
    Class<? extends ResourceLocator>[] sourceLocators() default { ClasspathLocator.class, FileLocator.class };
}
