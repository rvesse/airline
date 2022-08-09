---
layout: page
title: JDK Compatibility
---

### Source

Our source code is Java 7 compliant.

### Build

Airline can be built with Java 7 or higher and our `pom.xml` contains appropriate profile customisations to enable 
this.  Regardless of the version built the `pom.xml` will target Java 7 byte code. 

Airline may not build with very recent JDKs if there are any incompatibilities with our choice of Maven plugins.

**NB** - If you are trying to build older versions from source the relevant `pom.xml` customisations may not have 
existed at that time.

### Runtime

Airline is built with Java 7 so can be used with Java 7 or above.

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

{% include req-ver.md version="2.10.0" %}

Prior to `2.10.0` Airline only provided basic `module-info.java` files, as of `2.10.0` these have been properly 
handcrafted to provide much improved JPMS compatibility.

If you are using any of the Airline annotations that locate resources e.g. `@Version` then you **MAY** need to 
explicitly open the package containing those resources to `com.github.rvesse.airline` and to `io.github.classgraph`. 
Where your resources are in the root of your package this **MAY** be unnecessary.

You may find that Airline is unable to locate some resources with its default configuration.  If this is the case you
can add the `airline-jpms-resources` module as an additional dependency and reference the `JpmsResourceLocator.class` in
the relevant field of your annotations e.g. `sourceLocators` for the
[`@Version`](../annotations/version.html#resource-locators) annotation.

If you encounter a problem with this please report it at https://github.com/rvesse/airline/issues
