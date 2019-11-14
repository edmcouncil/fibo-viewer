package org.edmcouncil.spec.fibo.weasel.utils;

import org.edmcouncil.spec.fibo.weasel.ontology.factory.ViewerIdentifierFactory;
import org.semanticweb.owlapi.model.IRI;

/**
 * @author Michał Daniel (michal.daniel@makolab.com)
 */
public class StringUtils {

  private static final String AXIOM_PATTERN = ViewerIdentifierFactory.createId(
        ViewerIdentifierFactory.Type.axiom,
      ViewerIdentifierFactory.Element.empty);

  public static String getFragment(IRI iri) {
    String iriString = iri.toString();
    return getFragment(iriString);
  }

  public static String getFragment(String iri) {
    if(iri.contains(AXIOM_PATTERN)){
      return iri.substring(iri.lastIndexOf("."));
    }
    String[] splitIri = iri.split("/");
    String lastElement = splitIri[splitIri.length - 1];
    if (iri.endsWith("/")) {
      return lastElement;
    } else if (lastElement.contains("#")) {
      return lastElement.substring(lastElement.indexOf("#") + 1);
    } else {
      return lastElement;
    }
  }

  public static int countLetter(String string, char letter) {
    int count = 0;

    //Counts each character except space    
    for (int i = 0; i < string.length(); i++) {
      if (string.charAt(i) == letter) {
        count++;
      }
    }
    return count;
  }
}
