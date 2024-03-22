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
package com.github.rvesse.airline.parser.plugins;

import java.lang.annotation.Annotation;
import java.util.*;

/**
 * Central registry for parser plugins
 */
public class PluginsRegistry {

    private static final Map<Class<? extends Annotation>, ParserPluginFactory> PRE_PARSE_PLUGIN_FACTORIES =
            new HashMap<>();
    private static final Map<Class<? extends Annotation>, ParserPluginFactory> POST_PARSE_PLUGIN_FACTORIES =
            new HashMap<>();
    private static volatile boolean init = false;

    static {
        init();
    }

    /**
     * Initializes the base set of restrictions using the {@link ServiceLoader} mechanism
     */
    static synchronized void init() {
        if (init) {
            return;
        }

        // Use ServerLoader to obtain plugin factories
        try {
            ServiceLoader<ParserPluginFactory> factories = ServiceLoader.load(ParserPluginFactory.class);
            Iterator<ParserPluginFactory> iter = factories.iterator();
            while (iter.hasNext()) {
                ParserPluginFactory factory = iter.next();
                for (Class<? extends Annotation> annotationClass : factory.supportedPreParsePluginAnnotations()) {
                    PRE_PARSE_PLUGIN_FACTORIES.put(annotationClass, factory);
                }
                for (Class<? extends Annotation> annotationClass : factory.supportedPostParsePluginAnnotations()) {
                    POST_PARSE_PLUGIN_FACTORIES.put(annotationClass, factory);
                }
            }
        } catch (Throwable e) {
            System.err.println("Failed to load ParserPluginFactory: " + e.getMessage());
        }

        init = true;
    }

    /**
     * Resets the registry to its default state
     */
    public synchronized static void reset() {
        init = false;
        PRE_PARSE_PLUGIN_FACTORIES.clear();
        POST_PARSE_PLUGIN_FACTORIES.clear();
        init();
    }

    public static Set<Class<? extends Annotation>> getPreParsePluginAnnotationClasses() {
        return PRE_PARSE_PLUGIN_FACTORIES.keySet();
    }

    public static Set<Class<? extends Annotation>> getPostParsePluginAnnotationClasses() {
        return POST_PARSE_PLUGIN_FACTORIES.keySet();
    }

    public static <C, T extends Annotation> PreParsePlugin<C> getPreParsePlugin(Class<? extends Annotation> cls,
                                                                                T annotation) {
        ParserPluginFactory factory = PRE_PARSE_PLUGIN_FACTORIES.get(cls);
        if (factory != null) {
            return factory.createPreParsePlugin(annotation);
        }
        return null;
    }

    public static <C, T extends Annotation> PostParsePlugin<C> getPostParsePlugin(Class<? extends Annotation> cls,
                                                                                   T annotation) {
        ParserPluginFactory factory = POST_PARSE_PLUGIN_FACTORIES.get(cls);
        if (factory != null) {
            return factory.createPostParsePlugin(annotation);
        }
        return null;
    }

}
