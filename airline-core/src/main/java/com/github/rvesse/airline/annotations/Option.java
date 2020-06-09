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

import com.github.rvesse.airline.model.OptionMetadata;
import com.github.rvesse.airline.types.DefaultTypeConverterProvider;
import com.github.rvesse.airline.types.TypeConverterProvider;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;

import java.lang.annotation.Documented;

/**
 * Annotation to mark a field as an option
 *
 */
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target({ FIELD })
@Documented
public @interface Option {
    /**
     * Is this a command, group or global option
     * 
     * @return Option type
     */
    OptionType type() default OptionType.COMMAND;

    /**
     * Name use to identify the option value in documentation and error messages
     * <p>
     * If your option has {@link #arity()} greater than 1 then you can specify titles for each parameter by specifying
     * an array of titles. If there are fewer titles than the arity then the last title in the list will be used for all
     * subsequent options
     * </p>
     * 
     * @return Title
     */
    String[] title() default "";

    /**
     * An array of allowed command line parameters e.g. {@code -n}, {@code --name}, etc...
     * 
     * @return Names
     */
    String[] name();

    /**
     * A description of this option.
     * 
     * @return Description
     */
    String description() default "";

    /**
     * How many parameter values this option will consume. For example, an arity of 2 will allow
     * {@code -pair value1 value2}
     * 
     * @return Arity
     */
    int arity() default Integer.MIN_VALUE;

    /**
     * If true, this parameter won't appear in the usage().
     * 
     * @return True if hidden, false otherwise
     */
    boolean hidden() default false;

    /**
     * If true this parameter can override parameters of the same name (set via the {@link Option#name()} property)
     * declared by parent classes assuming the option definitions are compatible.
     * <p>
     * See {@link OptionMetadata#override(java.util.Set, OptionMetadata, OptionMetadata)} for legal overrides
     * </p>
     * <p>
     * Note that where the child option definition is an exact duplicate of the parent then overriding is implicitly
     * permitted
     * </p>
     * 
     * @return True if an override, false otherwise
     */
    boolean override() default false;

    /**
     * If true this parameter cannot be overridden by parameters of the same name declared in child classes regardless
     * of whether the child class declares the {@link #override()} property to be true
     * 
     * @return True if sealed, false otherwise
     */
    boolean sealed() default false;

    /**
     * Sets an alternative type converter provider for the option. This allows the type converter for an option to be
     * customised appropriately. By default this will defer to using the type converter provided in the parser
     * configuration.
     * 
     * @return Type converter provider
     */
    Class<? extends TypeConverterProvider> typeConverterProvider() default DefaultTypeConverterProvider.class;
}
