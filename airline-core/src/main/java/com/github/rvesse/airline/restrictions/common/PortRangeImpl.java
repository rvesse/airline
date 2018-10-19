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
