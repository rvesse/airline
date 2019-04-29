---
layout: page
title: StartsWith Annotation
---

## `@StartsWith`

The `@StartsWith` annotation can be used to require that values given start with a given prefix.  For example:

```java
@Option(name = "--urls")
@StartsWith(prefixes = { "http", "https", "ftp" })
public List<String> urls = new ArrayList<>();
```
Here the `--urls` option requires that any URL given starts with one of `http`, `https` or `ftp`

### Case Sensitivity

Optionally you can make the prefixes case insensitive by setting the `ignoreCase` field to `true` and also specifying a `locale` if needed:

```java
@Option(name = "--urls")
@StartsWith(ignoreCase = true, locale = "en", prefixes = { "http", "https", "ftp" })
public List<String> urls = new ArrayList<>();
```

When declared like this the prefixes and any values given for the option will first be lower cased in the given locale before being compared.

### Related Annotations

If you have a suffixes you need to enforce then use the [`@EndsWith`](ends-with.html) annotation.  For more complex string value enforcement you can also use the regular expression based [`@Pattern`](pattern.html) annotation.