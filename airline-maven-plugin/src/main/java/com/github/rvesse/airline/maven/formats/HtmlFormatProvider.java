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

import java.io.File;
import org.apache.commons.lang3.StringUtils;

import com.github.rvesse.airline.help.CommandGroupUsageGenerator;
import com.github.rvesse.airline.help.CommandUsageGenerator;
import com.github.rvesse.airline.help.GlobalUsageGenerator;
import com.github.rvesse.airline.help.html.HtmlCommandUsageGenerator;

/**
 * Provides HTML help generators
 * 
 * @author rvesse
 *
 */
public class HtmlFormatProvider implements FormatProvider {

    @Override
    public String getDefaultMappingName() {
        return "HTML";
    }

    @Override
    public String getExtension(FormatOptions options) {
        return ".html";
    }

    private String[] getStylesheets(FormatOptions options) {
        String sheets = options.getProperty("stylesheet");
        if (StringUtils.isBlank(sheets)) {
            return new String[] { HtmlCommandUsageGenerator.DEFAULT_STYLESHEET };
        } else if (sheets.contains(",")) {
            return StringUtils.split(sheets, ',');
        } else {
            return new String[] { sheets };
        }
    }

    @Override
    public CommandUsageGenerator getCommandGenerator(File outputDirectory, FormatOptions options) {
        return new HtmlCommandUsageGenerator(options.includeHidden(), getStylesheets(options));
    }

    @Override
    public CommandGroupUsageGenerator<Object> getGroupGenerator(File outputDirectory, FormatOptions options) {
        return null;
    }

    @Override
    public GlobalUsageGenerator<Object> getGlobalGenerator(File outputDirectory, FormatOptions options) {
        return null;
    }

}
