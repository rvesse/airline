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

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;

/**
 * Annotation that provides a discussion section for a commands help
 * 
 * @author rvesse
 *
 */
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target({ TYPE })
@Documented
public @interface Discussion {

    /**
     * An array of paragraphs of text that provides an extended discussion on
     * the behaviour of the command. Discussion typically goes into much greater
     * detail about exact behaviour of commands particularly where complex
     * commands may behave differently depending on the option combinations
     * used.
     * 
     * @return Command discussion
     */
    String[]paragraphs() default {};
}
