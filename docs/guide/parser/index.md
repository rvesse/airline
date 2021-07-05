---
layout: page 
title: Parsing
---

The core of Airline is its parser which takes in string arguments passed into the JVM and turns that into a command
instance with appropriately populated fields that your code can then use to actually run its intended function.

The parser is designed to be highly customisable allowing various aspects to be configured to suit your purposes. For
example the [User Defined Aliases](../practise/aliases.html) feature allows you to permit end users to introduce their
own command shortcuts. Customisation is usually done by defining an appropriate [`@Parser`](../annotations/parser.html)
annotation on your [`@Command`](../annotations/command.html) annotated class or via the `parserConfiguration` field of
your [`@Cli`](../annotations/cli.html) annotation.

Please refer to the [`@Parser`](../annotation/parser.html) documentation to understand the available fields for defining
your parser configuration. The rest of this page discusses the parser in general terms to help you understand how
Airline works in more detail.

### The Parsing Process

Once you have a `Cli` or `SingleCommand` instance and call the `parse()` or `parseWithResult()` methods a complex
process kicks off. Airline takes the provided input arguments and attempts to interpret them according to your CLIs
metadata and your parser configuration. Broadly speaking the process has the following steps:

1. Parse any globally scoped option (if a CLI)
1. Apply command aliases (if a CLI)
1. Try to parse a command group (if a CLI)
1. Try to parse any group scoped options (if a CLI)
1. Try to parse a command (if a CLI)
1. Try to parse command options
1. Try to parse arguments
1. Applies global restrictions and does final validation for option and argument restrictions
1. Returns the parsed command/parser result (depending on whether `parse()` or `parseWithResult()` was called)

Steps 1-5 only apply to CLIs, for single commands step 6 onwards apply. Each of these steps may in of itself have
multiple steps within it.

#### Error Handling

Error handling during the parsing process is described in the [Error Handling and
Exceptions](../practise/exceptions.html) document.

#### Command Aliases

Command aliases are a feature of Airline that allows for both developer and user-defined command shortcuts. Please take
a look at the [User Defined Aliases](../practise/aliases.html) document for more detail on this feature.

#### Command and Option Abbreviation

Airline optionally allows developers to enable a feature whereby command/group names and option names may be abbreviated
provided those abbreviations are unambiguous. This is described in more detail in the
[`@Parser`](../annotations/parser.html) annotation documentation.

#### Option Parsing

`OptionParser` implementations control how Airline parses inputs into options and their values. By default Airline
supports 3 common option styles with a further two that may be enabled if desired. The default parsers are as follows:

- {% include javadoc-ref.md class="StandardOptionParser" package="parser.options" %} - Simple white space separated
  option and values e.g. `--name value` sets the option `--name` to `value`
- {% include javadoc-ref.md class="LongGetOptParser" package="parser.options" %} - Long form GNU `getopt` style e.g.
  `--name=value` sets the option `--name` to `value`
- {% include javadoc-ref.md class="ClassicGetOptParser" package="parser.options" %} - Short form GNU `getopt` style e.g.
  `-n1` sets the option `-n` to `1`

Additionally the following alternative styles are supported:

- {% include javadoc-ref.md class="MaybePairValueOptionParser" package="parser.options" %} - Arity 2 options where the
  user may specify the values as whitespace/`=` separated e.g. `--name foo bar` and `--name foo=bar` are both acceptable
  and set the option `--name` to the values `foo` and `bar`
- {% include javadoc-ref.md class="ListValueOptionParser" package="parser.options" %} - Options that may be specified
  multiple times can be specified in a compact comma separated list form e.g. `--name foo,bar` sets the option `--name`
  to the values `foo` and `bar`

Users may create their own option parsers if desired as discussed on the [Custom Option Parsers](options.html) page.

##### `ClassicGetOptParser`

The classic `getopt` style parser only works with short form options i.e. those with a single character name e.g. `-a`.
However, it is particularly useful for flag options, those which do not take a value (arity 0), in that it allows users
to specify multiple options together.  For example the user input `-abcd` can be parsed into setting the `-a`, `-b`,
`-c` and `-d` flag (arity zero) options.

It also allows for a value to be provided to the final option in the sequence provided that it is an arity 1 option e.g.
`-abe value` sets the `-a` and `-b` options and sets the `-e` option to `value`.

{% include version="2.8.2" %}

As of 2.8.2 a bug fix to option parsers was made that made them less greedy when parsing options with values.  Prior to
this change they could incorrectly parse a token that was intended to represent an option as a value.  Considering the
above example if you had `-abc` where `-a` is a flag option and `-b` has arity 1 the parser would previously have set
the value of `-b` to be `c` even if you also had a `-c` option defined.  From that release onwards this would now result
in an error if `c` may itself be an option.

##### `GreedyClassicGetOptParser`

{% include version="2.8.2" %}

As of 2.8.2 a bug fix to option parsers was made that made them less greedy when parsing options with values, see the
example given for `ClassicGetOptParser` above.  While for most parsers this changes only impact was that parsing errors
were detected and reporting more reliably for the `ClassicGetOptParser` this could cause noticeable change in behaviour.
In order to allow users the choice to preseve the old behaviour if needed a new 
{% include javadoc-ref.md class="GreedyClassicGetOptParser" package="parser.options" %} was provided in this release.

Per the above example with the greedy parser `-abc` will set `-a` and the value of `-b` to `c` regardless of whether
there is a `-c` option defined.

##### `MaybePairValueOptionParser`

This parser is intended for arity 2 options where the user is providing key values pairs and so it may be natural for
the user to enter these as a single value separated by an `=` sign but equally permits whitespace separation.

So both `--conf key=value` and `--conf key value` are acceptable to set the `--conf` option to the values `key` and
`value`

##### `ListValueOptionParser`

This parser requires that the list of values provided be an exact multiple of the arity of the option being set.  So for
example if option `--conf` has arity 2 it would allow `--conf foo,bar` but not `--conf foo,bar,faz`

#### Value Conversion

Airline converts the raw string values to the appropriate strong types as described in the [Supported
Types](../practise/types.html) documentation. We make this process fully extensible as described in that document.

#### Global Restrictions

Global restrictions apply near the end of the passing process and so are able to inspect the final parser state and make
appropriate decisions. See the [Global Restrictions](../restrictions/global.html) documentation for more details.

#### Command Instantiation

Assuming a successful parse of the user arguments Airline instantiates an instance of your command class and populates
it's fields appropriately based upon the options and arguments that were seen. By default it uses the
`DefaultCommandFactory` to create an instance of your command class, this supports any command class that has a zero
arguments constructor.

Like other aspects this can be customised where necessary by setting the `commandFactory` field of your
[`@Parser`](../annotations/parser.html) annotation. This might be useful this can be useful if you want to integrate
your CLI with a dependency injection framework or if your command classes have a more complex constructor.

#### Metadata Introspection

If your command class declares fields with the Java `@Inject` annotation that have Airline metadata types -
`GlobalMetadata`, `CommandGroupMetadata` and `CommandMetadata` - then your command class instance will also have those
populated with the relevant parser metadata. This allows commands to introspect the CLI they belong to which can be
extremely useful for things like help commands.