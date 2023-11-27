---
layout: page
title: Resource Locators
---

{% include req-ver.md version="2.6.0" %}

Resource Locators are a new abstraction introduced from 2.6.0 to allow configurability of how other subsystems, such as
[User Aliases](aliases.html), locate resources.  It abstract a mechanism that was previously hard coded in order to
allow users to extend resource location as desired.  The following locators are provided:

| Locator | Behaviour |
|--------------|----------------|
| {% include javadoc-ref.md class="FileLocator" package="parser.resources" %} | Locates resources on the file system using the exact locations given. |
| {% include javadoc-ref.md class="HomeDirectoryLocator" package="parser.resources" %} | Locates resources in the users home directory.  Search locations beginning with `~/` or `~\` (OS dependent) are treated as expanded to the users home directory. |
| {% include javadoc-ref.md class="WorkingDirectoryLocator" package="parser.resources" %} | Locates resources in the applications working directory.  Search locations beginning with `./` or `.\` (OS dependent) are treated as expanded to the applications working directory. |
| {% include javadoc-ref.md class="EnvVarLocator" package="parser.resources" %} | Locates resources on the filesystem where the search locations given may contain `${VAR}` placeholders to refer to environment variables. |
| {% include javadoc-ref.md class="JvmSystemPropertyLocator" package="parser.resources" %} | Locates resources on the filesystem where the search locations given may contain `${var}` placeholders to refer to JVM system properties i.e. those passed to the JVM via the `-Dvar=value` flag. |
| {% include javadoc-ref.md class="ClasspathLocator" package="parser.resources" %} | Locates resources on the JVM class path. |
| {% include javadoc-ref.md class="ModulePathLocator" package="parser.resources" %} | Locates resources on the JVM module path. |
| {% include javadoc-ref.md class="JpmsResourceLocator" package="parser.resources.jpms" module="airline-jpms-resources" %} | Locates resources on the JVM Module Path, especially in the case where modules are not open to each other. |

This makes it possible to configure CLIs that have intelligent behaviours e.g. resolving user aliases from environment variable driven locations.

### Ambiguous Locations

Sometimes a search location given may be ambiguous in that it may be successfully resolved by multiple of the configured
locators.  Therefore the order in which you configure your resource locators matters since the first one that
successfully opens the resource will be used.

In some cases it may be desirable to force a particular locator to be favoured over another e.g. filesystem vs JVM
classpath.  Several of the locators are implemented such that they permit URI-style prefixes to be added to the search
location so that only the desired locator will process that location.  You can add `file://` to your search locations to
force the `FileLocator` to be used and `classpath://` to force the `ClasspathLocator` to be used.

### JPMS Resource Location

{% include req-ver.md version="3.0.0" %}

When running on the Java Module Path resource location is somewhat more complicated as in order for a resource to be
accessible to another module like Airline the package in which it is contained **MUST** match the name of the module
that contains it, and be declared as unconditionally `opens` in the `module-info.java` for your module.

From 3.0.0 onwards a `ModulePathLocator` was added to the core `airline` module so that it can successfully locate
resources on the module path when the above conditions are met.

{% include req-ver.md version="3.0.0" module="airline-jpms-resources" %}

Howeverl in some cases this may be insufficientl e.g. you want to read a resource from a module whose `module-info.java`
you do not control and thus cannot change the `opens` declarations. To address this from 3.0.0 onwards a
`JpmsResourceLocator` was added in a separate `airline-jpms-resources` module since it requires additional dependencies.
This resource locator is capable of locating resources when Airline is run on the Module Path where the stronger
encapsulation may make accessing resources via the standard `ClasspathLocator`/`ModulePathLocator` fail.

Please refer to the [JPMS Notes](jdk.html#jpms) for more information on running Airline in a JPMS context.

