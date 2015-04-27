package io.airlift.airline.parser;

/**
 * Error that is thrown if too many arguments are provided
 *
 */
public class ParseTooManyArgumentsException extends ParseException {

    public ParseTooManyArgumentsException(String string, Object... args) {
        super(String.format(string, args));
    }

    public ParseTooManyArgumentsException(Exception cause, String string, Object... args) {
        super(String.format(string, args), cause);
    }
}