package org.edmcouncil.spec.fibo.weasel.changer;

import java.util.List;
import java.util.Map;
import org.edmcouncil.spec.fibo.weasel.model.PropertyValue;
import org.edmcouncil.spec.fibo.weasel.model.details.OwlGroupedDetails;
import org.edmcouncil.spec.fibo.weasel.model.property.OwlGroupedDetailsProperties;
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
public class ChangerIriToLabel {
  
  private static final Logger LOG = LoggerFactory.getLogger(ChangerIriToLabel.class);
  
  @Autowired
  private LabelProvider labelProvider;

  public OwlGroupedDetails changeIriKeysInGroupedDetails(OwlGroupedDetails det) {
   OwlGroupedDetailsProperties<PropertyValue> newProp = new OwlGroupedDetailsProperties<>();
    
    LOG.debug("Change IRI Keys in grouped details for object with iri: {}", det.getIri());
    det.getProperties().entrySet().forEach((entryLVL1) -> {
      LOG.debug("\t LVL 1 key: {}", entryLVL1.getKey());
      entryLVL1.getValue().entrySet().forEach((entryLVL2) -> {
        LOG.debug("\t\t LVL 2 key: {}", entryLVL2.getKey());
        String newKey = labelProvider.getLabelOrDefaultFragment(IRI.create(entryLVL2.getKey()));
        entryLVL2.getValue().forEach((propertyValue) -> {
          LOG.debug("\t\t\t LVL 3 added val: {}", propertyValue.toString());
          newProp.addProperty(entryLVL1.getKey(), newKey, propertyValue);
        });
      });
    });
    det.setProperties(newProp);
    return det;
  }
  
    
  
}
