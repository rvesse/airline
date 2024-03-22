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
import java.util.List;

public interface ParserPluginFactory {

    /**
     * Try and create a pre-parse plugin from the given annotation
     *
     * @param annotation Annotation
     * @return Pre-parse plugin or {@code null} if this factory cannot create a plugin from the given annotation
     */
     <T> PreParsePlugin<T> createPreParsePlugin(Annotation annotation);

    /**
     * Try and create a post-parse plugin from the given annotation
     *
     * @param annotation Annotation
     * @return Post-parse plugin or {@code null} if this factory cannot create a plugin from the given annotation
     */
    <T> PostParsePlugin<T> createPostParsePlugin(Annotation annotation);

    /**
     * Gets a list of annotations that this factory can convert into pre-parse plugins
     *
     * @return List of supported annotations
     */
    List<Class<? extends Annotation>> supportedPreParsePluginAnnotations();

    /**
     * Gets a list of annotations that this factory can convert into post-parse plugins
     *
     * @return List of supported annotations
     */
    List<Class<? extends Annotation>> supportedPostParsePluginAnnotations();
}
