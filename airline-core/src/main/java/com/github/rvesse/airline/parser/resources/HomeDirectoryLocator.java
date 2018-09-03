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

package com.github.rvesse.airline.parser.resources;

import java.io.File;

import org.apache.commons.lang3.StringUtils;

/**
 * A resource locator that allows the {@code ~/} or {@code ~\} alias
 * (depending on your platforms file separator) to be used to refer to the users
 * home directory
 * <p>
 * Note that if the users home directory cannot be obtained then this locator
 * will not resolve the alias
 * </p>
 * 
 * @author rvesse
 *
 */
public class HomeDirectoryLocator extends FileLocator {

    @Override
    protected String resolve(String searchLocation) {
        // Apply parent resolve() first as this strips off any file:// prefix
        searchLocation = super.resolve(searchLocation);
        
        // Find the home directory since we will potentially use this to resolve
        // the special ~/ alias
        File homeDir;
        if (!StringUtils.isEmpty(System.getProperty("user.home"))) {
            homeDir = new File(System.getProperty("user.home"));
        } else {
            // Can't resolve as no home directory available
            return searchLocation;
        }

        // Expand ~/ or ~\ alias (platform dependent)
        if (searchLocation.startsWith("~" + File.separator)) {
            // Remember we need to ensure there is a separator between the home
            // directory and the rest of the path
            searchLocation = homeDir.getAbsolutePath()
                    + searchLocation.substring(homeDir.getAbsolutePath().endsWith(File.separator) ? 2 : 1);
        }

        return searchLocation;
    }
}
