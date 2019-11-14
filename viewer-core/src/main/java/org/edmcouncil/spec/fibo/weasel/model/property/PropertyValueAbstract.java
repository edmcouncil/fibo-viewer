package org.edmcouncil.spec.fibo.weasel.model.property;

import org.edmcouncil.spec.fibo.weasel.model.PropertyValue;
import org.edmcouncil.spec.fibo.weasel.model.WeaselOwlType;

/**
 * The class stores the Property Value.
 * 
 * @author Michał Daniel (michal.daniel@makolab.com)
 */
public abstract class PropertyValueAbstract<T> implements PropertyValue<T> {

  private WeaselOwlType type;
  private T value;
  
  @Override
  public WeaselOwlType getType() {
    return this.type;
  }

  @Override
  public void setType(WeaselOwlType type) {
    this.type = type;
  }

  @Override
  public T getValue() {
    return this.value;
  }

  @Override
  public void setValue(T value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return "PropertyValue<" + "value:" + value + "type:" + type + '>';
  }
  
  
}
