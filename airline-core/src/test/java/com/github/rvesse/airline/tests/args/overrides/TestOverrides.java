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
package com.github.rvesse.airline.tests.args.overrides;

import org.testng.annotations.Test;

import com.github.rvesse.airline.SingleCommand;
import com.github.rvesse.airline.model.ArgumentsMetadata;
import com.github.rvesse.airline.model.CommandMetadata;
import com.github.rvesse.airline.model.OptionMetadata;

import static com.github.rvesse.airline.tests.TestingUtil.singleCommandParser;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Tests behaviours with regards to overriding options from class hierarchies
 * 
 */
public class TestOverrides {

    /**
     * Finds an option by name
     * 
     * @param metadata
     *            Metadata
     * @param name
     *            Name
     * @return Option if found, null otherwise
     */
    private OptionMetadata findByName(CommandMetadata metadata, String name) {
        for (OptionMetadata opData : metadata.getAllOptions()) {
            if (opData.getOptions().contains(name))
                return opData;
        }
        return null;
    }

    @Test
    public void merging_additive() {
        SingleCommand<ArgsMergeAddition> parser = singleCommandParser(ArgsMergeAddition.class);
        CommandMetadata metadata = parser.getCommandMetadata();

        OptionMetadata verboseOption = findByName(metadata, "-v");
        assertNotNull(verboseOption);
        assertFalse(verboseOption.isHidden());
        
        OptionMetadata hiddenOption = findByName(metadata, "--hidden");
        assertNotNull(hiddenOption);
        assertTrue(hiddenOption.isHidden());
        
        ArgumentsMetadata argsData = metadata.getArguments();
        assertNotNull(argsData);
    }
    
    @Test
    public void merging_declared_override() {
        SingleCommand<ArgsMergeOverride> parser = singleCommandParser(ArgsMergeOverride.class);
        CommandMetadata metadata = parser.getCommandMetadata();

        OptionMetadata verboseOption = findByName(metadata, "-v");
        assertNotNull(verboseOption);
        assertFalse(verboseOption.isHidden());
        
        OptionMetadata hiddenOption = findByName(metadata, "--hidden");
        assertNotNull(hiddenOption);
        assertTrue(hiddenOption.isOverride());
        assertFalse(hiddenOption.isHidden());
        assertFalse(hiddenOption.isSealed());
        
        ArgumentsMetadata argsData = metadata.getArguments();
        assertNotNull(argsData);
        
        // Check that the overridden option gets propagated to all classes in the hierarchy
        ArgsMergeOverride cmd = parser.parse("--hidden");
        assertTrue(cmd.hidden);
        assertTrue(((ArgsMergeParent)cmd).hidden);
    }
    
    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = ".*must explicitly specify override.*")
    public void merging_undeclared_override() {
        // Should fail as the override is not explicitly declared in the child class
        SingleCommand<ArgsMergeUndeclaredOverride> parser = singleCommandParser(ArgsMergeUndeclaredOverride.class);
        parser.getCommandMetadata();
    }
    
    @Test
    public void merging_sealed() {
        SingleCommand<ArgsMergeSealed> parser = singleCommandParser(ArgsMergeSealed.class);
        CommandMetadata metadata = parser.getCommandMetadata();

        OptionMetadata verboseOption = findByName(metadata, "-v");
        assertNotNull(verboseOption);
        assertFalse(verboseOption.isHidden());
        
        OptionMetadata hiddenOption = findByName(metadata, "--hidden");
        assertNotNull(hiddenOption);
        assertTrue(hiddenOption.isOverride());
        assertFalse(hiddenOption.isHidden());
        assertTrue(hiddenOption.isSealed());
        
        // Check that the overridden option gets propagated to all classes in the hierarchy
        ArgsMergeSealed cmd = parser.parse("--hidden");
        assertTrue(cmd.hidden);
        assertTrue(((ArgsMergeParent)cmd).hidden);
    }
    
    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = ".*sealed.*")
    public void merging_sealed_override() {
        // Should fail as cannot override an option declared sealed in the parent class
        SingleCommand<ArgsMergeSealedOverride> parser = singleCommandParser(ArgsMergeSealedOverride.class);
        parser.getCommandMetadata();
    }
    
    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = ".*overlapping.*")
    public void merging_overlapping_names() {
        // Should fail as cannot change the names of an option when overriding
        SingleCommand<ArgsMergeNameChange> parser = singleCommandParser(ArgsMergeNameChange.class);
        parser.getCommandMetadata();
    }
    
    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Cannot change the Java type.*")
    public void merging_invalid_type_change() {
        // Should fail as cannot change the Java type of an option when overriding unless there is a legal narrowing change
        SingleCommand<ArgsMergeInvalidTypeChange> parser = singleCommandParser(ArgsMergeInvalidTypeChange.class);
        parser.getCommandMetadata();
    }
    
    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = ".*widening type change.*")
    public void merging_widening_type_change() {
        // Should fail as cannot change the Java type of an option when overriding
        SingleCommand<ArgsMergeWideningTypeChange> parser = singleCommandParser(ArgsMergeWideningTypeChange.class);
        parser.getCommandMetadata();
    }
    
    @Test
    public void merging_narrowing_type_change() {
        // It is legal to make a narrowing type change
        SingleCommand<ArgsMergeNarrowingTypeChange> parser = singleCommandParser(ArgsMergeNarrowingTypeChange.class);
        CommandMetadata metadata = parser.getCommandMetadata();
        
        OptionMetadata testOption = findByName(metadata, "--test");
        assertNotNull(testOption);
        assertEquals(testOption.getArity(), 1);
        assertEquals(testOption.getJavaType(), ArgsMergeTypeParent.C.class);
        
        // Check that the overridden option gets propagated to all classes in the hierarchy
        ArgsMergeNarrowingTypeChange cmd = parser.parse("--test", "12345");
        assertEquals(cmd.test.value, 12345);
        assertEquals(((ArgsMergeTypeParent)cmd).test.value, 12345);
        
        // Note that everything in the hierarchy receives an instance in the narrowest class
        assertEquals(cmd.test.getClass(), ArgsMergeTypeParent.C.class);
        assertEquals(((ArgsMergeTypeParent)cmd).test.getClass(), ArgsMergeTypeParent.C.class);
        assertTrue(cmd.test.getClass().equals(((ArgsMergeTypeParent)cmd).test.getClass()));
    }
    
    @Test
    public void merging_inherits_restrictions() {
        SingleCommand<ArgsMergeInheritRestrictions> parser = singleCommandParser(ArgsMergeInheritRestrictions.class);
        CommandMetadata metadata = parser.getCommandMetadata();
        
        OptionMetadata testOption = findByName(metadata, "--required");
        assertNotNull(testOption);
        assertTrue(testOption.isRequired());
    }
    
    @Test
    public void merging_inherits_changing_restrictions() {
        SingleCommand<ArgsMergeChangeRestrictions> parser = singleCommandParser(ArgsMergeChangeRestrictions.class);
        CommandMetadata metadata = parser.getCommandMetadata();
        
        OptionMetadata testOption = findByName(metadata, "--required");
        assertNotNull(testOption);
        assertFalse(testOption.isRequired());
    }
}
