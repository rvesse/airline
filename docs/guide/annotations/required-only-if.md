---
layout: page 
title: Required Only If Annotation
---

## `@RequiredOnlyIf`

The `@RequiredOnlyIf` annotation is applied to a field annotated with [`@Option`](option.html) or [`@Arguments`]
(arguments.html) to indicate that the option/argument must be specified only when some other option is specified.  
This is typically used when you have options that only make sense in conjunction with other options, but are required
when those options are used.

```java
@Option(name = "--authenticate",
        arity = 0,
        description = "Enables authentication")
private boolean authenticate = false;

@Option(name = "--user-source",
        arity = 1,
        description = "Specifies a source of user information used when authentication is enabled.")
@RequiredOnlyIf(names = { "--authenticate" })
private String userSource;
```

When a field is marked with `@RequiredOnlyIf` and any of the named options are used then this option becomes 
required.  So in the above example if a user specifies `--authenticate` then they **MUST** also specify `--user-source`.

If the user fails to supply the correct combination of options and arguments then an error will be thrown during
[parsing](.. /parser/)

### Related Annotations

If you want to require an option regardless then you should use [`@Required`](required.html)