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
package com.github.rvesse.airline.tests.restrictions;

import org.testng.annotations.Test;

import com.github.rvesse.airline.SingleCommand;
import com.github.rvesse.airline.tests.TestingUtil;
import com.github.rvesse.airline.parser.errors.ParseRestrictionViolatedException;

public class TestOccurrences {

    @Test
    public void occurrences_good() {
        SingleCommand<Occurrences> parser = TestingUtil.singleCommandParser(Occurrences.class);
        parser.parse("-a", "a1", "-b", "b1", "-b", "b2");
        parser.parse("-a", "a1", "-a", "a2", "-a", "a3", "-b", "b1", "-b", "b2", "-c", "c1");
    }
    
    @Test(expectedExceptions = ParseRestrictionViolatedException.class, expectedExceptionsMessageRegExp = ".*(maximum of 3).*(found 4).*")
    public void occurrences_bad_too_many_01() {
        SingleCommand<Occurrences> parser = TestingUtil.singleCommandParser(Occurrences.class);
        parser.parse("-a", "a1", "-a", "a2", "-a", "a3", "-a", "a4");
    }
    
    @Test(expectedExceptions = ParseRestrictionViolatedException.class, expectedExceptionsMessageRegExp = ".*(maximum of 1).*(found 2).*")
    public void occurrences_bad_too_many_02() {
        SingleCommand<Occurrences> parser = TestingUtil.singleCommandParser(Occurrences.class);
        parser.parse("-b", "b1", "-b", "b2", "-c", "c1", "-c", "c2");
    }
    
    @Test(expectedExceptions = ParseRestrictionViolatedException.class, expectedExceptionsMessageRegExp = ".*(at least 2).*(found 0).*")
    public void occurrences_bad_too_few_01() {
        SingleCommand<Occurrences> parser = TestingUtil.singleCommandParser(Occurrences.class);
        parser.parse();
    }
    
    @Test(expectedExceptions = ParseRestrictionViolatedException.class, expectedExceptionsMessageRegExp = ".*(at least 2).*(found 1).*")
    public void occurrences_bad_too_few_02() {
        SingleCommand<Occurrences> parser = TestingUtil.singleCommandParser(Occurrences.class);
        parser.parse("-b", "b1");
    }
}
