# Airline 2

Airline is a Java library providing an annotation-based framework for parsing command line interfaces.

It supports both simple single commands through to complex git style interfaces with groups and sub-groups.

Additionally it provides many powerful features including, but not limited to, the following:

- Highly customisable [Parser](http://rvesse.github.io/airline/guide/parser/)
- [User Defined Aliases](http://rvesse.github.io/airline/guide/practise/aliases.html)
- Annotation driven [restrictions](http://rvesse.github.io/airline/guide/restrictions/) framework to reduce boilerplate
- Extensible [Help](http://rvesse.github.io/airline/guide/help) system supporting multiple output formats including 
- generating Man pages and Bash completion scripts
- [Maven Plugin](http://rvesse.github.io/airline/guide/practise/maven-plugin.html) for validation and help generation 
- during builds

## User Guide

Our project website at [http://rvesse.github.io/airline/](http://rvesse.github.io/airline/) contains a fairly 
comprehensive user guide.  Some portions are still under development but it covers the vast majority of the features 
of the library.

## Usage

To use airline you need to add a dependency to it to your own code, the Maven artifacts are described later in this 
ReadMe.

You then need to use the various annotations to annotate your command classes:

- `@Command` is used to annotate classes
- `@Option` is used to annotate fields to indicate they are options
- `@Arguments` is used to annotate fields that take in arguments
- `@AirlineModule` can be used to [modularise](http://rvesse.github.io/airline/guide/practise/oop.html) option 
  definitions into separate classes

Please see the [examples](airline-examples/) module for a range of examples that show off the many features of this 
library and practical examples of using the annotations.

Or for a quick tutorial why not read our [Introduction to Airline](http://rvesse.github.io/airline/guide/) in our User 
Guide.

### Quick Examples

#### Single Commands

Simply create a parser instance via `SingleCommand.singleCommand()` passing in a class that is annotated with the 
`@Command` annotation e.g.

```java
    public static void main(String[] args) {
        SingleCommand<YourClass> parser = SingleCommand.singleCommand(YourClass.class);
        YourClass cmd = parser.parse(args);
        
         // Execute your command however is appropriate e.g.
         cmd.run();   
    }
```

#### Multiple Commands

Create an instance of a `Cli`, this can be done either using the `CliBuilder` or by annotating a class with the `@Cli` 
annotation.  This is somewhat more involved so please see the
[User Guide](http://rvesse.github.io/airline/guide/#building-a-cli) or the [examples module](examples/) for proper 
examples.

#### Executable JAR

Note that typically you will want to create an executable JAR for your CLI using something like the Maven Shade plugin.  
This will then allow you to create a simple wrapper script that invokes your CLI.

    #!/bin/bash
    # myapp
    
    java -jar my-app.jar "$@"
    
**Note:** You must use `"$@"` here as otherwise Bash may expand/interpret arguments and as a result the JVM may not 
receive the expected arguments that the user enters.

If this is done you can then invoke your application e.g.

     myapp --global-option command --command-option arguments
     
Or:

    myapp --global-option group --group-option command --command-option arguments
    
    
## License

Airline is licensed under the Apache Software License Version 2.0, see provided **LICENSE**

See provided **NOTICE** for Copyright Holders

## JDK Compatibility

### Source

Our source code is Java 7 compliant.

### Build

Airline can be built with Java 7 through 11 and our `pom.xml` contains appropriate profile customisations to enable this.  
Regardless of the version built the `pom.xml` will target Java 7 byte code.  Our official releases are built with 
Java 11 but targeting Java 7 bytecode to maximise version compatibility.

It may be possible to build with newer Java versions than those stated here but those have not been tested by the 
developers.

**NB** - If you are trying to build older versions from source the relevant `pom.xml` customisations may not have 
existed at that time.  Also. you may encounter SSL errors trying to download Maven plugins using older versions of 
Java, you may need to first build with a newer version of Java to successfully download relevant Maven plugins before 
then building with the desired version of Java.

### Runtime

Airline releases are configured to build Java 7 compatible bytecode so can be used with Java 7 or above.

#### JPMS

Airline does make heavy use of reflection and therefore will likely not work on the Module Path without exporting 
packages that contain your CLI and Command classes as part of your `module-info.java`

From 2.7.0 onwards builds include contributions from our user community to enable use of Airline on the Module Path
including relevant `module-info.java` files.  From 3.0.0 onwards the module definitions were significantly improved to
provide better JPMS compatibility.  If you encounter a problem with this please report it at
https://github.com/rvesse/airline/issues

## Maven Artifacts

This library is available from [Maven Central](http://search.maven.org) with the latest stable release being `3.0.0`

Use the following maven dependency declaration:

```xml
<dependency>
    <groupId>com.github.rvesse</groupId>
    <artifactId>airline</artifactId>
    <version>3.0.0</version>
</dependency>
```

Snapshot artifacts of the latest source are also available using the version `3.0.1-SNAPSHOT` from the 
[OSSRH repositories](http://central.sonatype.org/pages/ossrh-guide.html#ossrh-usage-notes).

## Build Status

CI builds are run on [Travis CI](http://travis-ci.org/) ![Build Status](https://travis-ci.org/rvesse/airline.png), see 
build information and history at https://travis-ci.org/rvesse/airline

# Historical Information

This is a substantially rewritten fork of the original [airline library](https://github.com/airlift/airline) created 
based on improvements predominantly developed by myself plus some minor improvements taken from the
[Clark & Parsia](https://github.com/clarkparsia/airline) fork.  It has significantly deviated from the original library 
and gained many powerful features that differentiate it from both the original and other libraries with similar goals.

## Migrating between Versions

Airline 2 contains significant breaking changes from Airline 1.x, please see [Migrating.md](Migrating.md) for more 
details on how to migrate code forward.

Airline 2.1 contains some further minor breaking changes that should only affect advanced users, again please see 
[Migrating.md](Migrating.md) for more details on how to migrate code forward.  Some users may need to add additional 
Maven dependencies if they were using help formats other than the basic CLI help.

Airline 2.2 has some minor breaking changes that may affect users of the `@Arguments` annotation, again please see 
[Migrating.md](Migrating.md) for more details.

Airline 2.9 has some changes to composition in preparation for future breaking changes, most notably introducing the 
`@AirlineModule` annotation as a replacement for `@Inject`.  It remains backwards compatible with prior 2.x releases 
but users may wish to start making changes to ease future transitions.

Airline 3.0.0 has several major breaking changes, this includes updating the minimum JDK version to 11, making some
dependencies optional and much improved JPMS compatibility.
