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

import com.github.rvesse.airline.builder.CliBuilder;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.Option;
import com.github.rvesse.airline.args.Args1;
import com.github.rvesse.airline.args.Args2;
import com.github.rvesse.airline.args.ArgsAllowedValues;
import com.github.rvesse.airline.args.ArgsArityLimited;
import com.github.rvesse.airline.args.ArgsArityString;
import com.github.rvesse.airline.args.ArgsBooleanArity;
import com.github.rvesse.airline.args.ArgsBooleanArity0;
import com.github.rvesse.airline.args.ArgsDefaultOption;
import com.github.rvesse.airline.args.ArgsDefaultOptionAndArguments;
import com.github.rvesse.airline.args.ArgsDefaultOptionBadArity;
import com.github.rvesse.airline.args.ArgsDefaultOptionGlobalScope;
import com.github.rvesse.airline.args.ArgsDefaultOptionGroupScope;
import com.github.rvesse.airline.args.ArgsEnum;
import com.github.rvesse.airline.args.ArgsInherited;
import com.github.rvesse.airline.args.ArgsMultipleDefaultOptions;
import com.github.rvesse.airline.args.ArgsMultipleUnparsed;
import com.github.rvesse.airline.args.ArgsNoArguments;
import com.github.rvesse.airline.args.ArgsNoArgumentsIgnored;
import com.github.rvesse.airline.args.ArgsOutOfMemory;
import com.github.rvesse.airline.args.ArgsPrivate;
import com.github.rvesse.airline.args.ArgsRequired;
import com.github.rvesse.airline.args.ArgsSingleChar;
import com.github.rvesse.airline.args.Arity1;
import com.github.rvesse.airline.args.OptionsRequired;
import com.github.rvesse.airline.command.CommandAdd;
import com.github.rvesse.airline.command.CommandCommit;
import com.github.rvesse.airline.model.CommandMetadata;
import com.github.rvesse.airline.parser.ParseResult;
import com.github.rvesse.airline.parser.errors.ParseArgumentsUnexpectedException;
import com.github.rvesse.airline.parser.errors.ParseException;
import com.github.rvesse.airline.parser.errors.ParseOptionMissingException;
import com.github.rvesse.airline.parser.errors.ParseTooManyArgumentsException;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.github.rvesse.airline.TestingUtil.singleAbbreviatedCommandParser;
import static com.github.rvesse.airline.TestingUtil.singleAbbreviatedOptionParser;
import static com.github.rvesse.airline.TestingUtil.singleCommandParser;
import static com.github.rvesse.airline.TestingUtil.singleCli;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class TestCommand
{
    @Test
    public void simpleArgs()
            throws ParseException
    {
        Args1 args = singleCommandParser(Args1.class).parse(
                "-debug", "-log", "2", "-float", "1.2", "-double", "1.3", "-bigdecimal", "1.4",
                "-groups", "unit", "a", "b", "c");

        assertTrue(args.debug);
        assertEquals(args.verbose.intValue(), 2);
        assertEquals(args.groups, "unit");
        assertEquals(args.parameters, Arrays.asList("a", "b", "c"));
        assertEquals(args.floa, 1.2f, 0.1f);
        assertEquals(args.doub, 1.3f, 0.1f);
        assertEquals(args.bigd, new BigDecimal("1.4"));
    }

    @Test
    public void equalsArgs()
            throws ParseException
    {
        Args1 args = singleCommandParser(Args1.class).parse(
                "-debug", "-log=2", "-float=1.2", "-double=1.3", "-bigdecimal=1.4",
                "-groups=unit", "a", "b", "c");

        assertTrue(args.debug);
        assertEquals(args.verbose.intValue(), 2);
        assertEquals(args.groups, "unit");
        assertEquals(args.parameters, Arrays.asList("a", "b", "c"));
        assertEquals(args.floa, 1.2f, 0.1f);
        assertEquals(args.doub, 1.3f, 0.1f);
        assertEquals(args.bigd, new BigDecimal("1.4"));
    }

    @Test
    public void classicGetoptArgs()
            throws ParseException
    {
        ArgsSingleChar args = singleCommandParser(ArgsSingleChar.class).parse(
                "-lg", "-dsn", "-pa-p", "-2f", "-z", "--Dfoo");

        assertTrue(args.l);
        assertTrue(args.g);
        assertTrue(args.d);
        assertEquals(args.s, "n");
        assertEquals(args.p, "a-p");
        assertFalse(args.n);
        assertTrue(args.two);
        assertEquals(args.f, "-z");
        assertFalse(args.z);
        assertEquals(args.dir, null);
        assertEquals(args.parameters, Arrays.asList("--Dfoo"));
    }

    @Test
    public void classicGetoptFailure()
            throws ParseException
    {
        ArgsSingleChar args = singleCommandParser(ArgsSingleChar.class).parse(
                "-lgX");

        assertFalse(args.l);
        assertFalse(args.g);
        assertEquals(args.parameters, Arrays.asList("-lgX"));
    }

    /**
     * Make sure that if there are args with multiple names (e.g. "-log" and "-verbose"),
     * the usage will only display it once.
     */
    @Test
    public void repeatedArgs()
    {
        SingleCommand<Args1> parser = singleCommandParser(Args1.class);
        CommandMetadata command = parser.getCommandMetadata();
        assertEquals(command.getAllOptions().size(), 8);
    }

    /**
     * Getting the description of a nonexistent command should throw an exception.
     */
    @Test(expectedExceptions = ParseException.class, expectedExceptionsMessageRegExp = "No command specified")
    public void nonexistentCommandShouldThrow()
    {
        singleCli(Args1.class).parse();
    }

    /**
     * Getting the description of a nonexistent command should throw an exception.
     */
    @Test(expectedExceptions = ParseException.class, expectedExceptionsMessageRegExp = "Command 'foo' not recognized")
    public void unrecognizedCommandShouldThrow()
    {
        singleCli(Args1.class).parse("foo");
    }

    /**
     * Required options with multiple names should work with all names.
     */
    private void multipleNames(String option)
    {
        Args1 args = singleCommandParser(Args1.class).parse( option, "2");
        assertEquals(args.verbose.intValue(), 2);
    }

    @Test
    public void multipleNames1()
    {
        multipleNames("-log");
    }

    @Test
    public void multipleNames2()
    {
        multipleNames("-verbose");
    }

    @Test
    public void arityString()
    {
        ArgsArityString args = singleCommandParser(ArgsArityString.class).parse("-pairs", "pair0", "pair1", "rest");

        assertEquals(args.pairs.size(), 2);
        assertEquals(args.pairs.get(0), "pair0");
        assertEquals(args.pairs.get(1), "pair1");
        assertEquals(args.rest.size(), 1);
        assertEquals(args.rest.get(0), "rest");
    }

    @Test(expectedExceptions = ParseException.class)
    public void arity2Fail()
    {
        singleCommandParser(ArgsArityString.class).parse("-pairs", "pair0");
    }
    
    @Test
    public void argumentsRestrictedArity()
    {
        ArgsArityLimited args = singleCommandParser(ArgsArityLimited.class).parse("one", "two", "three");
        
        assertEquals(args.args.size(), 3);
    }
    
    @Test(expectedExceptions = ParseTooManyArgumentsException.class)
    public void argumentsRestrictedArityFail()
    {
        singleCommandParser(ArgsArityLimited.class).parse("one", "two", "three", "four");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void multipleUnparsedFail()
    {
        singleCommandParser(ArgsMultipleUnparsed.class).parse();
    }

    @Test
    public void privateArgs()
    {
        ArgsPrivate args = singleCommandParser(ArgsPrivate.class).parse("-verbose", "3");
        assertEquals(args.getVerbose().intValue(), 3);
    }

    private void argsBoolean1(String[] params, Boolean expected)
    {
        List<String> values = new ArrayList<String>();
        values.addAll(Arrays.asList(params));
        ArgsBooleanArity args = singleCommandParser(ArgsBooleanArity.class).parse(values);
        assertEquals(args.debug, expected);
    }

    private void argsBoolean0(String[] params, Boolean expected)
    {
        List<String> values = new ArrayList<String>();
        values.addAll(Arrays.asList(params));
        ArgsBooleanArity0 args = singleCommandParser(ArgsBooleanArity0.class).parse(values);
        assertEquals(args.debug, expected);
    }

    @Test
    public void booleanArity1()
    {
        argsBoolean1(new String[] {}, Boolean.FALSE);
        argsBoolean1(new String[] {"-debug", "true"}, Boolean.TRUE);
    }

    @Test
    public void booleanArity0()
    {
        argsBoolean0(new String[] {}, Boolean.FALSE);
        argsBoolean0(new String[] {"-debug"}, Boolean.TRUE);
    }

    @Test(expectedExceptions = ParseException.class)
    public void badParameterShouldThrowParameter1Exception()
    {
        singleCommandParser(Args1.class).parse("-log", "foo");
    }

    @Test(expectedExceptions = ParseException.class)
    public void badParameterShouldThrowParameter2Exception()
    {
        singleCommandParser(Args1.class).parse("-long", "foo");
    }

    @Test
    public void listParameters()
    {
        Args2 a = singleCommandParser(Args2.class).parse("-log", "2", "-groups", "unit", "a", "b", "c", "-host", "host2");
        assertEquals(a.verbose.intValue(), 2);
        assertEquals(a.groups, "unit");
        assertEquals(a.hosts, Arrays.asList("host2"));
        assertEquals(a.parameters, Arrays.asList("a", "b", "c"));
    }

    @Test
    public void inheritance()
    {
        ArgsInherited args = singleCommandParser(ArgsInherited.class).parse("-log", "3", "-child", "2");
        assertEquals(args.child.intValue(), 2);
        assertEquals(args.log.intValue(), 3);
    }

    @Test
    public void negativeNumber()
    {
        Args1 a = singleCommandParser(Args1.class).parse("-verbose", "-3");
        assertEquals(a.verbose.intValue(), -3);
    }

    @Test(expectedExceptions = ParseException.class)
    public void requiredMainParameters()
    {
        singleCommandParser(ArgsRequired.class).parse();
    }

    @Test(expectedExceptions = ParseException.class, expectedExceptionsMessageRegExp = ".*option.*missing.*")
    public void requiredOptions()
    {
        singleCommandParser(OptionsRequired.class).parse();
    }

    @Test
    public void ignoresOptionalOptions()
    {
        singleCommandParser(OptionsRequired.class).parse("--required", "foo");
    }

    private void verifyCommandOrdering(String[] commandNames, Class<?>... commands)
    {
        CliBuilder<Object> builder = CommandLineInterface.builder("foo");
        for (Class<?> command : commands) {
            builder = builder.withCommand(command);
        }
        CommandLineInterface<Object> parser = builder.build();

        final List<CommandMetadata> commandParsers = parser.getMetadata().getDefaultGroupCommands();
        assertEquals(commandParsers.size(), commands.length);

        int i = 0;
        for (CommandMetadata commandParser : commandParsers) {
            assertEquals(commandParser.getName(), commandNames[i++]);
        }
    }

    @Test
    public void commandsShouldBeShownInOrderOfInsertion()
    {
        verifyCommandOrdering(new String[] {"add", "commit"},
                CommandAdd.class, CommandCommit.class);
        verifyCommandOrdering(new String[] {"commit", "add"},
                CommandCommit.class, CommandAdd.class);
    }

    @DataProvider
    public static Object[][] f()
    {
        return new Integer[][] {
                new Integer[] {3, 5, 1},
                new Integer[] {3, 8, 1},
                new Integer[] {3, 12, 2},
                new Integer[] {8, 12, 2},
                new Integer[] {9, 10, 1},
        };
    }

    @Test(expectedExceptions = ParseException.class)
    public void arity1Fail()
    {
        singleCommandParser(Arity1.class).parse("-inspect");
    }

    @Test
    public void arity1Success1()
    {
        Arity1 arguments = singleCommandParser(Arity1.class).parse("-inspect", "true");
        assertTrue(arguments.inspect);
    }

    @Test
    public void arity1Success2()
    {
        Arity1 arguments = singleCommandParser(Arity1.class).parse("-inspect", "false");
        assertFalse(arguments.inspect);
    }

    @Test(expectedExceptions = ParseException.class,
            description = "Verify that the main parameter's type is checked to be a List")
    public void wrongMainTypeShouldThrow()
    {
        singleCommandParser(ArgsRequiredWrongMain.class).parse("f1", "f2");
    }

    @Test(description = "This used to run out of memory")
    public void oom()
    {
        singleCommandParser(ArgsOutOfMemory.class).parse();
    }

    @Test
    public void getParametersShouldNotNpe()
    {
        singleCommandParser(Args1.class).parse();
    }

    private static final List<String> V = Arrays.asList("a", "b", "c", "d");

    @DataProvider
    public Object[][] variable()
    {
        return new Object[][] {
                new Object[] {0, V.subList(0, 0), V},
                new Object[] {1, V.subList(0, 1), V.subList(1, 4)},
                new Object[] {2, V.subList(0, 2), V.subList(2, 4)},
                new Object[] {3, V.subList(0, 3), V.subList(3, 4)},
                new Object[] {4, V.subList(0, 4), V.subList(4, 4)},
        };
    }

    @Test
    public void enumArgs()
    {
        ArgsEnum args = singleCommandParser(ArgsEnum.class).parse("-choice", "ONE");
        assertEquals(args.choice, ArgsEnum.ChoiceType.ONE);
    }

    @Test(expectedExceptions = ParseException.class)
    public void enumArgsFail()
    {
        singleCommandParser(ArgsEnum.class).parse("-choice", "A");
    }

    @Test(expectedExceptions = ParseException.class)
    public void shouldThrowIfUnknownOption()
    {
        @Command(name = "A")
        class A
        {
            @Option(name = "-long")
            public long l;
        }
        singleCommandParser(A.class).parse("-lon", "32");
    }
    
    @Test(expectedExceptions = ParseException.class)
    public void commandAbbreviationsDisallowedByDefault() {
        singleCli(Args1.class).parse("Args");
    }
    
    @Test
    public void commandAbbreviationsEnabled() {
        singleAbbreviatedCommandParser(Args1.class).parse("Args");
    }
    
    @Test
    public void optionAbbreviationsDisabledByDefault01() {
        Args1 args = singleCli(Args1.class).parse("Args1", "-deb");
        Assert.assertFalse(args.debug);
        Assert.assertTrue(args.parameters.contains("-deb"));
    }
    
    @Test(expectedExceptions = ParseException.class)
    public void optionAbbreviationsDisabledByDefault02() {
        singleCli(ArgsAllowedValues.class).parse("ArgsAllowedValues", "-mo");
    }
    
    @Test
    public void optionAbbreviationsEnabled() {
        Args1 args = singleAbbreviatedOptionParser(Args1.class).parse("Args1", "-deb");
        Assert.assertTrue(args.debug);
    }
    
    
    @Test
    public void defaultOption01() {
        ArgsDefaultOption cmd = singleCommandParser(ArgsDefaultOption.class).parse("--test", "example");
        assertEquals(cmd.arg, "example");
    }
    
    @Test
    public void defaultOption02() {
        ArgsDefaultOption cmd = singleCommandParser(ArgsDefaultOption.class).parse("example");
        assertEquals(cmd.arg, "example");
    }
    
    @Test(expectedExceptions = ParseOptionMissingException.class)
    public void defaultOption03() {
        singleCommandParser(ArgsDefaultOption.class).parse();
    }
    
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void defaultOptionIncorrectArity() {
        singleCommandParser(ArgsDefaultOptionBadArity.class);
    }
    
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void defaultOptionMultipleDeclarationsForbidden() {
        singleCommandParser(ArgsMultipleDefaultOptions.class);
    }
    
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void defaultOptionAndArgumentsForbidden() {
        singleCommandParser(ArgsDefaultOptionAndArguments.class);
    }
    
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void defaultOptionOnGroupOptionForbidden() {
        singleCommandParser(ArgsDefaultOptionGroupScope.class);
    }
    
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void defaultOptionOnGlobalOptionForbidden() {
        singleCommandParser(ArgsDefaultOptionGlobalScope.class);
    }
    
    @Test
    public void unexpectedArgs1() {
        ArgsNoArguments args = singleCommandParser(ArgsNoArguments.class).parse("-f");
        Assert.assertTrue(args.flag);
    }
    
    @Test(expectedExceptions = ParseArgumentsUnexpectedException.class)
    public void unexpectedArgs2() {
        singleCommandParser(ArgsNoArguments.class).parse("foo");
    }
    
    @Test
    public void unexpectedArgs3() {
        ArgsNoArgumentsIgnored args = singleCommandParser(ArgsNoArgumentsIgnored.class).parse("-f", "foo");
        Assert.assertTrue(args.flag);
        
        ParseResult<ArgsNoArgumentsIgnored> result = singleCommandParser(ArgsNoArgumentsIgnored.class).parseWithResult("-f", "foo");
        Assert.assertTrue(result.wasSuccessful());
        Assert.assertEquals(result.getState().getUnparsedInput().size(), 1);
        Assert.assertEquals(result.getState().getUnparsedInput().get(0), "foo");
    }
}
