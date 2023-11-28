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
package com.github.rvesse.airline.annotations.help.bash;

import static java.lang.annotation.ElementType.FIELD;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.github.rvesse.airline.help.cli.bash.CompletionBehaviour;

/**
 * Annotates a field with Bash completion information
 *
 */
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target({ FIELD })
public @interface BashCompletion {

    /**
     * Sets the desired completion behaviour
     * <p>
     * This is used by usage generators that are capable of generating
     * completion scripts. It indicates any additional completion behaviour that
     * should apply in addition to {@link #command()} or other behaviours
     * obtained from the option/argument meta-data.
     * </p>
     * 
     * @return Completion Behaviours
     */
    CompletionBehaviour behaviour() default CompletionBehaviour.NONE;

    /**
     * Provides a command used to generate the completion words for this option
     * <p>
     * This is used by usage generators that are capable of generating
     * completion scripts.
     * </p>
     * 
     * @return Completion command
     */
    String command() default "";
}
