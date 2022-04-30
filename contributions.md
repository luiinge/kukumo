Kukumo Core contributions
====================================================================================================

Extension points
----------------------------------------------------------------------------------------------------

### ConfigContribution

Allow registering *configurations*, each one including a set of properties along with their
default values or validation rules. The overall configuration used in an execution would be
a merge of all configurations provided by the existing plugins.



### ContentType

Allow registering *content types*, that is, ways to interpret a stream of bytes 
that may come from different sources (as long as a `InputStream` can be obtained). 
The output of this interpretation may vary for each implementation: for example, the `gherkin`
content type would read and parse a Gherkin document producing a Java model of the document.

#### Interactions
- `SourceDiscoverer` requires defining a content type in order to parse the test plan sources
- `PlanAssembler` would check the content type of the received source to determine whether the
source is suited for the plan assembler

#### Provided extensions
- `text`
- `gherkin`
- `xml`
- `json`


### SourceDiscoverer

A *source discoverer* is a component that scan the environment (according some configuration)
and try to create source objects. These objects would be used to attempt to build the test plan.

#### Interactions
- `ContentType` is used in order to parse the source inputs
- `PlanAssembler` would used the source objects discovered to attempt to build the test plan

#### Provided extensions
- `gherkin-discoverer` reads `*.feature` files from the path defined by the `sourcePath` configuration
property


### DataType

Allow registering *data types*, small components that would parse data fragments of the steps 
expressions and transform them into Java types. The used locale may change how data is 
represented.

#### Interactions
- `StepContributor` uses data types in the step expressions (referencing them by their name)


