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
package com.github.rvesse.airline;

import com.github.rvesse.airline.Cli;
import com.github.rvesse.airline.annotations.Arguments;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.Option;
import com.github.rvesse.airline.annotations.restrictions.Required;
import com.github.rvesse.airline.builder.CliBuilder;
import com.github.rvesse.airline.help.Help;
import com.github.rvesse.airline.model.CommandMetadata;
import com.github.rvesse.airline.model.GlobalMetadata;
import com.github.rvesse.airline.utils.AirlineTestUtils;
import com.github.rvesse.airline.utils.predicates.parser.CommandFinder;

import org.apache.commons.collections4.CollectionUtils;
import com.github.rvesse.airline.utils.StringUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.inject.Inject;

import java.util.ArrayList;
import java.util.List;

import static com.github.rvesse.airline.annotations.OptionType.GLOBAL;
import static com.github.rvesse.airline.utils.AirlineTestUtils.toStringHelper;

public class TestGalaxyCommandLineParser
{
    @Test
    public void test_parsing()
    {
        run();
        run("help");
        run("help", "galaxy");
        run("help", "show");
        run("help", "install");
        run("help", "upgrade");
        run("help", "upgrade");
        run("help", "terminate");
        run("help", "start");
        run("help", "stop");
        run("help", "restart");
        run("help", "reset-to-actual");
        run("help", "ssh");
        run("help", "agent");
        run("help", "agent", "show");
        run("help", "agent", "add");

        run("--debug");
        run("--debug", "show", "-u", "b2", "--state", "r");
        run("--debug", "install", "com.proofpoint.discovery:discovery-server:1.1", "@discovery:general:1.0");
        run("--debug", "upgrade", "-u", "b2", "1.1", "@1.0");
        run("--debug", "upgrade", "-u", "b2", "1.1", "@1.0", "-s", "r");
        run("--debug", "terminate", "-u", "b2");
        run("--debug", "start", "-u", "b2");
        run("--debug", "stop", "-u", "b2");
        run("--debug", "restart", "-u", "b2");
        run("--debug", "reset-to-actual", "-u", "b2");
        run("--debug", "ssh");
        run("--debug", "ssh", "-u", "b2", "--state", "r", "tail -F var/log/launcher.log");
        run("--debug", "agent");
        run("--debug", "agent", "show");
        run("--debug", "agent", "add", "--count", "4", "t1.micro");
    }
    
    @Test
    public void test_default_command_01() {
        GalaxyCommand command = parse();
        Assert.assertTrue(command instanceof HelpCommand);
    }
    
    @Test
    public void test_default_command_02() {
        GalaxyCommand command = parse("--debug");
        Assert.assertTrue(command instanceof HelpCommand);
    }
    
    @Test
    public void test_default_command_03() {
        GalaxyCommand command = parse("agent");
        Assert.assertTrue(command instanceof AgentShowCommand);
    }
    
    @Test
    public void test_metadata() {
        Cli<GalaxyCommand> cli = createParser();
        
        GlobalMetadata<GalaxyCommand> global = cli.getMetadata();
        Assert.assertEquals(global.getOptions().size(), 2);
        
        CommandMetadata show = CollectionUtils.find(global.getDefaultGroupCommands(), new CommandFinder("show"));
        Assert.assertNotNull(show);
        Assert.assertEquals(show.getCommandOptions().size(), 6);
        Assert.assertEquals(show.getAllOptions().size(), 8);
    }

    protected Cli<GalaxyCommand> createParser()
    {
        CliBuilder<GalaxyCommand> builder = Cli.<GalaxyCommand>builder("galaxy")
                .withDescription("cloud management system")
                .withDefaultCommand(HelpCommand.class)
                .withCommand(HelpCommand.class)
                .withCommand(ShowCommand.class)
                .withCommand(InstallCommand.class)
                .withCommand(UpgradeCommand.class)
                .withCommand(TerminateCommand.class)
                .withCommand(StartCommand.class)
                .withCommand(StopCommand.class)
                .withCommand(RestartCommand.class)
                .withCommand(SshCommand.class)
                .withCommand(ResetToActualCommand.class);

        builder.withGroup("agent")
                .withDescription("Manage agents")
                .withDefaultCommand(AgentShowCommand.class)
                .withCommand(AgentShowCommand.class)
                .withCommand(AgentAddCommand.class)
                .withCommand(AgentTerminateCommand.class);

        return builder.build();
    }

    private void run(String... args)
    {
        GalaxyCommand command = parse(args);
        command.execute();
        System.out.println();
    }

    protected GalaxyCommand parse(String... args) {
        System.out.println("$ galaxy " + StringUtils.join(args, ' '));
        GalaxyCommand command = createParser().parse(args);
        return command;
    }

    public static class GlobalOptions
    {
        @Option(type = GLOBAL, name = "--debug", description = "Enable debug messages")
        public boolean debug = false;

        @Option(type = GLOBAL, name = "--coordinator", description = "Galaxy coordinator host (overrides GALAXY_COORDINATOR)")
        public String coordinator = AirlineTestUtils.firstNonNull(System.getenv("GALAXY_COORDINATOR"), "http://localhost:64000");

        @Override
        public String toString()
        {
            return toStringHelper(this)
                    .add("debug", debug)
                    .add("coordinator", coordinator)
                    .toString();
        }
    }

    public static class SlotFilter
    {
        @Option(name = {"-b", "--binary"}, description = "Select slots with a given binary")
        public List<String> binary;

        @Option(name = {"-c", "--config"}, description = "Select slots with a given configuration")
        public List<String> config;

        @Option(name = {"-i", "--host"}, description = "Select slots on the given host")
        public List<String> host;

        @Option(name = {"-I", "--ip"}, description = "Select slots at the given IP address")
        public List<String> ip;

        @Option(name = {"-u", "--uuid"}, description = "Select slot with the given UUID")
        public List<String> uuid;

        @Option(name = {"-s", "--state"}, description = "Select 'r{unning}', 's{topped}' or 'unknown' slots")
        public List<String> state;

        @Override
        public String toString()
        {
            return toStringHelper(this)
                    .add("binary", binary)
                    .add("config", config)
                    .add("host", host)
                    .add("ip", ip)
                    .add("uuid", uuid)
                    .add("state", state)
                    .toString();
        }
    }

    public static class AgentFilter
    {
        @Option(name = {"-i", "--host"}, description = "Select slots on the given host")
        public final List<String> host = new ArrayList<>();

        @Option(name = {"-I", "--ip"}, description = "Select slots at the given IP address")
        public final List<String> ip = new ArrayList<>();

        @Option(name = {"-u", "--uuid"}, description = "Select slot with the given UUID")
        public final List<String> uuid = new ArrayList<>();

        @Option(name = {"-s", "--state"}, description = "Select 'r{unning}', 's{topped}' or 'unknown' slots")
        public final List<String> state = new ArrayList<>();

        @Override
        public String toString()
        {
            return toStringHelper(this)
                    .add("host", host)
                    .add("ip", ip)
                    .add("uuid", uuid)
                    .add("state", state)
                    .toString();
        }
    }

    public static abstract class GalaxyCommand
    {
        @Inject
        public GlobalOptions globalOptions = new GlobalOptions();

        public void execute()
        {
            System.out.println(this);
        }
    }

    @Command(name = "help", description = "Display help information about galaxy")
    public static class HelpCommand
            extends GalaxyCommand
    {
        @Inject
        public Help<GalaxyCommand> help;

        @Override
        public void execute()
        {
            help.call();
        }
    }

    @Command(name = "show", description = "Show state of all slots")
    public static class ShowCommand
            extends GalaxyCommand
    {
        @Inject
        public final SlotFilter slotFilter = new SlotFilter();

        @Override
        public String toString()
        {
            return toStringHelper(this)
                    .add("slotFilter", slotFilter)
                    .add("globalOptions", globalOptions)
                    .toString();
        }
    }

    @Command(name = "install", description = "Install software in a new slot")
    public static class InstallCommand
            extends GalaxyCommand
    {
        @Option(name = {"--count"}, description = "Number of instances to install")
        public int count = 1;

        @Inject
        public final AgentFilter agentFilter = new AgentFilter();

        @Arguments(description = "The binary and @configuration to install.  The default packaging is tar.gz")
        public final List<String> assignment = new ArrayList<>();

        @Override
        public String toString()
        {
            return toStringHelper(this)
                    .add("count", count)
                    .add("agentFilter", agentFilter)
                    .add("assignment", assignment)
                    .add("globalOptions", globalOptions)
                    .toString();
        }
    }

    @Command(name = "upgrade", description = "Upgrade software in a slot")
    public static class UpgradeCommand
            extends GalaxyCommand
    {
        @Inject
        public final SlotFilter slotFilter = new SlotFilter();

        @Arguments(description = "Version of the binary and/or @configuration")
        public final List<String> versions = new ArrayList<>();

        @Override
        public String toString()
        {
            return toStringHelper(this)
                    .add("slotFilter", slotFilter)
                    .add("versions", versions)
                    .add("globalOptions", globalOptions)
                    .toString();
        }
    }

    @Command(name = "terminate", description = "Terminate (remove) a slot")
    public static class TerminateCommand
            extends GalaxyCommand
    {
        @Inject
        public final SlotFilter slotFilter = new SlotFilter();

        @Override
        public String toString()
        {
            return toStringHelper(this)
                    .add("slotFilter", slotFilter)
                    .add("globalOptions", globalOptions)
                    .toString();
        }
    }

    @Command(name = "start", description = "Start a server")
    public static class StartCommand
            extends GalaxyCommand
    {
        @Inject
        public final SlotFilter slotFilter = new SlotFilter();

        @Override
        public String toString()
        {
            return toStringHelper(this)
                    .add("slotFilter", slotFilter)
                    .add("globalOptions", globalOptions)
                    .toString();
        }
    }

    @Command(name = "stop", description = "Stop a server")
    public static class StopCommand
            extends GalaxyCommand
    {
        @Inject
        public final SlotFilter slotFilter = new SlotFilter();

        @Override
        public String toString()
        {
            return toStringHelper(this)
                    .add("slotFilter", slotFilter)
                    .add("globalOptions", globalOptions)
                    .toString();
        }
    }

    @Command(name = "restart", description = "Restart server")
    public static class RestartCommand
            extends GalaxyCommand
    {
        @Inject
        public final SlotFilter slotFilter = new SlotFilter();

        @Override
        public String toString()
        {
            return toStringHelper(this)
                    .add("slotFilter", slotFilter)
                    .add("globalOptions", globalOptions)
                    .toString();
        }
    }

    @Command(name = "reset-to-actual", description = "Reset slot expected state to actual")
    public static class ResetToActualCommand
            extends GalaxyCommand
    {
        @Inject
        public final SlotFilter slotFilter = new SlotFilter();

        @Override
        public String toString()
        {
            return toStringHelper(this)
                    .add("slotFilter", slotFilter)
                    .add("globalOptions", globalOptions)
                    .toString();
        }
    }

    @Command(name = "ssh", description = "ssh to slot installation")
    public static class SshCommand
            extends GalaxyCommand
    {
        @Inject
        public final SlotFilter slotFilter = new SlotFilter();

        @Arguments(description = "Command to execute on the remote host")
        public String command;

        @Override
        public String toString()
        {
            return toStringHelper(this)
                    .add("slotFilter", slotFilter)
                    .add("command", command)
                    .toString();
        }
    }

    @Command(name = "add", description = "Provision a new agent")
    public static class AgentAddCommand
            extends GalaxyCommand
    {
        @Option(name = {"--count"}, description = "Number of agents to provision")
        public int count = 1;

        @Option(name = {"--availability-zone"}, description = "Availability zone to provision")
        public String availabilityZone;

        @Arguments(description = "Instance type to provision")
        public String instanceType;

        @Override
        public String toString()
        {
            return toStringHelper(this)
                    .add("count", count)
                    .add("availabilityZone", availabilityZone)
                    .add("instanceType", instanceType)
                    .add("globalOptions", globalOptions)
                    .toString();
        }
    }

    @Command(name = "show", description = "Show agent details")
    public static class AgentShowCommand
            extends GalaxyCommand
    {
        @Inject
        public final AgentFilter agentFilter = new AgentFilter();

        @Override
        public String toString()
        {
            return toStringHelper(this)
                    .add("agentFilter", agentFilter)
                    .add("globalOptions", globalOptions)
                    .toString();
        }
    }

    @Command(name = "terminate", description = "Provision a new agent")
    public static class AgentTerminateCommand
            extends GalaxyCommand
    {
        @Arguments(title = "agent-id", description = "Agent to terminate")
        @Required
        public String agentId;

        @Override
        public String toString()
        {
            return toStringHelper(this)
                    .add("agentId", agentId)
                    .add("globalOptions", globalOptions)
                    .toString();
        }
    }
}
