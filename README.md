# Airline 2

Airline is a Java library providing an annotation-based framework for parsing command line interfaces.

It supports both simple single commands through to complex git style interfaces with groups.

This is a substantially rewritten fork of the original [airline library](https://github.com/airlift/airline) created based on improvements predominantly developed by myself plus some taken from the [Clark & Parsia](https://github.com/clarkparsia/airline) fork.

## Breaking Changes versus 1.x

Airline 2 contains significant breaking changes from Airline 1.x, please see `Migrating.md` in this folder for more details on how to migrate code forward

## Usage

To use airline you need to add a dependency to it to your own code, the Maven artifacts are described later in this file.

You then need to use the various annotations to annotate your command classes:

- `@Command` is used to annotate classes
- `@Option` is used to annotate fields to indicate they are options
- `@Arguments` is used to annotate fields that take in arguments
- `@Inject` can be used to modularize option definitions into separate classes

Please see the [examples](examples/) module for a range of examples that show off the many features of this library and practical examples of using the annotations.

In your `main(String[] args)` method you then need to create a parser instance either via `SingleCommand.singleCommand()` or by creating an instance of a `Cli` using the `CliBuilder` and then call the `parse()` method passing in the provided `args`.  This will return you an instance of the command the user wants to execute and then you can go ahead and execute that however you want.

Note that typically you will want to create an executable JAR for your CLI using something like the Maven Shade plugin.  This will then allow you to create a simple wrapper script that invokes your CLI.

Once that is done you can then invoke your application e.g.

     myapp --global-option command --command-option arguments
     
Or:

    myapp --global-option group --group-option command --command-option arguments
    
## License

Airline is licensed under the Apache Software License Version 2.0, see provided **License.txt**

See provided **Notice.md** for Copyright Holders

## Maven Artifacts

This library is available from [Maven Central](http://search.maven.org) with the latest stable release being `1.0.2`

Use the following maven dependency declaration:

```xml
<dependency>
    <groupId>com.github.rvesse</groupId>
    <artifactId>airline</artifactId>
    <version>1.0.2</version>
</dependency>
```

Snapshot artifacts of the latest source are also available using the version `1.0.3-SNAPSHOT` from the [OSSRH repositories](http://central.sonatype.org/pages/ossrh-guide.html#ossrh-usage-notes).

`2.0.0-SNAPSHOT` represents a more significant rewrite with the aim of making the library more configurable and extensible.

## Build Status

CI builds are run on [Travis CI](http://travis-ci.org/) ![Build Status](https://travis-ci.org/rvesse/airline.png), see build information and history at https://travis-ci.org/rvesse/airline
