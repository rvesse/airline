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

/**
 * Helper class used to represent the result of an attempted conversion. Primarily used internally by
 * {@link DefaultTypeConverter} but may be generally useful for implementing custom {@link TypeConverter}
 * implementations or extending the {@linkplain DefaultTypeConverter}
 *
 */
public class ConvertResult {
    private final Object value;
    private final boolean success;
    private final Throwable cause;

    /**
     * Special constant instance used to indicate failure
     */
    public static final ConvertResult FAILURE = new ConvertResult();

    /**
     * Creates a basic convert result that indicates failure
     */
    private ConvertResult() {
        this.value = null;
        this.success = false;
        this.cause = null;
    }

    /**
     * Creates a new conversion result that indicates success
     *
     * @param value
     *            Converted value
     */
    public ConvertResult(Object value) {
        this.value = value;
        this.success = true;
        this.cause = null;
    }

    /**
     * Creates a new conversion result that indicates failure
     *
     * @param cause
     *            Cause of the failure
     */
    public ConvertResult(Throwable cause) {
        this.value = null;
        this.success = false;
        this.cause = cause;
    }

    /**
     * Whether the conversion was successful
     *
     * @return True if successful, false otherwise
     */
    public boolean wasSuccessful() {
        return this.success;
    }

    /**
     * Whether the conversion was successful
     *
     * @return True if successful, false otherwise
     * @deprecated Use correctly spelled overload {@link #wasSuccessful()} instead
     */
    @Deprecated
    public boolean wasSuccessfull() {
        return this.success;
    }

    /**
     * The converted value
     *
     * @return Converted value
     */
    public Object getConvertedValue() {
        return this.value;
    }

    /**
     * Gets whether a cause is available
     *
     * @return Cause
     */
    public boolean hasCause() {
        return this.cause != null;
    }

    /**
     * Gets the cause if available
     *
     * @return Cause, or {@code null} if none available
     */
    public Throwable getCause() {
        return this.cause;
    }
}