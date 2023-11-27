---
layout: page
title: Annotations
---

{% include toc.html %}

Airline uses a variety of different annotations to allow you to declaratively define various aspects of your CLIs.  This section of the user guide covers each annotation in-depth with examples of their practical usage.

## CLI Annotations

The following annotations are used to define the high level aspects of your CLIs:

- The [`@Command`](command.html) annotation defines classes as being commands
- The [`@Option`](option.html) annotation defines fields of a class as denoting options
- The [`@Arguments`](arguments.html) annotation defines a field of a class as denoting arguments
- The [`@Cli`](cli.html) annotation defines a class as being a CLI which consists of potentially many commands
    - The [`@Group`](group.html) annotation defines a command group within a CLI
    - The [`@Groups`](groups.html) annotation defines an `@Command` annotated class as belonging to some command groups within a CLI
- The [`@Parser`](parser.html) annotation defines the parser behaviour
    - The [`@Alias`](alias.html) annotation defines command aliases for a parser configuration
- The [`@DefaultOption`](default-option.html) annotation defines an `@Option` annotated field as being able to also be populated as if it were `@Arguments` annotated

## Restriction Annotations

The following annotations are used to define various restrictions on options and arguments that cause Airline to automatically enforce restrictions on their usage e.g. requiring an option take a value from a restricted set of values.

All these annotations are applied to fields that are annotated with [`@Option`](option.html) or [`@Arguments`](arguments.html) and are automatically discovered during meta-data extraction.  If you are overriding the definition of an option then restrictions are automatically inherited unless you specify new restrictions further as part of your override.  In the case where you wish to remove inherited restrictions you can use the special [`@Unrestricted`](unrestricted.html) annotation to indicate that.

{% include restrictions.md path="" %}

## Help Annotations

The following annotations are used to add additional help information to commands that may be consumed by the various
help generators provided by Airline by producing additional help sections.

All these annotations are added to an [`@Command`](command.html) annotated class or a parent class in the commands
hierarchy.  They are automatically discovered from a command classes inheritance hierarchy, where an annotation occurs
more than once the annotation specified furthest down the hierarchy is used.  This can be used to specify a default for
some help annotation with the option of overriding it if necessary, the special [`@HideSection`](hide-section.html)
annotation may be used to suppress an inherited help annotation.

{% include help-sections.md path="" %}