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
 * Interface for classes that represents additional help sections
 */
public interface HelpSection extends HelpHint {

    /**
     * Gets the title for the section
     * 
     * @return Section title
     */
    String getTitle();

    /**
     * Gets the post-amble text that should be included at the end of the
     * section
     * 
     * @return
     */
    String getPostamble();

    /**
     * Gets an integer indicating the suggested order that a help generator
     * should include this section in
     * <p>
     * A value of zero indicates that the help generator can decide where to
     * place this section, a positive value indicates it should be placed after
     * the default sections while a negative value indicates it should be placed
     * before the default sections. There is however no guarantee that a help
     * generator will honour the order given.
     * </p>
     * 
     * @return Suggested order
     */
    int suggestedOrder();

}
