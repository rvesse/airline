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

package com.github.rvesse.airline.prompts.builders;

import com.github.rvesse.airline.builder.AbstractChildBuilder;
import com.github.rvesse.airline.prompts.Prompt;
import com.github.rvesse.airline.prompts.formatters.ListFormat;

/**
 * A prompt format builder is a child builder of a {@link PromptBuilder} used to define the prompt format in a fluent
 * style
 * 
 * @param <TOption> Option type
 */
public abstract class PromptFormatBuilder<TOption>
        extends AbstractChildBuilder<ListFormat<TOption>, Prompt<TOption>, PromptBuilder<TOption>> {

    protected PromptFormatBuilder(PromptBuilder<TOption> parentBuilder) {
        super(parentBuilder);
    }

}
