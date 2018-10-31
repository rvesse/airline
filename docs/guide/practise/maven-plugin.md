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

The `generate` goal is used to generate help for your [`@Cli`](../annotations/cli.html) or [`@Command`](../annotations/command.html) annotated classes.  It can be used to generate multiple formats of help, including for custom formats not provided by Airline itself, and can customise help output on a global, per-format or per-source level.

For example:

```xml
<plugin>
        <groupId>com.github.rvesse</groupId>
        <artifactId>airline-maven-plugin</artifactId>
        <version>{{ site.version }}</version>
        <configuration>
          <formats>
            <format>MAN</format>
            <format>CLI</format>
            <format>MARKDOWN</format>
          </formats>
          <sources>
            <!-- Separate source elements rather than multiple class under a single source element -->
            <source>
              <classes>
                <class>com.github.rvesse.airline.args.Args1</class>
              </classes>
            </source>
            <source>
              <classes>
                <class>com.github.rvesse.airline.examples.userguide.BasicCli</class>
              </classes>
            </source>
          </sources>
        </configuration>
        <executions>
          <execution>
            <goals>
              <goal>generate</goal>
            </goals>
          </execution>
        </executions>
      </plugin>
```

Here we generate help in three formats for two different sources.

### Goal Configuration

This goal expects to find a `<sources>` element specifying one/more `<source>` elements.  Each of which specifies a `<classes>` element with one/more `<class>` elements containing the fully qualified class name of a class to load metadata for.
Each `<source>` element may also have an optional `<outputMode>` element and an `<options>` element.  The details of these are described below.


## Configuration Elements

The following configuration elements are supported by the plugin.  For each we note where they may be used, permitted child elements, example usage and descriptions of their functionality.

### Top Level Elements

These elements are all supplied directly as children of the plugin declarations `<configuration>` element.

#### `<defaultOptions>`

*Applicable Goals:* `airline:generate`
*Child Elements*: `<columns>`, `<includeHidden>`, `<manSection>`, `<multiFile>` and `<properties>`

Provides the default formatting options used to configure each requested output format.  These defaults can be overridden at both a `<format>` and `<source>` level.  See documentation for the child elements for their defaults.

For example:

```xml
<defaultOptions>
  <columns>120</columns>
  <includeHidden>false</includeHidden>
  <multiFile>true</multiFile>
  <properties>
    <key>value</key>
  </properties>
</defaultOptions>
```

#### `<failOnNoSources>`

*Applicable Goals:* `airline:generate`, `airline:validate`

Takes a Boolean value indicating whether the goal should fail the build if no sources are specified.  Defaults to **true**

```xml
<failOnNoSources>false</failOnNoSources>
```

#### `<failOnUnknownFormat>`

*Applicable Goals:* `airline:generate`

Takes a Boolean value indicating whether the goal should fail the build if a format is requested that is not either provided by Airline or registered via `<formatMappings>`.  Defaults to **true**

```xml
<failOnUnknownFormat>false</failOnUnknownFormat>
```

#### `<failOnUnsupportedOutputMode>`

*Applicable Goals:* `airline:generate`

Takes a Boolean value indicating whether the goal should fail the build if an unsupported output mode is requested for a source.  Defaults to **false**

When **false** an unsupported output mode simply results in a warning and help not being generated for some/all formats.

```xml
<failOnUnsupportedOutputMode>true</failOnUnsupportedOutputMode>
```

#### `<formatMappings>`

*Applicable Goals:* `airline:generate`
*Child Elements:* `<mapping>`

Contains one or more `<mapping>` elements used to configure available output formats e.g.

```xml
<formatMappings>
  <mapping>
    <format>CUSTOM</format>
    <provider>some.package.YourHelpProvider</provider>
    <options>
      <!-- Formatting options for this format -->
    </options>
  </mapping>
</formatMappings>
```

#### `<formats>`

*Applicable Goals:* `airline:generate`
*Child Elements:* `<format>`

Contains one or more `<format>` elements indicating desired output formats.  The separate `<formatMappings>` element is used to configure the available output formats e.g.

```xml
<formats>
  <format>MAN</format>
  <format>CLI</format>
</formats>
```

If no formats are specified then `MAN` is the default.

#### `<outputDirectory>`

*Applicable Goals:* `airline:generate`

Specifies the directory to which generated help output will be written.  Defaults to **${project.build.directory}/help/**


```xml
<outputDirectory>${project.build.directory}/man-pages/<outputDirectory>
```

#### `<skipBadSources>`

*Applicable Goals:* `airline:generate`

Takes a Boolean value indicating whether the goal should skip invalid sources.  Defaults to **true**

When **false** an invalid source will fail the build.

```xml
<skipBadSources>false</skipBadSources>
```

#### `<sources>`

*Applicable Goals:* `airline:generate`, `airline:validate`  
*Child Elements:* `<source>`

Specifies one or more `<source>` elements that specifies source(s) for which help should be generated e.g.

```xml
<sources>
  <source>
    <classes>
      <class>some.package.YourCommand</class>
    </classes>
  </source>
</sources>
```

### Child Elements

These elements are all supplied as child elements to the relevant top level elements.

#### `<columns>`

*Applicable Goals:* `airline:generate`  
*Child Element Of:* `<defaultOptions>` and `<options>`

Specifies the integer number of columns to use for column wrapped output formats e.g.

```xml
<columns>70</columns>
```

#### `<format>`

*Applicable Goals:* `airline:generate`  
*Child Element Of:* `<formats>` and `<mapping>`

Specifies the name of an output format.  By default Airline recognizes the names `BASH`, `CLI`, `HTML`, `MAN` and `MARKDOWN` as referring to built in help formats.  However this mapping can be redefined by the `<formatMappings>` element as desired.

#### `<includeHidden>`

#### `<manSection>`

#### `<mapping>`

#### `<multiFile>`

#### `<outputMode>`

*Child Element Of:* `<source>`

#### `<options>`

#### `<properties>`

#### `<provider>`