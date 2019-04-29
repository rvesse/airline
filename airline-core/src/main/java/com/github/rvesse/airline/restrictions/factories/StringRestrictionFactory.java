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
import java.util.Locale;

import com.github.rvesse.airline.annotations.restrictions.EndsWith;
import com.github.rvesse.airline.annotations.restrictions.ExactLength;
import com.github.rvesse.airline.annotations.restrictions.MaxLength;
import com.github.rvesse.airline.annotations.restrictions.MinLength;
import com.github.rvesse.airline.annotations.restrictions.NotBlank;
import com.github.rvesse.airline.annotations.restrictions.NotEmpty;
import com.github.rvesse.airline.annotations.restrictions.Pattern;
import com.github.rvesse.airline.annotations.restrictions.StartsWith;
import com.github.rvesse.airline.restrictions.AbstractCommonRestriction;
import com.github.rvesse.airline.restrictions.ArgumentsRestriction;
import com.github.rvesse.airline.restrictions.OptionRestriction;
import com.github.rvesse.airline.restrictions.common.EndsWithRestriction;
import com.github.rvesse.airline.restrictions.common.LengthRestriction;
import com.github.rvesse.airline.restrictions.common.NotBlankRestriction;
import com.github.rvesse.airline.restrictions.common.NotEmptyRestriction;
import com.github.rvesse.airline.restrictions.common.PatternRestriction;
import com.github.rvesse.airline.restrictions.common.StartsWithRestriction;

public class StringRestrictionFactory implements ArgumentsRestrictionFactory, OptionRestrictionFactory {

    @Override
    public OptionRestriction createOptionRestriction(Annotation annotation) {
        return createCommon(annotation);
    }

    @Override
    public ArgumentsRestriction createArgumentsRestriction(Annotation annotation) {
        return createCommon(annotation);
    }

    protected AbstractCommonRestriction createCommon(Annotation annotation) {
        if (annotation instanceof Pattern) {
            Pattern pattern = (Pattern) annotation;
            return new PatternRestriction(pattern.pattern(), pattern.flags(), pattern.description());
        } else if (annotation instanceof MaxLength) {
            MaxLength ml = (MaxLength) annotation;
            return new LengthRestriction(ml.length(), true);
        } else if (annotation instanceof MinLength) {
            MinLength ml = (MinLength) annotation;
            return new LengthRestriction(ml.length(), false);
        } else if (annotation instanceof ExactLength) {
            ExactLength el = (ExactLength) annotation;
            return new LengthRestriction(el.length(), el.length());
        } else if (annotation instanceof NotBlank) {
            return new NotBlankRestriction();
        } else if (annotation instanceof NotEmpty) {
            return new NotEmptyRestriction();
        } else if (annotation instanceof EndsWith) {
            EndsWith ew = (EndsWith) annotation;
            return new EndsWithRestriction(ew.ignoreCase(), Locale.forLanguageTag(ew.locale()), ew.suffixes());
        } else if (annotation instanceof StartsWith) {
            StartsWith sw = (StartsWith) annotation;
            return new StartsWithRestriction(sw.ignoreCase(), Locale.forLanguageTag(sw.locale()), sw.prefixes());
        }
        return null;
    }

    protected List<Class<? extends Annotation>> supportedAnnotations() {
        List<Class<? extends Annotation>> supported = new ArrayList<>();
        supported.add(Pattern.class);
        supported.add(MaxLength.class);
        supported.add(MinLength.class);
        supported.add(ExactLength.class);
        supported.add(NotBlank.class);
        supported.add(NotEmpty.class);
        supported.add(EndsWith.class);
        supported.add(StartsWith.class);
        return supported;
    }

    @Override
    public List<Class<? extends Annotation>> supportedOptionAnnotations() {
        return supportedAnnotations();
    }

    @Override
    public List<Class<? extends Annotation>> supportedArgumentsAnnotations() {
        return supportedAnnotations();
    }
}
