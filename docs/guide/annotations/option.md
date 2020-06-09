---
layout: page
title: Option Annotation
---

{% include toc.html %}

## `@Option`

The `@Option` annotation is applied to fields of a class to indicate that the field should be populated from an option.

Airline discovers options by walking the class hierarchy of `@Command`, therefore `@Option` annotated fields need not appear in the same class that is annotated with `@Command`, they may occur anywhere in the ancestry of the class or even in another classes as described in the [Inheritance and Composition](../practise/oop.html) documentation.

### Basic Definition

At a minimum you must specify the names of an `@Option`, these are the strings that are used by users to specify that option e.g. `-a`, `--alpha`, `--alphabet` etc.

```java
@Option(name = { "-a", "--alpha",  "--alphabet" })
private String alphabet;
```
	
Here we specify that this option may be referred to by any of the three names - `-a`, `--alpha`, `--alphabet` and that the option has a type of `String`.  The options type is taken from the declared type of the annotated field and need not be declared in any other way.  Please see [Supported Types](../practise/types.html) for details on the types that Airline supports and how to extend it to additional types.

{% include alert.html %}
Different option definitions may not have overlapping names i.e. all values in `name` must be unique and not used by any other option defined for a command.
If you define options that have overlapping names then Airline will throw an `IllegalArgumentException` when you attempt to create a parser.
{% include end-alert.html %}#### Title and Description

For help purposes you may also want to specify the `title` and the `description` for an option.

The `title` specifies how the values (if any) that the option takes will be referred to in [Help](../help/), if you don't specify a `title` then the title is inferred from the name of the annotated field.

```java
@Option(name = { "-a", "--alpha",  "--alphabet" }, 
        title = "CharacterSet")
private String alphabet;
```

So here we define that our option should have its value referred to as `CharacterSet` in help.

Similarly if we want to describe how an option is used we can add a `description` e.g.

```java
@Option(name = { "-a", "--alpha",  "--alphabet" }, 
        title = "CharacterSet", 
        description = "Sets the character set to be used for output")
private String alphabet;
```

This provides users with some information about what an option actually does that will be included in [Help](../help/).

{% include req-ver.md version="2.8.0" %}

If we have an option with an arity greater than 1 we can specify an array of titles e.g.

```java
@Option(name = "--kvp", arity = 2, title = { "Key", "Value" })
private List<String> kvps;
```
Here we have an option with arity 2 and separate titles for each value that the option accepts.

#### Hidden Options

As with `@Command` annotations we can add a `hidden` field to specify that an option is hidden e.g.

```java
@Option(name = { "-a", "--alpha",  "--alphabet" }, 
        title = "CharacterSet", 
        description = "Sets the character set to be used for output", 
        hidden = true)
private String alphabet;
```

If an option is marked `hidden = true` then it will not be included in [Help](../help/) so only users who are aware of the option can invoke it.

{% include alert.html %}
Marking an option `hidden = true` **DOES NOT** prevent users from using it.  **DO NOT** rely on hiding options to prevent users from invoking them.
{% include end-alert.html %}

#### Arity

The `arity` of an option specifies how many values it expects to receive, by default the arity is 0 for any `Boolean` typed option and 1 for any other option.  This means that for `Boolean` options they do not take a value while for any other option they take 1 value, so our example `--alphabet` option described on this page takes a single value.

In some cases we may actually want to take multiple values e.g.

```java
@Option(name = { "-b", "--beta" }, 
        arity = 2)
private List<String> beta;
```
	
Here we define a new option `-b`/`--beta` which takes in two values, so for example we might invoke it like so:

    > cli group command --beta x y

This would place the values `x` and `y` into our `beta` field.

{% include alert.html %}
Note that when consuming the values of collection fields the field will be `null` if the user did not provide any values for that option **and** your class does not explicitly instantiate the collection.
{% include end-alert.html %}

If users invoke options with too few arguments then Airline will throw an error during parsing.

### Advanced Definition

There are some further fields that are less commonly used on `@Option` definitions but may occasionally be useful in more complex CLI definitions.

#### Scope

By default options have command scope which means they can only be passed to commands, alternatively you can specify that options have `GROUP` or `GLOBAL` scope via the `type` field.
	
So for our example so far if we were invoking this it would look something like the following:

    > cli group command --alphabet UTF-8

When an option has `GROUP` or `GLOBAL` scope it may be specified earlier in the invocation either after the group or immediately.

So if our option was redefined like so:

```java
@Option(name = { "-a", "--alpha",  "--alphabet" }, 
        title = "CharacterSet", 
        description = "Sets the character set to be used for output",
        type = OptionType.GROUP)
private String alphabet;
```

Then we could instead invoke it like so:

    > cli group --alphabet UTF-8 command
    
And similarly if our option was redefined like so:

```java
@Option(name = { "-a", "--alpha",  "--alphabet" }, 
         title = "CharacterSet", 
         description = "Sets the character set to be used for output", 
         type = OptionType.GLOBAL)
private String alphabet;
```

Then we could instead invoke it like so:

    > cli --alphabet UTF-8 group command
    
#### Overrides and Sealed

When you are using [Inheritance and Composition](../practise/oop.html) you may need/want to allow changing the definition of an option further down your inheritance hierarchy.

In order to do this we simply need to define the option again changing the relevant parts of the definition and adding the `overrides = true` field to indicate that we are changing a definition.

{% include alert.html %}
Airline does not allow changing an option definition unless you explicitly state `overrides = true`  
Even then there are some things that **cannot** be overridden - namely the `name`, `type` and `arity` fields.  Also you can only change the Java type of the annotated field if the new type has a narrowing cast from the original type.
{% include end-alert.html %}

For example lets change the `hidden` state of our option:

```java
@Option(name = { "-a", "--alpha",  "--alphabet" }, 
        title = "CharacterSet", 
        description = "Sets the character set to be used for output", 
        hidden = true, 
        overrides = true)
private String alphabet;
```

On the other hand sometimes if you are defining an option you may wish to prevent derived commands from changing the specification of that option in which case you can add `sealed = true` to the definition e.g.

```java
@Option(name = { "-a", "--alpha",  "--alphabet" }, 
        title = "CharacterSet", 
        description = "Sets the character set to be used for output", 
        hidden = true, 
        overrides = true,
        sealed = true)
private String alphabet;
```

Using this prevents the option from being redefined further down the inheritance hierarchy.

### Type Converter Provider

To override how user inputs are converted into values for these arguments specify a custom `TypeConverterProvider` to use e.g.

```java
@Option(name = "--hex",
               description = "Sets hexadecimal value", 
               typeConverterProvider = Hexadecimal.class)
private Integer hexCode;
```

The above example uses the `Hexadecimal` type converter which parses users input as hexadecimal numbers when converting to `Integer`.