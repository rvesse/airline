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
import com.github.rvesse.airline.annotations.OptionType;
import com.github.rvesse.airline.restrictions.OptionRestriction;
import com.github.rvesse.airline.types.DefaultTypeConverterProvider;
import com.github.rvesse.airline.types.TypeConverterProvider;
import com.github.rvesse.airline.utils.AirlineUtils;
import com.github.rvesse.airline.utils.predicates.restrictions.IsRequiredOptionFinder;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.SetUtils;

public class OptionMetadata {
    private final OptionType optionType;
    private final Set<String> options;
    private final String title, description;
    private final int arity;
    private final boolean hidden, overrides, sealed;
    private final List<OptionRestriction> restrictions;
    private final TypeConverterProvider provider;
    private Set<Accessor> accessors;

    //@formatter:off
    public OptionMetadata(OptionType optionType, 
                          Iterable<String> options, 
                          String title, 
                          String description, 
                          int arity,
                          boolean hidden, 
                          boolean overrides, 
                          boolean sealed,
                          Iterable<OptionRestriction> restrictions,
                          TypeConverterProvider typeConverterProvider,
                          Iterable<Field> path) {
    //@formatter:on
        if (optionType == null)
            throw new NullPointerException("optionType cannot be null");
        if (options == null)
            throw new NullPointerException("options cannot be null");
        if (!options.iterator().hasNext())
            throw new NullPointerException("options cannot be empty");
        if (title == null)
            throw new NullPointerException("title cannot be null");

        this.optionType = optionType;
        this.options = AirlineUtils.unmodifiableSetCopy(options);
        this.title = title;
        this.description = description;
        this.arity = arity;
        this.hidden = hidden;
        this.overrides = overrides;
        this.sealed = sealed;
        this.restrictions = restrictions != null ? AirlineUtils.unmodifiableListCopy(restrictions) : Collections.<OptionRestriction>emptyList();
        this.provider = typeConverterProvider != null ? typeConverterProvider : new DefaultTypeConverterProvider();

        if (path != null) {
            this.accessors = SetUtils.unmodifiableSet(Collections.singleton(new Accessor(path)));
        }
    }

    public OptionMetadata(Iterable<OptionMetadata> options) {
        if (options == null)
            throw new NullPointerException("options cannot be null");
        if (!options.iterator().hasNext())
            throw new IllegalArgumentException("options cannot be empty");

        OptionMetadata option = options.iterator().next();

        this.optionType = option.optionType;
        this.options = option.options;
        this.title = option.title;
        this.description = option.description;
        this.arity = option.arity;
        this.hidden = option.hidden;
        this.overrides = option.overrides;
        this.sealed = option.sealed;
        this.restrictions = option.restrictions;
        this.provider = option.provider;

        Set<Accessor> accessors = new LinkedHashSet<Accessor>();
        for (OptionMetadata other : options) {
            if (!option.equals(other))
                throw new IllegalArgumentException(
                        String.format("Duplicate options definitions: %s, %s", option, other));

            accessors.addAll(other.getAccessors());
        }
        this.accessors = SetUtils.unmodifiableSet(accessors);
    }

    public OptionType getOptionType() {
        return optionType;
    }

    public Set<String> getOptions() {
        return options;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getArity() {
        return arity;
    }

    public boolean isRequired() {
        return IterableUtils.matchesAny(this.restrictions, new IsRequiredOptionFinder());
    }

    public boolean isHidden() {
        return hidden;
    }

    public boolean isOverride() {
        return overrides;
    }

    public boolean isSealed() {
        return sealed;
    }

    public boolean isMultiValued() {
        return accessors.iterator().next().isMultiValued();
    }

    public Class<?> getJavaType() {
        return accessors.iterator().next().getJavaType();
    }

    public Set<Accessor> getAccessors() {
        if (accessors == null) {
            throw new NullPointerException("No accessors defined for option");
        }
        return accessors;
    }
    
    public List<OptionRestriction> getRestrictions() {
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

        OptionMetadata that = (OptionMetadata) o;

        if (arity != that.arity) {
            return false;
        }
        if (hidden != that.hidden) {
            return false;
        }
        if (overrides != that.overrides) {
            return false;
        }
        if (sealed != that.sealed) {
            return false;
        }
        if (description != null ? !description.equals(that.description) : that.description != null) {
            return false;
        }
        if (optionType != that.optionType) {
            return false;
        }
        if (!options.equals(that.options)) {
            return false;
        }
        if (!title.equals(that.title)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = optionType.hashCode();
        result = 31 * result + options.hashCode();
        result = 31 * result + title.hashCode();
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + arity;
        result = 31 * result + (hidden ? 1 : 0);
        result = 31 * result + (overrides ? 1 : 0);
        result = 31 * result + (sealed ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("OptionMetadata");
        sb.append("{optionType=").append(optionType);
        sb.append(", options=").append(options);
        sb.append(", title='").append(title).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", arity=").append(arity);
        sb.append(", hidden=").append(hidden);
        sb.append(", override=").append(overrides);
        sb.append(", sealed=").append(sealed);
        sb.append(", accessors=").append(accessors);
        sb.append('}');
        return sb.toString();
    }

    /**
     * Tries to merge the option metadata together such that the child metadata
     * takes precedence. Not all options can be successfully overridden and an
     * error may be thrown in cases where merging is not possible
     * <p>
     * The following pieces of metadata may be overridden:
     * </p>
     * <ul>
     * <li>Title</li>
     * <li>Description</li>
     * <li>Hidden</li>
     * </ul>
     * 
     * @param parent
     *            Parent
     * @param child
     *            Child
     * @return Merged metadata
     */
    public static OptionMetadata override(Set<String> names, OptionMetadata parent, OptionMetadata child) {
        // Cannot change option type, arity or names
        if (parent.optionType != child.optionType)
            throw new IllegalArgumentException(String.format("Cannot change optionType when overriding option %s",
                    names));
        if (parent.arity != child.arity)
            throw new IllegalArgumentException(String.format("Cannot change arity when overriding option %s", names));
        if (!parent.options.equals(child.options))
            throw new IllegalArgumentException(String.format("Cannot change option names when overriding option %s",
                    names));

        // Also cannot change the type of the option unless the change is a
        // narrowing conversion
        Class<?> parentType = parent.getJavaType();
        Class<?> childType = child.getJavaType();
        if (!parentType.equals(childType)) {
            if (!parentType.isAssignableFrom(childType)) {
                if (childType.isAssignableFrom(parentType)) {
                    // A widening conversion exists but this is illegal however
                    // we can give a slightly more informative error in this
                    // case
                    throw new IllegalArgumentException(
                            String.format(
                                    "Cannot change the Java type from %s to %s when overriding option %s as this is a widening type change - only narrowing type changes are permitted",
                                    parentType, childType, names));
                } else {
                    // No conversion exists
                    throw new IllegalArgumentException(
                            String.format(
                                    "Cannot change the Java type from %s to %s when overriding option %s - only narrowing type changes where a valid cast exists are permitted",
                                    parentType, childType, names));
                }
            }
        }

        // Check for duplicates
        boolean isDuplicate = parent == child || parent.equals(child);

        // Parent must not state it is sealed UNLESS it is a duplicate which can
        // happen when using @Inject to inject options via delegates
        if (parent.sealed && !isDuplicate)
            throw new IllegalArgumentException(String.format(
                    "Cannot override option %s as parent option declares it to be sealed", names));

        // Child must explicitly state that it overrides otherwise we cannot
        // override UNLESS it is the case that this is a duplicate which
        // can happen when using @Inject to inject options via delegates
        if (!child.overrides && !isDuplicate)
            throw new IllegalArgumentException(String.format(
                    "Cannot override option %s unless child option sets overrides to true", names));

        OptionMetadata merged;
        //@formatter:off
        merged = new OptionMetadata(child.optionType, 
                                    child.options, 
                                    child.title != null ? child.title : parent.title,
                                    child.description != null ? child.description : parent.description, 
                                    child.arity, 
                                    child.hidden, 
                                    child.overrides,
                                    child.sealed,
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
