package org.edmcouncil.spec.fibo.weasel.ontology.data;

import java.util.ArrayList;
import java.util.Collections;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.edmcouncil.spec.fibo.config.configuration.model.PairImpl;
import org.edmcouncil.spec.fibo.weasel.model.FiboModule;
import org.edmcouncil.spec.fibo.weasel.model.PropertyValue;
import org.edmcouncil.spec.fibo.weasel.model.property.OwlAxiomPropertyEntity;
import org.edmcouncil.spec.fibo.weasel.model.property.OwlAxiomPropertyValue;
import org.edmcouncil.spec.fibo.weasel.model.property.OwlDirectedSubClassesProperty;
import org.edmcouncil.spec.fibo.weasel.model.property.OwlListElementIndividualProperty;
import org.edmcouncil.spec.fibo.weasel.model.taxonomy.OwlTaxonomyElementImpl;
import org.edmcouncil.spec.fibo.weasel.model.taxonomy.OwlTaxonomyImpl;
import org.edmcouncil.spec.fibo.weasel.model.taxonomy.OwlTaxonomyValue;
import org.edmcouncil.spec.fibo.weasel.ontology.visitor.WeaselOntologyVisitors;
import org.edmcouncil.spec.fibo.weasel.utils.StringUtils;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.reasoner.InferenceDepth;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.springframework.stereotype.Component;

/**
 * @author Michał Daniel (michal.daniel@makolab.com)
 * @author Patrycja Miazek (patrycja.miazek@makolab.com)
 */
@Component
public class OwlDataHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(OwlDataHandler.class);

  private final OWLObjectRenderer rendering = new ManchesterOWLSyntaxOWLObjectRendererImpl();
  @Autowired
  private OwlDataExtractor dataExtractor;
  @Autowired
  private FiboDataHandler fiboDataHandler;
  @Autowired
  private AnnotationsDataHandler annotationsDataHandler;
  @Autowired
  private IndividualDataHandler individualDataHandler;

  public OwlListDetails handleParticularClass(IRI iri, OWLOntology ontology) {
    OwlListDetails resultDetails = new OwlListDetails();
    Iterator<OWLClass> classesIterator = ontology.classesInSignature().iterator();

    while (classesIterator.hasNext()) {
      OWLClass clazz = classesIterator.next();

      if (clazz.getIRI().equals(iri)) {
        LOGGER.debug("[Data Handler] Find OWL class wih IRI: {}", iri.toString());

        handleParticularSubClassOf(ontology, clazz);

        resultDetails.setLabel(StringUtils.getFragment(clazz.getIRI()));

        OwlDetailsProperties<PropertyValue> axioms = handleAxioms(clazz, ontology);

        List<PropertyValue> subclasses = axioms
            .getProperties()
            .getOrDefault(AxiomType.SUBCLASS_OF.getName(), new ArrayList<>(0));
        List<PropertyValue> taxElements = subclasses
            .stream()
            .filter((pv) -> (pv.getType().equals(WeaselOwlType.TAXONOMY)))
            .collect(Collectors.toList());
        OwlDetailsProperties<PropertyValue> handleSubClassOf = handleParticularSubClassOf(ontology, clazz);
        OwlDetailsProperties<PropertyValue> individuals = handleParticularIndividual(ontology, clazz);
        OwlDetailsProperties<PropertyValue> inheritedAxioms = handleInheritedAxioms(ontology, clazz);
        //This code is only for fibo ontology, this line can be deleted for other ontologies.
        //OwlDetailsProperties<PropertyValue> modules = fiboDataHandler.handleFiboModulesData(ontology, clazz);

        subclasses = subclasses.stream().filter((pv) -> (!pv.getType().equals(WeaselOwlType.TAXONOMY))).collect(Collectors.toList());
        axioms.getProperties().put(AxiomType.SUBCLASS_OF.getName(), subclasses);
        OwlTaxonomyImpl tax = extractTaxonomy(taxElements, iri, ontology, WeaselOwlType.AXIOM_CLASS);
        tax.sort();

        resultDetails.setTaxonomy(tax);
        OwlDetailsProperties<PropertyValue> annotations
            = handleAnnotations(clazz.getIRI(), ontology);

        resultDetails.addAllProperties(axioms);
        resultDetails.addAllProperties(annotations);
        resultDetails.addAllProperties(handleSubClassOf);
        resultDetails.addAllProperties(individuals);
        resultDetails.addAllProperties(inheritedAxioms);
        // resultDetails.addAllProperties(modules);

      }
    }
    return resultDetails;
  }

  public OwlListDetails handleParticularDatatype(IRI iri, OWLOntology ontology) {
    OwlListDetails resultDetails = new OwlListDetails();
    Iterator<OWLDataProperty> dataTypeIterator = ontology.dataPropertiesInSignature().iterator();

    while (dataTypeIterator.hasNext()) {
      OWLDataProperty data = dataTypeIterator.next();

      if (data.getIRI().equals(iri)) {
        LOGGER.debug("[Data Handler] Find owl dataType wih iri: {}", iri.toString());

        resultDetails.setLabel(StringUtils.getFragment(data.getIRI()));

        OwlDetailsProperties<PropertyValue> axioms = handleAxioms(data, ontology);

        OwlDetailsProperties<PropertyValue> annotations
            = handleAnnotations(data.getIRI(), ontology);

        resultDetails.addAllProperties(axioms);
        resultDetails.addAllProperties(annotations);

      }
    }
    return resultDetails;
  }

  public OwlListDetails handleParticularIndividual(IRI iri, OWLOntology ontology) {
    OwlListDetails resultDetails = new OwlListDetails();
    Iterator<OWLNamedIndividual> individualIterator = ontology.individualsInSignature().iterator();

    while (individualIterator.hasNext()) {
      OWLNamedIndividual individual = individualIterator.next();

      if (individual.getIRI().equals(iri)) {
        LOGGER.debug("[Data Handler] Find owl named individual wih iri: {}", iri.toString());

        resultDetails.setLabel(StringUtils.getFragment(individual.getIRI()));

        OwlDetailsProperties<PropertyValue> axioms = handleAxioms(individual, ontology);

        OwlDetailsProperties<PropertyValue> annotations
            = handleAnnotations(individual.getIRI(), ontology);

        resultDetails.addAllProperties(axioms);
        resultDetails.addAllProperties(annotations);
      }
    }
    return resultDetails;
  }

  private OwlDetailsProperties<PropertyValue> handleAnnotations(IRI iri, OWLOntology ontology) {
    return annotationsDataHandler.handleAnnotations(iri, ontology);
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

  private OwlTaxonomyImpl extractTaxonomy(List<PropertyValue> subElements, IRI objIri, OWLOntology ontology, WeaselOwlType type) {
    OwlTaxonomyImpl taxonomy = new OwlTaxonomyImpl();
    if (subElements.size() > 0) {

      for (PropertyValue property : subElements) {
        if (property.getType().equals(WeaselOwlType.TAXONOMY)) {
          OwlAxiomPropertyValue axiomProperty = (OwlAxiomPropertyValue) property;
          IRI sci = extractSubElementIri(axiomProperty, objIri);
          OWLEntity entity = createEntity(ontology, sci, type);
          LOGGER.trace("\t{} Sub Element Of {}", StringUtils.getFragment(objIri),
              StringUtils.getFragment(entity.getIRI()));
          List<PropertyValue> subTax = getSubElements(entity, ontology, type);

          OwlTaxonomyImpl subCLassTax = extractTaxonomy(subTax, entity.getIRI(), ontology, type);

          OwlTaxonomyValue val1 = new OwlTaxonomyValue(WeaselOwlType.STRING, StringUtils.getFragment(objIri));
          OwlTaxonomyValue val2 = new OwlTaxonomyValue(WeaselOwlType.IRI, objIri.getIRIString());
          OwlTaxonomyElementImpl taxEl = new OwlTaxonomyElementImpl(val1, val2);

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

      LOGGER.trace("\t\tEnd leaf on {}", StringUtils.getFragment(objIri));
      OwlTaxonomyValue val1 = new OwlTaxonomyValue(WeaselOwlType.STRING, StringUtils.getFragment(objIri));
      OwlTaxonomyValue val2 = new OwlTaxonomyValue(WeaselOwlType.IRI, objIri.getIRIString());
      OwlTaxonomyElementImpl taxEl = new OwlTaxonomyElementImpl(val1, val2);
      List<OwlTaxonomyElementImpl> currentTax = new LinkedList<>();
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
    }

    return null;
  }

  private IRI extractSubElementIri(OwlAxiomPropertyValue axiomProperty, IRI objIri) {
    for (Map.Entry<String, OwlAxiomPropertyEntity> entry : axiomProperty.getEntityMaping().entrySet()) {
      if (!entry.getValue().getIri().equals(objIri.getIRIString())) {
        return IRI.create(entry.getValue().getIri());
      }
    }
    return null;
  }

  private <T extends OWLAxiom> OwlDetailsProperties<PropertyValue> handleAxioms(
      Iterator<T> axiomsIterator, IRI elementIri) {
    OwlDetailsProperties<PropertyValue> result = new OwlDetailsProperties<>();
    String iriFragment = elementIri.getFragment();
    String splitFragment = StringUtils.getFragment(elementIri);
    Boolean fixRenderedIri = !iriFragment.equals(splitFragment); //if fragments is not the same we must repair rendered value
    while (axiomsIterator.hasNext()) {
      T axiom = axiomsIterator.next();
      String value = rendering.render(axiom);

      value = fixRenderedValue(value, iriFragment, splitFragment, fixRenderedIri);

      String key = axiom.getAxiomType().getName();
      OwlAxiomPropertyValue opv = new OwlAxiomPropertyValue();
      opv.setValue(value);

      opv.setType(WeaselOwlType.AXIOM);
      LOGGER.trace("[Data Handler] Find Axiom \"{}\" with type \"{}\"", value, key);
      Boolean isRestriction = isRestriction(axiom);

      if (!isRestriction && axiom.getAxiomType().equals(AxiomType.SUBCLASS_OF)) {
        LOGGER.trace("[Data Handler] Find non restriction SubClassOf");
        opv.setType(WeaselOwlType.TAXONOMY);
      }

      processingAxioms(axiom, fixRenderedIri, iriFragment, splitFragment, opv, value);
      result.addProperty(key, opv);
    }
    result.sortPropertiesInAlphabeticalOrder();
    return result;
  }

  private <T extends OWLAxiom> void processingAxioms(
      T axiom,
      Boolean fixRenderedIri,
      String iriFragment,
      String splitFragment,
      OwlAxiomPropertyValue opv,
      String renderedVal) {
    String argPattern = "/arg%s/";
    String[] splited = renderedVal.split(" ");
    String openingParenthesis = "(";
    String closingParenthesis = ")";
    Iterator<OWLEntity> iterator = axiom.signature().iterator();

    while (iterator.hasNext()) {
      OWLEntity next = iterator.next();
      String eSignature = rendering.render(next);
      eSignature = fixRenderedIri && iriFragment.equals(eSignature) ? splitFragment : eSignature;
      String key = null;

      for (int i = 0; i < splited.length; i++) {
        String string = splited[i];
        //more than 1 because when it's 1, it's a number
        Boolean hasOpeningParenthesis = string.length() > 1 ? string.startsWith(openingParenthesis) : false;
        int countOpeningParenthesis = StringUtils.countLetter(string, '(');
        Boolean hasClosingParenthesis = string.length() > 1 ? string.endsWith(closingParenthesis) : false;
        int countClosingParenthesis = StringUtils.countLetter(string, ')');
        if (hasOpeningParenthesis) {
          string = string.substring(countOpeningParenthesis);
        }
        if (hasClosingParenthesis) {
          string = string.substring(0, string.length() - countClosingParenthesis);
        }
        if (string.equals(eSignature)) {
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
          splited[i] = textToReplace;
          break;
        }
      }
      if (key == null) {
        continue;
      }

      String eIri = next.getIRI().toString();
      OwlAxiomPropertyEntity axiomPropertyEntity = new OwlAxiomPropertyEntity();
      axiomPropertyEntity.setIri(eIri);
      axiomPropertyEntity.setLabel(eSignature);
      opv.addEntityValues(key, axiomPropertyEntity);

    }
    opv.setValue(String.join(" ", splited));
  }

  private String fixRenderedValue(String value, String iriFragment, String splitFragment, Boolean fixRenderedIri) {
    String[] split = value.split(" ");
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

  private static <T extends OWLAxiom> Boolean isRestriction(T axiom) {
    Boolean isRestriction = axiom.accept(WeaselOntologyVisitors.isRestrictionVisitor);
    if (isRestriction == null) {
      isRestriction = Boolean.FALSE;
    }
    return isRestriction;
  }

  public OwlListDetails handleParticularDataProperty(IRI iri, OWLOntology ontology) {
    OwlListDetails resultDetails = new OwlListDetails();
    Iterator<OWLDataProperty> dataPropertyIt = ontology.dataPropertiesInSignature().iterator();

    while (dataPropertyIt.hasNext()) {
      OWLDataProperty dataProperty = dataPropertyIt.next();

      if (dataProperty.getIRI().equals(iri)) {
        LOGGER.debug("[Data Handler] Find owl data property wih iri: {}", iri.toString());

        resultDetails.setLabel(StringUtils.getFragment(dataProperty.getIRI()));

        OwlDetailsProperties<PropertyValue> axioms = handleAxioms(dataProperty, ontology);

        List<PropertyValue> subElements = getSubElements(dataProperty, ontology, WeaselOwlType.AXIOM_DATA_PROPERTY);
        OwlTaxonomyImpl taxonomy = extractTaxonomy(subElements, iri, ontology, WeaselOwlType.AXIOM_DATA_PROPERTY);
        taxonomy.sort();

        OwlDetailsProperties<PropertyValue> annotations
            = handleAnnotations(dataProperty.getIRI(), ontology);

        resultDetails.addAllProperties(axioms);
        resultDetails.addAllProperties(annotations);
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
        LOGGER.debug("[Data Handler] Find owl object property wih iri: {}", iri.toString());

        resultDetails.setLabel(StringUtils.getFragment(dataProperty.getIRI()));

        OwlDetailsProperties<PropertyValue> axioms = handleAxioms(dataProperty, ontology);

        List<PropertyValue> subElements = getSubElements(dataProperty, ontology, WeaselOwlType.AXIOM_OBJECT_PROPERTY);
        OwlTaxonomyImpl taxonomy = extractTaxonomy(subElements, iri, ontology, WeaselOwlType.AXIOM_OBJECT_PROPERTY);
        taxonomy.sort();

        OwlDetailsProperties<PropertyValue> annotations
            = handleAnnotations(dataProperty.getIRI(), ontology);

        resultDetails.addAllProperties(axioms);
        resultDetails.addAllProperties(annotations);
        resultDetails.setTaxonomy(taxonomy);
      }
    }
    return resultDetails;
  }

  private List<PropertyValue> getSubElements(OWLEntity entity, OWLOntology ontology, WeaselOwlType type) {
    Stream<OWLProperty> propertyStream = null;
    OWLProperty prop = null;
    switch (type) {
      case AXIOM_CLASS:
        return getSubclasses(ontology, AxiomType.SUBCLASS_OF, entity);
      case AXIOM_DATA_PROPERTY:
        prop = entity.asOWLDataProperty();
        propertyStream = EntitySearcher.getSuperProperties(prop, ontology);
        break;
      case AXIOM_OBJECT_PROPERTY:
        prop = entity.asOWLObjectProperty();
        propertyStream = EntitySearcher.getSuperProperties(prop, ontology);
        break;
    }
    List<PropertyValue> resultProperties = new LinkedList<>();

    for (OWLProperty owlProperty : propertyStream.collect(Collectors.toSet())) {
      LOGGER.trace("{} Sub Property Of {}", StringUtils.getFragment(entity.getIRI()),
          StringUtils.getFragment(owlProperty.getIRI()));
      IRI subClazzIri = entity.getIRI();
      IRI superClazzIri = owlProperty.getIRI();

      OwlAxiomPropertyValue pv = new OwlAxiomPropertyValue();
      OwlAxiomPropertyEntity entitySubClass = new OwlAxiomPropertyEntity();
      OwlAxiomPropertyEntity entitySuperClass = new OwlAxiomPropertyEntity();
      entitySubClass.setIri(subClazzIri.getIRIString());
      entitySubClass.setLabel(StringUtils.getFragment(subClazzIri));
      entitySuperClass.setIri(superClazzIri.getIRIString());
      entitySuperClass.setLabel(StringUtils.getFragment(superClazzIri));

      pv.setType(WeaselOwlType.TAXONOMY);
      pv.addEntityValues(StringUtils.getFragment(subClazzIri), entitySubClass);
      pv.addEntityValues(StringUtils.getFragment(superClazzIri), entitySuperClass);
      resultProperties.add(pv);

    }

    return resultProperties;
  }

  private List<PropertyValue> getSubclasses(OWLOntology ontology, AxiomType<OWLSubClassOfAxiom> subType, OWLEntity entity) {
    List<PropertyValue> result = new LinkedList<>();
    ontology.axioms(subType)
        .collect(Collectors.toList())
        .stream()
        .filter((subClasse)
            -> (subClasse.getSuperClass() instanceof OWLClass
        && subClasse.getSubClass() instanceof OWLClass))
        .forEachOrdered((subClasse) -> {

          OWLClass superClazz = (OWLClass) subClasse.getSuperClass();
          OWLClass subClazz = (OWLClass) subClasse.getSubClass();
          if (subClazz.getIRI().equals(entity.getIRI())) {
            IRI subClazzIri = subClazz.getIRI();
            IRI superClazzIri = superClazz.getIRI();

            OwlAxiomPropertyValue pv = new OwlAxiomPropertyValue();
            pv.setType(WeaselOwlType.TAXONOMY);
            OwlAxiomPropertyEntity entitySubClass = new OwlAxiomPropertyEntity();
            OwlAxiomPropertyEntity entitySuperClass = new OwlAxiomPropertyEntity();
            entitySubClass.setIri(subClazzIri.getIRIString());
            entitySubClass.setLabel(StringUtils.getFragment(subClazzIri));
            entitySuperClass.setIri(superClazzIri.getIRIString());
            entitySuperClass.setLabel(StringUtils.getFragment(superClazzIri));

            pv.setType(WeaselOwlType.TAXONOMY);
            pv.addEntityValues(StringUtils.getFragment(subClazzIri), entitySubClass);
            pv.addEntityValues(StringUtils.getFragment(superClazzIri), entitySuperClass);

            pv.setValue(rendering.render(subClasse));
            result.add(pv);
          }
        });
    return result;
  }

  /**
   * This method is used to display SubClassOf
   *
   * @param ontology This is a loaded ontology.
   * @param clazz Clazz are all properties of Inherited Axioms.
   * @return properties of Inherited Axioms.
   */
  public OwlDetailsProperties<PropertyValue> handleParticularSubClassOf(OWLOntology ontology, OWLClass clazz) {
    OwlDetailsProperties<PropertyValue> result = new OwlDetailsProperties<PropertyValue>();

    Iterator<OWLSubClassOfAxiom> iterator = ontology.subClassAxiomsForSuperClass(clazz).iterator();

    while (iterator.hasNext()) {
      OWLSubClassOfAxiom next = iterator.next();
      IRI iri = next.getSubClass().asOWLClass().getIRI();
      String fragment = StringUtils.getFragment(iri);
      OwlDirectedSubClassesProperty r = new OwlDirectedSubClassesProperty();
      r.setType(WeaselOwlType.DIRECT_SUBCLASSES);
      r.setValue(new PairImpl(fragment, iri.toString()));
      result.addProperty(WeaselOwlType.DIRECT_SUBCLASSES.name(), r);
    }
    return result;
  }

  /**
   * This method is used to display Particular Individual
   *
   * @param ontology This is a loaded ontology.
   * @param clazz Clazz are all properties of Inherited Axioms.
   * @return properties of Inherited Axioms.
   */
  private OwlDetailsProperties<PropertyValue> handleParticularIndividual(OWLOntology ontology, OWLClass clazz) {
    OwlDetailsProperties<PropertyValue> result = new OwlDetailsProperties<>();
    OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
    OWLReasoner reasoner = reasonerFactory.createNonBufferingReasoner(ontology);
    NodeSet<OWLNamedIndividual> instances = reasoner.getInstances(clazz, true);

    for (OWLNamedIndividual namedIndividual : instances.entities().collect(Collectors.toSet())) {
      String fragment = StringUtils.getFragment(namedIndividual.getIRI());
      //LOGGER.debug(namedIndividual.getIRI().toString());
      OwlListElementIndividualProperty s = new OwlListElementIndividualProperty();
      s.setType(WeaselOwlType.INSTANCES);
      s.setValue(new PairImpl(fragment, namedIndividual.getIRI().toString()));
      result.addProperty(WeaselOwlType.INSTANCES.name(), s);
      namedIndividual.getEntityType();
    }
    return result;
  }

  /**
   * This method is used to display Inherited Axioms
   *
   * @param ontology Paramter which loaded ontology.
   * @param clazz Class are all properties of Inherited Axioms.
   * @return properties of Inherited Axioms.
   */
  private OwlDetailsProperties<PropertyValue> handleInheritedAxioms(OWLOntology ontology, OWLClass clazz) {
    OwlDetailsProperties<PropertyValue> result = new OwlDetailsProperties<>();
    OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
    OWLReasoner reasoner = reasonerFactory.createNonBufferingReasoner(ontology);

    NodeSet<OWLClass> rset = reasoner.getSuperClasses(clazz, InferenceDepth.ALL);
    rset.entities().collect(Collectors.toSet())
        .stream()
        .map((c) -> handleAxioms(c, ontology))
        .forEachOrdered((handleAxioms) -> {
          for (Map.Entry<String, List<PropertyValue>> entry : handleAxioms.getProperties().entrySet()) {
            if (entry.getKey().equals("SubClassOf")) {
              for (PropertyValue propertyValue : entry.getValue()) {
                if (propertyValue.getType() != WeaselOwlType.TAXONOMY) {
                  result.addProperty(WeaselOwlType.ANONYMOUS_ANCESTOR.name(), propertyValue);
                }
              }
            }
          }
        });

    result.sortPropertiesInAlphabeticalOrder();
    return result;
  }

  public OwlListDetails handleOntologyMetadata(IRI iri, OWLOntology ontology) {

    OwlDetailsProperties<PropertyValue> metadata = fiboDataHandler.handleFiboOntologyMetadata(iri, ontology);
    OwlListDetails wd = new OwlListDetails();
    if (metadata != null && metadata.getProperties().keySet().size() > 0) {
      wd.addAllProperties(metadata);
    }
    wd.setIri(iri.toString());
    wd.setLabel(StringUtils.getFragment(iri));
    wd.setLocationInModules(fiboDataHandler.getElementLocationInModules(iri.toString(), ontology));
    return wd;

  }

  public List<FiboModule> getAllModulesData(OWLOntology ontology) {
    return fiboDataHandler.getAllModulesData(ontology);
  }

  public List<String> getElementLocationInModules(String iriString, OWLOntology ontology) {
    LOGGER.debug("[Data Handler] Handle location for element {}", iriString);
    return fiboDataHandler.getElementLocationInModules(iriString, ontology);
  }

}
