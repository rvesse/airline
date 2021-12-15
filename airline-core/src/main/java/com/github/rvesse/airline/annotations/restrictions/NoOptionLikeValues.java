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
package com.github.rvesse.airline.annotations.restrictions;

import static java.lang.annotation.ElementType.FIELD;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * An annotation that marks that values provided for options/arguments musn't look like possible options
 * 
 * @author rvesse
 *
 */
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target({ FIELD })
public @interface NoOptionLikeValues {

    /**
     * Indicates the prefixes that will be used to detect values that look like options. If a value starts with
     * <strong>any</strong> of these prefixes then it will be rejected.
     * <p>
     * If not specified then the default prefixes are {@code -} and {@code --}
     * </p>
     * 
     * @return Option Prefixes
     */
    String[] optionPrefixes() default { "-", "--" };
}
