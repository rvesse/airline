---
layout: page
title: Airline in Practise
---

## Airline in Practise

This section of the User Guide aims to cover some practicalities that you will need to know to use Airline in real-world use cases i.e. beyond the toy examples in this documentation.

### Recommended Reading

The following pages should be considered recommended reading and will help you understand how different aspects of Airline work in more detail:

- [Supported Types](types.html) - Details how Airline converts user command line inputs into strongly typed Java objects during the parsing process.  This includes notes on how you can have Airline support your own custom types.
- [Inheritance and Compostion](oop.html) - Details how Airline behaves with respect to inheritance and composition.
- [Exceptions and Error Handling](exceptions.html) - Covers Airline exceptions and how to customise the parsers error handling behaviour.

### Advanced Topics

The following topics are considered more advanced and provide more complex features that may not be applicable to everyday usage:

- [User Defined Aliases](aliases.html) - Describes the user defined aliases feature which provides the ability for users to extend your CLI with command aliases.
- [Maven Plugin](maven-plugin.html) - Describes the Maven plugin that can be used to validate Airline CLIs at build time or to generate help during builds.
- [JDK Compatibility](jdk.html) - Describes our JDK compatibility.