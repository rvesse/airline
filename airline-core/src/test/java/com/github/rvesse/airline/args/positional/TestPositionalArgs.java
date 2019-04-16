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

package com.github.rvesse.airline.args.positional;

import com.github.rvesse.airline.SingleCommand;
import com.github.rvesse.airline.help.cli.CliCommandUsageGenerator;
import com.github.rvesse.airline.model.PositionalArgumentMetadata;
import com.github.rvesse.airline.parser.errors.ParseArgumentsUnexpectedException;

import static com.github.rvesse.airline.TestingUtil.singleCommandParser;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.testng.Assert;
import org.testng.annotations.Test;

@SuppressWarnings("unused")
public class TestPositionalArgs {

    @Test
    public void positional_args_01() {
        SingleCommand<ArgsPositional> parser = singleCommandParser(ArgsPositional.class);
        assertFalse(parser.getCommandMetadata().getPositionalArguments().isEmpty());
        assertEquals(parser.getCommandMetadata().getPositionalArguments().size(), 2);

        PositionalArgumentMetadata posArg = parser.getCommandMetadata().getPositionalArguments().get(0);
        assertEquals(posArg.getZeroBasedPosition(), 0);
        assertEquals(posArg.getOneBasedPosition(), 1);
        assertEquals(posArg.getJavaType(), String.class);
        assertTrue(posArg.isRequired());
        assertEquals(posArg.getTitle(), "File");

        posArg = parser.getCommandMetadata().getPositionalArguments().get(1);
        assertEquals(posArg.getZeroBasedPosition(), 1);
        assertEquals(posArg.getOneBasedPosition(), 2);
        assertEquals(posArg.getJavaType(), Integer.class);
        assertEquals(posArg.getTitle(), "Mode");

        ArgsPositional cmd = parser.parse("example.txt", "600", "extra");
        assertEquals(cmd.file, "example.txt");
        assertEquals(cmd.mode, new Integer(600));
        assertEquals(cmd.parameters.size(), 1);
        assertEquals(cmd.parameters.get(0), "extra");
    }
    
    @Test
    public void positional_args_02() {
        SingleCommand<ArgsPositional> parser = singleCommandParser(ArgsPositional.class);
        ArgsPositional cmd = parser.parse("example.txt", "600", "extra", "other", "another");
        
        assertEquals(cmd.file, "example.txt");
        assertEquals(cmd.mode, new Integer(600));
        assertEquals(cmd.parameters.size(), 3);
        assertEquals(cmd.parameters.get(0), "extra");
        assertEquals(cmd.parameters.get(1), "other");
        assertEquals(cmd.parameters.get(2), "another");
    }
    
    @Test
    public void positional_args_03() {
        SingleCommand<ArgsPositionalDuplicate> parser = singleCommandParser(ArgsPositionalDuplicate.class);
        assertFalse(parser.getCommandMetadata().getPositionalArguments().isEmpty());
        assertEquals(parser.getCommandMetadata().getPositionalArguments().size(), 2);

        ArgsPositionalDuplicate cmd = parser.parse("example.txt", "600", "extra");
        assertEquals(cmd.file, "example.txt");
        assertEquals(cmd.otherFile, "example.txt");
        assertEquals(cmd.file, cmd.otherFile);
        assertEquals(cmd.mode, new Integer(600));
        assertEquals(cmd.parameters.size(), 1);
        assertEquals(cmd.parameters.get(0), "extra");
    }
    
    @Test
    public void positional_args_04() {
        SingleCommand<ArgsPositionalOverride> parser = singleCommandParser(ArgsPositionalOverride.class);
        assertFalse(parser.getCommandMetadata().getPositionalArguments().isEmpty());
        assertEquals(parser.getCommandMetadata().getPositionalArguments().size(), 2);

        PositionalArgumentMetadata posArg = parser.getCommandMetadata().getPositionalArguments().get(0);
        assertEquals(posArg.getZeroBasedPosition(), 0);
        assertEquals(posArg.getOneBasedPosition(), 1);
        assertEquals(posArg.getJavaType(), String.class);
        assertEquals(posArg.getTitle(), "YourFile");
        assertFalse(posArg.isRequired());

        posArg = parser.getCommandMetadata().getPositionalArguments().get(1);
        assertEquals(posArg.getZeroBasedPosition(), 1);
        assertEquals(posArg.getOneBasedPosition(), 2);
        assertEquals(posArg.getJavaType(), Integer.class);
        assertEquals(posArg.getTitle(), "Mode");

        ArgsPositional cmd = parser.parse("example.txt", "600", "extra");
        assertEquals(cmd.file, "example.txt");
        assertEquals(cmd.mode, new Integer(600));
        assertEquals(cmd.parameters.size(), 1);
        assertEquals(cmd.parameters.get(0), "extra");
    }
    
    @Test
    public void positional_args_05() {
        SingleCommand<ArgsPositionalNoExtras> parser = singleCommandParser(ArgsPositionalNoExtras.class);
        assertFalse(parser.getCommandMetadata().getPositionalArguments().isEmpty());
        assertEquals(parser.getCommandMetadata().getPositionalArguments().size(), 2);

        ArgsPositionalNoExtras cmd = parser.parse("example.txt", "600");
        assertEquals(cmd.file, "example.txt");
        assertEquals(cmd.mode, new Integer(600));
    }
    
    @Test(expectedExceptions = ParseArgumentsUnexpectedException.class)
    public void positional_args_06() {
        SingleCommand<ArgsPositionalNoExtras> parser = singleCommandParser(ArgsPositionalNoExtras.class);
        assertFalse(parser.getCommandMetadata().getPositionalArguments().isEmpty());
        assertEquals(parser.getCommandMetadata().getPositionalArguments().size(), 2);

        ArgsPositionalNoExtras cmd = parser.parse("example.txt", "600", "extra");
        assertEquals(cmd.file, "example.txt");
        assertEquals(cmd.mode, new Integer(600));
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void positional_args_gap_01() {
        singleCommandParser(ArgsPositionalGap.class);
    }
    
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void positional_args_conflict_01() {
        singleCommandParser(ArgsPositionalConflict.class);
    }
    
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void positional_args_conflcit_02() {
        singleCommandParser(ArgsPositionalOverrideChild.class);
    }
    
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void positional_args_required_01() {
        singleCommandParser(ArgsPositionalRequiredAfterOptional.class);
    }
    
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void positional_args_required_02() {
        singleCommandParser(ArgsPositionalRequiredAfterOptional2.class);
    }
    
    @Test
    public void positional_args_help_01() throws IOException {
        SingleCommand<ArgsPositional> parser = singleCommandParser(ArgsPositional.class);
        
        CliCommandUsageGenerator generator = new CliCommandUsageGenerator();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        generator.usage(parser.getCommandMetadata(), parser.getParserConfiguration(), output);
        
        String actual = output.toString(StandardCharsets.UTF_8.name());
        
        assertTrue(actual.contains("ArgsPositional [ -- ] <File> [ <Mode> ] [ <ExtraArg>... ]"));
    }
}
