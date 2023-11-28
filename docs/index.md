---
layout: default
title: Airline
permalink: index.html
---

# Airline

Airline is an annotation-driven Java library for building Command Line Interfaces (CLIs), it supports simple commands
all the way through to complex Git style CLIs with groups and user defined command aliases.

Airline aims to reduce the boiler plate code typically associated with CLIs in Java, many common behaviours can be
achieved purely with annotations and zero user code.  Let's take a look at an ultra simple example:

{% include code/getting-started.md %}

This is explained in depth in the [Introduction to Airline](guide/) but essentially we had to do the following:

- Annotate our class with [`@Command`](annotations/command.html) to indicate that it is a command
- Annotate fields with [`@Option`](annotations/option.html) and [`@Arguments`](annotations/arguments.html) to indicate
  that they receive values from the command line
- Use `SingleCommand.singleCommand()` to create a parser from our class
- Call `parse()` to pass the command line arguments
- Implement our command logic as desired, here it is contained in the `run()` method

## How to Use

Please start reading the [User Guide](guide/index.html) to learn how to use Airline for your applications.

## Get Airline

You can get Airline from Maven central by specifying the following Maven coordinates:

```xml
<dependency>
  <groupId>com.github.rvesse</groupId>
  <artifactId>airline</artifactId>
  <version>X.Y.Z</version>
</dependency>
```

Where `X.Y.Z` is your desired version, the current stable release is `{{ site.version }}`

### Dependencies

The core `airline` library has an intentionally minimal set of dependencies, these are currently as follows:

- Apache Commons Lang and Apache Commons Collections
- Airline IO (our own internal helper library for IO)
- Jakarta Inject (and Javax Inject)
   - Please see [Historical Composition](guide/practise/oop.html#historical-composition) for background on these.
   - These dependencies are **optional** as of `3.0.0` so will not be picked up unless you explicitly declare them.

Note that the additional modules e.g. `airline-help-external`, `airline-jpms-resources` etc may have additional
dependencies beyond the above.  Please refer to `mvn dependency:tree` output on your project to see exactly what 
dependencies different modules bring in.

## License

Airline is open source software licensed under the [Apache License 2.0](http://apache.org/licenses/LICENSE-2.0) and this
license also applies to the documentation found here.

Please see `license.txt` in this repository for further details

## Acknowledgements

This project was forked from [http://github.com/airlift/airline](http://github.com/airlift/airline) and would not exist
at all were it not for that library.

This website is built with [Jekyll](http://jekyllrb.com), it uses the following 3rd party resources:

- [Hyde theme](https://github.com/poole/hyde) by Mark Otto - MIT License
- [Table of Contents plugin](https://github.com/ghiculescu/jekyll-table-of-contents) by Alex Ghiculescu - MIT License
- [Multi-Level Push Menu plugin](https://github.com/adgsm/multi-level-push-menu) by Momcilo Dzunic  - MIT License
- [Better Simple Slideshow](https://github.com/leemark/better-simple-slideshow) by Mark Lee - Mozilla Public License

All 3rd party resources used on the website are licensed under the stated open source licenses.

Content on this website is licensed under the same [Apache License 2.0](http://apache.org/licenses/LICENSE-2.0) used for
the library as stated in the above License section.