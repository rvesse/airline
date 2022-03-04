/**
 * Copyright (C) 2010-16 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.github.rvesse.airline.restrictions.options;

import com.github.rvesse.airline.help.sections.HelpFormat;
import com.github.rvesse.airline.help.sections.HelpHint;
import com.github.rvesse.airline.model.ArgumentsMetadata;
import com.github.rvesse.airline.model.OptionMetadata;
import com.github.rvesse.airline.parser.ParseState;
import com.github.rvesse.airline.parser.errors.ParseInvalidRestrictionException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A restriction that requires an option/argument be set <strong>UNLESS</strong> a suitable environment variable is
 * specified.
 */
public class RequiredUnlessEnvironmentRestriction extends AbstractRequiredUnlessRestriction implements HelpHint {

    private final List<String> variables;

    public RequiredUnlessEnvironmentRestriction(String[] envVars) {
        this.variables = new ArrayList<>(Arrays.asList(envVars));
        if (CollectionUtils.isEmpty(this.variables)) {
            throw new ParseInvalidRestrictionException(
                    "A RequiredUnlessEnvironment restriction must specify at least one environment variable.");
        }
    }

    @Override
    protected <T> boolean unless(ParseState<T> state, OptionMetadata option) {
        return IterableUtils.matchesAny(this.variables, new IsEnvVarSet());
    }

    @Override
    protected <T> boolean unless(ParseState<T> state, ArgumentsMetadata arguments) {
        return IterableUtils.matchesAny(this.variables, new IsEnvVarSet());
    }

    @Override
    protected String unlessDescription() {
        if (this.variables.size() > 1) {
            return String.format("no default values were available from the environment variables %s",
                                 StringUtils.join(this.variables, ", "));
        } else {
            return String.format("no default value was available from the environment variable %s",
                                 this.variables.get(0));
        }
    }

    @Override
    public String getPreamble() {
        if (this.variables.size() > 1) {
            return "This option is required unless one of the following environment variables is set:";
        } else {
            return null;
        }
    }

    @Override
    public HelpFormat getFormat() {
        return this.variables.size() > 1 ? HelpFormat.LIST : HelpFormat.PROSE;
    }

    @Override
    public int numContentBlocks() {
        return 1;
    }

    @Override
    public String[] getContentBlock(int blockNumber) {
        if (this.variables.size() > 1) {
            return this.variables.toArray(new String[this.variables.size()]);
        } else {
            return new String[] {
                    String.format("This option is required unless the environment variable %s is set.",
                                  this.variables.get(0))
            };
        }
    }

    private static class IsEnvVarSet implements Predicate<String> {
        @Override
        public boolean evaluate(String var) {
            return StringUtils.isNotBlank(System.getenv(var));
        }
    }
}
