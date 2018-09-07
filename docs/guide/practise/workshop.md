---
layout: slideshow
title: Workshop - Building a Killer Command Line App with Airline
---

This workshop session is designed to give you a complete introduction to the core features of Airline for creating powerful CLIs.  This was originally written for the [Tech Exeter Conference](https://techexeter.uk) series and presented in September 2018.

The workshop is provided as a HTML slideshow embedded below, use the arrow keys to navigate through the slides.  You can view this in fullscreen by hitting the icon in the top left of the slides.

---

{% include slideshow-start.md name="workshop" %}

{% include slide-start.md %}

<div class="bss-title">
  <h1>Building a Killer Command Line App with Airline</h1>
  <h3>Rob Vesse</h3>
  <h4><span>rvesse</span>@<span>cray</span><span>.com</span></h4>
</div>

{% include slide-end.md %}
{% include slide-start.md %}

## Introduction

Everyone builds command line applications at some point but often they are cobbled together or full of needless boiler plate. Airline takes a fully declarative approach to command line applications allowing users to create powerful and flexible command lines.

Airline takes care of lots of heavy lifting providing many features not found in similar libraries including annotation driven value validation [restrictions](../restrictions/index.html), generating [Man pages](../help/man.html) and [Bash completion scripts](../help/bash.html) to name just a few.

In the workshop session we'll work through an example command line application to see just how powerful this can be.

### About Me

* Software Engineer at Cray in the AI & Analytics Group
* Long background in open source
    * Have been releasing open source software since around 2003
    * Involved at the [Apache Software Foundation](https://www.apache.org) (ASF) since 2012 and was elected as a  member in 2015
    * [https://github.com/rvesse]()

{% include slide-end.md %}
{% include slide-start.md %}

## Pre-requisites

In order to follow along with this workshop we assume the following knowledge and tools:

1. Understanding of the Java programming language
2. JDK 7, 8, 9 or 10 available
3. [`git`](https://git-scm.org) installed
4. [`mvn`](https://maven.apache.org) installed

**NB** 2-4 will allow you to run the examples shown in the slides but aren't essential.

A Linux/Mac laptop is preferred but you should be able to use Windows for most things.

You can find these slides at [{{ site.url }}{{ site.baseurl }}/guide/practise/workshop.html]()

{% include slide-end.md %}
{% include slide-start.md %}

### History

- Airline started out as an open source project on GitHub back in January 2012.
- I first encountered this library in use in one of our competitors products partway through that year.  
- I quickly started using it in my own work but encountered a few limitations.
- The original authors were not receptive to pull requests so I forked the code and started maintaining my own version that has since evolved considerably.
- First release of my fork was December 2014
- Latest release at time of writing was 2.6.0
- Have built various personal and work projects with it e.g.
    - [SPARQL Query Benchmarker](https://github.com/rvesse/spark-query-bm)
    - [GitHub PR Stats](https://github.com/rvesse/gh-pr-stats)
    - [Cray Graph Engine CLI](https://pubs.cray.com/content/S-3014/3.2.UP01/cray-graph-engine-user-guide/cge-cli)

{% include slide-end.md %}
{% include slide-start.md %}

### Design Philosophy

1. Be Declarative **Not** Imperative
2. Avoid boiler plate code
3. Allow deep customisation

#### Be Declarative **Not** Imperative

Firstly we want to define our command lines using declarative annotations.

This allows us to separate the command line definition cleanly from the runtime logic.

It also enables us to do optional build time checking of our definitions to ensure valid command line apps.

#### Avoid boiler plate code

Secondly we look to avoid the typical boiler plate code associated with many command line libraries.

You **shouldn't** need to write a ton of `if` statements to check that values for options fall in specified ranges or meet common application constraints.

#### Allow deep customisation

Finally we don't want to tie you into a particular implementation approach.

We provide extensibility of almost every aspect of the parsing process yet provide a general purpose default setup that should suit many users.

So a basic CLI should just work, advanced CLIs can be configured as desired

{% include slide-end.md %}
{% include slide-start.md %}

## Workshop Overview

Due to time constraints this will be more of an interactive demo, almost everything in these slides is in the GitHub repo so you can play along.

For this workshop we are going to build an example command line application called `send-it` for shipping of packages.  The example code in these slides is typically truncated to omit things like import declarations for brevity, the full code is linked alongside each example.

The example code all lives inside the Airline git repository at [https://github.com/rvesse/airline/tree/master/airline-examples](https://github.com/rvesse/airline/tree/master/airline-examples)

{% include slide-end.md %}
{% include slide-start.md %}

### Following Along with the Examples

We use `>` to indicate that a command should be run at a command prompt and `<input>` within that to indicate some input is needed.

To follow along you should start by checking out the code and building the examples:

```
> git clone https://github.com/rvesse/airline.git
> cd airline
> mvn package
```

#### Running an Example

Many of the examples are runnable using the `runExample` script in the `airline-examples` sub-directory e.g.

```
> cd airline-examples
> ./runExample SendIt <options>
```
Or for this specific workshop the `send-it` script in that same sub-directory can be used:

```
> ./send-it <options>
```

Or for Windows Users:

```
> send-it.bat <options>
```

{% include slide-end.md %}
{% include slide-start.md %}

## Step 1 - Define Options

Airline works with POJOs (Plain Old Java Objects) so firstly we need to define some classes that are going to hold our commands options.

We can define our options across multiple classes and our inheritance hierarchy i.e. you can create a `BaseCommand` with your common options.

Or you can define options in standalone classes and compose them together.

We're going to see the latter approach in this workshop, see [Inheritance and Composition](oop.html) for more detail on the former approach.

{% include slide-end.md %}
{% include slide-start.md %}

### `@Option`

The [`@Option`](../annotations/option.html) annotation is used to mark a field as being populated by an option.  At a minimum it needs to define the `name` field to provide one/more names that your users will enter to refer to your option e.g.

```java
@Option(name = { "-e", "--example" })
private String example;
```
Here we define a simple `String` field and annotate it with `@Option` providing two possible names - `-e` and `--example` - by which users can refer to it.

Other commonly used fields in the `@Option` annotation include `title` used to specify the title by which the value is referred to in help and `description` used to provide descriptive help information for the option.

{% include slide-end.md %}
{% include slide-start.md %}

#### `PostalAddress` example

Let's take a look at the {% include github-ref.md package="example.sendit" class="PostalAddress" module="airline-examples" %} class which defines options for specifying a UK postal address.  Explanatory text is interspersed into the example:

```java
public class PostalAddress {
    
    @Option(name = "--recipient", title = "Recipient", 
            description = "Specifies the name of the receipient")
    @Required
    public String recipient;
```
So we start with a fairly simply definition, this defines a `--recipient` option and states that it is a required option via the [`@Required`](../annoations/required.html) annotation.

{% include slide-end.md %}
{% include slide-start.md %}

#### Defining Related Options

```java
    @Option(name = "--number", title = "HouseNumber", 
                   description = "Specifies the house number")
    @RequireOnlyOne(tag = "nameOrNumber")
    @IntegerRange(min = 0, minInclusive = false)
    public Integer houseNumber;
    
    @Option(name = "--name", title = "HouseName", 
                   description = "Specifies the house name")
    @RequireOnlyOne(tag = "nameOrNumber")
    @NotBlank
    public String houseName;
```
Now we're starting to get more advanced, here we have two closely related options - `--number` and `--name` - which we declare that we require only one of via the [`@RequireOnlyOne`](../annotations/require-only-one.html) i.e. we've told Airline that one, and only one, of these two options may be specified.

Additionally for the `--number` option we state that it must be greater than zero via the [`@IntegerRange`](../annotations/integer-range.html) annotation and for the `--name` option we state that it must be [`@NotBlank`](../annotations/not-blank.html) i.e. it must have a non-empty value that is not all whitespace.

{% include slide-end.md %}
{% include slide-start.md %}

#### Defining a Repeated Option

Here we have an option that may be specified multiple times to provide multiple address lines.  **Importantly** we need to define it with an appropriate `Collection` based type, in this case `List<String>` in order to collect all the address lines specified.

```java
    @Option(name = { "-a", "--address", "--line" }, title = "AddressLine", 
            description = "Specifies an address line.  Specify this multiple times to provide multiple address lines, these should be in the order they should be used.")
    @Required
    @MinOccurrences(occurrences = 1)
    public List<String> addressLines = new ArrayList<>();
```

Here we also use the [`@MinOccurences`](../annotations/min-occurrences.html) annotation to state that it must occur at least once in addition to using the previously seen `@Required`

{% include slide-end.md %}
{% include slide-start.md %}

#### Options with Complex Value Restrictions

```java
    @Option(name = "--postcode", title = "PostCode", 
                   description = "Specifies the postcode")
    @Required
    @Pattern(pattern = "^([A-Z]{1,2}([0-9]{1,2}|[0-9][A-Z])) (\\d[A-Z]{2})$", 
                    description = "Must be a valid UK postcode.", 
                    flags = java.util.regex.Pattern.CASE_INSENSITIVE)
    public String postCode;
```
Here is another example of a complex restriction, this time we use the [`@Pattern`](../annotations/pattern.html) annotation to enforce a regular expression to validate our postcodes meet the UK format.

{% include slide-end.md %}
{% include slide-start.md %}

#### Plus Regular Code...

And finally we have some regular Java code in our class.  Your normal logic can co-exist happily alongside your Airline annotations, we'll see this used later to implement our actual command logic.

```java
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
```

{% include slide-end.md %}
{% include slide-start.md %}

### `@Arguments`

The [`@Arguments`](../annotation/arguments.html) annotation is used to annotate a field that will receive arbitrary inputs i.e. anything that is not recognised as an option as defined by your `@Option` annotations.  This is useful when your command wants to operate on a list of things so is typically used in conjunction with a `Collection` typed field e.g. `List<String>`.

#### `@Arguments` in use

For example let's take a look at it in use in the {% include github-ref.md package="example.sendit" class="CheckPostcodes" module="airline-examples" %} command:

```java
    @Arguments(title = "PostCode", description = "Specifies one/more postcodes to validate")
    @Required
    @MinOccurrences(occurrences = 1)
    @Pattern(pattern = "^([A-Z]{1,2}([0-9]{1,2}|[0-9][A-Z])) (\\d[A-Z]{2})$", 
             description = "Must be a valid UK postcode.", 
             flags = java.util.regex.Pattern.CASE_INSENSITIVE)
    public List<String> postCodes = new ArrayList<>();
```

Which we can run like so:

```
> ./send-it check-postcodes "BS1 4DJ" "RG19 6HS"
BS1 4DJ is a valid postcode
RG19 6HS is a valid postcode
```
{% include slide-end.md %}
{% include slide-start.md %}

### Restrictions

So we've already seen a number of [Restrictions](../restrictions/index.html) in the above examples.  This is one of the main ways Airline reduces boiler plate and prefers declarative definitions.  There are lots more built-in restrictions than just those seen so far and you can define [Custom Restrictions](../restrictions/custom.html) if you want to encapsulate reusable restriction logic.

Some useful common restrictions include:

- [`@Required`](../annotations/required.html) - For required options/arguments
- [`@NotBlank`](../annotations/not-blank.html) - To enforce non-blank string values
- [`@AllowedRawValues`](../annotations/allowed-raw-values.html)/[`@AllowedValues`](../annotations/allowed-values.html) - To restrict options/arguments to a set of acceptable values
- [`@Path`](../annotations/path.html) - Provides restrictions on options/arguments used to refer to files and directories

{% include slide-end.md %}
{% include slide-start.md %}

## Step 2 - Define a Command

So now we've seen the basics of defining options and arguments lets use these to define a command:

```java
@Command(name = "send", description = "Sends a package")
public class Send implements ExampleRunnable {

    @Inject
    private PostalAddress address = new PostalAddress();
    
    @Inject
    private Package item = new Package();

    @Option(name = { "-s",
            "--service" }, title = "Service", description = "Specifies the postal service you would like to use")
    private PostalService service = PostalService.FirstClass;
```
{% include slide-end.md %}
{% include slide-start.md %}

### Defining a Command continued

```java
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
```

There's quite a few new concepts introduced here, so let's break them down piece by piece.

{% include slide-end.md %}
{% include slide-start.md %}

### `@Command`

The [`@Command`](../annotations/command.html) annotation is used on Java classes to state that a class is a command.  Let's see our previously introduced `PostalAddress` class combined into an actual command, here we see the {% include github-ref.md  package="examples.sendit" class="Send" module="airline-examples" %}:

```java
@Command(name = "send", description = "Sends a package")
public class Send implements ExampleRunnable {
```
The `@Command` annotation is fairly simple, we simply have a `name` for our command and a `description`.  The `name` is the name users will use to invoke the command, this name can be any string of non-whitespace characters and is the only required field of the `@Command` annotation.

The `description` field provides descriptive text about the command that will be used in help output, we'll see this used later.

{% include slide-end.md %}
{% include slide-start.md %}

### Using `@Inject` for composition

Often for command line applications you want to define reusable sets of closely related options as we already saw with the `PostalAddress` class.  Airline provides a composition mechanism that makes this easy to do.

```java
    @Inject
    private PostalAddress address = new PostalAddress();
    
    @Inject
    private Package item = new Package();
```

Here we compose the previously seen `PostalAddress` class into our command, we use the standard Java `@Inject` annotation to indicate to Airline that it should find options declared by that class.  We also have another set of options defined in a separate class, this time the {% include github-ref.md package="examples.sendit" class="Package" module="airline-examples" %} class is used to provide options relating to the package being sent.

{% include slide-end.md %}
{% include slide-start.md %}

### Command specific options

As well as composing options defined in other classes we can also define options specific to a command directly in our command class:

```java
    @Option(name = { "-s",
            "--service" }, title = "Service", description = "Specifies the postal service you would like to use")
    private PostalService service = PostalService.FirstClass;
```
 
Here the command declares an additional option `-s/--service` that is specific to this command.  Here the field actual has an enum type - {% include github-ref.md package="examples.sendit" class="PostalService" module="airline-examples" %} - which Airline happily copes with.

For more details on how Airline supports differently typed fields see the [Supported Types](types.html) documentation.

{% include slide-end.md %}
{% include slide-start.md %}
 
### Command Logic

```java
    @Override
    public int run() {
        // TODO: In a real world app actual business logic would go here...
        
        System.out.println(String.format("Sending package weighing %.3f KG sent via %s costing £%.2f", 
        				   this.item.weight, this.service.toString(), this.service.calculateCost(this.item.weight)));
        System.out.println("Recipient:");
        System.out.println();
        System.out.println(this.address.toString());
        System.out.println();

        return 0;
    }
}
```

Finally we have the actual business logic of our class.  In this example application it simply prints out some information but this serves to show that we can access the fields that have been populated by the users command line inputs.

{% include slide-end.md %}
{% include slide-start.md %}

### Invoking our command

In order to actually invoke our command we need to get a parser from Airline and invoke it on the user input.  In this example we do this in our `main(String[] args)` method:

```java
    public static void main(String[] args) {
        SingleCommand<Send> parser = SingleCommand.singleCommand(Send.class);
```
We call the static `SingleCommand.singleCommand()` method passing in the command class we want to get a parser for.

```java
        try {
            Send cmd = parser.parse(args);
```
We can then invoke the `parse()` method passing in our users inputs.

```java
            System.exit(cmd.run());
```
Assuming the parsing is successful we now have an instance of our `Send` class which we can invoke methods on like any other Java object.   In this example our business logic is in the `run()` method so we simply call that method and use its return value as the exit code.

```java
        } catch (ParseException e) {
            System.err.print(e.getMessage());
            System.exit(1);
        }
    }
```

Finally if the parsing goes wrong we print the error message and exit with a non-zero return code.

{% include slide-end.md %}
{% include slide-start.md %}

### Testing our Command

Try this out now:

```
> ./send-it send --recipient You --number 123 -a "Your Street" -a "Somewhere" --postcode "AB12 3CD" -w 0.5
Sending package weighing 0.500 KG sent via FirstClass costing £0.50
Recipient:

You
123 Your Street
Somewhere
AB12 3CD

> echo $?
0
```
{% include slide-end.md %}
{% include slide-start.md %}

## Step 3 - Define a CLI

Typically real world command line interfaces (CLIs) consist of multiple commands e.g. `git`

Airline allows multiple commands to be composed together into a CLI to support complex applications.

### `@Cli`

We use the [`@Cli`](../annotations/cli.html) annotation to define a CLI, this is applied to classes similar to `@Command` e.g. the {% include github-ref.md module="airline-examples" package="examples.sendit" class="SendItCli" %} class:

```java
@Cli(name = "send-it", 
     description = "A demonstration CLI around shipping",
     commands = {
             CheckAddress.class,
             CheckPostcodes.class,
             Send.class,
             Price.class,
             Help.class,
             BashCompletion.class
     },
     defaultCommand = Help.class, 
     parserConfiguration = @Parser(
       useDefaultOptionParsers = true,
       defaultParsersFirst = false,
       optionParsers = { ListValueOptionParser.class },
       errorHandler = CollectAll.class
     )
)
public class SendItCli {

}
```
Let's break that down a bit...

{% include slide-end.md %}
{% include slide-start.md %}

#### Name and Description

As we saw with `@Command` this is pretty self-explanatory:

```java
@Cli(name = "send-it", 
     description = "A demonstration CLI around shipping",
```
The `name` is the name that you expect users to type at the command line, typically you'll create a Shell script named this which invokes your actual Java application.

As seen previously `description` is used to provide descriptive text that will get included in help output.

{% include slide-end.md %}
{% include slide-start.md %}

#### Available Commands

```java
commands = {
             CheckAddress.class,
             CheckPostcodes.class,
             Send.class,
             Price.class,
             Help.class,
             BashCompletion.class
     },
defaultCommand = Help.class, 
```

The `commands` field of the annotation defines the classes that provide your commands.  Each of these must be appropriately annotated with `@Command`.

We also see the `defaultCommand` field used to indicate what command is invoked if the user doesn't invoke a command.  This can be useful to provide default behaviour and is often used to point to the help system.

---

**NB** - Generally it is useful for all your commands to have a common parent class or interface since as we'll see in a few slides time we'll need to declare a type when creating a parser.  In this case all our commands implement {% include github-ref.md package="examples" module="airline-examples" class="ExampleRunnable" %}

{% include slide-end.md %}
{% include slide-start.md %}

#### Parser Customisation

Here we see the parser being customised, we're going to skip over most of this for now and come back to it later:

```java
parserConfiguration = @Parser(
       useDefaultOptionParsers = true,
       defaultParsersFirst = false,
       optionParsers = { ListValueOptionParser.class },
       errorHandler = CollectAll.class
     )
```
The one important thing to point out here is we are changing the `errorHandler` to `CollectAll` which will allow us to more intelligently handle errors later.

{% include slide-end.md %}
{% include slide-start.md %}

### Invoking our CLI

So you probably noticed we had zero logic in the class defining our CLI, similar to our single command example we need to define an appropriate `main()` method for our CLI.  This we do in the {% include github-ref.md package="examples.sendit" module="airline-examples" class="SendIt" %} class:

```java
public class SendIt {

    public static void main(String[] args) {
        Cli<ExampleRunnable> parser = new Cli<ExampleRunnable>(SendItCli.class);
        try {
            // Parse with a result to allow us to inspect the results of parsing
            ParseResult<ExampleRunnable> result = parser.parseWithResult(args);
            if (result.wasSuccessful()) {
                // Parsed successfully, so just run the command and exit
                System.exit(result.getCommand().run());
            } else {
                // Parsing failed
                // Display errors and then the help information
                System.err.println(String.format("%d errors encountered:", result.getErrors().size()));
                int i = 1;
                for (ParseException e : result.getErrors()) {
                    System.err.println(String.format("Error %d: %s", i, e.getMessage()));
                    i++;
                }

                System.err.println();
                
                Help.<ExampleRunnable>help(parser.getMetadata(), Arrays.asList(args), System.err);
            }
        } catch (Exception e) {
            // Errors should be being collected so if anything is thrown it is unexpected
            System.err.println(String.format("Unexpected error: %s", e.getMessage()));
            e.printStackTrace(System.err);
        }
        
        // If we got here we are exiting abnormally
        System.exit(1);
    }
}
```
Once again there's a lot going on, so let's break it down...

{% include slide-end.md %}
{% include slide-start.md %}

#### Creating a Parser Instance

```java
Cli<ExampleRunnable> parser = new Cli<ExampleRunnable>(SendItCli.class);
```
So firstly we create an instance of the `Cli` class, not to be confused with the `@Cli` annotation, referring to our previously introduced class with the `@Cli` annotation.

As mentioned we need to define a type for the commands that will be parsed.  So this is where it is helpful to have all your commands inherit from a common parent class or implement a common interface.

**NB** You can always use `Object` here as all Java objects derive from this but this will make the rest of your implementation awkward!

#### Parsing the User Inputs

```java
// Parse with a result to allow us to inspect the results of parsing
ParseResult<ExampleRunnable> result = parser.parseWithResult(args);
```
Here we call the `parseWithResult()` method passing in the user arguments received by our `main()` method.  This will give us a `ParseResult` instance that we can inspect to see if parsing succeeded:

```java
if (result.wasSuccessful()) {
    // Parsed successfully, so just run the command and exit
    System.exit(result.getCommand().run());
```
Assuming successful parsing we can simply call `getCommand()` on our `result` and then invoke its `run()` method since all our commands implement a common interface.

Alternatively we could have just called `parse(args)` which would return either the parsed command, throw an exception or return `null` depending on the user inputs and the parser configuration.

{% include slide-end.md %}
{% include slide-start.md %}

#### Handling Errors

If parsing wasn't successful then we need to do something about that.  We specified a different error handler earlier that allows us to collect up all the errors:

```java
} else {
     // Parsing failed
     // Display errors and then the help information
     System.err.println(String.format("%d errors encountered:", result.getErrors().size()));
     int i = 1;
     for (ParseException e : result.getErrors()) {
        System.err.println(String.format("Error %d: %s", i, e.getMessage()));
        i++;
     }
```
So we loop over all the errors printing them out

```java                
     System.err.println();
                
     Help.<ExampleRunnable>help(parser.getMetadata(), Arrays.asList(args), System.err);
  }
 ```
 
Followed by invoking the help system to display the help for the CLI.

{% include slide-end.md %}
{% include slide-start.md %}

### Shell Script

As noted earlier we usually want to create an entry point shell script for our CLI that matches the name declared in our `@Cli` annotation.

Here's the contents of [`send-it`]({{ site.github.repo }}/blob/master/airline-examples/send-it):

```bash
#!/usr/bin/env bash

JAR_FILE="target/airline-examples.jar"

if [ ! -f "${JAR_FILE}" ]; then
  echo "Examples JAR ${JAR_FILE} does not yet exist, please run mvn package to build"
  exit 1
fi

java -cp "${JAR_FILE}" com.github.rvesse.airline.examples.sendit.SendIt "$@"
```
To run our CLI we just need to invoke the script i.e.

```
> ./send-it
```

{% include slide-end.md %}
{% include slide-start.md %}

### `send-it`

Since we defined our default command to be the help command we get useful output:

```
> ./send-it
usage: send-it <command> [ <args> ]

Commands are:
    check-address          Check if an address meets our restrictions
    check-postcodes        Checks whether postcodes are valid
    generate-completions   Generates a Bash completion script, the file can then be sourced to provide completion for this CLI
    help                   A command that provides help on other commands
    price                  Calculates the price for a parcel
    send                   Sends a package

See 'send-it help <command>' for more information on a specific command.
```

Why not try asking for help on the `send` command we saw earlier:

```
> ./send-it help send
```

{% include slide-end.md %}
{% include slide-start.md %}

## Step 4 - Help System

So how did that last demo work?

Airline includes a help system that can generate help in a variety of formats plus prebuilt commands and options that can be added into your commands/CLIs.

We can invoke help in a number of ways:

- Composing `HelpOption` into our commands
- Composing the `Help` command into our CLIs
- Invoking the help system directly

Let's see the difference between each.

{% include slide-end.md %}
{% include slide-start.md %}

### Adding `HelpOption` to our commands

This is a pre-built class which defines a `-h`/`--help` option, therefore we can compose this using `@Inject` as seen earlier:

```java
@Command(name = "simple", description = "A simple example command")
public class Simple implements ExampleRunnable {
    @Inject
    private HelpOption<Simple> help;
    
    // Rest of implementation omitted for brevity
    
    @Override
    public int run() {
        if (help.showHelpIfRequested())
            return 0;
            
        System.out.println("Flag was " + (this.flag ? "set" : "not set"));
        System.out.println("Name was " + this.name);
        System.out.println("Number was " + this.number);
        if (args != null)
            System.out.println("Arguments were " + StringUtils.join(args, ","));
 
        return 0;
    }
}
```

We can see in the `run()` method we call the `showHelpIfRequested()` method to check if the user requested help.  If this returns `true` then help was requested and has been shown so we just exit.  If this returns `false` then we continue with our normal logic.

Let's try that:

```
> ./runExample Simple
Flag was not set
Name was null
Number was 0
> ./runExample Simple --help
NAME
        simple - A simple example command
...
```

{% include slide-end.md %}
{% include slide-start.md %}

### Including the `Help` command

Airline includes a pre-built `Help` command that implements `Runnable` and `Callable<Void>`.  If either of these are the common interface for your commands you can simply add this to your commands in your `@Cli` declaration e.g.

```java
@Cli(name = "send-it", 
     description = "A demonstration CLI around shipping",
     commands = {
             CheckAddress.class,
             CheckPostcodes.class,
             Send.class,
             Price.class,
             Help.class,
             BashCompletion.class
     })
public class SendItCli {
     
}
```

If you use a different interface then you can simply extend this class and have it implement your interface and call the `run()` method from the base class e.g. {% include github-ref.md package="examples.help" class="CustomHelp" module="airline-examples" %}

```java
@Command(name = "help", description = "Shows help")
public class CustomHelp extends Help<YourInterface> implements YourInterface {

    @Override
    public void execute() {
        super.run();
    }

}

```

{% include slide-end.md %}
{% include slide-start.md %}

### Invoking Help manually

If the previous two approaches don't work then you can always invoke help manually.  You can do this in two ways:

1. Static Methods
2. Directly creating a help generator

#### Static Methods

Even if you can't incorporate the `Help` command directly you can call its static help methods e.g. {% include github-ref.md package="examples.cli.commands" class="Help" module="airline-examples" %}

```java
@Command(name = "help", 
                     description = "A command that provides help on other commands")
public class Help implements ExampleRunnable {

    @Inject
    private GlobalMetadata<ExampleRunnable> global;

    @Arguments(description = "Provides the name of the commands you want to provide help for")
    @BashCompletion(behaviour = CompletionBehaviour.CLI_COMMANDS)
    private List<String> commandNames = new ArrayList<String>();
    
    @Option(name = "--include-hidden", description = "When set hidden commands and options are shown in help", hidden = true)
    private boolean includeHidden = false;

    @Override
    public int run() {
        try {
            com.github.rvesse.airline.help.Help.help(global, commandNames, this.includeHidden);
        } catch (IOException e) {
            System.err.println("Failed to output help: " + e.getMessage());
            e.printStackTrace(System.err);
            return 1;
        }
        return 0;
    }
}
```

Here we call the static `help()` method passing in the metadata for our CLI (which Airline populated for us) plus the command names that help was requested for.

{% include slide-end.md %}
{% include slide-start.md %}

#### Direct help generators

Alternatively we can create and use a help generator directly e.g. {% include github-ref.md package="examples.sendit" class="GenerateHelp" module="airline-examples" %}

```java
@Command(name = "generate-help")
public class GenerateHelp {

    public static void main(String[] args) {
        Cli<ExampleRunnable> cli = new Cli<ExampleRunnable>(SendItCli.class);
        
        GlobalUsageGenerator<ExampleRunnable> helpGenerator = new CliGlobalUsageGenerator<>();
        try {
            helpGenerator.usage(cli.getMetadata(), System.out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

Here we create a specific instance of a `GlobalUsageGenerator` and call the `usage()` method to generate the help.

{% include slide-end.md %}
{% include slide-start.md %}

## Workshop Summary

So we have seen:

- How to define options and arguments using [`@Option`](../annotations/option.html) and [`@Arguments`](../annotation/arguments.html)
    - How we can use restriction annotations to restrict permissible values, option combinations etc.
- How to compose our options into a [`@Command`](../annotations/command.html)
- How to further compose our commands into an `@Cli`
- How to execute our CLI
- How to incorporate help into our CLIs

This is everything you need to make a functional CLI with Airline.

### So What's Next?

The user guide which has been linked throughout these slides covers all these topics, plus many more, in lots more detail and examples.  Find it at [http://rvesse.github.io/airline/]()

Please try it out, post questions, problems etc. at [http://github.com/rvesse/airline/issues]()

#### Questions

Please feel free to ask questions now!

#### Time Allowing...

The remainder of the slides give a quick tour of some of the more advanced features of the library.  If we have any extra time left we'll take a look at these...

{% include slide-end.md %}
{% include slide-start.md %}

## Customising the Parser

As we glossed over earlier we can optionally customise our parser to change the command line behaviour in a variety of ways.

So we saw earlier in our `SendItCli` example the parser being customised via the `parserConfiguration` field of the `@Cli` annotation.  Let's look more into that now.

### `@Parser`

The [`@Parser`](../annotations/parser.html) annotation can be used in two ways:

- Applied directly to a class annotated with [`@Command`](../annotations/command.html), this customises the parser for `SingleCommand` based parsers
- Used in the `parserConfiguration` field of the [`@Cli`](../annotations/cli.html) annotation, this customises the parser for `Cli` based parsers

There are lots of behaviours that can be customised with this annotation e.g.

- Providing [User Defined Aliases](aliases.html) so users can define command aliases within your CLIs
- Configuring option styles (see next slide for discussion of this)
- Automated abbreviation support i.e. allow users to type only partial command/option names provided what they enter is unambiguous
- Error Handling (as seen earlier)

{% include slide-end.md %}
{% include slide-start.md %}

### Configuring option styles

By default Airline parses three common option styles in the following order of preference:

- {% include javadoc-ref.md class="StandardOptionParser" package="parser.options" %} - Simple white space separated option and values e.g. `--name value` sets the option `--name` to `value`
- {% include javadoc-ref.md class="LongGetOptParser" package="parser.options" %} - Long form GNU `getopt` style e.g. `--name=value` sets the option `--name` to `value`
- {% include javadoc-ref.md class="ClassicGetOptParser" package="parser.options" %} - Short form GNU `getopt` style e.g. `-n1` sets the option `-n` to `1`

This can be customised via several fields of the [`@Parser`](../annotations/parser.html) annotation e.g.

```java
parserConfiguration = @Parser(
       useDefaultOptionParsers = true,
       defaultParsersFirst = false,
       optionParsers = { ListValueOptionParser.class }
     )
```
- `useDefaultOptionParsers` indicates whether to use this default setup
- `defaultParsersFirst` controls whether the defaults parsers are preferred in favour of any additional ones specified
- `optionParsers` specifies additional option parsers to use

A couple of additional styles are built-in but not enabled by default:

- {% include javadoc-ref.md class="MaybePairValueOptionParser" package="parser.options" %} - Arity 2 options where the user may specify the values as whitespace/`=` separated e.g. `--name foo bar` and `--name foo=bar` are both acceptable and set the option `--name` to the values `foo` and `bar`
- {% include javadoc-ref.md class="ListValueOptionParser" package="parser.options" %} - Options that may be specified multiple times can be specified in a compact comma separated list form e.g. `--name foo,bar` sets the option `--name` to the values `foo` and `bar`.

**NB** - Power Users can also create [Custom Option Parsers](../parser/options.html) if desired.

{% include slide-end.md %}
{% include slide-start.md %}

### Allowing complex numeric inputs

Airline allows for customising how it interprets numeric values passed to any `@Option`/`@Arguments` annotated field that has a numeric type i.e. `byte`, `short`, `int`, `long`, `float` and `double` or their boxed equivalents.

This can be controlled either globally on the `@Parser` annotation with the `numericTypeConverter` field or on a per-option basis by using the `typeConverterProvider` e.g.

```java
@Parser(numericTypeConverter=Hexadecimal.class)
```

Or:

```java
    @Option(name = { "-b", "--bytes"}, 
            description = "Quantity of bytes, optionally expressed in compact form e.g. 1g",
            typeConverterProvider = KiloAs1024.class)
    @Required
    private Long bytes;
```

Let's try that:

```
> ./runExample ByteCalculator --bytes 4gb
4,294,967,296 Bytes

Exiting with Code 0
> ./runExample ByteCalculator --bytes 16k
16,384 Bytes

Exiting with Code 0
```

{% include slide-end.md %}
{% include slide-start.md %}

## Generating Manual Pages

Manual pages are provided by using help generator as seen earlier.  This is provided in the separate [`airline-help-man`](../help/man.html) module.

If we use {% include javadoc-ref.md class="ManCommandUsageGenerator" package="help.man" module="airline-help-man" %} or {% include javadoc-ref.md class="ManGlobalUsageGenerator" package="help.man" module="airline-help-man" %} our output will be Troff plus `man` extensions that can be rendered by the `man` command.

For example the {% include github-ref.md class="Manuals" package="examples.cli.commands" module="airline-examples" %} class demonstrates this:

```java
@Command(name = "generate-manuals", description = "Generates manual pages for this CLI that can be rendered with the man tool")
public class Manuals implements ExampleRunnable {

    @Inject
    private GlobalMetadata<ExampleRunnable> global;

    @Option(name = "--include-hidden", description = "When set hidden commands and options are shown in help", hidden = true)
    private boolean includeHidden = false;

    @Override
    public int run() {
        try (FileOutputStream output = new FileOutputStream(this.global.getName() + ".1")) {
            new ManGlobalUsageGenerator<ExampleRunnable>(ManSections.GENERAL_COMMANDS).usage(this.global, output);
            System.out.println("Generated manuals to " + this.global.getName() + ".1");
        } catch (IOException e) {
            System.err.println("Error generating completion script: " + e.getMessage());
            e.printStackTrace(System.err);
        }
        return 0;
    }
}
```

Let's get and view the output:

```
> ./send-it generate-manuals
Generated manuals to send-it.1
> man ./send-it.1
```
{% include slide-end.md %}
{% include slide-start.md %}

## Providing Bash Completion

By the same mechanism we can also do [Bash completion](../help/bash.html), we may need to use the additional `@BashCompletion` annotation on our option/argument fields to control how we'd like Bash to complete things.

We can then create a command like {% include github-ref.md class="BashCompletions" package="examples.cli.commands" %}:

```java
@Command(name = "generate-completions", description = "Generates a Bash completion script, the file can then be sourced to provide completion for this CLI")
public class BashCompletion implements ExampleRunnable {

    @Inject
    private GlobalMetadata<ExampleRunnable> global;
    
    @Option(name = "--include-hidden", description = "When set hidden commands and options are shown in help", hidden = true)
    private boolean includeHidden = false;

    @Override
    public int run() {
        try (FileOutputStream out = new FileOutputStream(this.global.getName() + "-completions.bash")) {
            new BashCompletionGenerator<ExampleRunnable>(this.includeHidden, false).usage(global, out);
            System.out.println("Generated completion script " + this.global.getName() + "-completions.bash");
        } catch (IOException e) {
            System.err.println("Error generating completion script: " + e.getMessage());
            e.printStackTrace(System.err);
        }
        return 0;
    }
}
```

Similar to our previous example this creates a script that we can source.  Let's try that out:

```
> ./send-it generate-completions
Generated completion script send-it-completions.bash
> source send-it-completions.bash
> ./send-it <tab>
check-address         check-postcodes       generate-completions  generate-manuals      help                  price                 send 
```

{% include slide-end.md %}
{% include slide-start.md %}

## Maven Plugin

There is also a Maven Plugin that can generate the help as part of your build process e.g.

```xml
      <plugin>
        <groupId>com.github.rvesse</groupId>
        <artifactId>airline-maven-plugin</artifactId>
        <version>{{ site.version }}</version>
        <configuration>
          <defaultOptions>
            <multiFile>true</multiFile>
          </defaultOptions>
          <formats>
            <format>BASH</format>
            <format>MAN</format>
          </formats>
          <sources>
            <source>
              <classes>
                <class>com.github.rvesse.airline.examples.sendit.SendItCli</class>
              </classes>
            </source>
          </sources>
        </configuration>
        <executions>
          <execution>
            <goals>
              <goal>generate</goal>
            </goals>
          </execution>
        </executions>
      </plugin>
```

Added to your `pom.xml` would generate both Bash Completion scripts and Manual pages as part of your build.

{% include slide-end.md %}

{% include slideshow-end.md name="workshop" %}