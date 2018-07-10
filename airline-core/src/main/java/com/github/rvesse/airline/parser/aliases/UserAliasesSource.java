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
package com.github.rvesse.airline.parser.aliases;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.github.rvesse.airline.model.AliasMetadata;
import com.github.rvesse.airline.parser.resources.FileLocator;
import com.github.rvesse.airline.parser.resources.HomeDirectoryLocator;
import com.github.rvesse.airline.parser.resources.ResourceLocator;
import com.github.rvesse.airline.parser.resources.WorkingDirectoryLocator;

/**
 * Represents the source of user defined aliases
 * 
 * @author rvesse
 *
 * @param <C>
 *            Command type
 */
public class UserAliasesSource<C> {

    //@formatter:off
    /**
     * Default user alias source locators
     */
    public static final ResourceLocator[] DEFAULT_LOCATORS = 
        { 
            new WorkingDirectoryLocator(), 
            new HomeDirectoryLocator(),
            new FileLocator() 
        };
    //@formatter:on

    private final List<ResourceLocator> locators;
    private final List<String> searchLocations;
    private final String filename, prefix;

    public UserAliasesSource(String filename, String prefix, String... searchLocations) {
        this(filename, prefix, null, Arrays.asList(searchLocations));
    }

    public UserAliasesSource(String filename, String prefix, List<ResourceLocator> locators,
            List<String> searchLocations) {
        this.filename = filename;
        this.prefix = prefix;
        this.searchLocations = Collections.unmodifiableList(searchLocations);
        this.locators = locators == null ? Arrays.asList(DEFAULT_LOCATORS) : Collections.unmodifiableList(locators);

        if (StringUtils.isBlank(this.filename)) {
            throw new IllegalArgumentException("Filename cannot be null/empty/blank");
        }
        if (this.searchLocations.size() == 0) {
            throw new IllegalArgumentException("At least one search location must be specified");
        }
    }

    /**
     * Gets the filename of the configuration file that will be scanned for
     * alias definitions
     * 
     * @return Configuration file name
     */
    public String getFilename() {
        return this.filename;
    }

    /**
     * Gets the search locations where the configuration file may be located in
     * order of preference
     * 
     * @return Search locations in order of preference
     */
    public List<String> getSearchLocations() {
        return this.searchLocations;
    }

    /**
     * Gets the prefix that is used to distinguish alias definitions from other
     * property definitions in the configuration file
     * <p>
     * If this is null/empty/blank then no prefix is in effect
     * </p>
     * 
     * @return Prefix
     */
    public String getPrefix() {
        return this.prefix;
    }

    /**
     * Gets the locators that are used to resolve search locations to actual
     * usable locations
     * 
     * @return User alias locators
     */
    public List<ResourceLocator> getLocators() {
        return this.locators;
    }

    /**
     * Loads the alias metadata based on the configured sources
     * 
     * @return Alias metadata
     * @throws FileNotFoundException
     *             Thrown if unable to find a properties file
     * @throws IOException
     *             Thrown if unable to read a properties file
     */
    public List<AliasMetadata> load() throws FileNotFoundException, IOException {
        Properties properties = new Properties();

        // Search locations in reverse order overwriting previously found values
        // each time. Thus the first location in the list has highest precedence
        Set<String> loaded = new HashSet<>();
        for (int i = searchLocations.size() - 1; i >= 0; i--) {
            // Check an actual location
            String loc = searchLocations.get(i);
            if (StringUtils.isBlank(loc))
                continue;

            // Don't read property files multiple times
            if (loaded.contains(loc))
                continue;

            for (ResourceLocator locator : this.locators) {
                try (InputStream input = locator.open(loc, filename)) {
                    // May not be supported by the locator in which case null
                    // will be returned and we should try the next locator
                    if (input == null)
                        continue;

                    properties.load(input);

                    // If we successfully load the input no need to try further
                    // locators
                    break;
                } finally {
                    // Remember we've tried to read this file so we don't try
                    // and read it multiple times
                    loaded.add(loc);
                }
            }

        }

        // Strip any irrelevant properties
        if (StringUtils.isNotBlank(prefix)) {
            List<Object> keysToRemove = new ArrayList<Object>();
            for (Object key : properties.keySet()) {
                if (!key.toString().startsWith(prefix))
                    keysToRemove.add(key);
            }
            for (Object key : keysToRemove) {
                properties.remove(key);
            }
        }

        // Generate the aliases
        List<AliasMetadata> aliases = new ArrayList<>();
        for (Object key : properties.keySet()) {
            String name = key.toString();
            if (!StringUtils.isBlank(prefix))
                name = name.substring(prefix.length());

            String value = properties.getProperty(key.toString());
            if (StringUtils.isEmpty(value)) {
                aliases.add(new AliasMetadata(name, Collections.<String> emptyList()));
                continue;
            }

            // Process property value into arguments
            List<String> args = AliasArgumentsParser.parse(value);
            aliases.add(new AliasMetadata(name, args));
        }

        return aliases;
    }
}
