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
module com.github.rvesse.airline.examples
{
    requires org.apache.commons.collections4;
    requires org.apache.commons.csv;
    requires org.apache.commons.lang3;
    requires com.github.rvesse.airline.io;
    requires com.github.rvesse.airline;
    requires com.github.rvesse.airline.help.bash;
    requires com.github.rvesse.airline.help.external;
    requires com.github.rvesse.airline.help.html;
    requires com.github.rvesse.airline.help.man;
    requires com.github.rvesse.airline.help.markdown;
    requires com.github.rvesse.airline.prompts;
    requires com.github.rvesse.airline.jpms.resources;
    
    exports com.github.rvesse.airline.examples;
    exports com.github.rvesse.airline.examples.cli;
    exports com.github.rvesse.airline.examples.cli.aliases;
    exports com.github.rvesse.airline.examples.cli.commands;
    exports com.github.rvesse.airline.examples.help;
    exports com.github.rvesse.airline.examples.inheritance;
    exports com.github.rvesse.airline.examples.io;
    exports com.github.rvesse.airline.examples.modules;
    exports com.github.rvesse.airline.examples.sendit;
    exports com.github.rvesse.airline.examples.simple;
    exports com.github.rvesse.airline.examples.userguide;
    exports com.github.rvesse.airline.examples.userguide.help.bash;
    exports com.github.rvesse.airline.examples.userguide.help.sections;
    exports com.github.rvesse.airline.examples.userguide.help.sections.custom;
    exports com.github.rvesse.airline.examples.userguide.parser;
    exports com.github.rvesse.airline.examples.userguide.parser.options;
    exports com.github.rvesse.airline.examples.userguide.practise;
    exports com.github.rvesse.airline.examples.userguide.prompts;
    exports com.github.rvesse.airline.examples.userguide.restrictions;
    exports com.github.rvesse.airline.examples.userguide.restrictions.custom;
    
    // As Airline is driven by reflection over the annotations on your classes you need 
    // to open packages containing Airline annotated types to the com.github.rvesse.airline module
    opens com.github.rvesse.airline.examples.cli to com.github.rvesse.airline;
    opens com.github.rvesse.airline.examples.cli.aliases to com.github.rvesse.airline;
    opens com.github.rvesse.airline.examples.cli.commands to com.github.rvesse.airline;
    opens com.github.rvesse.airline.examples.help to com.github.rvesse.airline;
    opens com.github.rvesse.airline.examples.inheritance to com.github.rvesse.airline;
    opens com.github.rvesse.airline.examples.io to com.github.rvesse.airline;
    opens com.github.rvesse.airline.examples.modules to com.github.rvesse.airline;
    opens com.github.rvesse.airline.examples.sendit to com.github.rvesse.airline;
    opens com.github.rvesse.airline.examples.simple to com.github.rvesse.airline;
    opens com.github.rvesse.airline.examples.userguide to com.github.rvesse.airline;
    opens com.github.rvesse.airline.examples.userguide.help.bash to com.github.rvesse.airline;
    opens com.github.rvesse.airline.examples.userguide.help.sections to com.github.rvesse.airline;
    opens com.github.rvesse.airline.examples.userguide.help.sections.custom to com.github.rvesse.airline;
    opens com.github.rvesse.airline.examples.userguide.parser to com.github.rvesse.airline;
    opens com.github.rvesse.airline.examples.userguide.parser.options to com.github.rvesse.airline;
    opens com.github.rvesse.airline.examples.userguide.practise to com.github.rvesse.airline;
    opens com.github.rvesse.airline.examples.userguide.restrictions to com.github.rvesse.airline;
    opens com.github.rvesse.airline.examples.userguide.restrictions.custom to com.github.rvesse.airline;

    opens resources to io.github.classgraph;
}