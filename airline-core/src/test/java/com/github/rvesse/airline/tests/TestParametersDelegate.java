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
import com.github.rvesse.airline.CommandLineInterface;
import com.github.rvesse.airline.SingleCommand;
import com.github.rvesse.airline.annotations.*;
import com.github.rvesse.airline.builder.ParserBuilder;
import com.github.rvesse.airline.model.MetadataLoader;
import com.github.rvesse.airline.parser.errors.ParseArgumentsUnexpectedException;
import com.github.rvesse.airline.parser.errors.ParseException;
import jakarta.inject.Inject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.github.rvesse.airline.tests.TestingUtil.singleCommandParser;
import static org.testng.Assert.*;

/**
 * This class provides a range of tests around Airline's composition mechanism whereby options and arguments may be
 * defined in a separate class provided that a field of that type is present on the class being scanned for Airline
 * metadata.  Historically we used the {@code @Inject} annotation for this but due to increasing conflicts with
 * dependency injection frameworks as of <strong>2.9.0</strong> we moved to use our own
 * {@link com.github.rvesse.airline.annotations.AirlineModule} annotation.  However, for backwards compatibility
 * purposes, we continue to support various {@code @Inject} annotations for the time being, though this will not be the
 * default behaviour in the future.  Therefore, you will see a mixture of all different annotations within this test
 * class.
 *
 * @author dain
 * @author rodionmoiseev
 * @author rvesse
 */
public class TestParametersDelegate {
    @Command(name = "command")
    public static class DelegatingEmptyClassHasNoEffect {
        public static class EmptyDelegate {
            public String nonParamString = "a";
        }

        @Option(name = "-a")
        public boolean isA;
        @Option(name = { "-b", "--long-b" })
        public String bValue = "";
        @AirlineModule
        public EmptyDelegate delegate = new EmptyDelegate();
    }

    @Test
    public void delegatingEmptyClassHasNoEffect() {
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
    public static class DelegatingSetsFieldsOnBothMainParamsAndTheDelegatedParams {
        public static class ComplexDelegate {
            @Option(name = "-c")
            public boolean isC;
            @Option(name = { "-d", "--long-d" })
            public Integer d;
        }

        @Option(name = "-a")
        public boolean isA;
        @Option(name = { "-b", "--long-b" })
        public String bValue = "";
        @AirlineModule
        public ComplexDelegate delegate = new ComplexDelegate();
    }

    @Test
    public void delegatingSetsFieldsOnBothMainParamsAndTheDelegatedParams() {

        DelegatingSetsFieldsOnBothMainParamsAndTheDelegatedParams p =
                singleCommandParser(DelegatingSetsFieldsOnBothMainParamsAndTheDelegatedParams.class)
                        .parse("-c", "--long-d", "123", "--long-b", "bValue");
        assertFalse(p.isA);
        assertEquals(p.bValue, "bValue");
        assertTrue(p.delegate.isC);
        assertEquals(p.delegate.d, Integer.valueOf(123));
    }

    // ========================================================================================================================

    /**
     * This class uses three different injection delegates
     */
    @Command(name = "command")
    @Parser(compositionAnnotationClasses = {
            MetadataLoader.AIRLINE_MODULE,
            MetadataLoader.JAKARTA_INJECT_INJECT,
            MetadataLoader.JAVAX_INJECT_INJECT
    })
    public static class CombinedAndNestedDelegates {
        public static class LeafDelegate {
            @Option(name = "--list")
            public List<String> list = new ArrayList<String>(Arrays.asList(new String[] { "value1", "value2" }));

            @Option(name = "--bool")
            public boolean bool;
        }

        public static class NestedDelegate1 {
            @Inject
            public LeafDelegate leafDelegate = new LeafDelegate();

            @Option(name = { "-d", "--long-d" })
            public Integer d;
        }

        public static class NestedDelegate2 {
            @Option(name = "-c")
            public boolean isC;

            // Intentionally using old javax.inject.Inject to verify Airline copes with mixtures of old and new @Inject
            // annotation
            // Your IDE may complain it can't see this annotation, it comes from shaded repackaging in the
            // airline-backcompact-javaxinject module to force different Maven coordinates as you can't have multiple
            // versions of the same Maven coordinates in your dependency tree
            @javax.inject.Inject
            public NestedDelegate1 nestedDelegate1 = new NestedDelegate1();
        }


        public static class NestedDelegate3 {
            @Option(name = "-e")
            public boolean isE;
        }

        @Option(name = "-a")
        public boolean isA;

        @Option(name = { "-b", "--long-b" })
        public String bValue = "";

        @AirlineModule
        public NestedDelegate2 nestedDelegate2 = new NestedDelegate2();

        @CustomModule
        public NestedDelegate3 nestedDelegate3 = new NestedDelegate3();
    }

    @Test
    public void combinedAndNestedDelegates() {
        CombinedAndNestedDelegates p = singleCommandParser(CombinedAndNestedDelegates.class)
                .parse("-d", "234", "--list", "a", "--list", "b", "-a");
        assertEquals(p.nestedDelegate2.nestedDelegate1.leafDelegate.list,
                     Arrays.asList("value1", "value2", "a", "b"));
        assertFalse(p.nestedDelegate2.nestedDelegate1.leafDelegate.bool);
        assertEquals(p.nestedDelegate2.nestedDelegate1.d, Integer.valueOf(234));
        assertFalse(p.nestedDelegate2.isC);
        assertTrue(p.isA);
        assertEquals(p.bValue, "");
        assertFalse(p.nestedDelegate3.isE);

        checkThrows(new Runnable() {
            @Override
            public void run() {
                singleCommandParser(CombinedAndNestedDelegates.class)
                        .parse("-e");
            }
        }, ParseArgumentsUnexpectedException.class);
    }

    @Test
    public void combinedAndNestedDelegatesWithCustomAnnotations() {
        CombinedAndNestedDelegates p =
                SingleCommand.singleCommand(CombinedAndNestedDelegates.class,
                                            new ParserBuilder<CombinedAndNestedDelegates>().withDefaultCompositionAnnotations()
                                                                                           .withCompositionAnnotations(
                                                                                                   MetadataLoader.JAVAX_INJECT_INJECT,
                                                                                                   MetadataLoader.JAKARTA_INJECT_INJECT,
                                                                                                   CustomModule.class.getCanonicalName())
                                                                                           .build())
                             .parse("-d", "234", "--list", "a", "--list", "b", "-a", "-e");
        assertEquals(p.nestedDelegate2.nestedDelegate1.leafDelegate.list,
                     Arrays.asList(new String[] { "value1", "value2", "a", "b" }));
        assertFalse(p.nestedDelegate2.nestedDelegate1.leafDelegate.bool);
        assertEquals(p.nestedDelegate2.nestedDelegate1.d, Integer.valueOf(234));
        assertFalse(p.nestedDelegate2.isC);
        assertTrue(p.isA);
        assertEquals(p.bValue, "");
        assertTrue(p.nestedDelegate3.isE);
    }

    private void checkThrows(Runnable runnable, Class<? extends ParseException> expectedErrorCls) {
        try {
            runnable.run();
            Assert.fail("Expected a " + expectedErrorCls.getCanonicalName() + " to be thrown");
        } catch (ParseException e) {
            Assert.assertEquals(e.getClass(), expectedErrorCls);
        }
    }

    @Test
    public void combinedAndNestedDelegatesIgnored() {
        final SingleCommand<CombinedAndNestedDelegates> p =
                SingleCommand.singleCommand(CombinedAndNestedDelegates.class,
                                            new ParserBuilder<CombinedAndNestedDelegates>().withCompositionAnnotations(
                                                                                                   "no.such.Annotation")
                                                                                           .build());
        checkThrows(new Runnable() {
            @Override
            public void run() {
                p.parse("-d", "234");
            }
        }, ParseArgumentsUnexpectedException.class);
        checkThrows(new Runnable() {
            @Override
            public void run() {
                p.parse("--list", "a", "b");
            }
        }, ParseArgumentsUnexpectedException.class);
    }

    // ========================================================================================================================

    @Command(name = "command")
    public static class CommandTest {
        public static class Delegate {
            @Option(name = "-a")
            public String a = "b";
        }

        @AirlineModule
        public Delegate delegate = new Delegate();
    }

    @Test
    public void commandTest() {
        CommandTest c = singleCommandParser(CommandTest.class).parse("-a", "a");
        assertEquals(c.delegate.a, "a");
    }

    // ========================================================================================================================

    @Command(name = "command")
    public static class NullDelegatesAreProhibited {
        public static class ComplexDelegate {
            @Option(name = "-a")
            public boolean a;
        }

        @AirlineModule
        public ComplexDelegate delegate;
    }

    @Test
    public void nullDelegatesAreAllowed() {

        NullDelegatesAreProhibited value = singleCommandParser(NullDelegatesAreProhibited.class).parse("-a");
        assertEquals(value.delegate.a, true);
    }

    // ========================================================================================================================

    @Command(name = "command")
    public static class DuplicateDelegateAllowed {
        public static class Delegate {
            @Option(name = "-a")
            public String a;
        }

        @AirlineModule
        public Delegate d1 = new Delegate();
        @AirlineModule
        public Delegate d2 = new Delegate();
    }

    @Test
    public void duplicateDelegateAllowed() {
        DuplicateDelegateAllowed value = singleCommandParser(DuplicateDelegateAllowed.class).parse("-a", "value");
        assertEquals(value.d1.a, "value");
        assertEquals(value.d2.a, "value");
    }

    // ========================================================================================================================

    /**
     * This class uses a mix of injection annotations
     */
    @Command(name = "command")
    public static class DuplicateMainParametersAreAllowed {
        public static class Delegate1 {
            @Arguments
            public List<String> mainParams1 = new ArrayList<>();
        }

        public static class Delegate2 {
            @Arguments
            public List<String> mainParams1 = new ArrayList<>();
        }

        @AirlineModule
        public Delegate1 delegate1 = new Delegate1();

        @AirlineModule
        public Delegate2 delegate2 = new Delegate2();
    }

    @Test
    public void duplicateMainParametersAreAllowed() {
        DuplicateMainParametersAreAllowed value =
                singleCommandParser(DuplicateMainParametersAreAllowed.class).parse("main", "params");
        assertEquals(value.delegate1.mainParams1, Arrays.asList("main", "params"));
        assertEquals(value.delegate2.mainParams1, Arrays.asList("main", "params"));
    }

    // ========================================================================================================================

    @Command(name = "command")
    public static class ConflictingMainParametersAreNotAllowed {
        public static class Delegate1 {
            @Arguments(description = "foo")
            public List<String> mainParams1 = new ArrayList<>();
        }

        public static class Delegate2 {
            @Arguments(description = "bar")
            public List<String> mainParams1 = new ArrayList<>();
        }

        @AirlineModule
        public Delegate1 delegate1 = new Delegate1();

        @AirlineModule
        public Delegate2 delegate2 = new Delegate2();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void conflictingMainParametersAreNotAllowed() {
        singleCommandParser(ConflictingMainParametersAreNotAllowed.class).parse("main", "params");
    }
}
