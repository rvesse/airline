---
layout: page 
title: RequireSome Annotation
---

## `@RequireSome`

The `@RequireSome` annotation is applied to a field annotated with [`@Option`](option.html) to indicate that at least
one from some set of options must be specified e.g.

```java
@Option(name = "--num",
        arity = 1,
        title = "Number")
@RequireSome(tag = "identifier")
private int number;

@Option(name = "--name",
        arity = 1,
        title = "Name")
@RequireSome(tag = "identifier")
private String name;
```

When fields are marked with `@RequireSome` if the user fails to supply at least one of the options that have the
same `tag` value then an error will be thrown during [parsing](../parser/).

In this example the user must specify at least one of the `--num` or `--name` option and may specify both if desired.

### Related Annotations

If you want to require that at most one of some set of options be specified then you should
use [`@RequireOnlyOne`](require-only-one.html) instead.

If you optionally want to allow only one from some set of options then you should
use [`@MutuallyExclusiveWith`](mutually-exclusive-with.html).