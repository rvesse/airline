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
package com.github.rvesse.airline.parser.aliases;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.github.rvesse.airline.CommandLineInterface;
import com.github.rvesse.airline.args.Args1;
import com.github.rvesse.airline.builder.CliBuilder;
import com.github.rvesse.airline.help.cli.CliGlobalUsageGenerator;
import com.github.rvesse.airline.model.AliasMetadata;
import com.github.rvesse.airline.parser.errors.ParseAliasCircularReferenceException;
import com.github.rvesse.airline.parser.errors.ParseCommandUnrecognizedException;
import com.github.rvesse.airline.parser.errors.ParseOptionConversionException;
import com.github.rvesse.airline.parser.resources.ClasspathLocator;
import com.github.rvesse.airline.parser.resources.EnvVarLocator;
import com.github.rvesse.airline.parser.resources.JvmSystemPropertyLocator;

public class TestAliases {

    private static final File f = new File("target/test.config");
    private static String homeDir;

    @BeforeClass
    public static void setup() {
        homeDir = System.getProperty("user.home");

        // Change home directory for purposes of these tests
        System.setProperty("user.home", new File("target/").getAbsolutePath());
    }

    @AfterClass
    public static void cleanup() {
        if (f.exists()) {
            f.delete();
        }
        System.setProperty("user.home", homeDir);
    }

    public static void prepareConfig(File f, String... lines) throws IOException {
        FileWriter writer = new FileWriter(f);
        for (String line : lines) {
            writer.append(line);
            writer.append('\n');
        }
        writer.close();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void customEnvironment(Map<String, String> customEnvironment) throws Exception {
        try {
            Class<?> processEnvironmentClass = Class.forName("java.lang.ProcessEnvironment");
            Field theEnvironmentField = processEnvironmentClass.getDeclaredField("theEnvironment");
            theEnvironmentField.setAccessible(true);
            Map<String, String> env = (Map<String, String>) theEnvironmentField.get(null);
            env.putAll(customEnvironment);
            Field theCaseInsensitiveEnvironmentField = processEnvironmentClass
                    .getDeclaredField("theCaseInsensitiveEnvironment");
            theCaseInsensitiveEnvironmentField.setAccessible(true);
            Map<String, String> cienv = (Map<String, String>) theCaseInsensitiveEnvironmentField.get(null);
            cienv.putAll(customEnvironment);
        } catch (NoSuchFieldException e) {
            Class[] classes = Collections.class.getDeclaredClasses();
            Map<String, String> env = System.getenv();
            for (Class cl : classes) {
                if ("java.util.Collections$UnmodifiableMap".equals(cl.getName())) {
                    Field field = cl.getDeclaredField("m");
                    field.setAccessible(true);
                    Object obj = field.get(env);
                    Map<String, String> map = (Map<String, String>) obj;
                    // map.clear();
                    map.putAll(customEnvironment);
                }
            }
        }
    }

    @Test
    public void user_aliases_default_01() throws IOException {
        prepareConfig(f, "foo=Args1 bar");

        //@formatter:off
        CliBuilder<Args1> builder = CommandLineInterface.<Args1>builder("test")
                                       .withCommand(Args1.class);
        builder.withParser()
               .withUserAliases()
                   .withProgramName("test")
                   .withSearchLocation("target/");
        CommandLineInterface<Args1> cli = builder.build();
        //@formatter:on

        // Check definition
        List<AliasMetadata> aliases = cli.getMetadata().getParserConfiguration().getAliases();
        Assert.assertEquals(aliases.size(), 1);

        AliasMetadata alias = aliases.get(0);
        Assert.assertEquals(alias.getName(), "foo");
        List<String> args = alias.getArguments();
        Assert.assertEquals(args.size(), 2);
        Assert.assertEquals(args.get(0), "Args1");
        Assert.assertEquals(args.get(1), "bar");

        // Check parsing
        Args1 cmd = cli.parse("foo");
        Assert.assertEquals(cmd.parameters.size(), 1);
        Assert.assertEquals(cmd.parameters.get(0), "bar");
    }

    @Test
    public void user_aliases_default_02() throws IOException {
        prepareConfig(f, "foo=Args1 bar");

        //@formatter:off
        CliBuilder<Args1> builder = CommandLineInterface.<Args1>builder("other")
                                       .withCommand(Args1.class);
        builder.withParser()
               .withUserAliases()
                   .withProgramName("target/");
        CommandLineInterface<Args1> cli = builder.build();
        //@formatter:on

        // Check definition
        List<AliasMetadata> aliases = cli.getMetadata().getParserConfiguration().getAliases();
        Assert.assertEquals(aliases.size(), 0);
    }

    @Test
    public void user_aliases_01() throws IOException {
        prepareConfig(f, "foo=Args1 bar");

        //@formatter:off
        CliBuilder<Args1> builder = CommandLineInterface.<Args1>builder("test")
                            .withCommand(Args1.class);
        builder.withParser()
               .withUserAliases()
                   .withFilename(f.getName())
                   .withSearchLocation("target/");
        CommandLineInterface<Args1> cli = builder.build();
        //@formatter:on

        // Check definition
        List<AliasMetadata> aliases = cli.getMetadata().getParserConfiguration().getAliases();
        Assert.assertEquals(aliases.size(), 1);

        AliasMetadata alias = aliases.get(0);
        Assert.assertEquals(alias.getName(), "foo");
        List<String> args = alias.getArguments();
        Assert.assertEquals(args.size(), 2);
        Assert.assertEquals(args.get(0), "Args1");
        Assert.assertEquals(args.get(1), "bar");

        // Check parsing
        Args1 cmd = cli.parse("foo");
        Assert.assertEquals(cmd.parameters.size(), 1);
        Assert.assertEquals(cmd.parameters.get(0), "bar");
    }

    @Test
    public void user_aliases_02() throws IOException {
        prepareConfig(f, "a.foo=Args1 bar", "b.foo=Args1 faz");

        //@formatter:off
        CliBuilder<Args1> builder = CommandLineInterface.<Args1>builder("test")
                                       .withCommand(Args1.class);
        builder.withParser()
               .withUserAliases()
                   .withFilename(f.getName())
                   .withPrefix("b.")
                   .withSearchLocation("target/");
        CommandLineInterface<Args1> cli = builder.build();
        //@formatter:on

        // Check definition
        List<AliasMetadata> aliases = cli.getMetadata().getParserConfiguration().getAliases();
        Assert.assertEquals(aliases.size(), 1);

        AliasMetadata alias = aliases.get(0);
        Assert.assertEquals(alias.getName(), "foo");
        List<String> args = alias.getArguments();
        Assert.assertEquals(args.size(), 2);
        Assert.assertEquals(args.get(0), "Args1");
        Assert.assertEquals(args.get(1), "faz");

        // Check parsing
        Args1 cmd = cli.parse("foo");
        Assert.assertEquals(cmd.parameters.size(), 1);
        Assert.assertEquals(cmd.parameters.get(0), "faz");
    }

    @Test
    public void user_aliases_home_dir_01() throws IOException {
        prepareConfig(f, "foo=Args1 bar");

        //@formatter:off
        CliBuilder<Args1> builder = CommandLineInterface.<Args1>builder("test")
                            .withCommand(Args1.class);
        builder.withParser()
               .withUserAliases()
                   .withFilename(f.getName())
                   .withSearchLocations("~/", "~\\");
        CommandLineInterface<Args1> cli = builder.build();
        //@formatter:on

        // Check definition
        List<AliasMetadata> aliases = cli.getMetadata().getParserConfiguration().getAliases();
        Assert.assertEquals(aliases.size(), 1);

        AliasMetadata alias = aliases.get(0);
        Assert.assertEquals(alias.getName(), "foo");
        List<String> args = alias.getArguments();
        Assert.assertEquals(args.size(), 2);
        Assert.assertEquals(args.get(0), "Args1");
        Assert.assertEquals(args.get(1), "bar");

        // Check parsing
        Args1 cmd = cli.parse("foo");
        Assert.assertEquals(cmd.parameters.size(), 1);
        Assert.assertEquals(cmd.parameters.get(0), "bar");
    }

    @Test
    public void user_aliases_home_dir_02() throws IOException {
        prepareConfig(f, "a.foo=Args1 bar", "b.foo=Args1 faz");

        //@formatter:off
        CliBuilder<Args1> builder = CommandLineInterface.<Args1>builder("test")
                                       .withCommand(Args1.class);
        builder.withParser()
               .withUserAliases()
                   .withFilename(f.getName())
                   .withPrefix("b.")
                   .withSearchLocations("~/", "~\\");
        CommandLineInterface<Args1> cli = builder.build();
        //@formatter:on

        // Check definition
        List<AliasMetadata> aliases = cli.getMetadata().getParserConfiguration().getAliases();
        Assert.assertEquals(aliases.size(), 1);

        AliasMetadata alias = aliases.get(0);
        Assert.assertEquals(alias.getName(), "foo");
        List<String> args = alias.getArguments();
        Assert.assertEquals(args.size(), 2);
        Assert.assertEquals(args.get(0), "Args1");
        Assert.assertEquals(args.get(1), "faz");

        // Check parsing
        Args1 cmd = cli.parse("foo");
        Assert.assertEquals(cmd.parameters.size(), 1);
        Assert.assertEquals(cmd.parameters.get(0), "faz");
    }

    @Test
    public void user_aliases_home_dir_03() throws IOException {
        // Empty aliases
        prepareConfig(f, "");

        //@formatter:off
        CliBuilder<Args1> builder = CommandLineInterface.<Args1>builder("test")
                            .withCommand(Args1.class);
        builder.withParser()
               .withUserAliases()
                   .withFilename(f.getName())
                   .withSearchLocations("~/", "~\\");
        CommandLineInterface<Args1> cli = builder.build();
        //@formatter:on

        // Check definition
        List<AliasMetadata> aliases = cli.getMetadata().getParserConfiguration().getAliases();
        Assert.assertEquals(aliases.size(), 0);
    }

    @Test
    public void user_aliases_working_dir_01() throws IOException {
        File workingDir = Paths.get("").toAbsolutePath().toFile();
        File config = new File(workingDir, "test.config");
        prepareConfig(config, "foo=Args1 bar");

        try {
            //@formatter:off
            CliBuilder<Args1> builder = CommandLineInterface.<Args1>builder("test")
                                           .withCommand(Args1.class);
            builder.withParser()
                   .withUserAliases()
                       .withFilename(config.getName())
                       .withSearchLocations("./", ".\\");
            CommandLineInterface<Args1> cli = builder.build();
            //@formatter:on

            // Check definition
            List<AliasMetadata> aliases = cli.getMetadata().getParserConfiguration().getAliases();
            Assert.assertEquals(aliases.size(), 1);

            AliasMetadata alias = aliases.get(0);
            Assert.assertEquals(alias.getName(), "foo");
            List<String> args = alias.getArguments();
            Assert.assertEquals(args.size(), 2);
            Assert.assertEquals(args.get(0), "Args1");
            Assert.assertEquals(args.get(1), "bar");

            // Check parsing
            Args1 cmd = cli.parse("foo");
            Assert.assertEquals(cmd.parameters.size(), 1);
            Assert.assertEquals(cmd.parameters.get(0), "bar");
        } finally {
            config.delete();
        }
    }

    @Test
    public void user_aliases_working_dir_02() throws IOException {
        File workingDir = Paths.get("").toAbsolutePath().toFile();
        File config = new File(workingDir, "test.config");
        // Empty aliases
        prepareConfig(config, "");

        try {
            //@formatter:off
            CliBuilder<Args1> builder = CommandLineInterface.<Args1>builder("test")
                                           .withCommand(Args1.class);
            builder.withParser()
                   .withUserAliases()
                       .withFilename(config.getName())
                       .withSearchLocations("./", ".\\");
            CommandLineInterface<Args1> cli = builder.build();
            //@formatter:on

            // Check definition
            List<AliasMetadata> aliases = cli.getMetadata().getParserConfiguration().getAliases();
            Assert.assertEquals(aliases.size(), 0);
        } finally {
            config.delete();
        }
    }

    @Test
    public void user_aliases_env_01() throws Exception {
        Map<String, String> custom = new HashMap<>();
        custom.put("FOO", f.getParentFile().getAbsolutePath());

        customEnvironment(custom);
        prepareConfig(f, "foo=Args1 bar");

        //@formatter:off
        CliBuilder<Args1> builder = CommandLineInterface.<Args1>builder("test")
                            .withCommand(Args1.class);
        builder.withParser()
               .withUserAliases()
                   .withFilename(f.getName())
                   .withSearchLocation("${FOO}/")
                   .withLocator(new EnvVarLocator());
        CommandLineInterface<Args1> cli = builder.build();
        //@formatter:on

        // Check definition
        List<AliasMetadata> aliases = cli.getMetadata().getParserConfiguration().getAliases();
        Assert.assertEquals(aliases.size(), 1);

        AliasMetadata alias = aliases.get(0);
        Assert.assertEquals(alias.getName(), "foo");
        List<String> args = alias.getArguments();
        Assert.assertEquals(args.size(), 2);
        Assert.assertEquals(args.get(0), "Args1");
        Assert.assertEquals(args.get(1), "bar");

        // Check parsing
        Args1 cmd = cli.parse("foo");
        Assert.assertEquals(cmd.parameters.size(), 1);
        Assert.assertEquals(cmd.parameters.get(0), "bar");
    }

    @Test
    public void user_aliases_env_02() throws Exception {
        Map<String, String> custom = new HashMap<>();
        custom.put("FOO", f.getParentFile().getAbsolutePath());

        customEnvironment(custom);
        prepareConfig(f, "foo=Args1 bar");

        //@formatter:off
        CliBuilder<Args1> builder = CommandLineInterface.<Args1>builder("test")
                            .withCommand(Args1.class);
        builder.withParser()
                // Bad placeholder
               .withUserAliases()
                   .withFilename(f.getName())
                   .withSearchLocation("${FOO/")
                   .withLocator(new EnvVarLocator());
        CommandLineInterface<Args1> cli = builder.build();
        //@formatter:on

        // Check definition
        List<AliasMetadata> aliases = cli.getMetadata().getParserConfiguration().getAliases();
        Assert.assertEquals(aliases.size(), 0);
    }

    @Test
    public void user_aliases_env_03() throws Exception {
        Map<String, String> custom = new HashMap<>();
        custom.put("FOO", new File(f.getAbsolutePath()).getParentFile().getParentFile().getAbsolutePath());
        custom.put("BAR", f.getParentFile().getName());

        customEnvironment(custom);
        prepareConfig(f, "foo=Args1 bar");

        //@formatter:off
        CliBuilder<Args1> builder = CommandLineInterface.<Args1>builder("test")
                            .withCommand(Args1.class);
        builder.withParser()
               .withUserAliases()
                   .withFilename(f.getName())
                   .withSearchLocation("${FOO}/${BAR}/")
                   .withLocator(new EnvVarLocator());
        CommandLineInterface<Args1> cli = builder.build();
        //@formatter:on

        // Check definition
        List<AliasMetadata> aliases = cli.getMetadata().getParserConfiguration().getAliases();
        Assert.assertEquals(aliases.size(), 1);

        AliasMetadata alias = aliases.get(0);
        Assert.assertEquals(alias.getName(), "foo");
        List<String> args = alias.getArguments();
        Assert.assertEquals(args.size(), 2);
        Assert.assertEquals(args.get(0), "Args1");
        Assert.assertEquals(args.get(1), "bar");

        // Check parsing
        Args1 cmd = cli.parse("foo");
        Assert.assertEquals(cmd.parameters.size(), 1);
        Assert.assertEquals(cmd.parameters.get(0), "bar");
    }
    
    @Test
    public void user_aliases_env_04() throws Exception {
        Map<String, String> custom = new HashMap<>();
        custom.put("FOO", new File(f.getAbsolutePath()).getParentFile().getParentFile().getAbsolutePath());

        customEnvironment(custom);
        prepareConfig(f, "foo=Args1 bar");

        //@formatter:off
        CliBuilder<Args1> builder = Cli.<Args1>builder("test")
                            .withCommand(Args1.class);
        builder.withParser()
               .withUserAliases()
                   .withFilename(f.getName())
                   .withSearchLocation("${FOO}/${UNDEF}/")
                   .withLocator(new EnvVarLocator());
        Cli<Args1> cli = builder.build();
        //@formatter:on

        // Check definition, should be missing as not all env vars were resolvable
        List<AliasMetadata> aliases = cli.getMetadata().getParserConfiguration().getAliases();
        Assert.assertEquals(aliases.size(), 0);
    }

    @Test
    public void user_aliases_sysprop_01() throws Exception {
        System.getProperties().put("FOO", f.getParentFile().getAbsolutePath());
        prepareConfig(f, "foo=Args1 bar");

        //@formatter:off
        CliBuilder<Args1> builder = CommandLineInterface.<Args1>builder("test")
                            .withCommand(Args1.class);
        builder.withParser()
               .withUserAliases()
                   .withFilename(f.getName())
                   .withSearchLocation("${FOO}/")
                   .withLocator(new JvmSystemPropertyLocator());
        CommandLineInterface<Args1> cli = builder.build();
        //@formatter:on

        // Check definition
        List<AliasMetadata> aliases = cli.getMetadata().getParserConfiguration().getAliases();
        Assert.assertEquals(aliases.size(), 1);

        AliasMetadata alias = aliases.get(0);
        Assert.assertEquals(alias.getName(), "foo");
        List<String> args = alias.getArguments();
        Assert.assertEquals(args.size(), 2);
        Assert.assertEquals(args.get(0), "Args1");
        Assert.assertEquals(args.get(1), "bar");

        // Check parsing
        Args1 cmd = cli.parse("foo");
        Assert.assertEquals(cmd.parameters.size(), 1);
        Assert.assertEquals(cmd.parameters.get(0), "bar");
    }

    @Test
    public void user_aliases_sysprop_02() throws Exception {
        System.getProperties().put("FOO", f.getParentFile().getAbsolutePath());
        prepareConfig(f, "foo=Args1 bar");

        //@formatter:off
        CliBuilder<Args1> builder = CommandLineInterface.<Args1>builder("test")
                            .withCommand(Args1.class);
        builder.withParser()
                // Bad placeholder
               .withUserAliases()
                   .withFilename(f.getName())
                   .withSearchLocation("${FOO/")
                   .withLocator(new JvmSystemPropertyLocator());
        CommandLineInterface<Args1> cli = builder.build();
        //@formatter:on

        // Check definition
        List<AliasMetadata> aliases = cli.getMetadata().getParserConfiguration().getAliases();
        Assert.assertEquals(aliases.size(), 0);
    }

    @Test
    public void user_aliases_sysprop_03() throws Exception {
        System.getProperties().put("FOO",
                new File(f.getAbsolutePath()).getParentFile().getParentFile().getAbsolutePath());
        System.getProperties().put("BAR", f.getParentFile().getName());
        prepareConfig(f, "foo=Args1 bar");

        //@formatter:off
        CliBuilder<Args1> builder = CommandLineInterface.<Args1>builder("test")
                            .withCommand(Args1.class);
        builder.withParser()
               .withUserAliases()
                   .withFilename(f.getName())
                   .withSearchLocation("${FOO}/${BAR}/")
                   .withLocator(new JvmSystemPropertyLocator());
        CommandLineInterface<Args1> cli = builder.build();
        //@formatter:on

        // Check definition
        List<AliasMetadata> aliases = cli.getMetadata().getParserConfiguration().getAliases();
        Assert.assertEquals(aliases.size(), 1);

        AliasMetadata alias = aliases.get(0);
        Assert.assertEquals(alias.getName(), "foo");
        List<String> args = alias.getArguments();
        Assert.assertEquals(args.size(), 2);
        Assert.assertEquals(args.get(0), "Args1");
        Assert.assertEquals(args.get(1), "bar");

        // Check parsing
        Args1 cmd = cli.parse("foo");
        Assert.assertEquals(cmd.parameters.size(), 1);
        Assert.assertEquals(cmd.parameters.get(0), "bar");
    }

    @Test
    public void user_aliases_classpath_01() throws Exception {
        //@formatter:off
        CliBuilder<Args1> builder = CommandLineInterface.<Args1>builder("test")
                            .withCommand(Args1.class);
        builder.withParser()
               .withUserAliases()
                   .withFilename("aliases.config")
                   .withSearchLocation("/")
                   .withLocator(new ClasspathLocator());
        CommandLineInterface<Args1> cli = builder.build();
        //@formatter:on

        // Check definition
        List<AliasMetadata> aliases = cli.getMetadata().getParserConfiguration().getAliases();
        Assert.assertEquals(aliases.size(), 1);

        AliasMetadata alias = aliases.get(0);
        Assert.assertEquals(alias.getName(), "foo");
        List<String> args = alias.getArguments();
        Assert.assertEquals(args.size(), 2);
        Assert.assertEquals(args.get(0), "Args1");
        Assert.assertEquals(args.get(1), "bar");

        // Check parsing
        Args1 cmd = cli.parse("foo");
        Assert.assertEquals(cmd.parameters.size(), 1);
        Assert.assertEquals(cmd.parameters.get(0), "bar");
    }

    @Test
    public void user_aliases_classpath_02() throws Exception {
        //@formatter:off
        CliBuilder<Args1> builder = CommandLineInterface.<Args1>builder("test")
                            .withCommand(Args1.class);
        builder.withParser()
               .withUserAliases()
                   .withFilename("aliases.config")
                   .withSearchLocation("classpath:/")
                   .withLocator(new ClasspathLocator());
        CommandLineInterface<Args1> cli = builder.build();
        //@formatter:on

        // Check definition
        List<AliasMetadata> aliases = cli.getMetadata().getParserConfiguration().getAliases();
        Assert.assertEquals(aliases.size(), 1);

        AliasMetadata alias = aliases.get(0);
        Assert.assertEquals(alias.getName(), "foo");
        List<String> args = alias.getArguments();
        Assert.assertEquals(args.size(), 2);
        Assert.assertEquals(args.get(0), "Args1");
        Assert.assertEquals(args.get(1), "bar");

        // Check parsing
        Args1 cmd = cli.parse("foo");
        Assert.assertEquals(cmd.parameters.size(), 1);
        Assert.assertEquals(cmd.parameters.get(0), "bar");
    }

    @Test(expectedExceptions = ParseOptionConversionException.class)
    public void user_aliases_positional_01() throws IOException {
        prepareConfig(f, "foo=Args1 -long $1");

        //@formatter:off
        CliBuilder<Args1> builder = CommandLineInterface.<Args1>builder("test")
                                       .withCommand(Args1.class);
        builder.withParser()
               .withUserAliases()
                   .withFilename(f.getName())
                   .withSearchLocation("target/");
        CommandLineInterface<Args1> cli = builder.build();
        //@formatter:on

        // Check definition
        List<AliasMetadata> aliases = cli.getMetadata().getParserConfiguration().getAliases();
        Assert.assertEquals(aliases.size(), 1);

        AliasMetadata alias = aliases.get(0);
        Assert.assertEquals(alias.getName(), "foo");
        List<String> args = alias.getArguments();
        Assert.assertEquals(args.size(), 3);
        Assert.assertEquals(args.get(0), "Args1");
        Assert.assertEquals(args.get(1), "-long");
        Assert.assertEquals(args.get(2), "$1");

        // Check parsing
        // Should error because the positional parameter $1 will fail to expand
        // and be passed as-is to the -long option resulting in a type
        // conversion error
        cli.parse("foo");
    }

    @Test
    public void user_aliases_positional_02() throws IOException {
        prepareConfig(f, "foo=Args1 -long $1");

        //@formatter:off
        CliBuilder<Args1> builder = CommandLineInterface.<Args1>builder("test")
                                       .withCommand(Args1.class);
        builder.withParser()
               .withUserAliases()
                   .withFilename(f.getName())
                   .withSearchLocation("target/");
        CommandLineInterface<Args1> cli = builder.build();
        //@formatter:on

        // Check definition
        List<AliasMetadata> aliases = cli.getMetadata().getParserConfiguration().getAliases();
        Assert.assertEquals(aliases.size(), 1);

        AliasMetadata alias = aliases.get(0);
        Assert.assertEquals(alias.getName(), "foo");
        List<String> args = alias.getArguments();
        Assert.assertEquals(args.size(), 3);
        Assert.assertEquals(args.get(0), "Args1");
        Assert.assertEquals(args.get(1), "-long");
        Assert.assertEquals(args.get(2), "$1");

        // Check parsing
        Args1 cmd = cli.parse("foo", "345");
        Assert.assertEquals(cmd.l, 345l);
        Assert.assertEquals(cmd.parameters.size(), 0);
    }

    @Test
    public void user_aliases_positional_03() throws IOException {
        //@formatter:off
        CliBuilder<Args1> builder = CommandLineInterface.<Args1>builder("test")
                                    .withCommand(Args1.class);
        builder.withParser()
               .withAlias("foo")
               .withArguments("Args1", "-long", "$1", "-float", "$3");
        CommandLineInterface<Args1> cli = builder.build();
        //@formatter:on

        // Check parsing
        Args1 cmd = cli.parse("foo", "345", "bar", "1.23");
        Assert.assertEquals(cmd.l, 345l);
        Assert.assertEquals(cmd.floa, 1.23f);
        List<String> args = cmd.parameters;
        Assert.assertEquals(args.size(), 1);
        Assert.assertEquals(args.get(0), "bar");
    }

    @Test
    public void user_aliases_positional_04() throws IOException {
        //@formatter:off
        CliBuilder<Args1> builder = CommandLineInterface.<Args1>builder("test")
                                    .withCommand(Args1.class);
        builder.withParser()
               .withAlias("foo")
               .withArguments("Args1", "-long", "$1", "-float", "$5");
        CommandLineInterface<Args1> cli = builder.build();
        //@formatter:on

        // Check parsing
        Args1 cmd = cli.parse("foo", "345", "a", "b", "c", "1.23", "d", "e");
        Assert.assertEquals(cmd.l, 345l);
        Assert.assertEquals(cmd.floa, 1.23f);
        List<String> args = cmd.parameters;
        Assert.assertEquals(args.size(), 5);
        for (int i = 0, c = 'a'; i < args.size(); i++, c++) {
            Assert.assertEquals(args.get(i), new String(new char[] { (char) c }));
        }
    }

    @Test
    public void user_aliases_override_01() throws IOException {
        prepareConfig(f, "Args1=Args1 bar");

        //@formatter:off
        CliBuilder<Args1> builder = CommandLineInterface.<Args1>builder("test")
                                       .withCommand(Args1.class);
        builder.withParser()
               .withUserAliases()
                   .withFilename(f.getName())
                   .withSearchLocation("target/");
        CommandLineInterface<Args1> cli = builder.build();
        //@formatter:on

        // Check definition
        List<AliasMetadata> aliases = cli.getMetadata().getParserConfiguration().getAliases();
        Assert.assertEquals(aliases.size(), 1);

        AliasMetadata alias = aliases.get(0);
        Assert.assertEquals(alias.getName(), "Args1");
        List<String> args = alias.getArguments();
        Assert.assertEquals(args.size(), 2);
        Assert.assertEquals(args.get(0), "Args1");
        Assert.assertEquals(args.get(1), "bar");

        // Check parsing
        // By default aliases don't override built-ins
        Args1 cmd = cli.parse("Args1");
        Assert.assertEquals(cmd.parameters.size(), 0);
    }

    @Test
    public void user_aliases_override_02() throws IOException {
        prepareConfig(f, "Args1=Args1 bar");

        //@formatter:off
        CliBuilder<Args1> builder = CommandLineInterface.<Args1>builder("test")
                                       .withCommand(Args1.class);
        builder.withParser()
               .withAliasesOverridingBuiltIns()
               .withUserAliases()
                   .withFilename(f.getName())
                   .withSearchLocation("target/");
        CommandLineInterface<Args1> cli = builder.build();
        //@formatter:on

        // Check definition
        List<AliasMetadata> aliases = cli.getMetadata().getParserConfiguration().getAliases();
        Assert.assertEquals(aliases.size(), 1);

        AliasMetadata alias = aliases.get(0);
        Assert.assertEquals(alias.getName(), "Args1");
        List<String> args = alias.getArguments();
        Assert.assertEquals(args.size(), 2);
        Assert.assertEquals(args.get(0), "Args1");
        Assert.assertEquals(args.get(1), "bar");

        // Check parsing
        // When enabled aliases can override built-ins
        Args1 cmd = cli.parse("Args1");
        Assert.assertEquals(cmd.parameters.size(), 1);
        Assert.assertEquals(cmd.parameters.get(0), "bar");
    }

    @Test
    public void user_aliases_no_args() throws IOException {
        prepareConfig(f, "foo=Args1 bar");

        //@formatter:off
        CliBuilder<Args1> builder = CommandLineInterface.<Args1>builder("test")
                                       .withCommand(Args1.class)
                                       .withDefaultCommand(Args1.class);
        builder.withParser()
               .withUserAliases()
                   .withFilename(f.getName())
                   .withSearchLocation("target/");
        CommandLineInterface<Args1> cli = builder.build();
        //@formatter:on

        // Check definition
        List<AliasMetadata> aliases = cli.getMetadata().getParserConfiguration().getAliases();
        Assert.assertEquals(aliases.size(), 1);

        AliasMetadata alias = aliases.get(0);
        Assert.assertEquals(alias.getName(), "foo");
        List<String> args = alias.getArguments();
        Assert.assertEquals(args.size(), 2);
        Assert.assertEquals(args.get(0), "Args1");
        Assert.assertEquals(args.get(1), "bar");

        // Check parsing
        cli.parse();
    }

    @Test
    public void user_aliases_help_01() throws IOException {
        prepareConfig(f, "a.foo=Args1 bar", "b.foo=Args1 faz");

        //@formatter:off
        CliBuilder<Args1> builder = CommandLineInterface.<Args1>builder("test")
                                       .withCommand(Args1.class);
        builder.withParser()
               .withUserAliases()
                   .withFilename(f.getName())
                   .withPrefix("b.")
                   .withSearchLocation("target/");
        CommandLineInterface<Args1> cli = builder.build();
        //@formatter:on

        // Alias Help
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        new CliGlobalUsageGenerator<Args1>().usage(cli.getMetadata(), output);
        //@formatter:off
        Assert.assertEquals(new String(output.toByteArray(), StandardCharsets.UTF_8),
                StringUtils.join(new String[] {
                "NAME",
                "        test -",
                "",
                "SYNOPSIS",
                "        test <command> [ <args> ]",
                "",
                "COMMANDS",
                "        Args1",
                "            args1 description",
                "",
                "USER DEFINED ALIASES",
                "        This CLI supports user defined aliases which may be placed in a",
                "        test.config file located in the following location(s):",
                "",
                "            1) target/",
                "",
                "        This file contains aliases defined in Java properties file style e.g.",
                "",
                "            b.foo=bar --flag",
                "",
                "        Here an alias foo is defined which causes the bar command to be invoked",
                "        with the --flag option passed to it. Aliases are distinguished from",
                "        other properties in the file by the prefix 'b.' as seen in the example.",
                "",
                "        Alias definitions are subject to the following conditions:",
                "",
                "            - Aliases cannot override existing commands",
                "            - Aliases cannot be defined in terms of other aliases",
                ""
                }, '\n'));
        //@formatter:on
    }

    private String[] generateAliasesChain(int links, boolean circular, String terminal) {
        String[] aliases = new String[links];
        for (int i = 0; i < links; i++) {
            if (i < links - 1) {
                aliases[i] = String.format("%s=%s", (char) ('a' + i), (char) ('a' + i + 1));
            } else if (circular) {
                aliases[i] = String.format("%s=a", (char) ('a' + i));
            } else {
                aliases[i] = String.format("%s=%s", (char) ('a' + i), terminal);
            }
        }
        return aliases;
    }

    @Test
    public void user_aliases_chained_01() throws IOException {
        String[] aliases = generateAliasesChain(2, false, "Args1");
        prepareConfig(f, aliases);

        //@formatter:off
        CliBuilder<Args1> builder = CommandLineInterface.<Args1>builder("test")
                                       .withCommand(Args1.class);
        builder.withParser()
               .withAliasesChaining()
               .withUserAliases()
                    .withProgramName("test")
                    .withSearchLocation("target/");
        CommandLineInterface<Args1> cli = builder.build();
        //@formatter:on

        // Check parsing
        cli.parse(aliases[0].substring(0, aliases[0].indexOf('=')));
    }

    @Test
    public void user_aliases_chained_02() throws IOException {
        for (int i = 1; i < 20; i++) {
            String[] aliases = generateAliasesChain(i, false, "Args1");
            prepareConfig(f, aliases);

            //@formatter:off
            CliBuilder<Args1> builder = CommandLineInterface.<Args1>builder("test")
                                           .withCommand(Args1.class);
            builder.withParser()
                   .withAliasesChaining()
                   .withUserAliases()
                       .withProgramName("test")
                       .withSearchLocation("target/");
            CommandLineInterface<Args1> cli = builder.build();
            //@formatter:on

            // Check parsing
            for (int j = 0; j < aliases.length; j++) {
                cli.parse(aliases[j].substring(0, aliases[j].indexOf('=')));
            }
        }
    }

    @Test(expectedExceptions = ParseAliasCircularReferenceException.class)
    public void user_aliases_circular_01() throws IOException {
        String[] aliases = generateAliasesChain(2, true, null);
        prepareConfig(f, aliases);

        //@formatter:off
        CliBuilder<Args1> builder = CommandLineInterface.<Args1>builder("test")
                                       .withCommand(Args1.class);
        builder.withParser()
               .withAliasesChaining()
               .withUserAliases()
                   .withProgramName("test")
                   .withSearchLocation("target/");
        CommandLineInterface<Args1> cli = builder.build();
        //@formatter:on

        // Check parsing
        cli.parse(aliases[0].substring(0, aliases[0].indexOf('=')));
    }

    @Test
    public void user_aliases_circular_02() throws IOException {
        for (int i = 1; i < 20; i++) {
            String[] aliases = generateAliasesChain(i, true, null);
            prepareConfig(f, aliases);

            //@formatter:off
            CliBuilder<Args1> builder = CommandLineInterface.<Args1>builder("test")
                                           .withCommand(Args1.class);
            builder.withParser()
                   .withAliasesChaining()
                   .withUserAliases()
                       .withProgramName("test")
                       .withSearchLocation("target/");
            CommandLineInterface<Args1> cli = builder.build();
            //@formatter:on

            // Check parsing
            for (int j = 0; j < aliases.length; j++) {
                try {
                    cli.parse(aliases[j].substring(0, aliases[j].indexOf('=')));

                    Assert.fail("Did not produce circular reference exception");
                } catch (ParseAliasCircularReferenceException e) {
                    // Expected, continue
                }
            }
        }
    }

    @Test(expectedExceptions = ParseAliasCircularReferenceException.class)
    public void user_aliases_chained_03() throws IOException {
        // Self-referential
        prepareConfig(f, "a=a");

        //@formatter:off
        CliBuilder<Args1> builder = CommandLineInterface.<Args1>builder("test")
                                       .withCommand(Args1.class);
        builder.withParser()
               .withAliasesChaining()
               .withUserAliases()
                   .withProgramName("test")
                   .withSearchLocation("target/");
        CommandLineInterface<Args1> cli = builder.build();
        //@formatter:on

        // Check parsing
        cli.parse("a");
    }

    @Test(expectedExceptions = ParseAliasCircularReferenceException.class)
    public void user_aliases_chained_04() throws IOException {
        // Override a built-in and create a circular reference
        prepareConfig(f, "Args1=a", "a=b", "b=Args1");

        //@formatter:off
        CliBuilder<Args1> builder = CommandLineInterface.<Args1>builder("test")
                                       .withCommand(Args1.class);
        builder.withParser()
               .withAliasesOverridingBuiltIns()
               .withAliasesChaining()
               .withUserAliases()
                   .withProgramName("test")
                   .withSearchLocation("target/");
        CommandLineInterface<Args1> cli = builder.build();
        //@formatter:on

        // Check parsing
        cli.parse("a");
    }

    @Test
    public void user_aliases_force_builtin_01() throws IOException {
        // Override a built-in but break the circular reference with the !
        // specifier
        prepareConfig(f, "Args1=a", "a=b", "b=!Args1 -debug");

        //@formatter:off
        CliBuilder<Args1> builder = Cli.<Args1>builder("test")
                                       .withCommand(Args1.class);
        builder.withParser()
               .withAliasesOverridingBuiltIns()
               .withAliasesChaining()
               .withUserAliases()
                   .withProgramName("test")
                   .withSearchLocation("target/");
        Cli<Args1> cli = builder.build();
        //@formatter:on

        // Check parsing
        Args1 args = cli.parse("Args1");
        Assert.assertTrue(args.debug);
    }

    @Test(expectedExceptions = ParseCommandUnrecognizedException.class)
    public void user_aliases_force_builtin_02() throws IOException {
        // Override a built-in but break the circular reference with the force
        // specifier
        // however specifier is configured so still fails with a command not
        // found instead
        prepareConfig(f, "Args1=a", "a=b", "b=!Args1 -debug");

        //@formatter:off
        CliBuilder<Args1> builder = Cli.<Args1>builder("test")
                                       .withCommand(Args1.class);
        builder.withParser()
               .withAliasesOverridingBuiltIns()
               .withAliasesChaining()
               .withAliasForceBuiltInPrefix('@')
               .withUserAliases()
                   .withProgramName("test")
                   .withSearchLocation("target/");
        Cli<Args1> cli = builder.build();
        //@formatter:on

        // Check parsing
        Args1 args = cli.parse("Args1");
        Assert.assertTrue(args.debug);
    }

    @Test
    public void user_aliases_force_builtin_03() throws IOException {
        // Override a built-in but break the circular reference with the force
        // specifier
        // however specifier is configured so still fails with a command not
        // found instead
        prepareConfig(f, "Args1=a", "a=b", "b=@Args1 -debug");

        //@formatter:off
        CliBuilder<Args1> builder = Cli.<Args1>builder("test")
                                       .withCommand(Args1.class);
        builder.withParser()
               .withAliasesOverridingBuiltIns()
               .withAliasesChaining()
               .withAliasForceBuiltInPrefix('@')
               .withUserAliases()
                   .withProgramName("test")
                   .withSearchLocation("target/");
        Cli<Args1> cli = builder.build();
        //@formatter:on

        // Check parsing
        Args1 args = cli.parse("Args1");
        Assert.assertTrue(args.debug);
    }

    @Test
    public void user_aliases_force_builtin_04() throws IOException {
        prepareConfig(f, "Args1=!Args1 -debug");

        //@formatter:off
        CliBuilder<Args1> builder = Cli.<Args1>builder("test")
                                       .withCommand(Args1.class);
        builder.withParser()
               .withAliasesOverridingBuiltIns()
               .withAliasesChaining()
               .withUserAliases()
                   .withProgramName("test")
                   .withSearchLocation("target/");
        Cli<Args1> cli = builder.build();
        //@formatter:on

        // Check parsing
        Args1 args = cli.parse("Args1");
        Assert.assertTrue(args.debug);

        // Check forcing built-in
        args = cli.parse("!Args1");
        Assert.assertFalse(args.debug);
    }

    @Test(expectedExceptions = ParseAliasCircularReferenceException.class)
    public void user_aliases_force_builtin_05() throws IOException {
        prepareConfig(f, "logs=logs --format Json", "xml=logs --format Xml", "json=logs --format Json");

        //@formatter:off
        CliBuilder<Logs> builder = Cli.<Logs>builder("test")
                                       .withCommand(Logs.class);
        builder.withParser()
               .withAliasesOverridingBuiltIns()
               .withAliasesChaining()
               .withUserAliases()
                   .withProgramName("test")
                   .withSearchLocation("target/");
        Cli<Logs> cli = builder.build();
        //@formatter:on

        cli.parse("logs");
    }

    @Test
    public void user_aliases_force_builtin_06() throws IOException {
        prepareConfig(f, "logs=!logs --format Json", "xml=!logs --format Xml", "json=!logs --format Json");

        //@formatter:off
        CliBuilder<Logs> builder = Cli.<Logs>builder("test")
                                       .withCommand(Logs.class);
        builder.withParser()
               .withAliasesOverridingBuiltIns()
               .withAliasesChaining()
               .withUserAliases()
                   .withProgramName("test")
                   .withSearchLocation("target/");
        Cli<Logs> cli = builder.build();
        //@formatter:on

        Logs logs = cli.parse("logs");
        Assert.assertEquals(logs.format, Logs.Format.Json);

        logs = cli.parse("json");
        Assert.assertEquals(logs.format, Logs.Format.Json);

        logs = cli.parse("xml");
        Assert.assertEquals(logs.format, Logs.Format.Xml);

        logs = cli.parse("logs", "--format", "Text");
        Assert.assertEquals(logs.format, Logs.Format.Text);

        logs = cli.parse("!logs");
        Assert.assertEquals(logs.format, Logs.Format.Text);
    }

    @Test(expectedExceptions = ParseAliasCircularReferenceException.class)
    public void user_aliases_force_builtin_08() throws IOException {
        prepareConfig(f, "logs=logs --format Json", "xml=logs --format Xml", "json=logs --format Json");

        Cli<Logs> cli = new Cli<>(LogsWithParser.class);

        cli.parse("logs");
    }

    @Test
    public void user_aliases_force_builtin_09() throws IOException {
        // Using alternative force prefix
        prepareConfig(f, "logs=@logs --format Json", "xml=@logs --format Xml", "json=@logs --format Json");

        Cli<Logs> cli = new Cli<>(LogsWithParser.class);

        Logs logs = cli.parse("logs");
        Assert.assertEquals(logs.format, Logs.Format.Json);

        logs = cli.parse("json");
        Assert.assertEquals(logs.format, Logs.Format.Json);

        logs = cli.parse("xml");
        Assert.assertEquals(logs.format, Logs.Format.Xml);

        logs = cli.parse("logs", "--format", "Text");
        Assert.assertEquals(logs.format, Logs.Format.Text);

        logs = cli.parse("@logs");
        Assert.assertEquals(logs.format, Logs.Format.Text);
    }

    @Test
    public void user_aliases_force_builtin_10() throws IOException {
        // Override a built-in but when alias overriding not enabled will result
        // in us just getting the built-in implementation
        prepareConfig(f, "Args1=!Args1 -debug");

        //@formatter:off
        CliBuilder<Args1> builder = Cli.<Args1>builder("test")
                                       .withCommand(Args1.class);
        builder.withParser()
               .withUserAliases()
                   .withProgramName("test")
                   .withSearchLocation("target/");
        Cli<Args1> cli = builder.build();
        //@formatter:on

        // Check parsing
        Args1 args = cli.parse("Args1");
        Assert.assertFalse(args.debug);
    }
}
