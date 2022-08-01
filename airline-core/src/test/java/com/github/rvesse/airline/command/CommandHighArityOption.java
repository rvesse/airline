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

import com.github.rvesse.airline.annotations.AirlineModule;
import com.github.rvesse.airline.annotations.Arguments;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.Option;

import java.util.List;

@Command(name = "cmd", description = "A command with an option that has a high arity option")
public class CommandHighArityOption {
	@AirlineModule
	public CommandMain commandMain;
	
	@Option(name = "--option", description = "An option with high arity", arity = 4)
	public List<String> option;
	
	@Option(name = "--option2", description = "Just another option")
	public String option2;
	
	@Arguments(description = "The rest of arguments")
	public List<String> args;
}
