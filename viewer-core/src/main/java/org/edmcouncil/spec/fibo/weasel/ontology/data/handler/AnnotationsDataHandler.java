package org.edmcouncil.spec.fibo.weasel.ontology.data.handler;

import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import org.edmcouncil.spec.fibo.config.configuration.model.AppConfiguration;
import org.edmcouncil.spec.fibo.config.configuration.model.impl.ViewerCoreConfiguration;
import org.edmcouncil.spec.fibo.weasel.model.PropertyValue;
import org.edmcouncil.spec.fibo.weasel.model.WeaselOwlType;
import org.edmcouncil.spec.fibo.weasel.model.details.OwlListDetails;
import org.edmcouncil.spec.fibo.weasel.model.property.OwlAnnotationPropertyValue;
import org.edmcouncil.spec.fibo.weasel.model.property.OwlDetailsProperties;
import org.edmcouncil.spec.fibo.weasel.ontology.factory.CustomDataFactory;
import org.edmcouncil.spec.fibo.weasel.ontology.data.extractor.OwlDataExtractor;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Michał Daniel (michal.daniel@makolab.com)
 */
@Component
public class AnnotationsDataHandler {

  private static final Logger LOG = LoggerFactory.getLogger(AnnotationsDataHandler.class);
  private static final IRI COMMENT_IRI = IRI.create("http://www.w3.org/2000/01/rdf-schema#comment");
  private final String FIBO_QNAME = "QName:";

  @Autowired
  private OwlDataExtractor dataExtractor;
  @Autowired
  private CustomDataFactory customDataFactory;
  @Autowired
  private AppConfiguration appConfig;

  /**
   * This method collects handle Annotations.
   *
   * @param iri ElementIri is used to identify for the given ontology.
   * @param ontology Actions will be performed for the given ontology.
   * @param details
   * @return
   */
  public OwlDetailsProperties<PropertyValue> handleAnnotations(IRI iri, OWLOntology ontology, OwlListDetails details) {

    Set<String> ignoredToDisplay = appConfig.getWeaselConfig().getIgnoredElements();

    OwlDetailsProperties<PropertyValue> result = new OwlDetailsProperties<>();

    Iterator<OWLAnnotationAssertionAxiom> annotationAssertionAxiom
        = ontology.annotationAssertionAxioms(iri).iterator();
    while (annotationAssertionAxiom.hasNext()) {
      OWLAnnotationAssertionAxiom next = annotationAssertionAxiom.next();
      IRI propertyiri = next.getProperty().getIRI();
      if (ignoredToDisplay.contains(propertyiri.toString())) {
        continue;
      }
      String value = next.annotationValue().toString();

      PropertyValue opv = new OwlAnnotationPropertyValue();
      WeaselOwlType extractAnnotationType = dataExtractor.extractAnnotationType(next);
      opv.setType(extractAnnotationType);

      if (next.getValue().isIRI()) {
        opv = customDataFactory.createAnnotationIri(value);

      } else if (next.getValue().isLiteral()) {
        Optional<OWLLiteral> asLiteral = next.getValue().asLiteral();
        if (asLiteral.isPresent()) {
          value = asLiteral.get().getLiteral();

          if (propertyiri.equals(COMMENT_IRI) && value.contains(FIBO_QNAME)) {
            details.setqName(value);
            continue;
          }

          String lang = asLiteral.get().getLang();
          value = lang.isEmpty() ? value : value.concat(" [").concat(lang).concat("]");

          checkUriAsIri(opv, value);
          opv.setValue(value);
          if (opv.getType() == WeaselOwlType.IRI) {
            opv = customDataFactory.createAnnotationIri(value);
          }
        }
      }
      LOG.info("[Data Handler] Find annotation, value: \"{}\", property iri: \"{}\" ", opv, propertyiri.toString());

      result.addProperty(propertyiri.toString(), opv);
      //result.addProperty(property, opv);
    }
    result.sortPropertiesInAlphabeticalOrder();
    return result;
  }

  /**
   * This method collects handle Ontology Annotations.
   *
   * @param annotations Stream of OWL abbotations.
   * @param ontology Loaded ontology.
   * @param details qName is added to this object if found.
   * @return Ontology Annottions in the appropriate data structure.
   */
  public OwlDetailsProperties<PropertyValue> handleOntologyAnnotations(Stream<OWLAnnotation> annotations, OWLOntology ontology, OwlListDetails details) {
    OwlDetailsProperties<PropertyValue> result = new OwlDetailsProperties<>();
    Set<String> ignoredToDisplay = appConfig.getWeaselConfig().getIgnoredElements();
    Iterator<OWLAnnotation> annotationIterator = annotations.iterator();
    while (annotationIterator.hasNext()) {
      OWLAnnotation next = annotationIterator.next();
      IRI propertyiri = next.getProperty().getIRI();

      if (ignoredToDisplay.contains(propertyiri.toString())) {
        continue;
      }

      String value = next.annotationValue().toString();

      PropertyValue opv = new OwlAnnotationPropertyValue();
      WeaselOwlType extractAnnotationType = dataExtractor.extractAnnotationType(next);
      opv.setType(extractAnnotationType);

      if (next.getValue().isIRI()) {

        opv = customDataFactory.createAnnotationIri(value);

      } else if (next.getValue().isLiteral()) {
        Optional<OWLLiteral> asLiteral = next.getValue().asLiteral();
        if (asLiteral.isPresent()) {
          value = asLiteral.get().getLiteral();
          if (propertyiri.equals(COMMENT_IRI) && value.contains(FIBO_QNAME)) {
            details.setqName(value);
            continue;
          }

          String lang = asLiteral.get().getLang();
          value = lang.isEmpty() ? value : value.concat(" [").concat(lang).concat("]");
          opv.setValue(value);
          checkUriAsIri(opv, value);
          if (opv.getType() == WeaselOwlType.IRI) {
            opv = customDataFactory.createAnnotationIri(value);
          }
        }
      }
      LOG.info("[Data Handler] Find annotation, value: \"{}\", propertyIRI: \"{}\" ", opv, propertyiri.toString());

      result.addProperty(propertyiri.toString(), opv);
      //result.addProperty(property, opv);
    }
    return result;
  }

  /**
   *
   * @param opv PropertyValue
   * @param value
   */
  private void checkUriAsIri(PropertyValue opv, String value) {
    //TODO: Change this to more pretty solution
    if (opv.getType() == WeaselOwlType.ANY_URI) {
      ViewerCoreConfiguration weaselConfiguration = (ViewerCoreConfiguration) appConfig.getWeaselConfig();
      if (weaselConfiguration.isUriIri(value)) {
        opv.setType(WeaselOwlType.IRI);
      }
    }
  }
}
