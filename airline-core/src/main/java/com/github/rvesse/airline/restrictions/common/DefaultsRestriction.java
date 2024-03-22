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
package com.github.rvesse.airline.restrictions.common;

import com.github.rvesse.airline.help.sections.HelpFormat;
import com.github.rvesse.airline.help.sections.HelpHint;
import com.github.rvesse.airline.restrictions.AbstractCommonRestriction;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

/**
 * A restriction that indicates that the option/arguments value may receive a default value from elsewhere
 */
public class DefaultsRestriction extends AbstractCommonRestriction implements HelpHint {

    private final Set<String> keys = new LinkedHashSet<>();

    /**
     * Creates a new defaults restriction
     *
     * @param keys Keys
     */
    public DefaultsRestriction(String[] keys) {
        CollectionUtils.addAll(this.keys, keys);
        ensureKeys();
    }

    private void ensureKeys() {
        if (this.keys.isEmpty()) {
            throw new IllegalArgumentException(
                    "A Defaults restriction requires at least one key to use in retrieving defaults");
        }
    }

    public DefaultsRestriction(Collection<String> keys) {
        CollectionUtils.addAll(this.keys, keys);
        ensureKeys();
    }

    /**
     * Gets the possible keys
     *
     * @return Keys
     */
    public String[] keys() {
        return this.keys.toArray(new String[0]);
    }


    @Override
    public String getPreamble() {
        return "The value for this option can be provided by setting one of the following keys in your defaults configuration, see the Defaults section of the command help for how to set these:";
    }

    @Override
    public HelpFormat getFormat() {
        return HelpFormat.LIST;
    }

    @Override
    public int numContentBlocks() {
        return 1;
    }

    @Override
    public String[] getContentBlock(int blockNumber) {
        if (blockNumber != 0) {
            throw new IndexOutOfBoundsException();
        }
        return this.keys.toArray(new String[0]);
    }
}
