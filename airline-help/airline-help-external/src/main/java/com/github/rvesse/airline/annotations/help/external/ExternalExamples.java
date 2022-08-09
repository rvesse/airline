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

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.github.rvesse.airline.help.external.parsers.ParagraphsParser;
import com.github.rvesse.airline.help.external.parsers.defaults.DefaultExternalHelpParser;
import com.github.rvesse.airline.parser.resources.ClasspathLocator;
import com.github.rvesse.airline.parser.resources.FileLocator;
import com.github.rvesse.airline.parser.resources.ResourceLocator;

/**
 * Annotation that provides an examples section for a commands help via two external resources in textual format
 */
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target({ TYPE })
@Documented
public @interface ExternalExamples {

    /**
     * Source containing the examples
     *
     * @return Example source
     */
    String exampleSource();

    /**
     * Source containing the descriptions of the examples
     *
     * @return Description source
     */
    String descriptionSource();

    /**
     * Resource locators used to find the resources specified in {@link #exampleSource()} and {@link #descriptionSource()}
     *
     * @return Resource locators to use
     */
    Class<? extends ResourceLocator>[] sourceLocators() default {
            ClasspathLocator.class,
            FileLocator.class
    };

    /**
     * The parser to use to translate the source specified by {@link #exampleSource()} and {@link #descriptionSource()}
     * into paragraphs
     *
     * @return Paragraphs parser
     */
    Class<? extends ParagraphsParser> parser() default DefaultExternalHelpParser.class;

}
