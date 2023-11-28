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
package com.github.rvesse.airline.types.numerics.abbreviated;

import java.util.Collections;

/**
 * A numeric type converter that supports standard using suffixes - {@code k m b t} - to abbreviate numbers expressed in
 * terms of thousands, millions, billions or trillions
 *
 */
public class KiloAs1000 extends SequenceAbbreviatedNumericTypeConverter {

    public KiloAs1000() {
        super(false, Collections.<String, Integer> emptyMap(), 1000, "k", "m", "b", "t");
    }
}
