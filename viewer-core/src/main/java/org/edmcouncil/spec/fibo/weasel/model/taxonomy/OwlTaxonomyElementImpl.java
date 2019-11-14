package org.edmcouncil.spec.fibo.weasel.model.taxonomy;

import org.edmcouncil.spec.fibo.config.configuration.model.PairImpl;

/**
 * @author Michał Daniel (michal.daniel@makolab.com)
 */
public class OwlTaxonomyElementImpl extends PairImpl<OwlTaxonomyValue, OwlTaxonomyValue> {

  public OwlTaxonomyElementImpl() {
  }

  public OwlTaxonomyElementImpl(OwlTaxonomyValue valA, OwlTaxonomyValue valB) {
    super(valA, valB);
  }

}
