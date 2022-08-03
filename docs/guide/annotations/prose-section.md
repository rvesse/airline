---
layout: page
title: ProseSection Annotation
---

## `@ProseSection`

The `@ProseSection` annotation may be applied to classes and provides a custom text section that may be used for whatever purpose you desire that will be included in [Help](../help/) as an additional section.

To add a prose section section simply add the `@ProseSection` annotation to a class like so:

```java
@ProseSection(title = "Additional Information"
              paragraphs = {
                   "This is additional information",
                   "We can have as many paragraphs as we feel are necessary"
              },
              suggestedOrder = 55)
public class MyClass { }
```

The annotation requires a `title` field which specifies the title that should be used for your help section.  It also takes a `paragraphs` field which takes a `String[]` array where each entry in the array is treated as a separate paragraph in the help output.

The optional `suggestedOrder` field is used to control where the section appears relative to other help sections in the generated help.  Values less than zero are used to indicate that the section should appear prior to the standard sections while values greater than zero are used to indicate that it should appear after the standard sections.  `CommonSections` provides the default values that are used for the other help annotations such as [`@Discussion`](discussion.html) if you wish to place your custom section relative to one of the other commonly used sections.

## `@ExternalProse`

{% include req-ver.md version="2.10.0" module="airline-help-external" %}

If your application has a lot of additional sections you wish to add and they are quite wordy it may be easier to provide this as a separate resource that Airline loads rather than directly in an annotation.  This can be done via the `@ExternalProse` annotation e.g.

```java
@ExternalProse(
  title = "Additional Information",
  suggestedOrder = 55,
  source = "/some/classpath/path/add-info.txt"
)
public class MyClass { }
```
Would add a section titled `Additional Information` whose content was read in from the specified resource on either the classpath or the filesystem.  The resource is simple a text file where each paragraph is considered to be one/more non-empty lines separated by one/more empty line(s) e.g.

```
This is the 
first paragraph.

Followed by another paragraph.


Multiple blank lines are compacted down so this
becomes the 3rd paragraph.

And so forth...
```

If you want the resource to be loaded from a different location you can customise the [Resource Locators](../practise/resource-locators.html) via the `sourceLocators` property e.g.

```java
@ExternalProse(
  title = "Additional Information",
  suggestedOrder = 55,
  source = "${MY_DOCS}/add-info.txt",
  sourceLocators = { EnvVarLocator.class }
)
public class MyClass { }
```
Here we use an environment variable as a pointer to our external help resource by configuring the `EnvVarLocator` as our source locator.