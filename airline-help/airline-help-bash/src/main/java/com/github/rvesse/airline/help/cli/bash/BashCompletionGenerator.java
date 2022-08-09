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
package com.github.rvesse.airline.help.cli.bash;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.github.rvesse.airline.Accessor;
import com.github.rvesse.airline.annotations.help.bash.BashCompletion;
import com.github.rvesse.airline.help.common.AbstractGlobalUsageGenerator;
import com.github.rvesse.airline.model.ArgumentsMetadata;
import com.github.rvesse.airline.model.CommandGroupMetadata;
import com.github.rvesse.airline.model.CommandMetadata;
import com.github.rvesse.airline.model.GlobalMetadata;
import com.github.rvesse.airline.model.OptionMetadata;
import com.github.rvesse.airline.restrictions.common.AbstractAllowedValuesRestriction;
import com.github.rvesse.airline.utils.predicates.restrictions.AllowedValuesOptionFinder;

public class BashCompletionGenerator<T> extends AbstractGlobalUsageGenerator<T> {

    private static final char NEWLINE = '\n';
    private static final String DOUBLE_NEWLINE = "\n\n";
    private final boolean withDebugging;

    public BashCompletionGenerator() {
        this(false, false);
    }

    /**
     * Creates a new completion generator
     * 
     * @param enableDebugging
     *            Whether to enable debugging, when true the generated script
     *            will do {@code set -o xtrace} in its functions and
     *            {@code set +o xtrace} at the end of its functions
     */
    public BashCompletionGenerator(boolean includeHidden, boolean enableDebugging) {
        super(includeHidden);
        this.withDebugging = enableDebugging;
    }

    @Override
    public void usage(GlobalMetadata<T> global, OutputStream output) throws IOException {
        Writer writer = new OutputStreamWriter(output);

        // Script header
        writeHeader(writer);
        writeHelperFunctions(writer);

        // If there are multiple groups then we will need to generate a function
        // for each
        boolean hasGroups = global.getCommandGroups().size() > 1 || global.getDefaultGroupCommands().size() == 0;
        if (hasGroups) {
            generateGroupFunctions(global, writer);
        }
        // Need to generate functions for default group commands regardless
        generateCommandFunctions(global, writer);

        // Start main completion function
        writeFunctionName(writer, global, true);

        indent(writer, 2);
        writer.append("# Get completion data").append(NEWLINE);
        indent(writer, 2);
        writer.append("CURR_WORD=${COMP_WORDS[COMP_CWORD]}").append(NEWLINE);
        indent(writer, 2);
        writer.append("PREV_WORD=${COMP_WORDS[COMP_CWORD-1]}").append(NEWLINE);
        indent(writer, 2);
        writer.append("CURR_CMD=").append(NEWLINE);
        indent(writer, 2);
        writer.append("if [[ ${COMP_CWORD} -ge 1 ]]; then").append(NEWLINE);
        indent(writer, 4);
        writer.append("CURR_CMD=${COMP_WORDS[1]}").append(NEWLINE);
        indent(writer, 2);
        writer.append("fi").append(DOUBLE_NEWLINE);

        // Prepare list of top level commands and groups
        Set<String> commandNames = new HashSet<>();
        for (CommandMetadata command : global.getDefaultGroupCommands()) {
            if (command.isHidden() && !this.includeHidden())
                continue;
            commandNames.add(command.getName());
        }
        if (hasGroups) {
            for (CommandGroupMetadata group : global.getCommandGroups()) {
                if (group.isHidden() && !this.includeHidden())
                    continue;

                commandNames.add(group.getName());
            }
        }
        if (global.getDefaultCommand() != null)
            commandNames.add(global.getDefaultCommand().getName());
        writeWordListVariable(writer, 2, "COMMANDS", commandNames.iterator());

        // Firstly check whether we are only completing the group or command
        indent(writer, 2);
        writer.append("if [[ ${COMP_CWORD} -eq 1 ]]; then").append(NEWLINE);

        // Include the default command directly if present
        if (global.getDefaultCommand() != null) {
            // Need to call the completion function and combine its output
            // with that of the list of available commands
            writeCommandFunctionCall(writer, global, null, global.getDefaultCommand(), 4);
            indent(writer, 4);
            writer.append("DEFAULT_COMMAND_COMPLETIONS=(${COMPREPLY[@]})").append(NEWLINE);
        }
        indent(writer, 4);
        writer.append("COMPREPLY=()").append(NEWLINE);
        if (global.getDefaultCommand() != null) {
            writeCompletionGeneration(writer, 4, false, null, "COMMANDS", "DEFAULT_COMMAND_COMPLETIONS");
        } else {
            writeCompletionGeneration(writer, 4, false, null, "COMMANDS");
        }
        indent(writer, 2);
        writer.append("fi").append(DOUBLE_NEWLINE);

        // Otherwise we must be in a specific group/command
        // Use a switch statement to provide group/command specific completion
        writer.append("  case ${CURR_CMD} in ").append(NEWLINE);
        if (hasGroups) {
            Set<String> groups = new HashSet<String>();

            // Add a case for each group
            for (CommandGroupMetadata group : global.getCommandGroups()) {
                if (group.isHidden() && !this.includeHidden())
                    continue;

                // Add case for the group
                writeGroupCase(writer, global, group, 4);

                // Track which groups we've generated completion functions for
                groups.add(group.getName());
            }

            // Include commands in the default group directly provided there
            // isn't a conflicting group
            for (CommandMetadata command : global.getDefaultGroupCommands()) {
                if (groups.contains(command.getName()))
                    continue;

                groups.add(command.getName());

                if (command.isHidden() && !this.includeHidden())
                    continue;

                // Add case for the command
                writeCommandCase(writer, global, null, command, 4, false);

                groups.add(command.getName());
            }
        } else {
            // Add a case for each command
            for (CommandMetadata command : global.getDefaultGroupCommands()) {
                if (command.isHidden() && !this.includeHidden())
                    continue;

                // Add case for the command
                writeCommandCase(writer, global, null, command, 4, false);
            }
        }

        indent(writer, 2);
        writer.append("esac").append(DOUBLE_NEWLINE);

        // End Function
        if (this.withDebugging) {
            indent(writer, 2);
            writer.append("set +o xtrace").append(NEWLINE);
        }
        writer.append("}").append(DOUBLE_NEWLINE);

        // Completion setup
        writer.append("complete -F ");
        writeFunctionName(writer, global, false);
        writer.append(" ").append(global.getName());

        // Flush the output
        writer.flush();
        output.flush();
    }

    private void generateCommandFunctions(GlobalMetadata<T> global, Writer writer) throws IOException {
        for (CommandMetadata command : global.getDefaultGroupCommands()) {
            if (command.isHidden() && !this.includeHidden())
                continue;

            // Generate the command completion function
            generateCommandCompletionFunction(writer, global, null, command);
        }
    }

    private void generateGroupFunctions(GlobalMetadata<T> global, Writer writer) throws IOException {
        for (CommandGroupMetadata group : global.getCommandGroups()) {
            if (group.isHidden() && !this.includeHidden())
                continue;

            // Generate the group completion function
            generateGroupCompletionFunction(writer, global, group);

            // Generate the associated command completion functions
            for (CommandMetadata command : group.getCommands()) {
                if (command.isHidden() && !this.includeHidden())
                    continue;

                generateCommandCompletionFunction(writer, global, group, command);
            }
        }
    }

    private void writeHeader(Writer writer) throws IOException {
        // Bash Header
        writer.append("#!/bin/bash").append(DOUBLE_NEWLINE);
        writer.append("# Generated by airline BashCompletionGenerator").append(DOUBLE_NEWLINE);
    }

    private void writeHelperFunctions(Writer writer) throws IOException {
        // Helper functions
        writer.append("containsElement () {\n");
        indent(writer, 2);
        writer.append("# This function from http://stackoverflow.com/a/8574392/107591\n");
        indent(writer, 2);
        writer.append("local e\n");
        indent(writer, 2);
        writer.append("for e in \"${@:2}\"; do [[ \"$e\" == \"$1\" ]] && return 0; done\n");
        indent(writer, 2);
        writer.append("return 1\n");
        writer.append("}\n\n");
    }

    private void writeCommandCase(Writer writer, GlobalMetadata<T> global, CommandGroupMetadata group,
            CommandMetadata command, int indent, boolean isNestedFunction) throws IOException {
        // Start the case
        indent(writer, indent);
        writer.append(command.getName()).append(')').append(NEWLINE);
        indent += 2;

        // Call the function
        writeCommandFunctionCall(writer, global, group, command, indent);

        if (isNestedFunction) {
            // If within a nested function needs to echo the reply
            indent(writer, indent);
            writer.append("echo ${COMPREPLY[@]}").append(NEWLINE);
        }

        // Want to return and terminate the case
        indent(writer, indent);
        writer.append("return $?").append(NEWLINE);
        indent(writer, indent);
        writer.append(";;").append(NEWLINE);
    }

    private void writeCommandFunctionCall(Writer writer, GlobalMetadata<T> global, CommandGroupMetadata group,
            CommandMetadata command, int indent) throws IOException {
        // Just call the command function and pass its value back up
        indent(writer, indent);
        writer.append("COMPREPLY=( $(");
        writeCommandFunctionName(writer, global, group, command, false);
        writer.append(" \"${COMMANDS}\" ) )").append(NEWLINE);
    }

    private void writeGroupCase(Writer writer, GlobalMetadata<T> global, CommandGroupMetadata group, int indent)
            throws IOException {
        // Start the case
        indent(writer, indent);
        writer.append(group.getName()).append(')').append(NEWLINE);
        indent += 2;

        // Call the function
        writeGroupFunctionCall(writer, global, group, indent);

        // Want to return and terminate the case
        indent(writer, indent);
        writer.append("return $?").append(NEWLINE);
        indent(writer, indent);
        writer.append(";;").append(NEWLINE);
    }

    private void writeGroupFunctionCall(Writer writer, GlobalMetadata<T> global, CommandGroupMetadata group, int indent)
            throws IOException {
        // Just call the group function and pass its value back up
        indent(writer, indent);
        writer.append("COMPREPLY=( $( ");
        writeGroupFunctionName(writer, global, group, false);
        writer.append(" ) )").append(NEWLINE);
    }

    private void generateGroupCompletionFunction(Writer writer, GlobalMetadata<T> global, CommandGroupMetadata group)
            throws IOException {
        // Start Function
        writeGroupFunctionName(writer, global, group, true);

        // Prepare variables
        writer.append("  # Get completion data").append(NEWLINE);
        writer.append("  COMPREPLY=()").append(NEWLINE);
        writer.append("  CURR_WORD=${COMP_WORDS[COMP_CWORD]}").append(NEWLINE);
        writer.append("  PREV_WORD=${COMP_WORDS[COMP_CWORD-1]}").append("\n");
        writer.append("  CURR_CMD=").append(NEWLINE);
        writer.append("  if [[ ${COMP_CWORD} -ge 2 ]]; then").append(NEWLINE);
        writer.append("    CURR_CMD=${COMP_WORDS[2]}").append(NEWLINE);
        writer.append("  fi").append(DOUBLE_NEWLINE);

        // Prepare list of group commands
        Set<String> commandNames = new HashSet<>();
        for (CommandMetadata command : group.getCommands()) {
            if (command.isHidden() && !this.includeHidden())
                continue;
            commandNames.add(command.getName());
        }
        writeWordListVariable(writer, 2, "COMMANDS", commandNames.iterator());

        // Check if we are completing a group
        writer.append("  if [[ ${COMP_CWORD} -eq 2 ]]; then").append(NEWLINE);
        // Include the default command directly if present
        if (group.getDefaultCommand() != null) {
            // Need to call the completion function and combine its output
            // with that of the list of available commands
            writeCommandFunctionCall(writer, global, group, group.getDefaultCommand(), 4);
            indent(writer, 4);
            writer.append("DEFAULT_GROUP_COMMAND_COMPLETIONS=(${COMPREPLY[@]})").append(NEWLINE);
        }
        if (global.getDefaultCommand() != null) {
            writeCompletionGeneration(writer, 4, true, null, "COMMANDS", "DEFAULT_GROUP_COMMAND_COMPLETIONS");
        } else {
            writeCompletionGeneration(writer, 4, true, null, "COMMANDS");
        }
        writer.append("  fi").append(DOUBLE_NEWLINE);

        // Otherwise we must be in a specific command
        // Use a switch statement to provide command specific completion
        writer.append("  case ${CURR_CMD} in").append(NEWLINE);
        for (CommandMetadata command : group.getCommands()) {
            if (command.isHidden() && !this.includeHidden())
                continue;

            // Add case for the command
            writeCommandCase(writer, global, group, command, 4, true);
        }
        writer.append("  esac").append(NEWLINE);

        // End Function
        writer.append('}').append(DOUBLE_NEWLINE);
    }

    private void generateCommandCompletionFunction(Writer writer, GlobalMetadata<T> global, CommandGroupMetadata group,
            CommandMetadata command) throws IOException {
        // Start Function
        writeCommandFunctionName(writer, global, group, command, true);

        // Prepare variables
        writer.append("  # Get completion data").append(NEWLINE);
        writer.append("  COMPREPLY=()").append(NEWLINE);
        writer.append("  CURR_WORD=${COMP_WORDS[COMP_CWORD]}").append(NEWLINE);
        writer.append("  PREV_WORD=${COMP_WORDS[COMP_CWORD-1]}").append(NEWLINE);
        writer.append("  COMMANDS=$1").append(DOUBLE_NEWLINE);

        // Prepare the option information
        Set<String> flagOpts = new HashSet<>();
        Set<String> argOpts = new HashSet<>();
        for (OptionMetadata option : command.getAllOptions()) {
            if (option.isHidden() && !this.includeHidden())
                continue;

            if (option.getArity() == 0) {
                flagOpts.addAll(option.getOptions());
            } else {
                argOpts.addAll(option.getOptions());
            }
        }
        writeWordListVariable(writer, 2, "FLAG_OPTS", flagOpts.iterator());
        writeWordListVariable(writer, 2, "ARG_OPTS", argOpts.iterator());
        writer.append(NEWLINE);

        // Check whether we are completing a value for an argument flag
        if (argOpts.size() > 0) {
            writer.append("  $( containsElement ${PREV_WORD} ${ARG_OPTS[@]} )").append(NEWLINE);
            writer.append("  SAW_ARG=$?").append(NEWLINE);

            // If we previously saw an argument then we are completing that
            // argument
            writer.append("  if [[ ${SAW_ARG} -eq 0 ]]; then").append(NEWLINE);
            writer.append("    ARG_VALUES=").append(NEWLINE);
            writer.append("    ARG_GENERATED_VALUES=").append(NEWLINE);
            writer.append("    case ${PREV_WORD} in").append(NEWLINE);
            for (OptionMetadata option : command.getAllOptions()) {
                if (option.isHidden() || option.getArity() == 0)
                    continue;

                // Add cases for the names
                indent(writer, 6);
                Iterator<String> names = option.getOptions().iterator();
                while (names.hasNext()) {
                    writer.append(names.next());
                    if (names.hasNext())
                        writer.append('|');
                }
                writer.append(")\n");

                // Then generate the completions for the option
                BashCompletion completion = getCompletionData(option);
                if (completion != null && StringUtils.isNotEmpty(completion.command())) {
                    indent(writer, 8);
                    writer.append("ARG_GENERATED_VALUES=$( ").append(completion.command()).append(" )").append(NEWLINE);
                }
                AbstractAllowedValuesRestriction allowedValues = (AbstractAllowedValuesRestriction) CollectionUtils
                        .find(option.getRestrictions(), new AllowedValuesOptionFinder());
                if (allowedValues != null && allowedValues.getAllowedValues().size() > 0) {
                    writeWordListVariable(writer, 8, "ARG_VALUES", allowedValues.getAllowedValues().iterator());
                }
                writeCompletionGeneration(writer, 8, true, getCompletionData(option), "ARG_VALUES",
                        "ARG_GENERATED_VALUES");
                indent(writer, 8);
                writer.append(";;").append(NEWLINE);
            }
            writer.append("    esac").append(NEWLINE);
            writer.append("  fi").append(DOUBLE_NEWLINE);
        }

        // If we previously saw a flag we could see another option or an
        // argument if supported
        BashCompletion completion = null;
        if (command.getArguments() != null) {
            completion = getCompletionData(command.getArguments());
            if (completion != null && StringUtils.isNotEmpty(completion.command())) {
                writer.append("  ARGUMENTS=$( ").append(completion.command()).append(" )").append(NEWLINE);
            } else {
                writer.append("  ARGUMENTS=").append(NEWLINE);
            }
        } else {
            writer.append("  ARGUMENTS=").append(NEWLINE);
        }
        writeCompletionGeneration(writer, 2, true, completion, "FLAG_OPTS", "ARG_OPTS", "ARGUMENTS");

        // End Function
        writer.append('}').append(DOUBLE_NEWLINE);
    }

    private void indent(Writer writer, int indent) throws IOException {
        repeat(writer, indent, ' ');
    }

    private void repeat(Writer writer, int count, char c) throws IOException {
        if (count <= 0)
            return;
        for (int i = 0; i < count; i++) {
            writer.append(c);
        }
    }

    private void writeWordListVariable(Writer writer, int indent, String varName, Iterator<String> words)
            throws IOException {
        indent(writer, indent);
        writer.append(varName).append("=\"");
        while (words.hasNext()) {
            writer.append(words.next());
            if (words.hasNext())
                writer.append(' ');
        }
        writer.append('"').append(NEWLINE);
    }

    private void writeFunctionName(Writer writer, GlobalMetadata<T> global, boolean declare) throws IOException {
        if (declare) {
            writer.append("function ");
        }

        writer.append("_complete_").append(bashize(global.getName()));

        if (declare) {
            writer.append("() {").append(NEWLINE);
        }
    }

    private void writeGroupFunctionName(Writer writer, GlobalMetadata<T> global, CommandGroupMetadata group,
            boolean declare) throws IOException {
        if (declare) {
            writer.append("function ");
        }

        //@formatter:off
        writer.append("_complete_")
              .append(bashize(global.getName()))
              .append("_group_")
              .append(bashize(group.getName()));
        
        //@formatter:on

        if (declare) {
            writer.append("() {").append(NEWLINE);
        }
    }

    private void writeCommandFunctionName(Writer writer, GlobalMetadata<T> global, CommandGroupMetadata group,
            CommandMetadata command, boolean declare) throws IOException {
        if (declare) {
            writer.append("function ");
        }

        //@formatter:off
        writer.append("_complete_")
              .append(bashize(global.getName()));
        if (group != null) {
            writer.append("_group_")
                  .append(bashize(group.getName()));
        }
        writer.append("_command_")
              .append(bashize(command.getName()));
        //@formatter:on

        if (declare) {
            writer.append("() {").append(NEWLINE);
        }
    }

    private void writeCompletionGeneration(Writer writer, int indent, boolean isNestedFunction,
            BashCompletion completion, String... varNames) throws IOException {
        indent(writer, indent);
        writer.append("COMPREPLY=( $(compgen ");

        if (completion != null) {
            // Add -o flag as appropriate
            switch (completion.behaviour()) {
            case FILENAMES:
                writer.append("-o default ");
                break;
            case DIRECTORIES:
                writer.append("-o dirnames ");
                break;
            case AS_FILENAMES:
                writer.append("-o filenames ");
                break;
            case AS_DIRECTORIES:
                writer.append("-o plusdirs ");
                break;
            case SYSTEM_COMMANDS:
                writer.append("-c ");
                break;
            default:
                // No completion behaviour
                break;
            }
        }

        // Build a word list from available variables
        writer.append("-W \"");
        for (int i = 0; i < varNames.length; i++) {
            writer.append("${").append(varNames[i]).append("}");
            if (i < varNames.length - 1)
                writer.append(' ');
        }
        if (completion != null && completion.behaviour() == CompletionBehaviour.CLI_COMMANDS) {
            writer.append(" ${COMMANDS}");
        }
        writer.append("\" -- ${CURR_WORD}) )").append(NEWLINE);

        // Echo is necessary due when using a nested function calls
        if (isNestedFunction) {
            indent(writer, indent);
            writer.append("echo ${COMPREPLY[@]}").append(NEWLINE);
        }
        indent(writer, indent);
        writer.append("return 0").append(NEWLINE);
    }

    private String bashize(String value) {
        StringBuilder builder = new StringBuilder();
        for (char c : value.toCharArray()) {
            if (Character.isLetterOrDigit(c) || c == '_') {
                builder.append(c);
            }
        }
        return builder.toString();
    }

    /**
     * Gets the completion info for an option
     * 
     * @param option
     *            Option
     * @return Completion data, {@code null} if none specified
     */
    protected BashCompletion getCompletionData(OptionMetadata option) {
        return getCompletionData(option.getAccessors());
    }

    /**
     * Gets the completion info for arguments
     * 
     * @param arguments
     *            Arguments
     * @return Completion data, {@code null} if none specified
     */
    protected BashCompletion getCompletionData(ArgumentsMetadata arguments) {
        return getCompletionData(arguments.getAccessors());
    }

    protected BashCompletion getCompletionData(Collection<Accessor> accessors) {
        BashCompletion info = null;
        for (Accessor accessor : accessors) {
            info = accessor.getAnnotation(BashCompletion.class);
            if (info != null)
                break;
        }
        return info;
    }

}
