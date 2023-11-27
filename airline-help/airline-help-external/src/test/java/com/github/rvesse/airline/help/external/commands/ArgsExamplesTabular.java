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

package com.github.rvesse.airline.help.external.commands;

import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.help.external.ExternalTabularExamples;
import com.github.rvesse.airline.tests.args.Args1;
import com.github.rvesse.airline.help.external.parsers.defaults.TabDelimitedHelpParser;

@Command(name = "args-examples-tabular", description = "args-examples-tabular description")
@ExternalTabularExamples(source = "/com/github/rvesse/airline/help/external/examples.tsv", parser = TabDelimitedHelpParser.class)
public class ArgsExamplesTabular extends Args1 {

}
