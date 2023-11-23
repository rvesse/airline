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
              <goal>package</goal>
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
                <class>com.github.rvesse.airline.tests.args.Args1</class>
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
            <phase>package</phase>
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

Contains one or more `<source>` elements that specifies source(s) for which help should be generated e.g.

```xml
<sources>
  <source>
    <classes>
      <class>some.package.YourCommand</class>
    </classes>
  </source>
</sources>
```

A `<source>` element may optionally have an `<options>` child element to define source specific options.

If this element is empty then the build may fail depending on the value of the `<failOnNoSources>` element.

### Child Elements

These elements are all supplied as child elements to the specified elements.

#### `<class>`

*Applicable Goals:* `airline:generate` and `airline:validate`  
*Child Element Of:* `<classes>`

Specifies a fully qualified class names to treat as a source.  This class should be `@Cli` or `@Command` annotated.  If the class specified does not exist then the class will either be ignored or fail the build depending on the value of the `<skipBadSources>` element.

#### `<classes>`

*Applicable Goals:* `airline:generate` and `airline:validate`  
*Child Element Of:* `<sources>`  
*Child Elements:* `<class>`

Contains one or more `<class>` elements that specify fully qualified class names to treat as sources.

#### `<columns>`

*Applicable Goals:* `airline:generate`  
*Child Element Of:* `<defaultOptions>` and `<options>`

Specifies the integer number of columns to use for column wrapped output formats, defaults to **79** e.g.

```xml
<columns>70</columns>
```

#### `<format>`

*Applicable Goals:* `airline:generate`  
*Child Element Of:* `<formats>` and `<mapping>`

Specifies the name of an output format.  By default Airline recognizes the names `BASH`, `CLI`, `HTML`, `MAN` and `MARKDOWN` as referring to built in help formats.  However this mapping can be redefined by the `<formatMappings>` element as desired.

```xml
<format>MAN</format>
```

#### `<includeHidden>`

*Applicable Goals:* `airline:generate`  
*Child Element Of:* `<formats>` and `<mapping>`

Specifies whether hidden groups, commands and options should be included in the output.  Defaults to **false**

```xml
<includeHidden>true</includeHidden>
```

#### `<manSection>`

*Applicable Goals:* `airline:generate`  
*Child Element Of:* `<formats>` and `<mapping>`

Specifies what man section should be used for the output for formats that respect man section.  Defaults to **1** which is General Commands.

```xml
<manSection>8</manSection>
```

#### `<mapping>`

*Applicable Goals:* `airline:generate`  
*Child Element Of:* `<formatMappings>`

Defines a relationship between a format name used in a `<format>` element to an underlying {% include javadoc-ref.md class="FormatProvider" package="maven.formats" module="airline-maven-plugin" %}.  Can also optionally specify default options for the format which would override `<defaultOptions>` but can also later be overridden by `<source>` options.

```xml
<mapping>
  <format>CUSTOM</format>
  <provider>some.package.YourFormatProvider</provider>
  <options>
    <columns>100</columns>
  </options>
</mapping>
```

This allows for configuring the plugin to use custom help formats provided by 3rd party modules.

#### `<multiFile>`

*Applicable Goals:* `airline:generate`  
*Child Element Of:* `<formats>` and `<mapping>`

Specifies whether output should be generated to multiple files when the format supports it.  So to take Man pages for example when set to **true** will generate an individual output file per CLI, group and command whereas when set to **false** generates a single output file.  Defaults to **false**.

```xml
<multiFile>true</multiFile>
```

#### `<outputMode>`

*Applicable Goals:* `airline:generate`  
*Child Element Of:* `<source>`

Specifies the desired output type for the source.  Acceptable values are `DEFAULT`, `CLI`, `GROUP` and `COMMAND`.

- `DEFAULT` causes the output to be source dependent, so `@Cli` sources generate CLI help while `@Command` sources generate command help
- `CLI` requests CLI output
- `GROUP` requests command group output
- `COMMAND` requests command output

If you request an output mode for a source that is not compatible then that source will either be skipped or fail the build depending on the value of `<failOnUnsupportedOutputMode>`.  Similarly not all formats support all output modes so again certain combinations of formats and output modes will result in either no output or a build failure.

#### `<options>`

*Applicable Goals:* `airline:generate`  
*Child Elements*: `<columns>`, `<includeHidden>`, `<manSection>`, `<multiFile>` and `<properties>`

Provides the formatting options for the parent source/format.  See documentation for the child elements for their purpose and defaults.

For example:

```xml
<options>
  <columns>120</columns>
  <includeHidden>false</includeHidden>
  <multiFile>true</multiFile>
  <properties>
    <key>value</key>
  </properties>
</options>
```

#### `<properties>`

*Applicable Goals:* `airline:generate`  
*Child Element Of:* `<formats>` and `<mapping>`

Specifies additional custom properties that may be used by output formats to further customise their output.  This allows for free-form key value pairs where interpretation is up to the format provider without needing to extend the plugin to introduce new configuration.

```xml
<properties>
  <stylesheet>css/my-style.css</stylesheet>
</properties>
```

The above example can be used with the built-in `HTML` format to customise the stylesheet linked into the output.

#### `<provider>`

*Applicable Goals:* `airline:generate`  
*Child Element Of:* `<mapping>`

Specifies the provider for a format.  This can either be a fully qualified class name or can be the special value `default`.  In the case of `default` whatever the default provider for the name declared by the `<format>` element of the `<mapping>` is will be used.  Default providers are discovered using the JDK `ServiceLoader` so provided your custom provider is present on the plugins classpath and has an appropriate `com.github.rvesse.airline.maven.formats.FormatProvider` file in its `META-INF/services` folder then using the `<provider>` element will be unecessary.

```xml
<provider>some.package.YourCustomProvider</provider>
```

### Option Precedence

As described above formatting options can be specified at several levels of configuration.  The resulting formatting options for each output are the result of merging the options in the following order:

- Top level options from `<defaultOptions>`
- Format options from `<formatMappings>/<mapping>/<options>`
- Source options from `<sources>/<source>/<options>`

i.e. the most specific options specified take precedence but respect any options that are not overridden.

Here's an example configuration that does all of the above:

```xml
      <plugin>
        <groupId>com.github.rvesse</groupId>
        <artifactId>airline-maven-plugin</artifactId>
        <version>{{ site.version }}</version>
        <configuration>
          <defaultOptions>
            <includeHidden>true</includeHidden>
          </defaultOptions>
          <formats>
            <format>CLI</format>
          </formats>
          <formatMappings>
            <mapping>
              <format>CLI</format>
              <options>
                <columns>68</columns>
                <includeHidden>false</includeHidden>
                <multiFile>true</multiFile>
              </options>
            </mapping>
          </formatMappings>
          <sources>
            <source>
              <classes>
                <class>com.github.rvesse.airline.tests.args.Args1</class>
              </classes>
              <options>
                <columns>100</columns>
               </options>
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
Here options are defined at all three levels, since the most specific options take effect the resulting options for our `Arg1` class are as follows:

- `columns` is 100 (overriden at source level)
- `multiFile` is `true` (set at format level)
- `includeHidden` is `false` (overridden at source level)