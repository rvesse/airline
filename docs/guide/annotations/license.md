---
layout: page
title: License Annotation
---

## `@License`

The `@License` annotation may be applied to classes and provides a copyright statement that will be included in [Help](../help/) as an additional section.

To use it simply add it like so:

```java
@License(paragraphs =
         {
           "This command is licensed under the Apache License 2.0"
         },
         url = "http://apache.org/license/2.0")
public class MyClass { }
```

The `paragraphs` field provides an array of strings where each string is a paragraph that is used to describe the license.  The `url` field may be used to provide a URL where the user can find further license information.
