package com.github.rvesse.airline.help.sections;

import com.github.rvesse.airline.annotations.Cli;
import com.github.rvesse.airline.annotations.help.Discussion;
import com.github.rvesse.airline.args.Args1;
import com.github.rvesse.airline.command.CommandRemove;
import com.github.rvesse.airline.help.Help;

@Cli(defaultCommand = Help.class, commands = { Help.class, Args1.class, CommandRemove.class }, name = "test")
@Discussion(paragraphs = { "Foo", "Bar" })
public class CliWithSections {

}
