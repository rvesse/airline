### Example Usage

{% if include.global != "" %}

#### Global Help

```java
   Cli<ExampleRunnable> cli = new Cli<ExampleRunnable>(ShipItCli.class);
        
   GlobalUsageGenerator<ExampleRunnable> helpGenerator = new {{ include.global }}<>();
   try {
       helpGenerator.usage(cli.getMetadata(), System.out);
   } catch (IOException e) {
       e.printStackTrace();
   }
```

{% endif %}

{% if include.group != "" %}

#### Command Group Help

```java
   Cli<ExampleRunnable> cli = new Cli<ExampleRunnable>(GroupCli.class);
        
   CommandGroupUsageGenerator<ExampleRunnable> helpGenerator = new {{ include.group }}<>();
   try {
       helpGenerator.usage(cli.getMetadata(), new CommandGroupMetadata[] { cli.getMetadata().getCommandGroups().get(0) }, System.out);
   } catch (IOException e) {
       e.printStackTrace();
   }
```

{% endif %}

{% if include.command != "" %}

#### Command Help

```java
SingleCommand<Price> command = new SingleCommand<Price>(Price.class);

CommandUsageGenerator generator = new {{ include.command }}();
try {
    generator.usage(null, null, "price", command.getCommandMetadata(), command.getParserConfiguration(), System.out);
} catch (IOException e) {
    e.printStackTrace();
}
```

{% endif %}