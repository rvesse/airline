---
layout: page
title: Copyright Annotation
---

## `@Copyright`

The `@Copyright` annotation may be applied to classes and provides a copyright statement that will be included in [Help](../help/) as an additional section.

To use it simply add it like so:

```java
@Copyright(startYear = 2015,
           endYear = 2016,
           holder = "John Doe")
public class MyClass { }
```

The `startYear` and `holder` are required while the `endYear` is optional.
