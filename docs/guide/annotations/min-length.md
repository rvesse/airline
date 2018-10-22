---
layout: page
title: MinLength Annotation
---

## `@MinLength`

The `@MinLength` annotation may be applied to fields annotated with [`@Option`](option.html) and [`@Arguments`](arguments.html) to limit the length of the value provided e.g.

```java
@Option(name = "--reference", arity = 1)
@MinLength(length = 5)
private String reference;
```

Restricts the `--reference` option to values of at least 5 characters.

### Related Annotations

If you want to restrict the minimum length of a value then use the [`@MaxLength`](max-length.html) annotation.  For exact length restrictions use the [`@ExactLength`](exact-length.html) annotation.