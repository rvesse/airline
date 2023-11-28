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
package com.github.rvesse.airline.tests;

import com.github.rvesse.airline.Cli;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.builder.CliBuilder;

/**
 * Tests related to #93 where commands with different classes but the same name
 * silently suppressed each other
 *
 */
public class TestCommandSuppression {

    @Command(name = "a")
    public static class A {

    }

    @Command(name = "a")
    public static class A2 {

    }

    @SuppressWarnings("unchecked")
    @Test(expectedExceptions =  IllegalArgumentException.class, expectedExceptionsMessageRegExp = ".*both declare the command name 'a'.*")
    public void suppressed_01() {
        //@formatter:off
        new CliBuilder<Object>("test")
            .withCommands(A.class,
                          A2.class)
            .build();
        //@formatter:on
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void suppressed_02() {
        //@formatter:off
        CommandLineInterface<Object> cli =
            new CliBuilder<Object>("test")
            // Duplicate instances of the same command are acceptable if odd
            .withCommands(A.class,
                          A.class)
            .build();
        //@formatter:on
        
        Object cmd = cli.parse("a");
        Assert.assertEquals(cmd.getClass(), A.class);
    }
    
    @Test
    public void suppressed_03() {
        //@formatter:off
        CliBuilder<Object> builder =
            new CliBuilder<Object>("test")
            // Instances with the same name in different groups is acceptable
            .withCommand(A.class);
        builder.withGroup("sub")
               .withCommand(A2.class);
        CommandLineInterface<Object> cli = builder.build();
        //@formatter:on
        
        Object cmd = cli.parse("a");
        Assert.assertEquals(cmd.getClass(), A.class);
        
        cmd = cli.parse("sub", "a");
        Assert.assertEquals(cmd.getClass(), A2.class);
    }
    
    @SuppressWarnings("unchecked")
    @Test(expectedExceptions =  IllegalArgumentException.class, expectedExceptionsMessageRegExp = ".*both declare the command name 'a'.*'sub'.*")
    public void suppressed_04() {
        //@formatter:off
        CliBuilder<Object> builder =
            new CliBuilder<Object>("test")
            // Instances with the same name in different groups is acceptable
            .withCommand(A.class);
        builder.withGroup("sub")
               // However if there are duplicates in a sub-group this is still illegal
               .withCommands(A.class,
                             A2.class);
        builder.build();
        //@formatter:on
    }
    
    @SuppressWarnings("unchecked")
    @Test(expectedExceptions =  IllegalArgumentException.class, expectedExceptionsMessageRegExp = ".*both declare the command name 'a'.*'sub foo bar'.*")
    public void suppressed_05() {
        //@formatter:off
        CliBuilder<Object> builder =
            new CliBuilder<Object>("test")
            // Instances with the same name in different groups is acceptable
            .withCommand(A.class);
        builder.withGroup("sub")
               .withSubGroup("foo")
               .withSubGroup("bar")
               // However if there are duplicates in a sub-group this is still illegal
               .withCommands(A.class,
                             A2.class);
        builder.build();
        //@formatter:on
    }
    
    @Test(expectedExceptions =  IllegalArgumentException.class, expectedExceptionsMessageRegExp = ".*both declare the command name 'a'.*")
    public void suppressed_06() {
        //@formatter:off
        new CliBuilder<Object>("test")
            // The default command should not suppress a command in the group
            .withCommand(A.class)
            .withDefaultCommand(A2.class)
            .build();
        //@formatter:on
    }
    
    @Test(expectedExceptions =  IllegalArgumentException.class, expectedExceptionsMessageRegExp = ".*both declare the command name 'a'.*")
    public void suppressed_07() {
        //@formatter:off
        new CliBuilder<Object>("test")
            // The default command should not suppress a command in the group
            .withGroup("sub")
            .withCommand(A.class)
            .withDefaultCommand(A2.class)
            .parent()
            .build();
        //@formatter:on
    }
    
    @Test(expectedExceptions =  IllegalArgumentException.class, expectedExceptionsMessageRegExp = ".*both declare the command name 'a'.*")
    public void suppressed_08() {
        //@formatter:off
        new CliBuilder<Object>("test")
            // The default command should not suppress a command in the group
            .withGroup("sub")
            .withSubGroup("foo")
            .withSubGroup("bar")
            .withCommand(A.class)
            .withDefaultCommand(A2.class)
            .parent()
            .build();
        //@formatter:on
    }
}
