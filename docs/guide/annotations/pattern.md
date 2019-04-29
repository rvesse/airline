---
layout: page
title: Pattern Annotation
---

## `@Pattern`

The `@Pattern` annotation may be applied to fields annotated with [`@Option`](option.html) and [`@Arguments`](arguments.html) to limit the values of the option to those matching a given regular expression e.g.

```java
@Option(name = "--tel")
@Pattern(pattern = "(\\+1-)?\\d{3}-\\d{3}-\\d{4}", 
         description = "Must provide a telephone number in standard US format e.g. +1-800-123-4567")
public String tel;
```

Here we require that the `--tel` option be a telephone number in the standard US format.

{% include alert.html %}
The use of the optional `description` field of the annotation allows us to supply a user friendly error message.  If this is not specified users will receive a potentially cryptic error message containing the regular expression they failed to match.
{% include end-alert.html %}

### Regular Expression Flags

You can use the `flags` field of the annotation to specify the flags that apply to the regular expression.  Airline uses the standard JVM `java.util.regex.Pattern` and so this field takes an integer which is interpreted as a bitmask of the various JVM constants e.g.

```java
@Option(name = "--other")
@Pattern(pattern = "foo|bar|foobar", flags = java.util.regex.Pattern.CASE_INSENSITIVE)
public String other;
```

Requires that the option meet the regular expression case insensitively i.e. `FOO` would be considered a valid value under this regular expression

### Related Annotations

To restrict values to specific sets of values it is likely more efficient to use the [`@AllowedRawValues`](allowed-raw-values.html) or [`@AllowedValues`](allowed-values.html) annotations.

To place simple string related restrictions some combination of [`@NotBlank`](not-blank.html), [`@NotEmpty`](not-empty.html), [`@MaxLength`](max-length.html) and [`@MinLength`](min-length.html) may also be useful.

For simple prefix/suffix constraints you can use the [`@StartsWith`](starts-with.html) and [`@EndsWith`](ends-with.html) annotations.