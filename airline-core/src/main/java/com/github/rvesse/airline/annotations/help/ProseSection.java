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

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Annotation that provides a prose section for a commands help
 * 
 * @author rvesse
 *
 */
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target({ TYPE })
@Documented
public @interface ProseSection {

    /**
     * Title of the section
     * 
     * @return Title
     */
    String title();

    /**
     * An array of paragraphs of text that provides prose for the help section
     * 
     * @return Paragraphs
     */
    String[] paragraphs() default {};

    /**
     * Suggested order in which the help section should be placed relative to
     * other help sections
     * <p>
     * Values less than zero will typically place the section before the
     * standard sections while values greater than or equal to zero will place
     * the section after the standard sections.
     * </p>
     * 
     * @return Suggested order
     */
    int suggestedOrder() default 0;
}
