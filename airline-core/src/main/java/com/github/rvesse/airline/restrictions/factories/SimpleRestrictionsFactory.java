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

import com.github.rvesse.airline.annotations.restrictions.DefaultsFrom;
import com.github.rvesse.airline.annotations.restrictions.Required;
import com.github.rvesse.airline.annotations.restrictions.Unrestricted;
import com.github.rvesse.airline.restrictions.AbstractCommonRestriction;
import com.github.rvesse.airline.restrictions.ArgumentsRestriction;
import com.github.rvesse.airline.restrictions.None;
import com.github.rvesse.airline.restrictions.OptionRestriction;
import com.github.rvesse.airline.restrictions.common.DefaultsRestriction;
import com.github.rvesse.airline.restrictions.common.IsRequiredRestriction;

public class SimpleRestrictionsFactory implements OptionRestrictionFactory, ArgumentsRestrictionFactory {

    @Override
    public ArgumentsRestriction createArgumentsRestriction(Annotation annotation) {
        return createCommon(annotation);
    }

    private AbstractCommonRestriction createCommon(Annotation annotation) {
        if (annotation instanceof Required) {
            return new IsRequiredRestriction();
        } else if (annotation instanceof Unrestricted) {
            return new None();
        } else if (annotation instanceof DefaultsFrom) {
            return new DefaultsRestriction(((DefaultsFrom) annotation).value());
        }
        return null;
    }

    @Override
    public OptionRestriction createOptionRestriction(Annotation annotation) {
        return createCommon(annotation);
    }

    protected List<Class<? extends Annotation>> supportedAnnotations() {
        List<Class<? extends Annotation>> supported = new ArrayList<>();
        supported.add(Required.class);
        supported.add(Unrestricted.class);
        supported.add(DefaultsFrom.class);
        return supported;
    }

    @Override
    public List<Class<? extends Annotation>> supportedArgumentsAnnotations() {
        return supportedAnnotations();
    }

    @Override
    public List<Class<? extends Annotation>> supportedOptionAnnotations() {
        return supportedAnnotations();
    }

}
