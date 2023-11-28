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
package com.github.rvesse.airline.help.external.sections;

import com.github.rvesse.airline.annotations.Cli;
import com.github.rvesse.airline.annotations.help.external.ExternalExitCodes;
import com.github.rvesse.airline.annotations.help.external.ExternalProse;
import com.github.rvesse.airline.tests.args.Args1;
import com.github.rvesse.airline.tests.command.CommandRemove;
import com.github.rvesse.airline.help.Help;
import com.github.rvesse.airline.help.external.commands.ArgsExamplesTabular;
import com.github.rvesse.airline.help.external.commands.ArgsExamplesTextual;
import com.github.rvesse.airline.help.sections.common.CommonSections;

//@formatter:off
@Cli(
     defaultCommand = Help.class, 
     commands = { 
             Help.class, 
             Args1.class, 
             CommandRemove.class, 
             ArgsExamplesTabular.class,
             ArgsExamplesTextual.class 
     }, 
     name = "test"
)
@ExternalProse(
        title = "Discussion", 
        suggestedOrder = CommonSections.ORDER_DISCUSSION, 
        source = "/com/github/rvesse/airline/help/external/discussion.txt"
)
@ExternalExitCodes(
        source = "/com/github/rvesse/airline/help/external/exit-codes.csv"
)
//@formatter:on
public class CliWithExternalSections {

}
