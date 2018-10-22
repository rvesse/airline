---
layout: page
title: MaxLength Annotation
---

## `@MaxLength`

The `@MaxLength` annotation may be applied to fields annotated with [`@Option`](option.html) and [`@Arguments`](arguments.html) to limit the length of the value provided e.g.

```java
@Option(name = "--reference", arity = 1)
@MaxLength(length = 10)
private String reference;
```

Restricts the `--reference` option to values of at most 10 characters.

### Related Annotations

If you want to restrict the minimum length of a value then use the [`@MinLength`](min-length.html) annotation.  For exact length restrictions use the [`@ExactLength`](exact-length.html) annotation.