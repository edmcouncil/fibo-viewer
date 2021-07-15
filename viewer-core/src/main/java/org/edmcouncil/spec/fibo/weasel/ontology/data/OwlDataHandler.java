package org.edmcouncil.spec.fibo.weasel.ontology.data;

import com.google.common.collect.HashBiMap;
import org.edmcouncil.spec.fibo.weasel.ontology.data.visitor.ContainsVisitors;
import org.edmcouncil.spec.fibo.weasel.ontology.data.handler.fibo.FiboDataHandler;
import org.edmcouncil.spec.fibo.weasel.ontology.data.handler.AnnotationsDataHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import org.edmcouncil.spec.fibo.weasel.model.details.OwlListDetails;
import org.edmcouncil.spec.fibo.weasel.model.property.OwlDetailsProperties;
import org.edmcouncil.spec.fibo.weasel.model.WeaselOwlType;
import org.semanticweb.owlapi.io.OWLObjectRenderer;
import org.semanticweb.owlapi.manchestersyntax.renderer.ManchesterOWLSyntaxOWLObjectRendererImpl;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAxiom;
import org.semanticweb.owlapi.model.OWLIndividualAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.edmcouncil.spec.fibo.config.configuration.model.AppConfiguration;
import org.edmcouncil.spec.fibo.config.configuration.model.PairImpl;
import org.edmcouncil.spec.fibo.config.configuration.model.impl.ViewerCoreConfiguration;
import org.edmcouncil.spec.fibo.weasel.model.module.FiboModule;
import org.edmcouncil.spec.fibo.weasel.model.PropertyValue;
import org.edmcouncil.spec.fibo.weasel.model.graph.OntologyGraph;
import org.edmcouncil.spec.fibo.weasel.model.graph.viewer.ViewerGraphFactory;
import org.edmcouncil.spec.fibo.weasel.model.graph.vis.VisGraph;
import org.edmcouncil.spec.fibo.weasel.model.property.OwlAxiomPropertyEntity;
import org.edmcouncil.spec.fibo.weasel.model.property.OwlAxiomPropertyValue;
import org.edmcouncil.spec.fibo.weasel.model.property.OwlDirectedSubClassesProperty;
import org.edmcouncil.spec.fibo.weasel.model.taxonomy.OwlTaxonomyElementImpl;
import org.edmcouncil.spec.fibo.weasel.model.taxonomy.OwlTaxonomyImpl;
import org.edmcouncil.spec.fibo.weasel.model.taxonomy.OwlTaxonomyValue;
import org.edmcouncil.spec.fibo.weasel.ontology.data.handler.IndividualDataHandler;
import org.edmcouncil.spec.fibo.weasel.ontology.data.handler.fibo.OntoFiboMaturityLevel;
import org.edmcouncil.spec.fibo.weasel.ontology.data.label.provider.LabelProvider;
import org.edmcouncil.spec.fibo.weasel.ontology.factory.ViewerIdentifierFactory;
import org.edmcouncil.spec.fibo.weasel.ontology.scope.ScopeIriOntology;
import org.edmcouncil.spec.fibo.weasel.utils.OwlUtils;
import org.edmcouncil.spec.fibo.weasel.utils.StringUtils;
import org.edmcouncil.spec.fibo.weasel.utils.UrlChecker;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.ClassExpressionType;
import org.semanticweb.owlapi.model.OWLAnnotationAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.model.OWLSubAnnotationPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.springframework.stereotype.Component;
import org.w3c.dom.Entity;
import uk.ac.manchester.cs.owl.owlapi.OWLSubAnnotationPropertyOfAxiomImpl;

/**
 * @author Michał Daniel (michal.daniel@makolab.com)
 * @author Patrycja Miazek (patrycja.miazek@makolab.com)
 */
@Component
public class OwlDataHandler {

  private static final Logger LOG = LoggerFactory.getLogger(OwlDataHandler.class);

  private final OWLObjectRenderer rendering = new ManchesterOWLSyntaxOWLObjectRendererImpl();
  @Autowired
  private FiboDataHandler fiboDataHandler;
  @Autowired
  private AnnotationsDataHandler annotationsDataHandler;
  @Autowired
  private IndividualDataHandler individualDataHandler;
  @Autowired
  private LabelProvider labelExtractor;
  @Autowired
  private RestrictionGraphDataHandler graphDataHandler;
  @Autowired
  private OwlUtils owlUtils;
  @Autowired
  private AppConfiguration config;
  @Autowired
  private ScopeIriOntology scopeIriOntology;
  @Autowired
  private RestrictionGraphDataHandler restrictionGraphDataHandler;
  @Autowired
  private ContainsVisitors containsVisitors;

  private final Set<String> unwantedEndOfLeafIri = new HashSet<>();

  private final String subClassOfIriString = ViewerIdentifierFactory
      .createId(ViewerIdentifierFactory.Type.axiom, AxiomType.SUBCLASS_OF.getName());
  private final String subObjectPropertyOfIriString = ViewerIdentifierFactory
      .createId(ViewerIdentifierFactory.Type.axiom, AxiomType.SUB_OBJECT_PROPERTY.getName());

  private final String subAnnotationPropertyOfIriString = ViewerIdentifierFactory
      .createId(ViewerIdentifierFactory.Type.axiom, AxiomType.SUB_ANNOTATION_PROPERTY_OF.getName());

  {
    /*static block*/
    unwantedEndOfLeafIri.add("http://www.w3.org/2002/07/owl#Thing");
    unwantedEndOfLeafIri.add("http://www.w3.org/2002/07/owl#topObjectProperty");
    unwantedEndOfLeafIri.add("http://www.w3.org/2002/07/owl#topDataProperty");
  }

  public OwlListDetails handleParticularClass(IRI iri, OWLOntology ontology) {
    OwlListDetails resultDetails = new OwlListDetails();

    Iterator<OWLClass> classesIterator = ontology.classesInSignature().iterator();
    //'Nothing' has all restrictions, we don't want to display that.
    boolean skipNothingData = !iri.equals(IRI.create("http://www.w3.org/2002/07/owl#Nothing"));
    while (classesIterator.hasNext()) {
      OWLClass clazz = classesIterator.next();

      if (clazz.getIRI().equals(iri)) {
        LOG.debug("[Data Handler] Find OWL class wih IRI: {}", iri.toString());

        String label = labelExtractor.getLabelOrDefaultFragment(clazz);

        resultDetails.setLabel(label);

        OwlDetailsProperties<PropertyValue> axioms = handleAxioms(clazz, ontology);
        List<PropertyValue> subclasses = getSubclasses(axioms);

        //List<PropertyValue> taxElements = extracttTaxonomyElements(subclasses);
        List<PropertyValue> subclasses2 = getSubclasses(clazz, ontology);
        List<PropertyValue> taxElements2 = extracttTaxonomyElements(subclasses2);

        OwlDetailsProperties<PropertyValue> directSubclasses = handleDirectSubclasses(ontology, clazz);
        OwlDetailsProperties<PropertyValue> individuals = handleInstances(ontology, clazz);
        OwlDetailsProperties<PropertyValue> usage = extractUsageForClasses(clazz, ontology);

        OwlDetailsProperties<PropertyValue> inheritedAxioms = new OwlDetailsProperties<>();
        OntologyGraph vg = new OntologyGraph();
        if (skipNothingData) {
          inheritedAxioms = handleInheritedAxioms(ontology, clazz);
          vg = graphDataHandler.handleGraph(clazz, ontology);
        }

        subclasses = filterSubclasses(subclasses);

        OwlTaxonomyImpl tax = extractTaxonomy(taxElements2, iri, ontology, WeaselOwlType.AXIOM_CLASS);
        tax.sort();

        OwlDetailsProperties<PropertyValue> annotations
            = handleAnnotations(clazz.getIRI(), ontology, resultDetails);
        
        setResultValues(resultDetails, tax, axioms, annotations, directSubclasses, individuals, inheritedAxioms, usage, vg, subclasses);
      }
    }
    return resultDetails;
  }

  private List<PropertyValue> filterSubclasses(List<PropertyValue> subclasses) {
    List<PropertyValue> result = subclasses.stream().filter((pv) -> (!pv.getType().equals(WeaselOwlType.TAXONOMY))).collect(Collectors.toList());
    return result;
  }

  private List<PropertyValue> getSubclasses(OwlDetailsProperties<PropertyValue> axioms) {
    LOG.debug("Axiom of subclasses {}", axioms.toString());
    List<PropertyValue> subclasses = axioms
        .getProperties()
        .getOrDefault(subClassOfIriString, new ArrayList<>(0));
    return subclasses;
  }

  private List<PropertyValue> getSubclasses(OWLClass clazz, OWLOntology ontology) {
    LOG.debug("getSubclasses -> clazz {}", clazz.toString());
    List<OWLClassExpression> subClasses = EntitySearcher.getSuperClasses(clazz, ontology).collect(Collectors.toList());

    List<PropertyValue> result = new LinkedList<>();
    for (OWLClassExpression subClass : subClasses) {
      LOG.debug("\tgetSubclasses -> subClass {}", subClass);
      Optional<OWLEntity> e = subClass.signature().findFirst();
      LOG.debug("\tgetSubclasses -> enity iri {}", e.get().getIRI());

      if (subClass.getClassExpressionType() != ClassExpressionType.OWL_CLASS) {
        continue;
      }

      IRI entityIri = e.get().getIRI();
      if (entityIri.equals(clazz.getIRI())) {
        continue;
      }
      OwlAxiomPropertyValue opv = new OwlAxiomPropertyValue();
      opv.setType(WeaselOwlType.TAXONOMY);
      String key = StringUtils.getFragment(entityIri);
      opv.setValue(key);

      OwlAxiomPropertyEntity propertyEntity = new OwlAxiomPropertyEntity();
      propertyEntity.setIri(entityIri.toString());
      propertyEntity.setLabel(labelExtractor.getLabelOrDefaultFragment(entityIri));
      opv.addEntityValues(key, propertyEntity);
      result.add(opv);
    }
    return result;
  }

  private List<PropertyValue> extracttTaxonomyElements(List<PropertyValue> subclasses) {
    List<PropertyValue> taxElements = subclasses
        .stream()
        .filter((pv) -> (pv.getType().equals(WeaselOwlType.TAXONOMY)))
        .collect(Collectors.toList());
    LOG.debug("TaxElements: {}", taxElements.toString());
    return taxElements;
  }

  private void setResultValues(OwlListDetails resultDetails,
      OwlTaxonomyImpl tax,
      OwlDetailsProperties<PropertyValue> axioms,
      OwlDetailsProperties<PropertyValue> annotations,
      OwlDetailsProperties<PropertyValue> directSubclasses,
      OwlDetailsProperties<PropertyValue> individuals,
      OwlDetailsProperties<PropertyValue> inheritedAxioms,
      OwlDetailsProperties<PropertyValue> usage,
      OntologyGraph vg,
      List<PropertyValue> subclasses) {
    axioms.getProperties().put(subClassOfIriString, subclasses);

    resultDetails.setTaxonomy(tax);
    resultDetails.addAllProperties(axioms);
    resultDetails.addAllProperties(annotations);
    resultDetails.addAllProperties(directSubclasses);
    resultDetails.addAllProperties(individuals);
    resultDetails.addAllProperties(inheritedAxioms);
    resultDetails.addAllProperties(usage);
    if (vg.isEmpty()) {
      resultDetails.setGraph(null);
    } else {
      VisGraph vgj = new ViewerGraphFactory().convertToVisGraph(vg);
      resultDetails.setGraph(vgj);
    }
  }

  public OwlListDetails handleParticularIndividual(IRI iri, OWLOntology ontology) {
    OwlListDetails resultDetails = new OwlListDetails();
    Iterator<OWLNamedIndividual> individualIterator = ontology.individualsInSignature().iterator();

    while (individualIterator.hasNext()) {
      OWLNamedIndividual individual = individualIterator.next();

      if (individual.getIRI().equals(iri)) {
        LOG.debug("[Data Handler] Find owl named individual wih iri: {}", iri.toString());

        resultDetails.setLabel(labelExtractor.getLabelOrDefaultFragment(individual.getIRI()));

        OwlDetailsProperties<PropertyValue> axioms = handleAxioms(individual, ontology);

        OwlDetailsProperties<PropertyValue> annotations
            = handleAnnotations(individual.getIRI(), ontology, resultDetails);
        OntologyGraph vg = new OntologyGraph();
        vg = graphDataHandler.handleGraph(individual, ontology);
        if (vg.isEmpty()) {
          resultDetails.setGraph(null);
        } else {
          VisGraph vgj = new ViewerGraphFactory().convertToVisGraph(vg);
          resultDetails.setGraph(vgj);
        }
        resultDetails.addAllProperties(axioms);
        resultDetails.addAllProperties(annotations);
      }
    }
    return resultDetails;
  }

  private OwlDetailsProperties<PropertyValue> handleAnnotations(IRI iri, OWLOntology ontology, OwlListDetails details) {
    return annotationsDataHandler.handleAnnotations(iri, ontology, details);
  }

  private OwlDetailsProperties<PropertyValue> handleAxioms(
      OWLNamedIndividual obj,
      OWLOntology ontology) {

    Iterator<OWLIndividualAxiom> axiomsIterator = ontology.axioms(obj).iterator();
    return handleAxioms(axiomsIterator, obj.getIRI());
  }

  private OwlDetailsProperties<PropertyValue> handleAxioms(
      OWLObjectProperty obj,
      OWLOntology ontology) {

    Iterator<OWLObjectPropertyAxiom> axiomsIterator = ontology.axioms(obj).iterator();
    return handleAxioms(axiomsIterator, obj.getIRI());
  }

  private OwlDetailsProperties<PropertyValue> handleAxioms(
      OWLDataProperty obj,
      OWLOntology ontology) {

    Iterator<OWLDataPropertyAxiom> axiomsIterator = ontology.axioms(obj).iterator();
    return handleAxioms(axiomsIterator, obj.getIRI());
  }

  private OwlDetailsProperties<PropertyValue> handleAxioms(
      OWLClass obj,
      OWLOntology ontology) {

    Iterator<OWLClassAxiom> axiomsIterator = ontology.axioms(obj).iterator();
    return handleAxioms(axiomsIterator, obj.getIRI());
  }

  private OwlDetailsProperties<PropertyValue> handleAxioms(
      OWLAnnotationProperty obj,
      OWLOntology ontology) {
    Iterator<OWLAnnotationAxiom> axiomsIterator = ontology.axioms(obj).iterator();
    return handleAxioms(axiomsIterator, obj.getIRI());
  }

  private OwlTaxonomyImpl extractTaxonomy(List<PropertyValue> subElements, IRI objIri, OWLOntology ontology, WeaselOwlType type) {
    OwlTaxonomyImpl taxonomy = new OwlTaxonomyImpl();
    if (subElements.size() > 0) {

      for (PropertyValue property : subElements) {
        if (property.getType().equals(WeaselOwlType.TAXONOMY)) {
          OwlAxiomPropertyValue axiomProperty = (OwlAxiomPropertyValue) property;
          LOG.debug("Axiom Property {}", axiomProperty.toString());
          IRI sci = extractSubElementIri(axiomProperty, objIri);

          LOG.debug("extractSubElementIri: {}", extractSubElementIri(axiomProperty, objIri));
          OWLEntity entity = createEntity(ontology, sci, type);

          LOG.debug("createEntity: {}", createEntity(ontology, sci, type));

          LOG.trace("\t{} Sub Element Of {}", StringUtils.getFragment(objIri),
              StringUtils.getFragment(entity.getIRI()));
          List<PropertyValue> subTax = getSuperElements(entity, ontology, type);

          OwlTaxonomyImpl subCLassTax = extractTaxonomy(subTax, entity.getIRI(), ontology, type);

          String label = labelExtractor.getLabelOrDefaultFragment(objIri);

          OwlTaxonomyElementImpl taxEl = new OwlTaxonomyElementImpl(objIri.getIRIString(), label);

          if (subCLassTax.getValue().size() > 0) {
            taxonomy.addTaxonomy(subCLassTax, taxEl);
          } else {
            List<OwlTaxonomyElementImpl> currentTax = new LinkedList<>();
            currentTax.add(taxEl);
            taxonomy.addTaxonomy(currentTax);
          }
        }
      }

    } else {

      LOG.trace("\t\tEnd leaf on {}", StringUtils.getFragment(objIri));
      String label = labelExtractor.getLabelOrDefaultFragment(objIri);

      OwlTaxonomyElementImpl taxEl = new OwlTaxonomyElementImpl(objIri.getIRIString(), label);
      List<OwlTaxonomyElementImpl> currentTax = new LinkedList<>();

      if (!unwantedEndOfLeafIri.contains(objIri.toString())) {

        switch (type) {
          case AXIOM_CLASS:
            label = labelExtractor.getLabelOrDefaultFragment(IRI.create("http://www.w3.org/2002/07/owl#Thing"));
            currentTax.add(new OwlTaxonomyElementImpl("http://www.w3.org/2002/07/owl#Thing", label));
            break;
          case AXIOM_OBJECT_PROPERTY:
            label = labelExtractor.getLabelOrDefaultFragment(IRI.create("http://www.w3.org/2002/07/owl#topObjectProperty"));
            currentTax.add(new OwlTaxonomyElementImpl("http://www.w3.org/2002/07/owl#topObjectProperty", label));
            break;
          case AXIOM_DATA_PROPERTY:
            label = labelExtractor.getLabelOrDefaultFragment(IRI.create("http://www.w3.org/2002/07/owl#topDataProperty"));
            currentTax.add(new OwlTaxonomyElementImpl("http://www.w3.org/2002/07/owl#topDataProperty", label));
            break;
          case AXIOM_ANNOTATION_PROPERTY:
            label = labelExtractor.getLabelOrDefaultFragment(IRI.create("http://www.w3.org/2002/07/owl#topAnnotationProperty"));
            currentTax.add(new OwlTaxonomyElementImpl("http://www.w3.org/2002/07/owl#topAnnotationProperty", label));
            break;
          case AXIOM_NAMED_INDIVIDUAL:
            break;
          default:
            label = labelExtractor.getLabelOrDefaultFragment(IRI.create("http://www.w3.org/2002/07/owl#Thing"));
            currentTax.add(new OwlTaxonomyElementImpl("http://www.w3.org/2002/07/owl#Thing", label));
            break;
        }
      }
      currentTax.add(taxEl);

      taxonomy.addTaxonomy(currentTax);
    }

    return taxonomy;
  }

  private OWLEntity createEntity(OWLOntology ontology, IRI sci, WeaselOwlType type) {

    switch (type) {
      case AXIOM_CLASS:
        return ontology.getOWLOntologyManager().getOWLDataFactory().getOWLClass(sci);
      case AXIOM_DATA_PROPERTY:
        return ontology.getOWLOntologyManager().getOWLDataFactory().getOWLDataProperty(sci);
      case AXIOM_OBJECT_PROPERTY:
        return ontology.getOWLOntologyManager().getOWLDataFactory().getOWLObjectProperty(sci);
      case AXIOM_DATATYPE:
        return ontology.getOWLOntologyManager().getOWLDataFactory().getOWLDatatype(sci);
      case AXIOM_ANNOTATION_PROPERTY:
        return ontology.getOWLOntologyManager().getOWLDataFactory().getOWLAnnotationProperty(sci);
    }

    return null;
  }

  private IRI extractSubElementIri(OwlAxiomPropertyValue axiomProperty, IRI objIri) {
    LOG.debug("Axiom Property SubElementIri {}", axiomProperty.toString());
    LOG.debug("extractSubElementIri -> obj {}", objIri);
    for (Map.Entry<String, OwlAxiomPropertyEntity> entry : axiomProperty.getEntityMaping().entrySet()) {
      LOG.debug("Axiom Property entry element {}", entry.toString());
      if (!entry.getValue().getIri().equals(objIri.getIRIString())) {
        return IRI.create(entry.getValue().getIri());
      }
    }
    return null;
  }

  private <T extends OWLAxiom> OwlDetailsProperties<PropertyValue> handleAxioms(
      Iterator<T> axiomsIterator,
      IRI elementIri) {
    OwlDetailsProperties<PropertyValue> result = new OwlDetailsProperties<>();
    String iriFragment = elementIri.getFragment();
    String splitFragment = StringUtils.getFragment(elementIri);
    Boolean fixRenderedIri = !iriFragment.equals(splitFragment);

    Set<String> ignoredToDisplay = config.getViewerCoreConfig().getIgnoredElements();
    int start = 0;
    while (axiomsIterator.hasNext()) {

      T axiom = axiomsIterator.next();

      LOG.debug("NotRenderedAxioms: {}", axiom.toString());
      String key = axiom.getAxiomType().getName();
      key = ViewerIdentifierFactory.createId(ViewerIdentifierFactory.Type.axiom, key);

      OwlAxiomPropertyValue opv = prepareAxiomPropertyValue(axiom, iriFragment, splitFragment, fixRenderedIri, ignoredToDisplay, key, start, true);


      if (opv == null) {
        continue;
      }
      start = opv.getLastId();

      result.addProperty(key, opv);
    }
    result.sortPropertiesInAlphabeticalOrder();
    return result;
  }

  private <T extends OWLAxiom> OwlAxiomPropertyValue prepareAxiomPropertyValue(
      T axiom,
      String iriFragment,
      String splitFragment,
      Boolean fixRenderedIri,
      Set<String> ignoredToDisplay,
      String key
  ) {
    return prepareAxiomPropertyValue(axiom, iriFragment, splitFragment, fixRenderedIri, ignoredToDisplay, key, 0, true);
  }

  private <T extends OWLAxiom> OwlAxiomPropertyValue prepareAxiomPropertyValue(
      T axiom,
      String iriFragment,
      String splitFragment,
      Boolean fixRenderedIri,
      Set<String> ignoredToDisplay,
      String key,
      int startCountingArgs,

      boolean bypassClass

  ) {

    String value = rendering.render(axiom);

    if (bypassClass == true) {
      value = fixRenderedValue(value, iriFragment, splitFragment, fixRenderedIri);
    }

    //String key = axiom.getAxiomType().getName();
    // key = ViewerIdentifierFactory.createId(ViewerIdentifierFactory.Type.axiom, key);
    if (ignoredToDisplay.contains(key)) {
      return null;
    }
    OwlAxiomPropertyValue opv = new OwlAxiomPropertyValue();
    opv.setValue(value);

    opv.setType(WeaselOwlType.AXIOM);
    LOG.debug("[Data Handler] Find Axiom \"{}\" with type \"{}\"", value, key);
    Boolean isRestriction = owlUtils.isRestriction(axiom);

    if (!isRestriction && axiom.getAxiomType().equals(AxiomType.SUBCLASS_OF)) {
      LOG.trace("[Data Handler] Find non restriction SubClassOf");
      opv.setType(WeaselOwlType.TAXONOMY);
    }

    processingAxioms(axiom, fixRenderedIri, iriFragment, splitFragment, opv, value, startCountingArgs);

    return opv;
  }

  //TODO: refactor this method
  private <T extends OWLAxiom> void processingAxioms(
      T axiom,
      Boolean fixRenderedIri,
      String iriFragment,
      String splitFragment,
      OwlAxiomPropertyValue opv,
      String renderedVal,
      int startCountingArgs) {

    String argPattern = "/arg%s/";
    String[] splited = renderedVal.split(" ");
    String openingParenthesis = "(";
    String closingParenthesis = ")";
    String comma = ",";
    Iterator<OWLEntity> iterator = axiom.signature().iterator();
    ViewerCoreConfiguration cfg = config.getViewerCoreConfig();

    LOG.trace("Rendered Val: {}", renderedVal);

    while (iterator.hasNext()) {
      OWLEntity next = iterator.next();
      String eSignature = rendering.render(next);
      eSignature = fixRenderedIri && iriFragment.equals(eSignature) ? splitFragment : eSignature;
      String key = null;
      LOG.debug("Processing Item: {}", next);
      LOG.trace("OWL Entity splited: {}", Arrays.asList(splited));

      for (int i = startCountingArgs; i < startCountingArgs + splited.length; i++) {

        int fixedIValue = i - startCountingArgs;

        String string = splited[fixedIValue].trim();
        LOG.trace("Splited string i: '{}', str: '{}'", fixedIValue, string);
        //more than 1 because when it's 1, it's a number
        Boolean hasOpeningParenthesis = string.length() > 1 ? string.contains("(") : false;
        int countOpeningParenthesis = StringUtils.countLetter(string, '(');
        Boolean hasClosingParenthesis = string.length() > 1 ? string.endsWith(closingParenthesis) : false;
        int countClosingParenthesis = StringUtils.countLetter(string, ')');
        Boolean hasComma = string.length() > 1 ? string.contains(",") : false;
        int countComma = StringUtils.countLetter(string, ',');
        if (hasOpeningParenthesis) {
          String newString = string.substring(countOpeningParenthesis);
          LOG.trace("Old string: '{}', new string '{}', count opening parenthesis '{}'", string,
              newString,
              countOpeningParenthesis);
          string = newString;
        }
        if (hasClosingParenthesis) {
          String newString = string.substring(0, string.length() - countClosingParenthesis);
          LOG.trace("Old string: '{}', new string '{}', count closing parenthesis '{}'", string,
              newString,
              countClosingParenthesis);

          string = newString;
        }
        if (hasComma) {
          String newString = string.substring(0, string.length() - countComma);
          LOG.trace("Old string: '{}', new string '{}', count comma '{}'", string,
              newString,
              countComma);

          string = newString;
        }
        if (string.equals(eSignature)) {
          LOG.trace("Find match for processing item {}", string);
          String generatedKey = String.format(argPattern, i);
          key = generatedKey;
          String textToReplace = generatedKey;
          if (hasOpeningParenthesis) {
            String prefix = String.join("", Collections.nCopies(countOpeningParenthesis, openingParenthesis));
            textToReplace = prefix + textToReplace;
          }
          if (hasClosingParenthesis) {
            String postfix = String.join("", Collections.nCopies(countClosingParenthesis, closingParenthesis));
            textToReplace = textToReplace + postfix;
          }
          if (hasComma) {
            String postfix = String.join("", Collections.nCopies(countComma, comma));
            textToReplace = textToReplace + postfix;
          }
          LOG.trace("Prepared text: {} for: {}", textToReplace, splited[fixedIValue]);
          splited[fixedIValue] = textToReplace;

          String eIri = next.getIRI().toString();

          if (scopeIriOntology.scopeIri(eIri)) {
            parseToIri(eIri, opv, key, splited, fixedIValue, splited[fixedIValue]);
            //break;
          } else {
            parseUrlAxiom(eIri, splited, fixedIValue, hasOpeningParenthesis, countOpeningParenthesis, hasClosingParenthesis, countClosingParenthesis, hasComma, countComma);
            //break;
          }

        }
        opv.setLastId(i);
      }
    }

    checkAndParseUriInLiteral(splited, argPattern, opv);

    String value = String.join(" ", splited).trim();

    LOG.debug("[Data Handler] Prepared value for axiom : {}", value);
    opv.setValue(value);
    String fullRenderedString = parseRenderedString(opv);
    opv.setFullRenderedString(fullRenderedString);
    LOG.debug("Full Rendered String: {}", fullRenderedString);
  }

  private void checkAndParseUriInLiteral(String[] splited, String argPattern, OwlAxiomPropertyValue opv) {
    ViewerCoreConfiguration cfg = config.getViewerCoreConfig();
    for (int j = 0; j < splited.length; j++) {
      String str = splited[j].trim();
      if (str.startsWith("<") && str.endsWith(">")) {
        int length = str.length();
        String probablyUrl = str.substring(1, length - 1);
        if (UrlChecker.isUrl(probablyUrl)) {
          String generatedKey = String.format(argPattern, j);
          String key = generatedKey;

          if (scopeIriOntology.scopeIri(probablyUrl)) {
            parseToIri(probablyUrl, opv, key, splited, j, generatedKey);
          } else {
            parseUrl(probablyUrl, splited, j);
          }
        }
      }
    }
  }

  private void parseUrl(String probablyUrl, String[] splited, int j) {
    String label = labelExtractor.getLabelOrDefaultFragment(IRI.create(probablyUrl));
    splited[j] = label;
  }

  private void parseToIri(String probablyUrl, OwlAxiomPropertyValue opv, String key, String[] splited, int j, String generatedKey) {
    OwlAxiomPropertyEntity axiomPropertyEntity = new OwlAxiomPropertyEntity();
    axiomPropertyEntity.setIri(probablyUrl);
    LOG.debug("Probably URL {}", probablyUrl);
    String label = labelExtractor.getLabelOrDefaultFragment(IRI.create(probablyUrl));
    axiomPropertyEntity.setLabel(label);
    opv.addEntityValues(key, axiomPropertyEntity);

    splited[j] = generatedKey;
  }

  private String fixRenderedValue(String value, String iriFragment, String splitFragment, Boolean fixRenderedIri) {
    String[] split = value.split(" ");
    LOG.debug("Split fixRenderedValue: {}", Arrays.asList(split));
    if (split[1].equals("SubClassOf")) {
      split[0] = "";
      split[1] = "";
    }
    if (fixRenderedIri) {
      int iriFragmentIndex = -1;
      for (int i = 0; i < split.length; i++) {
        String sString = split[i];
        if (iriFragment.equals(sString)) {
          iriFragmentIndex = i;
          break;
        }
      }
      if (iriFragmentIndex != -1) {
        split[iriFragmentIndex] = splitFragment;
      }
    }
    value = String.join(" ", split);
    return value;
  }

  public OwlListDetails handleParticularDataProperty(IRI iri, OWLOntology ontology) {
    OwlListDetails resultDetails = new OwlListDetails();
    Iterator<OWLDataProperty> dataPropertyIt = ontology.dataPropertiesInSignature().iterator();

    while (dataPropertyIt.hasNext()) {
      OWLDataProperty dataProperty = dataPropertyIt.next();

      if (dataProperty.getIRI().equals(iri)) {
        LOG.debug("[Data Handler] Find owl data property wih iri: {}", iri.toString());

        resultDetails.setLabel(labelExtractor.getLabelOrDefaultFragment(dataProperty.getIRI()));

        OwlDetailsProperties<PropertyValue> axioms = handleAxioms(dataProperty, ontology);
        OwlDetailsProperties<PropertyValue> directSubDataProperty = handleDirectSubDataProperty(ontology, dataProperty);

        List<PropertyValue> subElements = getSuperElements(dataProperty, ontology, WeaselOwlType.AXIOM_DATA_PROPERTY);
        OwlTaxonomyImpl taxonomy = extractTaxonomy(subElements, iri, ontology, WeaselOwlType.AXIOM_DATA_PROPERTY);
        taxonomy.sort();

        OwlDetailsProperties<PropertyValue> annotations
            = handleAnnotations(dataProperty.getIRI(), ontology, resultDetails);

        resultDetails.addAllProperties(axioms);
        resultDetails.addAllProperties(annotations);
        resultDetails.addAllProperties(directSubDataProperty);
        resultDetails.setTaxonomy(taxonomy);
      }
    }
    return resultDetails;

  }

  public OwlListDetails handleParticularObjectProperty(IRI iri, OWLOntology ontology) {
    OwlListDetails resultDetails = new OwlListDetails();
    Iterator<OWLObjectProperty> dataPropertyIt = ontology.objectPropertiesInSignature().iterator();

    while (dataPropertyIt.hasNext()) {
      OWLObjectProperty dataProperty = dataPropertyIt.next();

      if (dataProperty.getIRI().equals(iri)) {
        LOG.debug("[Data Handler] Find owl object property wih iri: {}", iri.toString());

        resultDetails.setLabel(labelExtractor.getLabelOrDefaultFragment(dataProperty.getIRI()));

        OwlDetailsProperties<PropertyValue> axioms = handleAxioms(dataProperty, ontology);
        OwlDetailsProperties<PropertyValue> directSubObjectProperty = handleDirectSubObjectProperty(ontology, dataProperty);

        List<PropertyValue> subElements = getSuperElements(dataProperty, ontology, WeaselOwlType.AXIOM_OBJECT_PROPERTY);
        OwlTaxonomyImpl taxonomy = extractTaxonomy(subElements, iri, ontology, WeaselOwlType.AXIOM_OBJECT_PROPERTY);
        taxonomy.sort();
        subElements = subElements.stream().filter((pv) -> (!pv.getType().equals(WeaselOwlType.TAXONOMY))).collect(Collectors.toList());
        OwlDetailsProperties<PropertyValue> annotations
            = handleAnnotations(dataProperty.getIRI(), ontology, resultDetails);

        axioms.getProperties().put(subObjectPropertyOfIriString, subElements);
        resultDetails.addAllProperties(axioms);
        resultDetails.addAllProperties(annotations);
        resultDetails.addAllProperties(directSubObjectProperty);
        resultDetails.setTaxonomy(taxonomy);
      }
    }
    return resultDetails;
  }

  private List<PropertyValue> getSuperElements(OWLEntity entity, OWLOntology ontology, WeaselOwlType type) {
    Stream<OWLProperty> propertyStream = null;
    OWLProperty prop = null;
    switch (type) {
      case AXIOM_CLASS:
        return getSubclasses(entity.asOWLClass(), ontology);
      case AXIOM_DATA_PROPERTY:
        prop = entity.asOWLDataProperty();
        propertyStream = EntitySearcher.getSuperProperties(prop, ontology);
        break;
      case AXIOM_OBJECT_PROPERTY:
        prop = entity.asOWLObjectProperty();
        propertyStream = EntitySearcher.getSuperProperties(prop, ontology);
        break;
      case AXIOM_ANNOTATION_PROPERTY:
        prop = entity.asOWLAnnotationProperty();
        propertyStream = EntitySearcher.getSuperProperties(prop, ontology);
        break;

    }
    List<PropertyValue> resultProperties = new LinkedList<>();

    for (OWLProperty owlProperty : propertyStream.collect(Collectors.toSet())) {
      LOG.trace("{} Sub Property Of {}", StringUtils.getFragment(entity.getIRI()),
          StringUtils.getFragment(owlProperty.getIRI()));
      IRI subClazzIri = entity.getIRI();
      IRI superClazzIri = owlProperty.getIRI();

      OwlAxiomPropertyValue pv = new OwlAxiomPropertyValue();
      OwlAxiomPropertyEntity entitySubClass = new OwlAxiomPropertyEntity();
      OwlAxiomPropertyEntity entitySuperClass = new OwlAxiomPropertyEntity();
      entitySubClass.setIri(subClazzIri.getIRIString());
      entitySubClass.setLabel(labelExtractor.getLabelOrDefaultFragment(subClazzIri));
      entitySuperClass.setIri(superClazzIri.getIRIString());
      entitySuperClass.setLabel(labelExtractor.getLabelOrDefaultFragment(superClazzIri));

      pv.setType(WeaselOwlType.TAXONOMY);
      pv.addEntityValues(labelExtractor.getLabelOrDefaultFragment(subClazzIri), entitySubClass);
      pv.addEntityValues(labelExtractor.getLabelOrDefaultFragment(superClazzIri), entitySuperClass);
      resultProperties.add(pv);

    }

    return resultProperties;
  }

  /**
   * This method is used to display SubClassOf
   *
   * @param ontology This is a loaded ontology.
   * @param clazz Clazz are all properties of direct Subclasses.
   * @return Properties of direct Subclasses.
   */
  public OwlDetailsProperties<PropertyValue> handleDirectSubclasses(OWLOntology ontology, OWLClass clazz) {
    OwlDetailsProperties<PropertyValue> result = new OwlDetailsProperties<>();

    Iterator<OWLSubClassOfAxiom> iterator = ontology.subClassAxiomsForSuperClass(clazz).iterator();

    while (iterator.hasNext()) {
      OWLSubClassOfAxiom next = iterator.next();
      IRI iri = next.getSubClass().asOWLClass().getIRI();
      OwlDirectedSubClassesProperty r = new OwlDirectedSubClassesProperty();
      r.setType(WeaselOwlType.DIRECT_SUBCLASSES);
      r.setValue(new PairImpl(labelExtractor.getLabelOrDefaultFragment(iri), iri.toString()));
      String key = ViewerIdentifierFactory.createId(ViewerIdentifierFactory.Type.function,
          WeaselOwlType.DIRECT_SUBCLASSES.name().toLowerCase());
      result.addProperty(key, r);
    }
    result.sortPropertiesInAlphabeticalOrder();
    return result;
  }

  /**
   * This method is used to display sub-object property
   *
   * @param ontology This is a loaded ontology.
   * @param obj Obj are all properties of direct subObjectProperty.
   * @return Properties of direct subObjectProperty.
   */
  public OwlDetailsProperties<PropertyValue> handleDirectSubObjectProperty(OWLOntology ontology, OWLObjectProperty obj) {
    OwlDetailsProperties<PropertyValue> result = new OwlDetailsProperties<>();

    Iterator<OWLSubObjectPropertyOfAxiom> iterator = ontology.objectSubPropertyAxiomsForSuperProperty(obj).iterator();

    while (iterator.hasNext()) {
      LOG.debug("OwlDataHandler -> handleDirectSubObjectProperty {}", iterator.hasNext());
      OWLSubObjectPropertyOfAxiom next = iterator.next();

      IRI iri = next.getSubProperty().asOWLObjectProperty().getIRI();
      LOG.debug("OwlDataHandler -> handleDirectSubObjectProperty2 {}", iri.toString());

      OwlDirectedSubClassesProperty r = new OwlDirectedSubClassesProperty();

      r.setType(WeaselOwlType.DIRECT_SUBCLASSES);
      r.setValue(new PairImpl(labelExtractor.getLabelOrDefaultFragment(iri), iri.toString()));

      LOG.debug("OwlDataHandler -> handleDirectSubObjectProperty3 {}", r.toString());
      String key = ViewerIdentifierFactory.createId(ViewerIdentifierFactory.Type.function,
          WeaselOwlType.DIRECT_SUB_OBJECT_PROPERTY.name().toLowerCase());
      result.addProperty(key, r);
    }
    result.sortPropertiesInAlphabeticalOrder();
    return result;
  }

  /**
   * This method is used to display sub-annotation property
   *
   * @param ontology This is a loaded ontology.
   * @param annotationProperty AnnotationProperty are all properties of direct
   * subAnnotationProperty.
   * @return Properties of direct subAnnotationProperty.
   */
  public OwlDetailsProperties<PropertyValue> handleDirectSubAnnotationProperty(OWLOntology ontology, OWLAnnotationProperty annotationProperty) {
    OwlDetailsProperties<PropertyValue> result = new OwlDetailsProperties<>();

    //  Iterator<OWLSubAnnotationPropertyOfAxiom> iterator = ontology.subAnnotationPropertyOfAxioms(annotationProperty).iterator();
    Iterator<OWLAnnotationProperty> iterator = EntitySearcher.getSubProperties(annotationProperty, ontology).iterator();
    while (iterator.hasNext()) {

      LOG.debug("OwlDataHandler -> handleDirectSubAnnotationProperty {}", iterator.hasNext());
      OWLAnnotationProperty next = iterator.next();

      IRI iri = next.getIRI();

      OwlDirectedSubClassesProperty r = new OwlDirectedSubClassesProperty();

      r.setType(WeaselOwlType.DIRECT_SUBCLASSES);
      r.setValue(new PairImpl(labelExtractor.getLabelOrDefaultFragment(iri), iri.toString()));

      String key = ViewerIdentifierFactory.createId(ViewerIdentifierFactory.Type.function,
          WeaselOwlType.DIRECT_SUB_ANNOTATION_PROPERTY.name().toLowerCase());
      result.addProperty(key, r);
    }
    result.sortPropertiesInAlphabeticalOrder();
    return result;
  }

  /**
   * This method is used to display sub-data property
   *
   * @param ontology This is a loaded ontology.
   * @param odj Odj are all properties of direct subDataProperty.
   * @return Properties of direct subDataProperty.
   */
  public OwlDetailsProperties<PropertyValue> handleDirectSubDataProperty(OWLOntology ontology, OWLDataProperty odj) {
    OwlDetailsProperties<PropertyValue> result = new OwlDetailsProperties<>();

    // Iterator<OWLSubDataPropertyOfAxiom> iterator = ontology.dataSubPropertyAxiomsForSuperProperty(odj).iterator();
    Iterator<OWLDataProperty> iterator = EntitySearcher.getSubProperties(odj, ontology).iterator();

    while (iterator.hasNext()) {
      LOG.debug("OwlDataHandler -> handleDirectSubDataProperty {}", iterator.hasNext());
      OWLDataProperty next = iterator.next();

      IRI iri = next.getIRI();

      OwlDirectedSubClassesProperty r = new OwlDirectedSubClassesProperty();

      r.setType(WeaselOwlType.DIRECT_SUBCLASSES);
      r.setValue(new PairImpl(labelExtractor.getLabelOrDefaultFragment(iri), iri.toString()));

      String key = ViewerIdentifierFactory.createId(ViewerIdentifierFactory.Type.function,
          WeaselOwlType.DIRECT_SUB_DATA_PROPERTY.name().toLowerCase());
      result.addProperty(key, r);
    }
    result.sortPropertiesInAlphabeticalOrder();
    return result;
  }

  /**
   * This method is used to display Particular Individual
   *
   * @param ontology This is a loaded ontology.
   * @param clazz Clazz are all Instances.
   * @return All instances of a given class;
   */
  private OwlDetailsProperties<PropertyValue> handleInstances(OWLOntology ontology, OWLClass clazz) {
    OwlDetailsProperties<PropertyValue> result = individualDataHandler.handleClassIndividuals(ontology, clazz);
    result.sortPropertiesInAlphabeticalOrder();
    return result;
  }

//  /**
//   * This method is used to handle Inherited Axioms
//   *
//   * @param ontology Paramter which loaded ontology.
//   * @param clazz Clazz are all properties of Inherited Axioms.
//   * @return Class and properties of Inherited Axioms.
//   */

  
  private OwlDetailsProperties<PropertyValue> handleInheritedAxioms(OWLOntology ontology, OWLClass clazz) {
    OwlDetailsProperties<PropertyValue> result = new OwlDetailsProperties<>();
    String subClassOfKey = ViewerIdentifierFactory.createId(ViewerIdentifierFactory.Type.axiom, "SubClassOf");
    String equivalentClassKey = ViewerIdentifierFactory.createId(ViewerIdentifierFactory.Type.axiom, "EquivalentClasses");
    String key = ViewerIdentifierFactory.createId(ViewerIdentifierFactory.Type.function,
        WeaselOwlType.ANONYMOUS_ANCESTOR.name().toLowerCase());

    Set<OWLClass> rset = owlUtils.getSuperClasses(clazz, ontology);
    Map<IRI, Set<OwlAxiomPropertyValue>> values = new HashMap<>();
    
    rset.stream()
        .forEachOrdered((c) -> {
          OwlDetailsProperties<PropertyValue> handleAxioms = handleAxioms(c, ontology);
          for (Map.Entry<String, List<PropertyValue>> entry : handleAxioms.getProperties().entrySet()) {

            if (entry.getKey().equals(subClassOfKey) || entry.getKey().equals(equivalentClassKey)) {

              for (PropertyValue propertyValue : entry.getValue()) {
                if (propertyValue.getType() != WeaselOwlType.TAXONOMY) {

                  if (entry.getKey().equals(equivalentClassKey)) {
                    OwlAxiomPropertyValue opv = (OwlAxiomPropertyValue) propertyValue;
                    String val = opv.getValue();
                    String[] value = val.split(" ");
                    value[0] = value[1] = "";
                    val = String.join(" ", value);
                    opv.setValue(val);
                  }
                  OwlAxiomPropertyValue opv = (OwlAxiomPropertyValue) propertyValue;

                  Set<OwlAxiomPropertyValue> owlAxiomPropertyValues = values.getOrDefault(c.getIRI(), new  LinkedHashSet<>());
                  owlAxiomPropertyValues.add(opv);
                  values.put(c.getIRI(), owlAxiomPropertyValues);

                }

              }
            }
          }
        });

    StringBuilder sb = new StringBuilder();

    for (Map.Entry<IRI, Set<OwlAxiomPropertyValue>> entry : values.entrySet()) {
      OwlAxiomPropertyValue opv = new OwlAxiomPropertyValue();

      sb.append("%arg00%").append(" <br />"); 

      int i = 0;
      for (OwlAxiomPropertyValue owlAxiomPropertyValue : entry.getValue()) {
        i++;
        sb.append("- ").append(owlAxiomPropertyValue.getValue());
        if (i < entry.getValue().size()) {
          sb.append("<br />");
        }

        for (Map.Entry<String, OwlAxiomPropertyEntity> mapping : owlAxiomPropertyValue.getEntityMaping().entrySet()) {
          opv.addEntityValues(mapping.getKey(), mapping.getValue());

        }
      }
      OwlAxiomPropertyEntity prop = new OwlAxiomPropertyEntity();
      prop.setIri(entry.getKey().toString());
      prop.setLabel(labelExtractor.getLabelOrDefaultFragment(entry.getKey()));
      opv.addEntityValues("%arg00%", prop);

      opv.setValue(sb.toString());
      opv.setType(WeaselOwlType.AXIOM);

      sb = new StringBuilder();

      String fullRenderedString = parseRenderedString(opv);
      opv.setFullRenderedString(fullRenderedString);

      result.addProperty(key, opv);
    }

    result.sortPropertiesInAlphabeticalOrder();
    return result;
  }

  public OwlListDetails handleOntologyMetadata(IRI iri, OWLOntology ontology) {
    OwlListDetails wd = new OwlListDetails();
    OwlDetailsProperties<PropertyValue> metadata = fiboDataHandler.handleFiboOntologyMetadata(iri, ontology, wd);
    if (metadata != null && metadata.getProperties().keySet().size() > 0) {

      wd.addAllProperties(metadata);
      wd.setIri(iri.toString());
      wd.setLabel(labelExtractor.getLabelOrDefaultFragment(iri));
      wd.setLocationInModules(fiboDataHandler.getElementLocationInModules(iri.toString(), ontology));
      return wd;
    }
    return null;

  }

  public List<FiboModule> getAllModulesData(OWLOntology ontology) {
    return fiboDataHandler.getAllModulesData(ontology);
  }

  public List<String> getElementLocationInModules(String iriString, OWLOntology ontology) {
    LOG.debug("[Data Handler] Handle location for element {}", iriString);
    return fiboDataHandler.getElementLocationInModules(iriString, ontology);
  }

  public OwlListDetails handleParticularDatatype(IRI iri, OWLOntology ontology) {
    OwlListDetails resultDetails = new OwlListDetails();
    Iterator<OWLDatatype> dataTypeIterator = ontology.datatypesInSignature().iterator();

    while (dataTypeIterator.hasNext()) {
      OWLDatatype data = dataTypeIterator.next();

      if (data.getIRI().equals(iri)) {
        LOG.debug("[Data Handler] Find owl dataType wih iri: {}", iri.toString());

        resultDetails.setLabel(labelExtractor.getLabelOrDefaultFragment(iri));
        OwlDetailsProperties<PropertyValue> annotations
            = handleAnnotations(data.getIRI(), ontology, resultDetails);
        resultDetails.addAllProperties(annotations);

      }
    }
    return resultDetails;
  }

  public OwlListDetails handleParticularAnnotationProperty(IRI iri, OWLOntology ontology) {
    OwlListDetails resultDetails = new OwlListDetails();
    Iterator<OWLAnnotationProperty> dataTypeIterator = ontology.annotationPropertiesInSignature().iterator();

    while (dataTypeIterator.hasNext()) {
      OWLAnnotationProperty data = dataTypeIterator.next();

      if (data.getIRI().equals(iri)) {
        LOG.debug("[Data Handler] Find owl dataType wih iri: {}", iri.toString());

        resultDetails.setLabel(labelExtractor.getLabelOrDefaultFragment(iri));

        OwlDetailsProperties<PropertyValue> directSubAnnotationProperty = handleDirectSubAnnotationProperty(ontology, data);

        OwlDetailsProperties<PropertyValue> axioms = handleAxioms(data, ontology);

        List<PropertyValue> subElements = getSuperElements(data, ontology, WeaselOwlType.AXIOM_ANNOTATION_PROPERTY);
        OwlTaxonomyImpl taxonomy = extractTaxonomy(subElements, iri, ontology, WeaselOwlType.AXIOM_ANNOTATION_PROPERTY);
        taxonomy.sort();
        // subElements = subElements.stream().filter((pv) -> (!pv.getType().equals(WeaselOwlType.TAXONOMY))).collect(Collectors.toList());
        OwlDetailsProperties<PropertyValue> annotations
            = handleAnnotations(data.getIRI(), ontology, resultDetails);

        //resultDetails.getProperties().put(subAnnotationPropertyOfIriString, subElements);
        resultDetails.addAllProperties(annotations);
        resultDetails.addAllProperties(directSubAnnotationProperty);
        resultDetails.addAllProperties(axioms);
        resultDetails.setTaxonomy(taxonomy);
      }
    }
    return resultDetails;

  }

  /**
   * @see FiboDataHandler#getMaturityLevelForElement FiboDataHandler
   * @param iriString iri represented by string
   * @param ontology loaded owl ontology
   * @return
   */
  public OntoFiboMaturityLevel getMaturityLevel(String iriString, OWLOntology ontology) {
    return fiboDataHandler.getMaturityLevelForElement(iriString, ontology);
  }

  private void parseUrlAxiom(String eIri, String[] splited, int i, Boolean hasOpeningParenthesis, int countOpeningParenthesis, Boolean hasClosingParenthesis, int countClosingParenthesis, Boolean hasComma, int countComma) {
    String label = labelExtractor.getLabelOrDefaultFragment(IRI.create(eIri));

    String textToReplace = label;

    if (hasOpeningParenthesis) {
      String prefix = String.join("", Collections.nCopies(countOpeningParenthesis, "("));
      textToReplace = prefix + textToReplace;
    }
    if (hasClosingParenthesis) {
      String postfix = String.join("", Collections.nCopies(countClosingParenthesis, ")"));
      textToReplace = textToReplace + postfix;
    }
    if (hasComma) {
      String postfix = String.join("", Collections.nCopies(countComma, ","));
      textToReplace = textToReplace + postfix;
    }
    splited[i] = textToReplace;

  }

  private OwlDetailsProperties<PropertyValue> extractUsageForClasses(OWLClass clazz, OWLOntology ontology) {

    OwlDetailsProperties<PropertyValue> result = new OwlDetailsProperties<>();
    String key = ViewerIdentifierFactory.createId(ViewerIdentifierFactory.Type.function,
        WeaselOwlType.USAGE_CLASSES.name().toLowerCase());
    //Usage OWLClass--------------------------------------------------------------------------

    Set<OWLSubClassOfAxiom> s = ontology.axioms(AxiomType.SUBCLASS_OF)
        .filter(el -> el.accept(containsVisitors.visitor(clazz.getIRI()))
        .booleanValue() == Boolean.TRUE)
        .collect(Collectors.toSet());
    int start = 0;
    Map<IRI, List<OwlAxiomPropertyValue>> values = new HashMap<>();
    for (OWLSubClassOfAxiom axiom : s) {
      LOG.debug("OwlDataHandler -> extractUsage {}", axiom.toString());
      LOG.debug("OwlDataHandler -> extractUsageAx {}", axiom.getSubClass());

      IRI iri = axiom.getSubClass().asOWLClass().getIRI();
      if (iri.equals(clazz.getIRI())) {
        continue;
      }

      String iriFragment = iri.getFragment();
      String splitFragment = StringUtils.getFragment(iri);
      Boolean fixRenderedIri = !iriFragment.equals(splitFragment);

      Set<String> ignoredToDisplay = config.getViewerCoreConfig().getIgnoredElements();

      OwlAxiomPropertyValue opv = prepareAxiomPropertyValue(axiom, iriFragment, splitFragment, fixRenderedIri, ignoredToDisplay, key, start, false);
      start = opv.getLastId() + 1;
      List<OwlAxiomPropertyValue> ll = values.getOrDefault(iri, new LinkedList());
      ll.add(opv);

      values.put(iri, ll);
    }

    StringBuilder sb = new StringBuilder();

    for (Map.Entry<IRI, List<OwlAxiomPropertyValue>> entry : values.entrySet()) {
      OwlAxiomPropertyValue opv = new OwlAxiomPropertyValue();
      sb.append("%arg00%").append(" <br />"); //<br />
      int i = 0;
      for (OwlAxiomPropertyValue owlAxiomPropertyValue : entry.getValue()) {
        i++;
        sb.append("- ").append(owlAxiomPropertyValue.getValue());
        if (i < entry.getValue().size()) {
          sb.append("<br />");
        }
        for (Map.Entry<String, OwlAxiomPropertyEntity> maping : owlAxiomPropertyValue.getEntityMaping().entrySet()) {
          opv.addEntityValues(maping.getKey(), maping.getValue());
        }
      }
      OwlAxiomPropertyEntity prop = new OwlAxiomPropertyEntity();
      prop.setIri(entry.getKey().toString());
      prop.setLabel(labelExtractor.getLabelOrDefaultFragment(entry.getKey()));
      opv.addEntityValues("%arg00%", prop);

      opv.setValue(sb.toString());
      opv.setType(WeaselOwlType.AXIOM);

      LOG.debug("Generated big axiom: {}", sb.toString());
      sb = new StringBuilder();

      String fullRenderedString = parseRenderedString(opv);
      opv.setFullRenderedString(fullRenderedString);

      result.addProperty(key, opv);
    }

    //Range of ObjectProperty--------------------------------
    Set<OWLObjectPropertyRangeAxiom> ops = ontology.axioms(AxiomType.OBJECT_PROPERTY_RANGE)
        .filter(el -> el.accept(containsVisitors.visitorObjectProperty(clazz.getIRI())))
        .collect(Collectors.toSet());

    int startR = 0;
    Map<IRI, List<OwlAxiomPropertyValue>> valuesO = new HashMap<>();

    LOG.debug("How many range is found for x : {}", ops.size());

    for (OWLObjectPropertyRangeAxiom axiom : ops) {
      OWLEntity rangeEntity = axiom.signature()
          .filter(e -> !e.getIRI()
          .equals(clazz.getIRI()))
          .findFirst().get();
      LOG.debug("OwlDataHandler -> extractUsageRangeAxiom {}", rangeEntity.getIRI());

      String iriFragment = rangeEntity.getIRI().toString();
      String splitFragment = StringUtils.getFragment(rangeEntity.getIRI().toString());
      Boolean fixRenderedIri = !iriFragment.equals(splitFragment);

      Set<String> ignoredToDisplay = config.getViewerCoreConfig().getIgnoredElements();

      OwlAxiomPropertyValue opv = prepareAxiomPropertyValue(axiom, iriFragment, splitFragment, fixRenderedIri, ignoredToDisplay, key, startR, false);
      startR = opv.getLastId() + 1;
      List<OwlAxiomPropertyValue> ll = valuesO.getOrDefault(rangeEntity, new LinkedList());
      ll.add(opv);

      valuesO.put(rangeEntity.getIRI(), ll);

    }

    StringBuilder sbo = new StringBuilder();

    for (Map.Entry<IRI, List<OwlAxiomPropertyValue>> entry : valuesO.entrySet()) {
      OwlAxiomPropertyValue opv = new OwlAxiomPropertyValue();
      sbo.append("%arg00%").append(" <br />"); //<br />
      int i = 0;
      for (OwlAxiomPropertyValue owlAxiomPropertyValue : entry.getValue()) {
        i++;
        sbo.append("- ").append(owlAxiomPropertyValue.getValue());
        if (i < entry.getValue().size()) {
          sbo.append("<br />");
        }
        for (Map.Entry<String, OwlAxiomPropertyEntity> maping : owlAxiomPropertyValue.getEntityMaping().entrySet()) {
          opv.addEntityValues(maping.getKey(), maping.getValue());
        }
      }
      OwlAxiomPropertyEntity prop = new OwlAxiomPropertyEntity();
      prop.setIri(entry.getKey().toString());
      prop.setLabel(labelExtractor.getLabelOrDefaultFragment(entry.getKey()));
      opv.addEntityValues("%arg00%", prop);

      opv.setValue(sbo.toString());
      opv.setType(WeaselOwlType.AXIOM);

      LOG.debug("Generated big axiom: {}", sbo.toString());
      sbo = new StringBuilder();

      String fullRenderedString = parseRenderedString(opv);
      opv.setFullRenderedString(fullRenderedString);

      result.addProperty(key, opv);
    }

    //Domain of ObjectProperty-----------------------------------------------------
    Set<OWLObjectPropertyDomainAxiom> opd = ontology.axioms(AxiomType.OBJECT_PROPERTY_DOMAIN)
        .filter(el -> el.accept(containsVisitors.visitorObjectProperty(clazz.getIRI())))
        .collect(Collectors.toSet());
    int startD = 0;
    Map<IRI, List<OwlAxiomPropertyValue>> valuesD = new HashMap<>();

    for (OWLObjectPropertyDomainAxiom axiom : opd) {
      OWLEntity domainEntity
          = axiom.signature()
              .filter(e -> !e.getIRI()
              .equals(clazz.getIRI()))
              .findFirst().get();
      LOG.debug("OwlDataHandler -> extractUsageObjectDomainAxiom {}", domainEntity.getIRI());

      String iriFragment = domainEntity.getIRI().toString();
      String splitFragment = StringUtils.getFragment(domainEntity.getIRI().toString());
      Boolean fixRenderedIri = !iriFragment.equals(splitFragment);

      Set<String> ignoredToDisplay = config.getViewerCoreConfig().getIgnoredElements();

      OwlAxiomPropertyValue opv = prepareAxiomPropertyValue(axiom, iriFragment, splitFragment, fixRenderedIri, ignoredToDisplay, key, startD, false);
      startD = opv.getLastId() + 1;
      List<OwlAxiomPropertyValue> ll = valuesD.getOrDefault(domainEntity, new LinkedList());
      ll.add(opv);

      valuesD.put(domainEntity.getIRI(), ll);

    }

    StringBuilder sbd = new StringBuilder();

    for (Map.Entry<IRI, List<OwlAxiomPropertyValue>> entry
        : valuesD.entrySet()) {
      OwlAxiomPropertyValue opv = new OwlAxiomPropertyValue();
      sbd.append("%arg00%").append(" <br />");
      int i = 0;
      for (OwlAxiomPropertyValue owlAxiomPropertyValue : entry.getValue()) {
        i++;
        sbd.append("- ").append(owlAxiomPropertyValue.getValue());
        if (i < entry.getValue().size()) {
          sbd.append("<br />");
        }
        for (Map.Entry<String, OwlAxiomPropertyEntity> maping : owlAxiomPropertyValue.getEntityMaping().entrySet()) {
          opv.addEntityValues(maping.getKey(), maping.getValue());
        }
      }
      OwlAxiomPropertyEntity prop = new OwlAxiomPropertyEntity();
      prop.setIri(entry.getKey().toString());
      prop.setLabel(labelExtractor.getLabelOrDefaultFragment(entry.getKey()));
      opv.addEntityValues("%arg00%", prop);

      opv.setValue(sbd.toString());
      opv.setType(WeaselOwlType.AXIOM);

      LOG.debug("Generated big axiom: {}", sbd.toString());
      sbd = new StringBuilder();
      String fullRenderedString = parseRenderedString(opv);
      opv.setFullRenderedString(fullRenderedString);

      result.addProperty(key, opv);
    }

    result.sortPropertiesInAlphabeticalOrder();
    return result;
  }

  private String parseRenderedString(OwlAxiomPropertyValue opv) {
    String result = opv.getValue();
    for (Map.Entry<String, OwlAxiomPropertyEntity> entry : opv.getEntityMaping().entrySet()) {
      LOG.debug("parseRenderedString: {}", entry.toString());
      String key = entry.getKey();
      if (!key.contains("arg")) {
        continue;
      }
      String replecment = entry.getValue().getLabel();
      LOG.debug("replecment: {}", replecment.toString());
      result = result.replaceAll(key, replecment);
      LOG.debug("result: {}", result.toString());
    }

    return result;

  }
}
