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

import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.lang3.tuple.Pair;

import com.github.rvesse.airline.model.OptionMetadata;
import com.github.rvesse.airline.restrictions.OptionRestriction;

public abstract class AbstractParsedOptionRestrictionBasedFinder implements Predicate<Pair<OptionMetadata, Object>> {

    public AbstractParsedOptionRestrictionBasedFinder() {
        super();
    }

    protected abstract Predicate<OptionRestriction> getRestrictionPredicate();

    @Override
    public boolean evaluate(Pair<OptionMetadata, Object> parsedOption) {
        return IterableUtils.matchesAny(parsedOption.getLeft().getRestrictions(), getRestrictionPredicate());
    }

}