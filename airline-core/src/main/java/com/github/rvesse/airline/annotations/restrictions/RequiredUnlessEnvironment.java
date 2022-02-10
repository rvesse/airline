/**
 * Copyright (C) 2010-16 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.github.rvesse.airline.annotations.restrictions;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;

/**
 * An annotation that indicates that an option/arguments is required <strong>UNLESS</strong> a specific environment
 * variable is set.
 * <p>
 * This is intended for situations where your options are being populated by default values taken from environment
 * variables and you only require the option if a suitable default is not already provided by the environment.
 * </p>
 * <p>
 * If you have other requirement criteria then you may wish to use {@link Required}, {@link RequiredOnlyIf}, {@link
 * RequireSome} or {@link RequireOnlyOne} instead.
 * </p>
 */
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target({FIELD})
public @interface RequiredUnlessEnvironment {
    /**
     * Specifies the name(s) of the environment variable that when present renders the option not required
     *
     * @return Name(s) of the environment variables
     */
    public String[] variables();
}
