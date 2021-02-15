---
layout: page
title: See Also Annotation
---

## `@SeeAlso`

{% include req-ver.md version="2.9.0" %}

The `@SeeAlso` annotation may be applied to classes and provides a see also section that will be included in [Help](../help/) as an additional section.

To use it simply add it like so:

```java
@SeeAlso(
     internalCommands = { "my-cli my-other-cmd"},
     externalCommands = { "grep" }
)
public class MyClass { }
```

There are two fields, `internalCommands` and `externalCommands` each take a string array where one/more relevant commands are listed.  The intent is that the former is used for referencing other commands within your CLI and the latter for commands provided by the OS, other software etc.

In terms of help rendering the practical difference is that commands listed in `internalCommands` array are listed prior to commands in the `externalCommands` array.