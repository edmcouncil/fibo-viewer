package org.edmcouncil.spec.fibo.weasel.ontology.data;

import org.edmcouncil.spec.fibo.weasel.ontology.data.extractor.OwlDataExtractor;
import org.edmcouncil.spec.fibo.weasel.ontology.data.handler.IndividualDataHandler;
import org.edmcouncil.spec.fibo.weasel.ontology.data.handler.FiboDataHandler;
import org.edmcouncil.spec.fibo.weasel.ontology.data.handler.AnnotationsDataHandler;
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
import org.edmcouncil.spec.fibo.config.configuration.model.AppConfiguration;
import org.edmcouncil.spec.fibo.config.configuration.model.PairImpl;
import org.edmcouncil.spec.fibo.weasel.model.module.FiboModule;
import org.edmcouncil.spec.fibo.weasel.model.PropertyValue;
import org.edmcouncil.spec.fibo.weasel.model.graph.ViewerGraph;
import org.edmcouncil.spec.fibo.weasel.model.property.OwlAxiomPropertyEntity;
import org.edmcouncil.spec.fibo.weasel.model.property.OwlAxiomPropertyValue;
import org.edmcouncil.spec.fibo.weasel.model.property.OwlDirectedSubClassesProperty;
import org.edmcouncil.spec.fibo.weasel.model.property.OwlListElementIndividualProperty;
import org.edmcouncil.spec.fibo.weasel.model.taxonomy.OwlTaxonomyElementImpl;
import org.edmcouncil.spec.fibo.weasel.model.taxonomy.OwlTaxonomyImpl;
import org.edmcouncil.spec.fibo.weasel.model.taxonomy.OwlTaxonomyValue;
import org.edmcouncil.spec.fibo.weasel.ontology.data.label.provider.LabelProvider;
import org.edmcouncil.spec.fibo.weasel.ontology.factory.ViewerIdentifierFactory;
import org.edmcouncil.spec.fibo.weasel.ontology.visitor.OntologyVisitors;
import org.edmcouncil.spec.fibo.weasel.utils.OwlUtils;
import org.edmcouncil.spec.fibo.weasel.utils.StringUtils;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAxiom;
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

  private static final Logger LOG = LoggerFactory.getLogger(OwlDataHandler.class);

  private final OWLObjectRenderer rendering = new ManchesterOWLSyntaxOWLObjectRendererImpl();
  @Autowired
  private FiboDataHandler fiboDataHandler;
  @Autowired
  private AnnotationsDataHandler annotationsDataHandler;
  @Autowired
  private LabelProvider labelExtractor;
  @Autowired
  private RestrictionGraphDataHandler graphDataHandler;
  @Autowired
  private OwlUtils owlUtils;
  @Autowired
  private AppConfiguration config;

  private final String subClassOfIriString = ViewerIdentifierFactory
      .createId(ViewerIdentifierFactory.Type.axiom, AxiomType.SUBCLASS_OF.getName()).toString();

  /**
   *
   * @param iri Iri is used to identify for the given ontology.
   * @param ontology Actions will be performed for the given ontology.
   * @return OwlListDetails
   */
  public OwlListDetails handleParticularClass(IRI iri, OWLOntology ontology) {
    OwlListDetails resultDetails = new OwlListDetails();

    Iterator<OWLClass> classesIterator = ontology.classesInSignature().iterator();

    while (classesIterator.hasNext()) {
      OWLClass clazz = classesIterator.next();

      if (clazz.getIRI().equals(iri)) {
//         << << << < HEAD:weasel / src / main / java / org / edmcouncil / spec / fibo / weasel / ontology / data / OwlDataHandler.java
//        LOGGER.debug("[Data Handler] Find OWL class wih IRI: {}", iri.toString());
//
//        handleParticularSubClassOf(ontology, clazz);
//
//        resultDetails.setLabel(StringUtils.getFragment(clazz.getIRI()));
//
//        OwlDetailsProperties<PropertyValue> axioms = handleAxioms(clazz, ontology);
//
//        List<PropertyValue> subclasses = axioms
//            .getProperties()
//            .getOrDefault(AxiomType.SUBCLASS_OF.getName(), new ArrayList<>(0));
//        List<PropertyValue> taxElements = subclasses
//            .stream()
//            .filter((pv) -> (pv.getType().equals(WeaselOwlType.TAXONOMY)))
//            .collect(Collectors.toList());
//        OwlDetailsProperties<PropertyValue> handleSubClassOf = handleParticularSubClassOf(ontology, clazz);
//        OwlDetailsProperties<PropertyValue> individuals = getInstancesByClass(ontology, clazz);
//         == == ==
        LOG.debug("[Data Handler] Find OWL class wih IRI: {}", iri.toString());

        String label = labelExtractor.getLabelOrDefaultFragment(clazz);

        resultDetails.setLabel(label);

        OwlDetailsProperties<PropertyValue> axioms = handleAxioms(clazz, ontology);
        List<PropertyValue> subclasses = getSubclasses(axioms);
        List<PropertyValue> taxElements = extracttTaxonomyElements(subclasses);

        OwlDetailsProperties<PropertyValue> directSubclasses = handleDirectSubclasses(ontology, clazz);
        OwlDetailsProperties<PropertyValue> individuals = handleInstances(ontology, clazz);

        OwlDetailsProperties<PropertyValue> inheritedAxioms = handleInheritedAxioms(ontology, clazz);

        ViewerGraph vg = graphDataHandler.handleGraph(clazz, ontology);

        subclasses = filterSubclasses(subclasses);

        OwlTaxonomyImpl tax = extractTaxonomy(taxElements, iri, ontology, WeaselOwlType.AXIOM_CLASS);
        tax.sort();
        //       << << << < HEAD:weasel / src / main / java / org / edmcouncil / spec / fibo / weasel / ontology / data / OwlDataHandler.java
//        resultDetails.setTaxonomy(tax);
//        OwlDetailsProperties<PropertyValue> annotations
//            = handleAnnotations(clazz.getIRI(), ontology);
//
//        resultDetails.addAllProperties(axioms);
//        resultDetails.addAllProperties(annotations);
//        resultDetails.addAllProperties(handleSubClassOf);
//        resultDetails.addAllProperties(individuals);
//        resultDetails.addAllProperties(inheritedAxioms);
//        // resultDetails.addAllProperties(modules);
//         == == ==
        OwlDetailsProperties< PropertyValue> annotations
            = handleAnnotations(clazz.getIRI(), ontology, resultDetails);

        setResultValues(resultDetails, tax, axioms, annotations, directSubclasses, individuals, inheritedAxioms, vg, subclasses);
        //       >>> >>> > develop:viewer - core / src / main / java / org / edmcouncil / spec / fibo / weasel / ontology / data / OwlDataHandler.java

      }
    }
    return resultDetails;
  }

  private List<PropertyValue> filterSubclasses(List<PropertyValue> subclasses) {
    List<PropertyValue> result = subclasses.stream().filter((pv) -> (!pv.getType().equals(WeaselOwlType.TAXONOMY))).collect(Collectors.toList());
    return result;
  }

  private List<PropertyValue> getSubclasses(OwlDetailsProperties<PropertyValue> axioms) {
    List<PropertyValue> subclasses = axioms
        .getProperties()
        .getOrDefault(subClassOfIriString, new ArrayList<>(0));
    return subclasses;
  }

  private List<PropertyValue> extracttTaxonomyElements(List<PropertyValue> subclasses) {
    List<PropertyValue> taxElements = subclasses
        .stream()
        .filter((pv) -> (pv.getType().equals(WeaselOwlType.TAXONOMY)))
        .collect(Collectors.toList());
    return taxElements;
  }

  private void setResultValues(OwlListDetails resultDetails,
      OwlTaxonomyImpl tax,
      OwlDetailsProperties<PropertyValue> axioms,
      OwlDetailsProperties<PropertyValue> annotations,
      OwlDetailsProperties<PropertyValue> directSubclasses,
      OwlDetailsProperties<PropertyValue> individuals,
      OwlDetailsProperties<PropertyValue> inheritedAxioms,
      ViewerGraph vg,
      List<PropertyValue> subclasses) {
    axioms.getProperties().put(subClassOfIriString, subclasses);

    resultDetails.setTaxonomy(tax);
    resultDetails.addAllProperties(axioms);
    resultDetails.addAllProperties(annotations);
    resultDetails.addAllProperties(directSubclasses);
    resultDetails.addAllProperties(individuals);
    resultDetails.addAllProperties(inheritedAxioms);
    resultDetails.setGraph(vg);
    // resultDetails.addAllProperties(modules);
  }

  /**
   * This method collects all particular Individual of the given class.As a result function
   * operation returns label and iri for each individual.
   *
   * @param ontology Actions will be performed for the given ontology.
   * @param iri Iri is used to identify for the given ontology.
   * @return Set of Individual.
   */
  public OwlListDetails handleParticularIndividual(IRI iri, OWLOntology ontology) {
    OwlListDetails resultDetails = new OwlListDetails();
    Iterator<OWLNamedIndividual> individualIterator = ontology.individualsInSignature().iterator();

    while (individualIterator.hasNext()) {
      OWLNamedIndividual individual = individualIterator.next();

      if (individual.getIRI().equals(iri)) {
        //   << << << < HEAD:weasel / src / main / java / org / edmcouncil / spec / fibo / weasel / ontology / data / OwlDataHandler.java
        //   LOGGER.debug("[Data Handler] Find owl named individual wih iri: {}", iri.toString());

        //    resultDetails.setLabel(StringUtils.getFragment(individual.getIRI()));
        //     == == ==
        LOG.debug("[Data Handler] Find owl named individual wih iri: {}", iri.toString());

        resultDetails.setLabel(labelExtractor.getLabelOrDefaultFragment(individual.getIRI()));
        //   >>> >>> > develop:viewer - core / src / main / java / org / edmcouncil / spec / fibo / weasel / ontology / data / OwlDataHandler.java

        OwlDetailsProperties<PropertyValue> axioms = handleAxioms(individual, ontology);

        OwlDetailsProperties<PropertyValue> annotations
            // <<<<<<< HEAD:
            // weasel / src / main / java / org / edmcouncil / spec / fibo / weasel / ontology / data / OwlDataHandler.java
            //    = handleAnnotations(individual.getIRI(), ontology);
            // == == ==
            = handleAnnotations(individual.getIRI(), ontology, resultDetails);
        //  >>> >>> > develop:viewer - core / src / main / java / org / edmcouncil / spec / fibo / weasel / ontology / data / OwlDataHandler.java

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

  /**
   * This method is collects taxonomy of the given class. As a result function operation returns
   * label and iri for all axioms.
   *
   * @param subElements
   * @param objIri objIri is used to identify for the given ontology.
   * @param ontology Actions will be performed for the given ontology.
   * @param type
   * @return Taxonomy in the appropriate data structure.
   */
  private OwlTaxonomyImpl extractTaxonomy(List<PropertyValue> subElements, IRI objIri, OWLOntology ontology, WeaselOwlType type) {
    OwlTaxonomyImpl taxonomy = new OwlTaxonomyImpl();
    if (subElements.size() > 0) {

      for (PropertyValue property : subElements) {
        if (property.getType().equals(WeaselOwlType.TAXONOMY)) {
          OwlAxiomPropertyValue axiomProperty = (OwlAxiomPropertyValue) property;
          IRI sci = extractSubElementIri(axiomProperty, objIri);
          OWLEntity entity = createEntity(ontology, sci, type);

          LOG.trace("\t{} Sub Element Of {}", StringUtils.getFragment(objIri),
              StringUtils.getFragment(entity.getIRI()));
          //          << << << < HEAD:weasel / src / main / java / org / edmcouncil / spec / fibo / weasel / ontology / data / OwlDataHandler.java
//          List<PropertyValue> subTax = getSubElements(entity, ontology, type);
//
//          OwlTaxonomyImpl subCLassTax = extractTaxonomy(subTax, entity.getIRI(), ontology, type);
//
//          OwlTaxonomyValue val1 = new OwlTaxonomyValue(WeaselOwlType.STRING, StringUtils.getFragment(objIri));
//           == == ==
          List< PropertyValue> subTax = getSuperElements(entity, ontology, type);

          OwlTaxonomyImpl subCLassTax = extractTaxonomy(subTax, entity.getIRI(), ontology, type);

          String label = labelExtractor.getLabelOrDefaultFragment(objIri);
          OwlTaxonomyValue val1 = new OwlTaxonomyValue(WeaselOwlType.STRING, label);
          //     >>> >>> > develop:viewer - core / src / main / java / org / edmcouncil / spec / fibo / weasel / ontology / data / OwlDataHandler.java
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
      //  << << << < HEAD:weasel / src / main / java / org / edmcouncil / spec / fibo / weasel / ontology / data / OwlDataHandler.java
      // LOGGER.trace("\t\tEnd leaf on {}", StringUtils.getFragment(objIri));
      // OwlTaxonomyValue val1 = new OwlTaxonomyValue(WeaselOwlType.STRING, StringUtils.getFragment(objIri));
      //   == == ==
      LOG.trace("\t\tEnd leaf on {}", StringUtils.getFragment(objIri));
      String label = labelExtractor.getLabelOrDefaultFragment(objIri);
      OwlTaxonomyValue val1 = new OwlTaxonomyValue(WeaselOwlType.STRING, label);
      // >>> >>> > develop:viewer - core / src / main / java / org / edmcouncil / spec / fibo / weasel / ontology / data / OwlDataHandler.java
      OwlTaxonomyValue val2 = new OwlTaxonomyValue(WeaselOwlType.IRI, objIri.getIRIString());
      OwlTaxonomyElementImpl taxEl = new OwlTaxonomyElementImpl(val1, val2);
      List<OwlTaxonomyElementImpl> currentTax = new LinkedList<>();
      currentTax.add(taxEl);
      taxonomy.addTaxonomy(currentTax);
    }

    return taxonomy;
  }

  /**
   *
   *
   * @param ontology Actions will be performed for the given ontology.
   * @param sci sci is used to identify for the given
   * @param type
   * @return OWLEntity
   */
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

  /**
   * This method is collects Axioms of the given class. As a result function operation returns label
   * and iri for axioms.
   *
   * @param axiomsIterator The axiomsIterator the passes through each element.
   * @param iri Iri is used to identify for the given ontology.
   * @return Axioms in the appropriate data structure.
   */
//  private <T extends OWLAxiom> OwlDetailsProperties<PropertyValue> handleAxioms(
//      Iterator<T> axiomsIterator, IRI iri) {
//    OwlDetailsProperties<PropertyValue> result = new OwlDetailsProperties<>();
//    String iriFragment = iri.getFragment();
//    String splitFragment = StringUtils.getFragment(iri);
//    Boolean fixRenderedIri = !iriFragment.equals(splitFragment); //if fragments is not the same we must repair rendered value
//     == == == =
  private <T extends OWLAxiom> OwlDetailsProperties<PropertyValue> handleAxioms(
      Iterator<T> axiomsIterator,
      IRI elementIri) {
    OwlDetailsProperties<PropertyValue> result = new OwlDetailsProperties<>();
    String iriFragment = elementIri.getFragment();
    String splitFragment = StringUtils.getFragment(elementIri);
    Boolean fixRenderedIri = !iriFragment.equals(splitFragment);

    Set<String> ignoredToDisplay = config.getWeaselConfig().getIgnoredElements();
    //>>> >>> > develop:viewer - core / src / main / java / org / edmcouncil / spec / fibo / weasel / ontology / data / OwlDataHandler.java
    while (axiomsIterator.hasNext()) {
      T axiom = axiomsIterator.next();
      String value = rendering.render(axiom);

      value = fixRenderedValue(value, iriFragment, splitFragment, fixRenderedIri);

      String key = axiom.getAxiomType().getName();
      key = ViewerIdentifierFactory.createId(ViewerIdentifierFactory.Type.axiom, key);
      if (ignoredToDisplay.contains(key)) {
        continue;
      }
      OwlAxiomPropertyValue opv = new OwlAxiomPropertyValue();
      opv.setValue(value);

      opv.setType(WeaselOwlType.AXIOM);
      // << << << < HEAD:weasel / src / main / java / org / edmcouncil / spec / fibo / weasel / ontology / data / OwlDataHandler.java
      // LOGGER.trace("[Data Handler] Find Axiom \"{}\" with type \"{}\"", value, key);
      // Boolean isRestriction = isRestriction(axiom);
      // == == ==
      LOG.debug("[Data Handler] Find Axiom \"{}\" with type \"{}\"", value, key);
      Boolean isRestriction = owlUtils.isRestriction(axiom);
      // >>> >>> > develop:viewer - core / src / main / java / org / edmcouncil / spec / fibo / weasel / ontology / data / OwlDataHandler.java

      if (!isRestriction && axiom.getAxiomType().equals(AxiomType.SUBCLASS_OF)) {
        LOG.trace("[Data Handler] Find non restriction SubClassOf");
        opv.setType(WeaselOwlType.TAXONOMY);
      }

      processingAxioms(axiom, fixRenderedIri, iriFragment, splitFragment, opv, value);

      result.addProperty(key, opv);
    }
    result.sortPropertiesInAlphabeticalOrder();
    return result;
  }

  /**
   *
   * @param axiom
   * @param iriFragment
   * @param opv
   * @return
   *
   */
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

    LOG.trace("Rendered Val: {}", renderedVal);

    while (iterator.hasNext()) {
      OWLEntity next = iterator.next();
      String eSignature = rendering.render(next);
      eSignature = fixRenderedIri && iriFragment.equals(eSignature) ? splitFragment : eSignature;
      String key = null;
      LOG.trace("OWL Entity: {}", next.toStringID());
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
      String label = labelExtractor.getLabelOrDefaultFragment(next.getIRI());
      axiomPropertyEntity.setLabel(label);
      opv.addEntityValues(key, axiomPropertyEntity);

    }
    String value = String.join(" ", splited);
    LOG.debug("[Data Handler] Prepared value for axiom : {}", value);
    opv.setValue(value);
  }

 /**
       *
       * @param value
       * @param iriFragment iriFragment is the last part of the iri address.
       * @param splitFragment splitFragment is the entire string of the last fragment iri.
       * @param fixRenderedIri
       * @return
       */


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


  /**
   * This method is collects particular DataProperty of the given class. As a result function
   * operation returns label and iri for each DataProperty.
   *
   * @param ontology Actions will be performed for the given ontology.
   * @param iri Iri is used to identify for the given ontology.
   * @return DataProperty in the appropriate data structure.
   */
  public OwlListDetails handleParticularDataProperty(IRI iri, OWLOntology ontology) {
    OwlListDetails resultDetails = new OwlListDetails();
    Iterator<OWLDataProperty> dataPropertyIt = ontology.dataPropertiesInSignature().iterator();

    while (dataPropertyIt.hasNext()) {
      OWLDataProperty dataProperty = dataPropertyIt.next();

      if (dataProperty.getIRI().equals(iri)) {
//         << << << < HEAD:weasel / src / main / java / org / edmcouncil / spec / fibo / weasel / ontology / data / OwlDataHandler.java
//        LOGGER.debug("[Data Handler] Find owl data property wih iri: {}", iri.toString());
//
//        resultDetails.setLabel(StringUtils.getFragment(dataProperty.getIRI()));
//
//        OwlDetailsProperties<PropertyValue> axioms = handleAxioms(dataProperty, ontology);
//
//        List<PropertyValue> subElements = getSubElements(dataProperty, ontology, WeaselOwlType.AXIOM_DATA_PROPERTY);
//         == == ==
             LOG.debug("[Data Handler] Find owl data property wih iri: {}", iri.toString());

        resultDetails.setLabel(labelExtractor.getLabelOrDefaultFragment(dataProperty.getIRI()));

        OwlDetailsProperties<PropertyValue> axioms = handleAxioms(dataProperty, ontology);

        List<PropertyValue> subElements = getSuperElements(dataProperty, ontology, WeaselOwlType.AXIOM_DATA_PROPERTY);
       //  >>> >>> > develop:viewer - core / src / main / java / org / edmcouncil / spec / fibo / weasel / ontology / data / OwlDataHandler.java
        OwlTaxonomyImpl taxonomy = extractTaxonomy(subElements, iri, ontology, WeaselOwlType.AXIOM_DATA_PROPERTY);
        taxonomy.sort();

        OwlDetailsProperties<PropertyValue> annotations
//        <<<<<<< HEAD:
//        weasel / src / main / java / org / edmcouncil / spec / fibo / weasel / ontology / data / OwlDataHandler.java
//            = handleAnnotations(dataProperty.getIRI(), ontology);
//         == == ==
           = handleAnnotations(dataProperty.getIRI(), ontology, resultDetails);
        // >>> >>> > develop:viewer - core / src / main / java / org / edmcouncil / spec / fibo / weasel / ontology / data / OwlDataHandler.java

        resultDetails.addAllProperties(axioms);
        resultDetails.addAllProperties(annotations);
        resultDetails.setTaxonomy(taxonomy);
      }
    }
    return resultDetails;

  }


/**
       * This method collects Particular ObjectProperty of the given class.As a result function
       * operationn return label and iri each object property.
       *
       * @param ontology Actions will be performed for the given ontology.
       * @param iri Iri is used to identify for the given ontology.
       * @return ObjectProperty in the appropriate data structure.
       */
  public OwlListDetails handleParticularObjectProperty(IRI iri, OWLOntology ontology) {
    OwlListDetails resultDetails = new OwlListDetails();
    Iterator<OWLObjectProperty> dataPropertyIt = ontology.objectPropertiesInSignature().iterator();

    while (dataPropertyIt.hasNext()) {
      OWLObjectProperty dataProperty = dataPropertyIt.next();

      if (dataProperty.getIRI().equals(iri)) {
//         << << << < HEAD:weasel / src / main / java / org / edmcouncil / spec / fibo / weasel / ontology / data / OwlDataHandler.java
//        LOGGER.debug("[Data Handler] Find owl object property wih iri: {}", iri.toString());
//
//        resultDetails.setLabel(StringUtils.getFragment(dataProperty.getIRI()));
//
//        OwlDetailsProperties<PropertyValue> axioms = handleAxioms(dataProperty, ontology);
//
//        List<PropertyValue> subElements = getSubElements(dataProperty, ontology, WeaselOwlType.AXIOM_OBJECT_PROPERTY);
//         == == ==
            LOG.debug("[Data Handler] Find owl object property wih iri: {}", iri.toString());

        resultDetails.setLabel(labelExtractor.getLabelOrDefaultFragment(dataProperty.getIRI()));

        OwlDetailsProperties<PropertyValue> axioms = handleAxioms(dataProperty, ontology);

        List<PropertyValue> subElements = getSuperElements(dataProperty, ontology, WeaselOwlType.AXIOM_OBJECT_PROPERTY);
        // >>> >>> > develop:viewer - core / src / main / java / org / edmcouncil / spec / fibo / weasel / ontology / data / OwlDataHandler.java
        OwlTaxonomyImpl taxonomy = extractTaxonomy(subElements, iri, ontology, WeaselOwlType.AXIOM_OBJECT_PROPERTY);
        taxonomy.sort();

        OwlDetailsProperties<PropertyValue> annotations
//        <<<<<<< HEAD:
//        weasel / src / main / java / org / edmcouncil / spec / fibo / weasel / ontology / data / OwlDataHandler.java
//            = handleAnnotations(dataProperty.getIRI(), ontology);
//         == == ==
              = handleAnnotations(dataProperty.getIRI(), ontology, resultDetails);
       //  >>> >>> > develop:viewer - core / src / main / java / org / edmcouncil / spec / fibo / weasel / ontology / data / OwlDataHandler.java

        resultDetails.addAllProperties(axioms);
        resultDetails.addAllProperties(annotations);
        resultDetails.setTaxonomy(taxonomy);
      }
    }
    return resultDetails;
  }

 /**
       * This method collects SubElements of the given class.
       *
       * @param ontology Actions will be performed for the given ontology.
       * @param type
       * @return SubElements in the appropriate data structure.
       */



  private List<PropertyValue> getSuperElements(OWLEntity entity, OWLOntology ontology, WeaselOwlType type) {
    Stream<OWLProperty> propertyStream = null;
    OWLProperty prop = null;
    switch (type) {
      case AXIOM_CLASS:
        return getSuperClasses(ontology, AxiomType.SUBCLASS_OF, entity);
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
//       << << << < HEAD:weasel / src / main / java / org / edmcouncil / spec / fibo / weasel / ontology / data / OwlDataHandler.java
//      entitySuperClass.setLabel(StringUtils.getFragment(superClazzIri));
//       == == ==
           entitySuperClass.setLabel(labelExtractor.getLabelOrDefaultFragment(superClazzIri));
     //  >>> >>> > develop:viewer - core / src / main / java / org / edmcouncil / spec / fibo / weasel / ontology / data / OwlDataHandler.java

      pv.setType(WeaselOwlType.TAXONOMY);
      pv.addEntityValues(labelExtractor.getLabelOrDefaultFragment(subClazzIri), entitySubClass);
      pv.addEntityValues(labelExtractor.getLabelOrDefaultFragment(superClazzIri), entitySuperClass);
      resultProperties.add(pv);

    }

    return resultProperties;
  }

/**
       *
       * @param ontology Actions will be performed for the given ontology.
       * @param subType subType is the type by which the axioms will be collected.
       * @param entity
       * @return
       */


  private List<PropertyValue> getSuperClasses(OWLOntology ontology, AxiomType<OWLSubClassOfAxiom> subType, OWLEntity entity) {
    
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
            entitySubClass.setLabel(labelExtractor.getLabelOrDefaultFragment(subClazzIri));
            entitySuperClass.setIri(superClazzIri.getIRIString());
            
                 entitySuperClass.setLabel(labelExtractor.getLabelOrDefaultFragment(superClazzIri));
            

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
   * This method collects particular SubClassOf of the given class. As a result function operation
   * returns label and iri for each restriction.
   *
   * @param ontology Actions will be performed for the given ontology.
   * @param clazz Instances are searched for the class. This class must appear in the ontology
   * given.
   * @return Particular SubClassOf in the appropriate data structure.
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
          WeaselOwlType.DIRECT_SUBCLASSES.name().toLowerCase()).toString();
      result.addProperty(key, r);
    }
    result.sortPropertiesInAlphabeticalOrder();
    return result;
  }

  /**
   * This method collects instances of the given class. A reasoner is used. As a result function
   * operation returns label and iri for each instance.
   *
   * @param ontology Actions will be performed for the given ontology.
   * @param clazz Instances are searched for the class. This class must appear in the ontology
   * given.
   * @return Instances in the appropriate data structure.
   */
  private OwlDetailsProperties<PropertyValue> handleInstances(OWLOntology ontology, OWLClass clazz) {
    OwlDetailsProperties<PropertyValue> result = new OwlDetailsProperties<>();
    OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
    OWLReasoner reasoner = reasonerFactory.createNonBufferingReasoner(ontology);
    NodeSet<OWLNamedIndividual> instances = reasoner.getInstances(clazz, true);

    for (OWLNamedIndividual namedIndividual : instances.entities().collect(Collectors.toSet())) {
      OwlListElementIndividualProperty s = new OwlListElementIndividualProperty();
      s.setType(WeaselOwlType.INSTANCES);
      s.setValue(new PairImpl(labelExtractor
          .getLabelOrDefaultFragment(namedIndividual.getIRI()), namedIndividual.getIRI().toString()));
      String key = ViewerIdentifierFactory.createId(ViewerIdentifierFactory.Type.function,
          WeaselOwlType.INSTANCES.name().toLowerCase()).toString();
      result.addProperty(key, s);
    }
    return result;
  }

  /**
   * This method is collects Inherited Axioms of given class. A reasoner is used. As a result
   * function operation returns label and iri for each Inherited Axioms. Clazz must appear in the
   * ontology.
   *
   * @param ontology Actions will be performed for the given ontology.
   * @param clazz Inherited Axioms are searched for the class.
   * @return Inherited Axiom in the appropriate data structure.
   */
  private OwlDetailsProperties<PropertyValue> handleInheritedAxioms(OWLOntology ontology, OWLClass clazz) {
    OwlDetailsProperties<PropertyValue> result = new OwlDetailsProperties<>();
    OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
    OWLReasoner reasoner = reasonerFactory.createNonBufferingReasoner(ontology);
    
         String subClassOfKey = ViewerIdentifierFactory.createId(ViewerIdentifierFactory.Type.axiom, "SubClassOf").toString();
    NodeSet<OWLClass> rset = reasoner.getSuperClasses(clazz, InferenceDepth.ALL);
    rset.entities().collect(Collectors.toSet())
        .stream()
        .map((c) -> handleAxioms(c, ontology))
        .forEachOrdered((handleAxioms) -> {
          for (Map.Entry<String, List<PropertyValue>> entry : handleAxioms.getProperties().entrySet()) {

            if (entry.getKey().equals(subClassOfKey)) {
              for (PropertyValue propertyValue : entry.getValue()) {
                if (propertyValue.getType() != WeaselOwlType.TAXONOMY) {
                  String key = ViewerIdentifierFactory.createId(ViewerIdentifierFactory.Type.function,
                      WeaselOwlType.ANONYMOUS_ANCESTOR.name().toLowerCase()).toString();
                  result.addProperty(key, propertyValue);
                }
              }
            }
          }
        });

    result.sortPropertiesInAlphabeticalOrder();
    return result;
  }

/**
       * This method is collects Metadata of given class.
       *
       * @param ontology Actions will be performed for the given ontology.
       * @param iri Iri is used to identify for the given ontology.
       * @return Metadata in the appropriate data structure.
       */


  public OwlListDetails handleOntologyMetadata(IRI iri, OWLOntology ontology) {
    OwlListDetails wd = new OwlListDetails();
    OwlDetailsProperties<PropertyValue> metadata = fiboDataHandler.handleFiboOntologyMetadata(iri, ontology, wd);
    if (metadata != null && metadata.getProperties().keySet().size() > 0) {
      wd.addAllProperties(metadata);
    }
    wd.setIri(iri.toString());
    wd.setLabel(labelExtractor.getLabelOrDefaultFragment(iri));
    wd.setLocationInModules(fiboDataHandler.getElementLocationInModules(iri.toString(), ontology));
    return wd;

  }

  public List<FiboModule> getAllModulesData(OWLOntology ontology) {
    return fiboDataHandler.getAllModulesData(ontology);
  }

  public List<String> getElementLocationInModules(String iriString, OWLOntology ontology) {
    LOG.debug("[Data Handler] Handle location for element {}", iriString);
    return fiboDataHandler.getElementLocationInModules(iriString, ontology);
  }

}
