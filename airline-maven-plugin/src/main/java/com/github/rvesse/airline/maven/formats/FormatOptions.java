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
package com.github.rvesse.airline.maven.formats;

import java.util.Properties;

import com.github.rvesse.airline.help.common.AbstractUsageGenerator;
import com.github.rvesse.airline.help.man.ManSections;
import com.github.rvesse.airline.maven.RawFormatOptions;

public class FormatOptions {
    private final Integer columns, manSection;
    private final Boolean includeHidden, multiFile;
    private final Properties properties;
    private final FormatOptions parent;

    public FormatOptions() {
        this(new RawFormatOptions(), null);
    }

    public FormatOptions(RawFormatOptions options) {
        this(options, null);
    }

    public FormatOptions(RawFormatOptions options, FormatOptions parentOptions) {
        this.parent = parentOptions;
        this.columns = options.columns;
        this.manSection = options.manSection;
        this.includeHidden = options.includeHidden;
        this.multiFile = options.multiFile;
        this.properties = options.properties;
    }

    public int getColumns() {
        return this.columns != null ? this.columns.intValue()
                : this.parent != null ? this.parent.getColumns() : AbstractUsageGenerator.DEFAULT_COLUMNS;
    }

    public int getManSection() {
        return this.manSection != null ? this.manSection.intValue()
                : this.parent != null ? this.parent.getManSection() : ManSections.GENERAL_COMMANDS;
    }

    public boolean includeHidden() {
        return this.includeHidden != null ? this.includeHidden.booleanValue()
                : this.parent != null ? this.parent.includeHidden() : false;
    }

    public boolean useMultipleFiles() {
        return this.multiFile != null ? this.multiFile.booleanValue()
                : this.parent != null ? this.parent.useMultipleFiles() : false;
    }

    public Object get(Object key) {
        if (this.properties != null) {
            Object value = this.properties.get(key);
            if (value == null && this.parent != null) {
                value = this.parent.get(key);
            }
            return value;
        } else if (this.parent != null) {
            return this.parent.get(key);
        } else {
            return null;
        }
    }

    public String getProperty(String key) {
        if (this.properties != null) {
            String value = this.properties.getProperty(key);
            if (value == null && this.parent != null) {
                value = this.parent.getProperty(key);
            }
            return value;
        } else if (this.parent != null) {
            return this.parent.getProperty(key);
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{ columns=").append(this.columns);
        builder.append(", manSection=").append(this.manSection);
        builder.append(", includeHidden=").append(this.includeHidden);
        builder.append(", multiFile=").append(this.multiFile);
        if (parent != null) {
            builder.append(", parent=").append(this.parent.toString());
        }
        builder.append("}");
        return builder.toString();
    }
}
