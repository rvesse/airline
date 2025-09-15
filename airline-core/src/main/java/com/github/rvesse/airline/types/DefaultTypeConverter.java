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
package com.github.rvesse.airline.types;

import com.github.rvesse.airline.parser.errors.ParseOptionConversionException;
import com.github.rvesse.airline.types.numerics.DefaultNumericConverter;
import com.github.rvesse.airline.types.numerics.NumericTypeConverter;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * The default type converter
 * <p>
 * This converter supports all the basic Java types plus types. Additionally it supports any class that defines a static
 * {@code fromString(String)} or {@code valueOf(String)} method. Finally it supports any class that defines a
 * constructor that takes a string.
 * </p>
 */
public class DefaultTypeConverter extends DefaultTypeConverterProvider implements TypeConverter {

    private NumericTypeConverter numericConverter;

    /**
     * Creates a new instance of the default type converter with the default {@link NumericTypeConverter} configured
     */
    public DefaultTypeConverter() {
        this(null);
    }

    /**
     * Creates a new instance of the default type converter
     *
     * @param numericConverter Numeric type converter to use, if {@code null} then {@link DefaultNumericConverter} is
     *                         used
     */
    public DefaultTypeConverter(NumericTypeConverter numericConverter) {
        this.numericConverter = numericConverter != null ? numericConverter : new DefaultNumericConverter();
    }

    @Override
    public Object convert(String name, Class<?> type, String value) {
        checkArguments(name, type, value);

        // Firstly try the standard Java types
        ConvertResult result = tryConvertBasicTypes(name, type, value);
        if (result.wasSuccessful()) {
            return result.getConvertedValue();
        }

        // Then look for a static fromString(String) method
        result = tryConvertFromString(name, type, value);
        if (result.wasSuccessful()) {
            return result.getConvertedValue();
        }

        // Then look for a static valueOf(String) method
        // This covers enums which have a valueOf method
        result = tryConvertFromValueOf(name, type, value);
        if (result.wasSuccessful()) {
            return result.getConvertedValue();
        }

        // Finally look for a constructor taking a string
        result = tryConvertStringConstructor(name, type, value);
        if (result.wasSuccessful()) {
            return result.getConvertedValue();
        }

        throw new ParseOptionConversionException(name, value, type.getSimpleName());
    }

    /**
     * Checks that the arguments are all non-null
     *
     * @param name  Option/Argument name
     * @param type  Target type
     * @param value String to convert
     */
    public static void checkArguments(String name, Class<?> type, String value) {
        if (name == null) {
            throw new NullPointerException("name is null");
        }
        if (type == null) {
            throw new NullPointerException("type is null");
        }
        if (value == null) {
            throw new NullPointerException("value is null");
        }
    }

    /**
     * Tries to convert the value by invoking a constructor that takes a string on the type
     * <p>
     * Considers two variants of the constructor, one which takes {@link String} as its parameter type and if that doesn't
     * exist one that takes {@link CharSequence} as it's parameter type
     * </p>
     *
     * @param type  Type
     * @param value value
     * @return Conversion result
     */
    protected final ConvertResult tryConvertStringConstructor(String name, Class<?> type, String value) {
        ConvertResult result = tryConvertConstructor(name, type, value, String.class);
        if (result.wasSuccessful()) {
            return result;
        } else {
            return tryConvertConstructor(name, type, value, CharSequence.class);
        }
    }

    /**
     * Tries to convert the value by invoking a constructor that takes a string on the type
     *
     * @param type  Type
     * @param value value
     * @return Conversion result
     */
    protected final <T> ConvertResult tryConvertConstructor(String name, Class<?> type, String value,
                                                            Class<T> parameterType) {
        try {
            Constructor<?> constructor = type.getConstructor(parameterType);
            return new ConvertResult(constructor.newInstance(value));
        } catch (Throwable ignored) {
        }
        return ConvertResult.FAILURE;
    }

    /**
     * Tries to convert the value by invoking a static {@code valueOf(String)} method on the type
     *
     * @param type  Type
     * @param value Value
     * @return Conversion result
     */
    protected final ConvertResult tryConvertFromValueOf(String name, Class<?> type, String value) {
        return tryConvertStringMethod(name, type, value, "valueOf");
    }

    /**
     * Tries to convert the value by invoking a static {@code fromString(String)} method on the type
     *
     * @param type  Type
     * @param value Value
     * @return Conversion result
     */
    protected final ConvertResult tryConvertFromString(String name, Class<?> type, String value) {
        return tryConvertStringMethod(name, type, value, "fromString");

    }

    /**
     * Tries to convert the value by invoking a static method on the
     * <p>
     * Considers two variants of the method, one which takes {@link String} as its parameter type and if that doesn't
     * exist one that takes {@link CharSequence} as it's parameter type
     * </p>
     *
     * @param type       Type
     * @param value      Value
     * @param methodName Name of the method to invoke
     * @return Conversion Result
     */
    @SuppressWarnings("unused")
    protected final ConvertResult tryConvertStringMethod(String name, Class<?> type, String value, String methodName) {
        ConvertResult result = tryConvertStringMethod(type, value, methodName, String.class);
        if (result.wasSuccessful()) {
            return result;
        } else {
            return tryConvertStringMethod(type, value, methodName, CharSequence.class);
        }
    }

    /**
     * Tries to convert the value by invoking a static method on the type
     *
     * @param type       Type
     * @param value      Value
     * @param methodName Name of the method to invoke
     * @return Conversion Result
     */
    protected final ConvertResult tryConvertStringMethod(Class<?> type, String value,
                                                         String methodName, Class<?> parameterType) {
        try {
            Method method = type.getMethod(methodName, parameterType);
            if (method.getReturnType().isAssignableFrom(type)) {
                return new ConvertResult(method.invoke(null, value));
            }
        } catch (Throwable ignored) {

        }
        return ConvertResult.FAILURE;
    }

    /**
     * Tries to convert the value if it is one of the common Java types
     *
     * @param type  Type
     * @param value Value
     * @return Conversion result
     */
    protected final ConvertResult tryConvertBasicTypes(String name, Class<?> type, String value) {
        try {
            if (String.class.isAssignableFrom(type)) {
                return new ConvertResult(value);
            } else if (Boolean.class.isAssignableFrom(type) || Boolean.TYPE.isAssignableFrom(type)) {
                return new ConvertResult(Boolean.valueOf(value));
            } else if (Path.class.isAssignableFrom(type)) {
                return new ConvertResult(Paths.get(value));
            } else if (this.numericConverter != null) {
                return this.numericConverter.tryConvertNumerics(name, type, value);
            }
        } catch (Exception ignored) {
        }
        return ConvertResult.FAILURE;
    }

    @Override
    public void setNumericConverter(NumericTypeConverter converter) {
        this.numericConverter = converter;
        if (this.numericConverter == null) {
            this.numericConverter = new DefaultNumericConverter();
        }
    }
}
