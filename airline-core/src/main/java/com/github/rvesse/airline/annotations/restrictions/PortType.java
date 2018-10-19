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
package com.github.rvesse.airline.annotations.restrictions;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import com.github.rvesse.airline.restrictions.common.PortRange;

/**
 * Port types
 */
public enum PortType implements PortRange {

    /**
     * Any port
     */
    ANY(0, 65535),
    /**
     * Port {@code 0} i.e. the special port that has the OS allocate an
     * available port
     */
    OS_ALLOCATED(0, 0),
    /**
     * The system (aka well known or privileged ports), these are ports
     * {@code 1} to {@code 1023} and usually require adminstrative privileges
     */
    SYSTEM(1, 1023),
    /**
     * The user ports (aka registered ports), these are ports {@code 1024} to
     * {@code 49151} and which may be registered and assigned by the IANA
     */
    USER(1024, 49151),
    /**
     * The dynamic ports (aka private or ephemeral ports), these are ports
     * {@code 49152} to {@code 65535} which are never assigned by the IANA
     */
    DYNAMIC(49152, 65535);

    private final int min, max;

    private PortType(int min, int max) {
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
    
    /**
     * Gets a string denoting all the acceptable port ranges
     * 
     * @param acceptablePorts
     *            Port types
     * @return String detailing acceptable ranges
     */
    public static String toRangesString(Set<PortRange> acceptablePorts) {
        StringBuilder builder = new StringBuilder();
        Iterator<PortRange> iter = acceptablePorts.iterator();
        Set<PortRange> types = new TreeSet<>();
        while (iter.hasNext()) {
            PortRange range = iter.next();

            // Check that the port range is not contained in another port range
            // we've already seen
            boolean add = true;
            for (PortRange other : types) {
                if (other.contains(range)) {
                    add = false;
                    break;
                }
            }
            if (add)
                types.add(range);
        }

        // Build the string
        iter = types.iterator();
        while (iter.hasNext()) {
            builder.append(iter.next().toString());
            if (iter.hasNext())
                builder.append(", ");
        }

        return builder.toString();
    }
}