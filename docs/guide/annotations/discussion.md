---
layout: page
title: Discussion Annotation
---

## `@Discussion`

The `@Discussion` annotation may be applied to classes and provides a longer form discussion of a command that will be
included in [Help](../help/) as an additional section.

To add a discussion section simply add the `@Discussion` annotation to a class like so:

```java
@Discussion(paragraphs = {
	"This is the first paragraph of discussion",
	"In our second paragraph we go into much more depth",
	"We can have as many paragraphs as we feel are necessary"
})
public class MyClass { }
```

The annotation takes a single `paragraphs` field which takes a `String[]` array where each entry in the array is treated
as a separate paragraph of discussion in the help output.

## `@ExternalDiscussion`

{% include req-ver.md version="3.0.0" module="airline-help-external" %}

If your application has a long discussion section you wish to add it may be easier to provide this as a separate
resource that Airline loads rather than directly in an annotation.  This can be done via the `@ExternalDiscussion`
annotation e.g.

```java
@ExternalDiscussion(
  source = "/some/classpath/path/discussion.txt"
)
public class MyClass { }
```

Would add a discussion section whose content was read in from the specified resource on either the classpath or the
filesystem.  The resource is simple a text file where each paragraph is considered to be one/more non-empty lines
separated by one/more empty line(s) e.g.

```
This is the 
first paragraph.

Followed by another paragraph.


Multiple blank lines are compacted down so this
becomes the 3rd paragraph.

And so forth...
```

If you want the resource to be loaded from a different location you can customise the [Resource
Locators](../practise/resource-locators.html) via the `sourceLocators` property e.g.

```java
@ExternalDiscussion(
  source = "${MY_DOCS}/add-info.txt",
  sourceLocators = { EnvVarLocator.class }
)
public class MyClass { }
```
Here we use an environment variable as a pointer to our external help resource by configuring the `EnvVarLocator` as our
source locator.