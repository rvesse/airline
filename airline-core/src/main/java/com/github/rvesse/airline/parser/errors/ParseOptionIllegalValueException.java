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
package com.github.rvesse.airline.parser.errors;

import java.util.Set;

import com.github.rvesse.airline.utils.AirlineUtils;

/**
 * Exception thrown when the value for an option is not in a specific set of allowed values
 */
public class ParseOptionIllegalValueException extends ParseRestrictionViolatedException {
    private static final long serialVersionUID = 810812151673279427L;

    private final String optionName, optionTitle;
    private final Object illegalValue;
    private final Set<Object> allowedValues;

    public ParseOptionIllegalValueException(String optionName, String optionTitle, Object value,
            Set<Object> allowedValues) {
        super("Value for option %s value '%s' was given as '%s' which is not in the list of allowed values: %s",
                optionName, optionTitle, value, allowedValues);
        this.optionName = optionName;
        this.optionTitle = optionTitle;
        this.illegalValue = value;
        this.allowedValues = AirlineUtils.unmodifiableSetCopy(allowedValues);
    }

    /**
     * Gets the option name i.e. the flag used to specify this option
     * 
     * @return Option name
     */
    public String getOptionName() {
        return optionName;
    }

    /**
     * Gets the option value title
     * 
     * @return Option title
     */
    public String getOptionTitle() {
        return optionTitle;
    }

    /**
     * Gets the illegal value
     * 
     * @return Illegal value
     */
    public Object getIllegalValue() {
        return illegalValue;
    }

    /**
     * Gets the set of allowed values
     * 
     * @return Allowed values
     */
    public Set<Object> getAllowedValues() {
        return allowedValues;
    }
}
