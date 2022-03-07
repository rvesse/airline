---
layout: page 
title: Required Unless Environment Annotation
---

## `@RequiredUnlessEnvironment`

{% include req-ver.md version="2.8.5" %}

The `@RequiredUnlessEnvironment` annotation is applied to a field annotated with [`@Option`](option.html) or
[`@Arguments`](arguments.html) to indicate that the option/argument must be specified unless some environment variable
is specified. This is typically used when your program automatically reads a default setting from the environment but
allows the user to override it via an option e.g.

```java
@Option(name = "--server",
        arity = 1,
        description = "Specifies the server to connect to.")
@RequiredUnlessEnvironment(variables = { "MY_SERVER" })
private String server = System.getenv("MY_SERVER");
```

When a field is marked with `@RequiredUnlessEnvironment` it only becomes required if none of the specified 
environment variables are set.  As seen in this example your code remains responsible for populating your 
option/argument from the environment variable appropriately.

If the user fails to supply the option/argument and no suitable environment variable is set then an error will be thrown
during [parsing](../parser/)

### Related Annotations

If you want to require an option regardless then you should use [`@Required`](required.html)