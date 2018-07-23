---
layout: page
title: Custom Help Sections
---

You can of course like most things in Airline extend the [help sections](sections.html) system with your own custom help section.

Creating a custom help section requires you to do the following:

1. Define an annotation for your section
2. Create an implementation of `HelpSection` for your section
3. Create a `HelpSectionFactory` that can create your help section from your annotation
4. Create a `ServiceLoader` manifest that registers your factory

#### Defining an annotation

Firstly we need to create an annotation that we will apply to our classes to specify our custom help section.  In this example we define a `@RelatedCommands` annotation:

```java
package com.github.rvesse.airline.examples.userguide.help.sections.custom;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target({ TYPE })
@Documented
public @interface RelatedCommands {

    /**
     * Provides a list of related commands
     * 
     * @return Related commands
     */
    String[] commands() default {};
}
```

This is a fairly simple annotation that simply takes in an array of commands that are related to our command.

The most important aspect here is the `@Retention` annotation to specify that this annotation is retained and available at runtime.  We also use the `@Target` annotation to constrain where users may apply our annotation.

#### Creating a `HelpSection`

Airline provides several useful base classes to make it easier to implement custom sections.  For a start the {% include github-ref.md class="BasicSection" package="help.sections" %} class provides a basic implementation of the entire interface that simply requires you to pass all the information into the constructor e.g.

```java
package com.github.rvesse.airline.examples.userguide.help.sections.custom;

import com.github.rvesse.airline.help.sections.HelpFormat;
import com.github.rvesse.airline.help.sections.common.BasicSection;
import com.github.rvesse.airline.help.sections.common.CommonSections;

public class RelatedCommandsSection extends BasicSection {

    public RelatedCommandsSection(String[] commands) {
        super("Related Commands", CommonSections.ORDER_EXAMPLES + 1,
                "The following related commands may also be of interest:", null, HelpFormat.LIST, commands);
    }

}
```

Note how we only need to define a constructor here that passes all the relevant arguments to the base implementation.

For even more simplistic sections which are just textual you can likely simply extend {% include github-ref.md class="ProseSection" package="help.sections" %} instead.

#### Creating a `HelpSectionFactory`

Now we have our annotation and our section implementation we can create a factory that knows how to convert the annotation into the help section e.g.

```java
package com.github.rvesse.airline.examples.userguide.help.sections.custom;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

import com.github.rvesse.airline.help.sections.HelpSection;
import com.github.rvesse.airline.help.sections.factories.HelpSectionFactory;

public class RelatedCommandsFactory implements HelpSectionFactory {

    @Override
    public HelpSection createSection(Annotation annotation) {
        if (annotation instanceof RelatedCommands) {
            RelatedCommands related = (RelatedCommands) annotation;
            return new RelatedCommandsSection(related.commands());
        } else {
            return null;
        }
    }

    @Override
    public List<Class<? extends Annotation>> supportedAnnotations() {
        return Collections.<Class<? extends Annotation>>singletonList(RelatedCommands.class);
    }

}
```

Here we can see that our factory declares that it supports our `@RelatedCommands` annotation and creates an instance of our actual help section `RelatedCommandsSection`

#### Creating a `ServiceLoader` manifest

Airline detects and processes the available help sections using Java's `ServiceLoader` mechanism for dynamic loading of extension points.  This requires manifest files to be placed under `META-INF/services` inside your JAR files, in a typical Maven build environment you will place these under `src/main/resources/META-INF/services` to ensure they are output in the correct place within the resulting JAR file.

You will need to add a manifest for the factory interfaces you are implementing.  So for our example on this page we need to add the manifests `com.github.rvesse.airline.help.sections.factories.HelpSectionFactory`

Each line in the manifest simply is the class name of our factory implementations, so for our example we would need to add the following line to both manifests:

```
com.github.rvesse.airline.examples.userguide.help.sections.custom.RelatedCommandsFactory
```

{% include alert.html %}
`ServiceLoader` will scan all manifests it can find anywhere on the classpath so they may be present across multiple JARs on your classpath.

If you are using a build process that combines/repackages JARs (e.g. Maven Shade) please make sure that you are appropriately handling merging of these manifests as otherwise some restrictions may no longer be usable.
{% include end-alert.html %}