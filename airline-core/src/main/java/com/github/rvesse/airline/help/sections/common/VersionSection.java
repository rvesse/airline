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
package com.github.rvesse.airline.help.sections.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import com.github.rvesse.airline.utils.StringUtils;

import com.github.rvesse.airline.help.sections.HelpFormat;
import com.github.rvesse.airline.help.sections.HelpSection;

public class VersionSection implements HelpSection {

    private List<VersionInfo> versions = new ArrayList<>();
    private final boolean tabular;
    private final String[] titles;

    public VersionSection(String[] dataSources, String componentProperty, String versionProperty, String buildProperty,
            String dateProperty, String[] additionalProperties, String[] additionalTitles, boolean suppressErrors,
            boolean tabular) {

        this.tabular = tabular;
        this.titles = new String[additionalProperties != null ? additionalProperties.length : 0];
        for (int i = 0; i < this.titles.length; i++) {
            this.titles[i] = i < additionalTitles.length ? additionalTitles[i] : additionalProperties[i];
        }

        for (String dataSource : dataSources) {
            try {
                Properties source = loadDataSource(dataSource);
                if (source == null) {
                    if (suppressErrors)
                        continue;
                    sourceNotFound(dataSource);
                }

                VersionInfo info = new VersionInfo();
                info.component = source.getProperty(componentProperty);
                info.version = source.getProperty(versionProperty);
                info.build = source.getProperty(buildProperty);
                info.date = source.getProperty(dateProperty);

                for (int i = 0; i < additionalProperties.length; i++) {
                    String title = titles[i];
                    info.additional.put(title, source.getProperty(additionalProperties[i]));
                }

                this.versions.add(info);
            } catch (IOException e) {
                if (suppressErrors)
                    continue;
                sourceNotFound(dataSource);
            }
        }

        if (this.versions.size() == 0) {
            if (!suppressErrors)
                throw new IllegalArgumentException(
                        String.format("@Version annotation specifies no valid version information sources - %s",
                                StringUtils.join(dataSources, ',')));
        }
    }

    private void sourceNotFound(String dataSource) {
        throw new IllegalArgumentException(String.format(
                "@Version annotation specifies %s as a data source which could not be resolved to a classpath resource/local file",
                dataSource));
    }

    private Properties loadDataSource(String source) throws IOException {
        if (source.startsWith("file://")) {
            return loadFile(source);
        } else {
            Properties p = loadResource(source);
            if (p == null)
                p = loadFile(source);
            return p;
        }
    }

    private Properties loadResource(String source) throws IOException {
        try (InputStream input = VersionSection.class.getResourceAsStream(source)) {
            if (input == null)
                return null;
            Properties p = new Properties();
            p.load(input);
            input.close();
            return p;
        }
    }

    private Properties loadFile(String source) throws IOException, FileNotFoundException {
        File f = new File(source);
        if (f.exists() && f.isFile() && f.canRead()) {
            Properties p = new Properties();
            try (InputStream input = new FileInputStream(f)) {
                p.load(input);
                input.close();
            }
            return p;
        }
        return null;
    }

    @Override
    public String getPreamble() {
        return null;
    }

    @Override
    public HelpFormat getFormat() {
        if (this.versions.size() == 0)
            return HelpFormat.NONE_PRINTABLE;
        return this.tabular ? HelpFormat.TABLE_WITH_HEADERS : HelpFormat.LIST;

    }

    @Override
    public int numContentBlocks() {
        if (this.tabular) {
            return 4 + this.titles.length;
        } else {
            return this.versions.size();
        }
    }

    @Override
    public String[] getContentBlock(int blockNumber) {
        if (blockNumber < 0 || blockNumber > this.numContentBlocks())
            throw new IndexOutOfBoundsException();

        if (this.tabular) {
            String[] column = new String[this.versions.size() + 1];
            for (int row = 0; row < this.versions.size(); row++) {
                switch (blockNumber) {
                case 0:
                    column[0] = "Component";
                    this.versions.get(row).addComponent(column, row + 1);
                    break;
                case 1:
                    column[0] = "Version";
                    this.versions.get(row).addVersion(column, row + 1);
                    break;
                case 2:
                    column[0] = "Build";
                    this.versions.get(row).addBuild(column, row + 1);
                    break;
                case 3:
                    column[0] = "Build Date";
                    this.versions.get(row).addBuildDate(column, row + 1);
                    break;
                default:
                    column[0] = this.titles[blockNumber - 4];
                    this.versions.get(row).addAdditionalColumn(column, row + 1, this.titles[blockNumber - 4]);
                    break;
                }
            }
            return column;
        } else {
            return this.versions.get(blockNumber).toList();
        }
    }

    @Override
    public String getTitle() {
        return CommonSections.TITLE_VERSION;
    }

    @Override
    public String getPostamble() {
        return null;
    }

    @Override
    public int suggestedOrder() {
        return CommonSections.ORDER_VERSION;
    }

    private static class VersionInfo {
        public String component, version, build, date;
        public Map<String, String> additional = new HashMap<>();

        public String[] toList() {
            List<String> items = new ArrayList<>();
            if (this.component != null)
                items.add(String.format("Component: %s", this.component));
            if (this.version != null)
                items.add(String.format("Version: %s", this.version));
            if (this.build != null)
                items.add(String.format("Build: %s", this.build));
            if (this.date != null)
                items.add(String.format("Build Date: %s", this.date));
            for (Entry<String, String> kvp : additional.entrySet()) {
                if (StringUtils.isEmpty(kvp.getValue()))
                    continue;
                items.add(String.format("%s: %s", kvp.getKey(), kvp.getValue()));
            }

            return items.toArray(new String[items.size()]);
        }

        private void fillColumn(String[] column, int row, String value) {
            column[row] = value;
        }

        public void addComponent(String[] column, int row) {
            fillColumn(column, row, this.component);
        }

        public void addVersion(String[] column, int row) {
            fillColumn(column, row, this.version);
        }

        public void addBuild(String[] column, int row) {
            fillColumn(column, row, this.build);
        }

        public void addBuildDate(String[] column, int row) {
            fillColumn(column, row, this.date);
        }

        public void addAdditionalColumn(String[] column, int row, String title) {
            fillColumn(column, row, this.additional.get(title));
        }
    }

}
