---
layout: page
title: Custom Option Parsers
---

Option parsing is provided to Airline by the instances of the `OptionParser` interface.  Airline ships with a variety of these which implement common option parsing styles which are detailed in the [Parser Overview](index.html#option-parsing).

It is also possible to provide your own custom implementations if you wish to more closely control the option parsing process yourself, here we work through an example implementation.

### The `OptionParser` interface

A custom option parser must implement the `OptionParser` interface which is pretty simple:

```java
public interface OptionParser<T> {

    /**
     * Parses one/more options from the token stream
     * 
     * @param tokens
     *            Tokens
     * @param state
     *            Current parser state
     * @param allowedOptions
     *            Allowed options at this point of the parsing
     * @return New parser state, may return {@code null} if this parser could
     *         not parse the next token as an option
     */
    public abstract ParseState<T> parseOptions(PeekingIterator<String> tokens, ParseState<T> state,
            List<OptionMetadata> allowedOptions);
}
```
Let's start by reviewing what the interface gives us. Firstly we get `tokens` which is a peeking iterator over the inputs being parsed, this allows us to `peek()` the next token as well as consume it with `next()` once we determine whether we can actually parse an option.

Secondly we get the parser `state` which provides access to useful information such as the parser configuration and which we will need to manipulate to indicate options we have successfully parsed.

Finally we get `allowedOptions` which is the list of options that are currently in-scope. Since our parser may be called at multiple stages in the parsing process different options may be in-scope each time we are called.

In terms of return value there are three things we can return:

1. A new `ParseState` indicating what we have parsed
2. A new `ParseState` indicating we encountered an exception
2. A `null` to indicate that we could not parse anything and other option parsers should be tried

Where possible parsers should prefer option 3 to 2 i.e. If they cannot parse anything they should not modify `tokens` or generate a new state and just return `null` so other configured parser implementations will be tried. However for some more complex parsers you may already have consumed some input by the time you realise that you cannot successfully parse an option in which case communicating an exception is the correct thing to do.

### Simple Custom Option Parser

If you simply wish to change the name and value separator then you can trivially do this by sub-classing {% include javadoc-ref.md class="AbstractNameValueOptionParser" package="parser.options" %} and specify the desired separator e.g.

```java
package com.github.rvesse.airline.examples.userguide.parser.options;

import com.github.rvesse.airline.parser.options.AbstractNameValueOptionParser;

/**
 * An option parser where the name and value are colon separated i.e.
 * {@code -name:value}
 *
 * @param <T>
 *            Command type
 */
public class ColonSeparatedOptionParser<T> extends AbstractNameValueOptionParser<T> {

    /**
     * Creates a new parser instance
     */
    public ColonSeparatedOptionParser() {
        super(':');
    }

}
```
This provides us a parser that uses `:` as the separator so `--name:example` would set the `--name` option to the value `example`

### Complex Custom Option Parsers

Now let's look at a more complex option parser, for example say we wanted to parse options specified in JDBC connection string style e.g.

`port=1234;security=enabled;charset=utf-8`

We could create an options parser that could do this like so:

```java
/**
 * Option parser that parses options given in JDBC connection URL style e.g.
 * {@code port=1000;user=example;}
 * 
 * @author rvesse
 *
 * @param <T>
 *            Command type
 */
public class JdbcStyleOptionParser<T> extends AbstractOptionParser<T> {

    public static final String NAME_VALUE_SEPARATOR = "=";

    public static final String OPTION_SEPARATOR = ";";

    @Override
    public ParseState<T> parseOptions(PeekingIterator<String> tokens, ParseState<T> state,
            List<OptionMetadata> allowedOptions) {
        // Peek at the first input
        String options = tokens.peek();

        // Must have at least one name=value pair for this to be a JDBC style
        // option specification
        if (!options.contains(NAME_VALUE_SEPARATOR))
            return null;

        // May potentially be multiple name value pairs in the string separated
        // by a semicolon
        String[] optionPairs = options.split(OPTION_SEPARATOR);

        // Try and parse the options
        ParseState<T> nextState = state;
        boolean first = true;
        for (String pair : optionPairs) {
            // Allow for empty pair, this may occur if the user terminates the
            // options with a semicolon which is legitimate
            if (StringUtils.isEmpty(pair))
                continue;

            if (!pair.contains(NAME_VALUE_SEPARATOR)) {
                // This would be invalid for us but if this is the first option
                // we've seen might be valid for another option parser
                if (first)
                    return null;

                // Otherwise treat as an invalid option
                state.getParserConfiguration().getErrorHandler()
                        .handleError(new ParseOptionMissingValueException(pair));
            }

            // Find the relevant option
            String[] nameValue = pair.split(NAME_VALUE_SEPARATOR, 2);
            OptionMetadata option = findOption(state, allowedOptions, nameValue[0]);
            if (option == null) {
                // No such option, let another option parser try to parse the
                // option string
                if (first)
                    return null;

                state.getParserConfiguration().getErrorHandler().handleError(new ParseOptionUnexpectedException(
                        "JDBC style option '%s' refers to option '%s' which does not refer to a known option", pair, nameValue[0]));
            }

            // Tell the parser we're parsing an option
            nextState = nextState.pushContext(Context.OPTION).withOption(option);

            if (option.getArity() == 1) {
                if (first) {
                    // If this is the first valid option we've seen we now
                    // consume the input token
                    tokens.next();
                    first = false;
                }

                // Set the option value
                nextState = nextState.withOptionValue(option, nameValue[1]).popContext();
            } else {
                // We only permit arity 1 options
                if (first)
                    return null;

                state.getParserConfiguration().getErrorHandler().handleError(new ParseOptionUnexpectedException(
                        "JDBC style option '%s' refers to option  '%s' which has arity %d, only arity 1 options are supported",
                        pair, nameValue[0], option.getArity()));
            }
        }

        // If we didn't parse anything let other parsers try
        if (first)
            return null;
        
        // Otherwise return the new state
        return nextState;
    }
}
```

View {% include github-ref.md package="examples.userguide.parser.options" module="airline-examples" class="JdbcStyleOptionsParser" %} to see the full code.

You can run this example like so:

```
> ./runExample JdbcStyleOptionParser "--name=foo;number=1234"
Parser error: JDBC style option 'number=1234' refers to option 'number' which does not refer to a known option

> ./runExample JdbcStyleOptionParser "--name=foo;--number=1234;"
Flag was not set
Name was foo
Number was 1234

Exiting with Code 0

> ./runExample JdbcStyleOptionParser "--foo=bar"
Flag was not set
Name was null
Number was 0
Arguments were --foo=bar

Exiting with Code 0

> ./runExample JdbcStyleOptionParser --flag
Flag was set
Name was null
Number was 0

Exiting with Code 0
```

In the first example we failed to use a valid name for the option resulting in an error.  In the second we set both the name and number options.  In the third we used an unknown option so that was ignored by the option parsers and got treated as an argument instead.  Finally in the fourth example we see that options our parser does not cope with e.g. the zero arity `--flag` option are handled by the default option parsers instead.

Note that we used quotes to surround the argument strings in some of these examples since `;` is a special character in some shells e.g. Bash