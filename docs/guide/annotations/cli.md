---
layout: page
title: Cli Annotation
---

## `@Cli`

The `@Cli` annotation is applied to a class to define a complex CLI consisting of potentially many commands.

At a minimum you need to define a name and at least one command for your CLI e.g.

```java
@Cli(name = "basic", 
     defaultCommand = GettingStarted.class, 
     commands = { GettingStarted.class, Tool.class })
public class BasicCli { }
```

Here we define a CLI named `basic` that has a default command of `GettingStarted.class` which we saw on the introductory page of the [User Guide](../) and also contains the command `Tool.class` which we saw in the examples for the [`@Command`](command.html) annotation.

{% include alert.html %}
Names are restricted to not contain whitespace but otherwise can contain whatever characters you want.
{% include end-alert.html %}
	
Remember that users need to be able to type the name at their command line terminal of choice so it is best to limit yourself to common characters i.e. alphanumerics and common punctuation marks.

### Description

As with commands typically we also want to add a `description` that describes what a CLI does e.g.

```java
@Cli(name = "basic", 
    description = "Provides a basic example CLI",
    defaultCommand = GettingStarted.class, 
    commands = { GettingStarted.class, Tool.class })
public class BasicCli {
```

### Commands

The `commands` field defines a list of commands e.g.:

```java
@Cli(name = "send-it", 
       description = "A demonstration CLI around shipping",
       commands = {
             CheckAddress.class,
             CheckPostcodes.class,
             Send.class,
             Price.class,
             Help.class,
             BashCompletion.class,
             Manuals.class
       })
    public class SendItCli { }
```

By default these commands are placed into the default top level group.  However if any of those commands has a [`@Group`](group.html) or [`@Groups`](groups.html) annotation then those will be respected and the commands placed into the appropriate groups.

### Default Command

The `defaultCommand` field specifies the default command to use if the user does not explicitly specify a command to run.  This can be useful to automatically invoke your CLIs help command or provide some other default behaviour e.g.

```java
    defaultCommand = Help.class
```


### Groups

The `groups` field is used to specify an array of [`@Group`](group.html) annotations each of which defines a group/sub-group within your CLI e.g.

```java
@Cli(
    name = "cli", 
    description = "A simple CLI with several commands available in groups",
    groups = {
        @Group(
            name = "basic",
            description = "Basic commands",
            commands = { Simple.class }
        ),
        @Group(
            name = "inheritance",
            description = "Commands that demonstrate option inheritance",
            commands = { Parent.class, Child.class, GoodGrandchild.class }
        )
    },
    commands = { Help.class }
)
public class GroupCli {
```

In the above example we have two top level groups named `basic` and `inheritance` defined.

#### Creating Sub-groups

To create a sub-group as opposed to a top level group the `name` field of the group needs to contain multiple names separated by whitespace, since whitespace is never valid in a group name e.g.

```java
    @Cli(name = "test", 
         groups = { 
           @Group(name = "foo bar", 
                          defaultCommand = Help.class, 
                          commands = { Help.class }), 
           @Group(name = "foo baz") 
         })
    private static class SubGroupsCli02 {

    }
```

Here we see two sub-groups being defined - `bar` and `baz` - both of which are children of the `foo` group.  Note that the `foo` group is implicitly created as a side effect of these definitions even though if it doesn't have an explicit definition itself.

### Global Restrictions

[Global restrictions](../restrictions/global.html) on a CLI may be added via the `restrictions` field e.g.

```java
@Cli(name = "cli",
     description = "Test CLI", 
     commands = { Args1.class,
                  EmptyCommand.class }, 
     includeDefaultRestrictions = false, 
     restrictions = { CommandRequiredRestriction.class })
    private class CommandRequiredCli {

    }
```

Note that as seen above you may also wish to use the `includeDefaultRestrictions` field to disable the default set of global restrictions that are normally used.

Alternatively you can also specify [global restrictions](../restrictions/global.html) via their corresponding annotations on the class that is annotated with `@Cli`.   Please refer to the global restrictions documentation for more information on those.


### Parser Configuration

Parser configuration for a CLI may be specified via the `parserConfiguration` field which takes a [`@Parser`](parser.html) annotation.

Please see the documentation for that annotation for notes on controlling the parser configuration, or take a look at the general [Parser](../parser/index.html) documentation for a more general overview of the parser sub-system.