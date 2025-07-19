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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.github.rvesse.airline.help.sections.HelpFormat;
import com.github.rvesse.airline.help.sections.HelpHint;

public class BasicHint implements HelpHint {
    
    private final String preamble;
    private final HelpFormat format;
    private final List<String[]> blocks;
    
    public BasicHint(String preamble, HelpFormat format, String[]... blocks) {
        this.preamble = preamble;
        this.format = format != null ? format : HelpFormat.UNKNOWN;
        this.blocks = new ArrayList<>(blocks.length);
        Collections.addAll(this.blocks, blocks);
    }

    @Override
    public String getPreamble() {
        return this.preamble;
    }

    @Override
    public HelpFormat getFormat() {
        return this.format;
    }

    @Override
    public int numContentBlocks() {
        return this.blocks.size();
    }

    @Override
    public String[] getContentBlock(int blockNumber) {
        return this.blocks.get(blockNumber);
    }

}
