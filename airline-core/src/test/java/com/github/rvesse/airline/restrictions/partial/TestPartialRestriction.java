/**
 * Copyright (C) 2010-16 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.rvesse.airline.restrictions.partial;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.rvesse.airline.SingleCommand;
import com.github.rvesse.airline.annotations.OptionType;
import com.github.rvesse.airline.model.ArgumentsMetadata;
import com.github.rvesse.airline.model.OptionMetadata;
import com.github.rvesse.airline.parser.ParseState;
import com.github.rvesse.airline.parser.errors.ParseArgumentsIllegalValueException;
import com.github.rvesse.airline.parser.errors.ParseRestrictionViolatedException;
import com.github.rvesse.airline.restrictions.ArgumentsRestriction;
import com.github.rvesse.airline.restrictions.OptionRestriction;
import com.github.rvesse.airline.restrictions.common.AllowedRawValuesRestriction;
import com.github.rvesse.airline.restrictions.common.AllowedValuesRestriction;
import com.github.rvesse.airline.restrictions.common.IsRequiredRestriction;
import com.github.rvesse.airline.restrictions.common.NotBlankRestriction;
import com.github.rvesse.airline.restrictions.common.PartialRestriction;

public class TestPartialRestriction {

    @Test
    public void partial_notblank_01() throws NoSuchFieldException, SecurityException {
        List<String> titles = new ArrayList<>();
        titles.add("not-blank");
        titles.add("blank");
        List<ArgumentsRestriction> restrictions = new ArrayList<>();
        PartialRestriction restriction = new PartialRestriction(new int[] { 0 },
                (ArgumentsRestriction) new NotBlankRestriction());
        restrictions.add(restriction);
        List<Field> fields = Collections.singletonList(PartialUnannotated.class.getField("args"));

        ArgumentsMetadata arguments = new ArgumentsMetadata(titles, "", restrictions, null, fields);

        ParseState<PartialUnannotated> state = ParseState.newInstance();
        state = state.withArgument(arguments, "text");
        state = state.withArgument(arguments, "");

        restriction.finalValidate(state, arguments);
    }

    @Test(expectedExceptions = ParseRestrictionViolatedException.class, expectedExceptionsMessageRegExp = ".*'not-blank' requires a non-blank value.*")
    public void partial_notblank_02() throws NoSuchFieldException, SecurityException {
        List<String> titles = new ArrayList<>();
        titles.add("not-blank");
        titles.add("blank");
        List<ArgumentsRestriction> restrictions = new ArrayList<>();
        PartialRestriction restriction = new PartialRestriction(new int[] { 0 },
                (ArgumentsRestriction) new NotBlankRestriction());
        restrictions.add(restriction);
        List<Field> fields = Collections.singletonList(PartialUnannotated.class.getField("args"));

        ArgumentsMetadata arguments = new ArgumentsMetadata(titles, "", restrictions, null, fields);

        ParseState<PartialUnannotated> state = ParseState.newInstance();
        // Should fail restriction because first argument cannot be blank
        state = state.withArgument(arguments, "");
    }

    @Test
    public void partial_allowed_values_01() throws NoSuchFieldException, SecurityException {
        List<String> titles = new ArrayList<>();
        titles.add("set-values");
        titles.add("any");
        List<ArgumentsRestriction> restrictions = new ArrayList<>();
        PartialRestriction restriction = new PartialRestriction(new int[] { 0 },
                (ArgumentsRestriction) new AllowedValuesRestriction("foo"));
        restrictions.add(restriction);
        List<Field> fields = Collections.singletonList(PartialUnannotated.class.getField("args"));

        ArgumentsMetadata arguments = new ArgumentsMetadata(titles, "", restrictions, null, fields);

        ParseState<PartialUnannotated> state = ParseState.newInstance();
        state = state.withArgument(arguments, "foo");
        state = state.withArgument(arguments, "bar");

        restriction.finalValidate(state, arguments);
    }

    @Test(expectedExceptions = ParseArgumentsIllegalValueException.class, expectedExceptionsMessageRegExp = ".*set-values' was given as 'bar' which is not in the list of allowed values: \\[foo\\]")
    public void partial_allowed_values_02() throws NoSuchFieldException, SecurityException {
        List<String> titles = new ArrayList<>();
        titles.add("set-values");
        titles.add("any");
        List<ArgumentsRestriction> restrictions = new ArrayList<>();
        PartialRestriction restriction = new PartialRestriction(new int[] { 0 },
                (ArgumentsRestriction) new AllowedValuesRestriction("foo"));
        restrictions.add(restriction);
        List<Field> fields = Collections.singletonList(PartialUnannotated.class.getField("args"));

        ArgumentsMetadata arguments = new ArgumentsMetadata(titles, "", restrictions, null, fields);

        ParseState<PartialUnannotated> state = ParseState.newInstance();
        // Should fail restriction because first argument is restricted to a set
        // of values
        state.withArgument(arguments, "bar");
    }

    @Test
    public void partial_allowed_raw_values_01() throws NoSuchFieldException, SecurityException {
        List<String> titles = new ArrayList<>();
        titles.add("set-values");
        titles.add("any");
        List<ArgumentsRestriction> restrictions = new ArrayList<>();
        PartialRestriction restriction = new PartialRestriction(new int[] { 0 },
                (ArgumentsRestriction) new AllowedRawValuesRestriction(false, Locale.ENGLISH, "foo"));
        restrictions.add(restriction);
        List<Field> fields = Collections.singletonList(PartialUnannotated.class.getField("args"));

        ArgumentsMetadata arguments = new ArgumentsMetadata(titles, "", restrictions, null, fields);

        ParseState<PartialUnannotated> state = ParseState.newInstance();
        state = state.withArgument(arguments, "foo");
        state = state.withArgument(arguments, "bar");

        restriction.finalValidate(state, arguments);
    }

    @Test(expectedExceptions = ParseArgumentsIllegalValueException.class, expectedExceptionsMessageRegExp = ".*set-values' was given as 'bar' which is not in the list of allowed values: \\[foo\\]")
    public void partial_allowed_raw_values_02() throws NoSuchFieldException, SecurityException {
        List<String> titles = new ArrayList<>();
        titles.add("set-values");
        titles.add("any");
        List<ArgumentsRestriction> restrictions = new ArrayList<>();
        PartialRestriction restriction = new PartialRestriction(new int[] { 0 },
                (ArgumentsRestriction) new AllowedRawValuesRestriction(false, Locale.ENGLISH, "foo"));
        restrictions.add(restriction);
        List<Field> fields = Collections.singletonList(PartialUnannotated.class.getField("args"));

        ArgumentsMetadata arguments = new ArgumentsMetadata(titles, "", restrictions, null, fields);

        ParseState<PartialUnannotated> state = ParseState.newInstance();
        // Should fail restriction because first argument is restricted to a set
        // of values
        state.withArgument(arguments, "bar");
    }

    @Test
    public void partial_allowed_raw_values_03() throws NoSuchFieldException, SecurityException {
        List<String> names = new ArrayList<>();
        names.add("--kvp");
        List<OptionRestriction> restrictions = new ArrayList<>();
        PartialRestriction restriction = new PartialRestriction(new int[] { 0 },
                (OptionRestriction) new AllowedRawValuesRestriction(false, Locale.ENGLISH, "a", "b", "c"));
        restrictions.add(restriction);
        List<Field> fields = Collections.singletonList(PartialUnannotated.class.getField("kvps"));

        OptionMetadata option = new OptionMetadata(OptionType.COMMAND, names, Arrays.asList("Key", "Value"), "", 2,
                false, false, false, restrictions, null, fields);

        ParseState<PartialUnannotated> state = ParseState.newInstance();
        state = state.withOptionValue(option, "a");
        state = state.withOptionValue(option, "value");

        restriction.finalValidate(state, option);
    }

    @Test
    public void partial_required_01() throws NoSuchFieldException, SecurityException {
        List<String> titles = new ArrayList<>();
        titles.add("not-blank");
        titles.add("blank");
        List<ArgumentsRestriction> restrictions = new ArrayList<>();
        PartialRestriction restriction = new PartialRestriction(new int[] { 0 },
                (ArgumentsRestriction) new IsRequiredRestriction());
        restrictions.add(restriction);
        List<Field> fields = Collections.singletonList(PartialUnannotated.class.getField("args"));

        ArgumentsMetadata arguments = new ArgumentsMetadata(titles, "", restrictions, null, fields);

        // Should pass because finalValidate() restrictions are not respected by
        // partial restrictions
        ParseState<PartialUnannotated> state = ParseState.newInstance();
        restriction.finalValidate(state, arguments);
    }

    @Test
    public void partial_annotated_notblank_01() throws NoSuchFieldException, SecurityException {
        SingleCommand<PartialAnnotated> parser = SingleCommand.singleCommand(PartialAnnotated.class);
        PartialAnnotated cmd = parser.parse(new String[] { "--kvp", "text", "" });

        Assert.assertEquals(cmd.kvps.size(), 2);
        Assert.assertEquals(cmd.kvps.get(0), "text");
        Assert.assertEquals(cmd.kvps.get(1), "");
    }

    @Test(expectedExceptions = ParseRestrictionViolatedException.class, expectedExceptionsMessageRegExp = ".*--kvp value '<Key>' requires a non-blank value.*")
    public void partial_annotated_notblank_02() throws NoSuchFieldException, SecurityException {
        SingleCommand<PartialAnnotated> parser = SingleCommand.singleCommand(PartialAnnotated.class);
        parser.parse(new String[] { "--kvp", "", "text" });
    }

    @Test
    public void partials_annotated_01() {
        SingleCommand<PartialsAnnotated> parser = SingleCommand.singleCommand(PartialsAnnotated.class);
        PartialsAnnotated cmd = parser.parse(new String[] { "--kvp", "server", "remote.com" });

        Assert.assertEquals(cmd.kvps.size(), 2);
        Assert.assertEquals(cmd.kvps.get(0), "server");
        Assert.assertEquals(cmd.kvps.get(1), "remote.com");
    }

    @Test(expectedExceptions = ParseRestrictionViolatedException.class)
    public void partials_annotated_02() {
        SingleCommand<PartialsAnnotated> parser = SingleCommand.singleCommand(PartialsAnnotated.class);
        parser.parse(new String[] { "--kvp", "other", "remote.com" });
    }

    @Test(expectedExceptions = ParseRestrictionViolatedException.class)
    public void partials_annotated_03() {
        SingleCommand<PartialsAnnotated> parser = SingleCommand.singleCommand(PartialsAnnotated.class);
        parser.parse(new String[] { "--kvp", "server", "" });
    }
}
