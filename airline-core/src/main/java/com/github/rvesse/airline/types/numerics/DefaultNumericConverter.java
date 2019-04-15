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
package com.github.rvesse.airline.types.numerics;

import com.github.rvesse.airline.model.PositionalArgumentMetadata;
import com.github.rvesse.airline.model.ArgumentsMetadata;
import com.github.rvesse.airline.model.OptionMetadata;
import com.github.rvesse.airline.parser.ParseState;
import com.github.rvesse.airline.types.ConvertResult;
import com.github.rvesse.airline.types.DefaultTypeConverter;
import com.github.rvesse.airline.types.TypeConverter;
import com.github.rvesse.airline.types.TypeConverterProvider;

public class DefaultNumericConverter implements TypeConverterProvider, NumericTypeConverter {

    @Override
    public <T> TypeConverter getTypeConverter(OptionMetadata option, ParseState<T> state) {
        return new DefaultTypeConverter(this);
    }

    @Override
    public <T> TypeConverter getTypeConverter(ArgumentsMetadata arguments, ParseState<T> state) {
        return new DefaultTypeConverter(this);
    }
    
    @Override
    public <T> TypeConverter getTypeConverter(PositionalArgumentMetadata argument, ParseState<T> state) {
        return new DefaultTypeConverter(this);
    }

    @Override
    public ConvertResult tryConvertNumerics(String name, Class<?> type, String value) {
        try {
            if (Byte.class.isAssignableFrom(type) || Byte.TYPE.isAssignableFrom(type)) {
                return tryConvertByte(name, value);
            } else if (Short.class.isAssignableFrom(type) || Short.TYPE.isAssignableFrom(type)) {
                return tryConvertShort(name, value);
            } else if (Integer.class.isAssignableFrom(type) || Integer.TYPE.isAssignableFrom(type)) {
                return tryConvertInteger(name, value);
            } else if (Long.class.isAssignableFrom(type) || Long.TYPE.isAssignableFrom(type)) {
                return tryConvertLong(name, value);
            } else if (Float.class.isAssignableFrom(type) || Float.TYPE.isAssignableFrom(type)) {
                return tryConvertFloat(name, value);
            } else if (Double.class.isAssignableFrom(type) || Double.TYPE.isAssignableFrom(type)) {
                return tryConvertDouble(name, value);
            }
        } catch (Exception ignored) {

        }
        return ConvertResult.FAILURE;
    }

    protected ConvertResult tryConvertDouble(String name, String value) {
        return new ConvertResult(Double.valueOf(value));
    }

    protected ConvertResult tryConvertFloat(String name, String value) {
        return new ConvertResult(Float.valueOf(value));
    }

    protected ConvertResult tryConvertLong(String name, String value) {
        return new ConvertResult(Long.valueOf(value));
    }

    protected ConvertResult tryConvertInteger(String name, String value) {
        return new ConvertResult(Integer.valueOf(value));
    }

    protected ConvertResult tryConvertShort(String name, String value) {
        return new ConvertResult(Short.valueOf(value));
    }

    protected ConvertResult tryConvertByte(String name, String value) {
        return new ConvertResult(Byte.valueOf(value));
    }
}
