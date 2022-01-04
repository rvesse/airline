---
layout: page
title: NoOptionLikeValues Annotation
---

## `@NoOptionLikeValues`

The `@NoOptionLikeValues` annotation can be used to require that values don't look like options.  Note that the 
Airline parsers are non-greedy so won't consume values that are clearly options.  However, sometimes users may make
typos when providing options in which case those values would get consumed by other `@Option`/`@Arguments` 
annotated fields rather than any kind of error being issued.

```java
@Arguments(title = "Arg")
@NoOptionLikeValues
public List<String> args = new ArrayList<>();
```

Here if a user tries to pass `--foo` into our command, and that isn't an option, it would result in an error.  If 
`@NoOptionLikeValues` is not applied then `--foo` would have been captured as an argument.

### Customising Option Prefixes

If you are customising the option parsers, or defining options using some non-standard prefix format, then you can 
choose to customise the option prefixes that are used to detect values that look like options e.g.

```java
@Arguments(title = "Arg")
@NoOptionLikeValues(optionPrefixes = { "/" })
public List<String> args = new ArrayList<>();
```

Here values starting with `/` would be rejected as being option like.

### Related Annotations

If you have a prefixes you need to enforce, rather than reject, then use the [`@StartsWith`](starts-with.html) 
annotation.  
For more complex string value enforcement you can also use the regular expression based [`@Pattern`](pattern.html)
annotation.
