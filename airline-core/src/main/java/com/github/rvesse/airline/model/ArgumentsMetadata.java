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
import java.util.List;
import java.util.Set;

import com.github.rvesse.airline.utils.CollectionUtils;
import com.github.rvesse.airline.utils.IteratorUtils;
import com.github.rvesse.airline.utils.ListUtils;
import com.github.rvesse.airline.utils.StringUtils;

public class ArgumentsMetadata {
    private final List<String> titles;
    private final String description;
    private final Set<Accessor> accessors;
    private final List<ArgumentsRestriction> restrictions;
    private final TypeConverterProvider provider;

    //@formatter:off
    public ArgumentsMetadata(Iterable<String> titles, 
                             String description, 
                             Iterable<ArgumentsRestriction> restrictions, 
                             TypeConverterProvider typeConverterProvider,
                             Iterable<Field> path) {
    //@formatter:on
        if (titles == null)
            throw new NullPointerException("title cannot be null");
        if (path == null)
            throw new NullPointerException("path cannot be null");
        if (!path.iterator().hasNext())
            throw new IllegalArgumentException("path cannot be empty");

        this.titles = ListUtils.unmodifiableList(IteratorUtils.toList(titles.iterator()));
        this.description = description;
        this.restrictions = restrictions != null ? AirlineUtils.unmodifiableListCopy(restrictions)
                : Collections.<ArgumentsRestriction> emptyList();
        this.provider = typeConverterProvider != null ? typeConverterProvider : new DefaultTypeConverterProvider();
        this.accessors = Collections.unmodifiableSet(AirlineUtils.singletonSet(new Accessor(path)));
    }

    public ArgumentsMetadata(Iterable<ArgumentsMetadata> arguments) {
        if (arguments == null)
            throw new NullPointerException("arguments cannot be null");
        if (!arguments.iterator().hasNext())
            throw new IllegalArgumentException("arguments cannot be empty");

        ArgumentsMetadata first = arguments.iterator().next();

        this.titles = first.titles;
        this.description = first.description;
        this.restrictions = first.restrictions;
        this.provider = first.provider;

        Set<Accessor> accessors = new HashSet<>();
        for (ArgumentsMetadata other : arguments) {
            if (!first.equals(other))
                throw new IllegalArgumentException(
                        String.format("Conflicting arguments definitions: %s, %s", first, other));

            accessors.addAll(other.getAccessors());
        }
        this.accessors = Collections.unmodifiableSet(accessors);
    }

    public List<String> getTitle() {
        return titles;
    }

    public String getDescription() {
        return description;
    }

    public boolean isRequired() {
        return CollectionUtils.exists(this.restrictions, new IsRequiredArgumentFinder());
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

        ArgumentsMetadata that = (ArgumentsMetadata) o;

        if (description != null ? !description.equals(that.description) : that.description != null) {
            return false;
        }
        if (!titles.equals(that.titles)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = titles.hashCode();
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("ArgumentsMetadata");
        sb.append("{title='").append(StringUtils.join(titles, ',')).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", accessors=").append(accessors);
        sb.append('}');
        return sb.toString();
    }
}
