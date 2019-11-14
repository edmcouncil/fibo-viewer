package org.edmcouncil.spec.fibo.weasel.model.onto;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.edmcouncil.spec.fibo.weasel.model.PropertyValue;

/**
 * 
 * @author Michał Daniel (michal.daniel@makolab.com)
 */
public class OntologyResources {

  private final Map<String, List<PropertyValue>> resources = new HashMap<>();

  public void addElement(String type, PropertyValue element) {
    List<PropertyValue> resList = resources.getOrDefault(type, new LinkedList<>());
    resList.add(element);
    resources.put(type, resList);
  }

  public Map<String, List<PropertyValue>> getResources() {
    return resources;
  }
  
  @Override
  public String toString() {
    return "OntologyResources{" + "resources=" + resources + '}';
  }

}
