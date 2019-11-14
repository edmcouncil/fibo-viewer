package org.edmcouncil.spec.fibo.config.configuration.model.impl.element;

import org.edmcouncil.spec.fibo.config.configuration.model.ConfigItemAbstract;

/**
 *
 * @author Michał Daniel (michal.daniel@makolab.com)
 */
public class MissingLanguageItem extends ConfigItemAbstract {

  private Action value;

  public Action getValue() {
    return value;
  }

  public void setValue(Action value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return value.name();
  }
  
  public enum Action{
    FIRST, FRAGMENT
  }

}
