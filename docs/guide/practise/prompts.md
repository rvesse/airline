---
layout: page
title: Prompts
---

{% include req-ver.md version="2.8.0" %}

Often command line applications may need to prompt users for input e.g. asking for a password, obtaining a missing value, asking for a Yes/No response etc.  From 2.8.0 onwards we provide the `airline-prompts` module which provides a library for doing this in a consistent manner, this includes features such as:

- Prompt timeouts so non-interactive apps don't hang forever
- Configurable prompt sources
- Configurable prompt formatting
- Prompt for keys, strings, passwords, options (from a pre-configured list) or a strongly typed value

{% include toc.html %}

## Prompting

Prompting is done by creating an instance of a `Prompt<T>` where `T` is the type used for strongly typed option prompts.  Prompts can be manually instantiated or created using the `PromptBuilder<T>` which is described later on this page.  Once you have a prompt you can ask for a variety of different inputs depending on what your application needs to do.

### Prompting for a Key

The `prompt.promptForKey()` method reads a single character from the configured provider and returns its character code as an `int` e.g.

```java
int key = prompt.promptForKey();
```
### Prompting for a String

The `prompt.promptForLine()` method reads a line of input from the configured provider and returns it as a string e.g.

```java
String line = prompt.promptForLine();
```
 
### Prompting for a Password

The `prompt.promptForSecure()` method securely reads a line of input as a `char[]` from the configured provider assuming that it is capable of reading securely i.e. this may not work with all providers.

```java
char[] password = prompt.promptForSecure();
```

### Prompting for an Option

The `prompt.promptForOption()` method reads a line of input and attempts to match that to one of the configured options using the configured option matcher.  This can allow for matching values using custom logic, allowing the user to select options by numeric index etc.

This method takes a `boolean` parameter indicating whether the method calls `promptForLine()` or `promptForSecure()` prior to attempting to match the input to an option.  The type returned from this method will be the type parameter used for the prompt and the configured options.

```java
String option = prompt.promptForOption(false);
```

### Prompting for a Value

You can also prompt for any strongly typed value assuming that your configured [Type Converter](types.html) supports it e.g.

```java
MyCustomType value = prompt.promptForValue(MyCustomType.class, false);
```

## Prompt Builder

Generally a `PromptBuilder<T>` is used to specify the desired prompt.  For example consider the following simple example:

```java
Prompt<String> prompt 
  = new PromptBuilder<String>()
                .withPromptProvider(new ConsolePrompt())
                .withOptions("a", "b", "c")
                .withListFormatter()
                .withTimeout(100, TimeUnit.MILLISECONDS)
                .build();
```
This builds a prompt with option type `String` that uses `ConsolePrompt` for the provider, has three options and a timeout of 100 milliseconds.

Alternatively we could use the static methods in `Prompts` to provide a builder for some common prompts, e.g. the above could be written as follows:

```java
Prompt<String> prompt 
  = Prompts.newOptionsPrompt("Choose an option", "a", "b", "c")
           .build();
```

### Provider

The `PromptProvider` provides the ability to output prompt messages and read user input.  This can be configured using the `withPromptProvider(PromptProvider)` method on the builder as seen in the earlier example.

Alternatively you can use `withDefaultPromptProvider()` to configure with the default provider for your environment.  This will be `ConsolePrompt` if the JVM `System.console()` is available or `StdIOPrompt` otherwise.

The choice of provider may impact what functionality you can use, in particular whether a provider returns `true` or `false` for `supportsSecureReads()` controls whether you can use the `promptForSecure()` method of a prompt.

### Timeouts

The timeout controls how long users have to respond to prompts.  Having a timeout can be useful because it can prevent your application from hanging indefinitely if run in a non-interactive environment or allow you to fallback to alternative behaviour, fail fast etc. when no prompt response is received.

The timeout can be set with the `withTimeout()` method and `withTimeoutUnit()` methods e.g.

```java
Prompt<String> prompt
  = Prompts.newYesNoPrompt("Proceed with deletion?")
           .withTimeout(30)
           .withTimeoutUnit(TimeUnit.SECONDS)
           .build();
```

Here we set a 30 second timeout, we could also combine these into a single method call like so:

```java
Prompt<String> prompt
  = Prompts.newYesNoPrompt("Proceed with deletion?")
           .withTimeout(30, TimeUnit.SECONDS)
           .build();
```

### Options

The options functionality allows you to create prompts that provide users with a choice of options and determine which option the user selected.  Options are a strongly typed API that is configured when you create your prompt by the type parameter used for your `PromptBuilder<T>` instance.  We can configure one/more options like so:

```java
Prompt<String> prompt 
  = new PromptBuilder<String>()
           .withPromptProvider(new ConsolePrompt())
           .withOptions("Aardvark", "Badger", "Cougar")
           .withListFormatter()
           .withTimeout(30, TimeUnit.SECONDS)
           .build();
```

Here we configure three string options and we can then use `promptForOption()` to prompt the user to select from those options.  Note that we also use `withListFormatter()` which applies a formatter that will display those options to the user in a list.  We'll discuss formatters more later in this page.

#### Type Conversion

As with the rest of Airline we use our standard [Type Converter](types.html) API to control how options and input values are converted where necessary.  This is configured on a prompt builder via the `withTypeConverter(TypeConverter)` method.

#### Option Matching

TODO

### Formatters

TODO