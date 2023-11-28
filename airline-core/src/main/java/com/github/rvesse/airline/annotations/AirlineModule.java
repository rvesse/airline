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

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Used to mark a field of a command class as representing a module of command functionality.  For fields marked with
 * this annotation Airline will inspect their value type in order to find further {@link Option} or {@link Arguments}
 * annotations that may be present on its fields.  This allows composing common functionality into commands without
 * using inheritance.  See <a href="http://rvesse.github.io/airline/guide/practise/oop.html">Inheritance and
 * Composition</a> in the User Guide for more details.
 * <p>
 * Historically Airline used the {@code @Inject} annotation for this purpose.  However in recent years dependency
 * injection has become much more widely used and {@code @Inject} is the standard annotation for that, this often
 * creates conflicts between Airline and the dependency injection frameworks.  Therefore from <strong>2.9.0</strong>
 * onwards we introduced this annotation as a replacement for it.
 * </p>
 * <p>
 * For backwards compatibility users can continue to use the {@code @Inject} annotation for the time being
 * <strong>BUT</strong> this will stop being the default behaviour in future releases and require manual configuration
 * to achieve.  See {@link Parser#compositionAnnotationClasses()} for how to configure this.
 * </p>
 */
@Target(FIELD)
@Retention(RUNTIME)
@Documented
public @interface AirlineModule {
}
