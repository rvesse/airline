---
layout: page
title: ExitCodes Annotation
---

## `@ExitCodes`

The `@ExitCodes` annotation may be applied to classes and provides documentation on the exit codes that a command may produce and will be included in [Help](../help/) as an additional section.

To use it add it like so:

```java
@ExitCodes(codes = 
           { 
               0,
               1
           }
           descriptions = 
           {
               "Success",
               "Error" 
           })
public class MyClass { }
```

The annotation takes two arrays, the `codes` array specifies the exit codes that the command may produce while `descriptions` provides corresponding description of the meaning of the exit codes.
