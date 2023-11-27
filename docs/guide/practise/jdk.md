---
layout: page
title: JDK Compatibility
---

### Source

Historically our source code was Java 7 compliant.  However, as of `3.0.0` we moved to Java 11 as the minimum supported
JDK version so expect our codebase to adopt more modern Java syntax as it continues to evolve.

### Build

Airline can be built with Java 11 or higher.  Regardless of the version built the `pom.xml` will target Java 11 byte
code. 

Airline may not build with very recent JDKs if there are any incompatibilities with our choice of Maven plugins and/or
dependencies.  Please report these issues via our GitHub so we can address them.

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
authored to provide full JPMS compatibility.

If you are using any of the Airline annotations that locate resources e.g. `@Version` then your resources **MUST**
satisfy the following constraints to continue to be usable:

- They should be placed in a package that matches your modules name e.g. if your package is `com.yourdomain.app` then
  your resources should be in that package
- The package containing the resources  needs to be unconditionally open

If these constraints are met then the new `ModulePathLocator` introduced in `3.0.0` should allow locating such
resources.

If you cannot meet these constraints then your resources will no longer be usable if you run your application on the
 Module Path. If this is the case you can add the `airline-jpms-resources` module as an additional dependency and
reference the `JpmsResourceLocator.class` in the relevant field of your annotations e.g. `sourceLocators` for the
[`@Version`](../annotations/version.html#resource-locators) annotation, this uses the
[ClassGraph](https://github.com/classgraph/classgraph) to locate resources at the cost of breaking the strong
encapsulation that modules are intended to provide.

If you encounter a problem with this please report it at https://github.com/rvesse/airline/issues
