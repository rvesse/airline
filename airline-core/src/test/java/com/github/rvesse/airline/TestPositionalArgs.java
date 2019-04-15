package com.github.rvesse.airline;

import com.github.rvesse.airline.args.ArgsPositional;
import com.github.rvesse.airline.args.ArgsPositionalGap;
import com.github.rvesse.airline.model.PositionalArgumentMetadata;
import com.github.rvesse.airline.parser.command.SingleCommandParser;

import static com.github.rvesse.airline.TestingUtil.singleCommandParser;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.testng.Assert;
import org.testng.annotations.Test;

public class TestPositionalArgs {

    @Test
    public void positional_args_01() {
        SingleCommand<ArgsPositional> parser = singleCommandParser(ArgsPositional.class);
        assertFalse(parser.getCommandMetadata().getPositionalArguments().isEmpty());
        assertEquals(parser.getCommandMetadata().getPositionalArguments().size(), 2);
        
        PositionalArgumentMetadata posArg = parser.getCommandMetadata().getPositionalArguments().get(0);
        assertEquals(posArg.getZeroBasedPosition(), 0);
        assertEquals(posArg.getOneBasedPosition(), 1);
        assertEquals(posArg.getJavaType(), String.class);
        
        posArg = parser.getCommandMetadata().getPositionalArguments().get(1);
        assertEquals(posArg.getZeroBasedPosition(), 1);
        assertEquals(posArg.getOneBasedPosition(), 2);
        assertEquals(posArg.getJavaType(), Integer.class);
        
    }
    
    @Test(expectedExceptions = IllegalStateException.class)
    public void positional_args_gap_01() {
        singleCommandParser(ArgsPositionalGap.class);
    }
}
