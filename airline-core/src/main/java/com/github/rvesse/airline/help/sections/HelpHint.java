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
package com.github.rvesse.airline.help.sections;

/**
 * Interface for classes that provide additional help information
 */
public interface HelpHint {

    /**
     * Gets the preamble text that should be included
     * 
     * @return Preamble text
     */
    String getPreamble();

    /**
     * Gets the format of the provided help information
     * 
     * @return Help format
     */
    HelpFormat getFormat();

    /**
     * Gets the number of content blocks provided
     * <p>
     * Help generators should consult the {@link #getFormat()} return value to
     * determine how to format the content blocks but they are not required to
     * do so
     * </p>
     * 
     * @return Number of content blocks
     */
    int numContentBlocks();

    /**
     * Gets the content block with the given number
     * 
     * @param blockNumber
     *            Block number
     * @return Content Block
     */
    String[] getContentBlock(int blockNumber);
}
