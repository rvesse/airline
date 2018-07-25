---
layout: page
title: Text Help Generators
---

The Text help generator, also referred to as the CLI help generator, is the default help generator built into the core Airline library.  It provides a full range of help generator implementations that can be used to generate help for CLIs, command groups and commands.

### Available Implementations

The following implementations are available:

- {% include github-ref.md package="help.cli" class="CliGlobalUsageGenerator" %} - Full blown CLI help
- {% include github-ref.md package="help.cli" class="CliGlobalUsageSummaryGenerator" %} - Quick summary help for CLIs
- {% include github-ref.md package="help.cli" class="CliCommandGroupUsageGenerator" %} - Help for command groups
- {% include github-ref.md package="help.cli" class="CliCommandUsageGenerator" %} - Help for individual commands 

{% include helpgen-examples.md global="CliGlobalUsageSummaryGenerator" group="CliCommandGroupUsageGenerator" command="CliCommandUsageGenerator" %}