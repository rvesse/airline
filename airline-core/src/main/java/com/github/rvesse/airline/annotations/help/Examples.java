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
 * Annotation that provides an examples section for a commands help
 * @author rvesse
 *
 */
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target({ TYPE })
@Documented
public @interface Examples {

    /**
     * An array of lines of text to provide a series of example usages of the
     * command. Each example may be provided with a description using the
     * {@link #descriptions()} property. The data in these two properties is
     * collated based on array indices.
     * 
     * @return Examples
     */
    String[] examples() default {};

    /**
     * An array of paragraphs of text where each paragraph described the
     * corresponding example given in the {@link #examples()} property. The data
     * in these two properties is collated based on array indices.
     * 
     * @return Descriptions
     */
    String[] descriptions() default {};
}
