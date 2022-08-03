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
package com.github.rvesse.airline.restrictions.factories;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import com.github.rvesse.airline.annotations.restrictions.NoOptionLikeValues;
import com.github.rvesse.airline.annotations.restrictions.Unrestricted;
import com.github.rvesse.airline.annotations.restrictions.global.CommandRequired;
import com.github.rvesse.airline.annotations.restrictions.global.NoMissingOptionValues;
import com.github.rvesse.airline.annotations.restrictions.global.NoUnexpectedArguments;
import com.github.rvesse.airline.restrictions.GlobalRestriction;
import com.github.rvesse.airline.restrictions.None;
import com.github.rvesse.airline.restrictions.common.NoOptionLikeValuesRestriction;
import com.github.rvesse.airline.restrictions.factories.GlobalRestrictionFactory;
import com.github.rvesse.airline.restrictions.global.CommandRequiredRestriction;
import com.github.rvesse.airline.restrictions.global.NoMissingOptionValuesRestriction;
import com.github.rvesse.airline.restrictions.global.NoUnexpectedArgumentsRestriction;

public class StandardGlobalRestrictionsFactory implements GlobalRestrictionFactory {

    @Override
    public GlobalRestriction createGlobalRestriction(Annotation annotation) {
        if (annotation instanceof Unrestricted) {
            return new None();
        } else if (annotation instanceof CommandRequired) {
            return new CommandRequiredRestriction();
        } else if (annotation instanceof NoMissingOptionValues) {
            return new NoMissingOptionValuesRestriction();
        } else if (annotation instanceof NoUnexpectedArguments) {
            return new NoUnexpectedArgumentsRestriction();
        } else if (annotation instanceof NoOptionLikeValues) {
            return new NoOptionLikeValuesRestriction(((NoOptionLikeValues) annotation).optionPrefixes());
        }
        return null;
    }

    @Override
    public List<Class<? extends Annotation>> supportedGlobalAnnotations() {
        List<Class<? extends Annotation>> supported = new ArrayList<>();
        supported.add(Unrestricted.class);
        supported.add(CommandRequired.class);
        supported.add(NoMissingOptionValues.class);
        supported.add(NoUnexpectedArguments.class);
        supported.add(NoOptionLikeValues.class);
        return supported;
    }

}
