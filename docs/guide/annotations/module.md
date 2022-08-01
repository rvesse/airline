---
layout: page
title: AirlineModule Annotation
---

{% include req-ver.md version="2.9.0" %}

## `@AirlineModule`

The `@AirlineModule` annotation is applied to fields to indicate that their value types should also be inspected to
discover further Airline metadata e.g. [`@Option`](option.html) and [`@Arguments`](arguments.html) annotated fields
that are used to define a command.

This is used to enable Airline's [Composition](../practise/oop.html#composition) mechanism that allows common 
definitions of options and arguments to be shared across multiple command classes without using inheritance.

For a practical example of this annotation in use see the [`HelpOption`](../help/index.html#helpoption) that is used
to provide a `-h`/`--help` option to other commands.