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
package com.github.rvesse.airline.parser.errors;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.github.rvesse.airline.model.PositionalArgumentMetadata;

/**
 * Exception thrown when required arguments are missing
 * 
 */
public class ParseArgumentsMissingException extends ParseRestrictionViolatedException {
    private static final long serialVersionUID = 6220909299960264997L;
    
    private final List<String> argumentTitles;
    
    public ParseArgumentsMissingException(PositionalArgumentMetadata posArg) {
        super("Required positional argument %d ('%s') is missing", posArg.getZeroBasedPosition(), posArg.getTitle());
        this.argumentTitles = Collections.singletonList(posArg.getTitle());
    }

    public ParseArgumentsMissingException(List<String> argumentTitles) {
        super("Required arguments are missing: '%s'", StringUtils.join(argumentTitles, ','));
        this.argumentTitles = argumentTitles;
    }
    
    public ParseArgumentsMissingException(String message, List<String> argumentTitles, Object... args) {
        super(message, args);
        this.argumentTitles = argumentTitles;
    }

    /**
     * Gets the argument title
     * 
     * @return Title
     */
    public List<String> getArgumentTitle() {
        return argumentTitles;
    }
}
