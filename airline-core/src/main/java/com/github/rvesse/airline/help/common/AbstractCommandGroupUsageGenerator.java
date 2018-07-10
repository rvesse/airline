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
import java.util.Comparator;

import com.github.rvesse.airline.Channels;
import com.github.rvesse.airline.help.CommandGroupUsageGenerator;
import com.github.rvesse.airline.help.UsageHelper;
import com.github.rvesse.airline.help.sections.HelpHint;
import com.github.rvesse.airline.model.CommandGroupMetadata;
import com.github.rvesse.airline.model.CommandMetadata;
import com.github.rvesse.airline.model.GlobalMetadata;
import com.github.rvesse.airline.model.OptionMetadata;

/**
 * Abstract command group usage generator
 */
public abstract class AbstractCommandGroupUsageGenerator<T> extends AbstractUsageGenerator
        implements CommandGroupUsageGenerator<T> {

    public AbstractCommandGroupUsageGenerator() {
        this(UsageHelper.DEFAULT_HINT_COMPARATOR, UsageHelper.DEFAULT_OPTION_COMPARATOR,
                UsageHelper.DEFAULT_COMMAND_COMPARATOR, false);
    }

    public AbstractCommandGroupUsageGenerator(Comparator<? super HelpHint> hintComparator,
            Comparator<? super OptionMetadata> optionComparator, Comparator<? super CommandMetadata> commandComparator,
            boolean includeHidden) {
        super(hintComparator, optionComparator, commandComparator, includeHidden);
    }

    @Override
    public void usage(GlobalMetadata<T> global, CommandGroupMetadata[] groups) throws IOException {
        usage(global, groups, Channels.output());
    }
}
