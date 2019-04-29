---
layout: page
title: AllowedValues Annotation
---

## `@AllowedValues`

The `@AllowedValues` annotation may be applied to fields annotated with [`@Option`](option.html) and [`@Arguments`](arguments.html) to limit the set of values that an option may be used with e.g.

```java
@Option(name = { "-v", "--verbosity" }, arity = 1, title = "Level", description = "Sets the desired verbosity")
@AllowedValues(allowedValues = { "1", "2", "3" })
public int verbosity = 1;
```
This specifies that the `--verbosity` option only allows the values `1`, `2` and `3` to be specified by the user.  Any other value will be rejected.

Note that the restriction applies to the option value **after** the parser converts it to the target type.  So in the above example the values given in the annotation would be converted to integers and checked against integers converted from the input to the parser.

### Related Annotations

If there is only one way that a given value may be specified then it may be simpler and more efficient to use the [`@AllowedRawValues`](allowed-raw-values.html) annotation instead.

If the acceptable values come from an `enum` you can simplify your code by using the [`@AllowedEnumValues`](allowed-enum-values.html) annotation.

For more complex value restrictions a regular expression based restriction using [`@Pattern`](pattern.html) might be appropriate.