package com.github.rvesse.airline.restrictions.common;

import java.util.Locale;

public abstract class AbstractLocaleAndCaseStringRestriction extends AbstractStringRestriction {
    
    protected final Locale locale;
    protected final boolean ignoreCase;

    public AbstractLocaleAndCaseStringRestriction(boolean ignoreCase, Locale locale) {
        this.ignoreCase = ignoreCase;
        if (locale == null)
            locale = Locale.ENGLISH;
        this.locale = locale;
    }
}
