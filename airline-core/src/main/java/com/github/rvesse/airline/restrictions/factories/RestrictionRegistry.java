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
package com.github.rvesse.airline.restrictions.factories;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.function.Function;

import com.github.rvesse.airline.restrictions.ArgumentsRestriction;
import com.github.rvesse.airline.restrictions.GlobalRestriction;
import com.github.rvesse.airline.restrictions.OptionRestriction;

/**
 * Central registry for restrictions
 */
public class RestrictionRegistry {

    private static final Map<Class<? extends Annotation>, OptionRestrictionFactory> OPTION_RESTRICTION_FACTORIES =
            new HashMap<>();
    private static final Map<Class<? extends Annotation>, ArgumentsRestrictionFactory> ARGUMENT_RESTRICTION_FACTORIES =
            new HashMap<>();
    private static final Map<Class<? extends Annotation>, GlobalRestrictionFactory> GLOBAL_RESTRICTION_FACTORIES =
            new HashMap<>();

    private static volatile boolean init = false;

    static {
        init();
    }

    static <T> void loadRestrictions(Class<T> cls, Function<T, List<Class<? extends Annotation>>> annotationsSelector,
                                     Map<Class<? extends Annotation>, T> registry) {
        try {
            ServiceLoader<T> factories = ServiceLoader.load(cls);
            Iterator<T> iter = factories.iterator();
            while (iter.hasNext()) {
                T factory = iter.next();
                for (Class<? extends Annotation> annotationClass : annotationsSelector.apply(factory)) {
                    registry.put(annotationClass, factory);
                }
            }
        } catch (Throwable e) {
            System.err.println("Failed to load " + cls.getSimpleName() + ": " + e.getMessage());
        }
    }

    /**
     * Initializes the base set of restrictions using the {@link ServiceLoader} mechanism
     */
    static synchronized void init() {
        if (init) {
            return;
        }

        // Use ServerLoader to obtain restrictions
        loadRestrictions(OptionRestrictionFactory.class, x -> x.supportedOptionAnnotations(),
                         OPTION_RESTRICTION_FACTORIES);
        loadRestrictions(ArgumentsRestrictionFactory.class, x -> x.supportedArgumentsAnnotations(),
                         ARGUMENT_RESTRICTION_FACTORIES);
        loadRestrictions(GlobalRestrictionFactory.class, x -> x.supportedGlobalAnnotations(),
                         GLOBAL_RESTRICTION_FACTORIES);

        init = true;
    }

    /**
     * Resets the registry to its default state
     */
    public synchronized static void reset() {
        init = false;
        OPTION_RESTRICTION_FACTORIES.clear();
        ARGUMENT_RESTRICTION_FACTORIES.clear();
        GLOBAL_RESTRICTION_FACTORIES.clear();
        init();
    }

    public static Set<Class<? extends Annotation>> getOptionRestrictionAnnotationClasses() {
        return OPTION_RESTRICTION_FACTORIES.keySet();
    }

    public static void addOptionRestriction(Class<? extends Annotation> cls, OptionRestrictionFactory factory) {
        if (cls == null) {
            throw new NullPointerException("cls cannot be null");
        }
        OPTION_RESTRICTION_FACTORIES.put(cls, factory);
    }

    public static <T extends Annotation> OptionRestriction getOptionRestriction(Class<? extends Annotation> cls,
                                                                                T annotation) {
        OptionRestrictionFactory factory = OPTION_RESTRICTION_FACTORIES.get(cls);
        if (factory != null) {
            return factory.createOptionRestriction(annotation);
        }
        return null;
    }

    public static void addArgumentsRestriction(Class<? extends Annotation> cls, ArgumentsRestrictionFactory factory) {
        if (cls == null) {
            throw new NullPointerException("cls cannot be null");
        }
        ARGUMENT_RESTRICTION_FACTORIES.put(cls, factory);
    }

    public static Set<Class<? extends Annotation>> getArgumentsRestrictionAnnotationClasses() {
        return ARGUMENT_RESTRICTION_FACTORIES.keySet();
    }

    public static <T extends Annotation> ArgumentsRestriction getArgumentsRestriction(Class<? extends Annotation> cls,
                                                                                      T annotation) {
        ArgumentsRestrictionFactory factory = ARGUMENT_RESTRICTION_FACTORIES.get(cls);
        if (factory != null) {
            return factory.createArgumentsRestriction(annotation);
        }
        return null;
    }

    public static Set<Class<? extends Annotation>> getGlobalRestrictionAnnotationClasses() {
        return GLOBAL_RESTRICTION_FACTORIES.keySet();
    }

    public static void addGlobalRestriction(Class<? extends Annotation> cls, GlobalRestrictionFactory factory) {
        if (cls == null) {
            throw new NullPointerException("cls cannot be null");
        }
        GLOBAL_RESTRICTION_FACTORIES.put(cls, factory);
    }

    public static <T extends Annotation> GlobalRestriction getGlobalRestriction(Class<? extends Annotation> cls,
                                                                                T annotation) {
        GlobalRestrictionFactory factory = GLOBAL_RESTRICTION_FACTORIES.get(cls);
        if (factory != null) {
            return factory.createGlobalRestriction(annotation);
        }
        return null;
    }
}
