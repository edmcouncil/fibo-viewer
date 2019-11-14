package org.edmcouncil.spec.fibo.config.configuration.model.impl.element;

import java.util.LinkedHashSet;
import java.util.Set;
import org.edmcouncil.spec.fibo.config.configuration.model.ConfigItemAbstract;
import org.edmcouncil.spec.fibo.config.configuration.model.ConfigItemType;
import org.edmcouncil.spec.fibo.config.configuration.model.GroupType;

/**
 * @author Michał Daniel (michal.daniel@makolab.com)
 * @author Patrycja Miazek (patrycja.miazek@makolab.com)
 */
public class GroupsItem extends ConfigItemAbstract {

  private String name;
  private Set<StringItem> elements;
  private GroupType groupType;
  
  public GroupsItem() {
    elements = new LinkedHashSet<>();
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Set<StringItem> getElements() {
    return elements;
  }

  public void setElements(Set<StringItem> elements) {
    this.elements = elements;
  }


  public GroupsItem(ConfigItemType type) {
    super(type);
  }

  public void addElement(StringItem el) {
    if (elements == null) {
      elements = new LinkedHashSet<>();
    }
    elements.add(el);
  }

  public boolean contains(String val) {
    if (elements == null) {
      return false;
    }
    //return elements.contains(new StringItem(val));
    /*return elements.stream()
        .map(StringItem::toString)
        .filter(val::equals)
        .findFirst()
        .isPresent();*/

    for (StringItem element : elements) {
      if (element.toString().equals(val)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public String toString() {
    return "ConfigGroupsElement{" + "name=" + name + ", elements=" + elements + ", groupType=" + groupType + '}';
  }

  public GroupType getGroupType() {
    return groupType;
  }

  public void setGroupType(GroupType groupType) {
    this.groupType = groupType;
    addElement(new StringItem(groupType.name()));
  }

}
