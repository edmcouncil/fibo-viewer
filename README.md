<img src="https://spec.edmcouncil.org/fibo/htmlpages/master/latest/img/logo.66a988fe.png" width="150" align="right"/>


#About

FIBO Viewer is an open-source project that is hosted by EDM Council. The project started in May 2019. FIBO Viewer is a JAVA application that is specifically designed to access both the FIBO structure and its content in the easiest possible way. FIBO Viewer servers both as a web application and REST API.

## How to contribute
Since the first release of FIBO Viewer, [this  repository](https://github.com/edmcouncil/fibo-viewer) is considered to be the only official space for the community discussion. So, this repository is where the project community develops the new proposals and discusses changes (via GitHub issues).


If you want to contribute to FIBO Viewer, you'll need to create a login for [GitHub](https://github.com). Then you can contribute to FIBO Viewer in two ways. 

The first way is to suggest changes via GitHub issues, e.g., the suggested changes may concern the GUI of application.

The second way is to contribute directly to the code. For that purpose, you'll need to do the following things: 

* Install a git client.  In FIBO, we recommend [Sourcetree](https://www.sourcetreeapp.com) from Atlassian
* Make a "fork" of the [fibo-viewer](https://github.com/edmcouncil/fibo-viewer) repository. Clone your fork to your local repository.
* Submit a Pull Request to the [fibo-viewer](https://github.com/edmcouncil/fibo-viewer) repository.



# License
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)


# How to run FIBO Viewer

Download the file named "fibo\_viewer\_relase.zip" from the [latest release](https://github.com/edmcouncil/fibo-viewer/releases). Unzip the file. In the command prompt of your operating system run the following command in the folder with the last release: 

```
java -jar app-v-LAST_VERSION_NUMBER.war
```
e.g.,

```
java -jar app-v-0.1.0.war
```

# FIBO Viewer project structure


## Fibo-Viewer-Core module (viewer-core folder)

This module contains methods responsible for extracting ontological components of FIBO, i.e., its modules, taxonomy, classes, properties, individuals, axioms, etc.  

* For a given IRI, the module checks the type of object the IRI belongs to. An object type can be class, individual, data property, or object property. Then for the IRI, Fibo-Viewer-Core gets information from FIBO about the resource represented by this IRI (e.g., annotations, taxonomy, subclasses, inherited axioms, metadata, instances, subelements, etc.). In the next step, the resource information is grouped and labeled.
* FIBO has a modular structure and contains meta-information about many domain ontologies of which it is composed. Fibo-Viewer-Core checks for a given FIBO IRI, whether it is FIBO ontology or FIBO entity. If IRI ends with '/', it means it is an ontology. In other case is an entity. 
* Labels that have been used once in a program session are saved in memory.
* Fibo-Viewer-Core generates graphs from FIBO restrictions. For a given FIBO class, its graph is displayed at the bottom of the page.  
* FIBO individuals are handled by OWLReasonerFactory. 


## Fibo-Config-Loader module (config-loader folder) This module is responsible for loading configuration for FIBO Viewer. 


## Fibo-Web-App module (web-app folder)
This module is responsible for displaying the input data from the Fibo-Viewer-Core module.
 
* Fibo-Web-App displays data using JSP templates.
* The css files are located in the "resources" directory.
* The vis.css file is responsible for displaying graphs.
* The graphs are displayed by vis-network.


web-app folder condains XML file "fibo\_viewer\_config.xml" that defines the FIBO Viewer configuration. Let us walk through the file.




### Load FIBO

FIBO can be loaded from the "AboutFIBOProd" file located in a folder. To load FIBO from the file on your disc, provide the path to the file as follows:

```
<ontologyPath>PATH/AboutFIBOProd.rdf</ontologyPath>
```

FIBO can also be loaded from URL. E.g.

```
<ontologyURL>https://spec.edmcouncil.org/fibo/ontology/master/latest/AboutFIBOProd.rdf</ontologyURL>
```

If neither URL nor path is provided, FIBO Viewer will look for the "AboutFIBOProd" file in the default folder.

### FIBO Development and FIBO Product
> <img 
> src="https://spec.edmcouncil.org/fibo/htmlpages/master/latest/img/FIBO
> _logo.11aeaf9b.jpg" width="300" align="right"/>
> 
> Note that FIBO Development is published in real time as changes are 
> incorporated by the FIBO Community Group and consists of draft as well 
> vetted content. The latest version is always avaliable at
> 
> * 
> [https://spec.edmcouncil.org/fibo/ontology/master/latest/AboutFIBODev.
> ttl](https://spec.edmcouncil.org/fibo/ontology/master/latest/AboutFIBO
> Dev.ttl)
> 
> FIBO Production is published at the end of each calendar quarter and has been vetted by SMEs and passed standard industry hygiene tests for OWL. To access the latest FIBO Production use the following URL:
> 
> * 
> [https://spec.edmcouncil.org/fibo/ontology/master/latest/AboutFIBOProd
> .ttl](https://spec.edmcouncil.org/fibo/ontology/master/latest/AboutFIB
> OProd.ttl)
> 
> and to read FIBO from a give quarter use the following URL:
> 
> * 
> [https://spec.edmcouncil.org/fibo/ontology/master/QUATER_REFERENCE/Abo
> utFIBOProd.ttl](https://spec.edmcouncil.org/fibo/ontology/master/2019Q
> 3/AboutFIBOProd.ttl)
> 
> An example of QUATER_REFERENCE may be "2019Q3".



### Labels display

Exemplary labels display configuration is shown below.

```
<labelConfig>
       <displayLabel>TRUE</displayLabel>
       <labelPriority>LABEL</labelPriority>
       <forceLabelLang>FALSE</forceLabelLang>
       <labelLang>en</labelLang>
       <missingLanguageAction>FIRST</missingLanguageAction>
       <userDefinedNamesList>
           <userDefinedName>
               <resourceIriToName>http://www.w3.org/2000/01/rdf-schema#Literal</resourceIriToName>
               <resourceIriName>literal</resourceIriName>
           </userDefinedName>
           <userDefinedName>
               <resourceIriToName>http://www.w3.org/2001/XMLSchema#string</resourceIriToName>
               <resourceIriName>string</resourceIriName>
           </userDefinedName>
       </userDefinedNamesList>
   </labelConfig>
```


If you want names for FIBO resources to be displayed instead of IRI fragments, enter "TRUE" between displayLabel tags: 

```
       <displayLabel>TRUE</displayLabel> ```

If "FALSE" is entered, IRI fragments will be displayed. 

If you want rdfs:labels for the FIBO resources to be display, enter "TRUE" between the displayLabel tags and "RDS_LABEL" between the labelPriority tags. 

```
       <displayLabel>TRUE</displayLabel>
       <labelPriority>RDFS_LABEL</labelPriority>
```

If you want the user-defined names to be displayed for the resources, enter "TRUE" between the displayLabel tags and "USER_DEFINED" between labelPriority tags:

```
       <displayLabel>TRUE</displayLabel>
       <labelPriority>USER_DEFINED</labelPriority>
```

If "RDFS_LABEL" is entered, but a FIBO resource does not have rdfs:label, FIBO Viewer will automatically look for names defined between the userDefinedNamesList tags, e.g.:
 
```
       <userDefinedNamesList>
           <userDefinedName>
               <resourceIriToName>http://www.w3.org/2000/01/rdf-schema#Literal</resourceIriToName>
               <resourceIriName>literal</resourceIriName>
           </userDefinedName>
           <userDefinedName>
               <resourceIriToName>http://www.w3.org/2001/XMLSchema#string</resourceIriToName>
               <resourceIriName>string</resourceIriName>
           </userDefinedName>
       </userDefinedNamesList>
```

To display labels with a given language, enter "TRUE" between the forceLabelLang tags. 

```
       <forceLabelLang>TRUE</forceLabelLang>
       <labelLang>en</labelLang>     
```

If the language is not present, an IRI fragment will be displayed. 
When a resource has more than one label, then one of them randomly picked will be displayed. 

If a language tag is not forced, we may still have a situation where we have more than one label for the resource, and none of them has a language tag. 

Line with code above defines an action when a label is not forced, and the entity has more than one label, but none has a language. "FIRST" value means that one of them randomly picked will be displayed. "FRAGMENT" value determines that the IRI fragment of the resource will be displayed.

```
       <forceLabelLang>FALSE</forceLabelLang>
       <missingLanguageAction>FIRST</missingLanguageAction>
```

Ontological information that a user wants to hide should be placed between ignore tags, e.g.:

```
<ignoreToDisplaying>
    <ignore>SubObjectPropertyOf</ignore>
</ignoreToDisplaying>
```

### URLs that are and are not IRIs of FIBO resources

Between the uriNamespace tags that are wrapped in the scopeIri tags, there are defined the URLs that are to be treated as the resource IRIs. 

```
<scopeIri>
   <uriNamespace>https://spec.edmcouncil.org/fibo/ontology</uriNamespace>
   <uriNamespace>https://www.omg.org/spec/</uriNamespace>
</scopeIri>
```

### Grouping

In the fibo\_vierwer\_config.xml file, there are tags that are responsible for displaying group names and the list of grouped items. Group elements must have a packaging tag.

```
<groups>
    <group>
      <groupName>Natural Language characteristic</groupName>
      <groupItem>http://www.w3.org/2000/01/rdf-schema#label</groupItem>
      <groupItem>https://spec.edmcouncil.org/fibo/ontology/FND/Utilities/AnnotationVocabulary/synonym</groupItem>
      ...
    </group>

    <group>
      <groupName>Ontological characteristic</groupName>
      <groupItem>@viewer.axiom.EquivalentClasses</groupItem>
      <groupItem>@viewer.axiom.SubClassOf</groupItem>
      ...
    </group>
</groups>    
```

To display items, enter their local IDs or IRIs between groupItem tags. Each list of groups should be wrapped into groups tags. The display order of groups and their items is determined by order of these elements in the config file or the priority list:

```
<priorityList>
    <priority>http://www.w3.org/2000/01/rdf-schema#subClassOf</priority>
    <priority>http://www.w3.org/2000/01/rdf-schema#seeAlso</priority>
</priorityList>
```

# Release Management
Release management (corresponding to software versioning in source code management) will follow the widely accepted  [Git's branching model](https://nvie.com/posts/a-successful-git-branching-model/).



## Versioning Policy
Versioning policy rules are the same for all components created in the project. All components should have the same version number (two first sequences) to reflect their proper cooperation. It means that there can be situations in which one component will have version changed, even if there were no code change.

A version number contains three sequences on numbers separated with a "." (dot) sign. A sample of version number is the following: 1.1.2. Rules for sequences and numbers:

* first two sequences reflect the version number of all services, frontend, and apps that should be deployed and run together,
* the first sequence is changed when API is changed,
* the second sequence is changed with every release,
* The last sequence indicates a version with a bug fix in release branch or production.

## Branching Policy
There are two main branches for source code in repository:

* master – main branch, reflects production version of the application,
* develop – derived from the master branch, used for synchronization of developers' work - releases are created from this branch

See the figure: [Git's branching model](https://nvie.com/posts/a-successful-git-branching-model/)

Additional branches used in versioning:

* feature branches – derives from develop, name of this branch is based on the name of the feature after feature is done the branch is merged with develop branch,
* release branch – derives from develop, branch is dedicated to the new release, name of the branch comes from release version number (release_v1.1); 
    * release branch is tagged (the version of the release: v1.1.3) and merged to develop branch after production release; 
    * release branch is merged with master branch during production deployment
* bugfix branches – derives from the master branch, used for critical bug fixing (bug found in production version; bugfix_v1.1.3)


# Release notes
## Last release
FIBO Viewer version 0.1.0 ([CHANGELOG.md](CHANGELOG.md))

