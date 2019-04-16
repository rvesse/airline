
# Migration to Airline 3.x from Airline 2.x

Airline 3.x takes the opportunity to make breaking API changes in order to streamline the API, introduce new features and improve support for dependency injection frameworks.

## Improvements to `CommandFactory`

The `CommandFactory` interface is modified to take a `CommandContext` as part of its `createInstance()` method.

### Introduction of `CommandContext`

This intermediate interface is provided that gives access to the parsed information that should then be used by the `CommandFactory` to construct and instantiate the command instance.

This allows for plugging in dependency injection frameworks that previously interacted poorly with Airline e.g. Google Guice

## Google Guice Support

A new `airline-guice` module is provided that provides integration support for Google Guice.  The new `GuiceCommandFactory` can be configured in your parser configuration to use Guice to instantiate your command classes.

## Introduction of `@PositionalArgument`

A new `@PositionalArgument` annotation is introduced along with its corresponding `PositionalArgumentMetadata` which is exposed on `CommandMetadata` via `getPositionalArguments()` e.g.

```java
@Command(name = "ArgsPositional", description = "ArgsPositional description")
public class ArgsPositional
{
    @PositionalArgument(position = PositionalArgument.FIRST, title = "File")
    @Required
    public String file;
    
    @PositionalArgument(position = PositionalArgument.SECOND, title = "Mode")
    public Integer mode;
    
    @Arguments
    public List<String> parameters = new ArrayList<>();
}
```

In the above example we have a required first positional argument which takes a string, followed by an optional positional argument that takes an integer.  Finally any further arguments are captured as before by the `@Arguments` annotated field.

This allows for more fine grained control of argument parsing including:

 - Having arguments with different types
 - Providing per-argument descriptions
 - Specifying different restrictions for different arguments without the need to use `@Partials`/`@Partial`
 - Overriding and sealing arguments similar to how this is already supported for `@Option`

Positional arguments, if present, are parsed prior to any existing `@Arguments` declaration.

There are some restrictions on the definitions of positional arguments:

- The `position` values must form an unbroken sequence i.e. can't have gaps in the positions
- Can't have a `@Required` argument (or `@Arguments`) in a position after an optional positional argument

### Updating `ArgumentsRestriction` implementations for positional arguments

Users who are using various extension points of the library, such as creating custom restrictions will have to update implementations to implement new methods that cover the validation of positional arguments.

### Updating Custom Help Generators for Positional Arguments

If you are writing custom help generators you should now take into account the positional arguments present on the `CommandMetadata` when writing your implementation.

# Migration to Airline 2.2 from Airline 2.1

Airline 2.2 has some minor breaking changes versus Airline 2.1

## `@Arguments` arity

Previously `@Arguments` had an `arity` field that was used to control the maximum arity of arguments.  In 2.2 this is removed and you should use the `@MaxOccurrences` restriction annotation to achieve the same effect e.g.

```java
@Arguments
@MaxOccurrences(occurrences = 3)
private List<String> args = new ArrayList<>();
```

## RONN Help Removed

The `airline-help-ronn` module was removed in 2.2 as it was already deprecated.  For Markdown format help use the `airline-help-markdown` module and for Man pages uses the `airline-help-man` module.

## Global Restriction Annotations

Previously global restrictions for CLIs could only be specified via the `restrictions` field of the `@Cli` annotation.  As of Airline 2.2 the global restrictions can now be specified via annotations in the same way that option and argument restrictions are specified.

Airline 2.2 supports the following global restriction annotations:

- `@CommandRequired`
- `@NoUnexpectedArguments`
- `@NoMissingOptionValues`
- `@None`

Note that most users will not need to use these annotations as the `@Cli` annotation has the `includeDefaultRestrictions` field default to `true` which causes the default set of global restrictions to be included without any further user configuration.

---

# Migrating to Airline 2.1 from Airline 2

Airline 2.1 includes a number of breaking changes which are part of improvements and restructuring of the help systems.

## Help Modules

Previously all help generators were included in the main `airline` module.  With Airline 2.1 only the CLI help is included in that module, all other help generators are provided by separate modules which can be pulled in as desired.

Help Generators | New Module
---------------------- | -----------------
RONN (**Deprecated in 2.1**) | `airline-help-ronn`
HTML | `airline-help-html`
Bash Completion | `airline-help-bash`
Man (**New in 2.1**) | `airline-help-man`
Markdown (**New in 2.1**) | `airline-help-markdown`

If you were using any of these then you may need to tweak your dependencies appropriately.

## Generating Man pages

Previously we provided RONN format help generators which generated RONN output (an extended dialect of Markdown) that could then be converted into `man` pages with the `ronn` tool.  However we found that RONN had a number of bugs that could cause the generated man pages to have strange formatting in some cases.

To address this new Man format help generators are introduced which are capable of generating man pages directly without needing an intermediate format or third party tool.  As a result the existing RONN generators are deprecated and should be avoided in favour of the new help generators.

## Generating Markdown help

Since RONN generators are now deprecated we have also added new Markdown format help generators which can be used to generate Markdown format help directly.  These don't use any of the extended RONN syntax though they do use GitHub Flaboured Markdown table syntax for tables.

## Bash Completion

Previously information for Bash completion was provided by the `completionBehaviour` and `completionCommand` fields of the `@Option` and `@Arguments` annotations.  In 2.1 these are now moved into their own `@BashCompletion` annotation which is now found in the `airline-help-bash` module.

For example in Airline 2:

    @Option(name = { "--example" }, arity = 1, completionBehaviour = CompletionBehaviour.FILES)
    private String example;
    
Would change to the following in Airline 2.1:

    @Option(name = { "--examples" }, arity = 1)
    @BashCompletion(behaviour = CompletionBehaviour.FILES)
    private String example;
    
## Restriction Factories

In Airline 2.0 restriction factories had to be explicitly registered with the `RestrictionRegistry`, Airline 2.1 moves to using `ServiceLoader` for discovery of restriction factories.  While factories may still be explicitly registered they can now also be specified in the following files:

- `META-INF/services/com.github.rvesse.airline.restrictions.factories.OptionRestrictionFactory` 
- `META-INF/services/com.github.rvesse.airline.restrictions.factories.ArgumentsRestrictionFactory`

Note that if you want to use this mechanism and intend to use the Maven shade plugin or similar to build a single JAR you must ensure that you preserve the built-in services files from the `airline` module as otherwise the built-in restriction annotations will not be honoured.

## Help Section Factories

In Airline 2.0 help section factories had to be explicitly registered with the `HelpSectionFactory`, Airline 2.1 moves to using `ServiceLoader` for discovery of help section factories.  While factories may still be explicitly registered they can now also be specified in the following file:

- `META-INF/services/com.github.rvesse.airline.help.sections.factories.HelpSectionFactory`

Note that if you want to use this mechanism and intend to use the Maven shade plugin or similar to build a single JAR you must ensure that you preserve the built-in services files from the `airline` module as otherwise the built-in help section annotations will not be honoured.

Additionally the `HelpSectionRegistry` was moved into the `com.github.rvesse.airline.help.sections.factories` package.

---

# Migrating to Airline 2 from Airline 1

Airline 2 is a significant rewrite of Airline and as such there are lots of breaking changes to be aware of.  This document aims to guide you through the user facing changes.

Note that if you are developing in the internals of Airline there are lots of other changes which are not covered here since they are not visible to end users.

## Defining CLIs

The major change in defining CLIs using the `CliBuilder<T>` is that all parser related options are now moved onto a new `ParserBuilder<T>`.  This builder can be accessed via the `withParser()` method.

So for example the following Airline 1 code:

```
CliBuilder<ExampleRunnable> builder 
  = Cli.<ExampleRunnable>builder("cli")
          // Add a description
          .withDescription("A simple CLI with several commands available in groups")
          // We can enable command and option abbreviation, this allows users to only
          // type part of the group/command/option name provided that the portion they
          // type is unambiguous
          .withCommandAbbreviation()
          .withOptionAbbreviation();
```

Would need changing to the following:

```
CliBuilder<ExampleRunnable> builder 
  = Cli.<ExampleRunnable>builder("cli")
          // Add a description
          .withDescription("A simple CLI with several commands available in groups");
          
// We can enable command and option abbreviation, this allows users to only
// type part of the group/command/option name provided that the portion they
// type is unambiguous
builder.withParser()
          .withCommandAbbreviation()
          .withOptionAbbreviation();
```

You can now also define `GlobalRestriction`s using the `withRestriction()` or `withRestrictions()` methods on your `CliBuilder<T>`.  If you don't define any restrictions a default set that provides backwards behavioural compatibility with Airline 1 is used.  This default set can also be explicitly added by calling `withDefaultRestrictions()` on your builder.

## Defining CLIs via Annotation

Previously CLIs could only be defined via the `CliBuilder<T>`, Airline 2 introduces a new `@Cli`  annotation that can be used to define a CLI via a class annotation.

For example the same example as the above could now be defined like so:

    @Cli(name = "cli", 
             description = "A simple CLI with several commands available in groups", 
             parser = @Parser(allowOptionAbbreviation = true, allowCommandAbbreviation = true))
             
A class annotated in this way can be used to create a `Cli` instance like so:

    Cli<ExampleRunnable> cli = new Cli<ExampleRunnable>(AnnotatedClass.class);

## Defining Single Commands

Previously you could not specify parser options when defining single commands, you are now able to pass in a `ParserMetadata<T>` in order to specify parser options for single command parsing.

Alternatively you can add the `@Parser` annotation to your class and this will be automatically detected and used unless you pass a `ParserMetadata<T>` explicitly to the `singleCommand()` method.

You may now also pass in the `GlobalRestriction`s that should apply, if you don't define any restrictions a default set that provides backwards behavioural compatibility with Airline 1 is used

## Annotation Changes

All annotations are now located in the `com.github.rvesse.airline.annotations` package or in sub-packages thereof.

### Command Annotation Changes

Some of the extended help fields are no longer available directly on the `@Command` annotation and instead now have their own specific annotations:

- `examples` moved to `@Examples`
- `discussion` moved to `@Discussion`
- `exitCodes` and `exitCodeDescriptions` moved to `@ExitCodes`

#### Migrating discussion

For example discussion in Airline 1:

```
@Command(name = "example", discussion = { "This is some discussion" })
public class MyCommand {

}
```

Becomes the following in Airline 2:

```
@Command(name = "example")
@Discussion(paragraphs = { "This is some discussion" })
public class MyCommand {

}
```

### Option Annotation Changes

Some fields of the `@Option` annotation that defined restrictions on options have been removed, namely these are the `required`, `allowedValues` and `ignoreCase` attributes.  Option restrictions are now instead expressed with specific annotations such as `@Required`.

When overriding existing options restrictions are inherited from the parent unless new restriction(s) are defined on the overridden option.  If you simply want to remove all existing restrictions when overriding an option you can add the `@Unrestricted` annotation to the overridden option.

#### Migrating Required Options

For example a required option in Airline 1:

    @Option(name = "--example", arity = 1, required = true)
    private String example;

Becomes the following in Airline 2:

    @Option(name = "--example", arity = 1)
    @Required
    private String example;

#### Migrating Allowed Values

Similarly an option with allowed values in Airline 1:

    @Option(name = "--example", arity = 1, allowedValues = { "a", "b", "c" })
    private String example;
    
Becomes the following in Airline 2:

    @Option(name = "--example", arity = 1)
    @AllowedRawValues(allowedValues = { "a", "b", "c" })
    private String example;

#### Arguments Annotation Changes

Similar to `@Option` the `@Arguments` annotation has changed to have the `required` field removed, as with options you can now simply add the `@Required` annotation instead to indicate that arguments are required.

Arguments now allows for applying many different restrictions such as `@AllowedRawValues` to arguments.

## Exception Changes

All exceptions are now located in the `com.github.rvesse.airline.parser.errors` package of sub-packages there-of.

## Metadata Changes

### GlobalMetadata

The `GlobalMetadata` class is now a generic type i.e. `GlobalMetadata<T>` which provides better type safety but may require changing the type signatures of existing code you have written against Airline 1

A new `getRestrictions()` method provides access to `GlobalRestriction` instances which represent global restrictions on the CLI.

#### Accessing Parser Metadata

Parser settings were previously held directly on `GlobalMetadata`, they are now held in a `ParserMetadata<T>` class which is accessed via the `getParserConfiguration()` method on `GlobalMetadata<T>`

### CommandMetadata

The extended help details are no longer expressed directly as fields but as instances of `HelpSection`.  The `getHelpSections()` method provides access to the extended help sections present for a command.

### OptionMetadata

Restrictions on options such as `required` are no longer expressed directly as fields but as instances of `OptionRestriction`.  The `isRequired()` method on `OptionMetadata` will check if the `IsRequiredRestriction` is present for an option.

A `getRestrictions()` method provides access to all the restrictions that are present for an option.

### ArgumentsMetadata

Restrictions on arguments such as `required` are no longer expressed directly as fields but as instances of `ArgumentsRestriction`.  The `isRequired()` method on `ArgumentsMetadata`will check if the `IsRequiredRestriction` is present for the arguments.

A `getRestrictions()` method provides access to all the restrictions that are present for arguments.

## Restrictions

A new restrictions framework is introduced which allows much more complex restrictions to be specified and enforced.

A few examples are given in the following table, there are many more new restrictions supported by Airline 2 than just these shown here:

| Restriction Class | Applicability | Annotation Class | Restriction |
| -------------- | ---------------- | -------------- | --------------- |
| `IsRequiredRestriction` | Options and Arguments | `@Required` | Indicates that options/arguments are required |
| `AllowedRawValuesRestriction` | Options and Arguments | `@AllowedRawValues` | Indicates that the raw string values passed to options/arguments must be in a given set of values.  May be configured to use custom locale and case insensitive comparisons as desired |
| `RangeRestriction` | Options and Arguments | `@LongRange`, `@IntegerRange`, `@ShortRange`, `@ByteRange`, `@DoubleRange`, `@FloatRange` | Indicates that the instantiated values for options/arguments must fall within a range of values |
| `None` | Global, Options and Arguments | `@Unrestricted` | Indicates that no restrictions apply, primarily useful if you need to override restrictions inherited from a parent option |

Option and argument restrictions are automatically discovered by examining the annotations present on fields marked with `@Option` and `@Arguments`.  The `RestrictionRegistry` is used to map annotations into instances of `OptionRestriction` or `ArgumentsRestriction` as appropriate.

Global restrictions are defined when you create a `Cli` or `SingleCommand` instance, if you don't define any the default set which provides backwards compatibility with the global restrictions that applied in Airline 1 are used.

The `ParseRestrictionViolatedException` serves as the parent exception type for all exceptions pertaining to restriction violations.

### Custom Restrictions

You can create your own custom restrictions by doing the following:

- Create a class that implements `OptionRestriction` and/or `ArgumentsRestriction` and enforces your restriction.  Each restriction interface has a different method signature so a restriction can be implemented that applies to multiple things.
- Create an annotation that you will use to denote your restriction.  Remember to specify the retention for your annotation as `RUNTIME`
- Create a class that implements `OptionRestrictionFactory` and/or `ArgumentsRestrictionFactory`. This class does the work of converting your annotation into a restriction instance
- Register your restriction:  
  `RestrictionRegistry.addOptionRestriction(MyAnnotation.class, new MyRestrictionFactory());`

You can now apply your annotation to the option/arguments field you want to restrict.
  
## Extended Help

Extended help for commands is now specified via the `HelpSection` interface.  This interface allows for supporting a variety of different extended help formats and the built-in help generators will automatically include discovered help sections in their outputs.

This interface is itself derived from the more basic `HelpHint` interface.  This interface is typically implemented by other classes that serve some function as well as provided some extended help such as `OptionRestriction` implementations.  Again the built-in help generators will automatically include restrictions which provide help hints when formatting help for options and arguments.

Similar to restrictions extra help sections are automatically discovered by examining the annotations present on classes marked with `@Command`.  The `HelpSectionRegistry` is used to map annotations into instances of `HelpSection`.

### Help Section Inheritance

Help section annotations are automatically inherited so for example if all your commands have a common set of exit codes you could define these once as an annotation on a super-class and all derives command classes would automatically include that exit code section.  Where annotations for the same section (same section being determined by having the case-insensitive same title) are defined multiple times in the class hierarchy the definition lowest in the hierarchy wins i.e. a derived class can always override help sections inherited from their parents.

If you are extending an annotated class and want to hide a section that you would normally inherit you can do so by adding the `@HideSection(title = "foo")` annotation where `foo` is the title of the section you wish to hide.

### Custom Help Sections

You can create your own custom help section by doing the following:

- Create a class that implements `HelpSection`, you can derive from `BasicSection` to get yourself started
- Create an annotation that you will use to denote your help section.  Remember to specify the retention for your annotation as `RUNTIME`
- Create a class that implements `HelpSectionFactory`.  This class does the work of converting your annotation into a `HelpSection` instance
- Register your help section:  
    `HelpSectionRegistry.addFactory(MyAnnotation.class, new MySectionFactory());`

You can now apply your annotation to command classes for which you wish to provide extra help.

## Parsing Changes

### Option Parsing Styles

Airline 1 supported only three option parsing styles in the following preference order:

* Classic GNU GetOpt i.e. `-nFoo`
* Long GNU GetOpt  i.e. `--name=Foo`
* Whitespace separated ie. `--name Foo`

In this release option parsing is now separated out into its own extensible `OptionParser` interface that allows you to customise how options are parsed.  You can call `withOptionParser()` or `withOptionParsers()` on your `ParserBuilder<T>` to specify the option parsers to use in your desired preference order.

You can also call `withDefaultOptionParsers()` to start from the default Airline 1 setup and then add your own, or call this after adding your own to use the default setup as your fallback.  If you don't explicitly configure any option parsers then the default Airline 1 setup is used automatically.

A couple of other parsing styles are now available but not enabled by default:

* `ListValueOptionParser` - Supports parsing options specified in the form `--name a,b,c` where the values is split on a separator (default `,` but configurable as desired) and passed to the option.  This parser is strict in that it requires users invoking commands to provide list values with the correct number of entries present
* `MaybePairValueOptionParser` - Supports parsing options of arity 2 where they may be specified either as `--name a=b` or `--name a b` i.e. where the user may specify the values either separated by whitespace or some separator (default `=` but configurable as desired)

### Customisable Type Converters

In Airline 1 how raw string values were converted into the appropriate Java types was fixed, with Airline 2 you can now configure the `TypeConverter` instance to use for this as desired.  This can be set via the `withTypeConverter()` method on your `ParserBuilder<T>` instance.

Creating a custom `TypeConverter` implementation allows you complete control over how you want to convert raw string values into Java types.

### Alias chaining

In Airline 1 aliases could not refer to other aliases, in Airline 2 this behaviour may be enabled and it permits aliases to reference each other unless a circular reference is generated during alias resolution.

To enable this you can call `withAliasesChaining()` on a `ParserBuilder<T>` or create an appropriate `ParserMetadata<T>` instance yourself with the appropriate parameter set to `true`.
