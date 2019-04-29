---
layout: page
title: ExactLength Annotation
---

## `@ExactLength`

The `@ExactLength` annotation may be applied to fields annotated with [`@Option`](option.html) and [`@Arguments`](arguments.html) to limit the length of the value provided to a specific length e.g.

```java
@Option(name = "--reference", arity = 1)
@MaxLength(length = 10)
private String reference;
```

Restricts the `--reference` option to values of exactly 10 characters.

### Related Annotations

If you want to restrict the minimum/maximum length of a value then use the [`@MinLength`](min-length.html) or [`@MaxLength`](max-length) annotations.