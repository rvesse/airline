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
module com.github.rvesse.airline
{
  requires com.github.rvesse.airline.io;
  requires org.apache.commons.lang3;
  requires org.apache.commons.collections4;
  requires java.inject;

  exports com.github.rvesse.airline;
  exports com.github.rvesse.airline.annotations;
  exports com.github.rvesse.airline.annotations.help;
  exports com.github.rvesse.airline.annotations.restrictions;
  exports com.github.rvesse.airline.annotations.restrictions.global;
  exports com.github.rvesse.airline.annotations.restrictions.ranges;
  exports com.github.rvesse.airline.builder;
  exports com.github.rvesse.airline.help;
  exports com.github.rvesse.airline.help.cli;
  exports com.github.rvesse.airline.help.common;
  exports com.github.rvesse.airline.help.sections;
  exports com.github.rvesse.airline.help.sections.common;
  exports com.github.rvesse.airline.help.sections.factories;
  exports com.github.rvesse.airline.help.suggester;
  exports com.github.rvesse.airline.model;
  exports com.github.rvesse.airline.parser;
  exports com.github.rvesse.airline.parser.aliases;
  exports com.github.rvesse.airline.parser.command;
  exports com.github.rvesse.airline.parser.errors;
  exports com.github.rvesse.airline.parser.errors.handlers;
  exports com.github.rvesse.airline.parser.options;
  exports com.github.rvesse.airline.parser.resources;
  exports com.github.rvesse.airline.parser.suggester;
  exports com.github.rvesse.airline.restrictions;
  exports com.github.rvesse.airline.restrictions.common;
  exports com.github.rvesse.airline.restrictions.factories;
  exports com.github.rvesse.airline.restrictions.global;
  exports com.github.rvesse.airline.restrictions.options;
  exports com.github.rvesse.airline.types;
  exports com.github.rvesse.airline.types.numerics;
  exports com.github.rvesse.airline.types.numerics.abbreviated;
  exports com.github.rvesse.airline.types.numerics.bases;
  exports com.github.rvesse.airline.utils;
  exports com.github.rvesse.airline.utils.comparators;
  exports com.github.rvesse.airline.utils.predicates;
  exports com.github.rvesse.airline.utils.predicates.parser;
  exports com.github.rvesse.airline.utils.predicates.restrictions;

  provides com.github.rvesse.airline.help.sections.factories.HelpSectionFactory with
      com.github.rvesse.airline.help.sections.factories.CommonSectionsFactory;

  provides com.github.rvesse.airline.restrictions.factories.ArgumentsRestrictionFactory with
      com.github.rvesse.airline.restrictions.factories.AllowedValuesRestrictionFactory,
      com.github.rvesse.airline.restrictions.factories.OccurrencesRestrictionFactory,
      com.github.rvesse.airline.restrictions.factories.PathRestrictionFactory,
      com.github.rvesse.airline.restrictions.factories.PortRestrictionFactory,
      com.github.rvesse.airline.restrictions.factories.RangeRestrictionFactory,
      com.github.rvesse.airline.restrictions.factories.SimpleRestrictionsFactory,
      com.github.rvesse.airline.restrictions.factories.StringRestrictionFactory;

  provides com.github.rvesse.airline.restrictions.factories.GlobalRestrictionFactory with
      com.github.rvesse.airline.restrictions.factories.StandardGlobalRestrictionsFactory;

  provides com.github.rvesse.airline.restrictions.factories.OptionRestrictionFactory with
      com.github.rvesse.airline.restrictions.factories.AllowedValuesRestrictionFactory,
      com.github.rvesse.airline.restrictions.factories.OccurrencesRestrictionFactory,
      com.github.rvesse.airline.restrictions.factories.PathRestrictionFactory,
      com.github.rvesse.airline.restrictions.factories.PortRestrictionFactory,
      com.github.rvesse.airline.restrictions.factories.RangeRestrictionFactory,
      com.github.rvesse.airline.restrictions.factories.RequiredOnlyIfRestrictionFactory,
      com.github.rvesse.airline.restrictions.factories.RequireFromRestrictionFactory,
      com.github.rvesse.airline.restrictions.factories.SimpleRestrictionsFactory,
      com.github.rvesse.airline.restrictions.factories.StringRestrictionFactory;
  
  provides com.github.rvesse.airline.ChannelFactory with
      com.github.rvesse.airline.SystemChannelFactory;

  uses com.github.rvesse.airline.ChannelFactory;
  uses com.github.rvesse.airline.help.sections.factories.HelpSectionFactory;
  uses com.github.rvesse.airline.restrictions.factories.ArgumentsRestrictionFactory;
  uses com.github.rvesse.airline.restrictions.factories.GlobalRestrictionFactory;
  uses com.github.rvesse.airline.restrictions.factories.OptionRestrictionFactory;

}
