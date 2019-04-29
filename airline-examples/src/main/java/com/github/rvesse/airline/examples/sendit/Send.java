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

import javax.inject.Inject;

import com.github.rvesse.airline.SingleCommand;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.Option;
import com.github.rvesse.airline.examples.ExampleRunnable;
import com.github.rvesse.airline.parser.errors.ParseException;

@Command(name = "send", description = "Sends a package")
public class Send implements ExampleRunnable {

    @Inject
    private PostalAddress address = new PostalAddress();

    @Inject
    private Package item = new Package();

    @Option(name = { "-s",
            "--service" }, title = "Service", description = "Specifies the postal service you would like to use")
    private PostalService service = PostalService.FirstClass;

    @Override
    public int run() {
        // TODO: In a real world app actual business logic would go here...

        System.out.println(String.format("Sending package weighing %.3f KG sent via %s costing £%.2f", this.item.weight,
                this.service.toString(), this.service.calculateCost(this.item.weight)));
        System.out.println("Recipient:");
        System.out.println();
        System.out.println(this.address.toString());
        System.out.println();

        return 0;
    }

    public static void main(String[] args) {
        SingleCommand<Send> parser = SingleCommand.singleCommand(Send.class);
        try {
            Send cmd = parser.parse(args);
            System.exit(cmd.run());
        } catch (ParseException e) {
            System.err.print(e.getMessage());
            System.exit(1);
        }
    }
}
