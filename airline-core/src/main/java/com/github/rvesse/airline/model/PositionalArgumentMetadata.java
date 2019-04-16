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
package com.github.rvesse.airline.model;

import com.github.rvesse.airline.Accessor;
import com.github.rvesse.airline.restrictions.ArgumentsRestriction;
import com.github.rvesse.airline.types.DefaultTypeConverterProvider;
import com.github.rvesse.airline.types.TypeConverterProvider;
import com.github.rvesse.airline.utils.AirlineUtils;
import com.github.rvesse.airline.utils.predicates.restrictions.IsRequiredArgumentFinder;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang3.StringUtils;

public class PositionalArgumentMetadata {
    private final int position;
    private final String title, description;
    private final boolean sealed, overrides;
    private Set<Accessor> accessors;
    private final List<ArgumentsRestriction> restrictions;
    private final TypeConverterProvider provider;

    /**
     * Creates new positional argument metadata
     * 
     * @param position
     *            Zero based position index
     * @param title
     *            Title
     * @param description
     *            Description
     * @param restrictions
     *            Restrictions
     * @param typeConverterProvider
     *            Type converter provider
     * @param path
     *            Field to modify
     */
    //@formatter:off
    public PositionalArgumentMetadata(int position, String title, 
                            String description, 
                            boolean sealed, boolean overrides,
                            Iterable<ArgumentsRestriction> restrictions, 
                            TypeConverterProvider typeConverterProvider,
                            Iterable<Field> path) {
    //@formatter:on
        if (position < 0)
            throw new IllegalArgumentException("Position must be >= 0");
        if (title == null)
            throw new NullPointerException("title cannot be null");

        this.position = position;
        this.title = title;
        this.description = description;
        this.overrides = overrides;
        this.sealed = sealed;
        this.restrictions = restrictions != null ? AirlineUtils.unmodifiableListCopy(restrictions)
                : Collections.<ArgumentsRestriction> emptyList();
        this.provider = typeConverterProvider != null ? typeConverterProvider : new DefaultTypeConverterProvider();

        if (path != null) {
            if (!path.iterator().hasNext())
                throw new IllegalArgumentException("path cannot be empty");
            this.accessors = SetUtils.unmodifiableSet(Collections.singleton(new Accessor(path)));
        }
    }

    public PositionalArgumentMetadata(Iterable<PositionalArgumentMetadata> arguments) {
        if (arguments == null)
            throw new NullPointerException("arguments cannot be null");
        if (!arguments.iterator().hasNext())
            throw new IllegalArgumentException("arguments cannot be empty");

        PositionalArgumentMetadata first = arguments.iterator().next();

        this.sealed = first.sealed;
        this.overrides = first.overrides;
        this.position = first.position;
        this.title = first.title;
        this.description = first.description;
        this.restrictions = first.restrictions;
        this.provider = first.provider;

        Set<Accessor> accessors = new HashSet<>();
        for (PositionalArgumentMetadata other : arguments) {
            if (!first.equals(other))
                throw new IllegalArgumentException(
                        String.format("Conflicting arguments definitions: %s, %s", first, other));

            accessors.addAll(other.getAccessors());
        }
        this.accessors = SetUtils.unmodifiableSet(accessors);
    }

    /**
     * Gets the zero based position index for the argument
     * 
     * @return Position
     */
    public int getZeroBasedPosition() {
        return this.position;
    }

    /**
     * Gets the one based position index for the argument
     * 
     * @return Position
     */
    public int getOneBasedPosition() {
        return this.position + 1;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public boolean isOverride() {
        return overrides;
    }

    public boolean isSealed() {
        return sealed;
    }

    public boolean isRequired() {
        return IterableUtils.matchesAny(this.restrictions, new IsRequiredArgumentFinder());
    }

    public Set<Accessor> getAccessors() {
        return accessors;
    }

    public boolean isMultiValued() {
        return accessors.iterator().next().isMultiValued();
    }

    public Class<?> getJavaType() {
        return accessors.iterator().next().getJavaType();
    }

    public List<ArgumentsRestriction> getRestrictions() {
        return this.restrictions;
    }

    public TypeConverterProvider getTypeConverterProvider() {
        return this.provider;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PositionalArgumentMetadata that = (PositionalArgumentMetadata) o;

        if (this.position != that.position)
            return false;
        if (!StringUtils.equals(this.description, that.description)) {
            return false;
        }
        if (!StringUtils.equals(this.title, that.title)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = title.hashCode();
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = result * this.position;
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("ArgumentsMetadata");
        sb.append("{position=").append(this.position).append('\'');
        sb.append(", title='").append(title).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", accessors=").append(accessors);
        sb.append('}');
        return sb.toString();
    }

    /**
     * Tries to merge the argument metadata together such that the child
     * metadata takes precedence. Not all arguments can be successfully
     * overridden and an error may be thrown in cases where merging is not
     * possible
     * <p>
     * The following pieces of metadata may be overridden:
     * </p>
     * <ul>
     * <li>Title</li>
     * <li>Description</li>
     * </ul>
     * 
     * @param parent
     *            Parent
     * @param child
     *            Child
     * @return Merged metadata
     */
    public static PositionalArgumentMetadata override(PositionalArgumentMetadata parent,
            PositionalArgumentMetadata child) {
        // Cannot change position
        if (parent.position != child.position)
            throw new IllegalArgumentException(
                    String.format("Cannot change argument position when overriding positional argument %d (%s)",
                            parent.position, parent.title));

        // Also cannot change the type of the argument unless the change is a
        // narrowing conversion
        Class<?> parentType = parent.getJavaType();
        Class<?> childType = child.getJavaType();
        if (!parentType.equals(childType)) {
            if (!parentType.isAssignableFrom(childType)) {
                if (childType.isAssignableFrom(parentType)) {
                    // A widening conversion exists but this is illegal however
                    // we can give a slightly more informative error in this
                    // case
                    throw new IllegalArgumentException(String.format(
                            "Cannot change the Java type from %s to %s when overriding positional argument %d (%s) as this is a widening type change - only narrowing type changes are permitted",
                            parentType, childType, parent.position, parent.title));
                } else {
                    // No conversion exists
                    throw new IllegalArgumentException(String.format(
                            "Cannot change the Java type from %s to %s when overriding positional argument %d (%s) - only narrowing type changes where a valid cast exists are permitted",
                            parentType, childType, parent.position, parent.title));
                }
            }
        }

        // Check for duplicates
        boolean isDuplicate = parent == child || parent.equals(child);

        // Parent must not state it is sealed UNLESS it is a duplicate which can
        // happen when using @Inject to inject options via delegates
        if (parent.sealed && !isDuplicate)
            throw new IllegalArgumentException(String.format(
                    "Cannot override positional argument %d (%s) as parent argument declares it to be sealed",
                    parent.position, parent.title));

        // Child must explicitly state that it overrides otherwise we cannot
        // override UNLESS it is the case that this is a duplicate which
        // can happen when using @Inject to inject options via delegates
        if (!child.overrides && !isDuplicate)
            throw new IllegalArgumentException(String.format(
                    "Cannot override positional argument %d (%s) unless child argument sets overrides to true",
                    parent.position, parent.title));

        PositionalArgumentMetadata merged;
        //@formatter:off
        merged = new PositionalArgumentMetadata(child.position, 
                                      child.title, 
                                      child.description, 
                                      child.sealed, 
                                      child.overrides, 
                                      child.restrictions.size() > 0 ? child.restrictions : parent.restrictions,
                                      child.provider, 
                                      null);
        //@formatter:on

        // Combine both child and parent accessors - this is necessary so the
        // parsed value propagates to all classes in the hierarchy
        Set<Accessor> accessors = new LinkedHashSet<>(child.accessors);
        accessors.addAll(parent.accessors);
        merged.accessors = AirlineUtils.unmodifiableSetCopy(accessors);
        return merged;
    }
}
