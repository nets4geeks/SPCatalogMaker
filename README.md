
# Security Pattern Catalog Maker

SPCatalogMaker is a toolset for creation and maintenance of well-organized catalogs of security patterns
(also misuse patterns, threats etc.). 

SPCatalogMaker consists of:

* an ontology-driven model (**Schema**) that describes concepts and instances for depiction of the security-specific knowledge
in a formal way;
* a small application (**Maker**) to build an OWL (Web Ontology Language) ontology and RDF (Resource Description Framework) 
dataset from a set of source ontologies and JSON (JavaScript Object Notation) files.


We use these tools for development of the **ACCTP catalog** (see below).

## Schema

The schema allows a) creation of security pattern catalogs, 
and b) definition of context labels to map patterns with design decisions and security problems.

The format includes five sections 
(Metadata, Organization and scope, Common characteristics, Context characteristics, Security characteristics). 
The last two are used to put a pattern into a context and define applicability as a solution of a security problem.

* [OWL File](schema/SecurityPatternCatalogNaiveSchema.owl)

Extra information about Schema:

* [Common description](schema_description.pdf)
* [Threats, attacks and vulnerabilities](schema_threats.pdf)


## Maker usage

The development of a SP catalog includes two steps:
* Firstly, you can create an ontology, based on our Schema, to describe concepts and instances of a specific domain 
(e.g. cloud computing). Such ontology contains a semantic description of a computing environment.

Then you can run the **Maker** application and get a *JSON schema file* from your ontologies.

* On the second stage you can use a schema-based JSON editor (like well-known *Atom* with the *atom-json-editor* plugin) 
to create *decriptions of patterns* in the JSON format.

Maker allows creation of a *target ontology* (and *inferred RDF dataset*) from those pieces of JSON.

To compile Maker, you need **Java** and **Maven**. Simply clone the repository and run **./maker_compile**.
To make the datasets, run **./maker_XXX.run**. For configuration use the *maker_XXX.properties* file.


## Academic Cloud Computing Threat Patterns (ACCTP) catalog

The Academic Cloud Computing Threat Patterns (ACCTP) catalog is based on the data of the common security knowledge sources, 
as well as the academic community findings.
It “mines” the knowledge of the risk-oriented cloud security models and maps the risk-based terminology (risks, assets) 
to the design terminology (components, flows, boundaries, threats).

The catalog contains different profiles (architecture, compliance, privacy, IaaS, PaaS, SaaS, cloud storage); 
all the entities are mapped to the STRIDE model.



And [source folder is here](catalogs/acctp/catalog/).
The *common.owl* file is an example of ontological description of the cloud computing domain.
Generated JSON schema (first stage) is in *cd.schema.json*; and Threat patterns descriptions are in the *xxx.json* files.
*ACCTPCatalog.owl* and *ACCTPCatalogReasoned.ttl* are target ontology and RDF dataset respectively (second stage).

You can use the OWL ontology to make DL queries, for example, with **Protege**.
It is possible to use the RDF dataset to make the SPARQL requests with the **Apache Jena** toolset.

Also, the ACCTP catalog can be used as a source of domain specific threat model 
for the [ontlogy-driven threat modeling](https://github.com/nets4geeks/OdTM)
of the cloud systems with the DFD (Data Flow Diagram) approach.

### References
If you want to refer to the Schema and Maker, please cite:
>Brazhuk A., Olizarovich E. Format and Usage Model of Security Patterns in Ontology-Driven Threat Modelling.
//Russian Conference on Artificial Intelligence (RCAI 2020). – Springer, Cham, 2020. – С. 382-392.
https://doi.org/10.1007/978-3-030-59535-7_28
