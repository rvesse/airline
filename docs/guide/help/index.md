---
layout: page
title: Help System
---

Airline provides a comprehensive system of help generators that are able to take Airline defined CLIs and output help in
a variety of formats.  The help system is divided into a number of concepts which cooperate together to make help
configurable and extensible.

### Concepts

#### Help Hints

[Help Hints](hints.html) are a mechanism by which [Restrictions](../restrictions/index.html) can provide information
about their behaviour. This allows for restrictions to be self-documenting and incorporated into help output.

Hints are also used as part of Help Sections.

#### Help Sections

[Help Sections](sections.html) are a mechanism by which [`@Command`](../annotations/command.html) annotated classes can
add additional sections into help output. This can be used to incorporate arbitrary additional content into your help
outputs.

A variety of common help sections are provided out of the box and you can create [Custom Help
Sections](custom-sections.html) if desired:

{% include help-sections.md path="../annotations/" %}

#### Help Generators

[Help Generators](generators.html) are classes that take in CLI/Command metadata and output help in some format.  The
core library includes [Text](text.html) based help generators and additional libraries provide
[Markdown](markdown.html), [HTML](html.html), [MAN](man.html) and [Bash](bash.html) format help.

### Generating Help

In order to generate help you need to create an instance of your desired generator and then pass in the metadata for
your CLI/Command e.g.

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

For single commands we could use `CliCommandUsageGenerator` instead.

### Incorporating Help into your CLI

Airline provides a couple of ways you can incorporate help into your CLIs/commands to make this easily accessible to
your end users.

#### `HelpOption`

For single commands you can add the `HelpOption` to your command classes e.g.

```java
@Command(name = "parent", description = "A parent command")
public class Parent implements ExampleRunnable {

    @AirlineModule
    protected HelpOption<ExampleRunnable> help;

    @Option(name = "--parent", description = "An option provided by the parent")
    private boolean parent;

    public static void main(String[] args) {
        ExampleExecutor.executeSingleCommand(Parent.class, args);
    }

    @Override
    public int run() {
        if (!help.showHelpIfRequested()) {
            System.out.println("--parent was " + (this.parent ? "set" : "not set"));
        }
        return 0;
    }

}
```

We use our normal [Composition](../practise/oop.html) mechanism to add in `HelpOption` to our command class.  This will
provide a `-h`/`--help` option in our command.  We can then use the `help.showHelpIfRequested()` method to check whether
help was requested and if not proceed normally.  If this method returns `true` then help has been requested and output
to the user.

#### `Help` class

The `Help` class is a pre-built `@Command` class that provides intelligent help for CLIs.  You can simply add this to
your CLIs as one of your commands and it will add a `help` command to your CLI.  This command will select the
appropriate help to display depending on the arguments provided.  For example calling `your-cli help` will display help
for the entire CLI, whereas calling `your-cli help your-command` will display command specific help for `your-command`.

If you can't incorporate `Help` directly as a command because it does not extend/implement the base command type for
your CLI you can still use it indirectly.  If you are using a base interface then you can simply extend the class and
add `implements YourInterface` plus any necessary implementation deferring to the `run()` method e.g.

```java

public class CustomHelpCommand extends Help implements YourInterface {
  @Override
  public void yourMethod() {
    super.run();
  }
}
```

Alternatively the class also provides a number of static methods that you can call from your own command if you can't
extend it, for example when using a base class for your commands, e.g.

```java
Help.help(cli.getMetadata(), args, System.out)
```

Where `args` is a `List<String>` containing the command name(s) that help is being requested on.  So for our earlier
example we might call the following:

```java
Help.help(cli.getMetadata(), Collections.singletonList("your-command"), System.out)
```

#### Help and Errors

Note that when trying to incorporate help into a CLI one common problem that occurs is that you add restrictions which
end up get violated when users try to just invoke help on your commands.  This is particularly the case when using
`HelpOption`.  To avoid this you will need to customise the error handler as discussed in the [Error
Handling](../practise/exceptions.html) documentation and do something like the following:

{% include code/error-handler.md %}