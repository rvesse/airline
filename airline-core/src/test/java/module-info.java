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
module com.github.rvesse.airline.tests
{
  // Required dependencies
  requires com.github.rvesse.airline;
  requires com.github.rvesse.airline.io;
  requires org.apache.commons.lang3;
  requires org.apache.commons.collections4;
  requires org.testng;
  requires jakarta.inject;
  requires javax.inject;

  // Exported packages
  exports com.github.rvesse.airline.tests;
  exports com.github.rvesse.airline.tests.args;
  exports com.github.rvesse.airline.tests.args.overrides;
  exports com.github.rvesse.airline.tests.command;
  exports com.github.rvesse.airline.tests.parser;
  exports com.github.rvesse.airline.tests.parser.aliases;
  exports com.github.rvesse.airline.tests.parser.errors.handlers;
  exports com.github.rvesse.airline.tests.restrictions;
  exports com.github.rvesse.airline.tests.restrictions.partial;
  exports com.github.rvesse.airline.tests.restrictions.ports;
  exports com.github.rvesse.airline.tests.restrictions.ranges;
  exports com.github.rvesse.airline.tests.sections;
  exports com.github.rvesse.airline.tests.types;
  exports com.github.rvesse.airline.tests.utils;

  // Opened packages
  opens com.github.rvesse.airline.tests;
  opens com.github.rvesse.airline.tests.args;
  opens com.github.rvesse.airline.tests.args.overrides;
  opens com.github.rvesse.airline.tests.args.positional;
  opens com.github.rvesse.airline.tests.parser;
  opens com.github.rvesse.airline.tests.restrictions;
  opens com.github.rvesse.airline.tests.restrictions.partial;
  opens com.github.rvesse.airline.tests.restrictions.ports;
  opens com.github.rvesse.airline.tests.restrictions.ranges;
  opens com.github.rvesse.airline.tests.sections;
  opens com.github.rvesse.airline.tests.types;
}
