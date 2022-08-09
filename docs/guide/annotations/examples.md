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

The annotation takes two arrays, the `examples` array specifies an example of using the command while `descriptions`
provides corresponding description of what each example does.

## `@ExternalExamples` and `@ExternalTabularExamples`

{% include req-ver.md version="2.10.0" module="airline-help-external" %}

If your application has a lot of examples it may be simpler to define these in separate file(s) and just have Airline
load this for you.

To use it add it like so:

```java
@ExternalExamples(
  exampleSource = "/some/classpath/path/examples.txt",
  descriptionSource = "/some/classpath/path/example-descriptions.txt"
)
public class MyClass { }
```
This will look for the specified resources on either the classpath or the filesystem and attempt to load them, each file
is expected to be a text file where each example/description is separated by one/more blank lines.  For examples here's
a sample `example.txt`:

```
my-cmd --verbose

my-cmd --dry-run
```

And here's it's corresponding `descriptions.txt`:

```
Runs in verbose mode

Run a dry-run, displays what would be done but does not do it.
```

Alternatively you may find it simpler to store both examples and their descriptions in a single tabular data file e.g.

```java
@ExternalTabularExamples(
  source = "/some/classpath/path/examples.csv",
)
public class MyClass { }
```
Here our data would be stored in a single CSV file like so:

```
my-cmd --verbose,Runs in verbose mode
my-cmd --dry-run,"Runs a dry-run, displays what would be done but does not do it"
```

If you want to format your exit code data in an alternative format you can customise how Airline parses the data via the
`parser` property which takes a `TabularParser` implementing class.  The default behaviour is to parse as CSV which is
provided by the `DefaultExternalHelpParser`, there is also a `TabDelimitedHelpParser` if you want to use tab delimited
data instead e.g. because your descriptions contain commas.

If you want the resource(s) to be loaded from a different location you can customise the [Resource
Locators](../practise/resource-locators.html) via the `sourceLocators` property e.g.

```java
@ExternalTabularExamples(
  source = "${MY_DOCS}/examples.csv",
  sourceLocators = { EnvVarLocator.class }
)
public class MyClass { }
```
Here we use an environment variable as a pointer to our external help resource by configuring the `EnvVarLocator` as our
source locator.