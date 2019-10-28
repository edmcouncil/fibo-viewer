package org.edmcouncil.spec.fibo.weasel.ontology.data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.edmcouncil.spec.fibo.config.configuration.model.AppConfiguration;
import org.edmcouncil.spec.fibo.config.configuration.model.impl.WeaselConfiguration;
import org.edmcouncil.spec.fibo.weasel.model.FiboModule;
import org.edmcouncil.spec.fibo.weasel.model.PropertyValue;
import org.edmcouncil.spec.fibo.weasel.model.WeaselOwlType;
import org.edmcouncil.spec.fibo.weasel.model.onto.OntologyResources;
import org.edmcouncil.spec.fibo.weasel.model.onto.OntologyResourcesTypeDefaultKeys;
import org.edmcouncil.spec.fibo.weasel.model.property.OwlAnnotationIri;
import org.edmcouncil.spec.fibo.weasel.model.property.OwlDetailsProperties;
import org.edmcouncil.spec.fibo.weasel.model.property.OwlFiboModuleProperty;
import org.edmcouncil.spec.fibo.weasel.model.property.OwlListElementIndividualProperty;
import org.edmcouncil.spec.fibo.weasel.utils.StringUtils;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
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
  //TODO: move this to set to configuration 
  private static final String MODULE_IRI = "http://www.omg.org/techprocess/ab/SpecificationMetadata/Module";

  private static final String RESOURCE_INTERNAL_PREFIX = "internal ";
  private static final String RESOURCE_EXTERNAL_PREFIX = "external ";

  private static final Logger LOGGER = LoggerFactory.getLogger(FiboDataHandler.class);

  @Autowired
  private AnnotationsDataHandler annotationsDataHandler;
  @Autowired
  private IndividualDataHandler individualDataHandler;
  @Autowired
  private AppConfiguration configuration;

  private String resourcesClassKey;
  private String resourcesDataPropertyKey;
  private String resourcesObjectPropertyKey;
  private String resourcesInstanceKey;

  private List<FiboModule> modules;

  private Map<String, OntologyResources> resources = null;

  @Deprecated
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

      LOGGER.debug("[FIBO Data Handler] domainIRI: {};\n\tmoduleIRI: {};\n\t ontologyIRI: {};",
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
        OntologyResources ors = getOntologyResources(iri.toString(), ontology);
        for (Map.Entry<String, List<PropertyValue>> entry : ors.getResources().entrySet()) {
          for (PropertyValue propertyValue : entry.getValue()) {
            annotations.addProperty(entry.getKey(), propertyValue);
          }
        }

        break;
      }
    }
    return annotations;
  }

  public List<FiboModule> getAllModulesData(OWLOntology ontology) {
    if (modules != null) {
      return modules;
    }
    List<FiboModule> result = new LinkedList<>();
    IRI moduleIri = IRI.create(MODULE_IRI);
    OWLClass clazz = ontology
        .classesInSignature()
        .filter(c -> c.getIRI().equals(moduleIri))
        .findFirst()
        .get();

    OwlDetailsProperties<PropertyValue> indi = individualDataHandler.handleClassIndividuals(ontology, clazz);

    Set<String> modulesIriSet = new HashSet<>();

    indi.getProperties().get(WeaselOwlType.INSTANCES.name()).stream()
        .map((propertyValue) -> (OwlListElementIndividualProperty) propertyValue)
        .map((individProperty) -> (String) individProperty.getValue().getValueB())
        .forEachOrdered((elIri) -> {
          modulesIriSet.add(elIri);
        });

    List<String> rootModulesIris = getRootModulesIris(modulesIriSet, ontology);

    rootModulesIris.stream()
        .map((rootModulesIri) -> {
          FiboModule fm = new FiboModule();
          fm.setIri(rootModulesIri);
          fm.setLabel(StringUtils.getFragment(rootModulesIri));
          fm.setSubModule(getSubModules(rootModulesIri, ontology));
          return fm;
        }).forEachOrdered(result::add);

    modules = result.stream()
        .sorted((obj1, obj2) -> obj1.getLabel().compareTo(obj2.getLabel()))
        .map(r -> {
          r.sort();
          return r;
        }).collect(Collectors.toList());

    return result;
  }

  private List<String> getRootModulesIris(Set<String> modulesIriSet, OWLOntology ontology) {
    Map<String, Integer> referenceCount = new LinkedHashMap<>();
    modulesIriSet.forEach((mIri) -> {
      Set<String> hasPartModules = getHasPartElements(IRI.create(mIri), ontology);
      if (referenceCount.get(mIri) == null) {
        referenceCount.put(mIri, 0);
      }
      hasPartModules.forEach((partModule) -> {
        Integer c = referenceCount.getOrDefault(partModule, 0);
        c++;
        referenceCount.put(partModule, c);
      });
    });
    List<String> rootModulesIris = referenceCount.entrySet().stream()
        .filter(r -> r.getValue() == 0).map(r -> r.getKey())
        .collect(Collectors.toList());
    return rootModulesIris;
  }

  private void loadAllOntologyResources(OWLOntology ontology) {
    OWLOntologyManager manager = ontology.getOWLOntologyManager();
    Map<String, OntologyResources> allResources = new HashMap<>();

    completeKeysUsingTheConfiguration();

    manager.ontologies().collect(Collectors.toSet()).forEach((owlOntology) -> {
      OntologyResources ontoResources = extractOntologyResources(owlOntology);

      allResources.put(owlOntology.getOntologyID().getOntologyIRI().get().toString(), ontoResources);
    });
    resources = allResources;
  }

  private OntologyResources extractOntologyResources(OWLOntology owlOntology) {
    OntologyResources ontoResources = new OntologyResources();
    IRI ontologyIri = owlOntology.getOntologyID().getOntologyIRI().get();
    owlOntology.classesInSignature()
        .map(c -> {
          String istring = c.getIRI().toString();
          OwlAnnotationIri pv = CustomDataFactory.createAnnotationIri(istring);
          return pv;
        })
        .forEachOrdered(c -> ontoResources
        .addElement(generateResourceKey(resourcesClassKey, c, ontologyIri), c));

    owlOntology.dataPropertiesInSignature()
        .map(c -> {
          String istring = c.getIRI().toString();
          OwlAnnotationIri pv = CustomDataFactory.createAnnotationIri(istring);
          return pv;
        })
        .forEachOrdered(c -> ontoResources
        .addElement(generateResourceKey(resourcesDataPropertyKey, c, ontologyIri), c));

    owlOntology.objectPropertiesInSignature()
        .map(c -> {
          String istring = c.getIRI().toString();
          OwlAnnotationIri pv = CustomDataFactory.createAnnotationIri(istring);
          return pv;
        })
        .forEachOrdered(c -> ontoResources
        .addElement(generateResourceKey(resourcesObjectPropertyKey, c, ontologyIri), c));

    owlOntology.individualsInSignature()
        .map(c -> {
          String istring = c.getIRI().toString();
          OwlAnnotationIri pv = CustomDataFactory.createAnnotationIri(istring);
          return pv;
        })
        .forEachOrdered(c -> ontoResources
        .addElement(generateResourceKey(resourcesInstanceKey, c, ontologyIri), c));

    return ontoResources;
  }

  public OntologyResources getOntologyResources(String iri, OWLOntology ontology) {

    if (resources == null) {
      loadAllOntologyResources(ontology);
    }

    return resources.get(iri);
  }

  public Set<String> getHasPartElements(IRI iri, OWLOntology ontology) {

    OWLDataFactory dataFactory = OWLManager.getOWLDataFactory();
    Optional<OWLNamedIndividual> individual = ontology
        .individualsInSignature()
        .filter(c -> c.getIRI().equals(iri))
        .findFirst();
    if (individual.isPresent() == false) {
      return new HashSet<>(0);
    }
    Iterator<OWLAnnotation> iteratorAnnotation = EntitySearcher
        .getAnnotations(individual.get(), ontology,
            dataFactory.getOWLAnnotationProperty(IRI.create("http://purl.org/dc/terms/hasPart")))
        .iterator();

    Set<String> result = new LinkedHashSet<>();
    while (iteratorAnnotation.hasNext()) {
      OWLAnnotation annotation = iteratorAnnotation.next();
      String s = annotation.annotationValue().toString();
      result.add(s);
    }
    return result;
  }

  private void completeKeysUsingTheConfiguration() {

    WeaselConfiguration weaselConfiguration = (WeaselConfiguration) configuration.getWeaselConfig();
    String tmp = weaselConfiguration.getNewName(OntologyResourcesTypeDefaultKeys.CLASSES);
    resourcesClassKey = tmp == null ? OntologyResourcesTypeDefaultKeys.CLASSES : tmp;

    tmp = weaselConfiguration.getNewName(OntologyResourcesTypeDefaultKeys.DATA_PROPERTY);
    resourcesDataPropertyKey = tmp == null ? OntologyResourcesTypeDefaultKeys.DATA_PROPERTY : tmp;

    tmp = weaselConfiguration.getNewName(OntologyResourcesTypeDefaultKeys.OBJECT_PROPERTY);
    resourcesObjectPropertyKey = tmp == null ? OntologyResourcesTypeDefaultKeys.OBJECT_PROPERTY : tmp;

    tmp = weaselConfiguration.getNewName(OntologyResourcesTypeDefaultKeys.INSTANCES);
    resourcesInstanceKey = tmp == null ? OntologyResourcesTypeDefaultKeys.INSTANCES : tmp;

  }

  private List<FiboModule> getSubModules(String moduleIri, OWLOntology ontology) {
    List<FiboModule> result = new LinkedList<>();

    Set<String> hasPartModules = getHasPartElements(IRI.create(moduleIri), ontology);

    hasPartModules.stream().map((partModule) -> {
      FiboModule fm = new FiboModule();
      fm.setIri(partModule);
      fm.setLabel(StringUtils.getFragment(partModule));
      fm.setSubModule(getSubModules(partModule, ontology));
      return fm;
    }).forEachOrdered(result::add);

    return result;
  }

  private String generateResourceKey(String resourcesKey, OwlAnnotationIri c, IRI ontologyIri) {
    String annotationIri = c.getValue().getIri();

    return annotationIri.contains(ontologyIri) ? RESOURCE_INTERNAL_PREFIX.concat(resourcesKey)
        : RESOURCE_EXTERNAL_PREFIX.concat(resourcesKey);
  }

  /**
   *This method is collect the path to the module in which it is located.
   * 
   * @param elementIri IRI element to which path we want to find
   * @param ontology
   * @return Returns the path to the module in which it is located, empty list if element not
   * present in modules
   */
  public List<String> getElementLocationInModules(String elementIri, OWLOntology ontology) {
    List<String> result = new LinkedList<>();
    if (resources == null) {
      loadAllOntologyResources(ontology);
    }
    if(modules == null){
      getAllModulesData(ontology);
    }
    String ontologyIri = findElementInOntology(elementIri);

    ontologyIri = ontologyIri == null ? elementIri : ontologyIri;

    LOGGER.debug("[FIBO Data Handler] Element found in ontology {}", ontologyIri);
    if (ontologyIri != null) {
      for (FiboModule module : modules) {
        if (trackingThePath(module, ontologyIri, result, elementIri)) {
          LOGGER.debug("[FIBO Data Handler] Location Path {}", Arrays.toString(result.toArray()));
          return result;
        }
      }
    }
    return result;
  }

  /**
   * This method find elements by iri.
   * 
   * @param elementIri ElementIri is used to identify for the given ontology.
   * @return ontology iri where the element is present
   */
  private String findElementInOntology(String elementIri) {

    String ontologyIri = null;
    for (Map.Entry<String, OntologyResources> entry : resources.entrySet()) {
      for (Map.Entry<String, List<PropertyValue>> entryResource : entry.getValue().getResources().entrySet()) {
        if (entryResource.getKey().contains(RESOURCE_INTERNAL_PREFIX)) {
          for (PropertyValue propertyValue : entryResource.getValue()) {
            OwlAnnotationIri annotation = (OwlAnnotationIri) propertyValue;
            if (annotation.getValue().getIri().equals(elementIri)) {
              if (elementIri.contains(entry.getKey())) {
                ontologyIri = entry.getKey();
                break;
              }
            }
          }
        }

      }
    }
    return ontologyIri;
  }

  private Boolean trackingThePath(FiboModule node, String ontologyIri, List<String> track, String elementIri) {

    if (node == null) {
      return false;
    }

    if (node.getIri().equals(elementIri)) {
      track.add(node.getIri());
      return true;
    }
    
    if (node.getIri().equals(ontologyIri)) {
      track.add(node.getIri());
      return true;
    }

    for (FiboModule child : node.getSubModule()) {
      if (trackingThePath(child, ontologyIri, track, elementIri)) {
        track.add(0, node.getIri());
        return true;
      }
    }
    return false;
  }

}
