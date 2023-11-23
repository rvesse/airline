---
layout: page
title: JDK Compatibility
---

### Source

Historically our source code was Java 7 compliant.  However, as of `3.0.0` we moved to Java 11 as the minimum supported
JDK version so expect our codebase to adopt more modern Java syntax as it continues to evolve.

### Build

Airline can be built with Java 11 or higher and our `pom.xml` contains appropriate profile customisations to enable 
this.  Regardless of the version built the `pom.xml` will target Java 11 byte code. 

Airline may not build with very recent JDKs if there are any incompatibilities with our choice of Maven plugins.

**NB** - If you are trying to build older versions from source the relevant `pom.xml` customisations may not have 
existed at that time.

### Runtime

Airline is built with Java 11 so can be used with Java 11 or above.

#### JPMS

{% include req-ver.md version="2.7.0" %}

Airline does make heavy use of reflection and therefore will likely not work on the Module Path without exporting 
packages that contain your CLI and Command classes as part of your `module-info.java`.  For example:

```java
module com.yourdomain.yourmodule
{
    requires com.github.rvesse.airline;
    
    exports com.yourdomain.yourpackage;
    
    // As Airline is driven by reflection over the annotations on your classes you need 
    // to open packages containing Airline annotated types to the com.github.rvesse.airline module
    opens com.yourdomain.yourpackage to com.github.rvesse.airline;
}
```

{% include req-ver.md version="3.0.0" %}

Prior to `3.0.0` Airline only provided basic `module-info.java` files, as of `3.0.0` these have been properly 
handcrafted to provide full JPMS compatibility.

If you are using any of the Airline annotations that locate resources e.g. `@Version` then you **MAY** need to
explicitly open the package containing those resources to `com.github.rvesse.airline`. The new `ModulePathLocator`
introduced in `3.0.0` should allow locating such resources when appropriate `opens` declarations are present in your
`module-info.java`.

You may find that Airline is unable to locate some resources with its default configuration.  If this is the case you
can add the `airline-jpms-resources` module as an additional dependency and reference the `JpmsResourceLocator.class` in
the relevant field of your annotations e.g. `sourceLocators` for the
[`@Version`](../annotations/version.html#resource-locators) annotation.

If you encounter a problem with this please report it at https://github.com/rvesse/airline/issues
