package org.edmcouncil.spec.fibo.weasel.model.onto;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.edmcouncil.spec.fibo.weasel.model.PropertyValue;
import org.edmcouncil.spec.fibo.weasel.model.property.OwlAnnotationIri;

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

  public void sortInAlphabeticalOrder() {
    for (Map.Entry<String, List<PropertyValue>> entry : resources.entrySet()) {
      List<PropertyValue> list = entry.getValue();
      list.sort((o1, o2) -> {
        return ((OwlAnnotationIri) o1).getValue().getLabel()
            .compareToIgnoreCase(((OwlAnnotationIri) o2).getValue().getLabel());
      });
    }

  }

}
