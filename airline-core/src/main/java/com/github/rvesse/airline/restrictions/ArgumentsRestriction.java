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
package com.github.rvesse.airline.restrictions;

import com.github.rvesse.airline.model.ArgumentsMetadata;
import com.github.rvesse.airline.model.PositionalArgumentMetadata;
import com.github.rvesse.airline.parser.ParseState;

/**
 * Represents restrictions on arguments
 * 
 * @author rvesse
 *
 */
public interface ArgumentsRestriction {

    /**
     * Method that is called before Airline attempts to convert a string
     * argument received into a strongly typed Java value
     * 
     * @param state
     *            Parser state
     * @param arguments
     *            Arguments meta-data
     * @param value
     *            String value
     */
    public abstract <T> void preValidate(ParseState<T> state, ArgumentsMetadata arguments, String value);

    /**
     * Method that is called after Airline has converted a string argument
     * received into a strongly typed Java value
     * 
     * @param state
     *            Parser state
     * @param arguments
     *            Arguments meta-data
     * @param value
     *            Strongly typed value
     */
    public abstract <T> void postValidate(ParseState<T> state, ArgumentsMetadata arguments, Object value);

    /**
     * Method that is called after Airline has completed parsing
     * <p>
     * This can be used to implement restrictions that require the final parser
     * state to process
     * </p>
     * 
     * @param state
     *            Parser state
     * @param arguments
     *            Arguments meta-data
     */
    public abstract <T> void finalValidate(ParseState<T> state, ArgumentsMetadata arguments);
    
    /**
     * Method that is called before Airline attempts to convert a string
     * argument received into a strongly typed Java value
     * 
     * @param state
     *            Parser state
     * @param arguments
     *            Arguments meta-data
     * @param value
     *            String value
     */
    public abstract <T> void preValidate(ParseState<T> state, PositionalArgumentMetadata arguments, String value);

    /**
     * Method that is called after Airline has converted a string argument
     * received into a strongly typed Java value
     * 
     * @param state
     *            Parser state
     * @param arguments
     *            Arguments meta-data
     * @param value
     *            Strongly typed value
     */
    public abstract <T> void postValidate(ParseState<T> state, PositionalArgumentMetadata arguments, Object value);

    /**
     * Method that is called after Airline has completed parsing
     * <p>
     * This can be used to implement restrictions that require the final parser
     * state to process
     * </p>
     * 
     * @param state
     *            Parser state
     * @param arguments
     *            Arguments meta-data
     */
    public abstract <T> void finalValidate(ParseState<T> state, PositionalArgumentMetadata arguments);
}