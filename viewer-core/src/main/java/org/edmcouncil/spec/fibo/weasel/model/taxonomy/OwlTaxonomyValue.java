package org.edmcouncil.spec.fibo.weasel.model.taxonomy;

import org.edmcouncil.spec.fibo.weasel.model.WeaselOwlType;
import org.edmcouncil.spec.fibo.weasel.model.property.PropertyValueAbstract;

/**
 * @author Michał Daniel (michal.daniel@makolab.com)
 */
public class OwlTaxonomyValue extends PropertyValueAbstract<String> {

  public OwlTaxonomyValue() {
  }

  public OwlTaxonomyValue(WeaselOwlType type, String value) {
    setType(type);
    setValue(value);
  }

}
