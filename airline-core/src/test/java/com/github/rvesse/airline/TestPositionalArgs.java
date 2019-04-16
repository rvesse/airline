package com.github.rvesse.airline;

import com.github.rvesse.airline.args.ArgsPositional;
import com.github.rvesse.airline.args.ArgsPositionalGap;
import com.github.rvesse.airline.model.PositionalArgumentMetadata;
import static com.github.rvesse.airline.TestingUtil.singleCommandParser;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.testng.Assert;
import org.testng.annotations.Test;

@SuppressWarnings("unused")
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
        assertEquals(posArg.getTitle(), "File");

        posArg = parser.getCommandMetadata().getPositionalArguments().get(1);
        assertEquals(posArg.getZeroBasedPosition(), 1);
        assertEquals(posArg.getOneBasedPosition(), 2);
        assertEquals(posArg.getJavaType(), Integer.class);
        assertEquals(posArg.getTitle(), "Mode");

        ArgsPositional cmd = parser.parse("example.txt", "600", "extra");
        assertEquals(cmd.file, "example.txt");
        assertEquals(cmd.mode, new Integer(600));
        assertEquals(cmd.parameters.size(), 1);
        assertEquals(cmd.parameters.get(0), "extra");

    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void positional_args_gap_01() {
        singleCommandParser(ArgsPositionalGap.class);
    }
}
