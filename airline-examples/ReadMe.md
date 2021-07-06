# Airline Examples

This module provides a variety of examples of how to build different kinds of command lines using airline.

## Running examples

Assuming you have built the code and are on a Linux like system you can use the provided `runExample` script e.g.

```
> runExample Simple
```
Optionally passing in any arguments e.g.

```
> runExample Simple foo bar
```
Or with Java 9+ you can run using JPMS:

```
> moduleExample Simple foo bar
```

### Running directly via Java

Assuming you have built the code:

    > java -cp "target/modules/*:target/airline-examples-<version>.jar" com.github.rvesse.airline.examples.simple.Simple --help
    
Or try running with some arguments:

    > java -cp "target/modules/*:target/airline-examples-<version>.jar" com.github.rvesse.airline.examples.simple.Simple foo bar

Or with Java 9+ you can run using JPMS:

   > java --module-path "target/modules:target/airline-examples-<version>.jar" -m com.github.rvesse.airline.examples/com.github.rvesse.airline.examples.simple.Simple foo bar

Please browse the code to see all the examples available.

