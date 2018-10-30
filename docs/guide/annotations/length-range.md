---
layout: page
title: LengthRange Annotation
---

## `@LengthRange`

The `@LengthRange` annotation may be applied to fields annotated with [`@Option`](option.html) and [`@Arguments`](arguments.html) to limit the length of values that an option may be used with to a range of lengths e.g.

```java
@Option(name = "--reference", arity = 1)
@LengthRange(min = 5, max = 10)
private String reference;
```
This specifies that the `--reference` option only allows values with lengths of 5 to 10 characters to be specified by the user.  Any other length value will be rejected.

### Related Annotations

If you have an exact length to enforce then you should use the [`@ExactLength`](exact-length.html) annotation.  If you want to restrict just the minimum/maximum length of a value then use the [`@MinLength`](min-length.html) or [`@MaxLength`](max-length) annotations.cd 