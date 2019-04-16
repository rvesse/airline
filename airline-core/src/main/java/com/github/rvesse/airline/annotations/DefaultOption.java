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
package com.github.rvesse.airline.annotations;

import static java.lang.annotation.ElementType.FIELD;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Annotation to mark a field as the default option
 * <p>
 * This means that supplying the name of the associated option is optional since
 * any non-option input seen will be treating as supplying a value for the
 * associated option.
 * </p>
 * <p>
 * This annotation can only be used <strong>once</strong> on a command field
 * provided the following requirements are met:
 * </p>
 * <ul>
 * <li>The field is also annotated with {@link Option}</li>
 * <li>The {@linkplain Option} annotation has an arity of 1</li>
 * <li>The {@linkplain Option} annotation has a type of
 * {@link OptionType#COMMAND}</li>
 * <li>The command does not have any field annotated with {@link Arguments} or
 * {@link PositionalArgument} as otherwise it would be ambiguous as to whether a
 * non-option value should be parsed as an argument or as an option value</li>
 * </ul>
 *
 */
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target({ FIELD })
@Documented
public @interface DefaultOption {

}
