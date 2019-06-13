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

package com.github.rvesse.airline.prompts;

import java.io.PrintWriter;

/**
 * A provider of prompting
 *
 */
public interface PromptProvider {

    /**
     * Gets the writer to which prompts should be written
     * 
     * @return Prompt writer
     */
    public PrintWriter getPromptStream();

    /**
     * Reads a line from the prompts source
     * 
     * @return Line (or {@code null} if unable to read a line)
     */
    public String readLine();

    /**
     * Reads a single key from the prompts source
     * 
     * @return Key code (or -1 if unable to read a key)
     */
    public int readKey();

    /**
     * Reads a line from the prompts source in a secure manner
     * 
     * @return Line (or {@code null} if unable to read a line)
     */
    public char[] readSecureLine();

    /**
     * Whether the provider supports secured reads
     * 
     * @return True if supported, false otherwise
     */
    public boolean supportsSecureReads();
}
