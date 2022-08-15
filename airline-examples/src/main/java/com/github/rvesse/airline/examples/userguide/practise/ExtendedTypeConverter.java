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
package com.github.rvesse.airline.examples.userguide.practise;

import com.github.rvesse.airline.types.ConvertResult;
import com.github.rvesse.airline.types.DefaultTypeConverter;

/**
 * An example of an extended type converter that adds support for converting
 * from types that provide an {@code parse(String)} method
 *
 */
public class ExtendedTypeConverter extends DefaultTypeConverter {

    @Override
    public Object convert(String name, Class<?> type, String value) {
        checkArguments(name, type, value);

        // Try and convert from a parse(String) method
        ConvertResult result = this.tryConvertStringMethod(name, type, value, "parse");
        if (result.wasSuccessful())
            return result.getConvertedValue();

        // Fall back to default behaviour otherwise
        return super.convert(name, type, value);
    }
}
