# JPMS Resource Debugging Utility

This is a simple utility that helps in debugging the visibility of resources when running in a JPMS context.

Once built run as follows:

```bash
$ ./debug --pattern "**.txt" --show-module-name
Scanning...
Module com.github.rvesse.airline.examples
 discussion.txt
Module io.github.classgraph
 LICENSE-ClassGraph.txt
Module org.apache.commons.collections4
 META-INF/LICENSE.txt
 META-INF/NOTICE.txt
Module org.apache.commons.csv
 META-INF/LICENSE.txt
 META-INF/NOTICE.txt
Module org.apache.commons.lang3
 META-INF/LICENSE.txt
 META-INF/NOTICE.txt
Found 8 resources
```

To see the options on offer run with `-h`/`--help`:

```bash
$ ./debug --help
NAME
        debug - Debugs the visibility of resources within modules

SYNOPSIS
        debug [ {-h | --help} ] [ {-m | --modules} <ModuleName>... ]
                [ {-p | --pattern} <ResourcePattern> ] [ --show-module-name ]
                [ --test-open ] [ --test-print ]

...
```

If you want to test with additional modules on the module path export the `EXTRA_MODULES` variable prior to invoking 
the script:

```bash
$ export EXTRA_MODULES="/path/to/modules"
$ ./debug --pattern "**.txt" --show-module-name
```