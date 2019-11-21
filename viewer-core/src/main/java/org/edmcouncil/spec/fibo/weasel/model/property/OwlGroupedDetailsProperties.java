package org.edmcouncil.spec.fibo.weasel.model.property;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import org.edmcouncil.spec.fibo.config.configuration.model.impl.element.GroupsItem;
import org.edmcouncil.spec.fibo.config.configuration.model.impl.element.StringItem;
import org.edmcouncil.spec.fibo.config.configuration.model.impl.ViewerCoreConfiguration;
import org.edmcouncil.spec.fibo.weasel.comparator.WeaselComparators;
import org.edmcouncil.spec.fibo.config.configuration.model.ConfigItem;

/**
 * @author Michał Daniel (michal.daniel@makolab.com)
 */
public class OwlGroupedDetailsProperties<T> {

  private List taxonomy;
  private Map<String, Map<String, List<T>>> properties;

  public OwlGroupedDetailsProperties() {
    properties = new LinkedHashMap<>();
  }

  /**
   * This method add Property.
   *
   * @param groupKey
   * @param propertyKey
   * @param property
   */
  public void addProperty(String groupKey, String propertyKey, T property) {
    if (this.properties == null) {
      this.properties = new LinkedHashMap<>();
    }

    Map<String, List<T>> group = properties.getOrDefault(groupKey, new LinkedHashMap<>());
    List<T> objects = group.getOrDefault(propertyKey, new LinkedList<>());

    objects.add(property);
    group.put(propertyKey, objects);
    properties.put(groupKey, group);
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

  public Map<String, Map<String, List<T>>> getProperties() {
    return properties;
  }

  //TODO: Check where we used that and do something with this.. this is very ugly..
  public void sort(List<StringItem> priotityList) {

    /*Comparator<String> comparator = WeaselComparators.getComparatorWithPriority(priotityList);
    SortedSet<String> keys = new TreeSet<>(comparator);
    keys.addAll(properties.keySet());

    Map<String, List<T>> result = new LinkedHashMap<>();
    keys.forEach((key) -> {
      result.put(key, properties.get(key));
    });
    properties = result;*/
  }

  //TODO: The same as up
  public void sort(Set<ConfigItem> groups) {

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
    final OwlGroupedDetailsProperties<?> other = (OwlGroupedDetailsProperties<?>) obj;
    if (!Objects.equals(this.taxonomy, other.taxonomy)) {
      return false;
    }
    if (!Objects.equals(this.properties, other.properties)) {
      return false;
    }
    return true;
  }

  /**
   * This method sort all elements.
   *
   * @param groups
   * @param cfg
   */
  public void sort(Set<ConfigItem> groups, ViewerCoreConfiguration cfg) {

    Map<String, Map<String, List<T>>> sortedResults = new LinkedHashMap<>();

    Map<String, List<T>> others = properties.get("other");

    for (ConfigItem g : groups) {
      GroupsItem group = (GroupsItem) g;
      Map<String, List<T>> prop = properties.get(group.getName());
      if (prop == null) {
        continue;
      }

      Map<String, List<T>> newprop = new LinkedHashMap();

      List<StringItem> priotityList = new LinkedList(group.getElements());
      Comparator<String> comparator = WeaselComparators.getComparatorWithPriority(priotityList);
      SortedSet<String> keys = new TreeSet<>(comparator);
      keys.addAll(prop.keySet());

      keys.forEach((key) -> {
        newprop.put(key, prop.get(key));
      });

      sortedResults.put(group.getName(), newprop);
    }

    if (others != null) {
      Map<String, List<T>> newothers = new LinkedHashMap<>();
      Comparator<String> comparator = WeaselComparators.getComparatorWithPriority(new ArrayList<>(0));
      SortedSet<String> keys = new TreeSet<>(comparator);
      keys.addAll(others.keySet());

      keys.forEach((key) -> {
        newothers.put(key, others.get(key));
      });

      sortedResults.put("other", newothers);
    }

    properties = sortedResults;
  }

}
