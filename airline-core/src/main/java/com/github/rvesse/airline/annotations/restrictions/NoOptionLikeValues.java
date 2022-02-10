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
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * An annotation that marks that values provided for options/arguments <strong>MUST NOT</strong> look like possible
 * options.
 * <p>
 * Remember that Airline parsers are generally conservative in that if they are trying to consume values and the next
 * value matches a known option they will produce an appropriate error.  However this does not protect against user
 * error, e.g. user making a typo in their options, in that case the value would still be consumed.  This can often lead
 * to unexpected behaviour because it can cause something the user expected to be an option to be treated as a value
 * meaning all subsequent arguments provided may also be misinterpreted.  Sometimes this can cause many parser errors to
 * be produced, none of which actually identified the root cause of the problems. Using this restriction prevents that
 * scenario since any value that looks like it could be an option will not be consumed and produce an appropriate error
 * instead.
 * </p>
 * <p>
 * This restriction can be used as either an Option/Argument level restriction, or as a Global restriction.  When used
 * as an Option/Argument level restriction it operates upon the raw values prior to their parsing into the appropriate
 * value type for the Option/Argument.  When used as a Global restriction it operates upon the typed values that have
 * been assigned to each parsed option.
 * </p>
 * <p>
 * This distinction can be quite important when options/arguments can legitimately take values that appear option like.
 * For example consider an option typed as {@link Integer}, users may legitimately expect to be able to pass in negative
 * numbers to this option.  If that option were to have this restriction applied directly to it any negative numbers
 * would be rejected since {@code -} is considered an option prefix.  However were this restriction instead applied at
 * the command/CLI level, i.e. made global, then negative numbers would be permitted because it would only consider the
 * typed values for the options.  As the typed value for the option is an {@link Integer} the restriction would not be
 * applied to it.
 * </p>
 *
 * @author rvesse
 */
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target({FIELD, TYPE})
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
    String[] optionPrefixes() default {"-", "--"};
}
