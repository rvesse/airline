---
layout: page
title: Supported Types
---

{% include toc.html %}

Airline converts the string arguments that users pass into the JVM at the command line into the appropriate Java types using a `TypeConverter` and uses these values to populate `@Option` and `@Arguments` annotated fields.

## Default Type Converter

The `TypeConverter` may be specified as part of the [Parser Configuration](../parser/) but if not explicitly configured will use the `DefaultTypeConverter`.  Out of the box this will support most common types, any Java `enum` and any Java class that conforms to certain constraints.

### Common Types

All of the following common Java types and any derived type that may be assigned to them as determined by `Class.isAssignableFrom()` are supported:

- `String`
- `Boolean`
- `Byte`
- `Short`
- `Integer`
- `Long`
- `Float`
- `Double`

{% include req-ver.md version="2.9.0" %}

From 2.9.0 onwards the additional common Java types are supported:

- `BigInteger`
- `BigDecimal`

### `enum`

Any Java `enum` is automatically supported because the Java compiler provides a static `valueOf(String)` method that Airline can invoke to turn the string argument into an `enum` member.

### Java Classes

Similarly any other Java class that provides a static `valueOf(String)` can also have a string argument converted into a class instance in this way.

More generally Airline supports any Java class that meets one of the following constraints:

- Provides a static `valueOf(String)` method
- Provides a static `fromString(String)` method
- Provides a constructor which takes a single `String` argument

### Collections

Airline also supports any type which is a `Collection` of another supported type.

The advantage of using collection types e.g. `List<String>` is that your `@Option` or `@Arguments` annotated field stores all the values passed in.  If your field has a non-collection type then only the last use of that option/argument will be stored in the final class that Airline creates.

## Numeric Conversions

One advanced feature of Airline available for numeric fields e.g. `Integer` is the ability to customise the numeric formats supported.  By default we just use the standard `parseFrom(String)` method for numeric types which only permits the default representation of those types to be used.

Often it may be desirable to allow users to specify numeric values in more natural formats e.g. `1m` to represent `1000000`.  This is enabled on a per-field basis by setting a specific `typeConverterProvider` on your [`@Option`](../annotations/option.html) and [`@Arguments`](../annotations/arguments.html) definition.  Alternatively it may be enabled at a parser level by setting the `numericTypeConverter` on the [`@Parser`](../annotations/parser.html) annotation.

The following table details the built in alternative numeric formats that are supported out of the box:

| `TypeConverterProvider` | Support Formats |
| --------------------------------  | ---------------------- |
| `Binary` | Numbers expressed in binary e.g. `11` converts to `3` |
| `Octal` | Numbers expressed in octal e.g. `71` converts to  `57` |
| `Hexadecimal` | Numbers expressed in hexadecimal e.g. `F6` converts to `246` | 
| `KiloAs1000` | Numbers optionally shortened using postfix units where kilo is treated as 1000 e.g. `1k` converts to `1000` and `1b` converts to `1000000000` |
| `KiloAs1024` | Numbers optionally shortened using postfix units where kilo is treated as 1024 e.g. `1k` converts to `1024` and `1g` converts to `1073741824` |

There are also several abstract classes that can be used to implement alternative custom formats as desired such as `SequenceAbbreviatedNumericTypeConverter`.

Additionally if you wish to use the default logic you can also use `DefaultNumericConverter`.

## Type Converter Providers

Individual [`@Option`](../annotations/option.html) and [`@Arguments`](../annotations/arguments.html) can specify a `TypeConverterProvider` via their `typeConverterProvider` field which provides a way to override the type converter used on a per-field basis.  This can be used for features such as our [Numeric Conversions](#numeric-conversions) support.

## Custom Type Converters

To create a custom type converter that converts strings to Java objects in some other way(s) you will need to implement the `TypeConverter` interface.  This interface has a single method with the following signature:

```java
Object convert(String name, Class<?> type, String value);
```

Where `name` is the name of the option/argument we are trying to convert a value for, `type` is the target type to which we are trying to convert and `value` is the string value we are converting.

Often it may be easier to simply extend the default behaviour described on this page by extending the `DefaultTypeConverter` e.g.

```java
package com.github.rvesse.airline.examples.userguide.practise;

import com.github.rvesse.airline.ConvertResult;
import com.github.rvesse.airline.DefaultTypeConverter;

/**
 * An example of an extended type converter that adds support for converting
 * from types that provide an {@code parse(String)} method
 *
 */
public class ExtendedTypeConverter extends DefaultTypeConverter {

    @Override
    public Object convert(String name, Class<?> type, String value) {
        checkArguments(name, type, value);

        // Try and convert from a parse(String) method
        ConvertResult result = this.tryConvertStringMethod(type, value, "parse");
        if (result.wasSuccessfull())
            return result.getConvertedValue();

        // Fall back to default behaviour otherwise
        return super.convert(name, type, value);
    }
}
```

Here we define our `ExtendedTypeConverter` which overrides the `convert()` method to add an attempt to convert the type by looking for a `parse(String)` method on the type.  `ConvertResult` is a helper class used in `DefaultTypeConverter` to pass around the results of an attempted conversion.

### Using a Custom Type Converter

In order to use a custom type converter you will need to register it with your parser.  To do this you can use the [`@Parser`](../annotations/parser.html) annotation e.g.

```java
@Parser(typeConverter = ExtendedTypeConverter.class)
```

If you are creating a single command i.e. a single `@Command` annotated class then simply add this annotation to your class.  If you are creating a CLI i.e. a `@Cli` annotation then you can use the `parserConfiguration` field of the annotation like so:

```java
@Cli(name = "basic", 
    description = "Provides a basic example CLI",
    defaultCommand = GettingStarted.class, 
    commands = { GettingStarted.class, Tool.class },
    parserConfiguration = @Parser(typeConverter = ExtendedTypeConverter.class))
```

Or if you are creating the `ParserMetadata<T>` using the fluent `ParserBuilder<T>` API you can add your custom type converter like so:

```java
ParserBuilder<Runnable> builder 
  = new ParserBuilder<Runnable>()
           .withTypeConverter(new ExtendedTypeConverter());
```

### Using a Custom Type Converter on a per-field basis

As discussed earlier you can specify `TypeConverterProvider` on a per-field basis on your [`@Option`](../annotations/option.html) and [`@Arguments`](../annotations/arguments.html) to control the type converter used for that specific field.  A `TypeConverterProvider` has the following methods:

```java
<T> TypeConverter getTypeConverter(OptionMetadata option, ParseState<T> state)

<T> TypeConverter getTypeConverter(ArgumentsMetadata arguments, ParseState<T> state)
```

These methods should simply return the desired `TypeConverter`, for simplicity all of our `TypeConverter` implementations also implement `TypeConverterProvider` by returning `this`