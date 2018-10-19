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

import com.github.rvesse.airline.annotations.restrictions.Port;
import com.github.rvesse.airline.annotations.restrictions.PortRanges;
import com.github.rvesse.airline.restrictions.ArgumentsRestriction;
import com.github.rvesse.airline.restrictions.OptionRestriction;
import com.github.rvesse.airline.restrictions.common.PortRange;
import com.github.rvesse.airline.restrictions.common.PortRangeImpl;
import com.github.rvesse.airline.restrictions.common.PortRestriction;

public class PortRestrictionFactory implements OptionRestrictionFactory, ArgumentsRestrictionFactory {

    @Override
    public ArgumentsRestriction createArgumentsRestriction(Annotation annotation) {
        if (annotation instanceof Port) {
            return createCommon((Port) annotation);
        }
        return null;
    }

    @Override
    public OptionRestriction createOptionRestriction(Annotation annotation) {
        if (annotation instanceof Port) {
            return createCommon((Port) annotation);
        } else if (annotation instanceof com.github.rvesse.airline.annotations.restrictions.PortRange) {
            com.github.rvesse.airline.annotations.restrictions.PortRange range = (com.github.rvesse.airline.annotations.restrictions.PortRange) annotation;
            return createCommon(new PortRangeImpl(range.minimum(), range.maximum()));
        } else if (annotation instanceof PortRanges) {
            PortRanges ranges = (PortRanges) annotation;
            PortRange[] resolvedRanges = new PortRange[ranges.value().length];
            int i = 0;
            for (com.github.rvesse.airline.annotations.restrictions.PortRange range : ranges.value()) {
                resolvedRanges[i] = createRange(range);
                i++;
            }
            return createCommon(resolvedRanges);
        }
        return null;
    }

    protected final PortRange createRange(
            com.github.rvesse.airline.annotations.restrictions.PortRange rangeAnnotation) {
        return new PortRangeImpl(rangeAnnotation.minimum(), rangeAnnotation.maximum());
    }

    protected final PortRestriction createCommon(Port annotation) {
        return createCommon(annotation.acceptablePorts());
    }

    protected final PortRestriction createCommon(PortRange... ranges) {
        return new PortRestriction(ranges);
    }

    protected List<Class<? extends Annotation>> supportedAnnotations() {
        List<Class<? extends Annotation>> supported = new ArrayList<>();
        supported.add(Port.class);
        supported.add(com.github.rvesse.airline.annotations.restrictions.PortRange.class);
        supported.add(PortRanges.class);
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
