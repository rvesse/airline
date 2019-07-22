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
package com.github.rvesse.airline.restrictions;

import java.util.concurrent.TimeUnit;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.rvesse.airline.SingleCommand;
import com.github.rvesse.airline.parser.errors.ParseOptionIllegalValueException;

public class TestAllowedValues {

    @Test
    public void allowed_raw_01() {
        String[] allowed = new String[] { "foo", "bar", "faz" };
        
        SingleCommand<Allowable> parser = SingleCommand.singleCommand(Allowable.class);
        for (String value : allowed) {
            Allowable cmd = parser.parse("--raw", value);
            Assert.assertEquals(cmd.raw, value);
        }
    }
        
    @Test(expectedExceptions = ParseOptionIllegalValueException.class)
    public void allowed_raw_02() {
        SingleCommand<Allowable> parser = SingleCommand.singleCommand(Allowable.class);
        parser.parse("--raw", "other");
    }
    
    @Test
    public void allowed_raw_03() {
        String[] allowed = new String[] { "upper", "LOWER", "mIXEDcASE", "UPPER", "UPPer", "loweR", "mIXeDcAsE" };
        
        SingleCommand<Allowable> parser = SingleCommand.singleCommand(Allowable.class);
        for (String value : allowed) {
            Allowable cmd = parser.parse("--case", value);
            Assert.assertEquals(cmd.caseInsensitive, value);
        }
    }
    
    @Test
    public void allowed_typed_01() {
        String[] allowed = new String[] { "1", "2.0", "0.3E1" };
        
        SingleCommand<Allowable> parser = SingleCommand.singleCommand(Allowable.class);
        for (String value : allowed) {
            Allowable cmd = parser.parse("--typed", value);
            Assert.assertEquals(cmd.typed, Double.parseDouble(value));
        }
    }
    
    @Test
    public void allowed_enum_01() {
        SingleCommand<Allowable> parser = SingleCommand.singleCommand(Allowable.class);
        for (TimeUnit unit : TimeUnit.values()) {
            Allowable cmd = parser.parse("--enum", unit.name());
            Assert.assertEquals(cmd.enumTyped, unit);
        }
    }
}
