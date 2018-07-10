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
package com.github.rvesse.airline.guice;

import com.github.rvesse.airline.annotations.Command;

import javax.inject.Inject;

@Command(name = "whoami")
public class WhoamiCommand implements Runnable {

    private final Output output;

    @Inject
    public WhoamiCommand(Output output) {
        this.output = output;
    }

    @Override
    public void run() {
        output.println("=> root");
    }
}
