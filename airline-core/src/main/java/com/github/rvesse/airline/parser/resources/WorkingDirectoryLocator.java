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
import java.nio.file.Paths;

/**
 * A user alias locator that allows the {@code ./} or {@code .\} alias
 * (depending on your platforms file separator) to be used to refer to the
 * current working directory
 * 
 * @author rvesse
 *
 */
public class WorkingDirectoryLocator extends FileLocator {

    @Override
    protected String resolve(String searchLocation) {
        // Apply parent resolve() first as this strips off any file:// prefix
        searchLocation = super.resolve(searchLocation);

        // Find the working directory since we will also potentially use this to
        // resolve the special ./ alias
        File workingDir = Paths.get("").toAbsolutePath().toFile();

        // Can't resolve if no working directory available
        if (workingDir == null)
            return searchLocation;

        // Expand ./ or .\ alias (platform dependent)
        if (searchLocation.startsWith("." + File.separator)) {
            // Remember we need to ensure there is a separator between the
            // working directory and the rest of the path
            searchLocation = workingDir.getAbsolutePath()
                    + searchLocation.substring(workingDir.getAbsolutePath().endsWith(File.separator) ? 2 : 1);
        }

        return searchLocation;
    }
}
