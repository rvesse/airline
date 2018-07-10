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
package com.github.rvesse.airline.help.common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.github.rvesse.airline.Channels;
import com.github.rvesse.airline.help.GlobalUsageGenerator;
import com.github.rvesse.airline.help.UsageHelper;
import com.github.rvesse.airline.help.sections.HelpHint;
import com.github.rvesse.airline.model.CommandGroupMetadata;
import com.github.rvesse.airline.model.CommandMetadata;
import com.github.rvesse.airline.model.GlobalMetadata;
import com.github.rvesse.airline.model.OptionMetadata;

/**
 * Abstract global usage generator
 */
public abstract class AbstractGlobalUsageGenerator<T> extends AbstractUsageGenerator
        implements GlobalUsageGenerator<T> {

    private final Comparator<? super CommandGroupMetadata> commandGroupComparator;

    public AbstractGlobalUsageGenerator() {
        this(UsageHelper.DEFAULT_HINT_COMPARATOR, UsageHelper.DEFAULT_OPTION_COMPARATOR,
                UsageHelper.DEFAULT_COMMAND_COMPARATOR, UsageHelper.DEFAULT_COMMAND_GROUP_COMPARATOR, false);
    }

    public AbstractGlobalUsageGenerator(boolean includeHidden) {
        this(UsageHelper.DEFAULT_HINT_COMPARATOR, UsageHelper.DEFAULT_OPTION_COMPARATOR,
                UsageHelper.DEFAULT_COMMAND_COMPARATOR, UsageHelper.DEFAULT_COMMAND_GROUP_COMPARATOR, includeHidden);
    }

    public AbstractGlobalUsageGenerator(Comparator<? super HelpHint> hintComparator,
            Comparator<? super OptionMetadata> optionComparator, Comparator<? super CommandMetadata> commandComparator,
            Comparator<? super CommandGroupMetadata> commandGroupComparator, boolean includeHidden) {
        super(hintComparator, optionComparator, commandComparator, includeHidden);
        this.commandGroupComparator = commandGroupComparator;
    }

    @Override
    public void usage(GlobalMetadata<T> global) throws IOException {
        usage(global, Channels.output());
    }

    /**
     * Sorts the command groups assumign a non-null comparator was provided at
     * instantiation time
     *
     * @param groups
     *            Command groups
     * @return Sorted command groups
     */
    protected List<CommandGroupMetadata> sortCommandGroups(List<CommandGroupMetadata> groups) {
        if (this.commandGroupComparator != null) {
            groups = new ArrayList<>(groups);
            Collections.sort(groups, this.commandGroupComparator);
        }
        return groups;
    }
}
