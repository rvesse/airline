---
layout: page
title: Bash Completion Help Generators
globals: [ "BashCompletionGenerator" ]
---

The Bash Completion help generator is provided by the `airline-help-bash` library.  It generates Bash completion scripts for CLIs which can be used to provide Bash completion to users of the Bash shell.  This differs from the other help generators in that the output is not intended for reading by end users.

### Available Implementations

The following implementations are available:

- {% include javadoc-ref.md module="airline-help-bash" package="help.cli.bash" class="BashCompletionGenerator" %} - Generates Bash completion script for the CLI

{% include helpgen-examples.md global=page.globals module="airline-help/airline-help-bash" package="help.cli.bash" %}

### Customising Completions

By default the generator will create completions based on various standard Airline metadata annotations:

 - [`@Cli`](../annotations/cli.html)
 - [`@ParserConfiguration`](../annotations/parser.html)
 - [`@Command`](../annotations/command.html)
 - [`@Option`](../annotations/option.html) 
 - [`@Arguments`](../annotations/arguments.html)
 - [`@AllowedRawValues`](../annotations/allowed-raw-values.html)

However in some cases it may be desirable to customise how things are completed further, you can do this using the `@BashCompletion` annotation provided by the `airline-help-bash` library e.g.

```java
@Command(name = "file-info", description = "Returns information about the given files")
public class FileInfo implements ExampleRunnable {
    
    @Arguments(description = "Files to get info for")
    @Required
    @BashCompletion(behaviour = CompletionBehaviour.FILENAMES)
    private List<String> files;

    @Override
    public int run() {
        for (String file : files) {
            File f = new File(file);
            System.out.printf("File: %s\n", file);
            System.out.printf("Absolute Path: %s\n", f.getAbsolutePath());
            System.out.printf("Is Directory? %s\n", f.isDirectory() ? "Yes" : "No");
            System.out.printf("Size: %,d\n", f.length());
            System.out.println();
        }
        
        return 0;
    }

}
```

Here the `behaviour` field is used to specify that the arguments should be completed with filenames.

The following completion behaviours are available:

| Behaviour | Functionality |
| ------------- | ----------------- |
| `NONE` | Don't do any additional completions |
| `FILENAMES` | Complete with filenames |
| `DIRECTORIES` | Complete with directory names |
| `CLI_COMMANDS` | Complete with the names of commands in this CLI |
| `SYSTEM_COMMANDS` | Complete with system commands visible on your `PATH` |
| `AS_FILENAMES` | Expand completions from standard meta-data as filenames |
| `AS_DIRECTORIES` | Expand completions from standard meta-data as directories |

### Using the Output

To use the output from this help generator you need to capture it in a file and then use the `source` command to load it into your Bash environment.  Once sourced you will be able to use tab completion for your CLI.
