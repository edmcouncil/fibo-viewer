package org.edmcouncil.spec.fibo.weasel.utils;

import org.edmcouncil.spec.fibo.weasel.ontology.visitor.OntologyVisitors;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Michał Daniel (michal.daniel@makolab.com)
 */
@Component
public class OwlUtils {
  
  @Autowired
  private OntologyVisitors ontologyVisitors;

  public final <T extends OWLAxiom> Boolean isRestriction(T axiom) {
    Boolean isRestriction = axiom.accept(ontologyVisitors.isRestrictionVisitor);
    if (isRestriction == null) {
      isRestriction = Boolean.FALSE;
    }
    return isRestriction;
  }

}
