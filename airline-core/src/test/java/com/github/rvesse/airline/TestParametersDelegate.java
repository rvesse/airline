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

import com.github.rvesse.airline.annotations.Arguments;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.Option;

import org.testng.annotations.Test;

import javax.inject.Inject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.github.rvesse.airline.TestingUtil.singleCommandParser;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * @author dain
 * @author rodionmoiseev
 */
public class TestParametersDelegate
{
    @Command(name = "command")
    public static class DelegatingEmptyClassHasNoEffect
    {
        public static class EmptyDelegate
        {
            public String nonParamString = "a";
        }

        @Option(name = "-a")
        public boolean isA;
        @Option(name = {"-b", "--long-b"})
        public String bValue = "";
        @Inject
        public EmptyDelegate delegate = new EmptyDelegate();
    }

    @Test
    public void delegatingEmptyClassHasNoEffect()
    {
        DelegatingEmptyClassHasNoEffect p = CommandLineInterface.<DelegatingEmptyClassHasNoEffect>builder("foo")
                .withCommand(DelegatingEmptyClassHasNoEffect.class)
                .build()
                .parse("command", "-a", "-b", "someValue");

        assertTrue(p.isA);
        assertEquals(p.bValue, "someValue");
        assertEquals(p.delegate.nonParamString, "a");
    }

    // ========================================================================================================================

    @Command(name = "command")
    public static class DelegatingSetsFieldsOnBothMainParamsAndTheDelegatedParams
    {
        public static class ComplexDelegate
        {
            @Option(name = "-c")
            public boolean isC;
            @Option(name = {"-d", "--long-d"})
            public Integer d;
        }

        @Option(name = "-a")
        public boolean isA;
        @Option(name = {"-b", "--long-b"})
        public String bValue = "";
        @Inject
        public ComplexDelegate delegate = new ComplexDelegate();
    }

    @Test
    public void delegatingSetsFieldsOnBothMainParamsAndTheDelegatedParams()
    {

        DelegatingSetsFieldsOnBothMainParamsAndTheDelegatedParams p = singleCommandParser(DelegatingSetsFieldsOnBothMainParamsAndTheDelegatedParams.class)
                .parse("-c", "--long-d", "123", "--long-b", "bValue");
        assertFalse(p.isA);
        assertEquals(p.bValue, "bValue");
        assertTrue(p.delegate.isC);
        assertEquals(p.delegate.d, Integer.valueOf(123));
    }

    // ========================================================================================================================

    @Command(name = "command")
    public static class CombinedAndNestedDelegates
    {
        public static class LeafDelegate
        {
            @Option(name = "--list")
            public List<String> list = new ArrayList<String>(Arrays.asList(new String[] { "value1", "value2" }));

            @Option(name = "--bool")
            public boolean bool;
        }

        public static class NestedDelegate1
        {
            @Inject
            public LeafDelegate leafDelegate = new LeafDelegate();

            @Option(name = {"-d", "--long-d"})
            public Integer d;
        }

        public static class NestedDelegate2
        {
            @Option(name = "-c")
            public boolean isC;

            @Inject
            public NestedDelegate1 nestedDelegate1 = new NestedDelegate1();
        }

        @Option(name = "-a")
        public boolean isA;

        @Option(name = {"-b", "--long-b"})
        public String bValue = "";

        @Inject
        public NestedDelegate2 nestedDelegate2 = new NestedDelegate2();
    }

    @Test
    public void combinedAndNestedDelegates()
    {
        CombinedAndNestedDelegates p = singleCommandParser(CombinedAndNestedDelegates.class)
                .parse("-d", "234", "--list", "a", "--list", "b", "-a");
        assertEquals(p.nestedDelegate2.nestedDelegate1.leafDelegate.list, Arrays.asList(new String[] { "value1", "value2", "a", "b" }));
        assertFalse(p.nestedDelegate2.nestedDelegate1.leafDelegate.bool);
        assertEquals(p.nestedDelegate2.nestedDelegate1.d, Integer.valueOf(234));
        assertFalse(p.nestedDelegate2.isC);
        assertTrue(p.isA);
        assertEquals(p.bValue, "");
    }

    // ========================================================================================================================

    @Command(name = "command")
    public static class CommandTest
    {
        public static class Delegate
        {
            @Option(name = "-a")
            public String a = "b";
        }

        @Inject
        public Delegate delegate = new Delegate();
    }

    @Test
    public void commandTest()
    {
        CommandTest c = singleCommandParser(CommandTest.class).parse("-a", "a");
        assertEquals(c.delegate.a, "a");
    }

    // ========================================================================================================================

    @Command(name = "command")
    public static class NullDelegatesAreProhibited
    {
        public static class ComplexDelegate
        {
            @Option(name = "-a")
            public boolean a;
        }

        @Inject
        public ComplexDelegate delegate;
    }

    @Test
    public void nullDelegatesAreAllowed()
    {

        NullDelegatesAreProhibited value = singleCommandParser(NullDelegatesAreProhibited.class).parse("-a");
        assertEquals(value.delegate.a, true);
    }

    // ========================================================================================================================

    @Command(name = "command")
    public static class DuplicateDelegateAllowed
    {
        public static class Delegate
        {
            @Option(name = "-a")
            public String a;
        }

        @Inject
        public Delegate d1 = new Delegate();
        @Inject
        public Delegate d2 = new Delegate();
    }

    @Test
    public void duplicateDelegateAllowed()
    {
        DuplicateDelegateAllowed value = singleCommandParser(DuplicateDelegateAllowed.class).parse("-a", "value");
        assertEquals(value.d1.a, "value");
        assertEquals(value.d2.a, "value");
    }

    // ========================================================================================================================

    @Command(name = "command")
    public static class DuplicateMainParametersAreAllowed
    {
        public static class Delegate1
        {
            @Arguments
            public List<String> mainParams1 = new ArrayList<>();
        }

        public static class Delegate2
        {
            @Arguments
            public List<String> mainParams1 = new ArrayList<>();
        }

        @Inject
        public Delegate1 delegate1 = new Delegate1();

        @Inject
        public Delegate2 delegate2 = new Delegate2();
    }

    @Test
    public void duplicateMainParametersAreAllowed()
    {
        DuplicateMainParametersAreAllowed value = singleCommandParser(DuplicateMainParametersAreAllowed.class).parse("main", "params");
        assertEquals(value.delegate1.mainParams1, Arrays.asList(new String[] { "main", "params" }));
        assertEquals(value.delegate2.mainParams1, Arrays.asList(new String[] { "main", "params" }));
    }

    // ========================================================================================================================

    @Command(name = "command")
    public static class ConflictingMainParametersAreNotAllowed
    {
        public static class Delegate1
        {
            @Arguments(description = "foo")
            public List<String> mainParams1 = new ArrayList<>();
        }

        public static class Delegate2
        {
            @Arguments(description = "bar")
            public List<String> mainParams1 = new ArrayList<>();
        }

        @Inject
        public Delegate1 delegate1 = new Delegate1();

        @Inject
        public Delegate2 delegate2 = new Delegate2();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void conflictingMainParametersAreNotAllowed()
    {
        singleCommandParser(ConflictingMainParametersAreNotAllowed.class).parse("main", "params");
    }
}
