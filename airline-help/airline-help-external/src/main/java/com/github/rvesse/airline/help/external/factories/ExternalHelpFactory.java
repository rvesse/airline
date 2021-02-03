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

package com.github.rvesse.airline.help.external.factories;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;

import com.github.rvesse.airline.annotations.help.ExternalProse;
import com.github.rvesse.airline.help.external.parsers.ParagraphsParser;
import com.github.rvesse.airline.help.sections.HelpSection;
import com.github.rvesse.airline.help.sections.common.ProseSection;
import com.github.rvesse.airline.help.sections.factories.HelpSectionFactory;

/**
 * Help section factory that enables the additonal annotations provided by this module
 *
 */
public class ExternalHelpFactory implements HelpSectionFactory {
    
    private final ParagraphsParser paragraphsParser = new ParagraphsParser();
    
    //@formatter:off
    private static final List<Class<? extends Annotation>> SUPPORTED 
        = Arrays.<Class<? extends Annotation>>asList(
                    ExternalProse.class
                );
    //@formatter:on

    @Override
    public HelpSection createSection(Annotation annotation) {
        if (annotation instanceof ExternalProse) {
            ExternalProse extProse = (ExternalProse) annotation;
            String[] paragraphs = this.paragraphsParser.parseParagraphs(extProse.resource());
            return new ProseSection(extProse.title(), extProse.suggestedOrder(), paragraphs);
        }
        
        return null;
    }

    @Override
    public List<Class<? extends Annotation>> supportedAnnotations() {
        return SUPPORTED;
    }

}
