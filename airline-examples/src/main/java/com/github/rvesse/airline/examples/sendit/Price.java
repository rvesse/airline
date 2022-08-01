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

import com.github.rvesse.airline.annotations.AirlineModule;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.Option;
import com.github.rvesse.airline.examples.ExampleRunnable;

@Command(name = "price", description = "Calculates the price for a parcel")
public class Price implements ExampleRunnable {

    @Option(name = { "-s",
            "--service" }, title = "Service", description = "Specifies the postal service you would like to use")
    private PostalService service = PostalService.FirstClass;

    @AirlineModule
    private Package item = new Package();

    @Override
    public int run() {
        System.out.println(String.format("Your package weighing %.3f KG sent via %s will cost £%.2f", this.item.weight,
                this.service.toString(), this.service.calculateCost(this.item.weight)));

        return 0;
    }

}
