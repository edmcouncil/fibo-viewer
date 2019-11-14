package org.edmcouncil.spec.fibo.weasel.model.property;

import org.edmcouncil.spec.fibo.config.configuration.model.PairImpl;

/**
 * @author Patrycja Miazek (patrycja.miazek@makolab.com)
 */
public class OwlDirectedSubClassesProperty extends PropertyValueAbstract<PairImpl> {

  @Override
  public String toString() {
    return super.getValue().getValueA().toString();
  }
}
