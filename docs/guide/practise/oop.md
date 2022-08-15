---
layout: page
title: Inheritance and Composition
---

{% include toc.html %}

## Inheritance

When you define a class as being a [`@Command`](../annotations/command.html) Airline will automatically discover command
metadata by examining the class hierarchy of the annotated class.  By this we mean that it will walk up the hierarchy to
base classes to discover additional Airline annotations that are inherited by the command class.  This means that you
can use standard inheritance to define base classes that contain [`@Option`](../annotations/option.html) definitions
that can be inherited by multiple command implementations.

For example we might want to have all our commands have a verbose option available e.g.

```java
public abstract class BaseCommand implements ExampleRunnable {

    @Option(name = { "-v", "--verbose" }, description = "Enables verbose mode")
    protected boolean verbose = false;
}

@Command(name = "maybe-verbose")
public abstract class MaybeVerboseCommand extends BaseCommand {

    @Override
    public int run() {
        if (this.verbose) {
            System.out.println("Verbose");
        } else {
            System.out.println("Normal");
        }
        return 0;
    }
    
    public static void main(String[] args) {
        ExampleExecutor.executeSingleCommand(MaybeVerboseCommand.class, args);
    }
}
```

Note that we still need to follow normal inheritance best practises about field visibility, in the above example the
field in the parent class is marked as `protected` so that we can access in the child class.

### Option Overriding

When we have larger class hierarchies it may be desirable to override the implementation of a option to be more specific
to a given command.  Details on overriding options are given in the documentation of the [`@Option`
Overriding](../annotations/option.html#overrides-and-sealed) annotation.

When you override an option Airline will still populate all the relevant fields individually.  So for example if you
override an option in a child class both the parent and child field for that option will get populated if the user
specifies the option.  This ensures that any logic in the parent and child can use the field values as they would
usually.

#### Option Restrictions

If you are using [Restrictions](../restrictions/) then any restrictions defined on an option are controlled by the
deepest definition of the option with restrictions.  This means that you can change the restrictions on an option by
defining new restrictions on an override.  Equally if you don't define any restrictions with an override you
automatically inherit any restrictions defined by the parent definition.

If you want to remove the restrictions on an overridden option you can use the special
[`@Unrestricted`](../annotations/unrestricted.html) annotation to denote this.

### Help Section Inheritance

If you are using Help Annotations e.g. [`@Discussion`](../annotations/discussion.html) then any help sections defined
are automatically inherited by child classes.  For example you might wish to define a
[`@Copyright`](../annotations/copyright.html) annotation on your base class to automatically add a Copyright section to
all your commands.

If the same help annotation is defined multiple times in a class hierarchy the deepest definition is used.  So if your
parent defines an `@Copyright` annotation and your child class also defines an `@Copyright` annotation then your child
definition will be used.

If you wish to hide an inherited help section the special [`@HideSection`](../annotations/hide-section.html) annotation
can be used to do this.

## Composition

Additionally we may want to break out sets of related options into reusable modules and compose these together into our
classes.  When Airline is scanning the command class for annotated fields it will also scan any field marked with the
[`@AirlineModule`](../annotations/module.html) annotation.  The class for that field will be scanned and any further
annotations included into the command metadata.

{% include alert.html %}
The default annotation for this changed to `@AirlineModule` in the **2.9.0**, see 
[Historical Composition](#historical-composition) for past alternatives.
{% include end-alert.html %}


For example if we wanted to make our verbose option reusable across commands without any common ancestor we could do the
following:

```java
public class VerbosityModule {

    @Option(name = { "-v", "--verbosity" }, arity = 1, title = "Level", description = "Sets the desired verbosity")
    // The AllowedRawValues annotation allows an option to be restricted to a given set of values
    @AllowedRawValues(allowedValues = { "1", "2", "3" })
    public int verbosity = 1;
}

@Command(name = "module-reuse", description = "A command that demonstrates re-use of modules and composition with locally defined options")
public class ModuleReuse implements ExampleRunnable {

    @AirlineModule
    private HelpOption<ExampleRunnable> help;

    /**
     * A field marked with {@link AirlineModule} will also be scanned for options
     */
    @AirlineModule
    private VerbosityModule verbosity = new VerbosityModule();

    @Arguments
    private List<String> args = new ArrayList<String>();

    public static void main(String[] args) {
        ExampleExecutor.executeSingleCommand(ModuleReuse.class, args);
    }

    @Override
    public int run() {
        if (!help.showHelpIfRequested()) {
            System.out.println("Verbosity is " + verbosity.verbosity);
            System.out.println("Arguments were " + StringUtils.join(args, ", "));
        }
        return 0;
    }
}
```

Note that we are able to compose as many other classes as we want by defining multiple fields annotated with
`@AirlineModule`. Note that when accessing these options we have to access them via their originating fields so again we
need to be aware of field visibility when composing modules together.

### Historical Composition

In versions of Airline prior to **2.9.0** we used the `javax.inject.Inject` annotation to achieve composition, so in the
above example you would replace `@AirlineModule` with `@Inject` and you may still see this used in old example code or
projects using older Airline releases.

However multiple users have reported over the years that this conflicts with its usage by dependency injection
frameworks.  Additionally with the JavaEE `javax.*` packages moving under the control of the Eclipse Foundation and
being repacked as `jakarta.*` package instead there are now conflicts between different versions of the same annotation.
Therefore the decision was taken as of **2.9.0** to change the annotation used for this to
[`@AirlineModule`](../annotations/module.html).

For backwards compatibility the 2.9.x releases continue to support the old `@Inject` annotation so there is no breaking 
change for existing users upgrading to newer Airline releases.  Currently the following annotations may be used for 
composition:

- `com.github.rvesse.airline.annotations.AirlineModule`
- `javax.inject.Inject`
- `jakarta.inject.Inject`
- `com.google.inject.Inject`

{% include alert.html %} 
While the above list represents the currently supported annotations future releases beyond
2.9.x will only support `@AirlineModule` by default.  Therefore, users may wish to migrate to using this new annotation
sooner rather than later.  2.9.0 onwards does support [Configurable Composition](#configurable-composition) so users 
will be able to use whichever annotations they see fit for their use cases.
{% include end-alert.html %}

In order to support this backwards compatibility we continue to have dependencies on both the `jakarta.inject` and
`javax.inject` modules.  The latter is indirectly depended upon via a temporary `airline-backcompact-javaxinject` to
make it clear that this is for backwards compatibility only and can be excluded for users who have moved to using 
the new [`@AirlineModule`](../annotations/module.html) annotation. As noted in the above warning these dependencies 
will become `optional` in future releases and eventually be removed as mandatory dependencies.

{% include alert.html %}
As of 2.10.0 the above dependencies have been made `optional` meaning that you **MUST** explicitly provide those 
dependencies yourself if you wish to continue using `@Inject` annotations for composition.
{% include end-alert.html %}

### Configurable Composition

{% include req-ver.md version="2.9.0" %}

As of **2.9.0** the annotations considered to mark a field as being used for composition, i.e. a field whose value type
Airline should scan for further Airline annotations, became fully configurable.

You can configured this via the `compositionAnnotationClasses` field of your
[`@Parser`](../annotations/parser.html#composition-annotations) annotation.  This takes an array of strings indicating
annotation classes that you want Airline to consider as composition annotations e.g.

```java
@Parser(compositionAnnotationClasses = {
  "com.github.rvesse.airline.annotations.AirlineModule",
  "javax.inject.Inject"
})
```

Would configure the parser to treat both [`@AirlineModule`](../annotations/module.html) and `@Inject` as composition
annotations.  This field is specified using string class names rather than `Class` objects to enable the backwards
compatibility described [above](#historical-composition) and to allow us to drop the extra dependencies in the future
without breaking existing consumers of the library.

If you choose to use additional annotations you may need to explicitly declare additional Maven dependencies to ensure
those annotation classes are available at runtime.