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

import com.github.rvesse.airline.parser.errors.ParseArgumentsMissingException;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.rvesse.airline.SingleCommand;
import com.github.rvesse.airline.TestingUtil;
import com.github.rvesse.airline.parser.errors.ParseOptionGroupException;
import com.github.rvesse.airline.parser.errors.ParseOptionMissingException;


public class TestRequired {

    @Test
    public void require_some_good() {
        SingleCommand<Some> parser = TestingUtil.singleCommandParser(Some.class);
        parser.parse("-a", "test");
        parser.parse("-b", "test");
        parser.parse("-c", "test");
        parser.parse("-a", "test", "-b", "test2");
        parser.parse("-a", "test", "-a", "test2");
        parser.parse("-a", "test", "-c", "test2");
        parser.parse("-b", "test", "-c", "test2");
        parser.parse("-a", "test", "-b", "test2", "-c", "test3");
        parser.parse("-a", "test", "-b", "test2", "-c", "test3", "-a", "test4", "-c", "test5");
    }

    @Test(expectedExceptions = ParseOptionGroupException.class)
    public void require_some_bad_none() {
        SingleCommand<Some> parser = TestingUtil.singleCommandParser(Some.class);
        parser.parse();
    }

    @Test
    public void require_one_good() {
        SingleCommand<One> parser = TestingUtil.singleCommandParser(One.class);
        parser.parse("-a", "test");
        parser.parse("-b", "test");
        parser.parse("-c", "test");
        parser.parse("-a", "test", "-a", "test2");
        parser.parse("-b", "test", "-b", "test2");
        parser.parse("-c", "test", "-c", "test2", "-c", "test3", "-c", "test4", "-c", "test5");
    }

    @Test(expectedExceptions = ParseOptionGroupException.class)
    public void require_one_bad_none() {
        SingleCommand<One> parser = TestingUtil.singleCommandParser(One.class);
        parser.parse();
    }

    @Test(expectedExceptions = ParseOptionGroupException.class)
    public void require_one_bad_multiple_01() {
        SingleCommand<One> parser = TestingUtil.singleCommandParser(One.class);
        parser.parse("-a", "test", "-b", "test2");
    }


    @Test(expectedExceptions = ParseOptionGroupException.class)
    public void require_one_bad_multiple_02() {
        SingleCommand<One> parser = TestingUtil.singleCommandParser(One.class);
        parser.parse("-a", "test", "-c", "test2");
    }


    @Test(expectedExceptions = ParseOptionGroupException.class)
    public void require_one_bad_multiple_03() {
        SingleCommand<One> parser = TestingUtil.singleCommandParser(One.class);
        parser.parse("-b", "test", "-c", "test2");
    }

    @Test
    public void required_if_good_unneeded_missing() {
        SingleCommand<If> parser = TestingUtil.singleCommandParser(If.class);
        parser.parse();
    }

    @Test
    public void required_if_good_unneeded_present() {
        SingleCommand<If> parser = TestingUtil.singleCommandParser(If.class);
        parser.parse("-b", "test");
    }

    @Test
    public void required_if_good_needed() {
        SingleCommand<If> parser = TestingUtil.singleCommandParser(If.class);
        parser.parse("-a", "test", "-b", "test2");
        parser.parse("--alpha", "test", "-b", "test2");
        parser.parse("-a", "test", "--bravo", "test2");
        parser.parse("--alpha", "test", "--bravo", "test2");
    }

    @Test(expectedExceptions = ParseOptionMissingException.class)
    public void required_if_bad_needed() {
        SingleCommand<If> parser = TestingUtil.singleCommandParser(If.class);
        parser.parse("-a", "test");
    }

    @Test
    public void mutually_exclusive_with_good() {
        SingleCommand<OptionallyOne> parser = TestingUtil.singleCommandParser(OptionallyOne.class);
        parser.parse("-a", "test");
        parser.parse("-b", "test");
        parser.parse("-c", "test");
        parser.parse("-a", "test", "-a", "test2");
        parser.parse("-b", "test", "-b", "test2");
        parser.parse("-c", "test", "-c", "test2", "-c", "test3", "-c", "test4", "-c", "test5");
    }

    @Test
    public void mutually_exclusive_with_none() {
        SingleCommand<OptionallyOne> parser = TestingUtil.singleCommandParser(OptionallyOne.class);
        parser.parse();
    }

    @Test(expectedExceptions = ParseOptionGroupException.class)
    public void mutually_exclusive_with_multiple_01() {
        SingleCommand<OptionallyOne> parser = TestingUtil.singleCommandParser(OptionallyOne.class);
        parser.parse("-a", "test", "-b", "test2");
    }

    @Test(expectedExceptions = ParseOptionGroupException.class)
    public void mutually_exclusive_with_multiple_02() {
        SingleCommand<OptionallyOne> parser = TestingUtil.singleCommandParser(OptionallyOne.class);
        parser.parse("-a", "test", "-c", "test2");
    }

    @Test(expectedExceptions = ParseOptionGroupException.class)
    public void mutually_exclusive_with_multiple_03() {
        SingleCommand<OptionallyOne> parser = TestingUtil.singleCommandParser(OptionallyOne.class);
        parser.parse("-b", "test", "-c", "test2");
    }

    @Test
    public void required_unless_env_01() {
        SingleCommand<Unless> parser = TestingUtil.singleCommandParser(Unless.class);
        Unless cmd = parser.parse("--path", "/bin", "--foo", "test", "some-arg");
        Assert.assertEquals(cmd.path, "/bin");
        Assert.assertEquals(cmd.foo, "test");
        Assert.assertNotNull(cmd.args);
        Assert.assertEquals(cmd.args.size(), 1);
        Assert.assertEquals(cmd.args.get(0), "some-arg");
    }

    @Test
    public void required_unless_env_02() {
        SingleCommand<Unless> parser = TestingUtil.singleCommandParser(Unless.class);
        Unless cmd = parser.parse("--foo", "test", "some-arg");
        Assert.assertEquals(cmd.path, System.getenv("PATH"));
        Assert.assertEquals(cmd.foo, "test");
        Assert.assertNotNull(cmd.args);
        Assert.assertEquals(cmd.args.size(), 1);
        Assert.assertEquals(cmd.args.get(0), "some-arg");
    }

    @Test(expectedExceptions = ParseOptionMissingException.class, expectedExceptionsMessageRegExp = "Required option.*is missing.*no default values.*FOO, FOO_BAR")
    public void required_unless_env_03() {
        SingleCommand<Unless> parser = TestingUtil.singleCommandParser(Unless.class);
        parser.parse("some-arg");
    }

    @Test(expectedExceptions = ParseArgumentsMissingException.class,expectedExceptionsMessageRegExp = "Required arguments.*are missing.*no default values.*ARGS, ARGUMENTS")
    public void required_unless_env_04() {
        SingleCommand<Unless> parser = TestingUtil.singleCommandParser(Unless.class);
        parser.parse("--foo", "test");
    }
}
