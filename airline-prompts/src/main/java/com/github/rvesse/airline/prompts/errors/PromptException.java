package com.github.rvesse.airline.prompts.errors;

public class PromptException extends Exception {

    private static final long serialVersionUID = -3768748521286786554L;

    
    public PromptException(String message) {
        super(message);
    }
    
    public PromptException(String message, Throwable cause) {
        super(message, cause);
    }
}
