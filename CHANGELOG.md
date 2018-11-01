# Airline - Change Log

## 2.7.0

- Bug Fixes
    - `EnvVarLocator` and `JvmSystemPropertyLocator` could resolve locations incorrectly if some placeholders were undefined
    - Fixes some corner cases with `@FloatRange` and `@DoubleRange` underlying implementation
    - Fix possible NPE in maven plugin (#45)
- Restriction Improvements
    - Added `@File` and `@Directory` as more explicit variants of `@Path` (#73)
    - Added `@Negative` and `@Positive` for numerics (#73)
    - Added `@LengthRange` and `@ExactLength` for strings (#73)
    - Added `@AllowedEnumValues` as a simpler way of specifying `@AllowedRawValues` on enum typed fields (#73)
    - Added `@StartsWith` and `@EndsWith` for enforcing prefixes/suffixes on strings (#73)

## 2.6.0

- Core Improvements
    - Allow creating a `Cli` instance with an explicit `ParserMetadata` for situations where parser configuration cannot be specified via annotation e.g. using dynamically determined paths for user aliases
    - Abstract direct usage of `System.out` via `Channels` factory interface to allow the library to be used in environments that are not consoles (#78) - Thanks to [sdorra](http://github.com/sdorra/)
- Parser Improvements
    - Allow `./` as a user alias search location resolved as the current working directory
    - Allow extending how user alias search locations are resolved to support custom behaviours via new `ResourceLocator` interface
    - Add ability to specify a force prefix for aliases which allows alias definitions to override built-ins while still being able to invoke those built-ins (#72)
    - Add ability to specify default values for positional parameters in alias definitions (#69)
- Help Improvements
     - Remove deprecated methods from `CommandUsageGenerator` interface and add new `usage()` overload for more easily generating usage for single commands
- Annotation Improvements
    - Allow specifying global restriction annotations directly on classes used with `SingleCommand`
    - `@Unrestricted` can now be applied directly to classes to specify no global restrictions on single commands and CLIs
    - `@Version` can now use `ResourceLocator` to modify search locations
    - Help Sections annotations can now be placed onto `@Cli` annotated classes and will be inherited by all commands defined for the CLI.  Commands can still override help sections as desired. (#59)
        - CLI defined help sections are included by CLI help generators (#57)
- Build Improvements
    - Now possible to build with JDK 9 and JDK 10.  Note that we still compile for JDK 7 compatibility
    - Dependencies
        - Upgraded Apache Commons Lang to 3.7 (#82)
        - Upgraded Apache Commons Collections to 4.2 (#82)

## 2.5.0

- *NEW* - Added `airline-maven-plugin` that provides two goals (#45)
    - A `generate` goal for generating help for your commands and CLIs during a Maven build
    - A `validate` goal for validating that your `@Command` and `@Cli` annotated classes have valid annotations that Airline can generate a CLI from

## 2.4.1

- Parser Changes
    - `ListValuesOptionParser` now accepts any number of values that are a multiple of the arity of the option so can now be used for options that wish to allow multiple values in a single list
- Documentation Improvements
    - Documentation website now located in `docs/` subfolder of `master` branch (#35)
    - New `airline-docs-VERSION-offline-site.zip` and `.tar.gz` artifacts contain a static build of the documentation website for offline reference (#70)
- Core Improvements
    - Removed some unnecessary helper methods from `AirlineUtils` in favour of JDK built-ins (#64)
- Help Improvements
    - Fixed some issues with UsagePrinter not correctly respecting configured column limits in some cases

## 2.4.0

- Bug Fixes
    - Fix bug in `@MutuallyExclusiveWith` error message (#66) - Thanks to [GTarkin](http://github/com/GTarkin/) for reporting
    - Fix bug with `flagNegationPrefix` specified via annotation - Thanks to [rkhaja](https://github.com/rkhaja) for the fix
    - Add missing Javadoc

## 2.3.0

- Parsing Changes
   - New `ParserErrorHandler` interface for handling non-fatal parser errors (#53)
        - `FailFast` - Classic behaviour, throw as soon as error encountered
        - `FailAll` - Collect and aggregate non-fatal errors and throw as single error
        - `CollectAll` - Collect all non-fatal errors
     - New `ParseResult<T>` interface for representing parsing result and accessing success status and errors (if any)
     - Support for customising type converter on a per-option/arguments basis (#54)
 - Help Changes
      - Added `showHelpIfErrors()` method to `HelpOption` for use with error handlers which collect errors e.g. `CollectAll` (#53)
 - Annotation Changes
      - New `errorHandler` field on `@Parser` annotation for specifying error handler, defaults to `FailFast` for backwards compatible behaviour (#53)
      - New `flagNegationPrefix` field on `@Parser` to allow specifying that if a flag option (zero arity option) name starts with that prefix the value should be set to `false` as opposed to setting flags to `true` as is the normal behaviour (#63)
      - New `description` field on `@Pattern` to provide human readable explanation of regular expression restriction intent (#48)
      - Noted in Javadoc that `@Version` supports both properties and Manifest files as data sources (#56)
      - Use new `typeConverterProvider` field on `@Option`/`@Arguments` to customise the type converter for a field (#54)
          - Variety of built in converters provided for supporting more complex number formats
              - Alternative Bases: `Hexadecimal`, `Octal` and `Binary`
              - Abbreviations: `KiloAs1000`, `KiloAs1024`

## 2.2.0

- Bug Fixes
    - Fixed a possible infinite loop in parsing when a group contains both sub-groups and commands (#55)
    - Fixed possible non-deterministic ordering of option and arguments help hints (#50)
- Annotation Changes
    - Removed deprecated `arity` from `@Arguments`, the `@MaxOccurrences` restriction annotation (and its related annotations) provide for much finer grained control than this property did
    - Global restrictions can now be specified via annotations
- Help Changes
    - Removed the deprecated RONN help generators (use `airline-help-man` or `airline-help-markdown` instead)
        - This removes the `airline-help-ronn` module (**Breaking**)
    - New `@Version` annotation for embedding version information into generated help where the version information is pulled from properties files in your JARs or on the local file system
    - Multi-page help generators can now be configured with a base directory to control where the per-command files are created
- Restriction Changes
    - New `GlobalRestrictionsFactory` interface and corresponding registry in `RestrictionsRegistry`
        - Uses `ServiceLoader` for automatic discovery, provide a `META-INF/services/com.github.rvesse.airline.restrictions.factories.GlobalRestrictionFactory` file to specify global restriction factories
    - Global restrictions can now be specified by adding annotations to `@Cli` annotated classes
    - Improved `OptionRestriction` and `ArgumentsRestriction` interfaces
        - Old `postValidate()` method becomes `finalValidate()`
        - Revised `postValidate()` method with better type signature that takes the converted value
    - Support partial restrictions i.e. restrictions only apply to some of the values for `@Option` or `@Arguments` fields
        - Specified via `@Partial`/`@Partials` annotations in addition 
- Parser Changes
    - Improve how restrictions for options and arguments are enforced
    - Options must always receive precisely their `arity` in values or a `ParseOptionMissingValueException` is thrown (at least with the built-in parsers)

## 2.1.1

- Bug Fixes
    - `ManCommandUsageGenerator` could incorrectly indent the arguments portion of the options list if both visible and hidden options are present
    - Man help output could fail in the options section if there were no visible options but some hidden options present
- Annotation Changes
   - Marked defunct fields of `@Arguments` annotation as deprecated
   - Marked various annotations as `@Documented` so they are included in JavaDoc output
   - All fields of `@Alias` annotation are now required
- Parser Changes
    - Made internal helper class `ConvertResult` public to make it easier to extend the `DefaultTypeConverter`
- Documentation Improvements
    - New website in progress at [http://rvesse.github.io/airline/](http://rvesse.github.io/airline/)

## 2.1.0

- Module Additions and Changes
    - New `airline-help-man` module
        - `ManSections` moved into this module (**Breaking**)
    - RONN help generators moved to `airline-help-ronn` module (**Breaking**)
        - All RONN generators are marked as `@deprecated` since they are superseded by the Man and Markdown format generators
    - HTML help generators moved to `airline-help-html` module (**Breaking**)
    - Bash help generators moved to `airline-help-bash` module (**Breaking**)
        - `CompletionBehaviour` moved into this module (**Breaking**)
     - New `airline-help-markdown` module for generating Markdown help
- Help Improvements
    - New direct man page generation via `ManCommandUsageGenerator`, `ManGlobalUsageGenerator` and `ManMultiPageGlobalUsageGenerator` provided in the `airline-help-man`
        - These are intended to replace use of the existing RONN generators for generating Man pages
   - New direct Markdown generation via `MarkdownCommandUsageGenerator`, `MarkdownGlobalUsageGenerator` and `MarkdownMultiPageGlobalUsageGenerator`
        -  These are intended to replace use of the existing RONN generators for generating Markdown help
    - `CommandUsageGenerator` has new overloads that take a `ParserMetadata<T>` object, old overloads are deprecated in favour of these.  This allows generators to produce more accurate help in some circumstances.
    - Switched to using `ServiceLoader` to discover available help section factories avoiding the need to explicitly register these with `HelpSectionRegistry`
        - Provide a `META-INF/services/com.github.rvesse.airline.help.sections.factories.HelpSectionFactory` file to specify help section factories
        - `HelpSectionRegistry` moved to package `com.github.rvesse.airline.help.sections.factories` (**Breaking**)
        - `HelpSectionFactory` now required to implement a `supportedAnnotations()` method to declare the annotations it can turn into `HelpSection` instances (**Breaking**)
    - New `@Copyright` and `@License` annotations for adding copyright and license statements to help
    - New `@ProseSection` annotation for adding a custom prose section to help
    - Improved presentation of help hint for options/arguments annotated with `@Port`
    - Improved presentation of help hints for numeric ranges, they no longer show min/max if those are set to the min/max of their respective numeric types and the range is inclusive
    - Improved presentation of help hint for single value ranges
 - Metadata Changes
     - User alias configuration are now preserved on a `UserAliasesSource<T>` class which is accessible via `ParserMetadata<T>.getUserAliasesSource()`
     - `completionBehaviour` and `completionCommand` are no longer fields on the `@Option` and `@Arguments` annotation.  Instead use the `@BashCompletion` annotation from the `airline-help-bash` module
- Restriction Improvements
    - New `@Path` restriction for specifying that an arguments value is a path to a file/directory and applying restrictions on the path/file that should be enforced e.g. must exist, readable etc.
    - New `@MutuallyExclusiveWith` restriction for specifying that only one of some set of options may be specified but that those options are optional, this is thus a less restrictive version of `@RequireOnlyOne`
    - Switched to using `ServiceLoader` to discover available restriction factories avoiding the need to explicitly register these with the `RestrictionRegistry`
        - Factories are now required to implement a method indicating what annotations they can translate into restrictions (**Breaking**)
        - Provide a `META-INF/services/com.github.rvesse.airline.restrictions.factories.OptionRestrictionFactory` file to specify option restriction factories
        - Provide a `META-INF/services/com.github.rvesse.airline.restrictions.factories.ArgumentsRestrictionFactory` file to specify argument restriction factories
- Bug Fixes
    - `@Port` restriction would incorrectly reject valid values when applies to `@Arguments` annotated fields
    - Restrictions could report incorrect argument title when applied to arguments with multiple titles

## 2.0.1

- Bug Fixes
    - Fix `@MinLength` being an exclusive restriction i.e. value had to be greater than given length when intention was that value should be at least the given length
- Documentation
    - Add missing Javadocs to new annotations

## 2.0.0

2.0.0 represents substantial breaking changes over 1.x which were made to make the library more configurable and extensible.  We **strongly** recommend reading the included `Migrating.md` for notes on how to migrate existing Airline powered CLIs forward.

- Dependency Changes
    - Removed Guava
    - Added Apache Commons Collections 4
- Builder improvements
    - All parser related options on `CliBuilder` are now moved to `ParserBuilder` which is access by calling `.withParser()` on the `CliBuilder` instance
    - Groups now support sub-groups and `GroupBuilder` provides `withSubGroup()` and `getSubGroup()` for working with these
- Annotation Changes
    - Various fields were removed from existing annotations in favour of moving them to separate annotations
        - `@Command` removes `examples`, `discussion`, `exitCodes` and `exitCodeDescriptions`
        - `@Option` removes `required`, `allowedValues` and `ignoreCase`
        - `@Group` can now be used to create sub-groups by inserting spaces into group names e.g. `@Group(name = "foo bar")` creates a group `foo` with a sub-group `bar` and applies any other configuration given to the sub-group
        - New `@Cli` annotation can be used to define a CLI entirely declaratively
        - New `@Parser` annotation can be used to customise parser for CLIs created with `SingleCommand` or as a field on a `@Cli` annotation
        - `@Arguments` removes `required`
    - New annotations for adding extended help to commands
        - `@Discussion` to add discussion, this replaces the `discussion` field of the `@Command` annotation
        - `@Examples` to add examples, this replaces the `examples` field of the `@Command annotation
        - `@ExitCodes` to add exit codes, this replaces the `exitCodes` and `exitCodeDescriptions` fields of the 
        - Custom extended help sections can be created and registered such that they are automatically discovered by Airline
    - New restriction annotations for expressing restrictions on options and arguments
        - `@Required` to indicate required options/arguments, this replaces the `required` field on the `@Option` and `@Arguments` annotations
        - New `@RequiredOnlyIf` for conditionally requiring an option if another option is present
        - New `@RequireSome` for requiring at least one from some set of options
        - New `@RequireOnlyOne` for requiring exactly one from some set of options
        - `@AllowedRawValues` for limiting the raw string values an option can receive, this replaces the `allowedValues` and `ignoreCase` fields on the `@Option` annotation
        - New `@AllowedValues` for limiting the converted values an option can receive
        - New `@MaxLength` and `@MinLength` for limiting the length of the raw string values an option can receive
        - New `@MinOccurrences`, `@MaxOccurrences` and `@Once` for limiting how many times an option can appear
        - New `@LongRange`, `@IntegerRange`, `@ShortRange`, `@ByteRange`, `@DoubleRange`, `@FloatRange` and `@LexicalRange` for indicating that arguments once converted to the appropriate value type must fall within a given range
        - New `@Port` for restricting options to some port range(s)
        - New `@NotEmpty` and `@NotBlank` for requiring the raw string values be non-empty or non-blank (not all whitespace)
        - New `@Pattern` for requiring that the raw string values conform to some regular expression
        - New `@Unrestricted` to indicate that restrictions inherited from an overridden option should be removed
        - Custom restrictions can be created and registered such that they are automatically enforced by Airline
- Parser Improvements
     - `TypeConverter` is now an interface and configurable i.e. allows you to control how Airline turns raw string values into Java objects
     - Option parsing styles are now fully configurable (default behaviour remains as 1.x which uses the first 3 styles):
         - Classic GNU Get Opt Style
         - Long GNU Get Opt Style
         - Standard whitespace separated style
         - List value style i.e. `--name a,b,c` for higher arity options
         - Pair value style i.e. `--name a=b` for arity 2 options
         - Users can define and register their own custom option parsers as desired
     - Alias Improvements
         - Can now support optional alias chaining i.e. aliases can reference other aliases
- Metadata Improvements
     - Parsing specific metadata moved to `ParserMetadata` class which is accessible via `GlobalMetadata.getParserConfiguration()`
     - `GlobalMetadata` is now a generic class taking the command type as the type parameter
- Help Improvements
    - New `HelpHint` interface which is used by restrictions to provide help
    - New `HelpSection` interface for adding custom help sections to commands
    - More advanced and flexible formatting of extra help hints and sections in all existing generators
    - Help supports providing help for sub-groups, help for groups will include information about available sub-groups

## 1.0.2

- Various minor improvements from Christian Raedel
    - Long style option parser `--name=value` now also accepts colon separated values e.g. `--name:value`
    - `allowedValues` on `@Option` can now be set to use case insensitive comparison
    - `TrueColor` can be instantiated from a hex value

## 1.0.1

- Fix regression in `RonnCommandUsageGenerator`

## 1.0.0

- Code Structure Refactoring
    - Root package is now `com.github.rvesse.airline`
    - Main library now lives under `lib/` in source control
    - New examples module under `examples/` in source control
- Annotation Improvements
    - `@Group` can now be marked as `hidden`
    - `discussion` parameter of `@Command` is now a `String[]` rather than a single string making it easier to specify long descriptions
- CLI Improvements
    - User defined command aliases are now supported
    - CLI builder classes are now public and have their own `builder` package
    - Parser Improvements
        - All parser errors in the `parser` package have public constructors
- Help Improvements
    - Help generators can now optionally display hidden commands and options
    - `Help.help()` static improved:
         - Respects command abbreviation when enabled
         - Additional overloads for enabled output of hidden commands and options
    - Bash completion fixes and improvements:
        - Default command and default group command completion now included
        - Functions for default group commands (i.e. top level commands) are no longer missing if there are also groups present
    - Fixed a bug with hidden options not displaying in synopsis even when including hidden options was enabled
    - `HelpOption` improved:
        - Generated help will include program and group name where applicable
        - It can now be used to show help with an arbitrary usage generator
        - `showHelpIfRequested()` guarantees to only display help once
        - `showHelp()` can be used to display help regardless
    - Command usage generators now print each item in the discussion as a separate paragraph
- IO Improvements
    - New `com.github.rvesse.airline.io` package with useful helper stuff for doing advanced console IO
    - Support for colorised output streams and writers:
        - Basic ANSI Colors (8 Colors, normal and bright variants)
        - 256 Colors (Basic ANSI Colors plus 3 colour palettes plus grayscale palette) - See [color chart](https://camo.githubusercontent.com/6378594a85c578517c5a4e494789bd4d66c9e46b/68747470733a2f2f7261772e6769746875622e636f6d2f666f697a652f676f2e7367722f6d61737465722f787465726d5f636f6c6f725f63686172742e706e67) for more detail
        - True Color (24 bit colour i.e. 16 million colours) - Many terminals may not support this mode
        - Includes basic text decorations e.g. bold, underline

## 0.9.2

- Annotation Improvements
    - `Arguments` improvements
        - An `arity` can now be specified to set the maximum arity for arguments and throws a `ParseTooManyArgumentsException` if too many arguments are seen
    - New `DefaultOption` annotation
        - Allows a field already annotated with `@Option` to also be marked as the default option under certain circumstances.  This allows one option to be specified arguments style i.e. the `-n` or `--name` can be omitted provided only one field is annotated this way, it has an arity of 1 and no fields are annotated with `@Arguments`
- Bash Completion Fixes
    - Bash Completion Scripts for CLI that use groups are now valid Bash and function correctly 
    - Generated scripts use more unique function names to avoid clashes between different airline generated completion scripts
- CLI Improvements
    - CLIs can now have command abbreviation enabled which allows users to only type part of the command name provided that the portion typed is unambiguous
    - CLIs can now have option abbreviation enabled which allows users to only type part of the option name provided that the portion typed is unambiguous
- Package Refactoring
    - Parser functionality moved into `io.airlift.airline.parser` package

## 0.9.1

- Help improvements
    - Refactored various usage generators to make them easier to extend

## 0.9

- Annotation improvements
    - `Command` improvements
        - Added `exitCodes` and `exitDescriptions` for declaring the exit codes a command can produce and their meanings
- Help improvements
    - All command help generator now include exit code information if declared for a command via the `exitCodes` and `exitDescriptions` properties
    - Add new `RonnMultiPageGlobalUsageGenerator` which generates a top level overview RONN page and then individual RONN files for each sub-command
    - Fix broken sort order of commands and groups in various help generator implementations
    - Add documentation of `allowedValues` to all help generators
    - Fix typo in presentation of `--` option in various help generators
    - Fix presentation of `examples` to avoid need for users to use markup in their annotations

## 0.8

- Forked from upstream repository
    - Group ID changed to `com.github.rvesse`
    - Currently package names remain same as upstream (bar the help system) to make it easy to migrate existing apps to this fork.  
    - **Please note** that future releases will change the package names to align with the Group ID.
- Annotation improvements
    - `Option` improvements:
        - `allowedValues` properties is actually enforced and produces `ParseOptionIllegalValueException` if an invalid value is received
        - New `override` and `sealed` properties allowing derived commands to change some properties of the annotation
        - Overridden options may change the type when it is a narrowing conversion
        - New `completionBehaviour` and `completionCommand` properties allowing defining behaviours for the purposes of completion script generators
    - `Arguments` improvements
        - New `completionBehaviour` and `completionCommand` properties allowing defining behaviours for the purposes of completion script generators
    - `Group` annotation for specifying groups
    - `Command` annotation supports discussion and examples
- Help system improvements
    - Most portions moved to `io.airlift.airline.help` package
    - Help printing respects new lines allowing them to be used in longer descriptions
    - Support for additional examples and discussion sections in command help
    - Abstracted out help generation into interfaces with multiple concrete implementations:
        - Command Line (the existing help system)
        - [Ronn](http://rtomayko.github.io/ronn/)
        - HTML
        - Bash auto-completion script
- Support for Command Factories