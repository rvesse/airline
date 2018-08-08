---
layout: page
title: Workshop - Building a Killer Command Line App with Airline
---

This workshop session is designed to give you a complete introduction to the core features of Airline for creating powerful CLIs.

{% include toc.html %}

## Pre-requisites

In order to follow along with this workshop we assume the following knowledge and tools:

- Understanding of the Java programming language
- JDK 7, 8, 9 or 10 available
- [`git`](https://git-scm.org) installed
- [`mvn`](https://maven.apache.org) installed

## Background

Everyone builds command line applications at some point but often they are cobbled together or full of needless boiler plate. Airline takes a fully declarative approach to command line applications allowing users to create powerful and flexible command lines.

Airline takes care of lots of heavy lifting providing many features not found in similar libraries including annotation driven value validation restrictions, generating Man pages and Bash completion scripts to name just a few. In the workshop session we'll work through a comprehensive example command line application to see just how powerful this can be.

### History

Airline started out as an open source project on GitHub back in January 2012.  I first encountered this library in use in one of our competitors products partway through that year.  I quickly started using it in my own work but encountered a few limitations.  The original authors were not receptive to pull requests so I forked the code and started maintaining my own version that has since evolved considerably.

### Design Philosophy

1. Be Declarative **Not** Imperative
2. Avoid boiler plate code
3. Allow deep customisation

Firstly we want to define our command lines using declarative annotations.  This allows us to separate the command line definition cleanly from the runtime logic.  It also enables us to do optional build time checking of our definitions to ensure valid command line apps.

Secondly we look to avoid the typical boiler plate code associated with many command line libraries.  You shouldn't need to write a ton of `if` statements to check that values for options fall in specified ranges or meet application specific constraints.

Finally we don't want to tie you into a particular implementation.  We provide extensibility of almost every aspect of the parsing process yet provide a general purpose default setup that should suit many users.

## Workshop Overview

For this workshop we are going to build an example command line application called `send-it` that is designed for shipping of packages.  The example code on this page is typically truncated to omit things like import declarations for brevity, the full code is linked alongside each example.

The example code all lives inside the Airline git repository at [https://github.com/rvesse/airline/tree/master/airline-examples](https://github.com/rvesse/airline/tree/master/airline-examples)

### Following Along with the Examples

We use `>` to indicate that a command should be run at a command prompt

To follow along you should check out the code and build the examples:

```
> git clone https://github.com/rvesse/airline.git
> cd airline
> mvn package
```

Many of the examples are runnable using the `runExample` script in the `airline-examples` sub-directory e.g.

```
> cd airline-examples
> ./runExample SendIt
```
Or for this specific workshop the `send-it` script in that same sub-directory can be used:

```
> ./send-it
```

## Step 1 - Define Options

Airline works with POJOs (Plain Old Java Objects) so firstly we need to define some classes that are going to hold our commands options.

### `@Option`

The [`@Option`](../annotations/option.html) annotation is used to mark a field as being populated by an option.  At a minimum it needs to define the `name` field to provide one/more names that your users will enter to refer to your option e.g.

```java
@Option(name = { "-e", "--example" })
private String example;
```
Here we define a simple `String` field and annotate it with `@Option` providing two possible names - `-e` and `--example` - by which users can refer to it.

Other commonly used fields in the `@Option` annotation include `title` used to specify the title by which the value is referred to in help and `description` used to provide descriptive help information for the option.

#### `PostalAddress` example

Let's take a look at {% include github-ref.md package="example.sendit" class="PostalAddress" module="airline-examples" %} which defines options for specifying a UK postal address.  Explanatory text is interspersed into the example:

```java
public class PostalAddress {
    
    @Option(name = "--recipient", title = "Recipient", 
            description = "Specifies the name of the receipient")
    @Required
    public String recipient;
```
So we start with a fairly simply definition, this defines a `--recipient` option and states that it is a required option via the [`@Required`](../annoations/required.html) annotation.

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

```java
    @Option(name = { "-a", "--address", "--line" }, title = "AddressLine", 
            description = "Specifies an address line.  Specify this multiple times to provide multiple address lines, these should be in the order they should be used.")
    @Required
    @MinOccurrences(occurrences = 1)
    public List<String> addressLines = new ArrayList<>();
```
Here we have an option that may be specified multiple times to provide multiple address lines.  **Importantly** we need to define it with an appropriate `Collection` based type, in this case `List<String>` in order to collect all the address lines specified.

Here we also use the [`@MinOccurences`](../annotations/min-occurrences.html) annotation to state that it must occur at least once in addition to using the previously seen `@Required`

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
And finally we have some regular Java code in our class.  Your normal logic can co-exist happily alongside your Airline annotations, we'll see this used later to implement our actual command logic.

### `@Arguments`

The [`@Arguments`](../annotation/arguments.html) is used to annotate a field that will receive arbitrary arguments i.e. anything that is not an option as defined by your `@Option` annotations.  This is useful when your command wants to operate on a list of things so is typically used in conjunction with a `Collection` typed field e.g. `List<String>`.

For example let's take a look at it in use in the {% include github-ref.md package="example.sendit" class="CheckPostcodes" module="airline-examples" %} command:

```java
    @Arguments(title = "PostCode", description = "Specifies one/more postcodes to validate")
    @Required
    @MinOccurrences(occurrences = 1)
    @Pattern(pattern = "^([A-Z]{1,2}([0-9]{1,2}|[0-9][A-Z])) (\\d[A-Z]{2})$", description = "Must be a valid UK postcode.", flags = java.util.regex.Pattern.CASE_INSENSITIVE)
    public List<String> postCodes = new ArrayList<>();
```

Which we can run like so:

```
> ./send-it check-postcodes "BS1 4DJ" "RG19 6HS"
BS1 4DJ is a valid postcode
RG19 6HS is a valid postcode
```

### Restrictions

## Step 2 - Define a Command

### `@Command`

### Using `@Inject` for composition

### Command Logic

### Invoking our command

## Step 3 - Define a CLI

### `@Cli`

### Invoking our CLI

## Step 4 - Customising the Parser

### `@Parser`

### Configuring option styles

### Allowing complex numeric inputs

## Step 5 - Help System

### Adding `HelpOption` to our commands

### Including the `Help` command

### Invoking Help manually

### Generating Manual Pages