---
layout: page
title: Positive and Negative Annotations
---

## `@Positive`

The `@Positive` annotation is applied to numerically typed fields to indicate that their values must be a positive number e.g.

```java
@Option(name = "-i", title = "Integer", arity = 1)
@Positive
public long i;
```

Here the `-i` option must take a positive value.

### Treatment of Zero

By default zero is considered a positive number, if you do not want this to be the case you can add `includesZero = false` to your annotation e.g.

```java
@Option(name = "-i", title = "Integer", arity = 1)
@Positive(includesZero = false)
public long i;
```

## `@Negative`

`@Negative` is the opposite of `@Positive`, it is applied to numeric fields to indicate that their values must be a negative number e.g.

```java
@Option(name = "-i", title = "Integer", arity = 1)
@Negative
public long i;
```

Here the `-i` option must take a negative value.

### Treatment of Zero

By default zero is considered a positive number, if you want to treat it as a negative number you can add `includesZero = true` to your annotation e.g.

```java
@Option(name = "-i", title = "Integer", arity = 1)
@Negative(includesZero = true)
public long i;
```

### Related Annotations

For more specific value ranges on numeric fields use the various numeric range annotations - [`@ByteRange`](byte-range.html), [`@ShortRange`](short-range.html), [`@IntegerRange`](integer-range.html), [`@LongRange`](long-range.html), [`@FloatRange`](float-range.html) and [`@DoubleRange`](double-range.html) - to specify desired minimum and maximum values.

For limiting numeric fields to small sets of values consider the [`@AllowedValues`](allowed-values.html) annotation.