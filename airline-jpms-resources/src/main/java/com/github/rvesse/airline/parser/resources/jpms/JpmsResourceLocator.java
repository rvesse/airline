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
package com.github.rvesse.airline.parser.resources.jpms;

import com.github.rvesse.airline.parser.resources.ResourceLocator;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.Resource;
import io.github.classgraph.ScanResult;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * A resource locator that finds resources using {@link ClassGraph} that works in JPMS runtime contexts where the
 * standard {@link com.github.rvesse.airline.parser.resources.ClasspathLocator} does not.
 *
 */
public class JpmsResourceLocator implements ResourceLocator {

    @Override
    public InputStream open(String searchLocation, String filename) throws IOException {
        if (searchLocation == null) {
            return null;
        }

        // Build the expected resource name
        StringBuilder resourceName = new StringBuilder();
        resourceName.append(searchLocation);
        if (!searchLocation.endsWith("/") && StringUtils.isNotEmpty(filename)) {
            resourceName.append("/");
        }
        resourceName.append(filename);

        ScanResult scanResult = new ClassGraph().scan();
        for (Resource resource : scanResult.getResourcesWithPath(resourceName.toString()).nonClassFilesOnly()) {
            // IMPORTANT - Must wrap so that we close the ScanResult when we are finished reading the resource otherwise
            // we'll leave resources open unnecessarily which can impact application performance
            return new ScanResultInputStream(scanResult, resource.open());
        }

        return null;

    }

}
