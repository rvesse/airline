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

import com.github.rvesse.airline.help.external.parsers.TabularParser;
import com.github.rvesse.airline.help.external.parsers.defaults.DefaultExternalHelpParser;
import com.github.rvesse.airline.parser.resources.ClasspathLocator;
import com.github.rvesse.airline.parser.resources.FileLocator;
import com.github.rvesse.airline.parser.resources.ModulePathLocator;
import com.github.rvesse.airline.parser.resources.ResourceLocator;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;

/**
 * Annotation that provides an examples section for a commands help via a single external resource in tabular format
 */
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target({ TYPE })
@Documented
public @interface ExternalTabularExamples {

    /**
     * Source containing the examples and their descriptions in tabular format
     *
     * @return Source
     */
    String source();

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
     * The parser to use to translate the source specified by {@link #source()} into tabular data
     *
     * @return Tabular parser
     */
    Class<? extends TabularParser> parser() default DefaultExternalHelpParser.class;

}
