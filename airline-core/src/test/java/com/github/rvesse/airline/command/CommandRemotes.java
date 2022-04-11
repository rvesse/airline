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
package com.github.rvesse.airline.command;

import javax.inject.Inject;

import com.github.rvesse.airline.annotations.Command;

@Command(name = "remotes",
         description = "A command whose name is an extension of another commands name")
public class CommandRemotes {
    // Intentionally using old javax.inject.Inject to verify Airline copes with mixtures of old and new @Inject
    // annotation
    // Your IDE may complain it can't see this annotation, it comes from shaded repackaging in the
    // airline-backcompact-javaxinject module to force different Maven coordinates as you can't have multiple
    // versions of the same Maven coordinates in your dependency tree
    @Inject
    public CommandMain commandMain;

}
