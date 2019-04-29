---
layout: page
title: Help Sections
---

Help Sections are a basic component of the help system, they extend the [Help hints](hints.html) interface with some additional methods:

```java
public interface HelpSection extends HelpHint {

    public String getTitle();

    public String getPostamble();

    public int suggestedOrder();

}
```

Most importantly they add a section title that is used to uniquely identify a help section, when Airline processes help section annotations it overrides sections based upon titles.

Additionally it provides a suggested order that is used by help generators to decide in which order the help sections are included in the output.  Generally speaking a suggested order of less than zero means the section should be shown before the standard sections and a suggested order of greater than zero means the section should be shown after the standard sections.  However this is only a suggestion and help generators may not honour this.

Finally you have a post-amble that allows you to provide additional text to be shown at the end of the section.

### Build-In Sections

Airline provides a number of built-in help sections each of which has an annotation associated with them.  The following help section annotations are available:

{% include help-sections.md path="../annotations/" %}

To use one of these simply apply the relevant annotation to your `@Command` class e.g.

```java
@Command(name = "discussed", description = "A command with a discussion section")
//@formatter:off
@Discussion(paragraphs = 
    { 
        "This command uses the @Discussion annotation to add a discussion section to its help",
        "This is an example of using a Help Section annotation to add content into our generated help output." 
    })
//@formatter:on
public class Discussed
```

Airline will automatically detect these when creating your commands metadata and then help generators will include the content in the help output.



