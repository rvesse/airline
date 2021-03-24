### Requirement Restrictions

The following annotations are used to specify that options/arguments (or combinations thereof) are required:

- The [`@Required`]({{ include.path }}required.html) annotation indicates that an option/argument must be specified
- The [`@RequireSome`]({{ include.path }}require-some.html) annotation indicates that one/more from some set of options must be specified
- The [`@RequireOnlyOne`]({{ include.path }}require-only-one.html) annotation indicates that precisely one of some set of options must be specified
- The [`@MutuallyExclusiveWith`]({{ include.path }}mutually-exclusive-with.html) annotation indicates that precisely one of some set of options may be specified

### Occurrence Restrictions

The following annotations are used to specify the number of times that options/arguments can be specified:

- The [`@Once`]({{ include.path }}once.html) annotation indicates that at option/argument may be specified only once
- The [`@MinOccurrences`]({{ include.path }}min-occurrences.html) annotation indicates that an option/argument must be specified a minimum number of times
- The [`@MaxOccurrences`]({{ include.path }}max-occurrences.html) annotation indicates that an option/argument may be specified a maximum number of times

### Value Restrictions

The following annotations are used to specify restrictions on the values for options/arguments:

- The [`@AllowedRawValues`]({{ include.path }}allowed-raw-values.html) annotation specifies a set of strings that may be specified as the value
- The [`@AllowedValues`]({{ include.path }}allowed-values.html) annotation specifies a set of values that may be specified as the value
- The [`@MaxLength`]({{ include.path }}max-length.html) annotation specifies the maximum length of the value that may be given
- The [`@MinLength`]({{ include.path }}min-length.html) annotation specifies the minimum length of the value that may be given
- The [`@NotBlank`]({{ include.path }}not-blank.html) annotation specifies that a value may not consist entirely of white space
- The [`@NotEmpty`]({{ include.path }}not-empty.html) annotation specifies that the value may not be an empty string
- The [`@IsPath`]({{ include.path }}path.html) annotation specifies restrictions on values that refer to files and/or directories
- The [`@Pattern`]({{ include.path }}pattern.html) annotation specifies that a value must match a regular expression
- The [`@Port`]({{ include.path }}port.html) annotation specifies restrictions on values that represent port numbers

### Range Value Restrictions

A further subset of annotations specify restrictions on the values for options/arguments in terms of ranges of acceptable values:

- The [`@ByteRange`]({{ include.path }}byte-range.html) annotation specifies a range of `byte` values that are acceptable
- The [`@DoubleRange`]({{ include.path }}double-range.html) annotation specifies a range of `double` values that are acceptable
- The [`@FloatRange`]({{ include.path }}float-range.html) annotation specifies a range of `float` values that are acceptable
- The [`@IntegerRange`]({{ include.path }}integer-range.html) annotation specifies a range of `int` values that are acceptable
- The [`@LexicalRange`]({{ include.path }}lexical-range.html) annotation specifies a range of `string` values that are acceptable
- The [`@LongRange`]({{ include.path }}long-range.html) annotation specifies a range of `long` values that are acceptable
- The [`@ShortRange`]({{ include.path }}short-range.html) annotation specifies a range of `short` values that are acceptable

### Special Restrictions

The following are special purpose restrictions:

- The [`@Unrestricted`]({{ include.path }}unrestricted.html) annotation indicates that no restrictions apply to the option.  Used in conjunction with option overriding to clear restrictions.
- The [`@Partials/@Partial`]({{ include.path }}partials.html) annotation is used to apply restrictions to only some parts of options/arguments where multiple values are accepted by those fields

### Global Restrictions

The following are [Global restrictions](../restrictions/global.html) which apply over the final parser state:

- The [`@CommandRequired`]({{ include.path}}command-required.html) annotation indicates that a command must be specified.
- The [`@NoMissingOptionValues`]({{ include.path}}no-missing-option-values.html) annotation indicates that specifying an option without its corresponding value(s) is not permitted.
- The [`@NoUnexpectedArguments`]({{ include.path}}no-unexpected-arguments.html) annotation indicates that any unrecognised inputs are not permitted.
