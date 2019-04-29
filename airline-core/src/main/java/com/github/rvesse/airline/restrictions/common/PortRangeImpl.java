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

import com.github.rvesse.airline.parser.errors.ParseInvalidRestrictionException;

/**
 * Concrete implementation of a port range
 * 
 * @author rvesse
 *
 */
public class PortRangeImpl implements PortRange, Comparable<PortRange> {
    private final int min, max;

    /**
     * Creates a new port range
     * 
     * @param min
     *            Minimum
     * @param max
     *            Maximum
     */
    public PortRangeImpl(int min, int max) {
        if (min > max)
            throw new ParseInvalidRestrictionException("Minimum port %d cannot be greater than Maximum port %d", min,
                    max);
        this.min = min;
        this.max = max;
    }

    @Override
    public int getMinimumPort() {
        return this.min;
    }

    @Override
    public int getMaximumPort() {
        return this.max;
    }

    @Override
    public boolean inRange(int port) {
        return port >= min && port <= max;
    }

    @Override
    public boolean contains(PortRange other) {
        if (this == other)
            return true;

        return this.getMinimumPort() <= other.getMinimumPort() && this.getMaximumPort() >= other.getMaximumPort();
    }

    @Override
    public String toString() {
        if (min != max) {
            return String.format("%d-%d", min, max);
        } else {
            return Integer.toString(min);
        }
    }

    @Override
    public int compareTo(PortRange other) {
        if (this == other)
            return 0;
        if (other == null)
            return 1;

        if (this.min < other.getMinimumPort()) {
            return -1;
        } else if (this.min == other.getMinimumPort()) {
            if (this.max < other.getMaximumPort()) {
                return -1;
            } else if (this.max == other.getMaximumPort()) {
                return 0;
            } else {
                return 1;
            }
        }
        return 1;
    }
}
