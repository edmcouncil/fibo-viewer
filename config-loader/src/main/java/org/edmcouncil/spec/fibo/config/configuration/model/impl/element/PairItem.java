package org.edmcouncil.spec.fibo.config.configuration.model.impl.element;

import org.edmcouncil.spec.fibo.config.configuration.model.ConfigItemType;
import org.edmcouncil.spec.fibo.config.configuration.model.PairImpl;
import org.edmcouncil.spec.fibo.config.configuration.model.ConfigItem;

/**
 * Create by Michał Daniel (michal.daniel@makolab.com)
 */
@Deprecated
public class PairItem extends PairImpl<String, String> implements ConfigItem {

  private ConfigItemType type;

  @Override
  public ConfigItemType getType() {
    return this.type;
  }

}
