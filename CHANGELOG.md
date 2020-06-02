# Changelog
All notable changes to this project will be documented in this file.


## [0.2.0] - 2020-06-02
### Added
- handle dataproperties annotaions
- load default ontologies (dcterms, owl, rdf-schema, rdf-syntax, skos, fibo-glosary) from resource folder in application
- full configurable searcher
- specially endpoint for hints
- mark not released resources(PR#40)
- displaying OWLDataExactCardinality in graph
- displaying Annotation Property
- create rest api documentation(api-doc/rest_api.md)

### Changed
- changing the storing and returning graph value from generating string to objects
- small changes in config file structure
- change graph returned structures(more info in directory api-doc/graph.md )

### Removed
- deprecated methods in fibo data handler


## [0.1.0] - 2019-11-08
### Added
- external properties file used for configuration
- rest api
- data grouping function, defined in configuration file, groups can be renamed 
- extract and display direct subclasses, individuals, inherited axioms, meta information about ontologies, axioms, taxonomy,
- display information about modularization on webapp
- sort elements
- load ontology using url or file
- label support
- graph from the OWLRestriction for class
- ignore some items to display defined in configuration file
