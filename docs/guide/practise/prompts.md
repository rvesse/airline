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


## Prompt Builder

Generally a `PromptBuilder<T>` is used to specify the desired prompt.  For example consider the following simple example:

```java
Prompt<String> prompt 
  = new PromptBuilder<String>()
                .withPromptProvider(new ConsolePrompt())
                .withOptions("a", "b", "c")
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