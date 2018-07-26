---
layout: page
title: HTML Help Generators
commands: [ "HtmlCommandUsageGenerator" ]
---

The HTML help generator is provided by the `airline-help-html` library.  It provides a help generator implementation that can be used to generate help in HTML format that can be viewed in a web browser.

### Available Implementations

The following implementations are available:

- {% include javadoc-ref.md package="help.html" class="HtmlCommandUsageGenerator" %} - Help

{% include helpgen-examples.md command=page.commands package="help.html" %}

### Customisation

The main piece of customisation for HTML help generators is what stylesheets to use, by default the HTML generated assumes Bootstrap is used as a stylesheet.  If you wish to use alternative stylesheets then you can do so e.g.

```java
CommandUsageGenerator = new HtmlCommandUsageGenerator("css/my-style.css");
```