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
package com.github.rvesse.airline.help.sections.factories;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.github.rvesse.airline.annotations.help.Copyright;
import com.github.rvesse.airline.annotations.help.Discussion;
import com.github.rvesse.airline.annotations.help.Examples;
import com.github.rvesse.airline.annotations.help.HideSection;
import com.github.rvesse.airline.annotations.help.License;
import com.github.rvesse.airline.annotations.help.ProseSection;
import com.github.rvesse.airline.annotations.help.SeeAlso;
import com.github.rvesse.airline.annotations.help.Version;
import com.github.rvesse.airline.annotations.help.ExitCodes;
import com.github.rvesse.airline.help.sections.HelpFormat;
import com.github.rvesse.airline.help.sections.HelpSection;
import com.github.rvesse.airline.help.sections.common.BasicSection;
import com.github.rvesse.airline.help.sections.common.CommonSections;
import com.github.rvesse.airline.help.sections.common.DiscussionSection;
import com.github.rvesse.airline.help.sections.common.ExamplesSection;
import com.github.rvesse.airline.help.sections.common.ExitCodesSection;
import com.github.rvesse.airline.help.sections.common.VersionSection;
import com.github.rvesse.airline.parser.ParserUtil;
import com.github.rvesse.airline.parser.resources.ResourceLocator;

/**
 * A help section factory that implements the common sections built into Airline
 */
public class CommonSectionsFactory implements HelpSectionFactory {

    //@formatter:off
    private static final List<Class<? extends Annotation>> SUPPORTED 
        = Arrays.asList(
            Examples.class,
            Discussion.class,
            ExitCodes.class,
            HideSection.class,
            ProseSection.class,
            Copyright.class,
            License.class,
            Version.class,
            SeeAlso.class
        );
    //@formatter:on

    @Override
    public HelpSection createSection(Annotation annotation) {
        if (annotation instanceof Examples) {
            // Examples
            Examples ex = (Examples) annotation;
            return new ExamplesSection(ex.examples(), ex.descriptions());
        } else if (annotation instanceof Discussion) {
            // Discussion
            return new DiscussionSection(((Discussion) annotation).paragraphs());
        } else if (annotation instanceof ExitCodes) {
            // Exit Codes
            ExitCodes exits = (ExitCodes) annotation;
            return new ExitCodesSection(exits.codes(), exits.descriptions());
        } else if (annotation instanceof HideSection) {
            // Hide Section
            // Used to hide inherited section
            HideSection hide = (HideSection) annotation;
            return new BasicSection(hide.title(), 0, null, null, HelpFormat.NONE_PRINTABLE, new String[0]);
        } else if (annotation instanceof ProseSection) {
            // Prose Section
            ProseSection prose = (ProseSection) annotation;
            return new com.github.rvesse.airline.help.sections.common.ProseSection(prose.title(),
                    prose.suggestedOrder(), prose.paragraphs());
        } else if (annotation instanceof Copyright) {
            // Copyright Section
            Copyright copyright = (Copyright) annotation;
            String line = String.format("Copyright (c) %s %s%s", copyright.holder(), copyright.startYear(),
                    copyright.endYear() > copyright.startYear() ? String.format("-%s", copyright.endYear()) : "");
            return new com.github.rvesse.airline.help.sections.common.ProseSection(CommonSections.TITLE_COPYRIGHT,
                    CommonSections.ORDER_COPYRIGHT, new String[] { line });
        } else if (annotation instanceof License) {
            // License section
            License license = (License) annotation;
            String[] data = Arrays.copyOf(license.paragraphs(),
                    StringUtils.isNotBlank(license.url()) ? license.paragraphs().length + 1
                            : license.paragraphs().length);
            if (StringUtils.isNotBlank(license.url())) {
                data[data.length - 1] = String.format("Please see %s for more information", license.url());
            }
            return new com.github.rvesse.airline.help.sections.common.ProseSection(CommonSections.TITLE_LICENSE,
                    CommonSections.ORDER_LICENSE, data);
        } else if (annotation instanceof Version) {
            // Version section
            Version version = (Version) annotation;
            ResourceLocator[] locators = ParserUtil.createResourceLocators(version.sourceLocators());
            return new VersionSection(version.sources(), locators, version.componentProperty(),
                    version.versionProperty(), version.buildProperty(), version.dateProperty(),
                    version.additionalProperties(), version.additionalTitles(), version.suppressOnError(),
                    version.tabular());
        } else if (annotation instanceof SeeAlso) {
            // See Also section
            SeeAlso seeAlso = (SeeAlso) annotation;
            Set<String> cmds = new LinkedHashSet<>();
            cmds.addAll(Arrays.asList(seeAlso.internalCommands()));
            cmds.addAll(Arrays.asList(seeAlso.externalCommands()));
            if (!cmds.isEmpty()) {
                return new com.github.rvesse.airline.help.sections.common.ProseSection(CommonSections.TITLE_SEE_ALSO,
                        CommonSections.ORDER_SEE_ALSO, new String[] { String.join(", ", cmds) });
            }
        }
        return null;
    }

    @Override
    public List<Class<? extends Annotation>> supportedAnnotations() {
        return SUPPORTED;
    }

}
