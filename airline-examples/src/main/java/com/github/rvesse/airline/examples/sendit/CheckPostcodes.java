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

package com.github.rvesse.airline.examples.sendit;

import java.util.ArrayList;
import java.util.List;

import com.github.rvesse.airline.annotations.Arguments;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.restrictions.MinOccurrences;
import com.github.rvesse.airline.annotations.restrictions.Pattern;
import com.github.rvesse.airline.annotations.restrictions.Required;
import com.github.rvesse.airline.examples.ExampleRunnable;

@Command(name = "check-postcodes", description = "Checks whether postcodes are valid")
public class CheckPostcodes implements ExampleRunnable {

    @Arguments(title = "PostCode", description = "Specifies one/more postcodes to validate")
    @Required
    @MinOccurrences(occurrences = 1)
    @Pattern(pattern = "^([A-Z]{1,2}([0-9]{1,2}|[0-9][A-Z])) (\\d[A-Z]{2})$", description = "Must be a valid UK postcode.", flags = java.util.regex.Pattern.CASE_INSENSITIVE)
    public List<String> postCodes = new ArrayList<>();

    @Override
    public int run() {
        for (String postCode : postCodes) {
            // TODO In a real application we'd probably check the postcodes
            // against a database of valid postcodes
            System.out.format("%s is a valid postcode\n", postCode);
        }
        return 0;
    }

}
