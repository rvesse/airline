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

import com.github.rvesse.airline.io.AnsiControlCodes;
import com.github.rvesse.airline.io.decorations.BasicDecoration;
import com.github.rvesse.airline.prompts.errors.PromptException;
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
}
