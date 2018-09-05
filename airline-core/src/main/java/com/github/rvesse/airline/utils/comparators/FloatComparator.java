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
package com.github.rvesse.airline.utils.comparators;

public class FloatComparator extends AbstractComparableComparator<Float> {

    public FloatComparator() {
        super(Float.class);
    }

    @Override
    protected int compareValues(Float v1, Float v2) {
        // Handle special cases
        if (v1 == Float.MIN_VALUE) {
            if (v2 == Float.MIN_VALUE)
                return 0;
            return -1;
        } else if (v2 == Float.MIN_VALUE) {
            return 1;
        } else if (v1 == Float.MAX_VALUE) {
            if (v2 == Float.MAX_VALUE)
                return 0;
            return 1;
        } else if (v2 == Float.MAX_VALUE) {
            return -1;
        }
        
        // Normal comparison
        return super.compareValues(v1, v2);
    }
}