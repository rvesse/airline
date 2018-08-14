---
layout: page
title: Using the Maven Plugin
---

From 2.5.0 onwards we now provide a Maven plugin that can be used to integrate Airline into your Java build process.  Our plugin provides two goals:

- `airline:validate` - Loads the metadata for the configured sources to check that there is no invalid metadata that would cause a runtime exception
- `airline:generate` - Generates help for the configured sources

## `airline:validate`

The `validate` goal is used to load and validate the metadata for [`@Cli`](../annotations/cli.html) or [`@Command`](../annotations/command.html) annotated classes.  It will attempt to load the Airline metadata and fails the build if the metadata is invalid.

Example usage:

```xml
      <plugin>
        <groupId>com.github.rvesse</groupId>
        <artifactId>airline-maven-plugin</artifactId>
        <version>{{ site.version }}</version>
        <configuration>
          <sources>
            <source>
              <classes>
                <class>com.github.rvesse.airline.examples.simple.Simple</class>
                <class>com.github.rvesse.airline.examples.userguide.BasicCli</class>
              </classes>
            </source>
          </sources>
        </configuration>
        <executions>
          <execution>
            <phase>verify</phase>
            <goals>
              <goal>validate</goal>
            </goals>
          </execution>
        </executions>
      </plugin>
```

Here the plugin will load the metadata for `com.github.rvesse.airline.examples.simple.Simple` and `com.github.rvesse.airline.examples.userguide.BasicCli` in order to validate those.

In the event that any of the configured sources are invalid your build will fail with an error like the following:

> [ERROR] Failed to execute goal com.github.rvesse:airline-maven-plugin:2.5.1-SNAPSHOT:validate (default) on project airline-plugin-validate-bad: Class com.github.rvesse.airline.examples.cli.SimpleCli is not annotated with @Cli or @Command -> [Help 1]

### Goal Configuration

This goal expects to find a `<sources>` element specifying one/more `<source>` elements.  Each of which specifies a `<classes>` element with one/more `<class>` elements containing the fully qualified class name of a class to load metadata for.

The `<source>` element may also have other children as detailed in the configuration for the `airline:generate` goal however these are ignored for the purposes of the `airline:validate` goal.

## `airline:generate`