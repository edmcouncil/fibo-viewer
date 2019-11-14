package org.edmcouncil.spec.fibo.config.configuration.model.impl.element;

import org.edmcouncil.spec.fibo.config.configuration.model.ConfigItemAbstract;

/**
 * @author Michał Daniel (michal.daniel@makolab.com)
 */
public class BooleanItem extends ConfigItemAbstract {

  private Boolean value;

  public Boolean getValue() {
    return value;
  }

  public void setValue(Boolean value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return value.toString();
  }

}
