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
package com.github.rvesse.airline.help.sections.common;

import com.github.rvesse.airline.help.sections.HelpFormat;
import com.github.rvesse.airline.help.sections.HelpSection;

public class BasicSection extends BasicHint implements HelpSection {
    
    private final String title, postamble;
    private final int order;

    public BasicSection(String title, int suggestedOrder, String preamble, String postamble, HelpFormat format, String[]... blocks) {
        super(preamble, format, blocks);
        this.title = title;
        this.postamble = postamble;
        this.order = suggestedOrder;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public String getPostamble() {
        return this.postamble;
    }

    @Override
    public int suggestedOrder() {
        return this.order;
    }

}
