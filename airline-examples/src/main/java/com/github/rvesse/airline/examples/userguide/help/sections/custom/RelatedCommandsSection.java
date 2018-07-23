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

package com.github.rvesse.airline.examples.userguide.help.sections.custom;

import com.github.rvesse.airline.help.sections.HelpFormat;
import com.github.rvesse.airline.help.sections.common.BasicSection;
import com.github.rvesse.airline.help.sections.common.CommonSections;

public class RelatedCommandsSection extends BasicSection {

    public RelatedCommandsSection(String[] commands) {
        super("Related Commands", CommonSections.ORDER_EXAMPLES + 1,
                "The following related commands may also be of interest:", null, HelpFormat.LIST, commands);
    }

}
