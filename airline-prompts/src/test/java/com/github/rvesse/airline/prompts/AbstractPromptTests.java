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

package com.github.rvesse.airline.prompts;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

import com.github.rvesse.airline.annotations.OptionType;
import com.github.rvesse.airline.prompts.builders.PromptBuilder;
import com.github.rvesse.airline.prompts.errors.PromptException;
import com.github.rvesse.airline.prompts.errors.PromptTimeoutException;
import com.github.rvesse.airline.prompts.formatters.QuestionFormat;
import com.github.rvesse.airline.prompts.matchers.IgnoresCaseMatcher;
import com.github.rvesse.airline.prompts.matchers.ValueMatcher;
import com.github.rvesse.airline.prompts.utils.DelayedInputStream;
import com.github.rvesse.airline.types.DefaultTypeConverter;
import com.github.rvesse.airline.types.numerics.abbreviated.KiloAs1000;
import com.github.rvesse.airline.types.numerics.abbreviated.KiloAs1024;

/**
 * Abstract suite of tests for prompts
 * <p>
 * This test suite is designed to cover all the functionality provided by a {@link PromptProvider} implementation,
 * implementors should create sub-classes of this class and implement the
 * {@link #getProvider(InputStream, OutputStream)} method in order to test their implementations.
 * </p>
 *
 */
public abstract class AbstractPromptTests {

    /**
     * Gets the prompt provider to test
     * 
     * @return Prompt provider
     */
    protected abstract PromptProvider getProvider(InputStream input, OutputStream output);

    @Test
    public void key_01() throws PromptException {
        byte[] data = "b".getBytes(StandardCharsets.UTF_8);
        InputStream input = new ByteArrayInputStream(data);
        OutputStream output = new ByteArrayOutputStream();

        //@formatter:off
        Prompt<String> prompt = new PromptBuilder<String>()
                .withPromptProvider(this.getProvider(input, output))
                .withOptions("a", "b", "c")
                .withTimeout(100, TimeUnit.MILLISECONDS)
                .build();
        //@formatter:on

        int key = prompt.promptForKey();
        Assert.assertNotEquals(key, -1);
        Assert.assertEquals((char) key, 'b');
    }

    @Test
    public void key_02() throws PromptException {
        byte[] data = new byte[0];
        InputStream input = new ByteArrayInputStream(data);
        OutputStream output = new ByteArrayOutputStream();

        //@formatter:off
        Prompt<String> prompt = new PromptBuilder<String>()
                .withPromptProvider(this.getProvider(input, output))
                .withOptions("a", "b", "c")
                .withTimeout(100, TimeUnit.MILLISECONDS)
                .build();
        //@formatter:on

        int key = prompt.promptForKey();
        Assert.assertEquals(key, -1);
    }

    @Test(expectedExceptions = PromptTimeoutException.class, expectedExceptionsMessageRegExp = ".*timeout.*")
    public void key_03() throws PromptException {
        byte[] data = new byte[0];
        InputStream input = new DelayedInputStream(new ByteArrayInputStream(data), 110);
        OutputStream output = new ByteArrayOutputStream();

        //@formatter:off
        Prompt<String> prompt = new PromptBuilder<String>()
                .withPromptProvider(this.getProvider(input, output))
                .withOptions("a", "b", "c")
                .withTimeout(100, TimeUnit.MILLISECONDS)
                .build();
        //@formatter:on

        prompt.promptForKey();
    }

    @Test
    public void key_04() throws PromptException {
        byte[] data = "b".getBytes(StandardCharsets.UTF_8);
        InputStream input = new ByteArrayInputStream(data);
        OutputStream output = new ByteArrayOutputStream();

        //@formatter:off
        Prompt<String> prompt = new PromptBuilder<String>()
                .withPromptProvider(this.getProvider(input, output))
                .withOptions("a", "b", "c")
                .withTimeout(100, TimeUnit.MILLISECONDS)
                .build();
        //@formatter:on

        int key = prompt.promptForKey();
        Assert.assertNotEquals(key, -1);
        Assert.assertEquals((char) key, 'b');

        key = prompt.promptForKey();
        Assert.assertEquals(key, -1);
    }

    @Test
    public void secure_01() throws PromptException {
        String password = "password";
        byte[] data = password.getBytes(StandardCharsets.UTF_8);
        InputStream input = new ByteArrayInputStream(data);
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        //@formatter:off
        Prompt<String> prompt = new PromptBuilder<String>()
                .withPromptProvider(this.getProvider(input, output))
                .withTimeout(100, TimeUnit.MILLISECONDS)
                .build();
        //@formatter:on

        if (!prompt.getProvider().supportsSecureReads()) {
            throw new SkipException("Test prompt provider does not support secure reads");
        } else {
            char[] secure = prompt.promptForSecure();
            Assert.assertEquals(secure.length, 8);
            Assert.assertFalse(output.toString().contains(password));
        }
    }

    @Test(expectedExceptions = PromptException.class)
    public void secure_unsupported_01() throws PromptException {
        String password = "password";
        byte[] data = password.getBytes(StandardCharsets.UTF_8);
        InputStream input = new ByteArrayInputStream(data);
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        //@formatter:off
        Prompt<String> prompt = new PromptBuilder<String>()
                .withPromptProvider(this.getProvider(input, output))
                .withTimeout(100, TimeUnit.MILLISECONDS)
                .build();
        //@formatter:on

        if (!prompt.getProvider().supportsSecureReads()) {
            char[] secure = prompt.promptForSecure();
            Assert.assertNull(secure);
        } else {
            throw new SkipException("Test prompt provider supports secure reads");
        }
    }

    @Test
    public void option_01() throws PromptException {
        byte[] data = "b".getBytes(StandardCharsets.UTF_8);
        InputStream input = new ByteArrayInputStream(data);
        OutputStream output = new ByteArrayOutputStream();

        //@formatter:off
        Prompt<String> prompt = new PromptBuilder<String>()
                .withPromptProvider(this.getProvider(input, output))
                .withOptions("a", "b", "c")
                .withTimeout(100, TimeUnit.MILLISECONDS)
                .build();
        //@formatter:on

        String option = prompt.promptForOption(false);
        Assert.assertEquals(option, "b");
    }

    @Test(expectedExceptions = PromptException.class, expectedExceptionsMessageRegExp = ".*does not unambiguously.*")
    public void option_02() throws PromptException {
        byte[] data = "a".getBytes(StandardCharsets.UTF_8);
        InputStream input = new ByteArrayInputStream(data);
        OutputStream output = new ByteArrayOutputStream();

        //@formatter:off
        Prompt<String> prompt = new PromptBuilder<String>()
                .withPromptProvider(this.getProvider(input, output))
                .withOptions("aardvark", "anteater", "another")
                .withTimeout(100, TimeUnit.MILLISECONDS)
                .build();
        //@formatter:on

        prompt.promptForOption(false);
    }

    @Test(expectedExceptions = PromptException.class, expectedExceptionsMessageRegExp = ".*does not unambiguously.*")
    public void option_03() throws PromptException {
        byte[] data = "an".getBytes(StandardCharsets.UTF_8);
        InputStream input = new ByteArrayInputStream(data);
        OutputStream output = new ByteArrayOutputStream();

        //@formatter:off
        Prompt<String> prompt = new PromptBuilder<String>()
                .withPromptProvider(this.getProvider(input, output))
                .withOptions("aardvark", "anteater", "another")
                .withTimeout(100, TimeUnit.MILLISECONDS)
                .build();
        //@formatter:on

        prompt.promptForOption(false);
    }

    @Test(expectedExceptions = PromptException.class, expectedExceptionsMessageRegExp = ".*not a valid option.*")
    public void option_04() throws PromptException {
        byte[] data = "b".getBytes(StandardCharsets.UTF_8);
        InputStream input = new ByteArrayInputStream(data);
        OutputStream output = new ByteArrayOutputStream();

        //@formatter:off
        Prompt<String> prompt = new PromptBuilder<String>()
                .withPromptProvider(this.getProvider(input, output))
                .withOptions("aardvark", "anteater", "another")
                .withTimeout(100, TimeUnit.MILLISECONDS)
                .build();
        //@formatter:on

        prompt.promptForOption(false);
    }

    @Test
    public void option_05() throws PromptException {
        byte[] data = "aa".getBytes(StandardCharsets.UTF_8);
        InputStream input = new ByteArrayInputStream(data);
        OutputStream output = new ByteArrayOutputStream();

        //@formatter:off
        Prompt<String> prompt = new PromptBuilder<String>()
                .withPromptProvider(this.getProvider(input, output))
                .withOptions("aardvark", "anteater", "another")
                .withTimeout(100, TimeUnit.MILLISECONDS)
                .build();
        //@formatter:on

        String option = prompt.promptForOption(false);
        Assert.assertEquals(option, "aardvark");
    }

    @Test
    public void option_06() throws PromptException {
        byte[] data = "aardvark".getBytes(StandardCharsets.UTF_8);
        InputStream input = new ByteArrayInputStream(data);
        OutputStream output = new ByteArrayOutputStream();

        //@formatter:off
        Prompt<String> prompt = new PromptBuilder<String>()
                .withPromptProvider(this.getProvider(input, output))
                .withOptions("aardvark", "anteater", "another")
                .withTimeout(100, TimeUnit.MILLISECONDS)
                .build();
        //@formatter:on

        String option = prompt.promptForOption(false);
        Assert.assertEquals(option, "aardvark");
    }

    @Test(expectedExceptions = PromptException.class, expectedExceptionsMessageRegExp = ".*no options were configured.*")
    public void option_07() throws PromptException {
        byte[] data = "foo".getBytes(StandardCharsets.UTF_8);
        InputStream input = new ByteArrayInputStream(data);
        OutputStream output = new ByteArrayOutputStream();

        //@formatter:off
        Prompt<String> prompt = new PromptBuilder<String>()
                .withPromptProvider(this.getProvider(input, output))
                .withTimeout(100, TimeUnit.MILLISECONDS)
                .build();
        //@formatter:on

        prompt.promptForOption(false);
    }

    @Test(expectedExceptions = PromptException.class, expectedExceptionsMessageRegExp = ".*not a valid option.*")
    public void option_08() throws PromptException {
        byte[] data = "Aardvark".getBytes(StandardCharsets.UTF_8);
        InputStream input = new ByteArrayInputStream(data);
        OutputStream output = new ByteArrayOutputStream();

        //@formatter:off
        Prompt<String> prompt = new PromptBuilder<String>()
                .withPromptProvider(this.getProvider(input, output))
                .withOptions("aardvark", "anteater", "another")
                .withTimeout(100, TimeUnit.MILLISECONDS)
                .build();
        //@formatter:on

        prompt.promptForOption(false);
    }

    @Test
    public void option_09() throws PromptException {
        byte[] data = "Aardvark".getBytes(StandardCharsets.UTF_8);
        InputStream input = new ByteArrayInputStream(data);
        OutputStream output = new ByteArrayOutputStream();

        //@formatter:off
        Prompt<String> prompt = new PromptBuilder<String>()
                .withPromptProvider(this.getProvider(input, output))
                .withOptions("aardvark", "anteater", "another")
                .withOptionMatcher(new IgnoresCaseMatcher<String>())
                .withTimeout(100, TimeUnit.MILLISECONDS)
                .build();
        //@formatter:on

        String option = prompt.promptForOption(false);
        Assert.assertEquals(option, "aardvark");
    }
    
    @Test(expectedExceptions = PromptTimeoutException.class, expectedExceptionsMessageRegExp = ".*timeout.*")
    public void option_10() throws PromptException {
        byte[] data = "Aardvark".getBytes(StandardCharsets.UTF_8);
        InputStream input = new DelayedInputStream(new ByteArrayInputStream(data), 110);
        OutputStream output = new ByteArrayOutputStream();

        //@formatter:off
        Prompt<String> prompt = new PromptBuilder<String>()
                .withPromptProvider(this.getProvider(input, output))
                .withOptions("aardvark", "anteater", "another")
                .withOptionMatcher(new IgnoresCaseMatcher<String>())
                .withTimeout(100, TimeUnit.MILLISECONDS)
                .build();
        //@formatter:on

        String option = prompt.promptForOption(false);
        Assert.assertEquals(option, "aardvark");
    }
    
    @Test(expectedExceptions = PromptException.class, expectedExceptionsMessageRegExp = ".*unambiguously.*")
    public void option_ambiguous_01() throws PromptException {
        byte[] data = "an".getBytes(StandardCharsets.UTF_8);
        InputStream input = new ByteArrayInputStream(data);
        OutputStream output = new ByteArrayOutputStream();

        //@formatter:off
        Prompt<String> prompt = new PromptBuilder<String>()
                .withPromptProvider(this.getProvider(input, output))
                .withOptions("aardvark", "anteater", "another")
                .withOptionMatcher(new IgnoresCaseMatcher<String>())
                .withTimeout(100, TimeUnit.MILLISECONDS)
                .build();
        //@formatter:on

        prompt.promptForOption(false);
    }
    
    @Test
    public void option_ambiguous_02() throws PromptException {
        byte[] data = "ant".getBytes(StandardCharsets.UTF_8);
        InputStream input = new ByteArrayInputStream(data);
        OutputStream output = new ByteArrayOutputStream();

        //@formatter:off
        Prompt<String> prompt = new PromptBuilder<String>()
                .withPromptProvider(this.getProvider(input, output))
                .withOptions("aardvark", "anteater", "another")
                .withOptionMatcher(new IgnoresCaseMatcher<String>())
                .withTimeout(100, TimeUnit.MILLISECONDS)
                .build();
        //@formatter:on

        String option = prompt.promptForOption(false);
        Assert.assertEquals(option, "anteater");
    }
    
    @Test
    public void option_ambiguous_03() throws PromptException {
        byte[] data = "ano".getBytes(StandardCharsets.UTF_8);
        InputStream input = new ByteArrayInputStream(data);
        OutputStream output = new ByteArrayOutputStream();

        //@formatter:off
        Prompt<String> prompt = new PromptBuilder<String>()
                .withPromptProvider(this.getProvider(input, output))
                .withOptions("aardvark", "anteater", "another")
                .withOptionMatcher(new IgnoresCaseMatcher<String>())
                .withTimeout(100, TimeUnit.MILLISECONDS)
                .build();
        //@formatter:on

        String option = prompt.promptForOption(false);
        Assert.assertEquals(option, "another");
    }
    
    @Test
    public void option_duplicates_01() throws PromptException {
        byte[] data = "aardvark".getBytes(StandardCharsets.UTF_8);
        InputStream input = new ByteArrayInputStream(data);
        OutputStream output = new ByteArrayOutputStream();

        //@formatter:off
        Prompt<String> prompt = new PromptBuilder<String>()
                .withPromptProvider(this.getProvider(input, output))
                .withOptions("aardvark", "anteater", "aardvark", "another", "anteater")
                .withTimeout(100, TimeUnit.MILLISECONDS)
                .build();
        //@formatter:on

        String option = prompt.promptForOption(false);
        Assert.assertEquals(option, "aardvark");
    }
    
    @Test
    public void option_duplicates_02() throws PromptException {
        byte[] data = "ant".getBytes(StandardCharsets.UTF_8);
        InputStream input = new ByteArrayInputStream(data);
        OutputStream output = new ByteArrayOutputStream();

        //@formatter:off
        Prompt<String> prompt = new PromptBuilder<String>()
                .withPromptProvider(this.getProvider(input, output))
                .withOptions("aardvark", "anteater", "aardvark", "another", "anteater")
                .withTimeout(100, TimeUnit.MILLISECONDS)
                .build();
        //@formatter:on

        String option = prompt.promptForOption(false);
        Assert.assertEquals(option, "anteater");
    }
    
    @Test
    public void option_duplicates_03() throws PromptException {
        byte[] data = "1".getBytes(StandardCharsets.UTF_8);
        InputStream input = new ByteArrayInputStream(data);
        OutputStream output = new ByteArrayOutputStream();

        //@formatter:off
        Prompt<Double> prompt = new PromptBuilder<Double>()
                .withPromptProvider(this.getProvider(input, output))
                .withOptions(1d, 1.0, 0.1e1, 1.0e0)
                .withOptionMatcher(new ValueMatcher<>(Double.class))
                .withTimeout(100, TimeUnit.MILLISECONDS)
                .build();
        //@formatter:on

        Double option = prompt.promptForOption(false);
        Assert.assertEquals(option, 1.0);
    }

    @Test
    public void option_numeric_01() throws PromptException {
        byte[] data = "1".getBytes(StandardCharsets.UTF_8);
        InputStream input = new ByteArrayInputStream(data);
        OutputStream output = new ByteArrayOutputStream();

        //@formatter:off
        Prompt<String> prompt = new PromptBuilder<String>()
                .withPromptProvider(this.getProvider(input, output))
                .withOptions("aardvark", "anteater", "another")
                .withTimeout(100, TimeUnit.MILLISECONDS)
                .build();
        //@formatter:on

        String option = prompt.promptForOption(false);
        Assert.assertEquals(option, "aardvark");
    }

    @Test
    public void option_numeric_02() throws PromptException {
        byte[] data = "2".getBytes(StandardCharsets.UTF_8);
        InputStream input = new ByteArrayInputStream(data);
        OutputStream output = new ByteArrayOutputStream();

        //@formatter:off
        Prompt<String> prompt = new PromptBuilder<String>()
                .withPromptProvider(this.getProvider(input, output))
                .withOptions("aardvark", "anteater", "another")
                .withTimeout(100, TimeUnit.MILLISECONDS)
                .build();
        //@formatter:on

        String option = prompt.promptForOption(false);
        Assert.assertEquals(option, "anteater");
    }

    @Test(expectedExceptions = PromptException.class, expectedExceptionsMessageRegExp = ".*not a valid option.*")
    public void option_numeric_03() throws PromptException {
        byte[] data = "4".getBytes(StandardCharsets.UTF_8);
        InputStream input = new ByteArrayInputStream(data);
        OutputStream output = new ByteArrayOutputStream();

        //@formatter:off
        Prompt<String> prompt = new PromptBuilder<String>()
                .withPromptProvider(this.getProvider(input, output))
                .withOptions("aardvark", "anteater", "another")
                .withTimeout(100, TimeUnit.MILLISECONDS)
                .build();
        //@formatter:on

        prompt.promptForOption(false);
    }

    @Test(expectedExceptions = PromptException.class, expectedExceptionsMessageRegExp = ".*not a valid option.*")
    public void option_numeric_04() throws PromptException {
        byte[] data = "-1".getBytes(StandardCharsets.UTF_8);
        InputStream input = new ByteArrayInputStream(data);
        OutputStream output = new ByteArrayOutputStream();

        //@formatter:off
        Prompt<String> prompt = new PromptBuilder<String>()
                .withPromptProvider(this.getProvider(input, output))
                .withOptions("aardvark", "anteater", "another")
                .withTimeout(100, TimeUnit.MILLISECONDS)
                .build();
        //@formatter:on

        prompt.promptForOption(false);
    }

    @Test(expectedExceptions = PromptException.class, expectedExceptionsMessageRegExp = ".*not a valid option.*")
    public void option_numeric_05() throws PromptException {
        byte[] data = "0".getBytes(StandardCharsets.UTF_8);
        InputStream input = new ByteArrayInputStream(data);
        OutputStream output = new ByteArrayOutputStream();

        //@formatter:off
        Prompt<String> prompt = new PromptBuilder<String>()
                .withPromptProvider(this.getProvider(input, output))
                .withOptions("aardvark", "anteater", "another")
                .withTimeout(100, TimeUnit.MILLISECONDS)
                .build();
        //@formatter:on

        prompt.promptForOption(false);
    }

    @Test
    public void option_numeric_06() throws PromptException {
        byte[] data = "100".getBytes(StandardCharsets.UTF_8);
        InputStream input = new ByteArrayInputStream(data);
        OutputStream output = new ByteArrayOutputStream();

        //@formatter:off
        Prompt<Integer> prompt = new PromptBuilder<Integer>()
                .withPromptProvider(this.getProvider(input, output))
                .withOptions(1, 10, 100)
                .withTimeout(100, TimeUnit.MILLISECONDS)
                .build();
        //@formatter:on

        Integer value = prompt.promptForOption(false);
        Assert.assertEquals(value, 100);
    }

    @Test
    public void option_numeric_07() throws PromptException {
        byte[] data = "1".getBytes(StandardCharsets.UTF_8);
        InputStream input = new ByteArrayInputStream(data);
        OutputStream output = new ByteArrayOutputStream();

        //@formatter:off
        Prompt<Integer> prompt = new PromptBuilder<Integer>()
                .withPromptProvider(this.getProvider(input, output))
                .withOptions(100, 10, 1)
                .withoutNumericOptionSelection()
                .withTimeout(100, TimeUnit.MILLISECONDS)
                .build();
        //@formatter:on

        // As numeric option selection is disabled we should get the actual
        // value 1
        Integer value = prompt.promptForOption(false);
        Assert.assertEquals(value, 1);
    }

    @Test
    public void option_numeric_08() throws PromptException {
        byte[] data = "1".getBytes(StandardCharsets.UTF_8);
        InputStream input = new ByteArrayInputStream(data);
        OutputStream output = new ByteArrayOutputStream();

        //@formatter:off
        Prompt<Integer> prompt = new PromptBuilder<Integer>()
                .withPromptProvider(this.getProvider(input, output))
                .withOptions(100, 10, 1)
                .withNumericOptionSelection()
                .withTimeout(100, TimeUnit.MILLISECONDS)
                .build();
        //@formatter:on

        // As numeric option selection is enabled we should get the value 100
        // because thats the value at index 1
        Integer value = prompt.promptForOption(false);
        Assert.assertEquals(value, 100);
    }

    @Test
    public void question_01() throws PromptException {
        byte[] data = "Y".getBytes(StandardCharsets.UTF_8);
        InputStream input = new ByteArrayInputStream(data);
        OutputStream output = new ByteArrayOutputStream();

        //@formatter:off
        Prompt<String> prompt = Prompts.newYesNoPrompt("Proceed?")
                .withPromptProvider(this.getProvider(input, output))
                .withTimeout(100, TimeUnit.MILLISECONDS)
                .build();
        //@formatter:on

        int key = prompt.promptForKey();
        Assert.assertNotEquals(key, -1);
        Assert.assertEquals((char) key, 'Y');
    }

    @Test
    public void question_02() throws PromptException {
        byte[] data = "Y".getBytes(StandardCharsets.UTF_8);
        InputStream input = new ByteArrayInputStream(data);
        OutputStream output = new ByteArrayOutputStream();

        //@formatter:off
        Prompt<String> prompt = Prompts.newYesNoPrompt("Proceed?")
                .withPromptProvider(this.getProvider(input, output))
                .withTimeout(100, TimeUnit.MILLISECONDS)
                .build();
        //@formatter:on

        String response = prompt.promptForOption(false);
        Assert.assertEquals(response, "Yes");
    }

    @Test
    public void question_03() throws PromptException {
        byte[] data = "No".getBytes(StandardCharsets.UTF_8);
        InputStream input = new ByteArrayInputStream(data);
        OutputStream output = new ByteArrayOutputStream();

        //@formatter:off
        Prompt<String> prompt = Prompts.newYesNoPrompt("Proceed?")
                .withPromptProvider(this.getProvider(input, output))
                .withTimeout(100, TimeUnit.MILLISECONDS)
                .build();
        //@formatter:on

        String response = prompt.promptForOption(false);
        Assert.assertEquals(response, "No");
    }

    @Test
    public void question_04() throws PromptException {
        byte[] data = "Abort".getBytes(StandardCharsets.UTF_8);
        InputStream input = new ByteArrayInputStream(data);
        OutputStream output = new ByteArrayOutputStream();

        //@formatter:off
        Prompt<String> prompt = Prompts.newYesNoAbortPrompt("Proceed?")
                .withPromptProvider(this.getProvider(input, output))
                .withTimeout(100, TimeUnit.MILLISECONDS)
                .build();
        //@formatter:on

        String response = prompt.promptForOption(false);
        Assert.assertEquals(response, "Abort");
    }

    @Test
    public void question_05() throws PromptException {
        byte[] data = "Cancel".getBytes(StandardCharsets.UTF_8);
        InputStream input = new ByteArrayInputStream(data);
        OutputStream output = new ByteArrayOutputStream();

        //@formatter:off
        Prompt<String> prompt = Prompts.newYesNoCancelPrompt("Proceed?")
                .withPromptProvider(this.getProvider(input, output))
                .withTimeout(100, TimeUnit.MILLISECONDS)
                .build();
        //@formatter:on

        String response = prompt.promptForOption(false);
        Assert.assertEquals(response, "Cancel");
    }

    @Test(expectedExceptions = PromptException.class, expectedExceptionsMessageRegExp = ".*not a valid option.*")
    public void question_06() throws PromptException {
        byte[] data = "foo".getBytes(StandardCharsets.UTF_8);
        InputStream input = new ByteArrayInputStream(data);
        OutputStream output = new ByteArrayOutputStream();

        //@formatter:off
        Prompt<String> prompt = Prompts.newYesNoAbortPrompt("Proceed?")
                .withPromptProvider(this.getProvider(input, output))
                .withTimeout(100, TimeUnit.MILLISECONDS)
                .build();
        //@formatter:on

        prompt.promptForOption(false);
    }

    @Test
    public void question_07() throws PromptException {
        byte[] data = "a".getBytes(StandardCharsets.UTF_8);
        InputStream input = new ByteArrayInputStream(data);
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        //@formatter:off
        Prompt<String> prompt = new PromptBuilder<String>()
                .withPromptProvider(this.getProvider(input, output))
                .withFormatter(new QuestionFormat<>())
                .withOptions("alpha", "beta")
                .withPromptMessage("Which option?")
                .withTimeout(100, TimeUnit.MILLISECONDS)
                .build();
        //@formatter:on

        String response = prompt.promptForOption(false);
        Assert.assertEquals(response, "alpha");
        String outputData = new String(output.toByteArray(), StandardCharsets.UTF_8);
        Assert.assertTrue(outputData.contains("Which option?"));
    }

    @Test
    public void value_01() throws PromptException {
        byte[] data = "8k".getBytes(StandardCharsets.UTF_8);
        InputStream input = new ByteArrayInputStream(data);
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        //@formatter:off
        Prompt<String> prompt = new PromptBuilder<String>()
                .withPromptProvider(this.getProvider(input, output))
                .withFormatter(new QuestionFormat<>())
                .withPromptMessage("How much memory would you like?")
                .withTypeConverter(new DefaultTypeConverter(new KiloAs1024()))
                .withTimeout(100, TimeUnit.MILLISECONDS)
                .build();
        //@formatter:on

        Long value = prompt.promptForValue(Long.class, false);
        Assert.assertEquals(value.longValue(), 8192l);
    }

    @Test
    public void value_02() throws PromptException {
        byte[] data = "16M".getBytes(StandardCharsets.UTF_8);
        InputStream input = new ByteArrayInputStream(data);
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        //@formatter:off
        Prompt<String> prompt = new PromptBuilder<String>()
                .withPromptProvider(this.getProvider(input, output))
                .withFormatter(new QuestionFormat<>())
                .withPromptMessage("How much memory would you like?")
                .withTypeConverter(new DefaultTypeConverter(new KiloAs1024()))
                .withTimeout(100, TimeUnit.MILLISECONDS)
                .build();
        //@formatter:on

        Long value = prompt.promptForValue(Long.class, false);
        Assert.assertEquals(value.longValue(), 16l * 1024l * 1024l);
    }

    @Test
    public void value_03() throws PromptException {
        byte[] data = "16M".getBytes(StandardCharsets.UTF_8);
        InputStream input = new ByteArrayInputStream(data);
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        //@formatter:off
        Prompt<String> prompt = new PromptBuilder<String>()
                .withPromptProvider(this.getProvider(input, output))
                .withFormatter(new QuestionFormat<>())
                .withPromptMessage("How much memory would you like?")
                .withTypeConverter(new DefaultTypeConverter(new KiloAs1000()))
                .withTimeout(100, TimeUnit.MILLISECONDS)
                .build();
        //@formatter:on

        Long value = prompt.promptForValue(Long.class, false);
        Assert.assertEquals(value.longValue(), 16l * 1000l * 1000l);
    }

    @Test
    public void value_04() throws PromptException {
        byte[] data = OptionType.GROUP.name().getBytes(StandardCharsets.UTF_8);
        InputStream input = new ByteArrayInputStream(data);
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        //@formatter:off
        Prompt<String> prompt = new PromptBuilder<String>()
                .withPromptProvider(this.getProvider(input, output))
                .withQuestionFormatter()
                .withPromptMessage("What option type?")
                .withTimeout(100, TimeUnit.MILLISECONDS)
                .build();
        //@formatter:on

        OptionType value = prompt.promptForValue(OptionType.class, false);
        Assert.assertEquals(value, OptionType.GROUP);
    }
    
    @Test
    public void value_matching_01() throws PromptException {
        byte[] data = "4".getBytes(StandardCharsets.UTF_8);
        InputStream input = new ByteArrayInputStream(data);
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        //@formatter:off
        Prompt<Double> prompt = new PromptBuilder<Double>()
                .withPromptProvider(this.getProvider(input, output))
                .withListFormatter()
                .withOptions(1.0, 2.0, 4.0, 8.0, 16.0)
                .withOptionMatcher(new ValueMatcher<>(Double.class))
                .withPromptMessage("What scaling factor?")
                .withTimeout(100, TimeUnit.MILLISECONDS)
                .build();
        //@formatter:on

        Double value = prompt.promptForOption(false);
        Assert.assertEquals(value, 4.0);
    }
    
    @Test
    public void value_matching_02() throws PromptException {
        byte[] data = "4.0".getBytes(StandardCharsets.UTF_8);
        InputStream input = new ByteArrayInputStream(data);
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        //@formatter:off
        Prompt<Double> prompt = new PromptBuilder<Double>()
                .withPromptProvider(this.getProvider(input, output))
                .withListFormatter()
                .withOptions(1.0, 2.0, 4.0, 8.0, 16.0)
                .withOptionMatcher(new ValueMatcher<>(Double.class))
                .withPromptMessage("What scaling factor?")
                .withTimeout(100, TimeUnit.MILLISECONDS)
                .build();
        //@formatter:on

        Double value = prompt.promptForOption(false);
        Assert.assertEquals(value, 4.0);
    }
    
    @Test
    public void value_matching_03() throws PromptException {
        byte[] data = "4.0E0".getBytes(StandardCharsets.UTF_8);
        InputStream input = new ByteArrayInputStream(data);
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        //@formatter:off
        Prompt<Double> prompt = new PromptBuilder<Double>()
                .withPromptProvider(this.getProvider(input, output))
                .withListFormatter()
                .withOptions(1.0, 2.0, 4.0, 8.0, 16.0)
                .withOptionMatcher(new ValueMatcher<>(Double.class))
                .withPromptMessage("What scaling factor?")
                .withTimeout(100, TimeUnit.MILLISECONDS)
                .build();
        //@formatter:on

        Double value = prompt.promptForOption(false);
        Assert.assertEquals(value, 4.0);
    }
    
    @Test
    public void value_matching_04() throws PromptException {
        byte[] data = "4.0e0".getBytes(StandardCharsets.UTF_8);
        InputStream input = new ByteArrayInputStream(data);
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        //@formatter:off
        Prompt<Double> prompt = new PromptBuilder<Double>()
                .withPromptProvider(this.getProvider(input, output))
                .withListFormatter()
                .withOptions(1.0, 2.0, 4.0, 8.0, 16.0)
                .withOptionMatcher(new ValueMatcher<>(Double.class))
                .withPromptMessage("What scaling factor?")
                .withTimeout(100, TimeUnit.MILLISECONDS)
                .build();
        //@formatter:on

        Double value = prompt.promptForOption(false);
        Assert.assertEquals(value, 4.0);
    }
    
    @Test
    public void value_matching_05() throws PromptException {
        byte[] data = "0.4e1".getBytes(StandardCharsets.UTF_8);
        InputStream input = new ByteArrayInputStream(data);
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        //@formatter:off
        Prompt<Double> prompt = new PromptBuilder<Double>()
                .withPromptProvider(this.getProvider(input, output))
                .withListFormatter()
                .withOptions(1.0, 2.0, 4.0, 8.0, 16.0)
                .withOptionMatcher(new ValueMatcher<>(Double.class))
                .withPromptMessage("What scaling factor?")
                .withTimeout(100, TimeUnit.MILLISECONDS)
                .build();
        //@formatter:on

        Double value = prompt.promptForOption(false);
        Assert.assertEquals(value, 4.0);
    }
    
    @Test(expectedExceptions = PromptException.class, expectedExceptionsMessageRegExp = ".*'32'.*not a valid option.*")
    public void value_matching_06() throws PromptException {
        byte[] data = "32".getBytes(StandardCharsets.UTF_8);
        InputStream input = new ByteArrayInputStream(data);
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        //@formatter:off
        Prompt<Double> prompt = new PromptBuilder<Double>()
                .withPromptProvider(this.getProvider(input, output))
                .withListFormatter()
                .withOptions(1.0, 2.0, 4.0, 8.0, 16.0)
                .withOptionMatcher(new ValueMatcher<>(Double.class))
                .withPromptMessage("What scaling factor?")
                .withTimeout(100, TimeUnit.MILLISECONDS)
                .build();
        //@formatter:on

        prompt.promptForOption(false);
    }
    
    @Test(timeOut = 2500)
    public void blocking_01() throws PromptException {
        byte[] data = "b".getBytes(StandardCharsets.UTF_8);
        InputStream input = new ByteArrayInputStream(data);
        OutputStream output = new ByteArrayOutputStream();

        //@formatter:off
        Prompt<String> prompt = new PromptBuilder<String>()
                .withPromptProvider(this.getProvider(input, output))
                .withOptions("a", "b", "c")
                .build();
        //@formatter:on

        String option = prompt.promptForOption(false);
        Assert.assertEquals(option, "b");
    }
    
    @Test(timeOut = 2500)
    public void blocking_02() throws PromptException {
        byte[] data = "b".getBytes(StandardCharsets.UTF_8);
        InputStream input = new ByteArrayInputStream(data);
        OutputStream output = new ByteArrayOutputStream();

        //@formatter:off
        Prompt<String> prompt = new PromptBuilder<String>()
                .withPromptProvider(this.getProvider(input, output))
                .withOptions("a", "b", "c")
                .build();
        //@formatter:on

        char key = (char) prompt.promptForKey();
        Assert.assertEquals(key, 'b');
    }
    
    @Test(timeOut = 2500)
    public void blocking_03() throws PromptException {
        byte[] data = "4".getBytes(StandardCharsets.UTF_8);
        InputStream input = new ByteArrayInputStream(data);
        OutputStream output = new ByteArrayOutputStream();

        //@formatter:off
        Prompt<Integer> prompt = new PromptBuilder<Integer>()
                .withPromptProvider(this.getProvider(input, output))
                .withOptions(1, 2, 3, 4)
                .build();
        //@formatter:on

        Integer value = prompt.promptForValue(Integer.class, false);
        Assert.assertNotNull(value);
        Assert.assertEquals(value.intValue(), 4);
    }
}
