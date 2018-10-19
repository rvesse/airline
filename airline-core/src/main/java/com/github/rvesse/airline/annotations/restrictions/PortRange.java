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
 * Annotation that indicates that an option/argument denotes a port number and
 * its value should be restricted as such to a specific set of ports. If you
 * just want to restrict to a predefined port type you should use {@link @Port}
 * instead, or if you need multiple ranges then use {@link @PortRanges}.
 */
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target({ FIELD })
public @interface PortRange {

    /**
     * Sets the minimum port (inclusive)
     * 
     * @return Minimum port
     */
    int minimum();

    /**
     * Sets the maximum port (inclusive)
     * 
     * @return Maximum port
     */
    int maximum();
}
