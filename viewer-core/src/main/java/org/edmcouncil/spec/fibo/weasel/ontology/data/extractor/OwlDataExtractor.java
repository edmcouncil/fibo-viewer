package org.edmcouncil.spec.fibo.weasel.ontology.data.extractor;

import java.util.stream.Collectors;
import org.edmcouncil.spec.fibo.weasel.model.WeaselOwlType;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLRestriction;
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


//  public String extractAnyUriToString(String anyUri) {
//    String uriString = anyUri.replaceFirst("\"", "");
//    uriString = uriString.substring(0, uriString.length() - 13);
//    return uriString;
//  }

  public static String extrackAxiomPropertyIri(OWLRestriction someValuesFromAxiom) {
    String propertyIri = null;
    for (OWLEntity oWLEntity : someValuesFromAxiom.getProperty().signature().collect(Collectors.toList())) {
      propertyIri = oWLEntity.toStringID();
    }
    return propertyIri;
  }

}
