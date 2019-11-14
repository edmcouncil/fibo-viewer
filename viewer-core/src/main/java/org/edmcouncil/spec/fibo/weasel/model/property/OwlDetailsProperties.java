package org.edmcouncil.spec.fibo.weasel.model.property;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;
import org.edmcouncil.spec.fibo.config.configuration.model.impl.element.StringItem;
import org.edmcouncil.spec.fibo.weasel.comparator.WeaselComparators;

/**
 * @author Michał Daniel (michal.daniel@makolab.com)
 */
public class OwlDetailsProperties<T> {

  private List taxonomy;
  private Map<String, List<T>> properties;

  public OwlDetailsProperties() {
    properties = new HashMap<>();
  }

  public void addProperty(String key, T property) {
    if (this.properties == null) {
      this.properties = new HashMap<>();
    }

    List<T> propertiesList = properties.get(key);
    if (propertiesList == null) {
      propertiesList = new LinkedList<>();
    }

    propertiesList.add(property);
    properties.put(key, propertiesList);
  }

  public void addTaxonomy(String tax) {
    if (this.taxonomy == null) {
      this.taxonomy = new LinkedList();
    }

    this.taxonomy.add(tax);
  }

  public List getTaxonomy() {
    return taxonomy == null ? new ArrayList(0) : taxonomy;
  }

  public Map<String, List<T>> getProperties() {
    return properties;
  }

  public void sort(List<StringItem> priotityList) {
    Comparator<String> comparator = WeaselComparators.getComparatorWithPriority(priotityList);
    SortedSet<String> keys = new TreeSet<>(comparator);
    keys.addAll(properties.keySet());

    Map<String, List<T>> result = new LinkedHashMap<>();
    keys.forEach((key) -> {
      result.put(key, properties.get(key));
    });
    properties = result;
  }

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 23 * hash + Objects.hashCode(this.taxonomy);
    hash = 23 * hash + Objects.hashCode(this.properties);
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final OwlDetailsProperties<?> other = (OwlDetailsProperties<?>) obj;
    if (!Objects.equals(this.taxonomy, other.taxonomy)) {
      return false;
    }
    if (!Objects.equals(this.properties, other.properties)) {
      return false;
    }
    return true;
  }

  public void sortPropertiesInAlphabeticalOrder() {
    for (Map.Entry<String, List<T>> entry : properties.entrySet()) {
      List<T> value = entry.getValue();
      value.sort(WeaselComparators.getComparatorWithAlphabeticalOrder());
    }
  }

  @Override
  public String toString() {
    return "OwlDetailsProperties{" + "taxonomy=" + taxonomy + ", properties=" + properties.toString() + '}';
  }

}
