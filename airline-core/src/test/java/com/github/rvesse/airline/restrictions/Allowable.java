package com.github.rvesse.airline.restrictions;

import java.util.concurrent.TimeUnit;

import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.Option;
import com.github.rvesse.airline.annotations.restrictions.AllowedEnumValues;
import com.github.rvesse.airline.annotations.restrictions.AllowedRawValues;
import com.github.rvesse.airline.annotations.restrictions.AllowedValues;

@Command(name = "allowable")
public class Allowable {

    @Option(name = "--raw")
    @AllowedRawValues(allowedValues = { "foo", "bar", "faz" })
    public String raw;
    
    @Option(name = "--typed")
    @AllowedValues(allowedValues = { "1", "2", "3" })
    public double typed;
    
    @Option(name = "--enum")
    @AllowedEnumValues(TimeUnit.class)
    public TimeUnit enumTyped;
}
