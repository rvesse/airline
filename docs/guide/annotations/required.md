---
layout: page 
title: Required Annotation
---

## `@Required`

The `@Required` annotation is applied to a field annotated with [`@Option`](option.html)
or [`@Arguments`](arguments.html) to indicate that the option/argument must be specified.

```java
@Option(name = "--num",
        arity = 1,
        title = "Number")
@Required
private int number;
```

When a field is marked with `@Required` if the user fails to supply that option/argument then an error will be thrown
during [parsing](../parser/)

### Related Annotations

If you want to require one option from some set of options then you should use [`@RequireSome`](require-some.html)