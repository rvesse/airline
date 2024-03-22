/*
 * Copyright (C) 2010-22 the original author or authors.
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertiesFileSupplier extends AbstractPropertiesSupplier {

    private final File file;

    public PropertiesFileSupplier(File f) {
        super(load(f));
        this.file = f;
    }

    private static Properties load(File f) {
        if (!f.exists()) {
            throw new IllegalArgumentException("Properties file " + f.getAbsolutePath() + " does not exist");
        }

        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream(f)) {
            properties.load(input);
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to read properties file " + f.getAbsolutePath(), e);
        }
        return properties;
    }

    @Override
    public String description() {
        return "Properties file " + this.file.getAbsolutePath();
    }
}
