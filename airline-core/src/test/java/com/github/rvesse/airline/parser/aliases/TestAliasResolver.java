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

import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.rvesse.airline.CommandLineInterface;
import com.github.rvesse.airline.args.Args1;
import com.github.rvesse.airline.builder.CliBuilder;
import com.github.rvesse.airline.parser.errors.ParseAliasCircularReferenceException;

public class TestAliasResolver {
    
    /**
     * Prepares the basic builder
     * @return Builder
     */
    private CliBuilder<Args1> prepareBuilder() {
        //@formatter:off
        return CommandLineInterface.<Args1>builder("test")
                  .withCommand(Args1.class)
                  .withDefaultCommand(Args1.class);
        //@formatter:on
    }

    @Test
    public void alias_resolution_simple_01() {
        //@formatter:off
        CliBuilder<Args1> builder = prepareBuilder();
        builder.withParser()
               .withAlias("a")
               .withArgument("-debug");
        //@formatter:on
        
        Args1 cmd = builder.build().parse("a");
        Assert.assertTrue(cmd.debug);
    }
    
    @Test
    public void alias_resolution_simple_02() {
        //@formatter:off
        CliBuilder<Args1> builder = prepareBuilder();
        builder.withParser()
               .withAlias("a")
               .withArguments("-verbose", "4");
        //@formatter:on
        
        Args1 cmd = builder.build().parse("a");
        Assert.assertEquals(cmd.verbose.intValue(), 4);
    }
    
    @Test
    public void alias_resolution_positional_01() {
        //@formatter:off
        CliBuilder<Args1> builder = prepareBuilder();
        builder.withParser()
               .withAlias("a")
               .withArguments("-verbose", "$1");
        //@formatter:on
        
        Args1 cmd = builder.build().parse("a", "7");
        Assert.assertEquals(cmd.verbose.intValue(), 7);
    }
    
    @Test
    public void alias_resolution_positional_02() {
        //@formatter:off
        CliBuilder<Args1> builder = prepareBuilder();
        builder.withParser()
               .withAlias("a")
               .withArguments("-verbose", "$2", "-long", "$1");
        //@formatter:on
        
        Args1 cmd = builder.build().parse("a", "3", "6");
        Assert.assertEquals(cmd.verbose.intValue(), 6);
        Assert.assertEquals(cmd.l, 3l);
    }
    
    @Test
    public void alias_resolution_chained_01() {
        //@formatter:off
        CliBuilder<Args1> builder = prepareBuilder();
        builder.withParser()
               .withAlias("a")
               .withArguments("b");
        builder.withParser()
               .withAlias("b")
               .withArgument("-debug");
        //@formatter:on
        
        Args1 cmd = builder.build().parse("a");
        Assert.assertFalse(cmd.debug);
        Assert.assertEquals(cmd.parameters.size(), 1);
        Assert.assertTrue(cmd.parameters.contains("b"));
    }
    
    @Test
    public void alias_resolution_chained_02() {
        //@formatter:off
        CliBuilder<Args1> builder = prepareBuilder();
        builder.withParser()
               .withAlias("a")
               .withArguments("b");
        builder.withParser()
               .withAlias("b")
               .withArgument("-debug");
        builder.withParser()
               .withAliasesChaining();
        //@formatter:on
        
        Args1 cmd = builder.build().parse("a");
        Assert.assertTrue(cmd.debug);
        Assert.assertEquals(cmd.parameters.size(), 0);
        Assert.assertFalse(cmd.parameters.contains("b"));
    }
    
    @Test(expectedExceptions = ParseAliasCircularReferenceException.class)
    public void alias_resolution_chained_03() {
        //@formatter:off
        CliBuilder<Args1> builder = prepareBuilder();
        builder.withParser()
               .withAlias("a")
               .withArguments("b");
        builder.withParser()
               .withAlias("b")
               .withArguments("c", "-debug");
        builder.withParser()
               .withAlias("c")
               .withArgument("a");
        builder.withParser()
               .withAliasesChaining();
        //@formatter:on
        
        builder.build().parse("a");
    }
}
