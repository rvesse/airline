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

/**
 * A resource locator that supports placeholders of the form
 * <code>${name}</code> which when found calls the
 * {@link #resolvePlaceholder(String)} method to try and resolve the encountered
 * placeholders. If the placeholder is not resolved then the string is left
 * as-is.
 * 
 * @author rvesse
 *
 */
public abstract class AbstractPlaceholderLocator extends FileLocator {

    private static final String PLACEHOLDER_START = "${";
    private static final String PLACEHOLDER_END = "}";

    @Override
    protected String resolve(String searchLocation) {
        if (!searchLocation.contains(PLACEHOLDER_START) && !searchLocation.contains(PLACEHOLDER_END)) {
            return searchLocation;
        }

        // Resolve all placeholders
        StringBuilder output = new StringBuilder();
        char[] cs = searchLocation.toCharArray();
        for (int i = 0; i < cs.length; i++) {
            char c = cs[i];

            switch (c) {
            case '$':
                // Possible start of placeholder
                if (i < cs.length - 1 && cs[i + 1] == '{') {
                    // Start of a placeholder
                    // Find the end
                    int j;
                    for (j = i + 1; j < cs.length; j++) {
                        if (cs[j] == '}')
                            break;
                    }
                    if (j == cs.length && cs[j - 1] != '}') {
                        // Invalid placeholder as no terminating }
                        // Just append the remaining string and stop
                        output.append(cs, i, cs.length - i);
                        i = j;
                        break;
                    } else {
                        // Valid placeholder
                        String placeholder = new String(cs, i + 2, j - i - 2);
                        String resolved = resolvePlaceholder(placeholder);
                        if (resolved == null) {
                            // If a placeholder fails to resolve then the whole location should fail to resolve
                            return searchLocation;
                        }
                        output.append(resolved);
                        i = j;
                        continue;
                    }
                }
                // Intentionally dropping through if not a placeholder start
            default:
                // Regular character
                output.append(c);
            }
        }

        return output.toString();
    }

    /**
     * Resolves a placeholder
     * 
     * @param name
     *            Placeholder name
     * @return Resolved placeholder
     */
    protected abstract String resolvePlaceholder(String name);
}
