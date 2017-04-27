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
package com.github.rvesse.airline.utils.predicates.parser;

import java.util.Map;

import org.apache.commons.collections4.Predicate;

import com.github.rvesse.airline.model.OptionMetadata;

public class ParsedOptionFinder implements Predicate<Map.Entry<OptionMetadata, Object>> {
    
    private final OptionMetadata opt;
    
    public ParsedOptionFinder(OptionMetadata option) {
        this.opt = option;
    }

    @Override
    public boolean evaluate(Map.Entry<OptionMetadata, Object> parsedOption) {
        if (parsedOption == null) return false;
        if (this.opt == null) return false;
        
        return this.opt.equals(parsedOption.getKey());
    }

}
