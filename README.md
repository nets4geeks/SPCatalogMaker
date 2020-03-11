
# Security Pattern Catalog Maker

SPCatalogMaker is a toolset for creation and maintenance of well-organized catalogs of security patterns
(also misuse patterns, threats etc.) in the OWL (Web Ontology Language) and RDF (Resource Description Framework) formats.

SPCatalogMaker consists of:

* an ontology-driven model (**Schema**) that describes concepts and instances for depiction of the security-specific knowledge;
* a small application (**Maker**) to build an ontology and RDF dataset from a set of source files.

## Schema

Schema contains an ontology-driven model of security pattern, including context and security characteristics
(components and functions of computer systems, security concerns and threats).

* [OWL File](schema/SecurityPatternCatalogNaiveSchema.owl)

Extra information about Schema:

* [Common description](schema_description.pdf)
* [Components and functions](schema_functions_components.pdf)
* [Threats, attacks and vulnerabilities](schema_threats.pdf)

## Maker

To maintaince a catalog you should create pieces of an ontology in the *catalog* folder, as examples show there.
For example, use **Protege** and include (*Direct imports*) the schema ontology (*schema/SecurityPatternCatalogNaiveSchema.owl*).

Maker allows to create a single OWL file from your pieces (by default *out/SecurityPatternCatalog.owl*),
and an inferred (i.e. processed with an automatic reasoner) RDF dataset (by default *out/SecurityPatternCatalogReasoned.ttl*). 

You can load the resulting OWL ontology in **Protege** and execute DL quieries, and it is possible to use the RDF file 
to make the SPARQL requests with the **Apache Jena** toolset (see the *examples* folder).

To compile Maker, you need **Java** and **Maven**. Simply clone the repository and run **./maker_compile**.

To make the datasets, run **./maker_run**.

For advanced configuration use the *maker.properties* file.


