package org.edmcouncil.spec.fibo.weasel.ontology.data;

import org.edmcouncil.spec.fibo.weasel.model.WeaselOwlType;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.springframework.stereotype.Component;

/**
 * @author Michał Daniel (michal.daniel@makolab.com)
 */
@Component
public class OwlDataExtractor {

  /**
   * This method extract Annotation Type.
   *
   * @param next
   * @return
   */
  public WeaselOwlType extractAnnotationType(OWLAnnotationAssertionAxiom next) {
    if (next.getValue().isIRI()) {
      return WeaselOwlType.IRI;
    } else if (next.getValue().isLiteral()) {
      String datatype = next.getValue().asLiteral().get().getDatatype().toString();
      //TODO: move this strings to list and use contains
      if (datatype.equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#langString")
          || datatype.equals("http://www.w3.org/2001/XMLSchema#string")) {
        return WeaselOwlType.STRING;
      } else if (datatype.equals("xsd:anyURI")) {
        return WeaselOwlType.ANY_URI;
      }
    }
    return WeaselOwlType.OTHER;
  }

  /**
   * This method is used to extract annotation type for ontology.
   *
   * @param next
   * @return
   */
  public WeaselOwlType extractAnnotationType(OWLAnnotation next) {
    if (next.getValue().isIRI()) {
      return WeaselOwlType.IRI;
    } else if (next.getValue().isLiteral()) {
      String datatype = next.getValue().asLiteral().get().getDatatype().toString();
      //TODO: move this strings to list and use contains
      if (datatype.equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#langString")
          || datatype.equals("http://www.w3.org/2001/XMLSchema#string")) {
        return WeaselOwlType.STRING;
      } else if (datatype.equals("xsd:anyURI")) {
        return WeaselOwlType.ANY_URI;
      }
    }
    return WeaselOwlType.OTHER;
  }


}
