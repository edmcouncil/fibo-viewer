package org.edmcouncil.spec.fibo.weasel.ontology;

import java.io.File;
import org.edmcouncil.spec.fibo.config.utils.files.FileSystemManager;
import org.edmcouncil.spec.fibo.weasel.model.details.OwlListDetails;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.edmcouncil.spec.fibo.config.configuration.model.AppConfiguration;
import org.edmcouncil.spec.fibo.config.configuration.model.ConfigElement;
import org.edmcouncil.spec.fibo.config.configuration.model.WeaselConfigKeys;
import org.edmcouncil.spec.fibo.config.configuration.model.impl.ConfigGroupsElement;
import org.edmcouncil.spec.fibo.config.configuration.model.impl.WeaselConfiguration;
import org.edmcouncil.spec.fibo.weasel.model.FiboModule;
import org.edmcouncil.spec.fibo.weasel.model.details.OwlGroupedDetails;
import org.edmcouncil.spec.fibo.weasel.model.PropertyValue;
import org.edmcouncil.spec.fibo.weasel.model.details.OwlDetails;
import org.edmcouncil.spec.fibo.weasel.model.property.OwlDetailsProperties;
import org.edmcouncil.spec.fibo.weasel.ontology.data.OwlDataHandler;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.ac.manchester.cs.owl.owlapi.OWLImportsDeclarationImpl;

/**
 * @author Michał Daniel (michal.daniel@makolab.com)
 * @author Patrycja Miazek (patrycja.miazek@makolab.com)
 */
@Component
public class WeaselOntologyManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(WeaselOntologyManager.class);
  private static final String DEFAULT_GROUP_NAME = "other";
  private OWLOntology ontology;

  @Autowired
  private OwlDataHandler dataHandler;
  @Autowired
  private AppConfiguration config;

  @PostConstruct
  public void init() throws IOException {
    WeaselConfiguration weaselConfiguration = (WeaselConfiguration) config.getWeaselConfig();
    try {
      if (weaselConfiguration.isOntologyLocationSet()) {
        if (weaselConfiguration.isOntologyLocationURL()) {
          String ontoURL = weaselConfiguration.getURLOntology();
          loadOntologyFromURL(ontoURL);
        } else {
          String ontoPath = weaselConfiguration.getPathOntology();
          loadOntologyFromFile(ontoPath);
        }
      } else {
        loadOntologyFromFile(null);
      }
    } catch (OWLOntologyCreationException ex) {
      LOGGER.error("[ERROR]: Error when creating ontology. Exception: {}", ex.getStackTrace(), ex.getMessage());
    }
  }

  @PreDestroy
  public void destroy() {

  }

  /**
   * This method is used to load ontology from file
   *
   * @param ontoPath OntoPath is the access path from which the ontology is being loaded.
   * @return Set of ontology.
   */
  private void loadOntologyFromFile(String ontoPath) throws IOException, OWLOntologyCreationException {
    FileSystemManager fsm = new FileSystemManager();
    Path pathToOnto = null;
    if (ontoPath == null) {
      pathToOnto = fsm.getDefaultPathToOntologyFile();
    } else {
      pathToOnto = fsm.getPathToOntologyFile(ontoPath);

    }
    LOGGER.debug("Path to ontology : {}", pathToOnto.toString());
    File inputOntologyFile = pathToOnto.toFile();

    OWLOntologyManager m = OWLManager.createOWLOntologyManager();

    OWLOntology o = m.loadOntologyFromOntologyDocument(inputOntologyFile);

    IRI fiboIRI = IRI.create("https://spec.edmcouncil.org/fibo/ontologyAboutFIBOProd/");

    m.makeLoadImportRequest(new OWLImportsDeclarationImpl(m.getOntologyDocumentIRI(o)));
    Stream<OWLOntology> directImports = m.imports(o);
    o = m.createOntology(fiboIRI, directImports, false);
    ontology = o;

  }

  /**
   * This method is used to load ontology from URL
   *
   * @param ontoURL OntoUrl is the web address from which the ontology is being loaded.
   * @return Set of ontology.
   */
  private Set<OWLOntology> loadOntologyFromURL(String ontoURL) throws IOException, OWLOntologyCreationException {

    LOGGER.debug("URL to Ontology : {} ", ontoURL);
    HttpGet httpGet = new HttpGet(ontoURL);
    OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
    CloseableHttpClient httpClient = HttpClients.createDefault();
    HttpResponse response = httpClient.execute(httpGet);
    Set<OWLOntology> result = new HashSet<>();
    HttpEntity entity = response.getEntity();
    if (entity != null) {
      long len = entity.getContentLength();
      InputStream inputStream = entity.getContent();
      OWLOntology newOntology = manager.loadOntologyFromOntologyDocument(inputStream);
      IRI fiboIRI = IRI.create(ontoURL);
      manager.makeLoadImportRequest(new OWLImportsDeclarationImpl(manager.getOntologyDocumentIRI(newOntology)));
      Stream<OWLOntology> directImports = manager.imports(newOntology);
      newOntology = manager.createOntology(fiboIRI, directImports, false);
      ontology = newOntology;
    }
    return result;
  }

  /**
   * This method is used to open all Ontologies from directory.
   *
   * @param ontologiesDir OntologiesDir is a loaded ontology file.
   * @param manager Manager loading and acessing ontologies.
   * @return Set of ontology.
   */
  private Set<OWLOntology> openOntologiesFromDirectory(File ontologiesDir, OWLOntologyManager manager) throws OWLOntologyCreationException {
    Set<OWLOntology> result = new HashSet<>();
    for (File file : ontologiesDir.listFiles()) {
      LOGGER.debug("isFile : {}, name: {}", file.isFile(), file.getName());
      if (file.isFile()) {
        if (getFileExtension(file).equalsIgnoreCase("rdf") && !file.getName().contains("Metadata")) {

          OWLOntology newOntology = manager.loadOntologyFromOntologyDocument(file);
          result.add(newOntology);
        }
      } else if (file.isDirectory()) {
        Set<OWLOntology> tmp = openOntologiesFromDirectory(file, manager);
        result.addAll(tmp);
      }
    }
    return result;
  }

  private static String getFileExtension(File file) {
    String fileName = file.getName();
    if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
      return fileName.substring(fileName.lastIndexOf(".") + 1);
    } else {
      return "";
    }
  }

  public OWLOntology getOntology() {
    return ontology;
  }

  /**
   * This method collects Details by IRI of the given class.
   *
   * @param <T>
   * @param iriString IRI is used to identify for the given ontology.
   * @return Details in the appropriate data structure.
   */
  public <T extends OwlDetails> T getDetailsByIri(String iriString) {
    IRI iri = IRI.create(iriString);
    OwlListDetails result = null;
    //FIBO: if '/' is at the end of the URL, we extract the ontolog metadata
    if (iriString.endsWith("/")) {
      LOGGER.debug("Handle ontology metadata. IRI: {}", iriString);
      OwlListDetails wd = dataHandler.handleOntologyMetadata(iri, ontology);

      result = wd;
    } else {
      if (ontology.containsClassInSignature(iri)) {
        LOGGER.debug("Handle class data.");
        OwlListDetails wd = dataHandler.handleParticularClass(iri, ontology);
        result = wd;
      } else if (ontology.containsDataPropertyInSignature(iri)) {
        LOGGER.info("Handle data property.");
        OwlListDetails wd = dataHandler.handleParticularDataProperty(iri, ontology);
        result = wd;
      } else if (ontology.containsObjectPropertyInSignature(iri)) {
        LOGGER.info("Handle object property.");
        OwlListDetails wd = dataHandler.handleParticularObjectProperty(iri, ontology);
        result = wd;
      } else if (ontology.containsIndividualInSignature(iri)) {
        LOGGER.info("Handle individual data.");
        OwlListDetails wd = dataHandler.handleParticularIndividual(iri, ontology);
        result = wd;
      }
    }

    WeaselConfiguration weaselConfig = (WeaselConfiguration) config.getWeaselConfig();
    if (weaselConfig.hasRenamedGroups()) {
      OwlDetailsProperties<PropertyValue> prop = new OwlDetailsProperties<>();
      for (Map.Entry<String, List<PropertyValue>> entry : result.getProperties().entrySet()) {
        String key = entry.getKey();
        String newName = weaselConfig.getNewName(key);
        newName = newName == null ? key : newName;
        for (PropertyValue propertyValue : entry.getValue()) {
          prop.addProperty(newName, propertyValue);
        }
      }
      result.setProperties(prop);
    }
    result.setIri(iriString);

    //Path to element in modules
    List<String> elementLocation = dataHandler.getElementLocationInModules(iriString, ontology);
    result.setLocationInModules(elementLocation);

    if (!config.getWeaselConfig().isEmpty()) {
      WeaselConfiguration cfg = (WeaselConfiguration) config.getWeaselConfig();
      if (cfg.isGrouped()) {
        OwlGroupedDetails newResult = groupDetails(result, cfg);
        return (T) newResult;
      } else {
        sortResults(result);
      }
    }
    return (T) result;
  }

  /**
   * This method group details.
   *
   * @param owlDetails Details by IRI of the given class.
   * @param cfg
   * @return Grouped details.
   */
  private OwlGroupedDetails groupDetails(OwlListDetails owlDetails, WeaselConfiguration cfg) {
    OwlGroupedDetails newResult = null;
    OwlGroupedDetails groupedDetails = new OwlGroupedDetails();
    Set<ConfigElement> groups = cfg.getConfiguration().get(WeaselConfigKeys.GROUPS);

    for (Map.Entry<String, List<PropertyValue>> entry : owlDetails.getProperties().entrySet()) {
      String propertyKey = entry.getKey();
      String propertyName = null;
      if (cfg.hasRenamedGroups()) {
        propertyName = cfg.getOldName(propertyKey);
        propertyName = propertyName == null ? propertyKey : propertyName;
      }
      String groupName = null;
      groupName = getGroupName(groups, propertyName);
      groupName = groupName == null ? DEFAULT_GROUP_NAME : groupName;
      for (PropertyValue property : entry.getValue()) {
        groupedDetails.addProperty(groupName, propertyKey, property);
      }
    }
    groupedDetails.setTaxonomy(owlDetails.getTaxonomy());
    groupedDetails.setLabel(owlDetails.getLabel());
    groupedDetails.setIri(owlDetails.getIri());
    groupedDetails.setLocationInModules(owlDetails.getLocationInModules());
    groupedDetails.sortProperties(groups, cfg);

    newResult = groupedDetails;
    return newResult;
  }

  /**
   * This method collects group name.
   *
   * @param groups
   * @param propertyKey
   * @return
   */
  private String getGroupName(Set<ConfigElement> groups, String propertyKey) {
    String result = null;
    if (propertyKey == null || propertyKey.isEmpty()) {
      return result;
    }
    for (ConfigElement g : groups) {
      ConfigGroupsElement group = (ConfigGroupsElement) g;
      if (group.getElements() != null && group.getElements().size() > 0) {
        if (group.contains(propertyKey)) {
          return group.getName();
        }
      }
    }
    return result;
  }

  /**
   * This method sort properties.
   *
   * @param result
   */
  private void sortResults(OwlListDetails result) {
    Set set = (Set) config.getWeaselConfig()
        .getConfigVal(WeaselConfigKeys.PRIORITY_LIST);
    if (set == null) {
      return;
    }
    List prioritySortList = new LinkedList();
    result.sortProperties(prioritySortList);
  }

  public List<FiboModule> getAllModulesData() {
    return dataHandler.getAllModulesData(ontology);
  }
}
