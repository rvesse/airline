### Example Usage

{% if include.global | size > 0 %}

#### Global Help

{% for generator in include.global %}

Using {% include github-ref.md module=include.module package=include.package class=generator %}:

```java
   Cli<ExampleRunnable> cli = new Cli<ExampleRunnable>(ShipItCli.class);
        
   GlobalUsageGenerator<ExampleRunnable> helpGenerator = new {{ generator }}<>();
   try {
       helpGenerator.usage(cli.getMetadata(), System.out);
   } catch (IOException e) {
       e.printStackTrace();
   }
```
{% endfor %}

{% endif %}

{% if include.group | size > 0 %}

#### Command Group Help

{% for generator in include.group %}

Using {% include github-ref.md module=include.module package=include.package class=generator %}:

```java
   Cli<ExampleRunnable> cli = new Cli<ExampleRunnable>(GroupCli.class);
        
   CommandGroupUsageGenerator<ExampleRunnable> helpGenerator = new {{ generator }}<>();
   try {
       helpGenerator.usage(cli.getMetadata(), new CommandGroupMetadata[] { cli.getMetadata().getCommandGroups().get(0) }, System.out);
   } catch (IOException e) {
       e.printStackTrace();
   }
```
{% endfor %}

{% endif %}

{% if include.command | size > 0 %}

#### Command Help

{% for generator in include.command %}

Using {% include github-ref.md module=include.module package=include.package class=generator %}:

```java
SingleCommand<Price> command = new SingleCommand<Price>(Price.class);

CommandUsageGenerator generator = new {{ include.command }}();
try {
    generator.usage(null, null, "price", command.getCommandMetadata(), command.getParserConfiguration(), System.out);
} catch (IOException e) {
    e.printStackTrace();
}
```
{% endfor %}

{% endif %}