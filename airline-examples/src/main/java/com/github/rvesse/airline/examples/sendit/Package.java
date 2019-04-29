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

import com.github.rvesse.airline.annotations.Option;
import com.github.rvesse.airline.annotations.restrictions.Required;
import com.github.rvesse.airline.annotations.restrictions.RequiredOnlyIf;
import com.github.rvesse.airline.annotations.restrictions.ranges.DoubleRange;

public class Package {

    @Option(name = { "-w", "--weight" }, title = "WeightInKg", description = "Specifies the packages weight in kilograms")
    @Required
    @DoubleRange(min = 0.1, max = 20.0)
    public Double weight;
    
    @Option(name = "--width", title = "Width", description = "Specifies the packages width in millimeteres, if one dimension is specified all must be specified")
    @RequiredOnlyIf(names = { "--height", "--depth" })
    public Integer width;
    
    @Option(name = "--height", title = "Height", description = "Specifies the packages height in millimeteres, if one dimension is specified all must be specified")
    @RequiredOnlyIf(names = { "--width", "--depth" })
    public Integer height;
    
    @Option(name = "--depth", title = "Depth", description = "Specifies the packages depth in millimeteres, if one dimension is specified all must be specified")
    @RequiredOnlyIf(names = { "--width", "--height"})
    public Integer depth;
    
    public Integer calculateVolume() {
        if (this.width == null || this.height == null || this.depth == null) {
            return null;
        } else {
            return this.width * this.height * this.depth;
        }
    }
}
