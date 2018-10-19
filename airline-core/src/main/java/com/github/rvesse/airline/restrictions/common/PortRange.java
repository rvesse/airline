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

/**
 * Interface for port ranges, useful if you want to use the
 * {@link PortRestriction} with a custom port range implementation
 * 
 * @author rvesse
 *
 */
public interface PortRange {

    /**
     * Gets the minimum port
     * 
     * @return Minimum port
     */
    int getMinimumPort();

    /**
     * Gets the maximum port
     * 
     * @return Maximum port
     */
    int getMaximumPort();

    /**
     * Gets whether a port falls within the range
     * 
     * @param port
     *            Port
     * @return True if in range, false otherwise
     */
    boolean inRange(int port);

    /**
     * Gets whether the port range contains another port range i.e. does this
     * cover at least the same range of ports as the other
     * 
     * @param other
     *            Other port type
     * @return True if this covers at least the same range of ports as the
     *         other, false otherwise
     */
    boolean contains(PortRange other);

}