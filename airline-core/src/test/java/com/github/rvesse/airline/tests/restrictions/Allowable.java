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
package com.github.rvesse.airline.tests.restrictions;

import java.util.concurrent.TimeUnit;

import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.Option;
import com.github.rvesse.airline.annotations.restrictions.AllowedEnumValues;
import com.github.rvesse.airline.annotations.restrictions.AllowedRawValues;
import com.github.rvesse.airline.annotations.restrictions.AllowedValues;

@Command(name = "allowable")
public class Allowable {

    @Option(name = "--raw")
    @AllowedRawValues(allowedValues = { "foo", "bar", "faz" })
    public String raw;
    
    @Option(name = "--typed")
    @AllowedValues(allowedValues = { "1", "2", "3" })
    public double typed;
    
    @Option(name = "--enum")
    @AllowedEnumValues(TimeUnit.class)
    public TimeUnit enumTyped;
    
    @Option(name = "--case")
    @AllowedRawValues(allowedValues = { "UPPER", "lower", "MixedCase" }, ignoreCase = true)
    public String caseInsensitive;
}
