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
module com.github.rvesse.airline.help.external
{
    requires org.apache.commons.collections4;
    requires org.apache.commons.lang3;
    // TODO Current Commons CSV release is quite old and doesn't have Automatic-Module-Name
    //requires org.apache.commons.csv;
    requires com.github.rvesse.airline.io;
    requires com.github.rvesse.airline;
    
    exports com.github.rvesse.airline.annotations.help.external;
    exports com.github.rvesse.airline.help.external.factories;
    exports com.github.rvesse.airline.help.external.parsers;
    exports com.github.rvesse.airline.help.external.parsers.defaults;
    
    provides com.github.rvesse.airline.help.sections.factories.HelpSectionFactory with
        com.github.rvesse.airline.help.external.factories.ExternalHelpFactory;

}