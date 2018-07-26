---
layout: page
title: Text Help Generators
globals: [ "CliGlobalUsageGenerator", "CliGlobalUsageSummaryGenerator" ]
groups: [ "CliCommandGroupUsageGenerator" ]
commands: [ "CliCommandUsageGenerator" ]
---

The Text help generator, also referred to as the CLI help generator, is the default help generator built into the core Airline library.  It provides a full range of help generator implementations that can be used to generate help for CLIs, command groups and commands.

### Available Implementations

The following implementations are available:

- {% include javadoc-ref.md package="help.cli" class="CliGlobalUsageGenerator" %} - Full blown CLI help
- {% include javadoc-ref.md package="help.cli" class="CliGlobalUsageSummaryGenerator" %} - Quick summary help for CLIs
- {% include javadoc-ref.md package="help.cli" class="CliCommandGroupUsageGenerator" %} - Help for command groups
- {% include javadoc-ref.md package="help.cli" class="CliCommandUsageGenerator" %} - Help for individual commands 

{% include helpgen-examples.md global=page.globals group=page.groups command=page.commands package="help.cli" %}

### Customisation

The main point of customisation for text based help is the number of columns to output i.e. the max number of character per line.  This defaults to 79 which results in maximum line lengths of 80 characters barring some exceptions.

If you prefer you can create instances of the help generators with a different number of columns e.g.

```java
CommandUsageGenerator = new CliCommandUsageGenerator(120);
```