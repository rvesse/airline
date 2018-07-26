---
layout: page
title: MAN Help Generators
globals: [ "ManGlobalUsageGenerator", "ManMultiPageGlobalUsageGenerator" ]
commands: [ "ManCommandUsageGenerator" ]
---

The MAN help generator is provided by the `airline-help-man` library.  It generates help output in Troff format using MAN page specific extensions to generate Linux manual pages that can be viewed with the `man` tool.

### Available Implementations

The following implementations are available:

- {% include javadoc-ref.md module="airline-help-man" package="help.man" class="ManGlobalUsageGenerator" %} - Generates help for the entire CLI as a single manual page
- {% include javadoc-ref.md module="airline-help-man" package="help.man" class="ManMultiPageGlobalUsageGenerator" %} - Generates help for the entire CLI as a series of separate manual pages
- {% include javadoc-ref.md module="airline-help-man" package="help.man" class="ManCommandUsageGenerator" %} - Generates manual pages for individual commands

{% include helpgen-examples.md global=page.globals command=page.commands module="airline-help/airline-help-man" package="help.man" %}

### Customisation

The main point of customisation for manual pages is the manual section which is typically baked into the filename of manual pages e.g. `sort.1`.  Depending on the command you may wish to place it in a different manual section e.g.

```java
CommandUsageGenerator generator = new ManCommandUsageGenerator(ManSections.SYSTEM_ADMIN_AND_DAEMONS);
```

### Viewing the output

To view the generated output you will typically save it to an appropriately named file and then open it with the `man` tool e.g.

```
> man ./ship-it.1
```

Note the need to use `./` to refer to a local relative path rather than having `man` search for manual pages.  Per the above note on customisation remember that manual pages are typically named with a file extension matching the manual section your manual belongs to.

If you want to be able to use the normal `man` functionality you will need to update the `MANPATH` environment variable to include the directory where your man pages are living e.g.

```
> export MANPATH=/your/output/directory:$MANPATH
> man ship-it
```