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
package com.github.rvesse.airline.help;

import com.github.rvesse.airline.help.sections.HelpHint;
import com.github.rvesse.airline.model.CommandGroupMetadata;
import com.github.rvesse.airline.model.CommandMetadata;
import com.github.rvesse.airline.model.OptionMetadata;

import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Function;

public interface UsageHelper {

    /**
     * Default comparator for help hints
     * <p>
     * Compares by class name of the implementation to give a predictable order
     * in output. Where multiple instances of same implementation hint exists
     * compares by identity hash codes of the instances.
     * </p>
     */
    Comparator<HelpHint> DEFAULT_HINT_COMPARATOR = Comparator
        .comparing(HelpHint::getClass, Comparator.comparing(Class::getName))
        .thenComparing(Function.identity(), Comparator.comparingInt(System::identityHashCode));

    /**
     * Default comparator for options
     * <p>
     * Compares against the user readable portion of the option name omitting
     * any leading {@code -} characters
     * </p>
     */
    Comparator<OptionMetadata> DEFAULT_OPTION_COMPARATOR = Comparator
            .comparing((OptionMetadata o) -> o.getOptions().iterator().next().replaceFirst("^-+", ""), String.CASE_INSENSITIVE_ORDER.thenComparing(Comparator.reverseOrder())
            .thenComparingInt(System::identityHashCode));

    /**
     * Default comparator for commands
     * <p>
     * Compares by alphabetical ordering
     * </p>
     */
    Comparator<CommandMetadata> DEFAULT_COMMAND_COMPARATOR = Comparator
        .comparing(CommandMetadata::getName, String.CASE_INSENSITIVE_ORDER)
        .thenComparing(CommandMetadata::getName, Comparator.reverseOrder())
        .thenComparing(Function.identity(), Comparator.comparingInt(System::identityHashCode));

    Comparator<CommandGroupMetadata> DEFAULT_COMMAND_GROUP_COMPARATOR = Comparator
            .comparing(CommandGroupMetadata::getName, String::compareToIgnoreCase)
            .thenComparing(CommandGroupMetadata::getName, Comparator.reverseOrder())
            .thenComparing(Function.identity(), Comparator.comparingInt(System::identityHashCode));

    /**
     * Default comparator for exit codes
     * <p>
     * Compares by numerical sorting on the exit codes and then alphabetical
     * sorting on the descriptions
     * </p>
     */
    Comparator<Entry<Integer, String>> DEFAULT_EXIT_CODE_COMPARATOR = Comparator
            .comparingInt(Entry<Integer, String>::getKey)
            .thenComparing(Entry::getValue)
            .thenComparing(Function.identity(), Comparator.comparingInt(System::identityHashCode));

    static String[] toGroupNames(List<CommandGroupMetadata> groupPath) {
        return groupPath.stream()
                .map(CommandGroupMetadata::getName)
                .toArray(String[]::new);
    }
}
