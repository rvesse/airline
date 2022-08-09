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

The annotation takes two arrays, the `codes` array specifies the exit codes that the command may produce while
`descriptions` provides corresponding description of the meaning of the exit codes.

## `@ExternalExitCodes`

{% include req-ver.md version="2.10.0" module="airline-help-external" %}

If your application has a lot of exit codes it may be simpler to define these in a separate file and just have Airline
load this for you.

To use it add it like so:

```java
@ExternalExitCodes(
  source = "/some/classpath/path/exit-codes.csv"
)
public class MyClass { }
```
This will look for the specified resource on either the classpath or the filesystem and attempt to load it as a CSV with
no headers where the first column is the exit code and the second column its corresponding description e.g.

```
0,Success
1,Error
```
If you want the resource to be loaded from a different location you can customise the [Resource
Locators](../practise/resource-locators.html) via the `sourceLocators` property e.g.

```java
@ExternalExitCodes(
  source = "${MY_DOCS}/exit-codes.csv",
  sourceLocators = { EnvVarLocator.class }
)
public class MyClass { }
```
Here we use an environment variable as a pointer to our external help resource by configuring the `EnvVarLocator` as our
source locator.

If you want to format your exit code data in an alternative format you can customise how Airline parses the data via the
`parser` property which takes a `TabularParser` implementing class.  The default behaviour is to parse as CSV which is
provided by the `DefaultExternalHelpParser`, there is also a `TabDelimitedHelpParser` if you want to use tab delimited
data instead e.g. because your descriptions contain commas.