```java
package com.github.rvesse.airline.examples.userguide;

import java.util.List;

import com.github.rvesse.airline.SingleCommand;
import com.github.rvesse.airline.annotations.Arguments;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.Option;

@Command(name = "getting-started", description = "We're just getting started")
public class GettingStarted {

    @Option(name = { "-f", "--flag" }, description = "An option that requires no values")
    private boolean flag = false;

    @Arguments(description = "Additional arguments")
    private List<String> args;

    public static void main(String[] args) {
        SingleCommand<GettingStarted> parser = SingleCommand.singleCommand(GettingStarted.class);
        GettingStarted cmd = parser.parse(args);
        cmd.run();
    }

    private void run() {
        System.out.println("Flag was " + (this.flag ? "set" : "not set"));
        if (args != null)
            System.out.println("Arguments were " + String.join(",", args));
    }
}
```