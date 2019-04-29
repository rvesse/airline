---
layout: page
title: Examples Annotation
---

## `@Examples`

The `@Examples` annotation may be applied to classes and provides documentation on how to use a command and will be included in [Help](../help/) as an additional section.

To use it add it like so:

```java
@Examples(examples = 
           { 
               "my-cmd --lower bar",
               "my-cmd --raise bar"
           }
           descriptions = 
           {
               "Lowers the bar",
               "Raises the bar" 
           })
public class MyClass { }
```

The annotation takes two arrays, the `examples` array specifies an example of using the command while `descriptions` provides corresponding description of what each example does.
