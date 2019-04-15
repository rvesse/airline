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
package com.github.rvesse.airline.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.github.rvesse.airline.model.ArgumentMetadata;
import com.github.rvesse.airline.types.DefaultTypeConverterProvider;
import com.github.rvesse.airline.types.TypeConverterProvider;

import static java.lang.annotation.ElementType.FIELD;

import java.lang.annotation.Documented;

/**
 * Annotation that marks a field as being populated from a positional argument
 *
 */
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target({ FIELD })
@Documented
public @interface PositionalArgument {
    /**
     * Name of the argument
     * 
     * @return Name of the argument
     */
    String title() default "";

    /**
     * A description of the argument
     * 
     * @return Description
     */
    String description() default "";

    /**
     * The positional index (one-based) for the argument
     * <p>
     * So {@code 1} represents the first argument, {@code 3} the third argument
     * and so forth
     * </p>
     * 
     * @return
     */
    int position();
    
    /**
     * If true this parameter can override parameters of the same index (set via
     * the {@link PositionalArgument#position()} property) declared by parent classes assuming
     * the argument definitions are compatible.
     * <p>
     * See
     * {@link ArgumentMetadata#override(ArgumentMetadata, ArgumentMetadata)}
     * for legal overrides
     * </p>
     * <p>
     * Note that where the child argument definition is an exact duplicate of the
     * parent then overriding is implicitly permitted
     * </p>
     * @return True if an override, false otherwise
     */
    boolean override() default false;

    /**
     * If true this parameter cannot be overridden by parameters of the same
     * name declared in child classes regardless of whether the child class
     * declares the {@link #override()} property to be true
     * 
     * @return True if sealed, false otherwise
     */
    boolean sealed() default false;

    /**
     * Sets an alternative type converter provider for the argument. This allows
     * the type converter for argument to be customised appropriately. By
     * default this will defer to using the type converter provided in the
     * parser configuration.
     * 
     * @return Type converter provider
     */
    Class<? extends TypeConverterProvider> typeConverterProvider() default DefaultTypeConverterProvider.class;
}
