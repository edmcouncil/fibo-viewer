package org.edmcouncil.spec.fibo.weasel.ontology.data.handler;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.edmcouncil.spec.fibo.config.configuration.model.PairImpl;
import org.edmcouncil.spec.fibo.weasel.model.PropertyValue;
import org.edmcouncil.spec.fibo.weasel.model.WeaselOwlType;
import org.edmcouncil.spec.fibo.weasel.model.property.OwlDetailsProperties;
import org.edmcouncil.spec.fibo.weasel.model.property.OwlListElementIndividualProperty;
import org.edmcouncil.spec.fibo.weasel.ontology.data.label.provider.LabelProvider;
import org.edmcouncil.spec.fibo.weasel.utils.StringUtils;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Michał Daniel (michal.daniel@makolab.com)
 */
@Component
public class IndividualDataHandler {

  private static final Logger LOG = LoggerFactory.getLogger(IndividualDataHandler.class);

  @Autowired
  private LabelProvider labelExtractor;

  /**
   * Handle all individual for OWLClass given on parameter. A reasoner is used. A result function
   * operation return label and IRI for each Individual. Clazz must appear in the ontology.
   *
   * @param ontology Actions will be performed for the given ontology.
   * @param clazz IndividualData are searched for the class.
   * @return IndividualData in the appropriate data structure.
   */
  public OwlDetailsProperties<PropertyValue> handleClassIndividuals(OWLOntology ontology, OWLClass clazz) {
    OwlDetailsProperties<PropertyValue> result = new OwlDetailsProperties<>();
    OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
    OWLReasoner reasoner = reasonerFactory.createNonBufferingReasoner(ontology);
    NodeSet<OWLNamedIndividual> instances = null;
    try {
      instances = reasoner.getInstances(clazz, true);
    } catch (java.util.NoSuchElementException e) {
      LOG.error(e.toString());
      return result;
    }

    Set<OWLNamedIndividual> individualList = instances.entities().collect(Collectors.toSet());
    for (OWLNamedIndividual namedIndividual : individualList) {
      OwlListElementIndividualProperty s = new OwlListElementIndividualProperty();
      s.setType(WeaselOwlType.INSTANCES);
      String label = labelExtractor.getLabelOrDefaultFragment(namedIndividual);
      s.setValue(new PairImpl(label, namedIndividual.getIRI().toString()));
      result.addProperty(WeaselOwlType.INSTANCES.name(), s);
      namedIndividual.getEntityType();
    }
    return result;
  }
}
