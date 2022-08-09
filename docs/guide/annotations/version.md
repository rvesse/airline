---
layout: page
title: Version Annotation
---

## `@Version`

The `@Version` annotation may be applied to classes and provides a version statement that will be included in [Help](../help/) as an additional section.

To use it simply add it like so:

```java
@Version(sources = "/version-info.txt")
```
This would look for the file `version-info.txt` either on the classpath or the local file system by default.  The search
locations can be customised by specifying [Resource Locators](#resource-locators).

It assumes that this file is either a Java properties or a Java Manifest file.  It then grabs information from
specific properties in that file to create the output.  The properties examined can be customised as desired, if no such
property exists that particular piece of output will be omitted.  The following table lists the available properties
that can be used:

| `Annotation Field` | Default Value | Usage |
| ------------------------ | ------------------ | -------- |
| `versionProperty`  | `version` | Version information |
| `componentProperty` | `component` | Component Name |
| `buildProperty` | `build` | Build information |
| `dateProperty` | `buildDate` | Build Date information |

For example:

```java
@Version(sources = "/version-info.txt", 
         versionProperty = "ver", 
         buildProperty = "bld")
```
Would look for version information specified in the `ver` and `bld` properties.

### Multiple Components

If multiple `sources` are specified as an array then each file will be separately processed and its content rendered as
a block.  In this way you can provide information for multiple components in your system e.g.

```java
@Version(sources = [ "/a.version", "/b.version" ])
```

Would provide information from both `a.version` and `b.version`

### Additional Information

If you want to include additional version related information in this section the `additionalProperties` and
`additionalTitles` can be used to specify extra properties to read and display e.g.

```java
@Version(sources = "/version-info.txt", 
         additionalProperties = [ "author", "commit" ],
         additionalTitles = [ "Author", "Commit" ])
```

Would grab the `author` and `commit` properties from your version information files and title them `Author` and `Commit`
in the output

### Error Handling

If the version information may not be present you may wish to add `suppressOnError = true` to your annotation.  When set
any errors in obtaining version information are silently ignored.

```java
@Version(sources = "/version-info.txt", suppressOnError = true)
```

### Tabular Output

If you prefer the version information to be presented in a tabular format rather than a list format you can ask for this
by specifying `tabular = true` in your annotation e.g.

```java
@Version(source = "/version-info.txt", tabular = true)
```

### Resource Locators

If your version information is stored somewhere unusual, or needs a custom [Resource
Locator](../practise/resource-locators.html) to locate then you can specify this via the `sourceLocators` field e.g.

```java
@Version(source = "/version-info.txt", sourceLocators = { ClasspathLocator.class, JpmsResourceLocator.class })
```

In this example we use `ClasspathLocator` and `JpmsResourceLocator` to ensure that our `version-info.txt` can be located
whether our application runs via the Class Path or the Module Path.