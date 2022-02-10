---
layout: page 
title: NoOptionLikeValues Annotation
---

## `@NoOptionLikeValues`

The `@NoOptionLikeValues` annotation can be used to require that values don't look like options. Note that the Airline
parsers are non-greedy so won't consume values that are clearly options. However, sometimes users may make typos when
providing options in which case those values would get consumed by other `@Option`/`@Arguments`
annotated fields rather than any kind of error being issued.

```java
@Arguments(title = "Arg")
@NoOptionLikeValues
public List<String> args=new ArrayList<>();
```

Here if a user tries to pass `--foo` into our command, and that isn't an option, it would result in an error. If
`@NoOptionLikeValues` is not applied then `--foo` would have been captured as an argument.

### Customising Option Prefixes

If you are customising the option parsers, or defining options using some non-standard prefix format, then you can
choose to customise the option prefixes that are used to detect values that look like options e.g.

```java
@Arguments(title = "Arg")
@NoOptionLikeValues(optionPrefixes = {"/"})
public List<String> args=new ArrayList<>();
```

Here values starting with `/` would be rejected as being option like.

### Use as a Global Restriction

Note that using this restriction **MAY NOT** make sense for certain option types, for example consider the following
option definition:

```java
@Option(name = "--limit",
        title = "Limit",
        description = "Specifies a limit on results, a negative value is treated as unlimited.")
@NoOptionLikeValues
public long limit=-1;
```

Here the intent is to allow negative values to be passed to this option, however since a negative number will start with
the `-` character it would always be treated as an option like value and rejected by this restriction. This is because
when applied to an `@Option`/`@Arguments` annotated field it operates over the raw unparsed values. So a user could not
call your command with `--limit -1` because the `-1` would be considered an option like value and be rejected by this
restriction.

Now this specific case could be avoided by customising the option prefixes as already shown, but if you want to use this
restriction broadly across many options that quickly becomes unwieldy.

Instead of applying this restriction to each option/argument individually you can instead apply it directly to your
`@Command`/`@Cli` annotated class. When applied at the class level this becomes a [Global Restriction](..
/restrictions/global.html) instead. As a Global Restriction it gets to inspect the typed parsed values of options and
arguments at the end of parsing. When it does this it **ONLY** considers options and arguments whose actual type
is `String`, the assumption being that any other type would already have handled a bad value when it was parsed. So for
the above example if a user invoked your command with `--limit --typo` then `--typo` would have resulted in a parser
error trying to convert it into an integer. Whereas `--limit -1` would now be acceptable because the typed value is not
a string.

### Related Annotations

If you have a prefixes you need to enforce, rather than reject, then use the [`@StartsWith`](starts-with.html)
annotation.  
For more complex string value enforcement you can also use the regular expression based [`@Pattern`](pattern.html)
annotation.
