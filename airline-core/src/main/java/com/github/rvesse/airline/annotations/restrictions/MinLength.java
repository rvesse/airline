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

import com.github.rvesse.airline.annotations.restrictions.ranges.LengthRange;

/**
 * Annotation that marks that an options value is restricted to a minimum length
 * (which is inclusive)
 * <p>
 * If you simply wish to require that an option have a non-empty value then use
 * {@link NotEmpty} instead. Alternatively for a range of lengths you can use
 * {@link LengthRange} or for an exact length you can use {@link ExactLength}.
 * </p>
 * 
 * @author rvesse
 *
 */
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target({ FIELD })
public @interface MinLength {

    /**
     * Minimum required length (inclusive)
     * 
     * @return Minimum length (inclusive)
     */
    public int length() default 0;
}
