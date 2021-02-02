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
package com.github.rvesse.airline.parser.errors.handlers;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.github.rvesse.airline.parser.errors.ParseException;

public abstract class AbstractCollectingHandler implements ParserErrorHandler {

    protected List<ParseException> errors = new ArrayList<>();

    public AbstractCollectingHandler() {
        super();
    }

    @Override
    public void handleError(ParseException e) {
        // Check for duplicate messages
        for (ParseException existing : this.errors) {
            if (StringUtils.equals(existing.getMessage(), e.getMessage())) {
                existing.addSuppressed(e);
                return;
            }
        }
        
        this.errors.add(e);
    }

    protected List<ParseException> getCollection() {
        return this.errors;
    }

    protected void resetCollection() {
        this.errors = new ArrayList<>();
    }

}