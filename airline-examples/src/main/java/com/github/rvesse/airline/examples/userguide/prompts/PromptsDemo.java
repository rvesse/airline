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
package com.github.rvesse.airline.examples.userguide.prompts;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.examples.ExampleExecutor;
import com.github.rvesse.airline.examples.ExampleRunnable;
import com.github.rvesse.airline.prompts.Prompt;
import com.github.rvesse.airline.prompts.Prompts;
import com.github.rvesse.airline.prompts.errors.PromptException;
import com.github.rvesse.airline.prompts.errors.PromptTimeoutException;
import com.github.rvesse.airline.prompts.matchers.IgnoresCaseMatcher;

@Command(name = "prompts", description = "Displays some user prompts")
public class PromptsDemo implements ExampleRunnable {

    public static void main(String[] args) {
        ExampleExecutor.executeSingleCommand(PromptsDemo.class, args);
    }

    @Override
    public int run() {
        // A simple yes/no prompt with a response timeout
        //@formatter:off
        Prompt<String> yesNo 
            = Prompts.newYesNoPrompt("Trying prompts for this first time")
                .withTimeout(5, TimeUnit.SECONDS)
                .build();
        //@formatter:off
        try {
            String firstTime = yesNo.promptForLine();
            
            if (StringUtils.equals(firstTime, "Yes")) {
                System.out.println("Hope you enjoy this new feature");
            } else {
                System.out.println("You answered " + firstTime);
            }
        } catch (PromptTimeoutException e) {
            System.out.println();
            // NB - If the prompt times out the underlying provider may still be blocking for input from the user, so
            //      they will need to press enter to clear that block before they can proceed
            System.err.println("You did not answer fast enough, press enter to continue");
        } catch (PromptException e) {
            System.err.println(e.getMessage());
        }
        System.out.println();
        
        // A free form prompt from which a value is retrieved
        //@formatter:off
        Prompt<Integer> pickNumber 
            = Prompts.<Integer>newFreeFormPrompt("Pick a number")
                     .build();
        //@formatter:on
        try {
            Integer number = pickNumber.promptForValue(Integer.class, false);
            System.out.println("You picked number " + number);
        } catch (PromptException e) {
            System.err.println(e.getMessage());
        }
        System.out.println();

        // A constrained option prompt
        //@formatter:off
        Prompt<String> options = 
            Prompts.newOptionsPrompt("Favourite colour", "Red", "Green", "Blue")
                   .withOptionMatcher(new IgnoresCaseMatcher<String>())
                   .withNumericOptionSelection()
                   .withoutTimeout()
                   .build();
        //@formatter:on
        try {
            String colour = options.promptForOption(false);
            System.out.println("Nice choice, I like " + colour + " too");
        } catch (PromptException e) {
            System.err.println(e.getMessage());
        }
        System.out.println();

        // A secure prompt
        //@formatter:off
        Prompt<String> secret =
            Prompts.<String>newFreeFormPrompt("Tell me a secret")
                   .withTimeout(15)
                   .withTimeoutUnit(TimeUnit.SECONDS)
                   .build();
        //@formatter:off
        try {
            char[] data = secret.promptForSecure();
            System.out.println("Your secret had " + data.length + " characters");
        } catch (PromptTimeoutException e) {
            System.out.println();
            // NB - If the prompt times out the underlying provider may still be blocking for input from the user so they will need
            System.err.println("Keeping it to yourself, that's ok, press enter to exit");
        } catch (PromptException e) {
            System.err.println(e.getMessage());
        }
        
        
        return 0;
    }

}
