package org.edmcouncil.spec.fibo.config.configuration.model.impl.element;

/**
 * @author Patrycja Miazek (patrycja.miazek@makolab.com)
 */

public class RenameItem extends PairItem {

  public String getOldName() {
    return super.getValueA();
  }

  public void setOldName(String oldName) {
    super.setValueA(oldName);
  }

  public String getNewName() {
    return super.getValueB();
  }

  public void setNewName(String newName) {
    super.setValueB(newName);
  }

  @Override
  public boolean equals(Object obj) {
    return super.equals(obj);
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }
  
}
