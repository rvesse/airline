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

import org.apache.commons.lang3.StringUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.rvesse.airline.builder.ParserBuilder;
import com.github.rvesse.airline.help.Help;
import com.github.rvesse.airline.model.ParserMetadata;
import com.github.rvesse.airline.parser.errors.ParseCommandUnrecognizedException;

public class TestGitByAnnotation {
    @Test
    public void test() {
        // simple command parsing example
        git("add", "-p", "file");
        git("remote", "add", "origin", "git@github.com:airlift/airline.git");
        git("-v", "remote", "show", "origin");
        // test default command
        git("remote");
        git("remote", "origin");
        git("remote", "-n", "origin");
        git("-v", "remote", "origin");

        // show help
        git();
        git("help");
        git("help", "git");
        git("help", "add");
        git("help", "remote");
        git("help", "remote", "show");
    }

    @Test
    public void testParserConfigOverride() {
        ParserMetadata<Runnable> parserConfig = new ParserBuilder<Runnable>().withCommandAbbreviation().build();

        git(parserConfig, "rem");
        git(parserConfig, "rem show");
        git(parserConfig, "rem sh");
        git(parserConfig, "remote sh");
    }

    @Test
    public void testParserConfigOverride2() {
        ParserMetadata<Runnable> parserConfig = new ParserBuilder<Runnable>().withCommandAbbreviation().build();

        // This works because the base config includes the alias foo
        gitOverridden(null, "foo");

        // These all work because our override enabled command abbreviation
        gitOverridden(parserConfig, "rem");
        gitOverridden(parserConfig, "rem", "show");
        gitOverridden(parserConfig, "rem", "sh");
        gitOverridden(parserConfig, "remote", "sh");

        // This returns help because the alias definition from the base config is overridden by the provided parser config
        CommandLineInterface<Runnable> gitParser = new CommandLineInterface<Runnable>(
                GitWithCliAnnotation2.class, parserConfig);
        Runnable cmd = gitParser.parse("foo");
        Assert.assertEquals(cmd.getClass(), Help.class);
    }

    private void git(String... args) {
        System.out.println("$ git " + StringUtils.join(args, ' '));
        GitWithCliAnnotation.run(args);
        System.out.println();
    }

    private void git(ParserMetadata<Runnable> parserConfig, String... args) {
        System.out.println("$ git " + StringUtils.join(args, ' '));
        CommandLineInterface<Runnable> gitParser = new CommandLineInterface<Runnable>(
                GitWithCliAnnotation.class, parserConfig);

        gitParser.parse(args).run();
        System.out.println();
    }

    private void gitOverridden(ParserMetadata<Runnable> parserConfig, String... args) {
        System.out.println("$ git " + StringUtils.join(args, ' '));
        CommandLineInterface<Runnable> gitParser = new CommandLineInterface<Runnable>(
                GitWithCliAnnotation2.class, parserConfig);

        gitParser.parse(args).run();
        System.out.println();
    }
}
