---
layout: page
title: Markdown Help Generators
globals: [ "MarkdownGlobalUsageGenerator", "MarkdownMultiPageGlobalUsageGenerator" ]
commands: [ "MarkdownCommandUsageGenerator" ]
---

The Markdown help generator is provided by the `airline-help-markdown` library.  It generates help output in [Markdown](https://daringfireball.net/projects/markdown/syntax) format which is a simple text markup format that can be rendered into HTML.

### Available Implementations

The following implementations are available:

- {% include javadoc-ref.md module="airline-help-markdown" package="help.markdown" class="MarkdownGlobalUsageGenerator" %} - Generates help for the entire CLI as a single markdown document
- {% include javadoc-ref.md module="airline-help-markdown" package="help.markdown" class="MarkdownMultiPageGlobalUsageGenerator" %} - Generates help for the entire CLI as a series of separate markdown documents
- {% include javadoc-ref.md module="airline-help-markdown" package="help.markdown" class="MarkdownCommandUsageGenerator" %} - Generates markdown documents for individual commands

{% include helpgen-examples.md global=page.globals command=page.commands module="airline-help/airline-help-markdown" package="help.markdown" %}

### Customisation

The main point of customisation for markdown based help is the number of columns to output i.e. the max number of character per line.  This defaults to 79 which results in maximum line lengths of 80 characters barring some exceptions.

If you prefer you can create instances of the help generators with a different number of columns e.g.

```java
CommandUsageGenerator = new MarkdownCommandUsageGenerator(120);
```