---
layout: page
title: Help Generators
---

Help Generators are classes that take in CLI/Command metadata and output help in some format.  The core library includes [Text](text.html) based help generators and additional libraries provide [Markdown](markdown.html), [HTML](html.html), [MAN](man.html) and [Bash](bash.html) format help.

### Generating Help

In order to generate help you need to create an instance of your desired generator and then pass in the metadata for your CLI/Command.  There are three interfaces provided for generating different kinds of help:

- `GlobalUsageGenerator` - Generates help for CLIs
- `CommandGroupUsageGenerator` - Generates help for command groups
- `CommandUsageGenerator` - Generates help for commands

 For example for a CLI we might do the following:

```java
   Cli<ExampleRunnable> cli = new Cli<ExampleRunnable>(ShipItCli.class);
        
   CliGlobalUsageGenerator<ExampleRunnable> helpGenerator = new CliGlobalUsageGenerator<>();
   try {
       helpGenerator.usage(cli.getMetadata(), System.out);
   } catch (IOException e) {
       e.printStackTrace();
   }
```

In this example we output the help to `System.out` but we could pass any `OutputStream` we desired.

### Available Generators

| Format | Module | Description |
| ---------- | --------- | -------------- |
| [Text](text.html) | `airline` | Creates column wrapped plain text suitable for displaying directly on a console |
| [Markdown](markdown.html) | `airline-help-markdown` | Creates Markdown formatted help |
| [HTML](html.html) | `airline-help-html` | Creates HTML format help |
| [MAN](man.html) | `airline-help-man` | Creates Linux MAN pages in Troff format that can be viewed with the `man` utility |
| [Bash](bash.html) | `airline-help-bash` | Creates Bash completion scripts |