---
layout: page
title: AllowedEnumValues Annotation
---

## `@AllowedEnumValues`

The `@AllowedEnumValues` annotation may be applied to fields annotated with [`@Option`](option.html) and [`@Arguments`](arguments.html) to limit the set of values that an option may be used with e.g.

```java
public enum VerbosityLevels {
  TRACE,
  DEBUG,
  INFO,
  WARNING,
  ERROR,
  FATAL
}

@Option(name = { "-v", "--verbosity" }, arity = 1, title = "Level", description = "Sets the desired verbosity")
@AllowedEnumValues(VerbosityLevels.class)
public VerbosityLevels verbosity = VerbosityLevels.INFO;
```
This specifies that the `--verbosity` option only allows the values from the `VerbosityLevels` enum to be specified by the user.  Any other value will be rejected.

Note that the restriction applies to the raw string provided to the parser **prior** to converting it to the target type.  So in the above example the values would be checked prior to parsing them as enum values.

### Related Annotations

If there may be more than one way that the same value may be specified then it may be useful to use the [`@AllowedValues`](allowed-values.html) annotation instead.  If you have a list of acceptable raw string values then use the [`@AllowedRawValues`](allowed-raw-values.html) annotation instead.

For more complex value restrictions a regular expression based restriction using [`@Pattern`](pattern.html) might be appropriate.