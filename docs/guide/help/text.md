---
layout: page
title: Text Help Generators
---

The Text help generator, also referred to as the CLI help generator, is the default help generator built into the core Airline library.  It provides a full range of help generator implementations that can be used to generate help for CLIs, command groups and commands.

### Available Implementations

The following implementations are available:

- {% include github-ref.md package="help.cli" class="CliGlobalUsageGenerator" %} - Full blown CLI help
- {% include github-ref.md package="help.cli" class="CliGlobalUsageSummaryGenerator" %} - Quick summary help for CLIs
- {% include github-ref.md package="help.cli" class="CliCommandGroupUsageGenerator" %} - Help for command groups
- {% include github-ref.md package="help.cli" class="CliCommandUsageGenerator" %} - Help for individual commands 

### Example Usage

#### Global Help

```java
   Cli<ExampleRunnable> cli = new Cli<ExampleRunnable>(ShipItCli.class);
        
   GlobalUsageGenerator<ExampleRunnable> helpGenerator = new CliGlobalUsageSummaryGenerator<>();
   try {
       helpGenerator.usage(cli.getMetadata(), System.out);
   } catch (IOException e) {
       e.printStackTrace();
   }
```

#### Command Group Help

```java
   Cli<ExampleRunnable> cli = new Cli<ExampleRunnable>(GroupCli.class);
        
   CommandGroupUsageGenerator<ExampleRunnable> helpGenerator = new CliCommandGroupUsageGenerator<>();
   try {
       helpGenerator.usage(cli.getMetadata(), new CommandGroupMetadata[] { cli.getMetadata().getCommandGroups().get(0) }, System.out);
   } catch (IOException e) {
       e.printStackTrace();
   }
```

#### Command Help

```java
SingleCommand<Price> command = new SingleCommand<Price>(Price.class);

CommandUsageGenerator generator = new CliCommandUsageGenerator();
try {
    generator.usage(null, null, "price", command.getCommandMetadata(), command.getParserConfiguration(), System.out);
} catch (IOException e) {
    e.printStackTrace();
}
```