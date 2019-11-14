package org.edmcouncil.spec.fibo.weasel.ontology;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.edmcouncil.spec.fibo.config.configuration.model.AppConfiguration;
import org.edmcouncil.spec.fibo.config.configuration.model.impl.ViewerCoreConfiguration;
import org.edmcouncil.spec.fibo.config.utils.files.FileSystemManager;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddImport;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.ac.manchester.cs.owl.owlapi.OWLImportsDeclarationImpl;

/**
 *
 * @author Michał Daniel (michal.daniel@makolab.com)
 */
@Component
public class OntologyManager {

  private static final Logger LOG = LoggerFactory.getLogger(OntologyManager.class);

  private OWLOntology ontology;
  private static final String OWL_ONTOLOGY = "https://www.w3.org/2002/07/owl.rdf";

  @Autowired
  private AppConfiguration config;
  @Autowired
  private FileSystemManager fsm;

  @PostConstruct
  public void init() throws IOException {
    ViewerCoreConfiguration viewerCoreConfiguration = config.getWeaselConfig();
    try {
      if (viewerCoreConfiguration.isOntologyLocationSet()) {
        if (viewerCoreConfiguration.isOntologyLocationURL()) {
          String ontoURL = viewerCoreConfiguration.getURLOntology();
          this.ontology = loadOntologyFromURL(ontoURL);
        } else {
          String ontoPath = viewerCoreConfiguration.getPathOntology();
          this.ontology = loadOntologyFromFile(ontoPath);
        }
      } else {
        this.ontology = loadOntologyFromFile(null);
      }
    } catch (OWLOntologyCreationException ex) {
      LOG.error("[ERROR]: Error when creating ontology. Exception: {}", ex.getStackTrace(), ex.getMessage());
    }
  }

  @PreDestroy
  public void destroy() {

  }

  /**
   * This method is used to load ontology from file
   *
   * @param ontoPath OntoPath is the access path from which the ontology is being loaded.
   */
  private OWLOntology loadOntologyFromFile(String ontoPath) throws IOException, OWLOntologyCreationException {
    Path pathToOnto = null;
    if (ontoPath == null) {
      pathToOnto = fsm.getDefaultPathToOntologyFile();
    } else {
      pathToOnto = fsm.getPathToOntologyFile(ontoPath);
    }

    LOG.debug("Path to ontology : {}", pathToOnto.toString());
    File inputOntologyFile = pathToOnto.toFile();

    OWLOntologyManager m = OWLManager.createOWLOntologyManager();

    LOG.debug("load ontology from document");
    OWLOntology o = m.loadOntologyFromOntologyDocument(inputOntologyFile);
    
    //OWLImportsDeclaration importDeclaration = m.getOWLDataFactory()
     //   .getOWLImportsDeclaration(IRI.create(OWL_ONTOLOGY));

    //m.applyChange(new AddImport(o, importDeclaration));

    IRI fiboIRI = IRI.create("https://spec.edmcouncil.org/fibo/ontologyAboutFIBOProd/");
    LOG.debug("load import request");
    m.makeLoadImportRequest(new OWLImportsDeclarationImpl(m.getOntologyDocumentIRI(o)));
    LOG.debug("direct imports");
    Stream<OWLOntology> directImports = m.imports(o);
    Set<OWLOntology> ontologiesTmp = directImports.collect(Collectors.toSet());
    ontologiesTmp.addAll(getDefaultOntologies());
    directImports = ontologiesTmp.stream();
    LOG.debug("create ontology");
    o = m.createOntology(fiboIRI, directImports, false);
    return o;

  }

  /**
   * This method is used to load ontology from URL
   *
   * @param ontoURL OntoUrl is the web address from which the ontology is being loaded.
   * @return set of ontology
   */
  private OWLOntology loadOntologyFromURL(String ontoURL) throws IOException, OWLOntologyCreationException {

    LOG.debug("URL to Ontology : {} ", ontoURL);
    HttpGet httpGet = new HttpGet(ontoURL);
    OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
    CloseableHttpClient httpClient = HttpClients.createDefault();
    HttpResponse response = httpClient.execute(httpGet);
    HttpEntity entity = response.getEntity();
    if (entity != null) {
      InputStream inputStream = entity.getContent();
      OWLOntology newOntology = manager.loadOntologyFromOntologyDocument(inputStream);
      OWLImportsDeclaration importDeclaration = manager.getOWLDataFactory()
          .getOWLImportsDeclaration(IRI.create(OWL_ONTOLOGY));

      manager.applyChange(new AddImport(newOntology, importDeclaration));
      IRI fiboIRI = IRI.create(ontoURL);
      OWLImportsDeclaration declaration = new OWLImportsDeclarationImpl(manager.getOntologyDocumentIRI(newOntology));
      manager.makeLoadImportRequest(declaration);
      Stream<OWLOntology> directImports = manager.imports(newOntology);
      Set<OWLOntology> ontologiesTmp = directImports.collect(Collectors.toSet());
      ontologiesTmp.addAll(getDefaultOntologies());
      directImports = ontologiesTmp.stream();
      newOntology = manager.createOntology(fiboIRI, directImports, false);
      return newOntology;
    }
    return null;
  }

  /**
   * This method is used to open all Ontologies from directory
   *
   * @param ontologiesDir OntologiesDir is a loaded ontology file.
   * @param manager Manager loading and acessing ontologies.
   * @return set of ontology.
   */
  private Set<OWLOntology> openOntologiesFromDirectory(File ontologiesDir, OWLOntologyManager manager) throws OWLOntologyCreationException {
    Set<OWLOntology> result = new HashSet<>();
    for (File file : ontologiesDir.listFiles()) {
      LOG.debug("isFile : {}, name: {}", file.isFile(), file.getName());
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

  private Set<OWLOntology> getDefaultOntologies() throws IOException, OWLOntologyCreationException {
    String ontoURL = OWL_ONTOLOGY;

    Set<OWLOntology> defaultOntologies = new HashSet();

    LOG.debug("URL to Ontology : {} ", ontoURL);
    HttpGet httpGet = new HttpGet(ontoURL);
    OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
    CloseableHttpClient httpClient = HttpClients.createDefault();
    HttpResponse response = httpClient.execute(httpGet);
    HttpEntity entity = response.getEntity();
    if (entity != null) {
      InputStream inputStream = entity.getContent();
      OWLOntology newOntology = manager.loadOntologyFromOntologyDocument(inputStream);
      IRI fiboIRI = IRI.create(ontoURL);
      manager.makeLoadImportRequest(new OWLImportsDeclarationImpl(manager.getOntologyDocumentIRI(newOntology)));
      Stream<OWLOntology> directImports = manager.imports(newOntology);
      directImports = directImports.collect(Collectors.toSet()).stream();
      newOntology = manager.createOntology(fiboIRI, directImports, false);
      defaultOntologies.add(newOntology);
    }
    LOG.debug("Loaded Default ontology size = {}", defaultOntologies.size());
    return defaultOntologies;
  }

}
