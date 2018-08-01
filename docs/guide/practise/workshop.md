---
layout: page
title: Workshop - Building a Killer Command Line App with Airline
---

This workshop session is designed to give you a complete introduction to the core features of Airline for creating powerful CLIs.

{% include toc.html %}

## Pre-requisites

In order to follow along with this workshop we assume the following knowledge and tools:

- Understanding of the Java programming language
- JDK 7, 8, 9 or 10 available
- [`git`](https://git-scm.org) installed
- [`mvn`](https://maven.apache.org) installed

## Background

Everyone builds command line applications at some point but often they are cobbled together or full of needless boiler plate. Airline takes a fully declarative approach to command line applications allowing users to create powerful and flexible command lines.

Airline takes care of lots of heavy lifting providing many features not found in similar libraries including annotation driven value validation restrictions, generating Man pages and Bash completion scripts to name just a few. In the workshop session we'll work through a comprehensive example command line application to see just how powerful this can be.

### History

Airline started out as an open source project on GitHub back in January 2012.  I (Rob Vesse, the maintainer of this fork) first encountered this library in use in one of our competitors products partway through that year.  I quickly started using it in my own work but encountered a few limitations.  The original authors were not receptive to pull requests so I forked the code and started maintaining my own version that has since evolved considerably.

### Design Philosophy

1. Be Declarative **Not** Imperative
2. Avoid boiler plate code
3. Allow deep customisation

Firstly we want to define our command lines using declarative annotations.  This allows us to separate the command line definition cleanly from the runtime logic.  It also enables us to do optional build time checking of our definitions to ensure valid command line apps.

Secondly we look to avoid the typical boiler plate code associated with many command line libraries.  You shouldn't need to write a ton of `if` statements to check that values for options fall in specified ranges or meet application specific constraints.

Finally we don't want to tie you into a particular implementation.  We provide extensibility of almost every aspect of the parsing process yet provide a general purpose default setup that should suit many users.

## Workshop Overview

For this workshop we are going to build an example command line application called `send-it` that is designed for shipping of packages.  The example code on this page is typically truncated to omit things like import declarations for brevity, the full code is linked alongside each example.

The example code all lives inside the Airline git repository at https://github.com/rvesse/airline/tree/master/airline-examples

To follow along you should check out the code and build the examples:

```
> git clone https://github.com/rvesse/airline.git
> cd airline
> mvn package -Pquick
```

Many of the examples are runnable using the `runExample` script in that directory e.g.

```
> ./runExample SendIt
```

## Step 1 - Define Options

Airline works with POJOs (Plain Old Java Objects) so firstly we need to define some classes that are going to hold our commands options.

### `@Option`

The [`@Option`](../annotations/option.html) annotation is used to mark a field as being populated by an option.

### `@Arguments`

### Restrictions

## Step 2 - Define a Command

### `@Command`

### Using `@Inject` for composition

### Command Logic

### Invoking our command

## Step 3 - Define a CLI

### `@Cli`

### Invoking our CLI

## Step 4 - Customising the Parser

### `@Parser`

### Configuring option styles

### Allowing complex numeric inputs

## Step 5 - Help System

### Adding `HelpOption` to our commands

### Including the `Help` command

### Invoking Help manually

### Generating Manual Pages