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

import com.github.rvesse.airline.annotations.Option;
import com.github.rvesse.airline.annotations.restrictions.MinOccurrences;
import com.github.rvesse.airline.annotations.restrictions.NotBlank;
import com.github.rvesse.airline.annotations.restrictions.Pattern;
import com.github.rvesse.airline.annotations.restrictions.RequireOnlyOne;
import com.github.rvesse.airline.annotations.restrictions.Required;
import com.github.rvesse.airline.annotations.restrictions.ranges.IntegerRange;

/**
 * Represents a UK postal address
 * @author rvesse
 *
 */
public class PostalAddress {
    
    @Option(name = "--recipient", title = "Recipient", description = "Specifies the name of the receipient")
    @Required
    public String recipient;

    @Option(name = "--number", title = "HouseNumber", description = "Specifies the house number")
    @RequireOnlyOne(tag = "nameOrNumber")
    @IntegerRange(min = 0, minInclusive = false)
    public Integer houseNumber;
    
    @Option(name = "--name", title = "HouseName", description = "Specifies the house name")
    @RequireOnlyOne(tag = "nameOrNumber")
    @NotBlank
    public String houseName;
    
    @Option(name = { "-a", "--address", "--line" }, title = "AddressLine", description = "Specifies an address line.  Specify this multiple times to provide multiple address lines, these should be in the order they should be used.")
    @Required
    @MinOccurrences(occurrences = 1)
    public List<String> addressLines = new ArrayList<>();
    
    @Option(name = "--postcode", title = "PostCode", description = "Specifies the postcode")
    @Required
    @Pattern(pattern = "^([A-Z]{1,2}([0-9]{1,2}|[0-9][A-Z])) (\\d[A-Z]{2})$", description = "Must be a valid UK postcode.", flags = java.util.regex.Pattern.CASE_INSENSITIVE)
    public String postCode;
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.recipient);
        builder.append('\n');
        if (this.houseNumber != null) {
            builder.append(Integer.toString(this.houseNumber));
            builder.append(' ');
        } else {
            builder.append(this.houseName);
            builder.append('\n');
        }
        
        for (String line : this.addressLines) {
            builder.append(line);
            builder.append('\n');
        }
        builder.append(this.postCode);
        
        return builder.toString();
    }
}
