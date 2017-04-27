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
package com.github.rvesse.airline.utils.predicates.restrictions;

import org.apache.commons.collections4.Predicate;
import com.github.rvesse.airline.utils.StringUtils;

import com.github.rvesse.airline.restrictions.OptionRestriction;
import com.github.rvesse.airline.restrictions.options.MutuallyExclusiveRestriction;

public class MutuallyExclusiveWithFinder implements Predicate<OptionRestriction> {

    private final String tag;

    public MutuallyExclusiveWithFinder() {
        this(null);
    }

    public MutuallyExclusiveWithFinder(String tag) {
        this.tag = tag;
    }

    @Override
    public boolean evaluate(OptionRestriction restriction) {
        if (restriction instanceof MutuallyExclusiveRestriction) {
            if (tag == null)
                return true;
            return StringUtils.equals(tag, ((MutuallyExclusiveRestriction) restriction).getTag());
        }
        return false;
    }

}
