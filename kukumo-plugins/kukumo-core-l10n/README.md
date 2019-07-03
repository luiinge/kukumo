# Kukumo::CORE (localization)

Localization for the Kukumo Core functionality, including logs, error messages, and assertion type expressions.

## Usage

Simply include this module as a dependency in your Kukumo launch process.

### Maven

```xml
<dependency>
  <groupId>iti.kukumo</groupId>
  <artifactId>kukumo-core-l10n</artifactId>
  <version>1.0.0</version>
</dependency>
```

### Kukumo Launcher

Include the following module coordinate either as a command line argument or in a configuration file.

`
kukumo -modules iti.kukumo:kukumo-core-l10n:1.0.0
`

## Localization

### Assertion Type Expressions

#### Spanish

| key | translation |
| --- | ----------- |
| `matcher.number.equals` | es (igual a) `x` 
| `matcher.number.greater` | es mayor que `x`
| `matcher.number.less` | es menor que `x`
| `matcher.number.greater.equals` | es mayor o igual que `x`
| `matcher.number.less.equals` | es menor o igual que `x`
| `matcher.string.equals` | es (igual a) `x`
| `matcher.string.equals.ignore.case` | es (igual a) `x` (sin distinguir mayúsculas)
| `matcher.string.equals.ignore.whitespace` | es (igual a) `x` (ignorando espacios)
| `matcher.string.starts.with` | empieza por `x`
| `matcher.string.starts.with.ignore.case` | empieza por `x` (sin distinguir mayúsculas)
| `matcher.string.ends.with` | acaba en `x`
| `matcher.string.ends.with.ignore.case` | acaba en `x` (sin distinguir mayúsculas)
| `matcher.string.contains` | contiene a `x`
| `matcher.string.contains.ignore.case` | contiene a `x` (sin distinguir mayúsculas)
| `matcher.generic.null` | es nulo
| `matcher.generic.empty` | está vacío
| `matcher.generic.null.empty` | es nulo o está vacío
| `matcher.number.not.equals` | no es (igual a) `x`
| `matcher.number.not.greater` | no es mayor que `x`
| `matcher.number.not.less` | no es menor que `x`
| `matcher.number.not.greater.equals` | no es mayor o igual que `x`
| `matcher.number.not.less.equals` | no es menor o igual que `x`
| `matcher.string.not.equals` | no es (igual a) `x`
| `matcher.string.not.equals.ignore.case` | no es (igual a) `x` (sin distinguir mayúsculas)
| `matcher.string.not.equals.ignore.whitespace` | no es (igual a) `x` (ignorando espacios)
| `matcher.string.not.starts.with` | no empieza por `x`
| `matcher.string.not.starts.with.ignore.case` | no empieza por `x` (sin distinguir mayúsculas)
| `matcher.string.not.ends.with` | no acaba en `x`
| `matcher.string.not.ends.with.ignore.case` | no acaba en `x` (sin distinguir mayúsculas)
| `matcher.string.not.contains` | no contiene a `x`
| `matcher.string.not.contains.ignore.case` | no contiene a `x` (sin distinguir mayúsculas)
| `matcher.generic.not.null` | no es nulo
| `matcher.generic.not.empty` | no está vacío
| `matcher.generic.not.null.empty` | no es nulo ni está vacío