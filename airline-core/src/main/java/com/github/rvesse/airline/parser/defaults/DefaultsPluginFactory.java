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
package com.github.rvesse.airline.parser.defaults;

import com.github.rvesse.airline.annotations.Defaults;
import com.github.rvesse.airline.parser.ParserUtil;
import com.github.rvesse.airline.parser.plugins.ParserPluginFactory;
import com.github.rvesse.airline.parser.plugins.PostParsePlugin;
import com.github.rvesse.airline.parser.plugins.PreParsePlugin;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A factory that can create a {@link DefaultsPlugin} from a {@link Defaults} annotation applied to the command or CLI
 * class
 */
public class DefaultsPluginFactory implements ParserPluginFactory {
    @Override
    public <T> PreParsePlugin<T> createPreParsePlugin(Annotation annotation) {
        return null;
    }

    @Override
    public <T> PostParsePlugin<T> createPostParsePlugin(Annotation annotation) {
        if (annotation instanceof Defaults) {
            Defaults defaults = (Defaults) annotation;
            List<DefaultsSupplier> suppliers = new ArrayList<>();
            for (Class<? extends DefaultsSupplier> supplierCls : defaults.suppliers()) {
                suppliers.add(ParserUtil.createInstance(supplierCls));
            }
            return new DefaultsPlugin<>(suppliers);
        }

        return null;
    }

    @Override
    public List<Class<? extends Annotation>> supportedPreParsePluginAnnotations() {
        return Collections.emptyList();
    }

    @Override
    public List<Class<? extends Annotation>> supportedPostParsePluginAnnotations() {
        return List.of(Defaults.class);
    }
}
