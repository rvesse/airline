---
layout: page
title: JDK Compatibility
---

### Source

Our source code is Java 7 compliant.

### Build

Airline can be built with Java 7, 8, 9 or 10 and our `pom.xml` contains appropriate profile customisations to enable this.  Regardless of the version built the `pom.xml` will target Java 7 byte code.  Our official releases are built with Java 7 to maximise version compatibility.

Airline cannot currently be built with Java 11 due to Maven plugin compatibility issues.

**NB** - If you are trying to build older versions from source the relevant `pom.xml` customisations may not have existed at that time.

### Runtime

Airline is built with Java 7 so can be used with Java 7 or above.

#### JPMS

Airline does make heavy use of reflection and therefore will likely not work on the Module Path without exporting packages that contain your CLI and Command classes as part of your `module-info.java`

Current `2.7.0-SNAPSHOT` builds include contributions from our user community to enable use of Airline on the Module Path including relevant `module-info.java` and `Automatic-Module-Name` manifest entries.  If you encounter a problem with this please report it at https://github.com/rvesse/airline/issues