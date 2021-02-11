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

package com.github.rvesse.airline.help.external.parsers;

import java.io.InputStream;

/**
 * Parser that translates input into a series of paragraphs
 * <p>
 * A paragraph is a series of blocks of text separated by one/more blank lines.
 * </p>
 *
 */
public interface ParagraphsParser {

    /**
     * Parses input into a series of paragraphs
     * 
     * @param resource
     *            Resource name
     * @param input
     *            Input
     * @return Paragraphs
     */
    String[] parseParagraphs(String resource, InputStream input);

}