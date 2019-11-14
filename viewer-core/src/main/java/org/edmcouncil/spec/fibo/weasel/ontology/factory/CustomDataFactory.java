package org.edmcouncil.spec.fibo.weasel.ontology.factory;

import org.edmcouncil.spec.fibo.weasel.model.OwlSimpleProperty;
import org.edmcouncil.spec.fibo.weasel.model.WeaselOwlType;
import org.edmcouncil.spec.fibo.weasel.model.property.OwlAnnotationIri;
import org.edmcouncil.spec.fibo.weasel.ontology.data.label.provider.LabelProvider;
import org.semanticweb.owlapi.model.IRI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Michał Daniel (michal.daniel@makolab.com)
 */
@Component
public class CustomDataFactory {

  private static final Logger LOG = LoggerFactory.getLogger(CustomDataFactory.class);

  @Autowired
  private LabelProvider labelExtractor;

  /**
   * 
   * @param iri IRI element for which we create annotationIRI
   * @return AnnotationIri contains iri and label
   */
  public OwlAnnotationIri createAnnotationIri(String iri) {
    LOG.debug("[Custom Data Factory] Create annotation for iri: {}", iri);

    OwlAnnotationIri owlAnnotationIri = new OwlAnnotationIri();
    OwlSimpleProperty osp = new OwlSimpleProperty();
    osp.setLabel(labelExtractor.getLabelOrDefaultFragment(IRI.create(iri)));
    osp.setIri(iri);
    owlAnnotationIri.setValue(osp);
    owlAnnotationIri.setType(WeaselOwlType.IRI);
    return owlAnnotationIri;
  }
}
