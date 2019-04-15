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

import com.github.rvesse.airline.model.PositionalArgumentMetadata;
import com.github.rvesse.airline.model.ArgumentsMetadata;
import com.github.rvesse.airline.model.OptionMetadata;
import com.github.rvesse.airline.parser.ParseState;

/**
 * Interface for type converter providers
 * 
 * @author rvesse
 *
 */
public interface TypeConverterProvider {

    /**
     * Gets the type converter to use for the given option and parser state
     * 
     * @param option
     *            Option
     * @param state
     *            Parser state
     * @return Type converter
     */
    public abstract <T> TypeConverter getTypeConverter(OptionMetadata option, ParseState<T> state);

    /**
     * Gets the type converter to use for the given arguments and parser state
     * 
     * @param arguments
     *            Arguments
     * @param state
     *            Parser state
     * @return Type converter
     */
    public abstract <T> TypeConverter getTypeConverter(ArgumentsMetadata arguments, ParseState<T> state);

    /**
     * Gets the type converter to use for the given positional argument and
     * parser state
     * 
     * @param argumentMetadata
     *            Positional argument metadata
     * @param parseState
     *            Parser state
     * @return Type converter
     */
    public abstract <T> TypeConverter getTypeConverter(PositionalArgumentMetadata argumentMetadata, ParseState<T> parseState);
}
