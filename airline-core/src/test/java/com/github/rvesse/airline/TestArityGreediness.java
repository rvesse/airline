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

import static com.github.rvesse.airline.SingleCommand.singleCommand;
import static org.testng.Assert.assertEquals;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.rvesse.airline.args.ArgsArityGreediness;
import com.github.rvesse.airline.builder.ParserBuilder;
import com.github.rvesse.airline.model.ParserMetadata;
import com.github.rvesse.airline.parser.errors.ParseOptionMissingValueException;
import com.github.rvesse.airline.parser.options.ClassicGetOptParser;
import com.github.rvesse.airline.parser.options.ListValueOptionParser;
import com.github.rvesse.airline.parser.options.MaybeListValueOptionParser;
import com.github.rvesse.airline.parser.options.OptionParser;
import com.github.rvesse.airline.parser.options.StandardOptionParser;

public class TestArityGreediness {

    @Test(expectedExceptions = ParseOptionMissingValueException.class)
    public void arity_greediness_01() {
        singleCommand(ArgsArityGreediness.class).parse("--help", "--debug", "--open");
    }

    @Test(expectedExceptions = ParseOptionMissingValueException.class)
    public void arity_greediness_02() {
        ArgsArityGreediness args = singleCommand(ArgsArityGreediness.class).parse("--help", "--open", "--debug");
        Assert.assertNotEquals(args.open, "--debug", "Parser erroneously consumed --debug option as value for --open");
    }

    private <T> ParserMetadata<T> useSpecificOptionParser(OptionParser<T> optionParser) {
        ParserBuilder<T> builder = new ParserBuilder<T>().withOptionParser(optionParser);
        ParserMetadata<T> parser = builder.build();
        assertEquals(parser.getOptionParsers().size(), 1);
        assertEquals(parser.getOptionParsers().get(0), optionParser);
        return parser;
    }

    @Test(expectedExceptions = ParseOptionMissingValueException.class)
    public void arity_greediness_03() {
        singleCommand(ArgsArityGreediness.class,
                useSpecificOptionParser(new ClassicGetOptParser<ArgsArityGreediness>())).parse("-hdo");
    }

    @Test(expectedExceptions = ParseOptionMissingValueException.class)
    public void arity_greediness_04() {
        ArgsArityGreediness args = singleCommand(ArgsArityGreediness.class,
                useSpecificOptionParser(new ClassicGetOptParser<ArgsArityGreediness>())).parse("-h", "-o", "-d");
        Assert.assertNotEquals(args.open, "--debug", "Parser erroneously consumed --debug option as value for --open");
    }
    
    @Test(expectedExceptions = ParseOptionMissingValueException.class)
    public void arity_greediness_05() {
        singleCommand(ArgsArityGreediness.class,
                useSpecificOptionParser(new ClassicGetOptParser<ArgsArityGreediness>())).parse("-h", "-d", "-o");
    }

    @Test(expectedExceptions = ParseOptionMissingValueException.class)
    public void arity_greediness_06() {
        ArgsArityGreediness args = singleCommand(ArgsArityGreediness.class,
                useSpecificOptionParser(new ClassicGetOptParser<ArgsArityGreediness>())).parse("-hod");
        Assert.assertNotEquals(args.open, "--debug", "Parser erroneously consumed --debug option as value for --open");
    }

    @Test(expectedExceptions = ParseOptionMissingValueException.class)
    public void arity_greediness_07() {
        singleCommand(ArgsArityGreediness.class,
                useSpecificOptionParser(new StandardOptionParser<ArgsArityGreediness>())).parse("--help", "--debug",
                        "--open");
    }

    @Test(expectedExceptions = ParseOptionMissingValueException.class)
    public void arity_greediness_08() {
        ArgsArityGreediness args = singleCommand(ArgsArityGreediness.class,
                useSpecificOptionParser(new StandardOptionParser<ArgsArityGreediness>())).parse("--help", "--open",
                        "--debug");
        Assert.assertNotEquals(args.open, "--debug", "Parser erroneously consumed --debug option as value for --open");
    }
    
    @Test(expectedExceptions = ParseOptionMissingValueException.class)
    public void arity_greediness_09() {
        singleCommand(ArgsArityGreediness.class,
                useSpecificOptionParser(new ListValueOptionParser<ArgsArityGreediness>())).parse("--help", "--debug",
                        "--open");
    }

    @Test(expectedExceptions = ParseOptionMissingValueException.class)
    public void arity_greediness_10() {
        ArgsArityGreediness args = singleCommand(ArgsArityGreediness.class,
                useSpecificOptionParser(new ListValueOptionParser<ArgsArityGreediness>())).parse("--help", "--open",
                        "--debug");
        Assert.assertNotEquals(args.open, "--debug", "Parser erroneously consumed --debug option as value for --open");
    }
    
    @Test(expectedExceptions = ParseOptionMissingValueException.class)
    public void arity_greediness_11() {
        singleCommand(ArgsArityGreediness.class,
                useSpecificOptionParser(new MaybeListValueOptionParser<ArgsArityGreediness>())).parse("--help", "--debug",
                        "--open");
    }

    @Test(expectedExceptions = ParseOptionMissingValueException.class)
    public void arity_greediness_12() {
        ArgsArityGreediness args = singleCommand(ArgsArityGreediness.class,
                useSpecificOptionParser(new MaybeListValueOptionParser<ArgsArityGreediness>())).parse("--help", "--open",
                        "--debug");
        Assert.assertNotEquals(args.open, "--debug", "Parser erroneously consumed --debug option as value for --open");
    }
    
}
