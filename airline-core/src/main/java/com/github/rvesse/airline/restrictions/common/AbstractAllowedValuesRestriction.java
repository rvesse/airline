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
package com.github.rvesse.airline.restrictions.common;

import java.util.LinkedHashSet;
import java.util.Set;

import com.github.rvesse.airline.help.sections.HelpFormat;
import com.github.rvesse.airline.help.sections.HelpHint;
import com.github.rvesse.airline.restrictions.AbstractCommonRestriction;

public abstract class AbstractAllowedValuesRestriction extends AbstractCommonRestriction implements HelpHint {
    
    protected static final boolean CASE_SENSITIVE = false, CASE_INSENSITIVE = true;

    protected final Set<String> rawValues = new LinkedHashSet<String>();
    protected final boolean caseInsensitive;

    public AbstractAllowedValuesRestriction(boolean caseInsensitive) {
        this.caseInsensitive = caseInsensitive;
    }

    protected static Set<Object> asObjects(Set<String> set) {
        Set<Object> newSet = new LinkedHashSet<Object>();
        for (String item : set) {
            newSet.add((Object) item);
        }
        return newSet;
    }

    @Override
    public String getPreamble() {
        return String.format("This options value is restricted to the following set of %s:",
                this.caseInsensitive ? "case insensitive values" : "values");
    }

    @Override
    public HelpFormat getFormat() {
        return HelpFormat.LIST;
    }

    @Override
    public int numContentBlocks() {
        return 1;
    }

    @Override
    public String[] getContentBlock(int blockNumber) {
        if (blockNumber != 0)
            throw new IndexOutOfBoundsException();
        return this.rawValues.toArray(new String[this.rawValues.size()]);
    }

    public Set<String> getAllowedValues() {
        return this.rawValues;
    }

}