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
package com.github.rvesse.airline.help.sections.common;

/**
 * Constants related to common help sections
 *
 */
public class CommonSections {

    /**
     * Constants defining the relative order of some commonly used help sections
     */
    //@formatter:off
    public static final int ORDER_DISCUSSION = 10,
                            ORDER_EXAMPLES = ORDER_DISCUSSION + 10,
                            ORDER_EXIT_CODES = ORDER_EXAMPLES + 10,
                            ORDER_VERSION = ORDER_EXIT_CODES + 10,
                            ORDER_COPYRIGHT = ORDER_VERSION + 10,
                            ORDER_LICENSE = ORDER_COPYRIGHT + 10,
                            ORDER_SEE_ALSO = ORDER_LICENSE + 10;
    //@formatter:on
    
    /**
     * Constants defining the titles of some commonly used help sections
     */
    //@formatter:off
    public static final String TITLE_EXAMPLES = "Examples",
                               TITLE_DISCUSSION = "Discussion",
                               TITLE_EXIT_CODES = "Exit Codes",
                               TITLE_COPYRIGHT = "Copyright",
                               TITLE_LICENSE = "License",
                               TITLE_VERSION = "Version",
                               TITLE_SEE_ALSO = "See Also";
    //@formatter:on
}
