---
layout: page
title: Introduction to Airline
---

{% include toc.html %}

## Welcome

Welcome to the Airline Users Guide, this guide is intended to show you how to use every aspect of Airline.

Many of the examples contained in this user guide may be found in compilable form in our Git repository at <a href="{{
site.github.repo }}/tree/master/airline-examples/src/main/java/com/github/rvesse/airline/examples/userguide">GitHub</a>.
If you have the code checked out and compiled locally you can use the provided `runExamples` script to launch an example
e.g.

```
airline-examples> ./runExample GettingStarted
```
## Warnings

Throughout this website where you see a box like the following:

{% include alert.html %}
Some warning text goes here...
{% include end-alert.html %}

This is a warning that describes a potential pit-fall that users might encounter.  These are used to call out places
where it is possible for users to introduce dangerous behaviours, or where they are strict constraints they must
respect.

Also where the library has evolved over time you may see the following:

{% include req-ver.md version="2.4.5" %}

This is used to denote features that are only available in newer versions of the library or where behaviours have
changed across versions.

Where a specific optional module is required then you may see the following:

{% include req-ver.md version="3.0.0" module="airline-jpms-resources" %}

## Definitions

Before you get started reading this guide it is useful to introduce the terminology that Airline uses to make sure you
are clear what we are referring to.

- **Command Line Interface (CLI)**  
  A Command Line Interface (CLI) is a collection of commands potentially grouped into hierarchical groupings.  `git` is a popular example of a CLI
- **Command**  
  A command is a single tool that is invoked by a user, a command may appear within multiple groups within a CLI and
  take a variety of options and arguments
- **Command Group**  
  A command group is a collection of commands within a CLI identified by a name.  Groups may themselves contain other
  groups to create hierarchies of commands. `git remote` is an example of a group within a CLI
- **Option**  
  An option is a combination of an identifying name e.g. `--name` followed by zero or more values that are used to
  populate a field of a command, for example `--name Example`
- **Arguments**  
  Arguments are any values passed to a command that are not otherwise interpreted i.e. they do not represent options,
  for example `Example`
- **Restriction**  
  A restriction is a constraint placed upon a CLI, options and/or arguments e.g. marking an option as required

## Phases

When talking about CLI parsing libraries people typically talk about three phases.

Firstly there is the **Definition Phase** in which you define your CLI i.e. command groups, commands, options,
arguments, restrictions etc.  In Airline the definition phase is done primarily through the use of declarative
annotations, you can also do parts of the definition phase using imperative code but it is usually much easier to just
rely on declarative annotations.

Secondly there is the **Parsing Phase** in which you create and run a parser.  Since Airline relies on a declarative
annotation based approach to the **Definition Phase** the parsing phase is very simple since you can have Airline
generate a parser from your class and then run that parser in as little as two lines of code.

Finally there is the **Interrogation Phase** in which you look at the results of the parser and run your command
accordingly.  With Airline the result of the parser is an instance of a Java class/interface with appropriately
populated fields and so you can simply access these fields as desired.

## Requirements

In order to use Airline in your applications you will need to add a dependency on the `airline` library to your project
at a minimum.  Assuming a Maven project you can do this like so:

```xml
<dependency>
  <groupId>com.github.rvesse</groupId>
  <artifactId>airline</artifactId>
  <version>X.Y.Z</version>
</dependency>
```

Where `X.Y.Z` is your desired version, the current stable release is `{{ site.version }}`

If you want to use some of the other features Airline provides then you may also need to add additional dependencies to
grab additional features.

## Your First Command

At its most basic defining a command in Airline means adding some annotation to a class.

Let's take a look at `GettingStarted.java`:

{% include code/getting-started.md %}

We'll talk about each of the things introduced in the subsequent sections and provide links to more in-depth pages where
you can explore each introduced concept in detail.

### Definition Phase

The definition phase in Airline is driven primarily through the use of annotations on classes and their fields.  99% of
the time you can define your desired CLI entirely through annotations, on the rare occasion where this is not the case
you can define some portions directly in Java code.

#### Defining the Command

At a minimum we need to annotate our class with the [`@Command`](annotations/command.html) annotation.  This annotation
tells Airline about the command, you must at a minimum specify the `name` field giving it the name of the command.
Names should ideally be short and memorable and they **must not** contain whitespace.

Here we've also defined the `description` field which gives a short description of the command.

In our example our class is annotated with `@Command` like so:

```java
@Command(name = "getting-started", description = "We're just getting started")
public class GettingStarted {
```

There are lots of other things that we can define on our command if we want, please see the [`@Command`
Annotation](annotations/command.html) documentation to learn more.

#### Defining Options

In order to accept options we need to add a field to our class and annotate it with the
[`@Option`](annotations/option.html) annotation.  In our example the `flag` field is annotated with `@Option` like so:

```java
@Option(name = { "-f", "--flag" }, description = "An option that requires no values")
private boolean flag = false;
```
Here we define a `boolean` option which the user may invoke by passing `-f` or `--flag` at the command line.

There are lots of additional things we can define for our option if we want, please see the [`@Option`
Annotation](annotations/option.html) documentation to learn more.

You can have as many `@Option` definitions as you need provided that none of the definitions have overlapping `name`
values.  Each `@Option` definition **MUST** be associated with a specific field.

#### Defining Arguments

We can accept additional arbitrary inputs by annotating a field with the [`@Arguments`](annotations/arguments.html) annotation e.g.

```java
@Arguments(description = "Additional arguments")
private List<String> args;
```

This will be populated with a list of arguments received at the command line which were not otherwise interpreted by
Airline i.e. anything that was not an option or a command/group name.

There are several additional things we can define for our arguments if we want, please see the [`@Arguments`
Annotation](annotations/arguments.html) documentation to learn more.

### Parsing Phase

For a single command like this we can create a parser in a single line like so:

```java
SingleCommand<GettingStarted> parser = SingleCommand.singleCommand(GettingStarted.class);
```

This tells Airline to extract the annotation meta-data from the given class - `GettingStarted` in our example - and to
use that to prepare the meta-data necessary to parse user input.  We can then create an instance of our class with the
fields appropriately populated based on our option and argument definitions by parsing the command line arguments like
so:

```java
GettingStarted cmd = parser.parse(args);
```
Here we simply pass the received command line arguments to our previously created parser and Airline creates and
populates an instance of our class appropriately.

### Interrogation Phase

Finally we can now interrogate the options received and act accordingly, in this example this phase has been placed into
a separate `run()` method for convenience e.g.

```java
private void run() {
    System.out.println("Flag was " + (this.flag ? "set" : "not set"));
    if (args != null)
        System.out.println("Arguments were " + StringUtils.join(args, ","));
}
```

Since we have an instance of the class we can access the fields as we would do normally since Airline has populated them
with the values passed in at the command line.

## Building a CLI

Now you've seen how to create a simple single command program we can move on to creating a more complex Git style CLI
where multiple commands are provided.  Let's take a look at `BasicCli.java`:

```java
@Cli(name = "basic", 
    description = "Provides a basic example CLI",
    defaultCommand = GettingStarted.class, 
    commands = { GettingStarted.class, Tool.class })
public class BasicCli {
    public static void main(String[] args) {
        com.github.rvesse.airline.Cli<Runnable> cli = new com.github.rvesse.airline.Cli<>(BasicCli.class);
        Runnable cmd = cli.parse(args);
        cmd.run();
    }
}
```

### Definition Phase

For a more complex CLI we need to use the [`@Cli`](annotations/cli.html) annotation to specify our CLI.  In our example
our `@Cli` annotation looks like the following:

```java
@Cli(name = "basic", 
    description = "Provides a basic example CLI",
    defaultCommand = GettingStarted.class, 
    commands = { GettingStarted.class, Tool.class })
```

This states that our CLI has a `name` of `basic` and that it consists of two commands - `GettingStarted.class` and
`Tool.class`.   Each command itself needs to be appropriately defined with at minimum a `@Command` annotation as shown
in the earlier `GettingStarted.java` example.

We also specify that `GettingStarted.class` will serve as our `defaultCommand`, this specifies what the behaviour of our
CLI is if a user does not explicitly provide the name of the command to be run.  The `commands` field is used to provide
an array of all the commands that make up the CLI.

You can see the [`@Cli`](annotations/cli.html) documentation for many more advanced options that the annotation supports.

### Parsing Phase

For a CLI we can create a parser in a single line like so:

```java
Cli<Runnable> cli = new Cli<Runnable>(BasicCli.class);
```

This instructs Airline to extract the annotation meta-data from the given class - `BasicCli` in our example - and use it
to prepare the necessary meta-data to parse user input.  Since this is a CLI this will also extract all the meta-data
for all the `commands` that you specified as part of your CLI annotation.

The type parameter given (`Runnable` in this example) specifies a common type for all the commands in the CLI and will
be the resulting type from parsing.  `Object` can always be used but often it may be useful to have all your commands
implement a common interface as in this example so that they have a standard method that your code can then invoke on
the parsed command to have the actual command logic run.

{% include alert.html %} 
Note that since `Cli` is a class and there is also a `@Cli` annotation if both are used in the
same class then one will need to use the fully qualified package name to disambiguate. 
{% include end-alert.html %}

Once we have the `Cli` object we can parse a command like so:

```java
Runnable cmd = cli.parse(args);
```

### Interrogation Phase

Finally you can now run the received command and interrogate the received options and act accordingly.  Since we defined
our CLI to have all our commands implement `Runnable` then we can simply call the `run()` method:

```java
cmd.run();
```

As Airline has populated the fields of the parsed command appropriately it will have all the necessary information it
needs to run its command logic.

## What Next?

You will probably want to read more about the various [annotations](annotations/) that Airline provides in order to
learn how to annotate your commands and CLI with more advanced features.

It is also worth taking a look at the [Airline in Practise](practise/) pages which detail various practicalities of
using Airline in the real world.

We'd also recommend learning about the [Help](help/) system which you can use to produce help for your commands and CLIs
in a variety of common formats.