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

package com.github.rvesse.airline.prompts.errors;

import java.util.concurrent.TimeoutException;

import com.github.rvesse.airline.prompts.Prompt;

public class PromptTimeoutException extends PromptException {
    private static final long serialVersionUID = 8701888121375546762L;

    public PromptTimeoutException(Prompt<?> prompt, TimeoutException e) {
        super(String.format("Failed to receive a response to prompt within specified timeout (%,d %s)",
                prompt.getTimeout(), prompt.getTimeoutUnit().toString()), e);
    }

}
