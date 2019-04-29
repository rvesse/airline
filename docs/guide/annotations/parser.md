---
layout: page
title: Parser Annotation
---

## `@Parser`

The `@Parser` annotation is applied to classes also annotated with `@Command` to configure the parser behaviour for that single command when parsers are created using `SingleCommand.singleCommand()`.

It may also be used as an argument to the [`@Cli`](cli.html) annotation in order to configure parser behaviour for a CLI via the `parserConfiguration` field of that annotation.

### Default Configuration

To get the default parser configuration you do not need to specify any fields on the annotation i.e. simply provide a basic annotation:

```java
@Parser()
```

This has the same effect as not specifying any parser configuration since in that case Airline will use its defaults which are sufficient for most basic uses.

### Option Parsers

[Option Parsers](../parser/options.html) are used to control how Airline parses options, Airline comes with a variety of implementations of these and there are several fields of the `@Parser` annotation that are used to configure these.

Firstly there are the `useDefaultOptionParsers` and `defaultParsersFirst` fields, the former indicates that the default set of option parsers should be used and the latter indicates whether the defaults are configured before/after any other option parsers that you might configure.  Both of these default to `true` so there is no need to specify either unless you want to disable the default parsers or use alternative parsers in preference to the defaults e.g.

```java
@Parser(useDefaultOptionParsers = true, 
        defaultParsersFirst = false)
```

The `optionParsers` field is used to provide an array of classes that implement the `OptionParser` interface and thus can be used to customise the option parsers used or to change the order in which they are used.

For example with the default parsers if we had an option that had arity 2 with the default parsers the user would be expected to specify it as `--option foo bar`.  However often in reality if we have an arity 2 argument we are expecting users to pass in a pair of values and it is quite common to do this as `--option foo=bar` which with the default configuration would be an error.  If we wanted to enable this style of parsing we could do so by enabling the built-in `MaybePairValueOptionParser` e.g.

```java
@Parser(defaultParsersFirst = false, 
        optionParsers = { MaybePairValueOptionParser.class })
```

### Type Converter

If we wanted to use a custom type converter as detailed in [Supported Types](../practise/types.html) then we can use the `typeConverter` field to do this e.g.

```java
@Parser(typeConverter = ExtendedTypeConverter.class)
```
The provided class must implement the `TypeConverter` interface.

#### Numeric Type Converter

If we wanted to customise how numeric values are converted as detailed in [Supported Types](../practise/types.html) then we can use the `numericTypeConverter` field to do this e.g.

```java
@Parser(numericTypeConverter = KiloAs1000.class)
```
The provided class must implement the `NumericTypeConverter` interface.

In this example the numeric type converter is set to use the built-in `KiloAs1000` numeric converter which allows for users to abbreviate numbers e.g. `4k` would be converted to `4000`

### Boolean Flag Negation

Often when using flag arguments i.e. those with `boolean` type and arity zero it is useful to allow users to specify both a positive and negative version of the flag.  For example we might want to have `--overwrite` and `--no-overwrite`.  In order to do this we need to define an option that has both versions present in its `name` array and set a `flagNegationPrefix` on our parser:

```java
@Parser(flagNegationPrefix = "--no-")
```

Now if a boolean arity zero option is used and its name starts with `--no-` then the parser will set its value to `false`, if the name does not start with the configured prefix then it will be set to `true` as usual.

This behaviour is off by default and must be explicitly enabled.

### Command Factory

If we wanted to use a custom command factory then we can use the `commandFactory` field to do this e.g.

```java
@Parser(commandFactory = ExtendedCommandFactory.class)
```

### Option and Command Abbreviation

Option and command abbreviation is an advanced feature of the Airline parser that allows users to avoid typing the full option and/or command names provided that the portion they type is unambiguous.

For example say we had a CLI which contained a `remove` and a `replace` command, by default users always have to type the full command name.  However if we enable `allowCommandAbbreviation` then users only need to type enough characters to unambiguously identify the command e.g.

```java
@Parser(allowCommandAbbreviation = true)
```

With that enabled a user can now type `rem` for `remove` and `rep` for `replace`, since the abbreviation must be unambiguous typing `re` would not be acceptable as it could refer to either command.

Similarly we can allow the same for option names via the `allowOptionAbbreviation`, option abbreviation is slightly more restrictive in that at least 3 characters must be typed i.e. short names such as `-a` can never be abbreviated.

### Arguments Separator

By default Airline supports a separator of `--` which can be used to separate options from arguments where arguments may be misinterpreted as options.  After this separator is seen any further command line arguments are treated as arguments and not as options.

If you wish to customise the separator then you can this like so:

```java
@Parser(argumentsSeparator = "@@")
```

Would instead use `@@` as the arguments separator.

### Aliases

Airline supports a [User Defined Aliases](../practise/aliases.html) system which allows for users to define custom aliases for use with your Airline powered CLIs.

To specify where to get user defined aliases from you use some combination of the `userAliasesFile`, `userAliasesSearchLocation`, and the `userAliasesPrefix` fields e.g.

```java
@Parser(userAliasesFile = "example.config",
        userAliasesSearchLocation = { "~/example/" },
        userAliasesPrefix = "alias.")
```

Here we define that aliases will be defined in a file `example.config` which should be found under `~/example` (where `~` is treated as special value for users home directory).

The treatment of special values is controlled by the choice of [Resource Locators](../practise/resource-locators.html) which can be changed via the `userAliasLocators` property.  The use of the default setup, which includes support for `~/` as a reference to the users home directory, is controlled via the `useDefaultAliasLocators` field and `defaultAliasLocatorsFirst`.  For example if we wished to enable use of an environment variable to locate user aliases we could do the following:

```java
@Parser(userAliasesFile = "example.config",
        userAliasesSearchLocation = { "${EXAMPLE_CONFIG_DIR}", "~/example/" },
        userAliasesPrefix = "alias.",
        defaultAliasLocatorsFirst = false,
        useDefaultAliasLocators = true,
        userAliasLocators = { EnvVarLocator.class })
```

Here we add a new search location `${EXAMPLE_CONFIG_DIR}` and we configure the use of the `EnvVarLocator` which understands how to resolve that location.  We prefer our configured locators so the environment variable location, if set and resolved, will be the first used.

#### Alias Definition Controls

If you are using aliases then there are two remaining options that you may also be interested in.  The `aliasesMayChain` field which defaults to `false` controls whether user defined aliases are allowed to reference each other i.e. whether users can define aliases in terms or other aliases.  When set to `true` then aliases may be defined in terms of each other provided that a circular reference does not exist.

```java
@Parser(userAliasesFile = "example.config",
        userAliasesSearchLocation = { "~/example/" },
        userAliasesPrefix = "alias.",
        aliasesMayChain = true)
```

You can use the `aliasesOverrideBuiltIns` field to control whether user defined aliases are allowed to override built-in commands and this also defaults to false.  To understand this consider that you had defined a command `test` and then a user defines an alias `test` - which version should Airline use?  By default Airline will defer to the built-in command, however in some cases you may want to allow the users alias to take precedence in which case you can set this field to `true` e.g.

```java
@Parser(userAliasesFile = "example.config",
        userAliasesSearchLocation = { "~/example/" },
        userAliasesPrefix = "alias.",
        aliasesOverrideBuiltIns = true)
```

Finally if you want to provide some pre-defined aliases you can do so via the `aliases` property which takes an array of [`@Alias`](alias.html) annotations e.g.

```java
@Parser(aliases = {
  @Alias(name = "rem", 
         arguments = { "remove" })
})
```

Here we define a single alias `rem` which invokes the `remove` command.