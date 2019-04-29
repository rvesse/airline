---
layout: page
title: EndsWith Annotation
---

## `@EndsWith`

The `@EndsWith` annotation can be used to require that values given end with a given suffix.  For example:

```java
 @Option(name = "--images")
 @EndsWith(suffixes = { ".jpg", ".png", ".gif" })
 public List<String> images = new ArrayList<>();
```

Here the `--images` option requires that any image given ends with one of the file extensions `.jpg`, `.png` or `.gif`

### Case Sensitivity

Optionally you can make the prefixes case insensitive by setting the `ignoreCase` field to `true` and also specifying a `locale` if needed:

```java
 @Option(name = "--images")
 @EndsWith(ignoreCase = true, locale = "en", suffixes = { ".jpg", ".png", ".gif" })
 public List<String> images = new ArrayList<>();
```

When declared like this the suffixes and any values given for the option will first be lower cased in the given locale before being compared.

### Related Annotations

If you have a prefixes you need to enforce then use the [`@StartsWith`](starts-with.html) annotation.  For more complex string value enforcement you can also use the regular expression based [`@Pattern`](pattern.html) annotation.