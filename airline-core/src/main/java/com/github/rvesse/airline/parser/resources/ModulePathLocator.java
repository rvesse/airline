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

import java.io.IOException;
import java.io.InputStream;

public class ModulePathLocator implements ResourceLocator {
    @Override
    public InputStream open(String searchLocation, String filename) throws IOException {
        if (searchLocation == null) {
            return null;
        }

        // Strip off Classpath URI prefix if present
        if (searchLocation.startsWith(ClasspathLocator.CLASSPATH_URI_PREFIX)) {
            searchLocation = searchLocation.substring(ClasspathLocator.CLASSPATH_URI_PREFIX.length());
        }

        // Strip off leading / if present
        // This is because when running on the Module Path the JVM translates the package name of the resource
        // to its associated module by simply replacing / characters with . characters.  Thus, if we have a leading
        // slash we end up with an invalid module name like .foo.bar instead of foo.bar as was intended.  And as a
        // result we never correctly locate resources when running on the Module Path if we didn't do this.
        if (searchLocation.startsWith("/")) {
            searchLocation = searchLocation.length() > 1 ? searchLocation.substring(1) : "";
        }

        // Build the expected resource name
        StringBuilder resourceName = new StringBuilder();
        resourceName.append(searchLocation);
        if (!searchLocation.endsWith("/") && searchLocation.length() > 0) {
            resourceName.append("/");
        }
        resourceName.append(filename);

        // Try to open the classpath resource
        InputStream resourceStream = ClassLoader.getSystemResourceAsStream(resourceName.toString());
        if (resourceStream != null) {
            return resourceStream;
        }

        // If the search location is not a package then return that directly if
        // it is a valid location
        if (!searchLocation.endsWith("/")) {
            InputStream locStream = ClassLoader.getSystemResourceAsStream(searchLocation);
            if (locStream != null) {
                return locStream;
            }
        }

        return null;
    }
}
