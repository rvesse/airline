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
package com.github.rvesse.airline.annotations.help.external;

import com.github.rvesse.airline.help.external.parsers.ParagraphsParser;
import com.github.rvesse.airline.help.external.parsers.defaults.DefaultExternalHelpParser;
import com.github.rvesse.airline.parser.resources.ClasspathLocator;
import com.github.rvesse.airline.parser.resources.FileLocator;
import com.github.rvesse.airline.parser.resources.ModulePathLocator;
import com.github.rvesse.airline.parser.resources.ResourceLocator;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Defines a prose section where the prose content is provided in a text file on the classpath
 *
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface ExternalProse {

    /**
     * Title of the section
     *
     * @return Title
     */
    String title();

    /**
     * Source containing the prose content
     *
     * @return Source
     */
    String source();

    /**
     * Suggested order in which the help section should be placed relative to other help sections
     * <p>
     * Values less than zero will typically place the section before the standard sections while values greater than or
     * equal to zero will place the section after the standard sections.
     * </p>
     *
     * @return Suggested order
     */
    int suggestedOrder() default 0;

    /**
     * Resource locators used to find the resources specified in {@link #source()}
     *
     * @return Resource locators to use
     */
    Class<? extends ResourceLocator>[] sourceLocators() default {
            ClasspathLocator.class,
            ModulePathLocator.class,
            FileLocator.class
    };

    /**
     * The parser to use to translate the source specified by {@link #source()} into paragraphs
     *
     * @return Paragraph parser
     */
    Class<? extends ParagraphsParser> parser() default DefaultExternalHelpParser.class;
}
