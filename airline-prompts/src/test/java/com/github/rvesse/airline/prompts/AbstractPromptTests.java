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
import org.testng.annotations.Test;

import com.github.rvesse.airline.io.decorations.BasicDecoration;
import com.github.rvesse.airline.prompts.builders.PromptBuilder;
import com.github.rvesse.airline.prompts.errors.PromptException;
import com.github.rvesse.airline.prompts.formatters.QuestionFormat;
import com.github.rvesse.airline.prompts.utils.DelayedInputStream;

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
    
    @Test(expectedExceptions = PromptException.class, expectedExceptionsMessageRegExp = ".*timeout.*")
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
        
        char[] secure = prompt.promptForSecure();
        Assert.assertEquals(secure.length, 8);
        
        String outputData = new String(output.toByteArray(), StandardCharsets.UTF_8);
        Assert.assertTrue(outputData.contains(BasicDecoration.CONCEAL.getAnsiDecorationEnabledControlCode()));
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
    
    @Test(expectedExceptions = PromptException.class, expectedExceptionsMessageRegExp = ".*not a valid response.*")
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
    
    @Test(expectedExceptions = PromptException.class, expectedExceptionsMessageRegExp = ".*not a valid response.*")
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
    
    @Test(expectedExceptions = PromptException.class, expectedExceptionsMessageRegExp = ".*not a valid response.*")
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
    
    @Test(expectedExceptions = PromptException.class, expectedExceptionsMessageRegExp = ".*not a valid response.*")
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
    
    @Test(expectedExceptions = PromptException.class, expectedExceptionsMessageRegExp = ".*not a valid response.*")
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
}
