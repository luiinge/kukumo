## Parts

### Regular text
An exact match, but ignoring case-sensitivity and extra whitespaces.
The symbols `?`, `|`, `(`, `)`, `{`, `}`, `[`, `]`, `*` and `\ `  have special meaning and must be escaped
property using the symbol `\ `. For example, the literal expression:
```
the root (of the filesystem) is \
```
should be escaped as:
```
the root \(of the filesystem\) is \\
```

### Optional segment
Enclosing a segment within the symbols `(` and `)`, the expression would match 
if the segment is *totally* present or *totally* absent.
#### Examples

| expression                  | matching                      |
|-----------------------------|-------------------------------|
| `the (selected) user`       | ✓ `The user`                  |
|                             | ✓ `The selected user`         |
|                             | ✗ `The lucky user`            |
| `The (lucky selected) user` | ✓ `The user`                  |
|                             | ✓ `The lucky selected user`   |
|                             | ✗ `The selected user`         |
|                             | ✗ `The unlucky selected user` |


### Negated segment
Prefixing a word with the symbols `^` , the expression would match
any word apart from the given word. It also can be used with a group 
of words grouping them using the symbols `[` and `]`.
#### Examples

| expression                   | matching                      |
|------------------------------|-------------------------------|
| `the ^selected user`         | ✓ `The opted user`            |
|                              | ✗ `The mainly opted user`     |
|                              | ✗ `The selected user`         |
|                              | ✗ `The user`                  |
| `The ^[lucky selected] user` | ✓ `The user`                  |
|                              | ✓ `The lucky selected user`   |
|                              | ✗ `The selected user`         |
|                              | ✗ `The unlucky selected user` |

### Choice segment
The expression matches only if one of the provided options is satisfied.
It can be used along with the optional symbol to express an optional choice. When the choices 
are formed by more than one word, the symbols `[` and `]` would be required.
Also, you can include an *empty* option using the optional operator.

| expression                                                     | matching                            |
|----------------------------------------------------------------|-------------------------------------|
| <code>the user was selected&#124;choosen</code>                | ✓ `The user was selected`           |
|                                                                | ✓ `The user was choosen`            |
|                                                                | ✗ `The user was opted`              |
| <code>The user was [selected &#124; choosen by destiny]</code> | ✓ `The user was selected`           |
|                                                                | ✓ `The user was choosen by destiny` |
|                                                                | ✗ `The user was opted`              |
|                                                                | ✗ `The user was choosen`            |
| <code>the (selected&#124;choosen) user</code>                  | ✓ `The selected user`               |
|                                                                | ✓ `The choosen user`                |
|                                                                | ✓ `The user`                        |
|                                                                | ✗ `The opted user`                  |


###Free segment
Using the symbol `*` allows to enter a segment with no restrictions (including an empty segment).
However, be aware that using it without enough context would be likely to cause 
ambiguous matches among other step definitions.
#### Examples

| expression                  | matching                      |
|-----------------------------|-------------------------------|
| `the * user`                | ✓ `The user`                  |
|                             | ✓ `The selected user`         |
|                             | ✓ `The lucky user`            |
|                             | ✓ `The unlucky selected user` |


###Argument segment
You can specify arguments in your step definition using the symbols `{` and `}`. 
There are two variants in the syntax:
- Explicitly-named argument: `{name:type}`
- Implicitly-name arguments: `{type}`

Arguments are only required to have a name when there is more than one argument of 
the same type. Otherwise, you can use the implicit declaration without issues.

The specific type names that can be used depend on the plugin environment. In absence of 
any addition plugin, the accepted built-in types are:
- `text`
- `id`
- `uri`
- `path`
- `word`
- `integer`
- `decimal`
- `date`
- `time`
- `date-time`
- `long`
- `float`
- `double`

Check the *Built-in Data Types* section for further information about the 
accepted values for each of them and the translated Java type to be used 
by the step contributor.

#### Examples

| expression                                   | matching                        |
|----------------------------------------------|---------------------------------|
| `the user named {text}`                      | ✓ `The user named 'John Smith'` |
| `the sum of {number1:int} and {number2:int}` | ✓ `The sum of 3 and 7`          |


### Sub-expressions
Sub-expressions are a convenient way of reusing step definition fragments. Each 
sub-expression would expand a step definition into many variants without having 
to explicitly define each of one.

For example, assume there is a step that validates the integer value result of 
an operation. You may define the following steps:

- `the operation result is {int}`
- `the operation result is greater than {int}`
- `the operation result is less than {int}`

That is good enough for one specific instance. But now we might want to apply the 
same validations to a side output, so we would to add extra step definitions:

- `the operation result is {int}`
- `the operation result is greater than {int}`
- `the operation result is less than {int}`
- `the side output is {int}`
- `the side output is greater than {int}`
- `the side output is less than {int}`

This scenario may well be repeated across several plugins, since integer validation
is a very basic feature to expect. 

Now, using Kukumo sub-expressions, you can extract the common fragments of these steps
and define them independently, so that you can add them to any step. For example,
given the sub-expression `integer-assertion` exploding to:

- `is {int}`
- `is greater than {int}`
- `is less than {int}`

you can rewrite the previous steps in a much more compact style:

- `the operation result {{integer-assertion}}`
- `the side output {{integer-assertion}}`
