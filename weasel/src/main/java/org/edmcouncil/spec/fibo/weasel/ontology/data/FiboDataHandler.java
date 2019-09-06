package org.edmcouncil.spec.fibo.weasel.ontology.data;

import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.edmcouncil.spec.fibo.weasel.model.FiboModule;
import org.edmcouncil.spec.fibo.weasel.model.PropertyValue;
import org.edmcouncil.spec.fibo.weasel.model.WeaselOwlType;
import org.edmcouncil.spec.fibo.weasel.model.property.OwlDetailsProperties;
import org.edmcouncil.spec.fibo.weasel.model.property.OwlFiboModuleProperty;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * This data handler working with FIBO ontology.
 *
 * @author Michał Daniel (michal.daniel@makolab.com)
 */
@Component
public class FiboDataHandler {

  private static final String DOMAIN_POSTFIX = "Domain";
  private static final String DOMAIN_KEY = "domain";
  private static final String MODULE_POSTFIX = "Module";
  private static final String MODULE_KEY = "module";
  private static final String ONTOLOGY_KEY = "ontology";
  private static final String METADATA_PREFIX = "Metadata";
  private static final String URL_DELIMITER = "/";

  private static final String MODULE_IRI = "http://www.omg.org/techprocess/ab/SpecificationMetadata/Module";

  private static final Logger LOGGER = LoggerFactory.getLogger(FiboDataHandler.class);

  @Autowired
  private AnnotationsDataHandler annotationsDataHandler;
  @Autowired
  private IndividualDataHandler individualDataHandler;

  public OwlDetailsProperties<PropertyValue> handleFiboModulesData(OWLOntology ontology, OWLEntity entity) {

    OWLDataFactory df = OWLManager.getOWLDataFactory();

    Iterator<OWLAnnotation> iterator = EntitySearcher
        .getAnnotations(entity, ontology, df.getRDFSIsDefinedBy())
        .iterator();

    OwlDetailsProperties<PropertyValue> result = new OwlDetailsProperties<>();

    while (iterator.hasNext()) {
      OWLAnnotation annotation = iterator.next();

      String isDefinedBy = annotation.annotationValue().toString();

      String[] splitedStr = isDefinedBy.split("/");
      int length = splitedStr.length;
      String domain = splitedStr[length - 3];
      String module = splitedStr[length - 2];
      String onto = splitedStr[length - 1];

      String fiboPath = prepareFiboPath(splitedStr);

      String domainIriString = prepareDomainIri(fiboPath, domain);
      result.addProperty(DOMAIN_KEY, createProperty(domain.concat(DOMAIN_POSTFIX), domainIriString));

      String moduleIriString = prepareModuleIri(fiboPath, domain, module);
      result.addProperty(MODULE_KEY, createProperty(module.concat(MODULE_POSTFIX), moduleIriString));
      String ontologyIriString = isDefinedBy;
      result.addProperty(ONTOLOGY_KEY, createProperty(onto, ontologyIriString));

      LOGGER.debug("[Fibo Data Handler] domainIRI: {};\n\tmoduleIRI: {};\n\t ontologyIRI: {};",
          domainIriString, moduleIriString, ontologyIriString);
    }

    return result;
  }

  private OwlFiboModuleProperty createProperty(String name, String iriString) {
    OwlFiboModuleProperty property = new OwlFiboModuleProperty();
    property.setIri(iriString);
    property.setName(name);
    property.setType(WeaselOwlType.MODULES);

    return property;
  }

  private String prepareFiboPath(String[] splitedStr) {
    StringBuilder stringBuilder = new StringBuilder();
    for (String fragment : splitedStr) {
      if (fragment.equals("http:") || fragment.equals("https:")) {
        stringBuilder.append(fragment).append(URL_DELIMITER);
      } else {
        stringBuilder.append(fragment).append(URL_DELIMITER);
        if (fragment.equals("ontology")) {
          break;
        }
      }
    }
    String fiboPath = stringBuilder.toString();
    return fiboPath;
  }

  private String prepareModuleIri(String fiboPath, String domain, String module) {
    String moduleIriString = fiboPath.concat(domain).concat(URL_DELIMITER)
        .concat(module).concat(URL_DELIMITER)
        .concat(METADATA_PREFIX).concat(domain).concat(module).concat(URL_DELIMITER)
        .concat(module).concat(MODULE_POSTFIX);
    return moduleIriString;
  }

  private String prepareDomainIri(String fiboPath, String domain) {
    String domainIri = fiboPath.concat(domain).concat(URL_DELIMITER)
        .concat(METADATA_PREFIX).concat(domain).concat(URL_DELIMITER)
        .concat(domain).concat(DOMAIN_POSTFIX);
    return domainIri;
  }

  public OwlDetailsProperties<PropertyValue> handleFiboOntologyMetadata(IRI iri, OWLOntology ontology) {
    OWLOntologyManager manager = ontology.getOWLOntologyManager();
    OwlDetailsProperties<PropertyValue> annotations = null;
    for (OWLOntology onto : manager.ontologies().collect(Collectors.toSet())) {
      if (onto.getOntologyID().getOntologyIRI().get().equals(iri)) {
        annotations = annotationsDataHandler.handleOntologyAnnotations(onto.annotations());
        break;
      }

    }
    return annotations;
  }

  public Set<FiboModule> getAllModulesData(OWLOntology ontology) {

    IRI moduleIri = IRI.create(MODULE_IRI);
    OWLClass clazz = ontology
        .classesInSignature()
        .filter(c-> c.getIRI().equals(moduleIri))
        .findFirst()
        .get();
    
    OwlDetailsProperties<PropertyValue> indi = individualDataHandler.handleClassIndividuals(ontology, clazz);
    
    return null;
  }

}