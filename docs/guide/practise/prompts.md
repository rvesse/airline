---
layout: page
title: Prompts
---

{% include req-ver.md version="2.10.0" module="airline-prompts" %}

Often command line applications may need to prompt users for input e.g. asking for a password, obtaining a missing
value, asking for a Yes/No response etc.  From 2.10.0 onwards we provide the `airline-prompts` module which provides a
library for doing this in a consistent manner, this includes features such as:

- Optional prompt timeouts so non-interactive apps don't hang forever
- Configurable prompt sources
- Configurable prompt formatting
- Prompt for keys, strings, passwords, options (from a pre-configured list) or a strongly typed value

{% include toc.html %}

## Prompting

Prompting is done by creating an instance of a `Prompt<T>` where `T` is the type used for strongly typed option prompts.  
Prompts can be manually instantiated or created using the `PromptBuilder<T>` (the recommended method) which is described
later on this page.  Once you have a prompt you can ask for a variety of different inputs depending on what your
application needs to do.

### Prompting for a Key

The `prompt.promptForKey()` method reads a single character from the configured provider and returns its character code 
as an `int` e.g.

```java
int key = prompt.promptForKey();
```
### Prompting for a String

The `prompt.promptForLine()` method reads a line of input from the configured provider and returns it as a string e.g.

```java
String line = prompt.promptForLine();
```
 
### Prompting for a Password

The `prompt.promptForSecure()` method securely reads a line of input as a `char[]` from the configured provider 
assuming that it is capable of reading securely i.e. this may not work with all providers.

```java
char[] password = prompt.promptForSecure();
```

Secure read means that it reads the data in such a way to as ensure that it does not echo any data back to the output.

### Prompting for an Option

The `prompt.promptForOption()` method reads a line of input and attempts to match that to one of the configured 
options using the configured option matcher.  This can allow for matching values using custom logic, allowing the 
user to select options by numeric index etc.  See the section on Option Matching later on this page for more detail 
on this.

This method takes a `boolean` parameter indicating whether the method calls `promptForLine()` or 
`promptForSecure()` prior to attempting to match the input to an option.  The type returned from this method will 
be the type parameter used for the prompt and the configured options.

```java
String option = prompt.promptForOption(false);
```

### Prompting for a Value

You can also prompt for any strongly typed value assuming that your configured [Type Converter](types.html) 
supports it e.g.

```java
MyCustomType value = prompt.promptForValue(MyCustomType.class, false);
```

## Example

An example command that uses several different prompts can be found in the `airline-examples` module at <a href="{{
site.github.repo }}/tree/master/airline-examples/src/main/java/com/github/rvesse/airline/examples/userguide">GitHub</a>.  
If you have the code checked out and compiled locally you can use the provided `runExample` script to launch an example
e.g.

```
airline-examples> ./runExample PromptsDemo
```
You can view the example {% include github-ref.md class="PromptsDemo" module="airline-examples"
package="com.github.rvesse.airline.examples.userguide.prompts" %} on GitHub.

## Prompt Builder

Generally a `PromptBuilder<T>` is used to specify the desired prompt.  For example consider the following:

```java
Prompt<String> prompt 
  = new PromptBuilder<String>()
                .withPromptProvider(new ConsolePrompt())
                .withOptions("a", "b", "c")
                .withListFormatter()
                .withTimeout(10, TimeUnit.SECONDS)
                .build();
```
This builds a prompt with option type `String` that uses `ConsolePrompt` for the provider, has three options 
and a timeout of 10 seconds.

Alternatively we could use the static methods in `Prompts` to provide a builder for some common prompts, 
e.g. the above could be written as follows:

```java
Prompt<String> prompt 
  = Prompts.newOptionsPrompt("Choose an option", "a", "b", "c")
           .withTimeout(10, TimeUnit.SECONDS)
           .build();
```

The following sections cover how to configure different aspects of your prompt via the builder interface.

### Provider

The `PromptProvider` provides the ability to output prompt messages and read user input.  This can be 
configured using the `withPromptProvider(PromptProvider)` method on the builder as seen in the earlier example.

Alternatively you can use `withDefaultPromptProvider()` to configure with the default provider for your 
environment.  This will be `ConsolePrompt` if the JVM `System.console()` is available or `StdIOPrompt` otherwise.

The choice of provider may impact what functionality you can use, in particular whether a provider returns 
`true` or `false` for `supportsSecureReads()` controls whether you can use the `promptForSecure()` method 
of a prompt.

### Timeouts

The timeout controls how long users have to respond to prompts.  Having a timeout can be useful because it 
prevents your application from hanging indefinitely if run in a non-interactive environment and/or allows you to 
fall back to alternative behaviour, fail fast etc. when no prompt response is received.

The timeout can be set with the `withTimeout()` method and `withTimeoutUnit()` methods e.g.

```java
Prompt<String> prompt
  = Prompts.newYesNoPrompt("Proceed with deletion?")
           .withTimeout(30)
           .withTimeoutUnit(TimeUnit.SECONDS)
           .build();
```

Here we set a 30-second timeout, we could also combine these into a single method call like so:

```java
Prompt<String> prompt
  = Prompts.newYesNoPrompt("Proceed with deletion?")
           .withTimeout(30, TimeUnit.SECONDS)
           .build();
```

A prompt that does not receive a response within the timeout throws a `PromptTimeoutException` to indicate this.

{% include alert.html %}
If a prompt timeout occurs then the underlying prompt provider may still be blocked waiting for user input in the
background.  Please ensure that you catch and handle these errors by asking the user to press enter or pumping
a new line character into the prompt input if you control it.  Otherwise, a subsequent prompt could read the input 
intended for the prior prompt.

Ideally if a prompt timeout occurs you **SHOULD** abort your application or use suitable defaults making no further
prompts for user input.
{% include end-alert.html %}

### Options

The options functionality allows you to create prompts that provide users with a choice of options and determine 
which option the user selected.  Options are a strongly typed API that is configured when you create your prompt 
by the type parameter used for your `PromptBuilder<T>` instance.  We can configure one/more options like so:

```java
Prompt<String> prompt 
  = new PromptBuilder<String>()
           .withPromptProvider(new ConsolePrompt())
           .withPromptMessage("What's your favourite animal?")
           .withOptions("Aardvark", "Badger", "Cougar")
           .withListFormatter()
           .withTimeout(30, TimeUnit.SECONDS)
           .build();
```

Here we configure three string options and we can then use `promptForOption()` to prompt the user to select from 
those options.  Note that we also use `withListFormatter()` which applies a formatter that will display those options 
to the user in a list.  We'll discuss formatters more later in this page.

#### Type Conversion

As with the rest of Airline we use our standard [Type Converter](types.html) API to control how options and input 
values are converted where necessary.  This is configured on a prompt builder via the 
`withTypeConverter(TypeConverter)` method.  If no type converter is explicitly configured  our standard 
`DefaultTypeConverter` is used.

#### Option Matcher

Option matching is used when the `promptForOption()` method is called on a prompt, it is provided by the 
`PromptOptionMatcher` interface.  It controls how the prompt takes the raw input response, provided by 
`promptForLine()` or `promptForSecure()` and turns it into a strongly type option value from the list of options 
provided when you configured the prompt.

The default implementation is the `DefaultMatcher`, this has the following behaviour:

- If the prompt was configured with `withNumericOptionSelection()` then first see if the response is a 1 based 
  index for one of the options
    - If a valid index to an option return it
- Then see if the raw response partially/exactly matches the string representations of any of the options
    - If no options match error as invalid response
    - If multiple options are matched check if it exactly matches only one option
        - If exactly one match return it
        - Otherwise, error as ambiguous response
    - If only one option is matched return it

Note that this is case-sensitive matching, if you prefer case-insensitive matching use `IgnoresCaseMatcher` instead.

The following additional matchers are also provided:

- `ExactMatcher` - Only match exact string representations.
- `ExactIgnoresCaseMatch` - Only match exact string representations allowing for case insensitivity.
- `IndexMatcher` - Treat response as a 1 based index to an option, requires a prompt configured 
  `withNumericOptionSelection()`
- `ValueMatcher` - Converts response into the option type and compares values directly, useful when the string 
  representation of an option can be given in multiple ways e.g. floating point numbers.

The matcher is configured by using the `withOptionMatcher(PromptOptionMatcher)` method of the `PromptBuilder`, 
or `withDefaultOptionMatcher()` to use the default matcher e.g.

```java
   Prompt<Double> prompt 
     = new PromptBuilder<Double>()
           .withPromptProvider(this.getProvider(input, output))
           .withListFormatter()
           .withOptions(1.0, 2.0, 4.0, 8.0, 16.0)
           .withOptionMatcher(new ValueMatcher<>(Double.class))
           .withPromptMessage("What scaling factor?")
           .withTimeout(100, TimeUnit.MILLISECONDS)
           .build();
           
Double scalingFactor = prompt.promptForOption(false);
```

Here we are prompting for a scaling factor as a `Double`, since `Double` values can be written down in multiple 
ways e.g. `1`, `1.0`, `0.1e1` etc. we use `ValueMatcher` to match the prompt response to one of our valid options
 
### Formatters

Formatters, defined via the `PromptFormatter` interface, display actual prompts to the user.  Airline provides two 
implementations of this out of the box:

- `QuestionFormat` which displays a simple question prompt
- `ListFormat` which displays a list of options for the user to select from

You can specify the desired formatter via the `withListFormatter()` or `withQuestionFormatter()` methods on your
`PromptBuilder<T>` instance e.g.

```java
Prompt<String> prompt 
  = new PromptBuilder<String>()
        .withPromptProvider(new ConsolePrompt())
        .withPromptMessage("Please enter your name")
        .withQuestionFormatter()
        .build();
```

If no formatter is explicitly specified then the builder selects one of the two default formats depending on whether 
any options have been specified.  If options have been specified then the list formatter is used, if no options then 
the question formatter is used.